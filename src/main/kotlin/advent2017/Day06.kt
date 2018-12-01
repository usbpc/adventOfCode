import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day

class Day06(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 6
    val input = adventOfCode.getInput(2017, 6).split('\t').map {it.toInt()}.toMutableList()
    var part1: Int? = null
    var part2: Int? = null
    override fun part1(): String {
        if (part1 == null) {
            solve()
        }
        return part1.toString()
    }

    override fun part2(): String {
        if (part2 == null) {
            solve()
        }
        return part2.toString()
    }
    private fun solve() {
        if (input.isEmpty()) throw IllegalArgumentException("Thing is not allowed to be empty!")
        val knownConfigurations = mutableSetOf<List<Int>>()
        var counter = 0
        while (input !in knownConfigurations) {
            knownConfigurations.add(input.toList())
            counter++

            var (biggestIndex, biggestNum) = input.withIndex().maxBy {it.value}!!

            input[biggestIndex] = 0
            while (biggestNum > 0) {
                biggestNum--
                biggestIndex = (biggestIndex + 1) % input.size
                input[biggestIndex]++
            }
        }

        part2 = knownConfigurations.size - knownConfigurations.indexOf(input)
        part1 = counter
    }
}