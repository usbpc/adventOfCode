package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException

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
            return if (this.x >= 0 && this.y <= 0) {
                Quadrant.TopRight
            } else if (this.x >= 0 && this.y > 0) {
                Quadrant.BottomRight
            } else if (this.x < 0 && this.y >= 0) {
                Quadrant.BottomLeft
            } else if (this.x < 0 && this.y < 0) {
                Quadrant.TopLeft
            } else {
                throw IllegalStateException("This can't happen!")
            }
        }

        override fun compareTo(other: Point): Int {
            val aQuadrant = this.calculateQuadrant()
            val bQuadrant = other.calculateQuadrant()

            val aSlope = this.slope()
            val bSlope = other.slope()
            if (aQuadrant == bQuadrant) {
                when (aQuadrant) {
                    Quadrant.TopRight, Quadrant.BottomRight -> {
                        return when {
                            aSlope > bSlope -> 1
                            aSlope < bSlope -> -1
                            else -> 0
                        }
                    }
                    Quadrant.BottomLeft, Quadrant.TopLeft -> {
                        return when {
                            aSlope > bSlope -> 1
                            aSlope < bSlope -> -1
                            else -> 0
                        }
                    }
                }
            }

            if (aQuadrant == Quadrant.TopRight) {
                return -1
            }
            if (bQuadrant == Quadrant.TopRight) {
                return 1
            }
            if (aQuadrant == Quadrant.BottomRight) {
                return -1
            }
            if (bQuadrant == Quadrant.BottomRight) {
                return 1
            }
            if (aQuadrant == Quadrant.BottomLeft) {
                return -1
            }
            if (bQuadrant == Quadrant.BottomLeft) {
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

    fun findBase(): Pair<Point, Set<Point>> {
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
        return findBase().second.size
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

    override fun part2() : Any {
        val all = inputSet().toMutableSet()

        val (base, _) = findBase()
        all.remove(base)

        val dirs = all
                .groupBy { base.directionVector(it) }
                .mapValues { it.value.sortedBy { base.distance(it) } }
                .map { (_, value) ->
                    value
                }
                .sortedBy {
                    base.directionVector(it.first())
                }

        val longest = dirs.maxBy { it.size }!!.size

        val sortedList = mutableListOf<Point>()

        for (i in 0..longest) {
            dirs
                    .filter { i < it.size }
                    .forEach {
                        sortedList.add(it[i])
                    }
        }

        //creatNiceString(base, all, sortedList, 18)

        return sortedList[199].x * 100 + sortedList[199].y
    }

    fun creatNiceString(base: Point, asteroids: Set<Point>, targetList: List<Point>, offset: Int) {
        val maxY = input.size
        val maxX = input.first().size

        val thing = List(maxY) { CharArray(maxX) { '.' } }
        asteroids.forEach {
            thing[it.y][it.x] = '#'
        }

        thing[base.y][base.x] = 'X'

        for (i in 0..offset) {
            if (i >= targetList.size)
                break
            val target = targetList[i]
            thing[target.y][target.x] = '.'
        }

        for (i in 0..8) {
            if (i >= targetList.size)
                break
            val target = targetList[offset+i]
            thing[target.y][target.x] = (i + '1'.toInt()).toChar()
        }

        buildString {
            thing.forEach { row ->
                row.forEach { char ->
                    this.append(char)
                }
                this.append('\n')
            }
        }.let {
            print(it)
        }
    }
}
