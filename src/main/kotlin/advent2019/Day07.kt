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
            prev = input.toMutableList().runWithInput(listOf(i, prev)).out.first().toInt()
        }
        return prev
    }

    override fun part1() : Any = (0..4).permutations().map { doSimulation(it) }.max()!!

    suspend fun doFeedbackSimulation(inputs: List<Long>) : Long {
        val inChannels = inputs.map {
            val inCh = Channel<Long>(5)
            inCh.send(it)
            inCh
        }.toMutableList()

        inChannels.add(inChannels.first())

        inChannels.first().send(0)

        var num = 1
        val vms = inChannels.zipWithNext().map { (inCh, outCh) -> Intcode(input.toMutableList().map { it.toLong() }.toMutableList(), inCh, outCh, "VM ${num++}") }

        coroutineScope {
            vms.forEach { vm ->
                launch {
                    vm.simulate()
                }
            }
        }

        return vms.last().out.last()
    }

    override fun part2() : Any {

        return runBlocking {
            (5..9).map { it.toLong() }.permutations().map { async { doFeedbackSimulation(it) } }.toList().awaitAll().max()!!
        }

    }


}
