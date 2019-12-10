package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.permutations
import java.lang.IllegalStateException
import java.util.*

fun gcd(x: Int, y: Int) : Int {
    var a = x
    var b = y
    var h: Int

    if (a == 0) return abs(b)
    if (b == 0) return abs(a)

    do {
        h = a % b
        a = b
        b = h
    } while (b != 0)

    return abs(a)
}

class Day10(override val adventOfCode: AdventOfCode) : Day {
    override val day = 10

    val input = adventOfCode.getInput(2019, day).lines().map { it.toCharArray() }

    data class Point(val x: Int, val y: Int) : Comparable<Point> {

        enum class Quadrant {
            TopRight,
            BottomRight,
            BottomLeft,
            TopLeft;
        }

        fun calculateQuadrant() : Quadrant {
            return if (this.x >= 0 && this.y >= 0) {
                Quadrant.TopRight
            } else if (this.x >= 0 && this.y < 0) {
                Quadrant.BottomRight
            } else if (this.x < 0 && this.y <= 0) {
                Quadrant.BottomLeft
            } else if (this.x < 0 && this.y > 0) {
                Quadrant.TopLeft
            } else {
                throw IllegalStateException("This can't happen!")
            }
        }

        override fun compareTo(other: Point): Int {
            val athing = this.calculateQuadrant()
            val bthing = other.calculateQuadrant()


            val aslope = this.slope()
            val bslope = other.slope()
            if (athing == bthing) {
                when (athing) {
                    Quadrant.TopRight -> {
                        return when {
                            aslope > bslope -> -1
                            aslope < bslope -> 1
                            else -> 0
                        }
                    }
                    Quadrant.BottomRight -> {
                        return when {
                            aslope > bslope -> -1
                            aslope < bslope -> 1
                            else -> 0
                        }
                    }
                    Quadrant.BottomLeft -> {
                        return when {
                            aslope > bslope -> -1
                            aslope < bslope -> 1
                            else -> 0
                        }
                    }
                    Quadrant.TopLeft -> {
                        return when {
                            aslope > bslope -> -1
                            aslope < bslope -> 1
                            else -> 0
                        }
                    }
                }
            }

            if (athing == Quadrant.TopRight) {
                return -1
            }
            if (bthing == Quadrant.TopRight) {
                return 1
            }
            if (athing == Quadrant.BottomRight) {
                return -1
            }
            if (bthing == Quadrant.BottomRight) {
                return 1
            }
            if (athing == Quadrant.BottomLeft) {
                return -1
            }
            if (bthing == Quadrant.BottomLeft) {
                return 1
            }

            throw IllegalStateException("This can't happen!")
        }

        operator fun minus(other: Point) : Point = Point(this.x - other.x, this.y - other.y)
        operator fun plus(other: Point) : Point = Point(this.x + other.x, this.y + other.y)
        operator fun div(mult: Int) : Point = Point(this.x/mult, this.y/mult)
        operator fun div(other: Point) : Point = Point(this.x/other.x, this.y/other.y)
        operator fun times(other: Point) : Point = Point(this.x*other.x, this.y*other.y)
        operator fun times(mult: Int) : Point = Point(this.x*mult, this.y*mult)

        fun xyGCD() = gcd(x, y)

        fun distance(other: Point) = (other - this).let {
            abs(it.x) + abs(it.y)
        }

        fun directionVector(other: Point) = (other - this).let {diff ->
            diff / diff.xyGCD()
        }

        fun slope(other: Point) =
                (this - other).let {
                it.y.toDouble() / it.x.toDouble()
            }
        fun slope() = this.y.toDouble() / this.x.toDouble()

        fun isInLine(start: Point, other: Point) : Boolean {
            val difference =
                    (this - start).let {difference ->
                        difference / difference.xyGCD()
                    }


            val thing = (other - start) / difference

            if (thing.x == thing.y && thing.x > 0) {
                val test = start + difference * thing
                return other == test
            }

            if (difference.x == 0) {
                if (start.x == other.x)
                    return true
            } else if (difference.y == 0) {
                if (start.y == other.y)
                    return true
            } else {
                val a = (other.x - start.x) / difference.x
                val b = (other.y - start.y) / difference.y

                if (a == b && a > 0) {
                    val test = start + Point(difference.x*a, difference.y*a)
                    return other == test
                }
            }
            return false
        }
    }

