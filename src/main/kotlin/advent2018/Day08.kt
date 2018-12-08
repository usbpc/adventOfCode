package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*

class Day08(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 8
    private val input = adventOfCode.getInput(2018, day).extractInts()

    class Node(val children: MutableList<Node> = mutableListOf(), val metadata: MutableList<Int> = mutableListOf(), val childNum : Int = 0, val metadataNum : Int = 0)

    fun parseInput(input: IntArray) = parseChildrenRecursive(input, 0).first

    fun parseChildrenRecursive(input: IntArray, initialPos: Int) : Pair<Node, Int> {
        var pos = initialPos

        val node = Node()

        var numOfChildren = input[pos++]
        var numOfMetadata = input[pos++]

        while (numOfChildren-- > 0) {
            val (child, newPos) = parseChildrenRecursive(input, pos)
            pos = newPos
            node.children.add(child)
        }

        while (numOfMetadata-- > 0) {
            node.metadata.add(input[pos++])
        }

        return node to pos
    }

    fun parse(input: IntArray) : Node {
        var pos = 0
        val stack = Stack<Node>()
        var node : Node? = null
        do {
            node = if (node == null)
                Node(childNum = input[pos++], metadataNum = input[pos++])
            else
                stack.pop()

            if (node!!.children.size < node.childNum) {
                stack.push(node)
                node = null
                continue
            } else if (stack.isNotEmpty()) {
                stack.peek().children.add(node)
            }

            repeat(node.metadataNum) {
                node.metadata.add(input[pos++])
            }
        } while (stack.isNotEmpty())

        return node!!
    }

    override fun part1(): String {
        val tree = parse(input)

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
                        .map { m -> m-1 }
                        .filter { index -> index < cur.children.size }
                        .forEach { index -> stack.push(cur.children[index]) }
            }
        }

        return "" + out
    }
}