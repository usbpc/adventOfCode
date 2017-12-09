import usbpc.aoc.inputgetter.AdventOfCode

fun main(args: Array<String>) {
    if (args.isEmpty()) {
        println("Please specify session id!")
        return
    }
    val adventOfcode = AdventOfCode(args[0])
    val day = Day09(adventOfcode)
    println("""
        |Part 1: ${day.part1()}
        |Part 2: ${day.part2()}
    """.trimMargin())
}