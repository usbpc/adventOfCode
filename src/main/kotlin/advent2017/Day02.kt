import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day

class Day02(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 2
    private val input: List<List<Long>>
    init {
        val tmp: MutableList<List<Long>> = mutableListOf()
        adventOfCode.getInput(2017, 2).lines().forEach {line ->
            val row = mutableListOf<Long>()
            line.splitToSequence("\t").forEach {number ->
                row.add(number.toLong())
            }
            tmp.add(row)
        }
        input = tmp.toList()
    }
    override fun part1(): String {
        var checksum = 0L
        input.forEach {row ->
            var largest = Long.MIN_VALUE
            var smallest = Long.MAX_VALUE
            row.forEach {
                if (it > largest)
                    largest = it
                if (it < smallest)
                    smallest = it
            }
            checksum += largest - smallest
        }
        return checksum.toString()
    }

    override fun part2(): String {
        var output = 0L
        input.forEach {row ->
            row.forEach {cur ->
                row.filter {it != cur && it % cur == 0L}.forEach {output += it/cur}}
        }
        return output.toString()
    }
}