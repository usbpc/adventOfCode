import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.collect

class Day10(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 10
    val input = adventOfCode.getInput(2017, 10)

    override fun part1(): String {
        val modified = IntArray(256) {it}.knot(input.split(',').map {it.toInt()}, 1)
        return (modified[0] * modified[1]).toString()
    }

    override fun part2(): String {
        return input.knotDenseHash()
                .map {it.toString(16).padStart(2, '0')}.collect(StringBuilder(), StringBuilder::append).toString()
    }


}

fun String.knotDenseHash() = IntArray(256){it}.knot(this.toByteArray().map {it.toInt()} + listOf(17, 31, 73, 47, 23), 64)
        .asIterable()
        .chunked(16)
        .map {it.fold(0) {acc, curr -> acc xor curr}}


fun IntArray.knot(lengths: List<Int>, rounds: Int): IntArray {
    val list = this.copyOf()
    var currentPosition = 0
    var skipSize = 0
    var tmp: Int
    repeat(rounds) {
        for (number in lengths) {
            if (number > list.size) continue
            for (curr in 0 until number / 2) {
                tmp = list[(currentPosition + curr) % list.size]
                list[(currentPosition + curr) % list.size] = list[(currentPosition + number - 1 - curr) % list.size]
                list[(currentPosition + number - 1 - curr) % list.size] = tmp
            }
            currentPosition = (currentPosition + number + skipSize++) % list.size
        }
    }
    return list
}
