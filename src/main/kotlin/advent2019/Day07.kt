package advent2019

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Queue

class Day07(override val adventOfCode: AdventOfCode) : Day {
    override val day = 7

    val input = adventOfCode.getInput(2019, day).split(",").map{ it.toInt()}
    //val input = "3,15,3,16,1002,16,10,16,1,16,15,15,4,15,99,0,0".split()

    val permutations = listOf(listOf(0,1,2,3,4), listOf(1,0,2,3,4), listOf(2,0,1,3,4), listOf(0,2,1,3,4), listOf(1,2,0,3,4), listOf(2,1,0,3,4), listOf(2,1,3,0,4), listOf(1,2,3,0,4), listOf(3,2,1,0,4), listOf(2,3,1,0,4), listOf(1,3,2,0,4), listOf(3,1,2,0,4), listOf(3,0,2,1,4), listOf(0,3,2,1,4), listOf(2,3,0,1,4), listOf(3,2,0,1,4), listOf(0,2,3,1,4), listOf(2,0,3,1,4), listOf(1,0,3,2,4), listOf(0,1,3,2,4), listOf(3,1,0,2,4), listOf(1,3,0,2,4), listOf(0,3,1,2,4), listOf(3,0,1,2,4), listOf(4,0,1,2,3), listOf(0,4,1,2,3), listOf(1,4,0,2,3), listOf(4,1,0,2,3), listOf(0,1,4,2,3), listOf(1,0,4,2,3), listOf(1,0,2,4,3), listOf(0,1,2,4,3), listOf(2,1,0,4,3), listOf(1,2,0,4,3), listOf(0,2,1,4,3), listOf(2,0,1,4,3), listOf(2,4,1,0,3), listOf(4,2,1,0,3), listOf(1,2,4,0,3), listOf(2,1,4,0,3), listOf(4,1,2,0,3), listOf(1,4,2,0,3), listOf(0,4,2,1,3), listOf(4,0,2,1,3), listOf(2,0,4,1,3), listOf(0,2,4,1,3), listOf(4,2,0,1,3), listOf(2,4,0,1,3), listOf(3,4,0,1,2), listOf(4,3,0,1,2), listOf(0,3,4,1,2), listOf(3,0,4,1,2), listOf(4,0,3,1,2), listOf(0,4,3,1,2), listOf(0,4,1,3,2), listOf(4,0,1,3,2), listOf(1,0,4,3,2), listOf(0,1,4,3,2), listOf(4,1,0,3,2), listOf(1,4,0,3,2), listOf(1,3,0,4,2), listOf(3,1,0,4,2), listOf(0,1,3,4,2), listOf(1,0,3,4,2), listOf(3,0,1,4,2), listOf(0,3,1,4,2), listOf(4,3,1,0,2), listOf(3,4,1,0,2), listOf(1,4,3,0,2), listOf(4,1,3,0,2), listOf(3,1,4,0,2), listOf(1,3,4,0,2), listOf(2,3,4,0,1), listOf(3,2,4,0,1), listOf(4,2,3,0,1), listOf(2,4,3,0,1), listOf(3,4,2,0,1), listOf(4,3,2,0,1), listOf(4,3,0,2,1), listOf(3,4,0,2,1), listOf(0,4,3,2,1), listOf(4,0,3,2,1), listOf(3,0,4,2,1), listOf(0,3,4,2,1), listOf(0,2,4,3,1), listOf(2,0,4,3,1), listOf(4,0,2,3,1), listOf(0,4,2,3,1), listOf(2,4,0,3,1), listOf(4,2,0,3,1), listOf(3,2,0,4,1), listOf(2,3,0,4,1), listOf(0,3,2,4,1), listOf(3,0,2,4,1), listOf(2,0,3,4,1), listOf(0,2,3,4,1), listOf(1,2,3,4,0), listOf(2,1,3,4,0), listOf(3,1,2,4,0), listOf(1,3,2,4,0), listOf(2,3,1,4,0), listOf(3,2,1,4,0), listOf(3,2,4,1,0), listOf(2,3,4,1,0), listOf(4,3,2,1,0), listOf(3,4,2,1,0), listOf(2,4,3,1,0), listOf(4,2,3,1,0), listOf(4,1,3,2,0), listOf(1,4,3,2,0), listOf(3,4,1,2,0), listOf(4,3,1,2,0), listOf(1,3,4,2,0), listOf(3,1,4,2,0), listOf(2,1,4,3,0), listOf(1,2,4,3,0), listOf(4,2,1,3,0), listOf(2,4,1,3,0), listOf(1,4,2,3,0), listOf(4,1,2,3,0))
    val otherPermutations = listOf(listOf(5,6,7,8,9), listOf(6,5,7,8,9), listOf(7,5,6,8,9), listOf(5,7,6,8,9), listOf(6,7,5,8,9), listOf(7,6,5,8,9), listOf(7,6,8,5,9), listOf(6,7,8,5,9), listOf(8,7,6,5,9), listOf(7,8,6,5,9), listOf(6,8,7,5,9), listOf(8,6,7,5,9), listOf(8,5,7,6,9), listOf(5,8,7,6,9), listOf(7,8,5,6,9), listOf(8,7,5,6,9), listOf(5,7,8,6,9), listOf(7,5,8,6,9), listOf(6,5,8,7,9), listOf(5,6,8,7,9), listOf(8,6,5,7,9), listOf(6,8,5,7,9), listOf(5,8,6,7,9), listOf(8,5,6,7,9), listOf(9,5,6,7,8), listOf(5,9,6,7,8), listOf(6,9,5,7,8), listOf(9,6,5,7,8), listOf(5,6,9,7,8), listOf(6,5,9,7,8), listOf(6,5,7,9,8), listOf(5,6,7,9,8), listOf(7,6,5,9,8), listOf(6,7,5,9,8), listOf(5,7,6,9,8), listOf(7,5,6,9,8), listOf(7,9,6,5,8), listOf(9,7,6,5,8), listOf(6,7,9,5,8), listOf(7,6,9,5,8), listOf(9,6,7,5,8), listOf(6,9,7,5,8), listOf(5,9,7,6,8), listOf(9,5,7,6,8), listOf(7,5,9,6,8), listOf(5,7,9,6,8), listOf(9,7,5,6,8), listOf(7,9,5,6,8), listOf(8,9,5,6,7), listOf(9,8,5,6,7), listOf(5,8,9,6,7), listOf(8,5,9,6,7), listOf(9,5,8,6,7), listOf(5,9,8,6,7), listOf(5,9,6,8,7), listOf(9,5,6,8,7), listOf(6,5,9,8,7), listOf(5,6,9,8,7), listOf(9,6,5,8,7), listOf(6,9,5,8,7), listOf(6,8,5,9,7), listOf(8,6,5,9,7), listOf(5,6,8,9,7), listOf(6,5,8,9,7), listOf(8,5,6,9,7), listOf(5,8,6,9,7), listOf(9,8,6,5,7), listOf(8,9,6,5,7), listOf(6,9,8,5,7), listOf(9,6,8,5,7), listOf(8,6,9,5,7), listOf(6,8,9,5,7), listOf(7,8,9,5,6), listOf(8,7,9,5,6), listOf(9,7,8,5,6), listOf(7,9,8,5,6), listOf(8,9,7,5,6), listOf(9,8,7,5,6), listOf(9,8,5,7,6), listOf(8,9,5,7,6), listOf(5,9,8,7,6), listOf(9,5,8,7,6), listOf(8,5,9,7,6), listOf(5,8,9,7,6), listOf(5,7,9,8,6), listOf(7,5,9,8,6), listOf(9,5,7,8,6), listOf(5,9,7,8,6), listOf(7,9,5,8,6), listOf(9,7,5,8,6), listOf(8,7,5,9,6), listOf(7,8,5,9,6), listOf(5,8,7,9,6), listOf(8,5,7,9,6), listOf(7,5,8,9,6), listOf(5,7,8,9,6), listOf(6,7,8,9,5), listOf(7,6,8,9,5), listOf(8,6,7,9,5), listOf(6,8,7,9,5), listOf(7,8,6,9,5), listOf(8,7,6,9,5), listOf(8,7,9,6,5), listOf(7,8,9,6,5), listOf(9,8,7,6,5), listOf(8,9,7,6,5), listOf(7,9,8,6,5), listOf(9,7,8,6,5), listOf(9,6,8,7,5), listOf(6,9,8,7,5), listOf(8,9,6,7,5), listOf(9,8,6,7,5), listOf(6,8,9,7,5), listOf(8,6,9,7,5), listOf(7,6,9,8,5), listOf(6,7,9,8,5), listOf(9,7,6,8,5), listOf(7,9,6,8,5), listOf(6,9,7,8,5), listOf(9,6,7,8,5))
    override fun part1() : Any {
        return permutations.map { doSimulation(it) }.max()!!
    }

    fun doSimulation(inputs: List<Int>) : Int {
        var prev = 0
        for (i in inputs) {
            prev = input.toMutableList().runWithInput(listOf(i, prev)).out.first()
        }
        return prev
    }

    suspend fun CoroutineScope.doFeedbackSimulation(inputs: List<Int>) : Int {
        val inChannels = inputs.map {
            val inCh = Channel<Int>(5)
            inCh.send(it)
            inCh
        }.toMutableList()

        inChannels.add(inChannels.first())

        inChannels.first().send(0)
        var num = 1
        val vms = inChannels.zipWithNext().map { (inCh, outCh) -> Intcode(input.toMutableList(), inCh, outCh, "VM ${num++}") }
        vms.map { async { it.run { coroutineScope { simulate() } } } }.forEach { it.join() }

        return vms.last().out.last()
    }

    override fun part2() : Any = runBlocking { otherPermutations.map { doFeedbackSimulation(it) }.max()!! }


}
