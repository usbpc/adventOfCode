import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day

class Day04(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 4
    val input = adventOfCode.getInput(2017, 4).lines()
    override fun part1() = input.filter {it.isValidPassphrase()}.count().toString()

    override fun part2() = input.filter {it.isStupidValidPassphrase()}.count().toString()

    private fun String.isStupidValidPassphrase() : Boolean {
        val stuff = this.split(" ").map {word ->
            val map = mutableMapOf<Char, Int>()
            word.forEach { char ->
                map.put(char, word.filter {it == char}.count())
            }
            map
        }
        stuff.forEach { word ->
            if (stuff.filter {it == word}.count() > 1) return false
        }
        return true
    }

    private fun String.isValidPassphrase() : Boolean {
        val stuff = this.split(" ")
        stuff.forEach { word ->
            if (stuff.filter { it == word}.count() > 1) return false
        }
        return true
    }
}
