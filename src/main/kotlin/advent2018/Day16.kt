package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*

fun IntArray.applyOperation(op: Day16.Operations, A: Int, B: Int, C: Int) {
    when(op) {
        Day16.Operations.ADDR -> this[C] = this[A] + this[B]
        Day16.Operations.ADDI -> this[C] = this[A] + B
        Day16.Operations.MULR -> this[C] = this[A] * this[B]
        Day16.Operations.MULI -> this[C] = this[A] * B
        Day16.Operations.BANR -> this[C] = this[A] and this[B]
        Day16.Operations.BANI -> this[C] = this[A] and B
        Day16.Operations.BORR -> this[C] = this[A] or this[B]
        Day16.Operations.BORI -> this[C] = this[A] or B
        Day16.Operations.SETR -> this[C] = this[A]
        Day16.Operations.SETI -> this[C] = A
        Day16.Operations.GTIR -> {
            if (A > this[B])
                this[C] = 1
            else
                this[C] = 0
        }
        Day16.Operations.GTRI -> {
            if (this[A] > B)
                this[C] = 1
            else
                this[C] = 0
        }
        Day16.Operations.GTRR -> {
            if (this[A] > this[B])
                this[C] = 1
            else
                this[C] = 0
        }
        Day16.Operations.EQIR -> {
            if (A == this[B])
                this[C] = 1
            else
                this[C] = 0
        }
        Day16.Operations.EQRI -> {
            if (this[A] == B)
                this[C] = 1
            else
                this[C] = 0
        }
        Day16.Operations.EQRR -> {
            if (this[A] == this[B])
                this[C] = 1
            else
                this[C] = 0
        }
    }
}

class Day16(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 16
    private val instRegex = """Before: \[\d+, \d+, \d+, \d+]\n\d+ \d+ \d+ \d+\nAfter:  \[\d+, \d+, \d+, \d+]""".toRegex()
    private val input = adventOfCode.getInput(2018, day).split("\n\n\n\n")

    private class TestCase(val preRegisters : IntArray, val inst: IntArray, val postRegisters : IntArray) {
        var matches = 0
        val possibleInstructions = mutableSetOf<Operations>()
        fun testAllOpcodes() {
            for (op in Operations.values()) {
                val applied = preRegisters.copyOf()
                applied.applyOperation(op, inst[1], inst[2], inst[3])

                if (Arrays.equals(postRegisters, applied)) {
                    possibleInstructions.add(op)
                    matches++
                }
            }
        }
    }

    private fun String.getTestCases() = this.split("\n\n").map { case ->
            val lines = case.split('\n')
            TestCase(lines[0].extractInts(), lines[1].extractInts(), lines[2].extractInts())
        }

    enum class Operations {
        ADDR,
        ADDI,
        MULR,
        MULI,
        BANR,
        BANI,
        BORR,
        BORI,
        SETR,
        SETI,
        GTIR,
        GTRI,
        GTRR,
        EQIR,
        EQRI,
        EQRR

    }

    override fun part1(): String {
        val testCases = input[0].getTestCases()
        testCases.forEach { it.testAllOpcodes() }
        return testCases.filter { it.matches >= 3 }.count().toString()
    }

    override fun part2(): String {
        val testCases = input[0].getTestCases()
        testCases.forEach { it.testAllOpcodes() }

        val possibles = testCases.groupBy { it.inst[0] }
                .map { (opcode, list) ->
                    val base = list.first().possibleInstructions
                    opcode to list.drop(1).fold(base as Set<Operations>) { acc, testCase -> acc.intersect(testCase.possibleInstructions) }.toMutableSet()
                }
                .sortedBy { it.first }

        var onlyOnePosisble = possibles.firstOrNull() { it.second.size == 1 }
        val alreadyResolved = mutableSetOf(onlyOnePosisble!!.first)

        while (onlyOnePosisble != null) {
            possibles.filter { it.first !in alreadyResolved }
                    .forEach {
                        it.second.remove(onlyOnePosisble!!.second.single())
                    }

            val newWithOnlyOne = possibles.filter { it.first !in alreadyResolved }.filter { it.second.size == 1 }

            onlyOnePosisble = newWithOnlyOne.firstOrNull()
            onlyOnePosisble?.let {
                alreadyResolved.add(it.first)
            }
        }

        val opcodeMap = possibles.map { it.first to it.second.single() }.toMap()

        println(opcodeMap)

        val initialRegisters = IntArray(4)

        input[1].lines().map { it.extractInts() }.forEach { (A, B, C, D) ->
            initialRegisters.applyOperation(opcodeMap[A]!!, B, C, D)
        }

        return "" + initialRegisters[0]
    }
}