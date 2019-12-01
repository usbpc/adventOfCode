package advent2017

import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day

class Day07(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 7
    data class Program(val name: String, var weight: Int = -1, val children: MutableList<Program>) {
        val subTowerWeight: Int
            get() = weight + children.sumBy {it.subTowerWeight}
        val subTowerSize: Int
            get() {
                var counter = 0
                var crr = children.firstOrNull()
                while (crr != null) {
                    counter++
                    crr = crr.children.firstOrNull()
                }
                return counter
            }
    }

    var part1: String? = null
    var part2: Int? = null
    override fun part1(): Any {
        if (part1 == null) {
            solve()
        }
        return part1.toString()
    }

    override fun part2(): Any {
        if (part2 == null) {
            solve()
        }
        return part2.toString()
    }

    private fun solve() {
        val programMap = mutableMapOf<String, Program>()
        adventOfCode.getInput(2017, 7).lines().forEach {line ->
            val tokens = line.split(' ')
            val childList = mutableListOf<Program>()
            if (tokens.size > 3) {
                tokens.drop(3).forEach {dirtyChild ->
                    //Remove those annoying comma
                    val child = dirtyChild.filter {it != ','}
                    childList.add(
                            programMap.getOrPut(child) {
                                Program(child, children = mutableListOf())
                            }
                    )
                }
            }
            val weight = tokens[1].drop(1).dropLast(1).toInt()

            programMap.getOrPut(tokens[0]) {
                Program(tokens[0], children = mutableListOf())
            }.apply {
                this.children.addAll(childList)
                this.weight = weight
            }
        }
        val nodesWithParents = programMap.flatMap {it.value.children}.map {it.name}
        part1 = programMap.values.single {it.name !in nodesWithParents}.name

        programMap.values.filter {program ->
            program.children.map {it.subTowerWeight}.let {
                it.max() != it.min()
            }
        }.minBy {it.subTowerSize}?.let {minimum ->
            val unique = minimum.children.first {weight ->
                minimum.children.map {it.subTowerWeight}.filter {it == weight.subTowerWeight}.count() == 1
            }
            val common = minimum.children.first {it.subTowerWeight != unique.subTowerWeight}
            val diff = unique.subTowerWeight - common.subTowerWeight
            part2 = unique.weight - diff
        } ?: throw IllegalStateException("There was no element in there?!")
    }
}
