import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day13(override val adventOfCode: AdventOfCode) : Day {
    private data class FirewallLayer(val layer: Int, val depth: Int)
    override val day: Int = 13
    private val regex = Regex("(\\d+): (\\d+)")
    private val input = adventOfCode.getInput(2017, 13).lines()
            .map {line ->
                regex.find(line)?.groups?.drop(1)
                        ?.map {it?.value?.toInt()}
                        ?.requireNoNulls()
            }.requireNoNulls()
            .map {(layer, depth) -> FirewallLayer(layer, depth)}

    override fun part1(): String = input
            .filter {(layer, depth) -> layer % (2 * (depth - 2) + 2) == 0}
            .fold(0) {acc, (layer, depth) -> acc + layer * depth}
            .toString()

    override fun part2(): String = generateSequence(0) {it + 1}
            .first {delay ->
                input.none {(layer, depth) -> (layer + delay) % (2 * (depth - 2) + 2) == 0}
            }
            .toString()
}