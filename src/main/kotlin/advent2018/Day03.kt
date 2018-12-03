package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day03(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 3

    data class Coords(val id: Int, val left: Int, val top: Int, val x: Int, val y: Int)

    val regex = """#(\d+) @ (\d+),(\d+): (\d+)x(\d+)""".toRegex()

    val input = adventOfCode.getInput(2018, day).lines().map { line ->

        val result = regex.find(line)

        if (result != null && result.groups.size == 6) {
            Coords(result.groups[1]!!.value.toInt(), result.groups[2]!!.value.toInt(), result.groups[3]!!.value.toInt(), result.groups[4]!!.value.toInt(),  result.groups[5]!!.value.toInt())
        } else {
            null
        }
    }

    val grid = List(1000) {MutableList(1000) {0} }

    override fun part1(): String {
        input.filterNotNull().forEach { coord ->
            for (i in coord.left until coord.left+coord.x) {
                for (j in coord.top until coord.top+coord.y) {
                    grid[i][j] = grid[i][j] + 1
                }
            }
        }

        return "" + grid.flatMap { it }.count { it >= 2 }
    }

    override fun part2(): String {
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