package advent2018

import xyz.usbpc  .aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder
import java.util.*

class Day05(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 5
    private val input = adventOfCode.getInput(2018, day)

    private infix fun Char.reactsWith(that: Char) : Boolean {
        return if (this.isLowerCase()) {
            this.toUpperCase() == that
        } else {
            this.toLowerCase() == that
        }
    }

    private fun <E> Stack<E>.properPeek() : E? {
        return if (this.empty()) {
            null
        } else {
            this.peek()
        }
    }

    private fun String.reduce() : String {
        val stack = StringBuilder()

        for (c in this) {
            if (stack.lastOrNull()?.reactsWith(c) == true) {
                stack.deleteCharAt(stack.lastIndex)
            } else {
                stack.append(c)
            }
        }

        return stack.toString()
    }

    override fun part1(): String {
        return "" + input.reduce().length
    }

    override fun part2(): String {
        val results = mutableListOf<String>()

        for (c in 'a'..'z') {
            var string = input.replace("$c", "").replace("${c.toUpperCase()}", "").reduce()

            results.add(string)
        }

        return "" + results.minBy { it.length }!!.length
    }
}