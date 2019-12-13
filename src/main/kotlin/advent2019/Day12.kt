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

    data class MutableVector(var x: Int, var y: Int, var z: Int) : Comparable<Vector> {
        fun add(other: MutableVector) {
            x += other.x
            y += other.y
            z += other.z
        }

        fun sub(other: MutableVector) {
            x -= other.x
            y -= other.y
            z -= other.z
        }

        override fun compareTo(other: Vector): Int {
            return this.x + this.y + this.z - other.x - other.y - other.z
        }
    }
    fun Vector.energy() : Int = abs(x) + abs(y) + abs(z)

    fun Vector.direction(other: Vector) : Vector {

        val x = Integer.signum(other.x - this.x)
        val y = Integer.signum(other.y - this.y)
        val z = Integer.signum(other.z - this.z)

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

    data class Moon(val position: Vector, val velocity: Vector)

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
        val offsets = MutableList(input.size) { Vector(0, 0, 0) }
        val velocities = MutableList(input.size) { Vector(0, 0, 0) }

        val indexCombinations = (input.indices).toList().combinations(2)

        var xRepeat = 0L
        var yRepeat = 0L
        var zRepeat = 0L

        outer@for (i in 1L..Long.MAX_VALUE) {
            indexCombinations.forEach { (first, second) ->
                val pos1 = input[first] + offsets[first]
                val pos2 = input[second] + offsets[second]

                val firstDir = pos1.direction(pos2)

                velocities[first] += firstDir
                velocities[second] -= firstDir
            }
            //println("-------------------------------------")
            for (j in 0 until offsets.size) {
                //print("pos=${input[i] + offsets[i]} vel=${-velocities[i]}\t")
                offsets[j] += velocities[j]

                //print("pos=${input[j] + offsets[j]} vel=${velocities[j]}\n")

                if (xRepeat == 0L && velocities.all { it.x == 0 } && offsets.all { it.x == 0 }) {
                    xRepeat = i
                }

                if (yRepeat == 0L && velocities.all { it.y == 0 } && offsets.all { it.y == 0 } ) {
                    yRepeat = i
                }

                if (zRepeat == 0L && velocities.all { it.z == 0 } && offsets.all { it.z == 0 }) {
                    zRepeat = i
                }

                if (zRepeat != 0L && yRepeat != 0L && zRepeat != 0L) {
                    println("xrepeat: $xRepeat, yrepeat: $yRepeat, zrepeat: $zRepeat")
                    println("$xRepeat $yRepeat $zRepeat")
                    break@outer
                }
            }
        }

        return ""
    }
}
