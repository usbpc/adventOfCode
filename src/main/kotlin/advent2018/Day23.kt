package advent2018

import advent2017.Day08
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*
import kotlin.math.abs
import kotlin.math.min

class Day23(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 23
    private val input = adventOfCode.getInput(2018, 23).lines().map { it.extractLongs() }
            .map { NanoBot(it[0], it[1], it[2], it[3]) }


    private data class NanoBot(val x: Long, val y: Long, val z: Long , val range : Long) {
        fun distanceTo(other: NanoBot) =
                abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
        fun distanceTo(other: Point) =
                abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
        fun asPoint() = Point(x, y, z)
    }
    private data class Point(val x: Long, val y: Long, val z: Long) {
        fun distanceTo(other: Point) =
                abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
        fun withoutPrev() = Point(x, y, z)
        fun adjacent() =
                listOf(
                        Point(x-1, y, z),
                        Point(x+1, y, z),
                        Point(x, y-1, z),
                        Point(x, y+1, z),
                        Point(x, y, z-1),
                        Point(x, y, z+1)
                )
        operator fun plus(other: Point) : Point =
                Point(x+other.x, y+other.y, z+other.z)

        operator fun times(other: Int) : Point =
                Point(x*other, x*other, z*other)
    }

    override fun part1(): String {
        val strongest = input.maxBy { it.range }!!

        var out = 0L

        for (nanobot in input) {
            if (nanobot.distanceTo(strongest) <= strongest.range)
                out++
        }

        return "" + out
    }

    private fun Point.nanobotsInRange(nanobots: List<NanoBot>) : Int {
        var out = 0
        for (nanobot in nanobots) {
            if (nanobot.distanceTo(this) <= nanobot.range)
                out++
        }
        return out
    }

    private fun walkFromOrigin(cur: Point, maxDistance: Int, action: (Point) -> Unit) {
        
        action(cur)

        walkStartingLine(cur, Point(0, 1, 0), maxDistance, action)
        walkStartingLine(cur, Point(0, -1, 0), maxDistance, action)

        walkMediumLine(cur, Point(0, 0, 1), maxDistance, action)
        walkMediumLine(cur, Point(0, 0, -1), maxDistance, action)

        walkFinalLine(cur, Point(1, 0, 0), maxDistance, action)
        walkFinalLine(cur, Point(-1, 0, 0), maxDistance, action)
    }

    private fun walkStartingLine(p: Point, dir: Point, maxDistance: Int, action: (Point) -> Unit) {
        for (dis in 1..maxDistance) {
            val cur = p + dir * dis
            action(cur)
            walkMediumLine(cur, Point(0, 0, 1), maxDistance-dis, action)
            walkMediumLine(cur, Point(0, 0, -1), maxDistance-dis, action)

            walkFinalLine(cur, Point(1, 0, 0), maxDistance-dis, action)
            walkFinalLine(cur, Point(-1, 0, 0), maxDistance-dis, action)
        }
    }

    private fun walkMediumLine(p: Point, dir: Point, maxDistance: Int, action: (Point) -> Unit) {
        for (dis in 1..maxDistance) {
            val cur = p + dir * dis
            action(cur)
            walkFinalLine(cur, Point(1, 0, 0), maxDistance-dis, action)
            walkFinalLine(cur, Point(-1, 0, 0), maxDistance-dis, action)
        }
    }

    private fun walkFinalLine(p: Point, dir: Point, maxDistance: Int, action: (Point) -> Unit) {
        for (dis in 1..maxDistance) {
            val cur = p + dir * dis
            action(cur)
        }
    }

    override fun part2(): String {

        val origin = Point(0,0,0)

        val pointMap = mutableMapOf<Point, Int>()

        for (nanobot in input) {
            val point = nanobot.asPoint()
            walkFromOrigin(point, nanobot.range.toInt()) { curP ->
                if (input.filter { it !== nanobot }.any { it.distanceTo(curP) <= it.range })
                    pointMap[curP] = 0
            }
        }
        println("We got this far!")
        val maxInRange = pointMap.maxBy { it.value }!!.value

        val out = pointMap.filterValues { it == maxInRange }.minBy { it.key.distanceTo(origin) }!!.key.distanceTo(origin)

        return "" + out
    }
}