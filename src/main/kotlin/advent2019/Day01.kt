package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day01(override val adventOfCode: AdventOfCode) : Day {
    override val day = 1
    val input = adventOfCode.getInput(2019, day).lines().map { it.toLong() }.toLongArray()

    private fun calcFuel(weight: Long) = weight / 3 - 2

    private fun recCalcFuel(weight: Long) : Long {
        val x = calcFuel(weight)
        return when {
            x <= 0 -> 0
            else -> x + recCalcFuel(x)
        }
    }

    override fun part1() = input.map(this::calcFuel).sum()

    override fun part2() = input.map(this::recCalcFuel).sum()
}
