package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day10(override val adventOfCode: AdventOfCode) : Day {
    override val day = 10

    val input = adventOfCode.getInput(2019, day).lines().map { it.toCharArray() }

    data class Point(val x: Int, val y: Int) {
        operator fun minus(other: Point) : Point = Point(this.x - other.x, this.y - other.y)
        operator fun plus(other: Point) : Point = Point(this.x + other.x, this.y + other.y)

        fun isInLine(start: Point, other: Point) : Boolean {
            val difference = this - start

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

    override fun part1() : Any {
        val asteroids = mutableSetOf<Point>().also { asteroids ->
            input.withIndex().forEach { (y, line) ->
                line.withIndex().forEach { (x, char) ->
                    if (char == '#') {
                        asteroids.add(Point(x, y))
                    }
                }
            }
        }.toSet()

        var maxCanSee = setOf<Point>()
        for (asteroid in asteroids) {
            //println("Dealing with $asteroid")
            val canSee = asteroids.toMutableSet()
            canSee.remove(asteroid)

            test@for (other in asteroids) {
                if (other == asteroid)
                    continue@test

                test2@for (thing in asteroids) {
                    if (thing == asteroid || thing == other)
                        continue@test2
                    if (other.isInLine(asteroid, thing))
                        canSee.remove(thing)
                }
            }
            if (canSee.size > maxCanSee.size) {
                println("Max right now is $asteroid")
                maxCanSee = canSee
            }
        }
        return maxCanSee.size
    }
    override fun part2() : Any {
        return ""
    }
}
