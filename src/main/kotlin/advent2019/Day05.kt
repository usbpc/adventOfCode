package advent2019

import kotlinx.coroutines.runBlocking
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.io.PipedInputStream
import java.io.PipedOutputStream

class Day05(override val adventOfCode: AdventOfCode) : Day {
    override val day = 5
    val input = adventOfCode.getInput(2019, day).split(",").map { it.toInt() }

    fun something() = runBlocking {

    }

    override fun part1() : Any {
        val myInput = input.toMutableList()

        val toVM = PipedOutputStream()
        val vmIn = PipedInputStream(toVM, 255)

        toVM.bufferedWriter().write("1\n")
        toVM.flush()
        println("I got this far!")

        val vmOut = PipedOutputStream()
        val fromVM = PipedInputStream(vmOut, 1024*255)

        Intcode.run { myInput.simulate(vmIn, vmOut) }

        return fromVM.reader().readLines().last()
    }

    override fun part2() : Any {
        return ""
    }
}
