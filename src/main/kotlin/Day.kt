import usbpc.aoc.inputgetter.AdventOfCode

interface Day {
    val adventOfCode: AdventOfCode
    fun part1(): String
    fun part2(): String
}