package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.permutations
import java.lang.IllegalStateException

class Day08(override val adventOfCode: AdventOfCode) : Day {
    override val day = 8

    val input = adventOfCode.getInput(2019, day)

    override fun part1() : Any {
        //25 x 6
        val leastZeros = input.windowed(25*6, 25*6).map { it.toCharArray() }.minBy { it.count { ch -> ch == '0' } }!!

        return leastZeros.count { it == '1' } * leastZeros.count { it == '2' }
    }

    enum class Colour {
        Black,
        White,
        Transparent;

        companion object {
            fun fromChar(i: Char) : Colour {
                return when (i) {
                    '0' -> Black
                    '1' -> White
                    '2' -> Transparent
                    else -> throw IllegalStateException("I don't know what to do with {i}")
                }
            }
        }

        fun addOther(other: Colour): Colour {
            return when(this) {
                Black, White -> this
                Transparent -> other
            }
        }

        override fun toString(): String {
            return when (this) {
                White -> "█"
                Transparent -> "░"
                Black -> " "
            }
        }
    }

    class Image(val width : Int = 25, val height : Int = 6) {
        val data = MutableList<MutableList<Colour>>(height) { MutableList(width) { Colour.Transparent } }

        fun addColour(position: Int, colour: Colour) {
            val height = position / 25
            val width =  position % 25

            data[height][width] = data[height][width].addOther(colour)
        }

        fun print() {
            for (row in data) {
                for (pixel in row) {
                    print(pixel)
                }
                print('\n')
            }
        }
    }

    override fun part2() : Any {

        val img = Image()

        input.windowed(25*6, 25*6).map { it.toCharArray() }.forEach { layer ->
            layer.map { Colour.fromChar(it) }
                    .forEachIndexed { i, pix ->
                        img.addColour(i, pix)
                    }
        }

        img.print()

        return ""
    }
}
