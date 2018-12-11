package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day11(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 11
    private val input = adventOfCode.getInput(2018, day).toLong()

    //rack ID = x coord + 10
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

    override fun part2(): String {
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
        var maxSize = -1
        for (size in 0..299) {
            for (x in 1..300-size) {
                for (y in 1..300-size) {
                    val powerLevel = grid.getSectionSum(x-1,y-1,size)
                    if (powerLevel > maxPower) {
                        maxPower = powerLevel
                        maxSize = size+1
                        maxX = x
                        maxY = y
                    }
                }
            }
        }

        return "$maxX,$maxY,$maxSize"
    }
}