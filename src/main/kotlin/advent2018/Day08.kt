package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*

class Day08(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 8
    private val input = adventOfCode.getInput(2018, day).extractLongs()

    class Node(val children: MutableList<Node> = mutableListOf(), val metadata: MutableList<Long> = mutableListOf())

    fun parseInput(input: LongArray) = parseChildren(input, 0).first

    fun parseChildren(input: LongArray, initialPos: Int) : Pair<Node, Int> {
        var pos = initialPos

        val node = Node()

        var numOfChildren = input[pos++]
        var numOfMetadata = input[pos++]

        while (numOfChildren-- > 0) {
            val (child, newPos) = parseChildren(input, pos)
            pos = newPos
            node.children.add(child)
        }

        while (numOfMetadata-- > 0) {
            node.metadata.add(input[pos++])
        }

        return node to pos
    }

    override fun part1(): String {
        val tree = parseInput(input)

        val stack = Stack<Node>()
        stack.push(tree)

        var out = 0L

        while (stack.isNotEmpty()) {
            val cur = stack.pop()
            out += cur.metadata.sum()
            cur.children.forEach { stack.push(it) }
        }

        return "" + out
    }

    override fun part2(): String {
        val tree = parseInput(input)

        val stack = Stack<Node>()
        stack.push(tree)

        var out = 0L

        while (stack.isNotEmpty()) {
            val cur = stack.pop()
            if (cur.children.isEmpty()) {
                out += cur.metadata.sum()
            } else {
                cur.metadata
                        .map { m -> (m-1).toInt() }
                        .filter { index -> index < cur.children.size }
                        .forEach { index -> stack.push(cur.children[index]) }
            }
        }

        return "" + out
    }
}