package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException

class Day19(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 19


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
        private fun IntArray.applyOperation(instruction: Instruction) {
            val A = instruction.A
            val B = instruction.B
            val C = instruction.C

            when(instruction.op) {
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

    private data class Instruction(val op: Operation, val A: Int, val B: Int, val C: Int)

    private val input = adventOfCode.getInput(2018, 19).lines()

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
        val program = input.getInstructions()
        val toFactor = 2*2*19*11 + (program[21].B*22+program[23].B)
        return divisorSum(toFactor).toString()
    }


    override fun part2(): Any {
        val program = input.getInstructions()
        val toFactor = 2*2*19*11 + (program[21].B*22+program[23].B) + ((27*28+29)*30*14*32)
        return divisorSum(toFactor).toString()
    }

    private fun divisorSum(n: Int) : Int {
        val sqrt = isqrt(n)
        var out = 1 + n

        for (i in 2 until sqrt)
            if (n % i == 0)
                out += i + n/i

        if (n % sqrt == 0)
            out += sqrt

        return out
    }

    private fun isqrt(n: Int) : Int {
        var prev = n

        while (true) {
            val cur = (prev + n / prev) / 2
            if (cur == prev) {
                return cur
            }
            prev = cur
        }
    }
}