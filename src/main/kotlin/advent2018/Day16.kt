package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*

class Day16(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 16
    private val input = adventOfCode.getInput(2018, day).split("\n\n\n\n")
    
    companion object {
        @Suppress("SpellCheckingInspection")
        private enum class Operation {
            ADDR, ADDI,
            MULR, MULI,
            BANR, BANI,
            BORR, BORI,
            SETR, SETI,
            GTIR, GTRI, GTRR,
            EQIR, EQRI, EQRR
        }
        
        @JvmStatic
        private fun IntArray.applyOperation(op: Operation, A: Int, B: Int, C: Int) {
            when(op) {
                Operation.ADDR -> this[C] = this[A] + this[B]
                Operation.ADDI -> this[C] = this[A] + B
                Operation.MULR -> this[C] = this[A] * this[B]
                Operation.MULI -> this[C] = this[A] * B
                Operation.BANR -> this[C] = this[A] and this[B]
                Operation.BANI -> this[C] = this[A] and B
                Operation.BORR -> this[C] = this[A] or this[B]
                Operation.BORI -> this[C] = this[A] or B
                Operation.SETR -> this[C] = this[A]
                Operation.SETI -> this[C] = A
                Operation.GTIR -> if (A > this[B]) this[C] = 1 else this[C] = 0
                Operation.GTRI -> if (this[A] > B) this[C] = 1 else this[C] = 0
                Operation.GTRR -> if (this[A] > this[B]) this[C] = 1 else this[C] = 0
                Operation.EQIR -> if (A == this[B]) this[C] = 1 else this[C] = 0
                Operation.EQRI -> if (this[A] == B) this[C] = 1 else this[C] = 0
                Operation.EQRR -> if (this[A] == this[B]) this[C] = 1 else this[C] = 0
            }
        }
    }
    private class TestCase(val preRegisters : IntArray, val inst: IntArray, val postRegisters : IntArray) {
        val possibleInstructions = mutableSetOf<Operation>()
        fun testAllOpcodes() {
            for (op in Operation.values()) {
                val applied = preRegisters.copyOf().apply {
                    applyOperation(op, inst[1], inst[2], inst[3])
                }

                if (Arrays.equals(postRegisters, applied))
                    possibleInstructions.add(op)

            }
        }
    }

    private fun String.getTestCases() = this.split("\n\n").map { case ->
            val lines = case.split('\n')
            TestCase(lines[0].extractInts(), lines[1].extractInts(), lines[2].extractInts())
        }

    

    override fun part1(): Any =
            input[0].getTestCases()
                    .onEach { testCase ->  testCase.testAllOpcodes() }
                    .count { it.possibleInstructions.size >= 3 }
                    .toString()


    override fun part2(): Any {
        val testCases = input[0].getTestCases()
                .onEach { testCase -> testCase.testAllOpcodes() }

        val possibles = testCases
                .groupBy { it.inst[0] }
                .map { (opcode, list) ->
                    val base = list.first().possibleInstructions
                    opcode to list.drop(1).fold(base as Set<Operation>) { acc, testCase -> acc.intersect(testCase.possibleInstructions) }.toMutableSet()
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


        val initialRegisters = IntArray(4)

        input[1].lines().map { it.extractInts() }.forEach { (A, B, C, D) ->
            initialRegisters.applyOperation(opcodeMap[A]!!, B, C, D)
        }

        return "" + initialRegisters[0]
    }
}