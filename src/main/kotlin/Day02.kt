import java.io.File

fun main(args: Array<String>) {
    val input: MutableList<List<Long>> = mutableListOf()
    File("resources/day02.txt").bufferedReader().useLines {lines ->
        lines.forEach {line ->
            val row = mutableListOf<Long>()
            line.splitToSequence("\t").forEach {number ->
                row.add(number.toLong())
            }
            input.add(row)
        }
    }
    println("Part 1: ${part1(input)}")
    println("Part 2: ${part2(input)}")
}

fun part2(spreadsheet: List<List<Long>>): Long {
    var output = 0L
    spreadsheet.forEach {row ->
        row.forEach {cur ->
            row.filter {it != cur && it % cur == 0L}.forEach {output += it/cur}}
    }
    return output
}

fun part1(spreadsheet: List<List<Long>>): Long {
    var checksum = 0L
    spreadsheet.forEach {row ->
        var largest = Long.MIN_VALUE
        var smallest = Long.MAX_VALUE
        row.forEach {
            if (it > largest)
                largest = it
            if (it < smallest)
                smallest = it
        }
        checksum += largest - smallest
    }
    return checksum
}