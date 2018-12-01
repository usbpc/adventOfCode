package advent2017

import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day
import xyz.usbpc.utils.Direction
import kotlin.math.abs

class Day03(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 3
    val input = adventOfCode.getInput(2017, 3).toInt()
    override fun part1() = getManhattanDistanceToCenter(getCoordinatesForAdress(input)).toString()
    override fun part2() = aWalkInThePark(347991).toString()
    fun aWalkInThePark(adress: Int): Int {
        val dataStructure = GridDataStructure()
        dataStructure.set(Coordinates(0, 0), 1)
        var current = 1
        var direction = Direction.UP
        var x = 1
        var y = 0
        var wallSize = 3
        var counter = 1

        while (current < adress) {
            current = dataStructure.calculateAndSet(Coordinates(x, y))
            //println("x: $x y: $y, value ${dataStructure.get(Coordinates(x, y))}")
            when (direction) {
                Direction.UP -> y++
                Direction.DOWN -> y--
                Direction.RIGHT -> x++
                Direction.LEFT -> x--
            }

            if (--counter == 0) {
                counter = wallSize - 1
                direction = when (direction) {
                    Direction.UP -> Direction.LEFT
                    Direction.LEFT -> Direction.DOWN
                    Direction.DOWN -> {
                        counter++
                        Direction.RIGHT
                    }
                    Direction.RIGHT -> {
                        wallSize += 2
                        counter++
                        Direction.UP
                    }
                }
            }
        }
        return current
    }

    fun GridDataStructure.calculateAndSet(coordinates: Coordinates): Int {
        var total = 0
        for (x in -1..1) {
            for (y in -1..1) {
                if (x == 0 && y == 0) continue
                total += this.get(Coordinates(coordinates.x + x, coordinates.y + y))
            }
        }
        this.set(coordinates, total)
        return total
    }

    fun getManhattanDistanceToCenter(coordinates: Coordinates) = abs(coordinates.x) + abs(coordinates.y)
    fun getCoordinatesForAdress(adress: Int): Coordinates {
        var counter = 1
        var coord = 0
        //Find the square this number is in, kinda
        while (counter * counter < adress) {
            counter += 2
            coord--
        }

        var wallSize = counter

        counter -= 2

        var current = counter * counter
        var x = -(++coord)
        var y = coord
        x++
        current++
        counter = wallSize - 2
        var direction = Direction.UP
        //Now from here walk to the square...
        while (current != adress) {
            //println("Position: ($x, $y) current: $current, direction: $direction, counter: $counter")
            current++
            when (direction) {
                Direction.UP -> y++
                Direction.DOWN -> y--
                Direction.RIGHT -> x++
                Direction.LEFT -> x--
            }
            if (--counter == 0) {
                counter = wallSize - 1
                direction = when (direction) {
                    Direction.UP -> Direction.LEFT
                    Direction.LEFT -> Direction.DOWN
                    Direction.DOWN -> {
                        counter++
                        Direction.RIGHT
                    }
                    Direction.RIGHT -> {
                        wallSize += 2
                        counter++
                        Direction.UP
                    }
                }
            }
        }
        return Coordinates(x, y)
    }

    data class Coordinates(val x: Int, val y: Int) {
        override fun toString() = "($x, $y)"
    }

    class GridDataStructure {
        var data = arrayOfNulls<Int>(1)
        private fun getIndexFromCoordinates(index: Coordinates): Int {
            if (index == Coordinates(0, 0)) return 0
            var tmp = 1 + 2 * if (abs(index.x) > abs(index.y)) {
                abs(index.x)
            } else {
                abs(index.y)
            }
            //What was the largest index in the square smaller than this!
            val base = (tmp - 2) * (tmp - 2) - 1

            val ourX = tmp / 2 + index.x
            val ourY = tmp / 2 + index.y

            val offset =
                    when {
                    //At the bottom
                        index.y == tmp / 2 -> ourX
                    //At the top
                        abs(index.y) == tmp / 2 -> ourX + tmp
                    //At one side... I hope
                        else -> {
                            if (ourX > 0) {
                                tmp + tmp + tmp + ourY - 3
                            } else {
                                tmp + tmp + ourY - 1
                            }
                        }
                    }

            return base + offset + 1
        }

        fun set(index: Coordinates, value: Int) {
            while (getIndexFromCoordinates(index) >= data.size) {
                data = data.copyOf(data.size * 2)
            }
            data[getIndexFromCoordinates(index)] = value
        }

        fun get(index: Coordinates): Int {
            if (getIndexFromCoordinates(index) >= data.size) return 0
            return data[getIndexFromCoordinates(index)] ?: 0
        }
    }
}