package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import xyz.usbpc.utils.repeat
import java.lang.IllegalStateException
import java.lang.StringBuilder

fun MutableList<Int>.runSimple() : List<Long> = runBlocking {
    val vm = Intcode(this@runSimple.map { it.toLong() }.toMutableList())
    val vmRunner = launch { vm.simulate() }
    vmRunner.join()
    vm.state.toList()
}

fun MutableList<Int>.runWithInput(`in`: List<Int>) = runBlocking {
    val input = Channel<Long>()
    val output = Channel<Long>()

    val vm = Intcode(this@runWithInput.map { it.toLong() }.toMutableList(), input, output)

    val vmRunner = launch { vm.simulate() }

    for (x in `in`)
        input.send(x.toLong())
    input.close()

    for (x in output) {}

    vmRunner.join()

    vm
}

fun MutableList<Long>.runWithLongInput(`in`: List<Long>) = runBlocking {
    val input = Channel<Long>()
    val output = Channel<Long>()

    val vm = Intcode(this@runWithLongInput, input, output)

    val vmRunner = launch { vm.simulate() }

    for (x in `in`)
        input.send(x)
    input.close()

    for (x in output) {}

    vmRunner.join()

    vm
}

//TODO make the channels mock channels that are actually closed!
class Intcode(val state : MutableList<Long>, val input: ReceiveChannel<Long> = Channel(), val output: SendChannel<Long> = Channel(), val name : String = "", val debug: Boolean = false) {
    val out = mutableListOf<Long>()
    var ip: Int = 0
    var relativeBase = 0

    private fun getArg(offset: Int) : Long {
        var base = 10
        repeat(offset.toInt()) { base *= 10 }
        val thing = when(val mode = ((state[ip] / base) % 10).toInt()) {
            0 -> state[ip+offset].toInt()
            1 -> ip+offset
            2 -> relativeBase + state[ip+offset].toInt()
            else -> throw NotImplementedError("Mode $mode is nor implemented yet!")
        }

        if (thing >= state.size) {
            while (thing >= state.size)
                state.add(0)
        }

        return state[thing]
    }

    private fun setArg(offset: Int, value: Long) {
        var base = 10
        repeat(offset) { base *= 10 }
        val thing = when(val mode = ((state[ip] / base) % 10).toInt()) {
            0 -> state[ip+offset].toInt()
            1 -> throw IllegalStateException("Direct mode not allowed for setting")
            2 -> relativeBase + state[ip+offset].toInt()
            else -> throw NotImplementedError("Mode $mode is nor implemented yet!")
        }

        if (thing >= state.size) {
            while (thing >= state.size)
                state.add(0)
        }

        state[thing] = value
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    private fun Boolean.toLong() = if (this) 1L else 0L

    suspend fun simulate() {
        while (step()) {}
    }

    /**
     * Steps forward one step.
     */
    suspend fun step() : Boolean {
        //println(this)
        when ((state[ip] % 100).toInt()) {
            1 -> {
                setArg(3, getArg(1) + getArg(2))
                ip += 4
            }

            2 -> {
                setArg(3, getArg(1) * getArg(2))
                ip += 4
            }

            3 -> {
                if (debug)
                    println("$name: Trying to get input")
                setArg(1, input.receive())
                if (debug)
                    println("$name: Got input! ${getArg(1)}")
                ip += 2
            }

            4 -> {
                if (debug)
                    println("$name: Outputting ${getArg(1)}")
                out.add(getArg(1))
                output.send(getArg(1))
                ip += 2
            }

            5 -> {
                if (getArg(1) != 0L) {
                    ip = getArg(2).toInt()
                } else {
                    ip += 3
                }
            }

            6 -> {
                if (getArg(1) == 0L) {
                    ip = getArg(2).toInt()
                } else {
                    ip += 3
                }
            }

            7 -> {
                setArg(3, (getArg(1) < getArg(2)).toLong())
                ip += 4
            }

            8 -> {
                setArg(3, (getArg(1) == getArg(2)).toLong())
                ip += 4
            }

            9 -> {
                relativeBase += getArg(1).toInt()
                ip += 2
            }

            99 -> {
                output.close()
                return false
            }

            else -> throw NotImplementedError("Opcode ${state[ip]} is not implemented yet!")
        }

        return true
    }

    private fun getSafe(index: Int) : Long {
        return if (index < state.size)
            state[index]
        else
            0L
    }

    override fun toString(): String {
        val builder = StringBuilder()


        builder.append("Instruction pointer: ")
        builder.append(ip)
        builder.append(" Current instruction: ")

        for (i in ip..ip+3) {
            builder.append(getSafe(i))
            builder.append(", ")
        }

        builder.deleteCharAt(builder.lastIndex)
        builder.deleteCharAt(builder.lastIndex)

        return builder.toString()
    }
}