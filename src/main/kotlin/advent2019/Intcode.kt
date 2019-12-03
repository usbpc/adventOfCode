package advent2019

object Intcode {
    fun String.parse() : List<Int> = this.split(",").map { it.toInt() }

    fun MutableList<Int>.simulate() : List<Int> {
        //Instruction pointer
        var ip = 0

        loop@while (true) {
            when (this[ip]) {
                1 -> {
                    this[this[ip+3]] = this[this[ip+1]] + this[this[ip+2]]
                    ip += 4
                }

                2 -> {
                    this[this[ip+3]] = this[this[ip+1]] * this[this[ip+2]]
                    ip += 4
                }

                99 -> break@loop

                else -> NotImplementedError("Opcode ${this[ip]} is not implemented yet!")
            }
        }

        return this
    }
}