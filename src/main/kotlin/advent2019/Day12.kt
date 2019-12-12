package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.NeverNullMap
import xyz.usbpc.utils.combinations
import xyz.usbpc.utils.permutations
import xyz.usbpc.utils.whileCount

class Day12(override val adventOfCode: AdventOfCode) : Day {
    override val day = 12

    val inputRegex = Regex("<x=(-?\\d+), y=(-?\\d+), z=(-?\\d+)>")

    data class Vector(val x: Int, val y: Int, val z: Int) : Comparable<Vector> {
        operator fun plus(other: Vector) = Vector(x + other.x, y + other.y, z + other.z)
        operator fun minus(other: Vector) = Vector(x - other.x, y - other.y, z - other.z)
        operator fun times(other: Vector) = Vector(x * other.x, y * other.y, z * other.z)
        operator fun div(other: Vector) = Vector(x / other.x, y / other.y, z / other.z)
        operator fun unaryMinus() = Vector(-x, -y, -z)

        override fun compareTo(other: Vector): Int {
            return this.x + this.y + this.z - other.x - other.y - other.z
        }
    }

    fun Vector.energy() : Int = abs(x) + abs(y) + abs(z)

    fun Vector.direction(other: Vector) : Vector {
        val x =
                when {
                    this.x > other.x -> -1
                    this.x < other.x -> 1
                    else -> 0
                }
        val y =
                when {
                    this.y > other.y -> -1
                    this.y < other.y -> 1
                    else -> 0
                }
        val z =
                when {
                    this.z > other.z -> -1
                    this.z < other.z -> 1
                    else -> 0
                }

        return Vector(x, y, z)
    }

    val input = adventOfCode.getInput(2019, day)
            .lines()
            .asSequence()
            .mapNotNull { inputRegex.find(it) }
            .map { it.groups.drop(1).mapNotNull { it?.value?.toInt() } }
            .filter { it.size == 3 }
            .map { Vector(it[0], it[1], it[2]) }
            .toList()

    data class Moon(val position: Vector, val velocity: Vector) {

    }

    override fun part1() : Any {
        var cur = input.toMutableList().map { Pair(it, Vector(0,0,0)) }.toMap().toMutableMap()

        repeat(1000) {
            cur.keys.combinations(2).forEach { (first, second) ->
                val firstDir = first.direction(second)

                cur[first] = cur[first]!! + firstDir
                cur[second] = cur[second]!! - firstDir
            }

            /*cur.forEach { pos, vec ->
                println("Pos: $pos, Vec: $vec")
            }*/

            cur = cur.mapKeys { (key, value) -> key + value }.toMutableMap()
        }

        return cur.map { (key, value) -> key.energy() * value.energy() }.sum()
    }

    override fun part2() : Any {
        val startingPos = input.toMutableList().map { Pair(it, Vector(0,0,0)) }.toMap()

        println(startingPos)

        var flag = true

        var cur = startingPos.toMutableMap()

        val count = whileCount({ startingPos != cur || flag }) {
            flag = false

            cur.keys.combinations(2).forEach { (first, second) ->
                val firstDir = first.direction(second)

                cur[first] = cur[first]!! + firstDir
                cur[second] = cur[second]!! - firstDir
            }

            /*cur.forEach { pos, vec ->
                println("Pos: $pos, Vec: $vec")
            }*/

            cur = cur.mapKeys { (key, value) -> key + value }.toMutableMap()
        }

        println(cur)

        return count
    }
}
