package advent2017

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.doWhileCount
import java.util.*

class Day16(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 16
    val spin = Regex("s(\\d+)")
    val exchange = Regex("x(\\d+)/(\\d+)")
    val partner = Regex("p([a-p])/([a-p])")
    private val input = adventOfCode.getInput(2017, day).split(',')
    private val transformedInput: List<(CharArray) -> CharArray> = input.map {
        when {
            spin.matches(it) -> {
                val r = spin.find(it)!!.groups[1]!!.value.toInt()
                return@map {programOrder: CharArray ->
                   CharArray(programOrder.size) { programOrder[(programOrder.size - r + it) % programOrder.size]}
                }
            }
            exchange.matches(it) -> {
                val (first, second) = exchange.find(it)!!.groups.drop(1).map {it!!.value.toInt()}
                return@map {programOrder: CharArray ->
                    val tmp = programOrder[first]
                    programOrder[first] = programOrder[second]
                    programOrder[second] = tmp
                    programOrder
                }
            }
            partner.matches(it) -> {
                val (fc, sc) = partner.find(it)!!.groups.drop(1).map {it!!.value.first()}
                return@map {programOrder: CharArray ->
                    val first = programOrder.indexOf(fc)
                    val second = programOrder.indexOf(sc)
                    val tmp = programOrder[first]
                    programOrder[first] = programOrder[second]
                    programOrder[second] = tmp
                    programOrder
                }
            }
            else -> throw IllegalStateException("Everything should be known!")
        }
    }

    override fun part1(): String {
        var programOrder = "abcdefghijklmnop".toCharArray()
        transformedInput.forEach {
            programOrder = it(programOrder)
        }
        return String(programOrder)
    }

    override fun part2(): String {
        var programOrder = "abcdefghijklmnop".toCharArray()
        val counter = doWhileCount({!Arrays.equals("abcdefghijklmnop".toCharArray(), programOrder)}) {
            transformedInput.forEach {
                programOrder = it(programOrder)
            }
        }

        repeat(1000000000 % counter) {
            transformedInput.forEach {
                programOrder = it(programOrder)
            }
        }
        return String(programOrder)
    }
}