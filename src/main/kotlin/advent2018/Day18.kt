package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException
import java.lang.StringBuilder

class Day18(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 18

    private enum class LumberArea {
        OPEN_GROUND,
        TREE,
        LUMBERYARD;

        companion object {
            fun fromChar(c : Char) =
                    when(c) {
                        '.' -> OPEN_GROUND
                        '|' -> TREE
                        '#' -> LUMBERYARD
                        else -> throw IllegalStateException("Unknown symbol in input")
                    }
        }
    }

    private val input = adventOfCode.getInput(2018, day).lines()
            .map { line ->
                line.map { c ->
                    LumberArea.fromChar(c)
                }
            }

    private fun List<List<LumberArea>>.prettyString() : String {
        val out = StringBuilder()

        for (line in this) {
            for (acre in line) {
                when (acre) {
                    LumberArea.OPEN_GROUND -> out.append('.')
                    LumberArea.TREE -> out.append('|')
                    LumberArea.LUMBERYARD -> out.append('#')
                }
            }
            out.append('\n')
        }

        return out.toString()
    }

    private fun List<List<LumberArea>>.getSurrounding(curY : Int, curX : Int) : List<LumberArea> {
        val out = mutableListOf<LumberArea>()
        for (y in curY-1..curY+1) {
            if (y < 0 || y > this.lastIndex)
                continue
            for (x in curX-1..curX+1) {
                if (x < 0 || x > this.first().lastIndex )
                    continue
                if (x == curX && y == curY)
                    continue
                out.add(this[y][x])
            }
        }
        return out
    }

    private fun List<List<LumberArea>>.getNextIteration() : List<List<LumberArea>> {
        return List(this.size) { y ->
            List(this[y].size) { x ->
                val surrounding = this.getSurrounding(y, x)
                when (this[y][x]) {
                    LumberArea.OPEN_GROUND ->
                        if (surrounding.count { sq -> sq == LumberArea.TREE } >= 3)
                            LumberArea.TREE
                        else
                            LumberArea.OPEN_GROUND
                    LumberArea.TREE ->
                        if (surrounding.count { sq -> sq == LumberArea.LUMBERYARD } >= 3)
                            LumberArea.LUMBERYARD
                        else
                            LumberArea.TREE
                    LumberArea.LUMBERYARD ->
                        if (surrounding.contains(LumberArea.LUMBERYARD) && surrounding.contains(LumberArea.TREE))
                            LumberArea.LUMBERYARD
                        else
                            LumberArea.OPEN_GROUND
                }
            }
        }
    }

    override fun part1(): String {
        var curState = input

        repeat(10) {
            curState = curState.getNextIteration()
        }

        return (curState.flatten().count { sq -> sq == LumberArea.LUMBERYARD } *  curState.flatten().count { sq -> sq == LumberArea.TREE }).toString()
    }

    override fun part2(): String {
        var curState = input
        val previousStates : MutableMap<List<List<LumberArea>>, Int> = mutableMapOf()

        for (i in 1..1000000000) {
            val newState = curState.getNextIteration()

            if (newState in previousStates) {
                val index = previousStates[newState]!!
                val cycleSize = previousStates.size - index

                val finalIndex = ((1000000000 - i) % cycleSize) + index

                val finalState = previousStates.filter { (_, i) -> i == finalIndex }.keys.single()

                return (finalState.flatten().count { sq -> sq == LumberArea.LUMBERYARD } *  finalState.flatten().count { sq -> sq == LumberArea.TREE }).toString()
            }

            previousStates[newState] = i

            curState = newState
        }

        return (curState.flatten().count { sq -> sq == LumberArea.LUMBERYARD } *  curState.flatten().count { sq -> sq == LumberArea.TREE }).toString()
    }
}