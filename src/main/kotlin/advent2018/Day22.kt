package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException
import java.math.BigInteger
import java.util.*

class Day22(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 22
    private val input = adventOfCode.getInput(2018, 22).lines().map { it.extractInts() }

    private data class Coord(val x: Int, val y: Int) {
        fun up() = Coord(x, y-1)
        fun down() = Coord(x, y+1)
        fun left() = Coord(x-1, y)
        fun right() = Coord(x+1, y)
        fun adjecent() = listOf(
                up(),
                down(),
                left(),
                right()
        )

        fun isValid() = this.x >= 0 && this.y >= 0
    }

    private val erosionMap = mutableMapOf<Coord, BigInteger>()

    private fun Coord.geologicIndex(depth: Int, goal: Coord) : BigInteger {
        val out = if (this.x == 0 && this.y == 0) {
            BigInteger.ZERO
        } else if (this == goal) {
            BigInteger.ZERO
        } else if (this.y == 0) {
            BigInteger.valueOf(this.x * 16807L)
        } else if (this.x == 0) {
            BigInteger.valueOf(this.y * 48271L)
        } else {
            this.up().erosionLevel(depth, goal) * this.left().erosionLevel(depth, goal)
        }

        return out
    }

    private fun Coord.erosionLevel(depth: Int, goal: Coord) : BigInteger {
        erosionMap[this]?.let { out ->
            return out
        }

        val out = ((BigInteger.valueOf(depth.toLong()) + this.geologicIndex(depth, goal)) % BigInteger.valueOf(20183))

        erosionMap[this] = out

        return out
    }

    private fun Coord.regionType(depth: Int, goal: Coord) : CaveType {
        val erosionLevel = ((BigInteger.valueOf(depth.toLong()) + this.geologicIndex(depth, goal)) % BigInteger.valueOf(20183)).toLong() % 3
        return when (erosionLevel) {
            0L -> CaveType.ROCKY
            1L -> CaveType.WET
            2L -> CaveType.NARROW
            else -> throw IllegalStateException("This can not happen! $erosionLevel $this $depth $goal")
        }
    }

    override fun part1(): String {
        val depth = input[0][0]
        val target = Coord(input[1][0], input[1][1])
        var out = 0L

        for (y in 0..target.y) {
            for (x in 0..target.x) {
                val curType = Coord(x, y).regionType(depth, target)
                /*if ((x == 0 && y == 0) || target.y == y && target.x == x) {
                    print('X')
                } else {
                    when (curType) {
                        CaveType.ROCKY -> print('.')
                        CaveType.WET -> print('=')
                        CaveType.NARROW -> print('|')
                    }
                }*/

                out += when (curType) {
                    CaveType.ROCKY -> 0
                    CaveType.WET -> 1
                    CaveType.NARROW -> 2
                }
            }
            //print('\n')
        }

        return "" + out
    }

    enum class Tool {
        TORCH, CLIMBING_GEAR, NOTHING
    }

    enum class CaveType(val validTools : List<Tool>) {
        ROCKY(listOf(Tool.CLIMBING_GEAR, Tool.TORCH)),
        WET(listOf(Tool.CLIMBING_GEAR, Tool.NOTHING)),
        NARROW(listOf(Tool.NOTHING, Tool.TORCH))
    }

    private data class CurrentSituation(val pos : Coord, val cost : Int, val tool : Tool)

    override fun part2(): String {
        val depth = 510//input[0][0]
        val target = Coord(10, 10)//Coord(input[1][0], input[1][1])

        val queue = ArrayDeque<CurrentSituation>()
        val seen = mutableMapOf<Coord, Int>()

        queue.add(CurrentSituation(Coord(0, 0), 0, Tool.TORCH))
        var minCost = Int.MAX_VALUE
        while (queue.isNotEmpty()) {
            val cur = queue.poll()
            if (cur.cost >= minCost)
                continue

            if (cur.pos == target) {
                if (cur.tool == Tool.TORCH) {
                    if (cur.cost < minCost)
                        minCost = cur.cost
                } else {
                    if (cur.cost + 7 < minCost)
                        minCost = cur.cost + 7
                }
                continue
            }

            if (cur.pos in seen) {
                if (cur.cost > seen[cur.pos]!!)
                    continue
                if (seen[cur.pos]!! > cur.cost)
                    seen[cur.pos] = cur.cost
            } else {
                seen[cur.pos] = cur.cost
            }

            cur.pos.adjecent()
                    .filter(Coord::isValid)
                    .forEach { adj ->
                        val regionType = adj.regionType(depth, target)
                        if (cur.tool in regionType.validTools) {
                            queue.add(CurrentSituation(adj, cur.cost + 1, cur.tool))
                        } else {
                            queue.add(CurrentSituation(adj, cur.cost + 1 + 7, cur.pos.regionType(depth, target).validTools.intersect(regionType.validTools).single()))
                        }
                    }
        }

        return "" + minCost
    }
}