package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day12(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 12
    private val input = adventOfCode.getInput(2018, day)

    private val initialStateRegex = """initial state: ([.#]{100})""".toRegex()
    private val transistionRegex = """([.#]{5}) => ([.#])""".toRegex()

    data class Transition(val from: BooleanArray, val to : Boolean)

    override fun part1(): String {
        val startState = initialStateRegex.find(input)!!.groups[1]!!.value.map { it == '#' }.toBooleanArray()
        var transitions = transistionRegex.findAll(input).map { result -> Transition(result.groups[1]!!.value.map { it == '#' }.toBooleanArray(), (result.groups[2]!!.value[0] == '#')) }
        val playfieldSize = 500
        val zeroIndex = playfieldSize/2 - startState.size/2
        var currentState = BooleanArray(playfieldSize)

        for (i in zeroIndex until zeroIndex+startState.size) {
            currentState[i] = startState[i-zeroIndex]
        }

        var previous = listOf<Boolean>()
        var iterations = 0
        while (true) {
            iterations++
            val newState = BooleanArray(playfieldSize)
            for (i in 2 until newState.size - 2) {
                for (transition in transitions) {
                    if (currentState.matchesRegion(i, transition.from)) {
                        newState[i] = transition.to
                        break
                    }
                }
            }
            currentState = newState

            var leftMostTrue = 0
            for (i in 0..currentState.lastIndex) {
                if (currentState[i]) {
                    leftMostTrue = i
                    break
                }
            }

            val trunkated = currentState.dropWhile { !it }.dropLastWhile { !it }
            if (previous == trunkated) {
                println("Repitition found!")
                return
            }
            previous = trunkated

            print("iter $iterations (${leftMostTrue-zeroIndex+1})")

            print(trunkated.withIndex().filter { (_, value) -> value }.sumBy { (i, _) -> i+leftMostTrue-zeroIndex })
            print('\n')
        }

        return "" + currentState.withIndex().filter { (_, value) -> value }.sumBy { (i, _) -> i-zeroIndex }
    }

    fun BooleanArray.matchesRegion(startIndex : Int, compare: BooleanArray) : Boolean{
        var out = true
        var index = 0
        for (i in startIndex-2 until startIndex+compare.size-2) {
            if (this[i] != compare[index++]) {
                out = false
                break
            }
        }
        return out
    }

    override fun part2(): String {


        return ""
    }
}