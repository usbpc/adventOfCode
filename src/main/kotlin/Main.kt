import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

fun main(args: Array<String>) {
    val adventOfCode = AdventOfCode(args[0])
    val list = mutableListOf<Day>().apply {
        add(Day01(adventOfCode))
        add(Day02(adventOfCode))
        add(Day03(adventOfCode))
        add(Day04(adventOfCode))
        add(Day05(adventOfCode))
        add(Day06(adventOfCode))
        add(Day07(adventOfCode))
        add(Day08(adventOfCode))
        add(Day09(adventOfCode))
        add(Day10(adventOfCode))
    }
    if (args.isEmpty()) {
        println("Please specify session id!")
        return
    }

    list.forEach {day ->
        println("""-------------- Day ${day.day.toString().padStart(2, '0')} --------------
        |Part 1: ${day.part1()}
        |Part 2: ${day.part2()}
    """.trimMargin())
    }

}