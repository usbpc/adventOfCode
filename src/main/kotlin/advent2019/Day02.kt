package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day02(override val adventOfCode: AdventOfCode) : Day {
    override val day = 2
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toInt() }

    fun MutableList<Int>.simulate() : Int {
        var ip = 0

        loop@while (true) {
            //Load operators into registers
            when (this[ip]) {
                1 -> {
                    this[this[ip+3]] = this[this[ip+1]] + this[this[ip+2]]
                    ip += 4
                }

                2 -> {
                    this[this[ip+3]] = this[this[ip+1]] * this[this[ip+2]]
                    ip += 4
                }

                99 -> break@loop

                else -> NotImplementedError("Opcode ${this[ip]} is not implemented yet!")
            }
        }

        return this[0]
    }

    override fun part1() : Any {
        val myInput = input.toMutableList()

        myInput[1] = 12
        myInput[2] = 2

       return myInput.simulate()
    }

    override fun part2() : Any {

        var noun = 0
        var verb = 0

        while (true) {
            val myInput = input.toMutableList()

            myInput[1] = noun
            myInput[2] = verb

            if (myInput.simulate() == 19690720)
                break

            noun++
            if (noun == 100) {
                noun = 0
                verb++
            }
        }

        return 100 * noun + verb
    }
}
