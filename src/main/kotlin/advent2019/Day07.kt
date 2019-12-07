package advent2019

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.permutations

class Day07(override val adventOfCode: AdventOfCode) : Day {
    override val day = 7

    val input = adventOfCode.getInput(2019, day).split(",").map{ it.toInt()}

    fun doSimulation(inputs: List<Int>) : Int {
        var prev = 0
        for (i in inputs) {
            prev = input.toMutableList().runWithInput(listOf(i, prev)).out.first()
        }
        return prev
    }

    override fun part1() : Any = (0..4).permutations().map { doSimulation(it) }.max()!!

    fun doFeedbackSimulation(inputs: List<Int>) : Int = runBlocking {
        val inChannels = inputs.map {
            val inCh = Channel<Int>(5)
            inCh.send(it)
            inCh
        }.toMutableList()

        inChannels.add(inChannels.first())

        inChannels.first().send(0)

        var num = 1
        val vms = inChannels.zipWithNext().map { (inCh, outCh) -> Intcode(input.toMutableList(), inCh, outCh, "VM ${num++}") }

        vms.map {vm ->
            launch {
                vm.simulate()
            }
        }.forEach { it.join() }

        vms.last().out.last()
    }

    override fun part2() : Any = (5..9).permutations().map { doFeedbackSimulation(it) }.max()!!


}
