package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day05(override val adventOfCode: AdventOfCode) : Day {
    override val day = 5
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toInt() }

    override fun part1() : Any {
        /*val myInput = "3,21,1008,21,8,20,1005,20,22,107,8,21,20,1006,20,31,1106,0,36,98,0,0,1002,21,125,20,4,20,1105,1,46,104,999,1105,1,46,1101,1000,1,20,4,20,1105,1,46,98,99"
                .split(",")
                .map { it.toInt() }
                .toMutableList()//
        */
        val myInput = input.toMutableList()

        return Intcode.run { myInput.simulate() }
    }

    override fun part2() : Any {
        return ""
    }
}
