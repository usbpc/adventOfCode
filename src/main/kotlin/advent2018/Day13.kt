package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException

class Day13(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 13

    enum class Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT;

        fun left() =
            when(this) {
                UP -> LEFT
                DOWN -> RIGHT
                LEFT -> DOWN
                RIGHT -> UP
            }
        fun right() =
                when(this) {
                    UP -> RIGHT
                    DOWN -> LEFT
                    LEFT -> UP
                    RIGHT -> DOWN
                }
    }

    private data class Cart(var x: Int, var y: Int, var facing: Direction, val tracks : List<List<Char>>) : Comparable<Cart> {

        override fun compareTo(other: Cart): Int =
                if (this.y == other.y) {
                    this.x - other.x
                } else {
                    this.y - other.y
                }

        var turnCounter = 0
        fun char() =
                when(facing) {
                    Direction.UP -> '^'
                    Direction.DOWN -> 'v'
                    Direction.LEFT -> '<'
                    Direction.RIGHT -> '>'
                }
        fun stepOnce() {
            when (facing) {
                Direction.UP -> y--
                Direction.DOWN -> y++
                Direction.LEFT -> x--
                Direction.RIGHT -> x++
            }
            when (tracks[y][x]) {
                '+' -> {
                    when (turnCounter) {
                        0 -> facing = facing.left()
                        2 -> facing = facing.right()
                    }
                    turnCounter = (turnCounter + 1) % 3
                }
                '/' -> {
                    facing = when (facing) {
                        Direction.UP -> Direction.RIGHT
                        Direction.DOWN -> Direction.LEFT
                        Direction.LEFT -> Direction.DOWN
                        Direction.RIGHT -> Direction.UP
                    }
                }
                '\\' -> {
                    facing = when (facing) {
                        Direction.UP -> Direction.LEFT
                        Direction.DOWN -> Direction.RIGHT
                        Direction.LEFT -> Direction.UP
                        Direction.RIGHT -> Direction.DOWN
                    }
                }
            }
        }
    }

    private val input = adventOfCode.getInput(2018, day).lines()

    private fun parseInput() : List<Cart> {
        val tracks = input
                .map { line ->
                    line.map { char ->
                        if (char == '>' || char == '<')
                            '-'
                        else if (char == '^' || char == 'v') {
                            '|'
                        } else {
                            char
                        }
                    }
                }

        return input.withIndex().map { (y, line) ->
            val value = line.withIndex()
                    .filter { (_, char) ->
                        char == '^' || char == 'v' || char == '<' || char == '>'
                    }
            IndexedValue(y, value)
        }.flatMap { (y, line) ->
            line.map { (x, char) ->
                when (char) {
                    '^' -> Cart(x, y, Direction.UP, tracks)
                    'v' -> Cart(x, y, Direction.DOWN, tracks)
                    '>' -> Cart(x, y, Direction.RIGHT, tracks)
                    '<' -> Cart(x, y, Direction.LEFT, tracks)
                    else -> throw IllegalStateException("How did we get here? $char")
                }
            }

        }
    }

    private fun printRailNetwork(carts: List<Cart>) {
        val tracks = carts.first().tracks
        tracks.forEachIndexed { y, line ->
            line.forEachIndexed { x, c ->
                val cartz = carts.filter { it.y == y && it.x == x }
                if (cartz.any()) {
                    val cart = cartz.singleOrNull()
                    print(cart?.char() ?: 'X')
                } else {
                    print(c)
                }
            }
            print('\n')
        }
    }

    override fun part1(): Any {
        var carts = parseInput()
        while (true) {
            for (cart in carts) {
                cart.stepOnce()
                if (carts.filter { it !== cart }.any { otherCart -> cart.y == otherCart.y && cart.x == otherCart.x }) {
                    return "${cart.x},${cart.y}"
                }
            }
            carts = carts.sorted()
        }
    }

    private fun <E> MutableList<E>.pop() = this.removeAt(0)

    override fun part2(): Any {
        var carts = parseInput()
        var ticks = 0
        while (carts.size > 1) {
            val cartsToMove = carts.sorted().toMutableList()
            ticks++
            while (cartsToMove.isNotEmpty()) {
                val movedCart = cartsToMove.pop()
                movedCart.stepOnce()
                if (carts.filter { it !== movedCart }.any { cart -> movedCart.y == cart.y && movedCart.x == cart.x }) {
                    carts = carts.filterNot { cart -> cart.y == movedCart.y && cart.x == movedCart.x }
                    cartsToMove.removeIf { cart -> cart.y == movedCart.y && cart.x == movedCart.x }
                }
            }
        }
        println("It took $ticks")
        val cart = carts.single()
        return "${cart.x},${cart.y}"
    }
}