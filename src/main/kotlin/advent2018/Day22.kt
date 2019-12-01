package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException
import java.util.*
import kotlin.Comparator
import kotlin.math.abs

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

        fun distanceTo(other: Coord) = abs(y - other.y) + abs(x - other.x)

        fun isValid() = this.x >= 0 && this.y >= 0
    }

    private fun Coord.geologicIndex(depth: Int, goal: Coord, erosionMap: MutableMap<Coord, Long>) : Long {
        return if (x == 0 && y == 0) {
            0L
        } else if (this == goal) {
            0L
        } else if (y == 0) {
            x * 16807L
        } else if (x == 0) {
            y * 48271L
        } else {
            up().erosionLevel(depth, goal, erosionMap) * left().erosionLevel(depth, goal, erosionMap)
        }
    }

    private fun Coord.erosionLevel(depth: Int, goal: Coord, erosionMap: MutableMap<Coord, Long>) : Long {
        erosionMap[this]?.let { out ->
            return out
        }

        val out = (depth + this.geologicIndex(depth, goal, erosionMap)) % 20183

        erosionMap[this] = out

        return out
    }

    private fun Coord.regionType(depth: Int, goal: Coord, erosionMap: MutableMap<Coord, Long>) : CaveType {
        return when (erosionLevel(depth, goal, erosionMap) % 3) {
            0L -> CaveType.ROCKY
            1L -> CaveType.WET
            2L -> CaveType.NARROW
            else -> throw IllegalStateException("This can not happen!")
        }
    }

    override fun part1(): Any {
        val depth = input[0][0]
        val target = Coord(input[1][0], input[1][1])
        var out = 0L

        val erosionMap = mutableMapOf<Coord, Long>()

        for (y in 0..target.y) {
            for (x in 0..target.x) {
                val curType = Coord(x, y).regionType(depth, target, erosionMap)
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

    private data class CurrentSituation(val pos : Coord, val tool : Tool)
    private data class CurrentSituationWH(val pos : Coord, val cost : Int, val tool : Tool, val history : List<CurrentSituation>) {
        //fun withoutHistory() = CurrentSituation(pos, cost, tool)
    }

    private data class QueueData(val curSit: CurrentSituation, val distance: Int)

    override fun part2(): Any {
        val depth = input[0][0]
        val target = Coord(input[1][0], input[1][1])

        val targetSit = CurrentSituation(target, Tool.TORCH)

        val visited = mutableSetOf<CurrentSituation>()
        val toVisit = PriorityQueue<QueueData>(11, Comparator<QueueData> { first, second ->
            (first.distance + first.curSit.pos.distanceTo(target)) - (second.distance + second.curSit.pos.distanceTo(target))
        })

        toVisit.add(QueueData(CurrentSituation(Coord(0, 0), Tool.TORCH), 0))


        val erosionMap = mutableMapOf<Coord, Long>()

        while (true) {
            val (curSit, distance) = toVisit.poll()
            if (curSit in visited)
                continue
            if (curSit == targetSit)
                return distance.toString()
            visited.add(curSit)

            val curRegionType = curSit.pos.regionType(depth, target, erosionMap)

            CurrentSituation(curSit.pos, curRegionType.validTools.single { it != curSit.tool }).let { newSit ->
                toVisit.add(QueueData(newSit, distance + 7))
            }

            curSit.pos.adjecent()
                    .filter(Coord::isValid)
                    .forEach { adj ->
                        val adjRegionType = adj.regionType(depth, target, erosionMap)

                        if (curSit.tool in adjRegionType.validTools) {
                            CurrentSituation(adj, curSit.tool).let { newSit ->
                                toVisit.add(QueueData(newSit, distance + 1))
                            }
                        }
                    }


        }
    }
}