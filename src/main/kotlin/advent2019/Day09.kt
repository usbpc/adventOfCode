package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.permutations
import java.lang.IllegalStateException

class Day09(override val adventOfCode: AdventOfCode) : Day {
    override val day = 9

    val input = adventOfCode.getInput(2019, day).split(",").map { it.toInt() }

    override fun part1() : Any {
        return input.toMutableList().runWithInput(listOf(1)).out
    }
    override fun part2() : Any {


        return input.toMutableList().runWithInput(listOf(2)).out
    }
}
