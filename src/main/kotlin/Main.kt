import usbpc.aoc.inputgetter.AdventOfCode

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Please specify session id!")
        return
    }
    val adventOfCode = AdventOfCode(args[0])
    val day = Day10(adventOfCode)
    println("""
        |Part 1: ${day.part1()}
        |Part 2: ${day.part2()}
    """.trimMargin())
}

class Y2015D11(override val adventOfCode: AdventOfCode) : Day {
    var part1: String? = null
    var part2: String? = null
    override fun part1(): String {
        if (part1 == null) {
            solve()
        }
        return part1.toString()
    }

    override fun part2(): String {
        if (part2 == null) {
            solve()
        }
        return part2.toString()
    }

    private fun solve() {
        val oldPw = adventOfCode.getInput(2015, 11)
        var newPw = oldPw
        while (!newPw.isValidPassword()) {
            newPw = newPw.inc()
        }
        part1 = newPw
        newPw = newPw.inc()
        while (!newPw.isValidPassword()) {
            newPw = newPw.inc()
        }
        part2 = newPw
    }
    private fun String.inc(): String {
        var tmp = this.reversed().toCharArray()
        for (index in 0 until tmp.size) {
            tmp[index]++
            if (tmp[index] == 'z' + 1) {
                tmp[index] = 'a'
            } else {
                break
            }
        }
        return String(tmp).reversed()
    }
    private val regex = Regex("([a-z])\\1[a-z]*([a-z])\\2")
    private fun String.isValidPassword(): Boolean {
        if (this.contains("(i|o|l)"))
            return false
        if (!regex.containsMatchIn(this))
            return false
        if (this.withIndex()
                .drop(2).none {(index, char) ->
            this[index - 2] == char - 2 && this[index - 1] == char - 1
        })
            return false
        return true
    }
}