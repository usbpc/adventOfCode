package advent2019

import advent2017.Day08
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Direction
import xyz.usbpc.utils.NeverNullMap
import java.lang.IllegalStateException

fun abs(x: Int) = if (x >= 0) x else -x

data class Vector(val dir: Direction, val vel: Int)

data class Point(val x: Int, val y: Int) {
    fun walk(dir: Direction) : Point {
        return when (dir) {
            Direction.UP -> Point(x, y+1)
            Direction.DOWN -> Point(x, y-1)
            Direction.LEFT -> Point(x-1, y)
            Direction.RIGHT -> Point(x+1, y)
        }
    }
    fun fromCenter() = abs(x) + abs(y)
}

class Day03(override val adventOfCode: AdventOfCode) : Day {
    override val day = 3
    val input = adventOfCode.getInput(2019, day).lines().map {
        val dirs = it.split(",")

        dirs.map { thin ->
            val direction = when (thin[0]) {
                'U' -> Direction.UP
                'D' -> Direction.DOWN
                'L' -> Direction.LEFT
                'R' -> Direction.RIGHT
                else -> throw IllegalStateException("Not a valid letter!")
            }

            Vector(direction, thin.substring(1).toInt())
        }
    }

    override fun part1() : Any {
        val (one, two) = input.map { insts ->
            val set = mutableSetOf<Point>()
            var cur = Point(0, 0)
            for (inst in insts) {
                repeat(inst.vel) {
                    cur = cur.walk(inst.dir)
                    set.add(cur)
                }
            }
            set
        }

        return one.intersect(two).sortedBy { it.fromCenter() }.first().fromCenter()
    }

    override fun part2() : Any {
        val (one, two) = input.map { insts ->
            val map = NeverNullMap<Point, Int> { Int.MAX_VALUE }
            var steps = 0
            var cur = Point(0, 0)
            for (inst in insts) {
                repeat(inst.vel) {
                    steps += 1
                    cur = cur.walk(inst.dir)
                    map[cur] = steps
                }
            }
            map
        }

        return one.keys.intersect(two.keys)
                .map { one[it] + two[it]}
                .min()
                .toString()
    }

}
