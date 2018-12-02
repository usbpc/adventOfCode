package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder

class Day02(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 2
    val input = adventOfCode.getInput(2018, day).lines()

    override fun part1(): String {
        val map = mutableMapOf<String, MutableMap<Char, Long>>()
        for (line in input) {
            map[line] = mutableMapOf()
            val myMap = map[line]!!
            line.forEach { letter -> myMap[letter] = (myMap[letter] ?: 0) + 1 }
        }

        return "" + map.filter { it.value.any { it.value == 2L } }.count() * map.filter { it.value.any { it.value == 3L} }.count()
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
                        diff++
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