package advent2017

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import kotlin.math.abs
import kotlin.math.max

class Day11(override val adventOfCode: AdventOfCode) : Day {
    enum class Direction(val shortName: String) {
        NORTH("n"),
        NORTH_EAST("ne"),
        SOUTH_EAST("se"),
        SOUTH("s"),
        SOUTH_WEST("sw"),
        NORTH_WEST("nw");

        companion object {
            fun getByShortName(sn: String) = Direction.values().single {it.shortName == sn}
        }
    }

    class HexWalker(private var x: Int, private var y: Int) {
        data class Position(val x: Int, val y: Int)

        val currentPosition: Position
            get() = Position(x, y)
        val distanceToOrigin: Int
            get() {
                if (x == 0) return abs(y)
                if (abs(y) == abs(x) / 2 && y <= 0) return abs(x)
                if (abs(y) == (abs(x) + 1) / 2 && y > 0) return abs(x)
                if (abs(x) / 2 > abs(y)) return abs(x)
                return abs(x) + abs(y) - if (y > 0) {
                    (abs(x) + 1) / 2
                } else {
                    (abs(x)) / 2
                }
            }

        fun walk(direction: Direction) {
            when (direction) {
                Direction.NORTH -> y++
                Direction.SOUTH -> y--
                Direction.NORTH_EAST -> {
                    if (x++ % 2 == 0) {
                        y++
                    }
                }
                Direction.SOUTH_EAST -> {
                    if (x++ % 2 != 0) {
                        y--
                    }
                }
                Direction.NORTH_WEST -> {
                    if (x-- % 2 == 0) {
                        y++
                    }
                }
                Direction.SOUTH_WEST -> {
                    if (x-- % 2 != 0) {
                        y--
                    }
                }

            }
        }
    }

    override val day: Int = 11
    private val input = adventOfCode.getInput(2017, 11).split(',').map { Direction.getByShortName(it) }

    override fun part1(): Any {
        return HexWalker(0, 0).apply {
            input.forEach {direction ->
                walk(direction)
            }
        }.distanceToOrigin.toString()
    }

    override fun part2(): Any {
        val hexWalker = HexWalker(0, 0)
        var maxDistance = 0
        input.forEach {
            hexWalker.walk(it)
            maxDistance = max(maxDistance, hexWalker.distanceToOrigin)
        }
        return maxDistance.toString()
    }
}