package advent2017

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Queue
import xyz.usbpc.utils.whileCount

class Day12(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 12
    private val currentProgram = Regex("(\\d+) <->")
    private val connectedPrograms = Regex("(\\d+)\\b(?! <->)")
    private val input: Map<Int, Set<Int>> =
            adventOfCode.getInput(2017, 12).lines().map {line ->
                (currentProgram.find(line)?.groups?.last()?.value?.toInt() ?:
                        throw IllegalStateException("Capture group for currentProgram not found!")) to
                        connectedPrograms.findAll(line).map {
                            it.groups.last()?.value?.toInt() ?:
                                    throw IllegalStateException("Capture group for children not found!")
                        }.toMutableSet()
            }.toMap()

    override fun part1(): Any = getConnected(input, 0).size.toString()
    override fun part2(): Any =
            input.keys.toMutableSet().let {
                whileCount(it::isNotEmpty) {
                    val current = it.first()
                    it.remove(current)
                    it.removeAll(getConnected(input, current))
                }
            }.toString()

    private fun getConnected(graph: Map<Int, Set<Int>>, startVertex: Int): Set<Int> {
        val queue = Queue<Int>().apply {add(startVertex)}
        val seen = mutableSetOf<Int>().apply {add(startVertex)}
        while (queue.isNotEmpty()) {
            val current = queue.remove()
            val children = graph[current] ?: throw IllegalStateException("$current vertex dosen't exist!")
            children.forEach {child ->
                if (seen.add(child)) {
                    queue.add(child)
                }
            }
        }
        return seen
    }
}
