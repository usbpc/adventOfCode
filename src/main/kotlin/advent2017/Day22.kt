package advent2017

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Direction


class Day22(override val adventOfCode: AdventOfCode) : Day {
    private data class Point(val x: Int, val y: Int)
    override val day: Int = 22
    private val input = adventOfCode.getInput(2017, day).lines().map { it.toCharArray() }

    override fun part1(): Any {
        assert(input.size == input.first().size)
        assert(input.size % 2 != 0)

        val infected = mutableSetOf<Point>()

        input.withIndex().forEach{(row, line) ->
            line.withIndex().forEach {(col, char) ->
                if (char == '#') {
                    infected.add(Point(row - input.size / 2, col - input.size / 2))
                }
            }
        }

        var dir = Direction.UP
        var row = 0
        var col = 0
        var counter = 0
        repeat(10000) {
            val pos = Point(row, col)
            if (!infected.add(pos)) {
                dir = dir.right()
                infected.remove(pos)
            } else {
                counter++
                dir = dir.left()
            }
            when (dir) {
                Direction.UP -> row--
                Direction.DOWN -> row++
                Direction.LEFT -> col--
                Direction.RIGHT -> col++
            }
        }

        return counter.toString()
    }

    override fun part2(): Any {

        val infected = mutableSetOf<Point>()
        val weakend = mutableSetOf<Point>()
        val flagged = mutableSetOf<Point>()

        input.withIndex().forEach{(row, line) ->
            line.withIndex().forEach {(col, char) ->
                if (char == '#') {
                    infected.add(Point(row - input.size / 2, col - input.size / 2))
                }
            }
        }

        var dir = Direction.UP
        var row = 0
        var col = 0
        var counter = 0
        repeat(10000000) {
            val pos = Point(row, col)
            when (pos) {
                in weakend -> {
                    weakend.remove(pos)
                    infected.add(pos)
                    counter++
                }
                in infected -> {
                    infected.remove(pos)
                    flagged.add(pos)
                    dir = dir.right()
                }
                in flagged -> {
                    flagged.remove(pos)
                    dir = dir.back()
                }
                else -> {
                    weakend.add(pos)
                    dir = dir.left()
                }
            }
            when (dir) {
                Direction.UP -> row--
                Direction.DOWN -> row++
                Direction.LEFT -> col--
                Direction.RIGHT -> col++
            }
        }

        return counter.toString()
    }


}