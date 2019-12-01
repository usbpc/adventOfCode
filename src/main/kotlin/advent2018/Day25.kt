package advent2018

import advent2017.Day08
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import kotlin.math.abs

class Day25(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 25

    private val input= adventOfCode.getInput(2018, day).lines().map { it.extractInts() }.map { Point(it[0],it[1],it[2],it[3]) }
    data class Point(val x1: Int, val x2: Int, val x3: Int, val x4: Int) {
        fun distanceTo(other: Point) =
                abs(this.x1-other.x1)+abs(this.x2-other.x2)+abs(this.x3-other.x3)+abs(this.x4-other.x4)
    }

    fun MutableMap<Point, MutableSet<Point>>.getAllConnected(cur: Point, soFar : MutableSet<Point>) {
        this[cur]?.let { points ->
            soFar.addAll(points)
            this.remove(cur)
            for (point in points) {
                this.getAllConnected(point, soFar)
            }
        }
    }

    override fun part1(): String {
        val map = mutableMapOf<Point, MutableSet<Point>>()

        for (point1 in input) {
            map[point1] = mutableSetOf()
            for (point2 in input) {
                if (point1.distanceTo(point2) <= 3) {
                    map[point1]!!.add(point2)
                }
            }
        }

        val collections = mutableListOf<Set<Point>>()

        for (point in input) {
            val tmp = mutableSetOf<Point>()
            map.getAllConnected(point, tmp)
            collections.add(tmp)
        }

        return "" + collections.count { it.isNotEmpty() }
    }

    override fun part2(): String {


        return ""
    }
}