import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Direction

class Day19(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 19
    private val input = adventOfCode.getInput(2017, day).split('\n').map {it.toCharArray()}
    override fun part1(): String {
        val builder = StringBuilder()
        runTroughMaze {if (it.isLetter()) builder.append(it)}
        return builder.toString()
    }

    override fun part2(): String {
        var counter = -1
        runTroughMaze {counter++}
        return counter.toString()
    }

    private inline fun runTroughMaze(collector: (Char) -> Unit) {
        var row = 0
        var col = input.first().withIndex().find {(_, c) -> c == '|'}?.index ?:
                throw IllegalStateException("No entrance")
        var dir = Direction.DOWN
        var symbol: Char
        do {
            symbol = input[row][col]
            //println("Position: row: ${row.toString().padStart(3, '0')} col: ${col.toString().padStart(3, '0')} char: $symbol")
            if (symbol == '+') {
                when (dir) {
                    Direction.UP -> {
                        if (col + 1 < input.first().size && input[row][col + 1].isValidDirection()) dir = Direction.RIGHT
                        if (col > 0 && input[row][col - 1].isValidDirection()) dir = Direction.LEFT
                    }
                    Direction.DOWN -> {
                        if (col + 1 < input.first().size && input[row][col + 1].isValidDirection()) dir = Direction.RIGHT
                        if (col > 0 && input[row][col - 1].isValidDirection()) dir = Direction.LEFT
                    }
                    Direction.LEFT -> {
                        if (row + 1 < input.size && input[row + 1][col].isValidDirection()) dir = Direction.DOWN
                        if (row - 1 >= 0 && input[row - 1][col].isValidDirection()) dir = Direction.UP
                    }
                    Direction.RIGHT -> {
                        if (row + 1 < input.size && input[row + 1][col].isValidDirection()) dir = Direction.DOWN
                        if (row - 1 >= 0 && input[row - 1][col].isValidDirection()) dir = Direction.UP
                    }
                }
            }

            collector(symbol)
            when (dir) {
                Direction.UP -> row--
                Direction.DOWN -> row++
                Direction.LEFT -> col--
                Direction.RIGHT -> col++
            }
        } while (symbol != ' ')
    }

    private fun Char.isValidDirection(): Boolean {
        if (this == '=') return false
        if (this.isLetter()) return true
        if (this == '|') return true
        if (this == '-') return true
        return false
    }
}