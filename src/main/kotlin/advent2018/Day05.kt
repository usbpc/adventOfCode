package advent2018

import xyz.usbpc  .aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder

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

    private fun String.reactPolymer() : String {
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

    override fun part1(): Any {
        return "" + input.reactPolymer().length
    }

    private fun String.removeCaseInsensitive(c: Char) : String =
            this.replace(Regex("${c.toUpperCase()}|${c.toLowerCase()}"), "")


    override fun part2(): Any {
        return "" + ('a'..'z').map { input.removeCaseInsensitive(it).reactPolymer() }.minBy { it.length }!!.length
    }
}