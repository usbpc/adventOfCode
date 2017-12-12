import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.collect

class Day12(override val adventOfCode: AdventOfCode) : Day {

    private class Queue<E> : Collection<E> {
        private val backing: MutableList<E> = mutableListOf()
        override val size: Int
            get() = backing.size

        override fun contains(element: E): Boolean {
            return backing.contains(element)
        }

        override fun containsAll(elements: Collection<E>): Boolean {
            return backing.containsAll(elements)
        }

        override fun iterator(): Iterator<E> {
            return backing.iterator()
        }

        fun queue(obj: E) = backing.add(obj)

        fun dequeue(): E = backing.removeAt(0)

        override fun isEmpty(): Boolean = backing.isEmpty()
    }

    override val day: Int = 12
    private val input = adventOfCode.getInput(2017, 12)
    private var part1: String? = null
    private var part2: String? = null
    override fun part1(): String {
        if (part1 == null) solve()
        return part1.toString()
    }

    override fun part2(): String {
        if (part2 == null) solve()
        return part2.toString()
    }

    private fun solve() {
        var programs = mutableMapOf<Int, MutableSet<Int>>()
        var usableInput = input.lines().map {it.split("<->")}

        usableInput.forEach {line ->
            val first = line.first().removeSuffix(" ").toInt()
            val seconds = line[1].split(", ").map {it.removePrefix(" ").toInt()}
            programs.put(first, seconds.toMutableSet())
        }

        var counter = 0
        val programNames = programs.keys.toMutableSet()
        while (programNames.isNotEmpty()) {
            counter++
            val current = programNames.first()
            programNames.remove(current)
            programNames.removeAll(getConnected(programs, current))
        }

        part1 = getConnected(programs, 0).size.toString()
        part2 = counter.toString()
    }

    private fun getConnected(graph: Map<Int, Set<Int>>, startVertex: Int): Set<Int> {
        val queue = Queue<Int>().apply {queue(startVertex)}
        val seen = mutableSetOf<Int>().apply {add(startVertex)}
        while (queue.isNotEmpty()) {
            val current = queue.dequeue()
            val children = graph[current] ?: throw IllegalStateException("$current vertex doesn't exist!")
            children.forEach { child ->
                if (seen.add(child)) {
                    queue.queue(child)
                }
            }
        }
        return seen
    }
}