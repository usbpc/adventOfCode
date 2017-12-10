import usbpc.aoc.inputgetter.AdventOfCode

class Day10(override val adventOfCode: AdventOfCode) : Day {
    val input = adventOfCode.getInput(2017, 10)

    override fun part1(): String {
        val modified = IntArray(256) {it}.knot(input.split(',').map {it.toInt()}, 1)
        return (modified[0] * modified[1]).toString()
    }

    override fun part2(): String {
        val lengths = input.toByteArray().map {it.toInt()}.toMutableList()
        lengths.addAll(listOf(17, 31, 73, 47, 23))
        val chunks = IntArray(256) {it}
                .knot(lengths, 64)
                .asIterable()
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

    private fun IntArray.knot(lengths: List<Int>, rounds: Int): IntArray {
        val list = this.copyOf()
        var currentPosition = 0
        var skipSize = 0
        var tmp: Int
        repeat (rounds) {
            for (number in lengths) {
                if (number > list.size) continue
                for (curr in 0 until number/2) {
                    tmp = list[(currentPosition + curr) % list.size]
                    list[(currentPosition + curr) % list.size] = list[(currentPosition + number - 1 - curr) % list.size]
                    list[(currentPosition + number - 1 - curr) % list.size] = tmp
                }
                currentPosition = (currentPosition + number + skipSize++) % list.size
            }
        }
        return list
    }
}