package advent2018

import advent2017.Day08
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
        var curPos = pos
        var i = 0
        loop@while(i < toWalk.length) {
            when(toWalk[i++]) {
                'E' -> {
                    if (curPos.east == null)
                        curPos.east = Room()
                    curPos.east!!.west = curPos
                    curPos = curPos.east!!
                }
                'N' -> {
                    if (curPos.north == null)
                        curPos.north = Room()
                    curPos.north!!.south = curPos
                    curPos = curPos.north!!
                }
                'S' -> {
                    if (curPos.south == null)
                        curPos.south = Room()
                    curPos.south!!.north = curPos
                    curPos = curPos.south!!
                }
                'W' -> {
                    if (curPos.west == null)
                        curPos.west = Room()
                    curPos.west!!.east = curPos
                    curPos = curPos.west!!
                }
                '(' -> {
                    break@loop
                }
            }
        }

        if (i < toWalk.length) {
            val groupEnd = toWalk.findCloseBracket(--i)

            val ways = mutableListOf<String>()

            toWalk.substring(i+1..groupEnd-1).let { group ->
                val pipeLocations = group.findAllPipes()


                var cur = 0
                for (pipe in pipeLocations) {
                    ways.add(group.substring(cur until pipe))
                    cur = pipe+1
                }
                ways.add(group.substring(cur))
            }

            val rest = toWalk.substring(groupEnd+1)
            if (ways.any { it.isEmpty() }) {
                for (way in ways) {
                    walkCharacterList(curPos, way)
                }
                walkCharacterList(curPos, rest)
            } else {
                for (way in ways) {
                    walkCharacterList(curPos, way + rest)
                }
            }

        }
    }

    private fun String.findAllPipes() : List<Int> {
        var openParents = 0
        val out = mutableListOf<Int>()

        for ((i, c) in this.withIndex()) {
            when(c) {
                '(' -> openParents++
                ')' -> openParents--
                '|' -> if (openParents == 0) out.add(i)
            }
        }

        return out
    }

    private fun String.findCloseBracket(startIndex : Int = 0) : Int {
        var openParents = 0
        var pos = startIndex

        do {
            when (this[pos++]) {
                '(' -> openParents++
                ')' -> openParents--
            }
        } while (openParents > 0)

        return --pos
    }

    private fun getDistanceMap(input : String) : Map<Room, Int> {
        val start = Room()

        walkCharacterList(start, input)

        val queue = ArrayDeque<Pair<Set<Room>, Room>>()

        queue.add(setOf<Room>() to start)

        val results = Day08.NeverNullMap<Room, Int> { 0 }

        while(queue.isNotEmpty()) {
            val (seen, cur) = queue.remove()

            if (cur in seen)
                continue

            results[cur] = seen.size

            val withThis = seen + setOf(cur)

            if (cur.north != null) {
                queue.add(withThis to cur.north!!)
            }
            if (cur.south != null) {
                queue.add(withThis to cur.south!!)
            }
            if (cur.east != null) {
                queue.add(withThis to cur.east!!)
            }
            if (cur.west != null) {
                queue.add(withThis to cur.west!!)
            }
        }

        return results
    }

    override fun part1(): Any = getDistanceMap(input).maxBy { (_, steps) -> steps }!!.value.toString()


    override fun part2(): Any = getDistanceMap(input).count { (_, steps) -> steps >= 1000 }.toString()
}