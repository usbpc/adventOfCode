import usbpc.aoc.inputgetter.AdventOfCode

class Day10(override val adventOfCode: AdventOfCode) : Day {
    val input = adventOfCode.getInput(2017, 10)

    override fun part1(): String {
        val modified = List(256) {it}.knot(input.split(',').map {it.toInt()}, 1)
        return (modified[0] * modified[1]).toString()
    }

    override fun part2(): String {
        val lengths = input.toByteArray().map {it.toInt()}.toMutableList()
        lengths.addAll(listOf(17, 31, 73, 47, 23))
        val chunks = List(256) {it}
                .knot(lengths, 64)
                .chunked(16)
        val output = StringBuilder()
        chunks.forEach {chunk ->
            var xored = 0
            chunk.forEach {number ->
                xored = xored xor number
            }

            output.append(xored.toString(16).padStart(2, '0'))
        }
        return output.toString()
    }

    private fun List<Int>.knot(lengths: List<Int>, rounds: Int): List<Int> {
        val list = this.toMutableList()
        var currentPosition = 0
        var skipSize = 0
        for (counter in 1..rounds) {
            for (number in lengths) {
                if (number > list.size) continue

                val reversedPart = List(number) {list[(currentPosition + it) % list.size]}.reversed()
                reversedPart.forEachIndexed {index, thing ->
                    list[(currentPosition + index) % list.size] = thing
                }
                currentPosition = (currentPosition + number + skipSize++) % list.size
            }
        }
        return list
    }
}