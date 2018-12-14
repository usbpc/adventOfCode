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

    override fun part1() = generateSequence().drop(input.toInt()).take(10).fold(StringBuilder()) { str, score -> str.append(score) }.toString()

    override fun part2(): String {
        val toFind = input.map { c -> c - '0' }
        return "${generateSequence().windowed(toFind.size, 1).withIndex().first { (_, seq) -> seq == toFind }.index}"
    }
}