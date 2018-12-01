package advent2017

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day23(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 23
    private val input = adventOfCode.getInput(2017, day).lines().map{ it.split(' ')}

    override fun part1(): String {
        val registers = Day08.NeverNullMap<String, Long> { 0 }
        var ins = 0
        var counter = 0L
        loop@while (true) {
            if (ins >= input.size)
                break@loop
            val curIns = input[ins]
            when(curIns[0]) {
                "set" -> {
                    registers[curIns[1]] = curIns[2].getValue(registers)
                }
                "sub" -> {
                    registers[curIns[1]] -= curIns[2].getValue(registers)
                }
                "mul" -> {
                    counter++
                    registers[curIns[1]] *= curIns[2].getValue(registers)
                }
                "jnz" -> {
                    if (curIns[1].getValue(registers) != 0L) {
                        ins += curIns[2].getValue(registers).toInt()
                        continue@loop
                    }

                }
            }
            ins++

        }

        return counter.toString()
    }

    override fun part2(): String {
        var a = 1L
        var b = 81L
        var c = b
        var d = 0L
        var e = 0L
        var f = 0L
        var g = 0L
        var h = 0L
        if (a != 0L) {
            b *= 100
            b += 100000
            c = b
            c += 17000
        }
        do {
            f = 1
            d = 2
            loop@do {
                e = 2
                do {
                    g = d
                    g *= e
                    g -= b
                    if (g == 0L) {
                        f = 0
                        break@loop
                    }
                    e += 1
                    g = e
                    g -= b
                } while (g != 0L)
                d += 1
                g = d
                g -= b
            } while(g != 0L)
            if (f == 0L) {
                h += 1 // <---------
            }
            g = b
            g -= c
            if (g != 0L) {
                b += 17
            }
        } while (g != 0L)
        return h.toString()
    }

    private fun String.getValue(map: Day08.NeverNullMap<String, Long>): Long {
        return if (this.matches(Regex("[a-z]"))) {
            map[this]
        } else {
            this.toLong()
        }
    }
}