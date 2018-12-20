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

    private fun walkCharacterList(pos: Room, toWalk: String) {
        //println(toWalk)
        var curPos = pos
        var i = 0
        loop@while(i < toWalk.length) {
            when(toWalk[i++]) {
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
        }

        if (i < toWalk.length) {
            val groupEnd = toWalk.findCloseBracket(--i)
            val group = toWalk.substring(i+1..groupEnd-1)

            val pipeLocations = group.findAllPipes()

            val ways = mutableListOf<String>()

            var cur = 0
            for (pipe in pipeLocations) {
                ways.add(group.substring(cur until pipe))
                cur = pipe+1
            }

            ways.add(group.substring(cur))

            val rest = toWalk.substring(groupEnd+1)

            for (way in ways) {
                walkCharacterList(curPos, way + rest)
            }
        }
    }

    private fun String.findAllPipes() : List<Int> {
        var openParens = 0
        val out = mutableListOf<Int>()

        for ((i, c) in this.withIndex()) {
            when(c) {
                '(' -> openParens++
                ')' -> openParens--
                '|' -> if (openParens == 0) out.add(i)
            }
        }

        return out
    }

    private fun String.findCloseBracket(startIndex : Int = 0) : Int {
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

        walkCharacterList(start, input)

        val stack = Stack<Pair<Set<Room>, Room>>()

        stack.push(setOf<Room>() to start)
        var max = 0
        while(stack.isNotEmpty()) {
            val (seen, cur) = stack.pop()
            val withThis = seen + setOf(cur)
            if (cur.north != null) {
                stack.push(withThis to cur.north!!)
            }
            if (cur.south != null) {
                stack.push(withThis to cur.south!!)
            }
            if (cur.east != null) {
                stack.push(withThis to cur.east!!)
            }
            if (cur.west != null) {
                stack.push(withThis to cur.west!!)
            }

            if (withThis.size-1 > max)
                max = withThis.size-1
        }

        return "$max"
    }

    override fun part2(): String {

        return ""
    }


}