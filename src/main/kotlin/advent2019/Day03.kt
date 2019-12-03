package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Direction
import java.lang.IllegalStateException

fun abs(x: Int) = if (x >= 0) x else -x

data class Vector(val dir: Direction, val vel: Int)

data class PointWithSteps(val point: Point, val steps: Int) {
    override fun equals(other: Any?): Boolean {
        if (other !is PointWithSteps)
            return false
        return point == (other as PointWithSteps).point
    }

    override fun hashCode(): Int {
        return point.hashCode()
    }
}

data class Point(val x: Int, val y: Int) {
    public fun walk(dir: Direction) : Point {
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
            val set = mutableSetOf<PointWithSteps>()
            var steps = 0
            var cur = Point(0, 0)
            for (inst in insts) {
                repeat(inst.vel) {
                    steps += 1
                    cur = cur.walk(inst.dir)
                    set.add(PointWithSteps(cur, steps))
                }
            }
            set
        }

        val output = one.intersect(two).map { it.point }.sortedBy { it.fromCenter() }.map { point ->
            val first = one.single { it.point == point }
            val second = two.single { it.point == point }

            first.steps + second.steps
        }.sorted()

        return output.first()
    }

}
