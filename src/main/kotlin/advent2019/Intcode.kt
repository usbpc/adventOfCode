package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

fun MutableList<Int>.runSimple() : List<Int> = runBlocking {
    val vm = Intcode(this@runSimple)
    val vmRunner = launch { vm.simulate() }
    vmRunner.join()
    vm.state.toList()
}

fun MutableList<Int>.runWithInput(`in`: List<Int>) = runBlocking {
    val input = Channel<Int>()
    val output = Channel<Int>()

    val vm = Intcode(this@runWithInput, input, output)

    val vmRunner = launch { vm.simulate() }

    for (x in `in`)
        input.send(x)
    input.close()

    for (x in output) {}

    vmRunner.join()

    vm
}

//TODO make the channels mock channels that are actually closed!
class Intcode(val state : MutableList<Int>, val input: ReceiveChannel<Int> = Channel(), val output: SendChannel<Int> = Channel(), val name : String = "", val debug: Boolean = false) {
    val out = mutableListOf<Int>()
    var ip: Int = 0

    private fun getArg(offset: Int) : Int {
        var base = 10
        repeat(offset) { base *= 10 }
        return when(val mode = (state[ip] / base) % 10) {
            0 -> state[state[ip+offset]]
            1 -> state[ip+offset]
            else -> throw NotImplementedError("Mode $mode is nor implemented yet!")
        }
    }

    private fun setArg(offset: Int, value: Int) {
        state[state[ip+offset]] = value
    }

    private fun Boolean.toInt() = if (this) 1 else 0

    suspend fun simulate() {
        while (step()) {}
    }

    /**
     * Steps forward one step.
     */
    suspend fun step() : Boolean {
        when (state[ip] % 100) {
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
                if (getArg(1) != 0) {
                    ip = getArg(2)
                } else {
                    ip += 3
                }
            }

            6 -> {
                if (getArg(1) == 0) {
                    ip = getArg(2)
                } else {
                    ip += 3
                }
            }

            7 -> {
                setArg(3, (getArg(1) < getArg(2)).toInt())
                ip += 4
            }

            8 -> {
                setArg(3, (getArg(1) == getArg(2)).toInt())
                ip += 4
            }

            99 -> {
                output.close()
                return false
            }

            else -> throw NotImplementedError("Opcode ${state[ip]} is not implemented yet!")
        }

        return true
    }
}