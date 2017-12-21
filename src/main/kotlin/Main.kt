import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val adventOfCode = AdventOfCode(args[0])
    val list = mutableListOf<Day>().apply {
        /*add(Day01(adventOfCode))
        add(Day02(adventOfCode))
        add(Day03(adventOfCode))
        add(Day04(adventOfCode))
        add(Day05(adventOfCode))
        add(Day06(adventOfCode))
        add(Day07(adventOfCode))
        add(Day08(adventOfCode))
        add(Day09(adventOfCode))
        add(Day10(adventOfCode))
        add(Day11(adventOfCode))
        add(Day12(adventOfCode))
        add(Day13(adventOfCode))
        add(Day14(adventOfCode))
        add(Day15(adventOfCode))
        add(Day16(adventOfCode))
        add(Day17(adventOfCode))
        add(Day18(adventOfCode))
        add(Day19(adventOfCode))
        add(Day20(adventOfCode))*/
        add(Day21(adventOfCode))
    }
    if (args.isEmpty()) {
        println("Please specify session id!")
        return
    }
    list.forEach {day ->
        val stringBuilder = StringBuilder()
        stringBuilder.appendln("---------------- Day ${day.day.toString().padStart(2, '0')} ----------------")
        val first = measureTimeMillis {stringBuilder.appendln("Part 1: ${day.part1()}")}
        val second = measureTimeMillis {stringBuilder.appendln("Part 2: ${day.part2()}")}
        stringBuilder.appendln("Part 1 took ${first}ms, Part 2 took ${second}ms")
        print(stringBuilder.toString())
    }

}