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

        operator fun times(other: Long) : Point =
                Point(x*other, y*other, z*other)

        operator fun div(other: Long) : Point =
                Point(x/other, y/other, z/other)
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

    private fun walkFromOrigin(cur: Point, asLong: (Point) -> Boolean, action: (Point) -> Unit) {
        
        action(cur)

        walkStartingLine(cur, Point(0, 1, 0), asLong, action)
        walkStartingLine(cur, Point(0, -1, 0), asLong, action)

        walkMediumLine(cur, Point(0, 0, 1), asLong, action)
        walkMediumLine(cur, Point(0, 0, -1), asLong, action)

        walkFinalLine(cur, Point(1, 0, 0), asLong, action)
        walkFinalLine(cur, Point(-1, 0, 0), asLong, action)
    }

    private fun walkStartingLine(p: Point, dir: Point, asLong: (Point) -> Boolean, action: (Point) -> Unit) {
        var dis = 1L
        while (true) {
            val cur = p + dir * dis++
            if (!asLong(cur))
                break
            action(cur)
            walkMediumLine(cur, Point(0, 0, 1), asLong, action)
            walkMediumLine(cur, Point(0, 0, -1), asLong, action)

            walkFinalLine(cur, Point(1, 0, 0), asLong, action)
            walkFinalLine(cur, Point(-1, 0, 0), asLong, action)
        }
    }

    private fun walkMediumLine(p: Point, dir: Point, asLong: (Point) -> Boolean, action: (Point) -> Unit) {
        var dis = 0L
        while (true) {
            val cur = p + dir * dis++
            if (!asLong(cur))
                break
            action(cur)
            walkFinalLine(cur, Point(1, 0, 0), asLong, action)
            walkFinalLine(cur, Point(-1, 0, 0), asLong, action)
        }
    }

    private fun walkFinalLine(p: Point, dir: Point, asLong: (Point) -> Boolean, action: (Point) -> Unit) {
        var dis = 0L
        while (true) {
            val cur = p + dir * dis++
            if (!asLong(cur))
                break
            action(cur)
        }
    }

    override fun part2(): String {

        val origin = Point(0,0,0)

        val pointMap = mutableMapOf<Point, Int>()

        val twoMeet = mutableSetOf<Pair<Point, List<NanoBot>>>()

        for (nanobot in input) {
            val point = nanobot.asPoint()
            for (other in input.dropWhile { it != nanobot }.drop(1)) {
                val otherP = other.asPoint()

                if (point.distanceTo(otherP) <= other.range + nanobot.range) {
                    val middle = (point * other.range + otherP * nanobot.range) / (other.range + nanobot.range)

                    val toCheck = mutableSetOf(middle)

                    middle.adjacent().flatMap { it.adjacent() }.flatMap { it.adjacent() }.forEach { toCheck.add(it) }

                    val checked = toCheck.firstOrNull { it.distanceTo(point) <= nanobot.range && it.distanceTo(otherP) <= other.range }
                    if (checked != null) {
                        twoMeet.add(checked to listOf(nanobot, other))
                    } else {
                        println(":(")
                    }
                }

            }
        }
        
        val (point, bots) = twoMeet.first()
        val tmpSet = mutableSetOf<Point>()

        walkFromOrigin(point, { p -> bots.all { b -> b.distanceTo(p) <= b.range }} ) { p -> tmpSet.add(p) }

        println("We got this far! We found: ${twoMeet.size}")
        println("We got this far! We found: ${tmpSet.size}")

        //println("These are all we got: $twoMeet")

        //val maxInRange = pointMap.maxBy { it.value }!!.value

        //val out = pointMap.filterValues { it == maxInRange }.minBy { it.key.distanceTo(origin) }!!.key.distanceTo(origin)

        return ""
    }
}