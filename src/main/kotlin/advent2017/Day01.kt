package advent2017

import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day

class Day01(override val adventOfCode: AdventOfCode): Day {
    override val day: Int = 1
    private val input: String = adventOfCode.getInput(2017, 1)
    private fun countWithOffset(offset: Int): Int {
        var output = 0
        input.filterIndexed {i, char -> input[(i + offset) % input.length] == char}
                .forEach {output += it.toInt() - 48}
        return output
    }

    override fun part1(): String = countWithOffset(1).toString()
    override fun part2(): String = countWithOffset(input.length / 2).toString()
}