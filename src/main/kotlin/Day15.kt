import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.constrainCount

class Day15(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 15
    private val inputRegex = Regex("Generator (?:A|B) starts with (\\d+)")
    private val input = adventOfCode.getInput(2017, day)
            .lines()
            .map {inputRegex.find(it)?.groups?.get(1)?.value?.toInt()}
            .requireNoNulls()

    override fun part1(): String {
        val genA = generateSequence((input[0] * 16807L) % 2147483647L) {(it * 16807L) % 2147483647L}.constrainOnce()
        val genB = generateSequence((input[1] * 48271L) % 2147483647L) {(it * 48271L) % 2147483647L}.constrainOnce()

        return (genA zip genB)
                .constrainCount(40_000_000)
                .filter {(numA, numB) -> numA % 65536L == numB % 65536L}
                .count()
                .toString()
    }

    override fun part2(): String {
        val genA = generateSequence((input[0] * 16807L) % 2147483647L) {
            var prev = (it * 16807L) % 2147483647L
            while (prev % 4L != 0L)
                prev = ((prev * 16807L) % 2147483647L)
            prev
        }
        val genB = generateSequence((input[1] * 48271L) % 2147483647L) {
            var prev = ((it * 48271L) % 2147483647L)
            while (prev % 8L != 0L)
                prev = ((prev * 48271L) % 2147483647L)
            prev
        }
        return (genA zip genB)
                .constrainCount(5_000_000)
                .filter {(numA, numB) -> numA % 65536L == numB % 65536L}
                .count()
                .toString()
    }
}