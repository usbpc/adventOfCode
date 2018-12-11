package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.io.File
import java.lang.StringBuilder

class Day10(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 10
    private val input = adventOfCode.getInput(2018, day).lines()
            .map { line -> line.extractLongs() }
            .map { numbers -> Position(numbers[1], numbers[0]) to Velocity(numbers[3], numbers[2]) }
    data class Position(var x : Long, var y : Long) {
        fun add(v : Velocity) {
            this.x += v.x
            this.y += v.y
        }
        fun sub(v : Velocity) {
            this.x -= v.x
            this.y -= v.y
        }
    }

    data class Velocity(val x : Long, val y : Long)

    fun printGrid(data : List<Pair<Position, Velocity>>) : String {
        val maxX = data.maxBy { it.first.x }!!.first.x
        val maxY = data.maxBy { it.first.y }!!.first.y
        val minX = data.minBy { it.first.x }!!.first.x
        val minY = data.minBy { it.first.y }!!.first.y

        val out = StringBuilder()

        for (x in minX..maxX) {
            for (y in minY..maxY) {
                if (data.filter { it.first == Position(x, y) }.any()) {
                    out.append('#')
                } else {
                    out.append(' ')
                }
            }
            out.append('\n')
        }
        out.deleteCharAt(out.lastIndex)
        return out.toString()
    }

    private fun getLetterMap() : Map<String, Char> {
        val fileText = File("resources/letters.txt").readText().split(".\n")
        val bigLetters = fileText.dropLast(1).map { it.dropLast(1) }
        val letters = fileText.last()
        return bigLetters.zip(letters.toList()).toMap()
    }

    fun String.splitLettersToList() : List<String> {
        val lines = this.lines()
        val out = mutableListOf<String>()
        for (i in 0..7) {
            val builder = StringBuilder()
            for (line in lines) {
                builder.append(line.subSequence((6+2)*i..(6+2)*i+5))
                builder.append('\n')
            }
            builder.deleteCharAt(builder.lastIndex)
            out.add(builder.toString())
        }
        return out
    }

    override fun part1(): String {
        val bigToSmall = getLetterMap()
        val inputCopy = input.map { it.first.copy() to it.second.copy() }
        var distance = Long.MAX_VALUE
        var counter = -1L
        while (true) {
            counter++
            val maxY = inputCopy.maxBy { it.first.y }!!.first.y
            val maxX = inputCopy.maxBy { it.first.x }!!.first.x
            val minY = inputCopy.minBy { it.first.y }!!.first.y
            val minX = inputCopy.minBy { it.first.x }!!.first.x

            if ((maxY - minY) * (maxX - minX)> distance) {
                break
            }

            distance = (maxY - minY) * (maxX - minX)
            inputCopy.forEach { it.first.add(it.second) }
        }

        inputCopy.forEach { it.first.sub(it.second) }
        return  "" + printGrid(inputCopy).splitLettersToList().map { bigToSmall[it] }.requireNoNulls().fold(StringBuilder()) { acc, c -> acc.append(c) }
    }

    override fun part2(): String {
        val inputCopy = input.map { it.first.copy() to it.second.copy() }
        var distance = Long.MAX_VALUE
        var counter = -1
        while (true) {
            val maxY = inputCopy.maxBy { it.first.y }!!.first.y
            val maxX = inputCopy.maxBy { it.first.x }!!.first.x
            val minY = inputCopy.minBy { it.first.y }!!.first.y
            val minX = inputCopy.minBy { it.first.x }!!.first.x

            if ((maxY - minY) * (maxX - minX)> distance) {
                break
            }

            distance = (maxY - minY) * (maxX - minX)
            inputCopy.forEach { it.first.add(it.second) }
            counter++
        }
        return "$counter"
    }
}
