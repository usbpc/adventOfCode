package advent2019

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day05(override val adventOfCode: AdventOfCode) : Day {
    override val day = 5
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toInt() }

    override fun part1() : Any = input.toMutableList().runWithInput(listOf(1)).out

    override fun part2() : Any = input.toMutableList().runWithInput(listOf(5)).out
}
