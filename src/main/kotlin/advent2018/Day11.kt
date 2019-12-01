package advent2018

import kotlinx.coroutines.*
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import kotlin.math.max

class Day11(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 11
    private val input = adventOfCode.getInput(2018, day).toInt()

    private fun Int.hundredsDigit() = (this % 1000) / 100

    override fun part1(): Any {
        val grid = Array(300) { IntArray(300) }
        for (x in 1..300) {
            for (y in 1..300) {
                val rackId = x + 10
                var powerLvl = rackId * y
                powerLvl += input
                powerLvl *= rackId
                powerLvl = powerLvl.hundredsDigit() - 5
                grid[x - 1][y - 1] = powerLvl
            }
        }

        var maxPower = Long.MIN_VALUE
        var maxX = -1
        var maxY = -1
        for (x in 1..298) {
            for (y in 1..298) {
                val powerLevel = grid.getSectionSum(x - 1, y - 1, 2)
                if (powerLevel > maxPower) {
                    maxPower = powerLevel
                    maxX = x
                    maxY = y
                }
            }
        }

        return "$maxX,$maxY"
    }

    private fun Array<IntArray>.getSectionSum(xstart: Int, ystart: Int, size: Int): Long {
        var out = 0L
        for (x in xstart..xstart + size) {
            for (y in ystart..ystart + size) {
                out += this[x][y]
            }
        }
        return out
    }

    override fun part2(): Any {
        val grid = Array(300) { IntArray(300) }
        for (x in 1..300) {
            for (y in 1..300) {
                val rackId = x + 10
                var powerLvl = rackId * y
                powerLvl += input
                powerLvl *= rackId
                powerLvl = powerLvl.hundredsDigit() - 5
                ((((x+10) * y + input) * (x+10)) % 1000) / 100
                grid[x - 1][y - 1] = powerLvl
            }
        }

        var maxX = 0
        var maxY = 0
        var maxSize = 0

        runBlocking(Dispatchers.Default) {
            val deferredList = mutableListOf<Deferred<List<Int>>>()
                for (x in 0..299) {
                    for (y in 0..299) {
                        async {
                            var ms = -1
                            var maxTotal = Int.MIN_VALUE
                            var total = 0
                            for (size in 0..299-max(x, y)) {
                                for (yToAdd in y..y+size) {
                                    total += grid[x+size][yToAdd]
                                }
                                for (xToAdd in x until x+size) {
                                    total += grid[xToAdd][y+size]
                                }
                                if (total > maxTotal) {
                                    maxTotal = total
                                    ms = size+1
                                }
                            }
                            listOf(maxTotal, x+1, y+1, ms)
                        }.let { localMax -> deferredList.add(localMax) }
                    }
                }
            deferredList.map { it.await() }.maxBy { it[0] }!!.let { bestArea ->
                maxX = bestArea[1]
                maxY = bestArea[2]
                maxSize = bestArea[3]
            }
        }

        return "$maxX,$maxY,$maxSize"
    }
}