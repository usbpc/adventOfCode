package advent2017

import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.aoc.Day

class Day08(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 8
    class NeverNullMap<K, V> private constructor(private val backing: MutableMap<K, V>, val default: () -> V) :
            MutableMap<K, V> by backing {

        constructor(default: () -> V) : this(mutableMapOf(), default)

        override operator fun get(key: K): V = backing.getOrPut(key, default)
        operator fun set(key: K, value: V) = backing.put(key, value)
    }

    var part1: Int? = null
    var part2: Int? = null
    override fun part1(): Any {
        if (part1 == null) {
            solve()
        }
        return part1.toString()
    }

    override fun part2(): Any {
        if (part2 == null) {
            solve()
        }
        return part2.toString()
    }

    fun solve() {
        val myMap = NeverNullMap<String, Int> { 0 }
        val compMap = hashMapOf<String, (Int, Int) -> Boolean>(
                ">" to {x, y -> x > y},
                "<" to {x, y -> x < y},
                ">=" to {x, y -> x >= y},
                "<=" to {x, y -> x <= y},
                "==" to {x, y -> x == y},
                "!=" to {x, y -> x != y}
        )
        val lines = adventOfCode.getInput(2017, 8).lines()
        var processMax = 0
        for (line in lines) {
            val tokens = line.split(' ')
            if (!compMap[tokens[5]]!!(myMap[tokens[4]], tokens[6].toInt())) continue
            when (tokens[1]) {
                "inc" -> myMap[tokens[0]] += tokens[2].toInt()
                "dec" -> myMap[tokens[0]] -= tokens[2].toInt()
                else -> throw IllegalStateException("What does ${tokens[1]} mean?")
            }
            processMax = maxOf(myMap.maxBy {it.value}?.value ?: -1, processMax)
        }

        part1 = myMap.maxBy {it.value}?.value ?: -1
        part2 = processMax
    }
}