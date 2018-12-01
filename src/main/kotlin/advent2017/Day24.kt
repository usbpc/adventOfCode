package advent2017

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode

class Day24(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 24
    private val input = adventOfCode.getInput(2017, day).lines().map { it.split('/').map { it.toInt() }}

    override fun part1(): String {
        assert(input.size == input.toSet().count())
        val allthings = findAll().groupBy { it.size }
        //println(allthings.size)
        allthings[allthings.keys.max()]!!.map { it.dominofy() }.forEach {
            println(it)
            //println(it.sumBy { it.sum() })
        }
        //println(findAll().filter { it.size > 30 }.map { it.dominofy() }.size)
        return findStrongest().sumBy { it.sum() }.toString()
    }
    private fun List<List<Int>>.dominofy(): List<List<Int>> {
        val dominos = mutableListOf<List<Int>>()
        var cur = 0
        this.forEach {
            cur = if (it[0] == cur) {
                dominos.add(it)
                it[1]
            } else {
                dominos.add(it.reversed())
                it[0]
            }
        }
        return dominos
    }
    override fun part2(): String {
        val result = findLongest()
        val dominos = mutableListOf<List<Int>>()
        var cur = 0
        result.forEach {
            cur = if (it[0] == cur) {
                dominos.add(it)
                it[1]
            } else {
                dominos.add(it.reversed())
                it[0]
            }
        }
        //println(dominos)
        //println(dominos.size)
        return result.sumBy { it.sum() }.toString()
    }

    private fun findStrongest(used: List<List<Int>> = emptyList(), cur: Int = 0): List<List<Int>> {
        val possible = input.filter { (a, b) -> (a == cur || b == cur) && (cur == 0 || (a != 0 && b != 0)) }.filter { it !in used }
        if (possible.isEmpty()) {
            return used
        }
        return possible.map { findStrongest(used + listOf(it), if (it[0] == cur) it[1] else it[0]) }.maxBy{it.sumBy { it.sum() }} ?: throw IllegalStateException("Nope ")
    }
    private fun findAll(used: List<List<Int>> = emptyList(), cur: Int = 0): List<List<List<Int>>> {
        val possible = input.filter { (a, b) -> (a == cur || b == cur) && (cur == 0 || (a != 0 && b != 0)) }.filter { it !in used }
        if (possible.isEmpty()) {
            return listOf(used)
        }
        return possible.flatMap { findAll(used + listOf(it), if (it[0] == cur) it[1] else it[0]) }
    }
    private fun findLongest(used: List<List<Int>> = emptyList(), cur: Int = 0): List<List<Int>> {
        val possible = input.filter { (a, b) -> (a == cur || b == cur) && (cur == 0 || (a != 0 && b != 0)) }.filter { it !in used }
        if (possible.isEmpty()) {
            return used
        }
        return possible.map { findLongest(used + listOf(it), if (it[0] == cur) it[1] else it[0]) }.maxBy{ it.size } ?: throw IllegalStateException("Nope ")
    }

}