package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.permutations
import java.lang.IllegalStateException

class Day10(override val adventOfCode: AdventOfCode) : Day {
    override val day = 10

    val input = adventOfCode.getInput(2019, day).lines().map { it.toCharArray() }

    data class Point(val x: Int, val y: Int) {
        operator fun minus(other: Point) : Point = Point(this.x - other.x, this.y - other.y)
        operator fun plus(other: Point) : Point = Point(this.x + other.x, this.y + other.y)

        fun isInLine(start: Point, other: Point) : Boolean {
            val difference = this - start

            if (difference.x == 0) {
                if (this.x != other.x)
                    return false
                val solution = (other.y - start.y) / difference.y
                val test = Point(x, solution*difference.y + start.y)
                return test == other
            } else if (difference.y == 0) {
                if (this.y != other.y)
                    return false
                val solution = (other.x - start.x) / difference.x
                val test = Point(solution*difference.x + start.x, y)
                return test == other
            }

            val a = if (difference.x == 0) 0 else (other.x - start.x) / difference.x
            val b = if (difference.y == 0) 0 else (other.y - start.y) / difference.y

            if ((a == b && a > 0) || (a == 0 && b != 0) || (b == 0  && a != 0)) {
                val test = this + Point(difference.x*a+start.x, difference.y*a+start.y)
                return other == test
            }
            return false
        }
    }

    override fun part1() : Any {
        val maxX = input.first().lastIndex
        val maxY = input.lastIndex
        val asteroids = mutableSetOf<Point>().also { asteroids ->
            input.withIndex().forEach { (y, line) ->
                line.withIndex().forEach { (x, char) ->
                    if (char == '#') {
                        asteroids.add(Point(x, y))
                    }
                }
            }
        }.toSet()

        var maxCanSee = 0

        for (asteroid in asteroids) {
            println("Dealing with $asteroid")
            val canSee = asteroids.toMutableSet()
            canSee.remove(asteroid)
            for (other in asteroids) {
                if (other == asteroid)
                    continue
                for (thing in asteroids) {
                    if (thing == asteroid || thing == other)
                        continue
                    if (other.isInLine(asteroid, thing))
                        canSee.remove(thing)
                }
            }
            if (canSee.size > maxCanSee)
                maxCanSee = canSee.size
        }
        return maxCanSee
    }
    override fun part2() : Any {
        return ""
    }
}
