package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day04(override val adventOfCode: AdventOfCode) : Day {
    override val day = 4
    val input = adventOfCode.getInput(2019, day).split("-").map { it.toLong() }

    fun Long.meetsCriteria() : Boolean {
        val str = this.toString()
        return str.zipWithNext().all { (l, r) -> r >= l } && str.zipWithNext().any { (l, r) -> r == l }
    }

    fun Long.meetsStrictCriteria() : Boolean {
        val str = this.toString()
        return str.zipWithNext().all { (l, r) -> r >= l } && (str.windowed(4, 1).any { x ->
            val (a, b, c, d) = x.toCharArray()
            a != b && c != d && b == c
        } || (str[0] == str[1] && str[1] != str[2]) || (str[4] == str[5] && str[3] != str[4]))
    }

    override fun part1() : Any {
        var counter = 0
        for (i in input[0]..input[1]) {
            if (i.meetsCriteria())
                counter++
        }
        return counter
    }

    override fun part2() : Any {
        var counter = 0
        for (i in input[0]..input[1]) {
            if (i.meetsStrictCriteria())
            {
                counter++
            }
        }
        return counter
    }
}
