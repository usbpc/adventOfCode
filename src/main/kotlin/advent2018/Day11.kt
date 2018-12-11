package advent2018

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day11(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 11
    private val input = adventOfCode.getInput(2018, day).toLong()

    fun Long.hundretsDigit() = this.toString().dropLast(2).last().toString().toLong()
    override fun part1(): String {
        val grid = Array<LongArray>(300){LongArray(300)}
        for (x in 1..300) {
            for (y in 1..300) {
                val rackId  = x + 10L
                var powerLvl = rackId * y
                powerLvl += input
                powerLvl *= rackId
                powerLvl = powerLvl.hundretsDigit() - 5
                grid[x-1][y-1] = powerLvl
            }
        }

        var maxPower = Long.MIN_VALUE
        var maxX = -1
        var maxY = -1
        for (x in 1..298) {
            for (y in 1..298) {
                val powerLevel = grid.getSectionSum(x-1,y-1, 2)
                if (powerLevel > maxPower) {
                    maxPower = powerLevel
                    maxX = x
                    maxY = y
                }
            }
        }

        return "$maxX,$maxY"
    }

    fun Array<LongArray>.getSectionSum(xstart : Int, ystart : Int, size : Int) : Long {
        var out = 0L
        for (x in xstart..xstart+size) {
            for (y in ystart..ystart+size) {
                out += this[x][y]
            }
        }
        return out
    }

    override fun part2(): String = runBlocking {
        val grid = Array(300){LongArray(300)}
        for (x in 1..300) {
            for (y in 1..300) {
                val rackId  = x + 10L
                var powerLvl = rackId * y
                powerLvl += input
                powerLvl *= rackId
                powerLvl = powerLvl.hundretsDigit() - 5
                grid[x-1][y-1] = powerLvl
            }
        }

        var curCellSize = 1
        val prefixSums = MutableList(300) { x-> grid[x].toMutableList() }

        var maxPower = Long.MIN_VALUE
        var maxX = -1
        var maxY = -1
        var maxSize = -1

        while (curCellSize <= 300) {
            prefixSums.withIndex()
                    .forEach { (x, yList) ->
                        yList.withIndex()
                                .forEach { (y, power) ->
                                    if (power > maxPower) {
                                        maxY = y
                                        maxX = x
                                        maxPower = power
                                        maxSize = curCellSize
                                    }
                                }
                    }

            curCellSize++

            prefixSums.removeAt(prefixSums.lastIndex)
            prefixSums.forEach { row -> row.removeAt(row.lastIndex) }

            val jobs = mutableListOf<Job>()

            prefixSums.withIndex()
                    .forEach { (x, yList) ->
                        val job = launch(Dispatchers.Default) {
                            for (y in 0 until yList.size) {
                                for (yToAdd in y until y+curCellSize) {
                                    yList[y] += grid[x+curCellSize-1][yToAdd]
                                }
                                for (xToAdd in x until x+curCellSize-1) {
                                    yList[y] += grid[xToAdd][y+curCellSize-1]
                                }
                            }
                        }
                        jobs.add(job)
                    }
            jobs.forEach { it.join() }
        }

        return@runBlocking "${maxX+1},${maxY+1},$maxSize"
    }
}