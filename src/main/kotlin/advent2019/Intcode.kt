package advent2019

import java.io.InputStream
import java.io.OutputStream

class Intcode(val state : MutableList<Int>) {
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

    /**
     * Steps forward one step.
     */
    fun step() : Boolean {
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
                //TODO get input
                state[state[ip+1]] = 1
                ip += 2
            }
            4 -> {
                //println(getRespectingMode((this[ip]/100) % 10, ip+1))
                println("VM Output: ${this[1]}")
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

            99 -> return false

            else -> NotImplementedError("Opcode ${state[ip]} is not implemented yet!")
        }

        return true
    }

    fun MutableList<Int>.simulate(input: InputStream? = null, output: OutputStream? = null) : List<Int> {
        //parameter mode
        // 0 = position mode
        // 1 = immediate mode
        //var mode = 0
        //Instruction pointer
        val writer = output?.writer()
        val reader = input?.bufferedReader()

        loop@while (true) {
            //TODO parameter modes
            when (state[ip] % 100) {
                1 -> {
                    state[state[ip+3]] = getRespectingMode((state[ip]/100) % 10, ip+1) + getRespectingMode((state[ip]/1000) % 10, ip+2)
                    ip += 4
                }

                2 -> {
                    state[state[ip+3]] = getRespectingMode((state[ip]/100) % 10, ip+1) * getRespectingMode((state[ip]/1000) % 10, ip+2)
                    ip += 4
                }
                3 -> {
                    //Input to this
                    writer?.write("Input: ")
                    writer?.flush()
                    state[state[ip+1]] = reader?.readLine()!!.toInt()
                    ip += 2
                }
                4 -> {
                    //println(getRespectingMode((this[ip]/100) % 10, ip+1))
                    writer?.write("${getRespectingMode((state[ip]/100) % 10, ip+1)}\n")
                    writer?.flush()
                    ip += 2
                }
                5 -> {
                    if (getRespectingMode((state[ip]/100) % 10, ip+1) != 0) {
                        ip = getRespectingMode((state[ip]/1000) % 10, ip+2)
                    } else {
                        ip += 3
                    }
                }
                6 -> {
                    if (getRespectingMode((state[ip]/100) % 10, ip+1) == 0) {
                        ip = getRespectingMode((state[ip]/1000) % 10, ip+2)
                    } else {
                        ip += 3
                    }
                }
                7 -> {
                    if (getRespectingMode((state[ip]/100) % 10, ip+1) < getRespectingMode((state[ip]/1000) % 10, ip+2)) {
                        state[state[ip+3]] = 1
                    } else {
                        state[state[ip+3]] = 0
                    }
                    ip += 4
                }
                8 -> {
                    if (getRespectingMode((state[ip]/100) % 10, ip+1) == getRespectingMode((state[ip]/1000) % 10, ip+2)) {
                        state[state[ip+3]] = 1
                    } else {
                        state[state[ip+3]] = 0
                    }
                    ip += 4
                }

                99 -> break@loop

                else -> NotImplementedError("Opcode ${state[ip]} is not implemented yet!")
            }
        }

        return this
    }
}