package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException
import kotlin.math.abs

class Day06(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 6

    data class Point(val x: Int, val y: Int) {
        fun up() = Point(x, y+1)
        fun down() = Point(x, y-1)
        fun right() = Point(x+1, y)
        fun left() = Point(x-1, y)
    }
    private val input = adventOfCode.getInput(2018, day).lines()
            .map { line ->
                val ints = line.extractInts()
                Point(ints[0], ints[1])
            }

    infix fun Point.distance(that: Point) : Int {
        return abs(this.x-that.x)+abs(this.y-that.y)
    }

    fun Point.nearestPoint(existing: List<Point>) : Point? {
        val mindistance = existing.minBy { this distance it }?.distance(this) ?: throw IllegalStateException("This shouldn't happen")
        return existing.singleOrNull { this distance it == mindistance }
    }


    override fun part1(): String {
        val highestX = input.sortedBy { -it.x }.first().x
        val lowestX = input.sortedBy { it.x }.first().x
        val highestY = input.sortedBy { -it.y }.first().y
        val lowestY = input.sortedBy { it.y }.first().y

        val res = mutableMapOf<Point, Int>()

        loop@for (point in input) {
            var area = 0
            var yw = point
            while (yw.nearestPoint(input) == point) {
                area++
                var xw = yw.right()
                while (xw.nearestPoint(input) == point) {
                    area++
                    xw = xw.right()
                    if (xw.x >= highestX)
                        continue@loop
                }
                xw = yw.left()
                while (xw.nearestPoint(input) == point) {
                    area++
                    xw = xw.left()
                    if (xw.x <= lowestX)
                        continue@loop
                }
                yw = yw.up()
                if (yw.y >= highestY)
                    continue@loop
            }
            yw = point.down()
            while (yw.nearestPoint(input) == point) {
                area++
                var xw = yw.right()
                while (xw.nearestPoint(input) == point) {
                    area++
                    xw = xw.right()
                    if (xw.x >= highestX)
                        continue@loop
                }
                xw = yw.left()
                while (xw.nearestPoint(input) == point) {
                    area++
                    xw = xw.left()
                    if (xw.x <= lowestX)
                        continue@loop
                }
                yw = yw.down()
                if (yw.y <= lowestY)
                    continue@loop
            }

            res[point] = area
        }

        return "" + res.values.max()
    }

    override fun part2(): String {
        val highestX = input.sortedBy { -it.x }.first().x
        val lowestX = input.sortedBy { it.x }.first().x
        val highestY = input.sortedBy { -it.y }.first().y
        val lowestY = input.sortedBy { it.y }.first().y

        var area = 0L

        for (x in lowestX..highestX) {
            for (y in lowestY..highestY) {
                val cur = Point(x, y)
                if (input.sumBy { it distance  cur } < 10000)
                    area++
            }
        }

        return "" + area
    }
}