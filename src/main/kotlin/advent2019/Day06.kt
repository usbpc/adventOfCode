package advent2019

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.Queue

class Day06(override val adventOfCode: AdventOfCode) : Day {
    override val day = 6

   val input = adventOfCode.getInput(2019, day).lines().map{ it.split(")")}

    data class Planet(val name: String, val orbitBy: MutableList<Planet>)

    override fun part1() : Any {
        val map = mutableMapOf<String, Planet>()

        input.forEach { (stat, orbiter) ->
            if (!map.containsKey(orbiter)) {
                map[orbiter] = Planet(orbiter, mutableListOf())
            }

            if (!map.containsKey(stat)) {
                map[stat] = Planet(stat, mutableListOf())
            }
            map[stat]!!.orbitBy.add(map[orbiter]!!)
        }

        var counter = 0

        map.values.forEach {planet ->
            val toVisit = Queue<Planet>()
            toVisit.add(planet)

            counter--

            while (toVisit.isNotEmpty()) {
                counter++
                val cur = toVisit.remove()
                //println("${planet.name} is orbitted by ${cur.name}")
                cur.orbitBy.forEach { toVisit.add(it) }
            }
        }

        return counter
    }

    override fun part2() : Any {
        val map = mutableMapOf<String, Planet>()

        input.forEach { (stat, orbiter) ->
            if (!map.containsKey(orbiter)) {
                map[orbiter] = Planet(orbiter, mutableListOf())
            }

            if (!map.containsKey(stat)) {
                map[stat] = Planet(stat, mutableListOf())
            }
            map[stat]!!.orbitBy.add(map[orbiter]!!)
        }

        var counter = 0

        // Search for SAN and YOU

        var pathToYOU = map.getPathTo("YOU")
        var pathToSAN = map.getPathTo("SAN")

        while (pathToSAN[counter] == pathToYOU[counter]) {
            counter++
        }

        return pathToYOU.size - counter + pathToSAN.size - counter
    }

    fun Map<String, Planet>.getPathTo(name: String) : List<Planet> {
        val ret = mutableListOf<Planet>()

        val root = this["COM"]!!

        val stack = mutableListOf<Pair<Int, Planet>>()

        stack.add(Pair(0, root))

        while (stack.last().second != this[name]) {
            val cur = stack.removeAt(stack.lastIndex)

            if (cur.first <= ret.lastIndex) {
                ret.removeAfter(cur.first)
            }

            ret.add(cur.second)

            cur.second.orbitBy.forEach { planet ->
                stack.add(Pair(cur.first+1, planet))
            }
        }

        return ret
    }

    fun <E> MutableList<E>.removeAfter(index: Int) {
        while (this.lastIndex >= index)
            this.removeAt(index)
    }

}
