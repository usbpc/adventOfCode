package advent2018

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
    }
    private data class Point(val x: Long, val y: Long, val z: Long) {
        fun distanceTo(other: Point) =
                abs(this.x - other.x) + abs(this.y - other.y) + abs(this.z - other.z)
        fun adjacent() =
                listOf(
                        Point(x-1, y, z),
                        Point(x+1, y, z),
                        Point(x, y-1, z),
                        Point(x, y+1, z),
                        Point(x, y, z-1),
                        Point(x, y, z+1)
                )
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

    override fun part2(): String {
        val minX = input.minBy { it.x }!!.x
        val maxX = input.maxBy { it.x }!!.x

        val minY = input.minBy { it.y }!!.y
        val maxY = input.maxBy { it.y }!!.y

        val minZ = input.minBy { it.z }!!.z
        val maxZ = input.maxBy { it.z }!!.z

        val origin = Point(0,0,0)

        var maxInRange = Long.MIN_VALUE
        var minToOrigin = Long.MAX_VALUE


        for (nanobot in input) {
            var localMax = 0L
            var localMinDistance = Long.MAX_VALUE

            var curPos = Point(nanobot.x, nanobot.y, nanobot.z)
            val toCheck = ArrayDeque<Point>()
            toCheck.add(curPos)

            while (toCheck.isNotEmpty()) {
                curPos = toCheck.poll()
                val inRange = curPos.nanobotsInRange(input).toLong()
                if (inRange < localMax)
                    continue
                if (inRange == localMax) {
                    if (curPos.distanceTo(origin) < localMinDistance) {
                        localMinDistance = curPos.distanceTo(origin)
                    }
                } else {
                    localMax = inRange
                    localMinDistance = curPos.distanceTo(origin)
                }
                curPos.adjacent().forEach { toCheck.add(it) }
            }

            if (localMax >= maxInRange) {
                if (localMax != maxInRange || localMinDistance < minToOrigin) {
                    maxInRange = localMax
                    minToOrigin = localMinDistance
                }
            }
        }

        return "" + minToOrigin
    }
}