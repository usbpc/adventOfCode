package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder
import java.util.*
import kotlin.math.max

class Day07(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 7

    val regex = """Step (.) must be finished before step (.) can begin\.""".toRegex()

    val input = adventOfCode.getInput(2018, day).lines()
            .map { line ->
                val re = regex.find(line)
                re!!.groups[1]!!.value to re.groups[2]!!.value
            }


    data class Tree(val letter: String, val children: MutableList<Tree> = mutableListOf(), val needed : MutableSet<String> = mutableSetOf())

    override fun part1(): String {
        val map = mutableMapOf<String, Tree>()
        input.forEach { line ->
            val cur = if (map.containsKey(line.first)) {
                map[line.first]!!
            } else {
                Tree(line.first).apply {
                    map[line.first] = this
                }
            }
            val next = if (map.containsKey(line.second)) {
                map[line.second]!!
            } else {
                Tree(line.second).apply {
                    map[line.second] = this
                }
            }
            next.needed.add(line.first)
            cur.children.add(next)
        }

        val next = mutableSetOf<String>()
        map.values.filter { thing -> map.values.none { it.children.any { it.letter == thing.letter } } }.forEach { next.add(it.letter) }
        val used = mutableSetOf<String>()
        val out = StringBuilder()

        while(!used.containsAll(map.keys)) {
            val cur = next.min()!!
            next.remove(cur)
            used.add(cur)

            map[cur]!!.children.filterNot { used.contains(it.letter) }.filter { used.containsAll(it.needed) }.forEach { next.add(it.letter) }

            out.append(cur)
        }

        return out.toString()
    }

    data class Worker(var letter: String = "", var cooldown: Int = 0)

    override fun part2(): String {
        val map = mutableMapOf<String, Tree>()
        input.forEach { line ->

            val cur = if (map.containsKey(line.first)) {
                map[line.first]!!
            } else {
                Tree(line.first).apply {
                    map[line.first] = this
                }
            }

            val next = if (map.containsKey(line.second)) {
                map[line.second]!!
            } else {
                Tree(line.second).apply {
                    map[line.second] = this
                }
            }

            next.needed.add(line.first)

            cur.children.add(next)
        }

        val next = mutableSetOf<String>()

        map.values.filter { thing -> map.values.none { it.children.any { it.letter == thing.letter } } }.forEach { next.add(it.letter) }

        val used = mutableSetOf<String>()

        var out = -1L

        val cooldowns = List(5) { Worker() }

        while(!used.containsAll(map.keys)) {
            cooldowns.filter { it.cooldown == 0 && it.letter != "" }.forEach {
                used.add(it.letter)
            }

            cooldowns.filter { it.cooldown == 0 && it.letter != "" }.forEach {
                map[it.letter]!!.children
                        .filterNot { used.contains(it.letter) }
                        .filter { used.containsAll(it.needed) }
                        .forEach { next.add(it.letter) }

                it.letter = ""
            }

            val cur = next.sorted().take(cooldowns.filter { it.cooldown == 0 }.count())

            cur.forEach { next.remove(it) }

            for (i in 0 until cooldowns.size) {
                cooldowns[i].cooldown = max(cooldowns[i].cooldown-1, 0)
            }

            cur.forEach { letter ->
                val firstZero = cooldowns.withIndex().filter { (_, value) ->
                    value.cooldown == 0 && value.letter == ""
                }.map { (index, _) -> index}.first()

                cooldowns[firstZero].cooldown = 60 + (letter[0] - 'A')
                cooldowns[firstZero].letter = letter
            }

            out++
        }

        while (cooldowns.any { it.cooldown > 0 })
        {
            out++
            for (i in 0 until cooldowns.size) {
                cooldowns[i].cooldown = max(cooldowns[i].cooldown-1, 0)
            }
        }

        return out.toString()
    }
}