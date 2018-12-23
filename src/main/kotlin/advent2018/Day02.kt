package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder

class Day02(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 2
    val input = adventOfCode.getInput(2018, day).lines()

    override fun part1(): String {
        val charCounts = input.map { line -> line.groupBy { it }.map { it.value.size } }
        return "" + charCounts.count{ 2 in it } * charCounts.count { 3 in it }
    }

    override fun part2(): String {
        val out = StringBuilder()
        loop@for (i in 0 until input.size) {
            val current = input[i]
            for (j in i until input.size) {
                val cmp = input[j]
                out.clear()
                var diff = 0
                for (count in 0 until current.length)
                    if (current[count] != cmp[count]) {
                        if (++diff > 1)
                            break
                    } else {
                        out.append(current[count])
                    }
                if (diff == 1)
                    break@loop

            }
        }
        return out.toString()
    }
}