    fun inputSet() = mutableSetOf<Point>().also { asteroids ->
        input.withIndex().forEach { (y, line) ->
            line.withIndex().forEach { (x, char) ->
                if (char == '#') {
                    asteroids.add(Point(x, y))
                }
            }
        }
    }.toSet()

    fun doTheThing(): Pair<Point, Set<Point>> {
        val maxY = input.lastIndex
        val maxX = input.first().lastIndex

        val asteroids = inputSet()


        var bestPos: Point = Point(0, 0)
        var maxCanSee = setOf<Point>()
        for (asteroid in asteroids) {
            //println("Dealing with $asteroid")
            val canSee = asteroids.toMutableSet()
            canSee.remove(asteroid)

            for (other in asteroids) {
                if (other == asteroid)
                    continue

                val dir = asteroid.directionVector(other)

                var cur = other + dir
                while (cur.x <= maxX && cur.y <= maxY && cur.x >= 0 && cur.y >= 0) {
                    //println("Testting $cur")
                    canSee.remove(cur)
                    cur += dir
                }

            }
            if (canSee.size > maxCanSee.size) {
                //println("Max right now is $asteroid")
                bestPos = asteroid
                maxCanSee = canSee
            }
        }


        return Pair(bestPos, maxCanSee)
    }

    override fun part1() : Any {
        return doTheThing().second.size
    }

    fun List<List<Point>>.getElement(num: Int) : Point {
        var toGo = num

        var cur = this

        while (toGo > this.size) {
            toGo -= cur.size
            cur = this.map { it.drop(1) }.filter { it.isNotEmpty() }
        }

        return cur[toGo].first()
    }

    fun SortedMap<Point, MutableList<Point>>.doThing(foo: Int) : Point {
        var toGo = foo
        while (toGo >= this.size) {
            if (toGo == 1)
                break
            toGo -= this.size
            val toDelete = mutableSetOf<Point>()
            this.forEach { (index, b) ->
                b.removeAt(0)
                if (b.isEmpty())
                    toDelete.add(index)
            }

            toDelete.forEach { this.remove(it) }
        }

        val sortedKeys = this.keys.toSortedSet(Comparator { a, b ->
            val aslope = a.slope()
            val bslope = b.slope()
            when {
                aslope > bslope -> 1
                aslope < bslope -> -1
                else -> 0
            }
        }).toMutableList()

        return this[sortedKeys[toGo]]!!.first()
    }

    override fun part2() : Any {
        val all = inputSet().toMutableSet()
        println("Total asteroids to destroy ${all.size}")
        val (base, _) = doTheThing()

        all.remove(base)

        val dirs = all.groupBy { base.directionVector(it) }.mapValues { it.value.sortedBy { base.distance(it) }.toMutableList() }
                //.map { Pair(base.slope(it), it) }.toList().sortedBy { it.first }

        val sortedList = dirs.map { it.value }.sortedBy {
            val x = base.directionVector(it.first())
            Point(x.x, -x.y)
        }.toList()

        var c = 0
        sortedList.forEach {
            print("Slope: ${base.directionVector(it.first()).slope()} = ")
            it.forEach {
                print("$it, ")
                c++
            }
            print('\n')
        }

        println("List counted is $c")

        val sortedDirs = dirs.toSortedMap()

        println("Base is $base")

        /*sortedDirs.entries.forEach { (dir, points) ->
            print("[$dir] (${dir.slope()})= ")
            points.forEach {
                count++
                print("$it, ")
            }
            print('\n')
        }*/


        //(0 until all.size).forEach { println(sortedList.getElement(it)) }

        val foobard = sortedList.getElement(4)

        println("Something")

        val sorted = listOf(
                Point(0, -1),
                Point(1, -1),
                Point(1, 0),
                Point(1, 1),
                Point(0, 1),
                Point(-1, 1),
                Point(-1, 0),
                Point(-1, -1)
        )

        sorted.windowed(2, 1).forEach { (a, b) ->
            //println("$a < $b ${a < b}")
        }

        val a =  Point(-1, 1)
        val b = Point(-1, -1)

        println("a < b = ${a < b}")

        return foobard.x * 100 + foobard.y
    }
}
