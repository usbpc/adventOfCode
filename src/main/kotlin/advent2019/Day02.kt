package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day02(override val adventOfCode: AdventOfCode) : Day {
    override val day = 2
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toInt() }

    override fun part1() : Any {
        var myInput = input.toMutableList()

        myInput[1] = 12
        myInput[2] = 2

        var pos = 0;
        loop@for (i in 0 until myInput.size step 4) {
            if (input[i] == 99 || i + 1 > input.size)
                break

            println(myInput)

            val op1 = myInput[myInput[i + 1]]
            val op2 = myInput[myInput[i + 2]]
            val pos = myInput[i + 3]
            val calc = when (myInput[i]) {
                1 -> op1 + op2;
                2 -> op1 * op2
                99 -> break@loop
                else -> {
                    println(myInput[i])
                    throw NotImplementedError()
                }
            }
            myInput[pos] = calc
        }

       return myInput[0]
    }

    override fun part2() : Any {
        val goal = 19690720
        var noun = 0
        var verb = 0

        while (true) {
            var myInput = input.toMutableList()

            myInput[1] = noun
            myInput[2] = verb

            loop@for (i in 0 until myInput.size step 4) {
                if (input[i] == 99 || i + 1 > input.size)
                    break

                println(myInput)

                val op1 = myInput[myInput[i + 1]]
                val op2 = myInput[myInput[i + 2]]
                val pos = myInput[i + 3]
                val calc = when (myInput[i]) {
                    1 -> op1 + op2;
                    2 -> op1 * op2
                    99 -> break@loop
                    else -> {
                        println(myInput[i])
                        throw NotImplementedError()
                    }
                }
                myInput[pos] = calc
            }
            println(myInput[0])
            if (goal == myInput[0])
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
