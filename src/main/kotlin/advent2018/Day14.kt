package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 14
    private val input = adventOfCode.getInput(2018, day)

    override fun part1(): String {
        val recipeNum = input.toInt()
        var firstElfCur = 0
        var secondElfCur = 1
        val recipeScores = mutableListOf(3, 7)

        while (recipeScores.size < recipeNum + 10) {
            val sum = recipeScores[firstElfCur] + recipeScores[secondElfCur]
            if (sum >= 10) {
                val firstDigit = sum / 10
                val secondDigit = sum % 10
                recipeScores.add(firstDigit)
                recipeScores.add(secondDigit)
            } else {
                recipeScores.add(sum)
            }

            firstElfCur = (firstElfCur + recipeScores[firstElfCur] + 1) % recipeScores.size
            secondElfCur = (secondElfCur +recipeScores[secondElfCur] + 1) % recipeScores.size
        }

        val out = StringBuilder()

        for (i in recipeNum until recipeNum+10) {
            out.append(recipeScores[i])
        }

        return out.toString()
    }

    override fun part2(): String {
        val inputList = input.map { c -> c - '0'}
        var firstElfCur = 0
        var secondElfCur = 1
        val recipeScores = mutableListOf(3, 7)

        while (true) {
            val sum = recipeScores[firstElfCur] + recipeScores[secondElfCur]
            if (sum >= 10) {
                val firstDigit = sum / 10
                val secondDigit = sum % 10
                recipeScores.add(firstDigit)
                recipeScores.add(secondDigit)
            } else {
                recipeScores.add(sum)
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