package advent2019

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day13(override val adventOfCode: AdventOfCode) : Day {
    override val day = 13
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toLong() }

    override fun part1() : Any = runBlocking {
        val inCh = Channel<Long>()
        val outCh = Channel<Long>()

        val vm = Intcode(input.toMutableList(), inCh, outCh)

        val vmRunner = launch { vm.simulate() }

        val thing = mutableSetOf<Point>()

        while (!outCh.isClosedForReceive) {
            try {
                val x = outCh.receive()
                val y = outCh.receive()
                val tileId = outCh.receive()

                if (tileId == 2L) {
                    thing.add(Point(x.toInt(), y.toInt()))
                } else {
                    thing.remove(Point(x.toInt(), y.toInt()))
                }
            } catch (ex: ClosedReceiveChannelException) {

            }
        }

        vmRunner.join()

        thing.size
    }

    enum class Tile(val char: Char) {
        Empty(' '),
        Wall('W'),
        Block('#'),
        Paddle('_'),
        Ball('*');

        companion object {
            fun fromLong(num: Long) : Tile {
                return when (num) {
                    0L -> Empty
                    1L -> Wall
                    2L -> Block
                    3L -> Paddle
                    4L -> Ball
                    else -> throw IllegalStateException("This can't happen! $num")
                }
            }
        }
    }

    override fun part2() : Any = runBlocking {
        val inCh = Channel<Long>()
        val outCh = Channel<Long>()
        val indicatorCh = Channel<Unit>()

        val vm = Intcode(input.toMutableList().apply { this[0] = 2L }, inCh, outCh, requestInput = indicatorCh, blockOnInputRequest = true)

        val vmRunner = launch { vm.simulate() }

        val thing = mutableMapOf<Point, Tile>()

        var score = 0L

        while (!outCh.isClosedForReceive) {
            try {
                val x = select<Long> {
                    outCh.onReceive { value ->
                        value
                    }
                    indicatorCh.onReceive { _ ->
                        //Print state.
                        val maxX = thing.keys.maxBy { it.x }!!.x
                        val maxY = thing.keys.maxBy { it.y }!!.y

                        var ballX = 0
                        var paddleX = 0

                        val str = buildString {
                            for (y in 0..maxY) {
                                for (x in 0..maxX) {
                                    val cur = Point(x, y)

                                    val curTile = thing[cur] ?: Tile.Empty

                                    if (curTile == Tile.Ball) {
                                        ballX = x
                                    }

                                    if (curTile == Tile.Paddle)
                                        paddleX = x

                                    append(curTile.char)
                                }
                                append('\n')
                            }
                        }

                        print(str)
                        //Send input

                        val send = if (paddleX > ballX)
                            -1L
                        else if (paddleX < ballX)
                            1L
                        else
                            0L

                        inCh.send(send)

                        Long.MIN_VALUE
                    }
                }

                if (x == Long.MIN_VALUE)
                    continue

                val y = outCh.receive()
                val tileId = outCh.receive()

                if (x > -1L) {
                    val curPosition = Point(x.toInt(), y.toInt())
                    val curTile = Tile.fromLong(tileId)

                    thing[curPosition] = curTile
                } else {
                    score = tileId
                }
            } catch (ex: ClosedReceiveChannelException) {

            }
        }

        vmRunner.join()

        score
    }
}
