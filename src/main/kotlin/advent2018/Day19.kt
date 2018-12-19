package advent2018

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode
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

    override fun part1(): String {
        val registers = IntArray(6)
        registers[0] = 1
        val instructionPointer = input.first().extractInts()[0]
        val program = input.drop(1).map {line ->
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
        var oldReg0 = registers[0]
        while(registers[instructionPointer] >= 0 && registers[instructionPointer] < program.size) {
            registers.applyOperation(program[registers[instructionPointer]])
            if (oldReg0 != registers[0]) {
                println(registers[0])
                oldReg0 = registers[0]
            }
            registers[instructionPointer]++
        }

        return "" + registers[0]
    }


    override fun part2(): String {
        /*
        val program = input.drop(1).map {line ->
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
        
        
        for (inst in program) {
            when(inst.op) {
                Operation.ADDR -> println("reg${inst.C} = reg${inst.A} + reg${inst.B}")
                Operation.ADDI -> println("reg${inst.C} = reg${inst.A} + ${inst.B}")
                Operation.MULR -> println("reg${inst.C} = reg${inst.A} * reg${inst.B}")
                Operation.MULI -> println("reg${inst.C} = reg${inst.A} * ${inst.B}")
                Operation.BANR -> println("reg${inst.C} = reg${inst.A} and reg${inst.B}")
                Operation.BANI -> println("reg${inst.C} = reg${inst.A} and${inst.B}")
                Operation.BORR -> println("reg${inst.C} = reg${inst.A} or reg${inst.B}")
                Operation.BORI -> println("reg${inst.C} = reg${inst.A} or ${inst.B}")
                Operation.SETR -> println("reg${inst.C} = reg${inst.A}")
                Operation.SETI -> println("reg${inst.C} = ${inst.A}")
                Operation.GTIR -> println("reg${inst.C} = if (${inst.A} > reg${inst.B}) 1 else 0")
                Operation.GTRI -> println("reg${inst.C} if (reg${inst.A} >${inst.B}) 1 else 0")
                Operation.GTRR -> println("reg${inst.C} if (reg${inst.A} > reg${inst.B}) 1 else 0")
                Operation.EQIR -> println("reg${inst.C} if (${inst.A} == reg${inst.B}) 1 else 0")
                Operation.EQRI -> println("reg${inst.C} if (reg${inst.A} ==${inst.B}) 1 else reg 0")
                Operation.EQRR -> println("reg${inst.C} if (reg${inst.A} == reg${inst.B}) 1 else 0")
            }
        }*/

        val reg2 = 10551377
        var ret = 0

        for (i in 1..reg2/2) {
            if (reg2 % i == 0) {
                ret += i
                println(ret)
            }
        }

        return ""
    }
}