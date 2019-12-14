package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.NeverNullMap

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day = 14

    val regex = Regex("(\\d+) (\\w+)")

    val input = adventOfCode.getInput(2019, day).lines()


    data class Element(val quantity: Long, val name: String) {
        val producedBy = mutableMapOf<Element, Long>()

        fun distanceToOre() : Int {
            if (name == "ORE")
                return 0

            return producedBy.keys.map { it.distanceToOre() }.max()!! + 1
        }

        override fun toString(): String = buildString {
            producedBy.forEach { (elem, qty) ->
                this.append(qty)
                this.append(" ")
                this.append(elem.name)
                this.append(", ")
            }

            if (producedBy.isNotEmpty()) {
                this.deleteCharAt(this.lastIndex)
                this.deleteCharAt(this.lastIndex)
            }

            this.append(" => ")

            this.append(quantity)
            this.append(' ')
            this.append(name)
        }
    }

    fun generateElementMap() : Map<String, Element> {
        val elemMap = mutableMapOf<String, Element>()

        elemMap["ORE"] = Element(1, "ORE")

        input.forEach { line ->
            val (_, output) = line.split("=>")

            val (outName, outQty) = regex.find(output)!!.let {
                Pair(it.groups[2]!!.value, it.groups[1]!!.value.toLong())
            }

            elemMap[outName] = Element(outQty, outName)
        }

        input.forEach { line ->
            val (inputs, output) = line.split("=>")
            val splitInputs = inputs.split(",")

            val (outName, _) = regex.find(output)!!.let {
                Pair(it.groups[2]!!.value, it.groups[1]!!.value.toLong())
            }

            splitInputs.map {
                regex.find(it)!!.let {
                    Pair(it.groups[2]!!.value, it.groups[1]!!.value.toLong())
                }
            }.forEach { (elemName, qty) ->
                elemMap[elemName]!!.let { toProduce ->
                    elemMap[outName]!!.producedBy[toProduce] = qty
                }
            }
        }

        return elemMap
    }

    fun getOreRequired(fuelWanted: Long) : Long {
        val elemMap = generateElementMap()

        val needed = NeverNullMap<Element, Long> {0}

        needed[elemMap["FUEL"]!!] = fuelWanted

        while (!needed.containsKey(elemMap["ORE"]!!) || needed.size > 1) {
            val cur = needed.keys.maxBy { it.distanceToOre() }!!
            val quantity = needed[cur]

            needed.remove(cur)

            var timesNeeded = quantity / cur.quantity
            if (quantity % cur.quantity != 0L)
                timesNeeded += 1

            cur.producedBy.forEach { (elem, qty) ->
                needed[elem] += timesNeeded * qty
            }
        }

        return needed.values.single()
    }

    override fun part1(): Any = getOreRequired(1)

    override fun part2() : Any {
        var upper = 1000000000000L
        var lower = 1000000000000L / getOreRequired(1)



        while (upper - lower > 1) {
            val middle = (upper + lower) / 2

            val oreRequired = getOreRequired(middle)

            if (oreRequired > 1000000000000L) {
                upper = middle
            } else {
                lower = middle
            }
        }

        return lower
    }
}
