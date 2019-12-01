package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day21(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 21

    private val input = adventOfCode.getInput(2018, day).lines()

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
        private fun LongArray.applyOperation(instruction: Instruction) {
            val A = instruction.A
            val B = instruction.B
            val C = instruction.C

            when(instruction.op) {
                Operation.ADDR -> this[C] = this[A] + this[B]
                Operation.ADDI -> this[C] = this[A] + B
                Operation.MULR -> this[C] = this[A] * this[B]
                Operation.MULI -> this[C] = this[A] * B
                Operation.BANR -> this[C] = this[A] and this[B]
                Operation.BANI -> this[C] = this[A] and B.toLong()
                Operation.BORR -> this[C] = this[A] or this[B]
                Operation.BORI -> this[C] = this[A] or B.toLong()
                Operation.SETR -> this[C] = this[A]
                Operation.SETI -> this[C] = A.toLong()
                Operation.GTIR -> if (A > this[B]) this[C] = 1 else this[C] = 0
                Operation.GTRI -> if (this[A] > B) this[C] = 1 else this[C] = 0
                Operation.GTRR -> if (this[A] > this[B]) this[C] = 1 else this[C] = 0
                Operation.EQIR -> if (A.toLong() == this[B]) this[C] = 1 else this[C] = 0
                Operation.EQRI -> if (this[A] == B.toLong()) this[C] = 1 else this[C] = 0
                Operation.EQRR -> if (this[A] == this[B]) this[C] = 1 else this[C] = 0
            }
        }
    }

    private data class Instruction(val op: Operation, val A: Int, val B: Int, val C: Int)

    private fun List<String>.getInstructions() =
            this.drop(1).map {line ->
                val numbers = line.extractInts()
                val op = when(line.substring(0..3)) {
                    "addr" -> Operation.ADDR
                    "addi" -> Operation.ADDI
                    "mulr" -> Operation.MULR
                    "muli" -> Operation.MULI
                    "banr" -> Operation.BANR
                    "bani" -> Operation.BANI
                    "borr" -> Operation.BORR
                    "bori" -> Operation.BORI
                    "setr" -> Operation.SETR
                    "seti" -> Operation.SETI
                    "gtir" -> Operation.GTIR
                    "gtri" -> Operation.GTRI
                    "gtrr" -> Operation.GTRR
                    "eqir" -> Operation.EQIR
                    "eqri" -> Operation.EQRI
                    "eqrr" -> Operation.EQRR
                    else -> throw IllegalStateException("How did we get ${line.substring(0..3)}?")
                }
                Instruction(op, numbers[0], numbers[1], numbers[2])
            }

    override fun part1(): Any {
        val inst = input[0].extractInts()[0]
        val registers = LongArray(6)
        val program = input.getInstructions()
        val cmpReg = program[28].A

        while (true) {
            if (registers[inst] == 28L) {
                return "${registers[cmpReg]}"
            }
            registers.applyOperation(program[registers[inst].toInt()])
            registers[inst]++
        }
    }

    override fun part2(): Any {
        val inst = input[0].extractInts()[0]
        val registers = LongArray(6)
        val program = input.getInstructions()
        val cmpReg = program[28].A
        var prev = 0L

        val seen = mutableSetOf<Long>()

        while (true) {
            if (registers[inst] == 28L) {
                if(!seen.add(registers[cmpReg]))
                    return "$prev"
                prev = registers[cmpReg]
            }
            registers.applyOperation(program[registers[inst].toInt()])
            registers[inst]++
        }
    }
}