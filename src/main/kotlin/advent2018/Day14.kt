package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 14
    private val input = adventOfCode.getInput(2018, day)

    private fun generateSequence() = sequence {
        var counter = 2
        var firstElf = 0
        var secondElf = 1
        val scores = mutableListOf(3, 7)
        yield(3)
        yield(7)
        while (true) {
            counter++
            var sum = scores[firstElf] + scores[secondElf]
            if (sum >= 10) {
                val firstDigit = sum / 10
                scores.add(firstDigit)
                yield(firstDigit)
                sum %= 10
            }
            scores.add(sum)
            yield(sum)

            firstElf = (firstElf + scores[firstElf] + 1) % scores.size
            secondElf = (secondElf + scores[secondElf] + 1) % scores.size
        }
    }

    override fun part1() = generateSequence().drop(input.toInt()).take(10).fold(StringBuilder()) { str, score -> str.append(score) }.toString()

    private infix fun List<Byte>.match(that: List<Byte>) : Boolean {
        if (this.size != that.size)
            return false
        for (i in 0..this.lastIndex) {
            if (this[i] != that[i])
                return false
        }
        return true
    }

    override fun part2(): String {
        val sequence = generateSequence().take(100).toList()

        val toFind = input.map { c -> (c - '0').toByte() }
        var firstElf = 4
        var secondElf = 3
        val scores = mutableListOf<Byte>(3, 7, 1, 0, 1, 0)
        while (true) {
            var sum = (scores[firstElf] + scores[secondElf]).toByte()
            if (sum >= 10) {
                val firstDigit : Byte = (sum / 10).toByte()
                scores.add(firstDigit)
                if (toFind match  scores.subList(scores.lastIndex - 6, scores.lastIndex))
                    return (scores.lastIndex-toFind.size).toString()
                sum = (sum % 10).toByte()
            }

            scores.add(sum)
            if (toFind match scores.subList(scores.lastIndex - 6, scores.lastIndex))
                return (scores.lastIndex-toFind.size).toString()

            firstElf = (firstElf + scores[firstElf] + 1) % scores.size
            secondElf = (secondElf + scores[secondElf] + 1) % scores.size
        }
    }
}