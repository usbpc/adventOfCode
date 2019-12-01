package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day01(override val adventOfCode: AdventOfCode) : Day {
    override val day = 1
    val input = adventOfCode.getInput(2019, day).lines().map { it.toLong() }.toLongArray()
    override fun part1(): String {
        return "" + input.fold(0L) {a, n -> a + n/3 - 2}
    }

    override fun part2(): String {
        var acc = 0L
        var x = 0L
        var i = 0

        while (i < input.size || x > 0L) {
            if (x <= 0L) {
                x = input[i++]
            }
            x = x / 3 - 2
            if (x > 0) {
                acc += x
            }
        }
        return "" + acc
    }
}
