import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day17(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 17
    private val input = adventOfCode.getInput(2017, day).toInt()

    override fun part1(): String {
        var insertedAt = 0
        var counter = 0
        var currentPos = 0
        val buffer = mutableListOf<Int>()
        while (counter <= 2017) {
            if (currentPos >= buffer.size - 1) {
                buffer.add(counter++)
            } else {
                buffer.add(currentPos + 1, counter++)
            }
            insertedAt = currentPos + 1
            currentPos = (currentPos + 1 + input) % buffer.size
        }

        return buffer[(insertedAt + 1) % buffer.size].toString()
    }

    override fun part2(): String {
        var curr = 0
        var counter = 0
        var currentPos = 0
        while (counter <= 50000000) {
            if (currentPos == 0) {
                curr = counter
            }
            currentPos = (currentPos + 1 + input) % ++counter
        }

        return curr.toString()
    }
}