package advent2019

object Intcode {
    fun String.parse() : List<Int> = this.split(",").map { it.toInt() }

    fun MutableList<Int>.getRespectingMode(mode: Int, position: Int): Int {
        return when(mode) {
            0 -> { this[this[position]] }
            1 -> { this[position] }
            else -> throw NotImplementedError("Mode ${mode} is not implemented yet.")
        }
    }

    fun MutableList<Int>.simulate() : List<Int> {
        //parameter mode
        // 0 = position mode
        // 1 = immediate mode
        //var mode = 0
        //Instruction pointer
        var ip = 0
        /*

    Opcode 3 takes a single integer as input and saves it to the address given by its only parameter. For example, the instruction 3,50 would take an input value and store it at address 50.
    Opcode 4 outputs the value of its only parameter. For example, the instruction 4,50 would output the value at address 50.

         */
        loop@while (true) {
            //TODO parameter modes
            when (this[ip] % 10) {
                1 -> {
                    this[this[ip+3]] = getRespectingMode((this[ip]/100) % 10, ip+1) + getRespectingMode((this[ip]/1000) % 10, ip+2)
                    ip += 4
                }

                2 -> {
                    this[this[ip+3]] = getRespectingMode((this[ip]/100) % 10, ip+1) * getRespectingMode((this[ip]/1000) % 10, ip+2)
                    ip += 4
                }
                3 -> {
                    //Input to this
                    println("Bitte gib mir eine Zahl:")
                    val thing : Int = readLine()!!.toInt()
                    println("I've read: $thing")
                    this[this[ip+1]] = thing
                    ip += 2
                }
                4 -> {
                    //Output this
                    println(getRespectingMode((this[ip]/100) % 10, ip+1))
                    ip += 2
                }
                5 -> {
                    if (getRespectingMode((this[ip]/100) % 10, ip+1) != 0) {
                        ip = getRespectingMode((this[ip]/1000) % 10, ip+2)
                    } else {
                        ip += 3
                    }
                }
                6 -> {
                    if (getRespectingMode((this[ip]/100) % 10, ip+1) == 0) {
                        ip = getRespectingMode((this[ip]/1000) % 10, ip+2)
                    } else {
                        ip += 3
                    }
                }
                7 -> {
                    if (getRespectingMode((this[ip]/100) % 10, ip+1) < getRespectingMode((this[ip]/1000) % 10, ip+2)) {
                        this[this[ip+3]] = 1
                    } else {
                        this[this[ip+3]] = 0
                    }
                    ip += 4
                }
                8 -> {
                    if (getRespectingMode((this[ip]/100) % 10, ip+1) == getRespectingMode((this[ip]/1000) % 10, ip+2)) {
                        this[this[ip+3]] = 1
                    } else {
                        this[this[ip+3]] = 0
                    }
                    ip += 4
                }

                99 -> break@loop

                else -> NotImplementedError("Opcode ${this[ip]} is not implemented yet!")
            }
        }

        return this
    }
}