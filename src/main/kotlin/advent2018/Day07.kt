package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.StringBuilder
import kotlin.math.max

class Day07(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 7

    private val regex = """Step (.) must be finished before step (.) can begin\.""".toRegex()

    private val input = adventOfCode.getInput(2018, day).lines()
            .map { line ->
                val re = regex.find(line)
                re!!.groups[1]!!.value[0] to re.groups[2]!!.value[0]
            }

    /**
     * Computes the prerequisites and following steps for each step
     */
    private fun createStepMap(input: List<Pair<Char, Char>>) : Map<Char, Step> {
        val map = mutableMapOf<Char, Step>()
        input.forEach { line ->
            val cur = if (map.containsKey(line.first)) {
                map[line.first]!!
            } else {
                Step(line.first).apply {
                    map[line.first] = this
                }
            }
            val next = if (map.containsKey(line.second)) {
                map[line.second]!!
            } else {
                Step(line.second).apply {
                    map[line.second] = this
                }
            }
            next.needed.add(line.first)
            cur.children.add(line.second)
        }

        return map
    }

    data class Step(val letter: Char, val children: MutableSet<Char> = mutableSetOf(), val needed : MutableSet<Char> = mutableSetOf())


    override fun part1(): Any {
        val map = createStepMap(input)

        val next = mutableSetOf<Char>()
        map.values
                .filter { step ->
                    map.values.none { it.children.any { it == step.letter } }
                }.forEach { step ->
                    next.add(step.letter)
                }
        val used = mutableSetOf<Char>()
        val out = StringBuilder()

        while(!used.containsAll(map.keys)) {
            val cur = next.min()!!
            next.remove(cur)
            used.add(cur)
            map[cur]!!.children.filterNot { used.contains(it) }.filter { used.containsAll(map[it]!!.needed) }.forEach { next.add(it) }
            out.append(cur)
        }

        return out.toString()
    }

    data class Worker(var letter: Char = ' ', var cooldown: Int = 0)

    override fun part2(): Any {
        val map = createStepMap(input)

        val next = mutableSetOf<Char>()
        map.values.filter { thing -> map.values.none { it.children.any { it == thing.letter } } }.forEach { next.add(it.letter) }
        val used = mutableSetOf<Char>()
        var out = -1L

        val cooldowns = List(5) { Worker() }

        while(!used.containsAll(map.keys)) {
            cooldowns.filter { it.cooldown == 0 && it.letter != ' ' }.forEach {
                used.add(it.letter)
            }

            cooldowns.filter { it.cooldown == 0 && it.letter != ' ' }.forEach {
                map[it.letter]!!.children
                        .filterNot { used.contains(it) }
                        .filter { used.containsAll(map[it]!!.needed) }
                        .forEach { next.add(it) }

                it.letter = ' '
            }

            val cur = next.sorted().take(cooldowns.filter { it.cooldown == 0 }.count())

            cur.forEach { next.remove(it) }
            for (i in 0 until cooldowns.size) {
                cooldowns[i].cooldown = max(cooldowns[i].cooldown-1, 0)
            }
            cur.forEach { letter ->
                val firstZero = cooldowns.withIndex().filter { (_, value) ->
                    value.cooldown == 0 && value.letter == ' '
                }.map { (index, _) -> index}.first()

                cooldowns[firstZero].cooldown = 60 + (letter - 'A')
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