package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*

class Day20(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 20

    private val input = adventOfCode.getInput(2018, day)

    private data class Room(val id : Int = getNextId()) {
        var north : Room? = null
        var east : Room? = null
        var south : Room? = null
        var west : Room? = null

        companion object {
            var id = 0
            private fun getNextId() = id++
        }
    }

    private fun walkCharacterList(pos: Room, input: List<Char>) {
        val stack = Stack<Pair<Room, IndexInfo>>()
        stack.push(pos to IndexInfo(0, input.lastIndex, input.size))
        while (stack.isNotEmpty()) {
            var (curPos, toWalk) = stack.pop()
            //println(toWalk)
            var i = toWalk.start
            loop@while(i < input.size) {
                when(input[i++]) {
                    'E' -> {
                        if (curPos.east == null)
                            curPos.east = Room()
                        curPos = curPos.east!!
                    }
                    'N' -> {
                        if (curPos.north == null)
                            curPos.north = Room()
                        curPos = curPos.north!!
                    }
                    'S' -> {
                        if (curPos.south == null)
                            curPos.south = Room()
                        curPos = curPos.south!!
                    }
                    'W' -> {
                        if (curPos.west == null)
                            curPos.west = Room()
                        curPos = curPos.west!!
                    }
                    '(' -> {
                        break@loop
                    }
                }
                if (i > toWalk.end && i < toWalk.skipTo)
                    i = toWalk.skipTo
            }

            if (i < input.size) {
                val groupEnd = input.findCloseBracket(--i)

                val pipeLocations = listOf(i+1) + input.findAllPipes(i+1, groupEnd-1) + listOf(groupEnd-1)

                for (way in pipeLocations.windowed(2, 1)) {
                    stack.push(curPos to IndexInfo(way[0], way[1], groupEnd+1))
                }
            }
        }
    }

    data class IndexInfo(val start: Int, val end: Int, val skipTo: Int)

    private fun List<Char>.findAllPipes(start: Int, end: Int) : List<Int> {
        var openParens = 0
        val out = mutableListOf<Int>()

        var i = start

        while (i <= end) {
            val c = this[i++]
            when(c) {
                '(' -> openParens++
                ')' -> openParens--
                '|' -> if (openParens == 0) out.add(i)
            }
        }

        return out
    }

    private fun List<Char>.findCloseBracket(startIndex : Int = 0) : Int {
        var openParens = 0
        var pos = startIndex

        do {
            when (this[pos++]) {
                '(' -> openParens++
                ')' -> openParens--
            }
        } while (openParens > 0)

        return --pos
    }

    override fun part1(): String {
        val start = Room()
        walkCharacterList(start, input.toCharArray().toList())

        return ""
    }

    override fun part2(): String {

        return ""
    }

}