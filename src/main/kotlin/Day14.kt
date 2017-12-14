import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Queue

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 14
    private val input = adventOfCode.getInput(2017, day)
    private val testInput = "flqrgnkx"

    override fun part1(): String = input.getMemoryMap().flatMap {it.asIterable()}.sumBy {if(it) 1 else 0}.toString()


    override fun part2(): String {
        val memoryMap = input.getMemoryMap()

        var counter = 0
        val visited = mutableSetOf<Pair<Int, Int>>()
        for ((rowIndex, row) in memoryMap.withIndex()) {
            for ((columnIndex, value) in row.withIndex()) {
                if (value) {
                    val pair = Pair(rowIndex, columnIndex)
                    if (pair !in visited) {
                        val connected = findConnected(memoryMap, pair)
                        visited.addAll(connected)
                        counter++
                    }
                }
            }
        }

        return counter.toString()
    }

    private fun String.getMemoryMap(): Array<BooleanArray> =
            Array(128) { x->
                val hash = ("$this-$x").knotSparseHash()
                hash.flatMap {b ->
                    var byte = b
                    val out = BooleanArray(8)
                    for (i in 7 downTo 0) {
                        if (byte and 1 == 1) {
                            out[i] = true
                        }
                        byte /= 2
                    }
                    out.asIterable()
                }.toBooleanArray()
            }

    private fun findConnected(field: Array<BooleanArray>, start: Pair<Int, Int>): Set<Pair<Int, Int>> {
        val visited = mutableSetOf<Pair<Int, Int>>()
        val toVisit = Queue<Pair<Int, Int>>()
        var current: Pair<Int, Int>
        toVisit.add(start)
        while (toVisit.isNotEmpty()) {
            current = toVisit.remove()
            visited.add(current)
            current.neighbors()
                    .filter {it.first >= 0 && it.second >= 0}
                    .filter {it.first < field.size && it.second < field[0].size}
                    .filter{field[it.first][it.second]}
                    .filter {it !in visited}
                    .filter {it !in toVisit}
                    .forEach {toVisit.add(it)}

        }
        return visited
    }

    private fun Pair<Int, Int>.neighbors(): Set<Pair<Int, Int>> {
        val out = mutableSetOf<Pair<Int, Int>>()
        out.add(Pair(this.first + 1, this.second))
        out.add(Pair(this.first - 1, this.second))
        out.add(Pair(this.first, this.second + 1))
        out.add(Pair(this.first, this.second - 1))
        return out
    }
}