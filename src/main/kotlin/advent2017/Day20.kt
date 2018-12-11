package advent2017

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*
import kotlin.math.abs

class Day20(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 20
    private val regex =
            Regex("p?<(-?\\d+),(-?\\d+),(-?\\d+)>, v=<(-?\\d+),(-?\\d+),(-?\\d+)>, a=<(-?\\d+),(-?\\d+),(-?\\d+)>")
    private val input = adventOfCode.getInput(2017, day).lines().map {line ->
        val match = regex.find(line) ?: throw IllegalStateException("Something went wrong!")
        val vecs = match.groups.drop(1).chunked(3).map {it.requireNoNulls()}.map {it.map {it.value.toLong()}}
                .map {
                    val vector = Vector<Long>(3)
                    it.forEach {
                        vector.add(it)
                    }
                    vector
                }
        Particle(vecs[0], vecs[1], vecs[2])
    }

    private data class Particle(val position: Vector<Long>, val velocity: Vector<Long>, val acceleration: Vector<Long>) {
        fun totalAcceleration() = abs(acceleration[0]) + abs(acceleration[1]) + abs(acceleration[2])
        fun totalVelocity() = abs(velocity[0]) + abs(velocity[1]) + abs(velocity[2])
        fun distanceFromOrigin() = abs(position[0]) + abs(position[1]) + abs(position[2])
        fun tick() {
            velocity[0] += acceleration[0]
            velocity[1] += acceleration[1]
            velocity[2] += acceleration[2]

            position[0] += velocity[0]
            position[1] += velocity[1]
            position[2] += velocity[2]
        }
    }

    override fun part1(): String {
        val accGroups = input.withIndex().groupBy {(_, particle) -> particle.totalAcceleration()}
        val minAcc = accGroups.keys.min() ?: NoSuchElementException("There were no particles?")
        val velGroups = accGroups[minAcc]!!.groupBy {(_, particle) -> particle.totalVelocity()}
        val minVel = velGroups.keys.min() ?: NoSuchElementException("WTF?")
        return velGroups[minVel]!!.minBy {(_, particle) -> particle.distanceFromOrigin()}?.index.toString()
    }

    override fun part2(): String {
        var cur = input
        repeat(100) {
            cur = cur.withOutCollisions()
            cur.forEach(Particle::tick)
            //println(cur.size)
        }
        return cur.size.toString()
    }

    private fun List<Particle>.withOutCollisions(): List<Particle> {
        val out = mutableListOf<Particle>()
        this.forEach { comp ->
            if (this.filter {it != comp}.none {it.position == comp.position}) {
                out.add(comp)
            }
        }
        return out
    }

}