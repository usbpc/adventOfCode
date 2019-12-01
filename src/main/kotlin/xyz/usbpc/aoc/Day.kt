package xyz.usbpc.aoc

import xyz.usbpc.aoc.inputgetter.AdventOfCode

interface Day {
    val adventOfCode: AdventOfCode
    val day: Int
    fun part1(): Any
    fun part2(): Any
}