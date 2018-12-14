package advent2018

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder
import java.util.*

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 14
    private val input = adventOfCode.getInput(2018, day)

    private fun generateSequence() = sequence {
        var index = 0
        val numbers = IntArray(20188250+10)
        numbers[index++] = 3
        numbers[index++] = 7
        var firstElf = 0
        var secondElf = 1
        yield(3)
        yield(7)
        while (true) {
            val sum = numbers[firstElf] + numbers[secondElf]
            if (sum >= 10) {
                val firstDigit = sum / 10
                val secondDigit = sum % 10
                numbers[index++] = firstDigit
                yield(firstDigit)
                numbers[index++] = secondDigit
                yield(secondDigit)
            } else {
                numbers[index++] = sum
                yield(sum)
            }

            firstElf = (firstElf + numbers[firstElf] + 1) % index
            secondElf = (secondElf +numbers[secondElf] + 1) % index
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

    override fun part1(): String {
        val numbers = generateSequence().iterator()

        repeat(input.toInt()) {
            numbers.next()
        }

        val out = StringBuilder()

        repeat(10) {
            out.append(numbers.next())
        }

        return out.toString()
    }

    private class FixedSizeLikedList<E>(val size: Int) {
        var head : Entry<E>
        var tail : Entry<E>
        init {
            head = Entry()
            var cur = head
            repeat(size-1) {
                val new = Entry(prev = cur)
                cur.next = new
                cur = new
            }
            tail = cur
        }

        fun add(e: E) {
            val tmp = head
            head = head.next!!
            head.prev = null

            tmp.next = null
            tmp.prev = tail

            tmp.value = e

            tail.next = tmp

            tail = tmp
        }

        override fun equals(other: Any?): Boolean {
            if (other !is List<*>)
                return false
            if (other.size != this.size)
                return false
            var cur = head

            for (i in 0..other.lastIndex) {
                if (other[i] != cur.value)
                    return false
                cur = cur.next ?: break
            }

            return true
        }

        override fun toString(): String {
            val out = StringBuilder()
            out.append('[')
            var cur = head
            while (cur.next != null) {
                out.append(cur.value)
                out.append(',')
                cur = cur.next!!
            }
            out.append(cur.value)
            out.append(']')
            return out.toString()
        }

        class Entry<E>(var next: Entry<E>? = null, var prev: Entry<E>? = null, var value : E? = null)
    }

    private class NoBoundsIntRingBuffer(size: Int) {
        val backingArray = IntArray(size)
        var front = 0

        fun add(num: Int) {
            backingArray[front] = num
            front = (front + 1) % backingArray.size
        }

        override fun equals(other: Any?): Boolean {
            if (other !is List<*>)
                return false
            if (other.size != backingArray.size)
                return false
            for (i in 0..other.lastIndex) {
                if (other[i] != backingArray[(i+front) % backingArray.size])
                    return false
            }
            return true
        }

        override fun toString(): String = Arrays.toString(backingArray)
    }

    override fun part2(): String {
        val numbers = generateSequence().iterator()
        val toFind = input.map { c -> c - '0' }
        var counter = 0

        val compare = FixedSizeLikedList<Int>(toFind.size)
        repeat(toFind.size) {
            compare.add(numbers.next())
            counter++
        }

        while (!compare.equals(toFind)) {
            compare.add(numbers.next())
            counter++
        }

        return "${counter - toFind.size}"
    }
}