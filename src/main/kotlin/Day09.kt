import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day

class Day09(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 9
    enum class State {
        GARBAGE, ESCAPED, NORMAL
    }
    var part1: Int? = null
    var part2: Int? = null

    override fun part1(): String {
        if (part1 == null)
            solve()
        return part1.toString()
    }

    override fun part2(): String {
        if (part2 == null)
            solve()
        return part2.toString()
    }
    private fun solve() {
        val input = adventOfCode.getInput(2017, 9)
        var counter = 0
        var score = 0
        var garbage = 0
        var state = State.NORMAL
        for (char in input) {
            when (state) {
                State.NORMAL -> {
                    when (char) {
                        '<' -> state = State.GARBAGE
                        '{' -> counter++
                        '}' -> score += counter--
                    }
                }
                State.GARBAGE -> {
                    when (char) {
                        '>' -> state = State.NORMAL
                        '!' -> state = State.ESCAPED
                        else -> garbage++
                    }
                }
                State.ESCAPED -> {
                    state = State.GARBAGE
                }
            }
        }
        part1 = score
        part2 = garbage
    }
}