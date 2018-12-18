package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException
import java.util.*

class Day17(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 17
    private val input = adventOfCode.getInput(2018, day).lines()

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
        companion object {
            fun fromRange(xes: IntRange, y: Int) = xes.map { x -> Point(x, y) }
            fun fromRange(x: Int, yes: IntRange) = yes.map { y -> Point(x, y) }
        }
        override fun compareTo(other: Point): Int =
                if (this.y == other.y) {
                    this.x - other.x
                } else {
                    this.y - other.y
                }
        fun adjacent() = listOf(
                Point(x-1, y),
                Point(x+1, y)
        )

        fun down() = Point(x, y+1)
        fun up() = Point(x, y-1)
    }

    private fun Array<IntArray>.getGroundString() : String {
        val out = StringBuilder()
        for (line in this) {
            for (square in line) {
                when (square) {
                    Int.MIN_VALUE -> out.append('#')
                    0 -> out.append('.')
                    1 -> out.append('~')
                    2 -> out.append('|')
                }
            }
            out.append('\n')
        }
        return out.toString()
    }

    private fun generateWaterMap(all: Set<Point>) : Array<IntArray> {
        val maxX = all.maxBy { it.x }!!.x
        val minX = all.minBy { it.x }!!.x
        val maxY = all.maxBy { it.y }!!.y


        val ground = Array(maxY+1) { IntArray(maxX - minX + 3) }


        val spring = Point(500 - minX + 1, 0)
        for (y in 0..maxY) {
            for (x in minX..maxX) {
                if (Point(x, y) in all)
                    ground[y][x - minX + 1] = Int.MIN_VALUE
            }
        }
        //println(ground.getGroundString())
        //println(ground.getGroundString())
        val queue = Stack<Point>()
        queue.add(spring.down())

        while (queue.isNotEmpty()) {
            val cur = queue.pop()
            if (cur.y < 0)
                continue
            val down = cur.down()
            //val groundString = ground.getGroundString()

            if (down.y < ground.size) {
                //found ground or our own water
                if (ground.getPoint(down) == Int.MIN_VALUE || ground.getPoint(down) == 1) {
                    var toFill = down
                    var shouldContinue = true
                    while (shouldContinue) {
                        toFill = toFill.up()
                        val (left, right) = ground[toFill.y].findBounds(toFill.x)

                        for (x in toFill.x..right) {
                            if (ground[toFill.y+1][x] == 0) {
                                val toAdd = Point(x, toFill.y)
                                if (!queue.contains(toAdd))
                                    queue.push(toAdd)

                                shouldContinue = false
                                break
                            } else if (ground[toFill.y+1][x] == 2) {
                                shouldContinue = false
                                break
                            } else
                                ground[toFill.y][x] = 1
                        }

                        for (x in toFill.x downTo left) {
                            if (ground[toFill.y+1][x] == 0) {
                                val toAdd = Point(x, toFill.y)
                                if (!queue.contains(toAdd))
                                    queue.push(toAdd)

                                shouldContinue = false
                                break
                            } else if (ground[toFill.y+1][x] == 2) {
                                shouldContinue = false
                                break
                            } else
                                ground[toFill.y][x] = 1
                        }

                        if (!shouldContinue) {
                            for (x in toFill.x..right) {
                                if (ground[toFill.y][x] == 1) {
                                    ground[toFill.y][x] = 2
                                }
                            }

                            for (x in toFill.x downTo left) {
                                if (ground[toFill.y][x] == 1) {
                                    ground[toFill.y][x] = 2
                                }
                            }
                        }
                    }
                }
                if (ground.getPoint(down) == 2) {
                    ground[cur.y][cur.x] = 2
                }
                if (ground.getPoint(down) == 0) {
                    ground[cur.y][cur.x] = 2
                    if (down.y < ground.size)
                        queue.add(down)

                }
            } else {
                ground[cur.y][cur.x] = 2
            }
        }

        return ground
    }

    override fun part1(): String {
        val all = input.flatMap { line ->
            val numbers = line.extractInts()
            when(line[0]) {
                'y' -> Point.fromRange(numbers[1]..numbers[2], numbers[0])
                'x' -> Point.fromRange(numbers[0], numbers[1]..numbers[2])
                else -> throw IllegalStateException("Malformed input, line started with ${line[0]}")
            }
        }.toSet()

        val ground = generateWaterMap(all)

        val minY = all.minBy { it.y }!!.y

        return "" + ground.filterIndexed{ y, _ -> y >= minY }.flatMap { it.asIterable() }.count { it == 1 || it == 2 }
    }

    fun IntArray.findBounds(x: Int) : Pair<Int, Int> {
        var min = x
        var max = x

        while (min >= 0 && this[min] != Int.MIN_VALUE)
            min--
        while (max < this.size && this[max] != Int.MIN_VALUE)
            max++

        return ++min to --max
    }
    fun Array<IntArray>.getPoint(point: Point) = this[point.y][point.x]
    
    override fun part2(): String {
        val all = input.flatMap { line ->
            val numbers = line.extractInts()
            when(line[0]) {
                'y' -> Point.fromRange(numbers[1]..numbers[2], numbers[0])
                'x' -> Point.fromRange(numbers[0], numbers[1]..numbers[2])
                else -> throw IllegalStateException("Malformed input, line started with ${line[0]}")
            }
        }.toSet()

        val ground = generateWaterMap(all)

        val minY = all.minBy { it.y }!!.y

        return "" + ground.filterIndexed{ y, _ -> y >= minY }.flatMap { it.asIterable() }.count { it == 1 }
    }
}