package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day02(override val adventOfCode: AdventOfCode) : Day {
    override val day = 2
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toInt() }

    override fun part1() : Any {

            val myInput = input.toMutableList()

            myInput[1] = 12
            myInput[2] = 2

            return myInput.runSimple()[0]

    }

    override fun part2() : Any {
            var noun = 0
            var verb = 0

            while (true) {
                val myInput = input.toMutableList()

                myInput[1] = noun
                myInput[2] = verb

                if (myInput.runSimple()[0] == 19690720L)
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
