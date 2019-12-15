package advent2019

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Queue

class Day15(override val adventOfCode: AdventOfCode) : Day {
    override val day = 15
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toLong() }

    class Tile(val type: Type, var distance: Int = Int.MAX_VALUE) {
        var north : Tile? = null
        var east : Tile? = null
        var south : Tile? = null
        var west : Tile? = null

        enum class Type {
            Wall,
            Path,
            Goal;
        }

        fun getConnectingTiles() =
                mutableListOf<Tile>().apply {
                if (north != null)
                    add(north!!)
                if (east != null)
                    add(east!!)
                if (south != null)
                    add(south!!)
                if (west != null)
                    add(west!!)
            }

        fun anyDirNull() = north == null || east == null || south == null || west == null

        fun linkNextDirection(tile: Tile) {
            when (getNextDirection()) {
                CardinalDirection.North -> {
                    north = tile
                    tile.south = this
                }
                CardinalDirection.East -> {
                    east = tile
                    tile.west = this
                }
                CardinalDirection.South -> {
                    south = tile
                    tile.north = this
                }
                CardinalDirection.West -> {
                    west = tile
                    tile.east = this
                }
            }
        }

        fun getNextDirection() : CardinalDirection {
            when {
                north == null -> return CardinalDirection.North
                east == null -> return CardinalDirection.East
                south == null -> return CardinalDirection.South
                west == null -> return CardinalDirection.West
            }

            when (listOf(north!!.distance, east!!.distance, south!!.distance, west!!.distance).withIndex().minBy { it.value }!!.index) {
                0 -> return CardinalDirection.North
                1 -> return CardinalDirection.East
                2 -> return CardinalDirection.South
                3 -> return CardinalDirection.West
            }

            throw IllegalStateException("This can't happen! I'm stuck in a 1x1 square! SEND HELP!!!!!")
        }
    }

    enum class CardinalDirection {
        North,
        East,
        South,
        West;
    }

    fun generateMap() = runBlocking {
        val inCh = Channel<Long>()
        val outCh = Channel<Long>()

        val tileMap = mutableMapOf<Point, Tile>()

        var curPos = Point(0, 0)
        var curTile = Tile(Tile.Type.Path, 0)

        tileMap[curPos] = curTile

        val vm = Intcode(input.toMutableList(), inCh, outCh)

        val vmRunner = launch { vm.simulate() }

        while (tileMap.values.filter { it.type != Tile.Type.Wall }.any{ it.anyDirNull() }) {
            val (nextPos, cmd) = when (curTile.getNextDirection()) {
                CardinalDirection.North -> Pair(curPos.plus(Point(0, -1)), 1L)
                CardinalDirection.South -> Pair(curPos.plus(Point(0, 1)), 2L)
                CardinalDirection.West -> Pair(curPos.plus(Point(-1, 0)), 3L)
                CardinalDirection.East -> Pair(curPos.plus(Point(1, 0)), 4L)
            }

            inCh.send(cmd)
            val lastResponse = outCh.receive()

            if (nextPos !in tileMap) {
                val tile = when (lastResponse) {
                    0L -> Tile(Tile.Type.Wall, Int.MAX_VALUE)
                    1L -> Tile(Tile.Type.Path, curTile.distance + 1)
                    2L -> Tile(Tile.Type.Goal, curTile.distance + 1)
                    else -> throw IllegalStateException("I got a $lastResponse don't know what that is supposed to mean.")
                }
                tileMap[nextPos] = tile
            }

            val nextTile = tileMap[nextPos]!!

            curTile.linkNextDirection(nextTile)

            if (nextTile.type != Tile.Type.Wall) {
                curPos = nextPos
                curTile = nextTile
            }
        }

        vmRunner.cancel()
        vmRunner.join()

        val maxY = tileMap.keys.maxBy { it.y }!!.y
        val minY = tileMap.keys.minBy { it.y }!!.y
        val maxX = tileMap.keys.maxBy { it.x }!!.x
        val minX = tileMap.keys.minBy { it.x }!!.x

        buildString {
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    when (val cur = Point(x, y)) {
                        curPos -> {
                            append('D')
                        }
                        in tileMap -> {
                            when (tileMap[cur]!!.type) {
                                Tile.Type.Wall -> append('#')
                                Tile.Type.Path -> append('.')
                                Tile.Type.Goal -> append('X')
                            }
                        }
                        else -> {
                            append(' ')
                        }
                    }
                }
                append('\n')
            }
        }

        tileMap[Point(0, 0)]!! to tileMap.values.single { it.type == Tile.Type.Goal }
    }

    override fun part1() : Any {
        val map = generateMap().first

        val queue = Queue<Pair<Tile, Int>>()

        val visited = mutableSetOf<Tile>()
        queue.add(map to 0)

        var curTile : Tile
        var curDistance : Int

        do {
            val (tile, distance) = queue.remove()
            curTile = tile
            curDistance = distance

            visited.add(curTile)

            curTile.getConnectingTiles()
                    .filter { it.type != Tile.Type.Wall }
                    .filter { it !in visited }
                    .forEach { queue.add(it to curDistance + 1) }
        } while (curTile.type != Tile.Type.Goal)

        return curDistance
    }

    override fun part2() : Any {
        val end = generateMap().second

        val queue = Queue<Pair<Tile, Int>>()

        val visited = mutableSetOf<Tile>()
        queue.add(end to 0)

        var curTile : Tile
        var curDistance : Int

        do {
            val (tile, distance) = queue.remove()
            curTile = tile
            curDistance = distance

            visited.add(curTile)

            curTile.getConnectingTiles()
                    .filter { it.type != Tile.Type.Wall }
                    .filter { it !in visited }
                    .forEach { queue.add(it to curDistance + 1) }
        } while (queue.isNotEmpty())

        return curDistance
    }
}
