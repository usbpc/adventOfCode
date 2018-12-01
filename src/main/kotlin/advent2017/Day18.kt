package advent2017

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.concurrent.atomic.AtomicInteger

class Day18(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 18
    private val input = adventOfCode.getInput(2017, day).lines().map {it.split(' ')}

    override fun part1(): String {
        val registers = Day08.NeverNullMap<String, Long> { 0 }
        var lastSoundPlayed = 0L
        var instruction = 0

        loop@ while (true) {
            val currInstruction = input[instruction]
            when (currInstruction[0]) {
                "snd" -> lastSoundPlayed = currInstruction[1].getValue(registers)
                "set" -> registers[currInstruction[1]] = currInstruction[2].getValue(registers)
                "add" -> registers[currInstruction[1]] += currInstruction[2].getValue(registers)
                "mul" -> registers[currInstruction[1]] *= currInstruction[2].getValue(registers)
                "mod" -> registers[currInstruction[1]] = registers[currInstruction[1]] mod currInstruction[2].getValue(registers)
                "rcv" -> if (currInstruction[1].getValue(registers) != 0L) break@loop
                "jgz" -> {
                    if (currInstruction[1].getValue(registers) > 0) {
                        instruction += currInstruction[2].getValue(registers).toInt()
                        continue@loop
                    }
                }

            }
            instruction++
        }
        return lastSoundPlayed.toString()
    }

    private fun String.getValue(map: Day08.NeverNullMap<String, Long>): Long {
        return if (this.matches(Regex("[a-z]"))) {
            map[this]
        } else {
            this.toLong()
        }
    }

    private infix fun Int.mod(other: Int): Int {
        var r = this % other
        if (r < 0) {
            r += other
        }
        return r
    }

    private infix fun Long.mod(other: Long): Long {
        var r = this % other
        if (r < 0) {
            r += other
        }
        return r
    }

    override fun part2(): String {
        val myContext = newSingleThreadContext("Day 18")
        val channel = arrayOf(Channel<Long>(1000), Channel<Long>(1000))
        val thing = AtomicInteger(0)

        val scope = CoroutineScope(myContext)

        val job0 = scope.launch {
            val registers = Day08.NeverNullMap<String, Long> { 0 }
            var instruction = 0
            loop@while (true) {
                val currInstruction = input[instruction]
                when (currInstruction[0]) {
                    "snd" -> {
                        channel[0].send(currInstruction[1].getValue(registers))
                    }
                    "set" -> registers[currInstruction[1]] = currInstruction[2].getValue(registers)
                    "add" -> registers[currInstruction[1]] += currInstruction[2].getValue(registers)
                    "mul" -> registers[currInstruction[1]] *= currInstruction[2].getValue(registers)
                    "mod" -> registers[currInstruction[1]] = registers[currInstruction[1]] mod currInstruction[2].getValue(registers)
                    "rcv" -> {
                        synchronized(thing) {
                            thing.incrementAndGet()
                        }
                        registers[currInstruction[1]] = channel[1].receive()
                        synchronized(thing) {
                            thing.decrementAndGet()
                        }
                    }
                    "jgz" -> {
                        if (currInstruction[1].getValue(registers) > 0) {
                            instruction += currInstruction[2].getValue(registers).toInt()
                            continue@loop
                        }
                    }

                }
                instruction++
            }
        }
        var sendCounter = 0
        val job1 = scope.launch(myContext) {
            val registers = Day08.NeverNullMap<String, Long> { 0 }.apply {
                set("p", 1)
            }
            var instruction = 0
            loop@ while (true) {
                val currInstruction = input[instruction]
                when (currInstruction[0]) {
                    "snd" -> {
                        sendCounter++
                        channel[1].send(currInstruction[1].getValue(registers))
                    }
                    "set" -> registers[currInstruction[1]] = currInstruction[2].getValue(registers)
                    "add" -> registers[currInstruction[1]] += currInstruction[2].getValue(registers)
                    "mul" -> registers[currInstruction[1]] *= currInstruction[2].getValue(registers)
                    "mod" -> registers[currInstruction[1]] = registers[currInstruction[1]] mod currInstruction[2].getValue(registers)
                    "rcv" -> {
                        synchronized(thing) {
                            thing.incrementAndGet()
                        }
                        registers[currInstruction[1]] = channel[0].receive()
                        synchronized(thing) {
                            thing.decrementAndGet()
                        }
                    }
                    "jgz" -> {
                        if (currInstruction[1].getValue(registers) > 0) {
                            instruction += currInstruction[2].getValue(registers).toInt()
                            continue@loop
                        }
                    }

                }
                instruction++
            }
        }
        while(!(thing.get() >= 2 && channel[0].isEmpty && channel[1].isEmpty)) {}
        job0.cancel()
        job1.cancel()
        myContext.close()
        return sendCounter.toString()
    }
}