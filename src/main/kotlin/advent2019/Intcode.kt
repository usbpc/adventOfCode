package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.io.InputStream
import java.io.OutputStream

fun MutableList<Int>.runSimple() : List<Int> = runBlocking {
    val vm = Intcode(this@runSimple)
    val vmRunner = vm.run { simulate() }
    vmRunner.join()
    vm.state.toList()
}

fun MutableList<Int>.runWithInput(`in`: List<Int>) = runBlocking {
    val input = Channel<Int>()
    val output = Channel<Int>()

    val vm = Intcode(this@runWithInput, input, output)

    val vmRunner = vm.run { simulate() }

    for (x in `in`)
        input.send(x)
    input.close()

    var last = Int.MAX_VALUE

    for (i in output) {
        last = i
    }

    vmRunner.join()

    last
}

//TODO make the channels mock channels that are actually closed!
class Intcode(val state : MutableList<Int>, val input: ReceiveChannel<Int> = Channel(), val output: SendChannel<Int> = Channel()) {
    var ip: Int = 0
    fun MutableList<Int>.getRespectingMode(mode: Int, position: Int): Int {
        return when(mode) {
            0 -> { this[this[position]] }
            1 -> { this[position] }
            else -> throw NotImplementedError("Mode ${mode} is not implemented yet.")
        }
    }
    private operator fun get(index: Int) : Int {
        var thing = 10
        repeat(index) { thing *= 10 }
        val mode = (state[ip] / thing) % 10
        return when(mode) {
            0 -> state[state[ip+index]]
            1 -> state[ip+index]
            else -> throw NotImplementedError("Mode ${mode} is nor implemented yet!")
        }
    }
    fun CoroutineScope.simulate() = launch {
        while (step()) {}
    }
    /**
     * Steps forward one step.
     */
    suspend fun step() : Boolean {
        when (state[ip] % 100) {
            1 -> {
                state[state[ip+3]] = this[1] + this[2]
                ip += 4
            }

            2 -> {
                state[state[ip+3]] = this[1] * this[2]
                ip += 4
            }
            3 -> {
                state[state[ip+1]] = input.receive()
                ip += 2
            }
            4 -> {
                output.send(this[1])
                ip += 2
            }
            5 -> {
                if (this[1] != 0) {
                    ip = this[2]
                } else {
                    ip += 3
                }
            }
            6 -> {
                if (this[1] == 0) {
                    ip = this[2]
                } else {
                    ip += 3
                }
            }
            7 -> {
                if (this[1] < this[2]) {
                    state[state[ip+3]] = 1
                } else {
                    state[state[ip+3]] = 0
                }
                ip += 4
            }
            8 -> {
                if (this[1] == this[2]) {
                    state[state[ip+3]] = 1
                } else {
                    state[state[ip+3]] = 0
                }
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