import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.collect

class Day21(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 21
    private val input = adventOfCode.getInput(2017, day).lines().associate { line -> line.split(" => ").let { it[0] to it[1] } }
    //private val grid = listOf(".#.", "..#", "###").map { it.toList() }

    override fun part1(): String {
        var grid = ".#./..#/###".inflate()
        repeat(5) {
            grid = grid.enhance()
        }
        return grid.flatMap { it }.filter { it == '#' }.count().toString()
    }

    override fun part2(): String {
        var grid = ".#./..#/###".inflate()
        repeat(18) {
            grid = grid.enhance()
        }
        return grid.flatMap { it }.filter { it == '#' }.count().toString()
    }

    /**
     * Input List has to be squared otherwies you'll get IndexOutOfBounds Exceptions
     * @return a new rotated list
     */
    private fun List<List<Char>>.rotateClockwies(): List<List<Char>> {
        val out = mutableListOf<List<Char>>()
        val length = this.size
        for (col in 0 until length) {
            val tmp = mutableListOf<Char>()
            for (row in length-1 downTo 0) {
                tmp.add(this[row][col])
            }
            out.add(tmp)
        }
        return out
    }
    private fun List<List<Char>>.enhance(): List<List<Char>> {
        val out = mutableListOf<List<Char>>()
        if (this.size % 2 == 0) {
            //cut in 2 by 2 squares
            for (i in 0 until this.size step 2) {
                val cur = MutableList<MutableList<Char>>(3){mutableListOf()}
                for (j in 0 until this.size step 2) {
                    listOf(
                            listOf(this[i][j], this[i][j+1]),
                            listOf(this[i+1][j], this[i+1][j+1])
                    ).enhancePart().let {
                        cur[0].addAll(it[0])
                        cur[1].addAll(it[1])
                        cur[2].addAll(it[2])
                    }
                }
                out.addAll(cur)
            }
        } else {
            assert(this.size % 3 == 0)
            //cut in 3 by 3 squares
            for (i in 0 until this.size step 3) {
                val cur = MutableList<MutableList<Char>>(4){mutableListOf()}
                for (j in 0 until this.size step 3) {
                    listOf(
                            listOf(this[i][j], this[i][j+1], this[i][j+2]),
                            listOf(this[i+1][j], this[i+1][j+1], this[i+1][j+2]),
                            listOf(this[i+2][j], this[i+2][j+1], this[i+2][j+2])
                    ).enhancePart().let {
                        cur[0].addAll(it[0])
                        cur[1].addAll(it[1])
                        cur[2].addAll(it[2])
                        cur[3].addAll(it[3])
                    }
                }
                out.addAll(cur)
            }
        }
        return out
    }
    private fun List<List<Char>>.enhancePart(): List<List<Char>> {
        var cur = this
        var out = input[cur.flatten()]
        if (out != null) return out.inflate()
        cur = this.flipH()
        out = input[cur.flatten()]
        if (out != null) return out.inflate()
        cur = this.flipV()
        out = input[cur.flatten()]
        if (out != null) return out.inflate()
        val rotateOnce = this.rotateClockwies()
        cur = rotateOnce
        out = input[cur.flatten()]
        if (out != null) return out.inflate()
        cur = cur.rotateClockwies()
        out = input[cur.flatten()]
        if (out != null) return out.inflate()
        cur = cur.rotateClockwies()
        out = input[cur.flatten()]
        if (out != null) return out.inflate()
        cur = rotateOnce.flipV()
        out = input[cur.flatten()]
        if (out != null) return out.inflate()
        cur = rotateOnce.flipH()
        out = input[cur.flatten()]
        if (out != null) return out.inflate()
        throw IllegalStateException("No match found!")
    }

    private fun List<List<Char>>.prettyString(): String = this
            .collect(kotlin.text.StringBuilder()) {line ->
                this.append(line.toCharArray()).append('\n')
            }.dropLast(1).toString()
    private fun List<List<Char>>.flipH(): List<List<Char>> = this.asReversed()
    private fun List<List<Char>>.flipV(): List<List<Char>> = this.map { it.asReversed() }
    private fun String.inflate(): List<List<Char>> {
        val out = mutableListOf<List<Char>>()
        this.split('/').forEach { line ->
            out.add(line.toList())
        }
        return out
    }
    private fun List<List<Char>>.flatten(): String {
        val out = StringBuilder()
        this.forEach{line ->
            out.append(line.toCharArray())
            out.append('/')
        }
        return out.deleteCharAt(out.length - 1).toString()
    }
}