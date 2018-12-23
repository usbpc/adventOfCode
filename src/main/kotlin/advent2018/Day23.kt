package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
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

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                for (z in minZ..maxZ) {
                    val curPos = Point(x, y, z)

                    var inRange = 0L

                    for (nanobot in input) {
                        if (nanobot.distanceTo(curPos) <= nanobot.range) {
                            inRange++
                        }
                    }

                    if (inRange >= maxInRange) {
                        if (curPos.distanceTo(origin) < minToOrigin) {
                            minToOrigin = curPos.distanceTo(origin)
                            maxInRange = inRange
                        }
                    }

                }
            }
        }

        return "" + minToOrigin
    }
}