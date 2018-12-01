import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day17(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 17
    private val input = adventOfCode.getInput(2017, day).toInt()

    override fun part1(): String {
        var currentPos = 0
        val buffer = mutableListOf(0)
        for (counter in 1..2017) {
            currentPos = (currentPos + input) % buffer.size + 1
            if (currentPos >= buffer.size) {
                buffer.add(counter)
            } else {
                buffer.add(currentPos, counter)
            }
        }
        return buffer[(currentPos + 1) % buffer.size].toString()
    }

    override fun part2(): String {
        var curr = 0
        var currentPos = 0
        for (counter in 1..50000000) {
            currentPos = (currentPos + input) % counter + 1
            if (currentPos == 1) {
                curr = counter
            }
        }
        return curr.toString()
    }
}
