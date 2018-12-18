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
        val newState = mutableListOf<List<LumberArea>>()
        this.forEachIndexed { y, line ->
            val lineList = mutableListOf<LumberArea>()
            line.forEachIndexed { x, acre ->
                val surrounding = this.getSurrounding(y, x)

                when (acre) {
                    LumberArea.OPEN_GROUND -> {
                        if (surrounding.count { sq -> sq == LumberArea.TREE } >= 3) {
                            LumberArea.TREE
                        } else {
                            LumberArea.OPEN_GROUND
                        }
                    }
                    LumberArea.TREE -> {
                        if (surrounding.count { sq -> sq == LumberArea.LUMBERYARD } >= 3) {
                            LumberArea.LUMBERYARD
                        } else {
                            LumberArea.TREE
                        }
                    }
                    LumberArea.LUMBERYARD -> {
                        if (surrounding.contains(LumberArea.LUMBERYARD) && surrounding.contains(LumberArea.TREE)) {
                            LumberArea.LUMBERYARD
                        } else {
                            LumberArea.OPEN_GROUND
                        }
                    }
                }.let { new ->
                    lineList.add(new)
                }
            }
            newState.add(lineList)
        }
        return newState
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
        val previousStates : MutableList<List<List<LumberArea>>> = mutableListOf()

        for (i in 1..1000000000) {
            val newState = curState.getNextIteration()

            if (newState in previousStates) {
                val index = previousStates.indexOf(newState)
                val cycleSize = previousStates.size - index

                val finalState = previousStates[((1000000000 - i) % cycleSize) + index]

                return (finalState.flatten().count { sq -> sq == LumberArea.LUMBERYARD } *  finalState.flatten().count { sq -> sq == LumberArea.TREE }).toString()
            }

            previousStates.add(newState)

            curState = newState
        }

        return (curState.flatten().count { sq -> sq == LumberArea.LUMBERYARD } *  curState.flatten().count { sq -> sq == LumberArea.TREE }).toString()
    }
}