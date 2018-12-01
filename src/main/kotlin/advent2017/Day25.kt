import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day25(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 25
    private val input = adventOfCode.getInput(2017, day)
    private val startStateRegex = Regex("Begin in state ([A-Z]).")
    private val startState = startStateRegex.find(input)?.groups?.get(1)?.value?.get(0) ?: throw IllegalStateException("No starting position defined!")
    private val stepCountRegex = Regex("Perform a diagnostic checksum after (\\d+) steps.")
    private val stepCount = stepCountRegex.find(input)?.groups?.get(1)?.value?.toInt() ?: throw IllegalStateException("Step count not defined!")
    private val stateRegex = Regex("In state ([A-Z]):\\n  If the current value is 0:\\n    - Write the value (1|0).\\n    . Move one slot to the (right|left).\\n    - Continue with state ([A-Z]).\\n  If the current value is 1:\\n    - Write the value (0|1).\\n    - Move one slot to the (right|left).\\n    . Continue with state ([A-Z]).")
    private val states = stateRegex.findAll(input).associate {
        val curState = it.groups[1]!!.value[0]
        val valOnZero = it.groups[2]!!.value.toInt()
        val dirOnZero = if (it.groups[3]!!.value == "right") 1 else -1
        val stateOnZero = it.groups[4]!!.value[0]
        val valOnOne = it.groups[5]!!.value.toInt()
        val dirOnOne = if (it.groups[6]!!.value == "right") 1 else -1
        val stateOnOne = it.groups[7]!!.value[0]
        curState to State(valOnZero, dirOnZero, stateOnZero, valOnOne, dirOnOne, stateOnOne)
    }
    private data class State(val valOnZero: Int, val dirOnZero: Int, val stateOnZero: Char, val valOnOne: Int, val dirOnOne: Int, val stateOnOne: Char)
    override fun part1(): String {
        var state  = startState
        var pos = 0
        var tape = Tape()
        repeat(stepCount) {
            val curState = states[state] ?: throw IllegalStateException("State was Illegal")
            if (tape[pos] == 0) {
                tape[pos] = curState.valOnZero
                pos += curState.dirOnZero
                state = curState.stateOnZero
            } else {
                tape[pos] = curState.valOnOne
                pos += curState.dirOnOne
                state = curState.stateOnOne
            }
        }
        return tape.checksum().toString()
    }

    override fun part2(): String {
        return "Nothing to do here :)"
    }
    private class Tape {
        private val right = mutableListOf<Int>()
        private val left = mutableListOf<Int>()
        operator fun get(index: Int): Int {
            if (index < 0) {
                return if (-index - 1< left.size) left[-index - 1] else 0
            }
            return if (index < right.size) right[index] else 0
        }
        operator fun set(index: Int, element: Int) {
            if (index < 0) {
                if (-index - 1 < left.size) {
                    left[-index - 1] = element
                } else {
                    assert(-index - 1 == left.size)
                    left.add(element)
                }
            } else {
                if (index < right.size) {
                    right[index] = element
                } else {
                    assert(index == right.size)
                    right.add(element)
                }
            }
        }
        fun checksum(): Int {
            return left.sum() + right.sum()
        }
        infix fun Int.mod(that: Int): Int {
            val tmp = this % that
            if (tmp < 0)
                return that + tmp
            return tmp
        }

    }

}