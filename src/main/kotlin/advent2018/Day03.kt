package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import kotlin.math.min
import kotlin.streams.asStream

class Day03(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 3

    data class Coords(val id: Int, val left: Int, val top: Int, val x: Int, val y: Int)

    val input = adventOfCode.getInput(2018, day).lines().map { line ->

        val numbers = line.extractInts()

        if (numbers.size == 5) {
            Coords(numbers[0], numbers[1], numbers[2], numbers[3],  numbers[4])
        } else {
            null
        }
    }

    val grid = List(1000) { ByteArray(1000) }

    override fun part1(): Any {
        input.filterNotNull().forEach { coord ->
            for (i in coord.left until coord.left+coord.x) {
                for (j in coord.top until coord.top+coord.y) {
                    grid[i][j] = min((grid[i][j] + 1), 2).toByte()
                }
            }
        }

        //return "" + grid.flatMap { it }.count { it >= 2 }
        return "${grid.stream().flatMap { it.asSequence().asStream() }.filter {it >= 2}.count()}"
    }

    override fun part2(): Any {
        input.filterNotNull().forEach { coord ->
            var candidate = true
            loop@for (i in coord.left until coord.left+coord.x) {
                for (j in coord.top until coord.top+coord.y) {
                    if (grid[i][j] > 1) {
                        candidate = false
                        continue@loop
                    }
                }
            }
            if (candidate)
                return  "" + coord.id
        }

        return "hi"
    }

}