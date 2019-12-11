package advent2019

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day11(override val adventOfCode: AdventOfCode) : Day {
    override val day = 11
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toLong() }

    enum class Colour {
        Black,
        White;

        companion object {
            fun fromLong(long: Long) : Colour {
                return when (long) {
                    0L -> Black
                    1L -> White
                    else -> throw IllegalStateException("This can't happen!")
                }
            }
        }

        fun toLong() : Long {
            return when (this) {
                Black -> 0L
                White -> 1L
            }
        }

        fun toChar() : Char {
            return when (this) {
                Black -> '░'
                White -> '█'
            }
        }
    }

    fun Point.turnRight() : Point {
        return when (this) {
            Point(0 ,-1) -> Point(1, 0)
            Point(1 ,0) -> Point(0, 1)
            Point(0 ,1) -> Point(-1, 0)
            Point(-1 ,0) -> Point(0, -1)
            else -> throw IllegalStateException("$this not a valid direction")
        }
    }

    fun Point.turnLeft() : Point {
        return when (this) {
            Point(0 ,-1) -> Point(-1, 0)
            Point(1 ,0) -> Point(0, -1)
            Point(0 ,1) -> Point(1, 0)
            Point(-1 ,0) -> Point(0, 1)
            else -> throw IllegalStateException("$this not a valid direction")
        }
    }

    override fun part1() : Any = runBlocking {
        val ret = mutableMapOf<Point, Colour>()

        var dir = Point(0, -1)
        var curPos = Point(0, 0)

        ret[curPos] = Colour.White

        val inCh = Channel<Long>()
        val outCh = Channel<Long>()

        val vm = Intcode(input.toMutableList(), inCh, outCh)

        val vmRunner = launch { vm.simulate() }

        while (!outCh.isClosedForReceive) {

            ret.getOrDefault(curPos, Colour.Black).let {
                inCh.send(it.toLong())
            }

            try {
                val newColour = Colour.fromLong(outCh.receive())

                ret[curPos] = newColour

                val turnDir = outCh.receive()

                when (turnDir) {
                    0L -> dir = dir.turnLeft()
                    1L -> dir = dir.turnRight()
                    else -> throw IllegalStateException("Don't know what $turnDir is supposed to make me turn")
                }

                curPos += dir
            } catch (e: ClosedReceiveChannelException) {
                println("Channel closed for receiving!")
                continue
            }

        }

        vmRunner.join()

        vm

        val maxX = ret.keys.maxBy { it.x }!!.x
        val minX = ret.keys.minBy { it.x }!!.x
        val maxY = ret.keys.maxBy { it.y }!!.y
        val minY = ret.keys.minBy { it.y }!!.y

        buildString {
            this.append('\n')
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    val cur = Point(x, y)
                    if (ret.containsKey(cur)) {
                        ret[cur]?.let {
                            this.append(it.toChar())
                        }
                    } else {
                        this.append(' ')
                    }
                }
                this.append('\n')
            }
        }
    }

    override fun part2() : Any {
        return ""
    }
}
