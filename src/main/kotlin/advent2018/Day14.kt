package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 14
    private val input = adventOfCode.getInput(2018, day)

    override fun part1(): String {
        val input = input.toInt()
        var firstElfCur = 0
        var secondElfCur = 1
        val recepieScores = mutableListOf(3, 7)

        while (recepieScores.size < input + 10) {
            val sum = recepieScores[firstElfCur] + recepieScores[secondElfCur]
            if (sum >= 10) {
                val firstDigit = sum / 10
                val secondDigit = sum % 10
                recepieScores.add(firstDigit)
                recepieScores.add(secondDigit)
            } else {
                recepieScores.add(sum)
            }

            firstElfCur = (firstElfCur + recepieScores[firstElfCur] + 1) % recepieScores.size
            secondElfCur = (secondElfCur +recepieScores[secondElfCur] + 1) % recepieScores.size
        }

        val out = StringBuilder()

        for (i in input until input+10) {
            out.append(recepieScores[i])
        }

        return out.toString()
    }

    override fun part2(): String {
        val inputList = input.toCharArray().map { it.toString().toByte() }
        var firstElfCur = 0
        var secondElfCur = 1
        val recipeScores = mutableListOf(3.toByte(), 7.toByte())

        while (true) {
            val sum = recipeScores[firstElfCur] + recipeScores[secondElfCur]
            if (sum >= 10) {
                val firstDigit = sum / 10
                val secondDigit = sum % 10
                recipeScores.add(firstDigit.toByte())
                recipeScores.add(secondDigit.toByte())
            } else {
                recipeScores.add(sum.toByte())
            }

            if (recipeScores.size >= inputList.size) {
                if (sum >= 10 && recipeScores.size-1 >= inputList.size) {
                    var cmp = true
                    for (i in 0..inputList.lastIndex) {
                        if (inputList[i] != recipeScores[i+recipeScores.lastIndex-inputList.lastIndex-1]) {
                            cmp = false
                            break
                        }
                    }
                    if (cmp) {
                        return "${recipeScores.size - 1 - inputList.size}"
                    }
                }
                var cmp = true
                for (i in 0..inputList.lastIndex) {
                    if (inputList[i] != recipeScores[i+recipeScores.lastIndex-inputList.lastIndex]) {
                        cmp = false
                        break
                    }
                }
                if (cmp) {
                    return "${(recipeScores.size) - inputList.size}"
                }
            }

            firstElfCur = (firstElfCur + recipeScores[firstElfCur] + 1) % recipeScores.size
            secondElfCur = (secondElfCur +recipeScores[secondElfCur] + 1) % recipeScores.size
        }
    }
}