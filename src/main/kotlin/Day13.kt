import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day13(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 13
    private val sample = """0: 3
1: 2
4: 4
6: 4"""
    private val regex = Regex("(\\d+): (\\d+)")
    private val input = adventOfCode.getInput(2017, 13).lines().map {
        regex.find(it)?.groups?.drop(1)?.map {it?.value?.toInt()}?.requireNoNulls()
    }.requireNoNulls()

    override fun part1(): String =
            input.filter {it[0] % (2 * (it[1] - 2) + 2) == 0}.fold(0) {acc, crr ->
                acc + crr[0] * crr[1]
            }.toString()

    override fun part2(): String {
        var delay = 0
        while (input.any {(it[0] + delay) % (2 * (it[1] - 2) + 2) == 0}) {
            delay++
        }
        return delay.toString()
    }
}