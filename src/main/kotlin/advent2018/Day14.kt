package advent2018

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 14
    private val input = adventOfCode.getInput(2018, day)

    private fun generateSequence() = sequence {
        var firstElf = 0
        var secondElf = 1
        val scores = mutableListOf(3, 7)
        yield(3)
        yield(7)
        while (true) {
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

    private fun CoroutineScope.generateSequence() : ReceiveChannel<Int> {
        val channel = Channel<Int>(5)
        launch {
            try {
                var firstElf = 0
                var secondElf = 1
                val scores = mutableListOf(3, 7)
                channel.send(3)
                channel.send(7)
                while (isActive) {
                    val sum = scores[firstElf] + scores[secondElf]
                    if (sum >= 10) {
                        val firstDigit = sum / 10
                        val secondDigit = sum % 10
                        scores.add(firstDigit)
                        channel.send(firstDigit)
                        scores.add(secondDigit)
                        channel.send(secondDigit)
                    } else {
                        scores.add(sum)
                        channel.send(sum)
                    }

                    firstElf = (firstElf + scores[firstElf] + 1) % scores.size
                    secondElf = (secondElf +scores[secondElf] + 1) % scores.size
                }
            } catch (e: Exception) {
                println("I got an exception: $e")
            }
        }
        return channel
    }

    fun generateKMPTable(w: List<Int>) : IntArray {
        val out = IntArray(w.size)
        out[0] = -1

        var pos = 1
        var cnd = 0

        while (pos < w.size) {
            if (w[pos] == w[cnd]) {
                out[pos] = out[cnd]
            } else {
                out[pos] = cnd
                cnd = out[cnd]
                while (cnd >= 0 && w[pos] != w[cnd]) {
                    cnd = out[cnd]
                }
            }

            pos++
            cnd++
        }
        return out
    }

    override fun part1() = generateSequence().drop(input.toInt()).take(10).fold(StringBuilder()) { str, score -> str.append(score) }.toString()

    override fun part2(): String {
        val toFind = input.map { c -> c - '0' }

        val table = generateKMPTable(toFind)
        var k = 0
        var j = 0

        var firstElf = 0
        var secondElf = 1
        val scores = mutableListOf(3, 7)
        while (true) {
            var sum = scores[firstElf] + scores[secondElf]
            if (sum >= 10) {
                val firstDigit = sum / 10
                scores.add(firstDigit)
                sum %= 10
                if (toFind[k] == scores[j]) {
                    k++
                    j++
                    if (k == toFind.size) {
                        return "${j-6}"
                    }
                } else {
                    k = table[k]
                    if (k < 0) {
                        k++
                        j++
                    }
                }
            }
            scores.add(sum)

            firstElf = (firstElf + scores[firstElf] + 1) % scores.size
            secondElf = (secondElf + scores[secondElf] + 1) % scores.size
            if (toFind[k] == scores[j]) {
                k++
                j++
                if (k == toFind.size) {
                    return "${j-6}"
                }
            } else {
                k = table[k]
                if (k < 0) {
                    k++
                    j++
                }
            }
        }
        
        return generateSequence().windowed(toFind.size, 1).withIndex().first { (_, seq) -> seq == toFind }.index.toString()
    }
}