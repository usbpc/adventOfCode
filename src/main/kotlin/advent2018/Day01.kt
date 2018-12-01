package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day01(override val adventOfCode: AdventOfCode) : Day {
    override val day = 1
    val input = adventOfCode.getInput(2018, day).lines().map { it.toLong() }.toLongArray()
    override fun part1(): String {
        return "" + input.fold(0L) {a, n -> a+n}
    }

    override fun part2(): String {
        val repInput = sequence {
            var start = 0L
            var index = 0

            while (true) {
                yield(start)
                start += input[index]
                index = (index + 1) % input.size
            }
        }

        val known = mutableSetOf<Long>()

        for (i in repInput) {
            if (!known.add(i))
                return "" + i
        }

        return ""
    }
}