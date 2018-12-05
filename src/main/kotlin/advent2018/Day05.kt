package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day05(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 5
    private val input = adventOfCode.getInput(2018, day)

    override fun part1(): String {
        val regexList = mutableListOf<Regex>()

        var string = input

        for (c in 'a'..'z') {
            regexList.add(Regex("$c${c.toUpperCase()}"))
            regexList.add(Regex("${c.toUpperCase()}$c"))
        }

       var prev = ""

        while (prev != string) {
            prev = string
            for (r in regexList) {
                string = r.replaceFirst(string, "")
            }
        }

        return "" + string.length
    }

    override fun part2(): String {
        val regexList = mutableListOf<Regex>()


        for (c in 'a'..'z') {
            regexList.add(Regex("$c${c.toUpperCase()}"))
            regexList.add(Regex("${c.toUpperCase()}$c"))
        }

        val resluts = mutableListOf<String>()
        for (c in 'a'..'z') {
            var string = input.replace("$c", "").replace("${c.toUpperCase()}", "")
            var prev = ""

            while (prev != string) {
                prev = string
                for (r in regexList) {
                    string = r.replaceFirst(string, "")
                }
            }

            resluts.add(string)
        }

        return "" + resluts.minBy { it.length }!!.length
    }
}