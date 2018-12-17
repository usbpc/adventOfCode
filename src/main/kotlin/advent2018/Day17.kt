package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException
import java.util.*

class Day17(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 17
    private val input = adventOfCode.getInput(2018, day).lines()

    data class Point(val x: Int, val y: Int) : Comparable<Point> {
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

    override fun part1(): String {
        val fixedY = input.filter { line -> line.startsWith("y") }.map { it.extractInts() }.flatMap { fixedY -> (fixedY[1]..fixedY[2]).map { x -> Point(x, fixedY[0]) } }
        val fixedX = input.filter { line -> line.startsWith("x") }.map { it.extractInts() }.flatMap { fixedX -> (fixedX[1]..fixedX[2]).map { y -> Point(fixedX[0], y) } }
        val all = (fixedX + fixedY).toSet()
        val maxX = all.maxBy { it.x }!!.x
        val minX = all.minBy { it.x }!!.x
        val maxY = all.maxBy { it.y }!!.y
        val minY = all.minBy { it.y }!!.y


        val ground = Array(maxY+1) { IntArray(maxX - minX + 3) }

        val spring = Point(500 - minX + 1, 0)
        for (y in 0..maxY) {
            for (x in minX..maxX) {
                if (Point(x, y) in all)
                    ground[y][x - minX + 1] = Int.MIN_VALUE
            }
        }

        //println(ground.getGroundString())
        var counter = 0
        val queue = ArrayDeque<Point>()
        queue.add(spring.down())

        while (queue.isNotEmpty()) {
            val cur = queue.poll()
            if (cur.y < 0)
                continue
            val down = cur.down()
            val groundString = ground.getGroundString()

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
                                    //if (ground[toFill.y+1][x] == Int.MIN_VALUE) {
                                    val toAdd = Point(x, toFill.y)
                                    if (queue.contains(toAdd)) {
                                        Unit
                                    } else {
                                        queue.add(toAdd)
                                    }
                                    //}
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
                                    if (queue.contains(toAdd)) {
                                        Unit
                                    } else {
                                        queue.add(toAdd)
                                    }
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

        println(ground.getGroundString())

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
        val fixedY = input.filter { line -> line.startsWith("y") }.map { it.extractInts() }.flatMap { fixedY -> (fixedY[1]..fixedY[2]).map { x -> Point(x, fixedY[0]) } }
        val fixedX = input.filter { line -> line.startsWith("x") }.map { it.extractInts() }.flatMap { fixedX -> (fixedX[1]..fixedX[2]).map { y -> Point(fixedX[0], y) } }
        val all = (fixedX + fixedY).toSet()
        val maxX = all.maxBy { it.x }!!.x
        val minX = all.minBy { it.x }!!.x
        val maxY = all.maxBy { it.y }!!.y
        val minY = all.minBy { it.y }!!.y


        val ground = Array(maxY+1) { IntArray(maxX - minX + 3) }

        val spring = Point(500 - minX + 1, 0)
        for (y in 0..maxY) {
            for (x in minX..maxX) {
                if (Point(x, y) in all)
                    ground[y][x - minX + 1] = Int.MIN_VALUE
            }
        }

        //println(ground.getGroundString())
        var counter = 0
        val queue = ArrayDeque<Point>()
        queue.add(spring.down())

        while (queue.isNotEmpty()) {
            val cur = queue.poll()
            if (cur.y < 0)
                continue
            val down = cur.down()
            val groundString = ground.getGroundString()

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
                                //if (ground[toFill.y+1][x] == Int.MIN_VALUE) {
                                val toAdd = Point(x, toFill.y)
                                if (queue.contains(toAdd)) {
                                    Unit
                                } else {
                                    queue.add(toAdd)
                                }
                                //}
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
                                if (queue.contains(toAdd)) {
                                    Unit
                                } else {
                                    queue.add(toAdd)
                                }
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

        println(ground.getGroundString())

        return "" + ground.filterIndexed{ y, _ -> y >= minY }.flatMap { it.asIterable() }.count { it == 1 }
    }
}