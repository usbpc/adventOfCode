import usbpc.aoc.inputgetter.AdventOfCode

class Day05(override val adventOfCode: AdventOfCode) : Day {
    val input = adventOfCode.getInput(2017, 5).lines().map {it.toInt()}
    override fun part1(): String {
        val inputCopy = input.toTypedArray()
        var currPos = 0
        var counter: Long = 0
        while (currPos < inputCopy.size && currPos >= 0) {
            counter++
            currPos += inputCopy[currPos]++
        }
        return counter.toString()
    }

    override fun part2(): String {
        val inputCopy = input.toTypedArray()
        var currPos = 0
        var counter: Long = 0
        while (currPos < inputCopy.size && currPos >= 0) {
            counter++
            currPos +=
                    if (inputCopy[currPos] >= 3) {
                        inputCopy[currPos]--
                    } else {
                        inputCopy[currPos]++
                    }
        }
        return counter.toString()
    }
}
