package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import java.lang.StringBuilder

fun MutableList<Int>.runSimple() : List<Long> = runBlocking {
    val vm = Intcode(this@runSimple.map { it.toLong() }.toMutableList())
    val vmRunner = launch { vm.simulate() }
    vmRunner.join()
    vm.state.toList()
}

fun MutableList<Int>.runWithInput(`in`: List<Int>) = runBlocking {
    val input = Channel<Long>()
    val output = Channel<Long>()

    val vm = Intcode(this@runWithInput.map { it.toLong() }.toMutableList(), input, output)

    val vmRunner = launch { vm.simulate() }

    for (x in `in`)
        input.send(x.toLong())
    input.close()

    for (x in output) {}

    vmRunner.join()

    vm
}

fun MutableList<Long>.runWithLongInput(`in`: List<Long>) = runBlocking {
    val input = Channel<Long>()
    val output = Channel<Long>()

    val vm = Intcode(this@runWithLongInput, input, output)

    val vmRunner = launch { vm.simulate() }

    for (x in `in`)
        input.send(x)
    input.close()

    for (x in output) {}

    vmRunner.join()

    vm
}

//TODO make the channels mock channels that are actually closed!
class Intcode(val state : MutableList<Long>, val input: ReceiveChannel<Long> = Channel(), val output: SendChannel<Long> = Channel(), val name : String = "", val debug: Boolean = false) {
    val out = mutableListOf<Long>()
    var ip: Int = 0
    var relativeBase = 0

    fun getIndex(offset: Int) : Int {
        var base = 10
        repeat(offset) { base *= 10 }
        return when(val mode = ((state[ip] / base) % 10).toInt()) {
            0 -> state[ip+offset].toInt()
            1 -> ip+offset
            2 -> relativeBase + state[ip+offset].toInt()
            else -> throw IllegalStateException("$mode is not a valid mode!")
        }
    }

    fun Int.read() : Long = if (this < state.size) state[this] else 0
    fun Int.write(value: Long) {
        while (this >= state.size)
            state.add(0)
        state[this] = value
    }

    enum class OperationResult {
        Halt,
        IncrementIP,
        Continue
    }

    enum class Instruction(val size: Int) {
        Add(4),
        Multiply(4),
        Read(2),
        Write(2),
        JumpNotZero(3),
        JumpIfZero(3),
        LessThan(4),
        Equals(4),
        ChangeRelativeBase(2),
        StopExecution(1);

        override fun toString(): String {
            return when (this) {
                Add -> "ADD"
                Multiply -> "MUL"
                Read -> "READ"
                Write -> "WRITE"
                JumpNotZero -> "JNZ"
                JumpIfZero -> "JIZ"
                LessThan -> "LZ"
                Equals -> "EQ"
                ChangeRelativeBase -> "CRB"
                StopExecution -> "HALT"
            }
        }

        companion object {
            fun fromIntcode(inst: Long): Instruction {
                val pure = (inst % 100).toInt()
                return when (pure) {
                    1 -> Add
                    2 -> Multiply
                    3 -> Read
                    4 -> Write
                    5 -> JumpNotZero
                    6 -> JumpIfZero
                    7 -> LessThan
                    8 -> Equals
                    9 -> ChangeRelativeBase
                    99 -> StopExecution
                    else -> throw IllegalStateException("$pure is not a valid instruction!")
                }
            }
        }
    }

    suspend fun simulate() {
        while(step()) {}
    }

    suspend fun step() : Boolean {
        val instruction = Instruction.fromIntcode(ip.read())

        if (debug)
            println(instruction.debugString())

        val res = instruction.execute()

        if (res == OperationResult.IncrementIP)
            ip += instruction.size

        return res != OperationResult.Halt
    }

    private fun Instruction.debugString() : String {
        val builder = StringBuilder()

        builder.apply {
            append("-----------------------------------------------------------------------\n")
            append("Instruction Pointer: ").append(ip)
            append("\nRelative Base: ").append(relativeBase)
            append("\nCurrent Instruction: ").append(this@debugString).append('\n')
        }

        when (this) {
            Instruction.Add -> builder.append('[').append(getIndex(3)).append("] = ").append(getIndex(1).read()).append(" + ").append(getIndex(2).read())
            Instruction.Multiply -> builder.append('[').append(getIndex(3)).append("] = ").append(getIndex(1).read()).append(" * ").append(getIndex(2).read())
            Instruction.Read -> builder.append('[').append(getIndex(1)).append("] = INPUT")
            Instruction.Write -> builder.append("OUTPUT = ").append(getIndex(1).read())
            Instruction.JumpNotZero -> builder.append("JUMP TO ").append(getIndex(2).read()).append(" IF ").append(getIndex(1).read()).append(" NOT ZERO")
            Instruction.JumpIfZero -> builder.append("JUMP TO ").append(getIndex(2).read()).append(" IF ").append(getIndex(1).read()).append(" IS ZERO")
            Instruction.LessThan -> builder.append('[').append(getIndex(3)).append("] = ").append(getIndex(1).read()).append(" < ").append(getIndex(2).read())
            Instruction.Equals -> builder.append('[').append(getIndex(3)).append("] = ").append(getIndex(1).read()).append(" == ").append(getIndex(2).read())
            Instruction.ChangeRelativeBase -> builder.append("REALTIVEBASE += ").append(getIndex(1).read())
            Instruction.StopExecution -> builder.append("STOP EXECUTION")
        }

        return builder.toString()
    }

    private suspend fun Instruction.execute() : OperationResult {
        return when (this) {
            Instruction.Add -> {
                getIndex(3).write(getIndex(1).read() + getIndex(2).read())
                OperationResult.IncrementIP
            }
            Instruction.Multiply -> {
                getIndex(3).write(getIndex(1).read() * getIndex(2).read())
                OperationResult.IncrementIP
            }
            Instruction.Read -> {
                getIndex(1).write(input.receive())
                OperationResult.IncrementIP
            }
            Instruction.Write -> {
                getIndex(1).read().let {
                    output.send(it)
                    out.add(it)
                }
                OperationResult.IncrementIP
            }
            Instruction.JumpNotZero -> {
                if (getIndex(1).read() != 0L) {
                    ip = getIndex(2).read().toInt()
                    OperationResult.Continue
                }
                else {
                    OperationResult.IncrementIP
                }
            }
            Instruction.JumpIfZero -> {
                if (getIndex(1).read() == 0L) {
                    ip = getIndex(2).read().toInt()
                    OperationResult.Continue
                }
                else {
                    OperationResult.IncrementIP
                }
            }
            Instruction.LessThan -> {
                val res = if (getIndex(1).read() < getIndex(2).read())
                    1L
                else
                    0L
                getIndex(3).write(res)
                OperationResult.IncrementIP
            }
            Instruction.Equals -> {
                val res = if (getIndex(1).read() == getIndex(2).read())
                    1L
                else
                    0L
                getIndex(3).write(res)
                OperationResult.IncrementIP
            }
            Instruction.ChangeRelativeBase -> {
                relativeBase += getIndex(1).read().toInt()
                OperationResult.IncrementIP
            }
            Instruction.StopExecution -> {
                output.close()
                OperationResult.Halt
            }
        }
    }
}