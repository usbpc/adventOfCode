package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.NeverNullMap

class Day14(override val adventOfCode: AdventOfCode) : Day {
    override val day = 14

    val regex = Regex("(\\d+) (\\w+)")

    val input = adventOfCode.getInput(2019, day).lines()


    data class Element(val quantity: Int, val name: String) {
        val producedBy = mutableMapOf<Element, Int>()

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

    override fun part1() : Any {

        val elemMap = mutableMapOf<String, Element>()

        elemMap["ORE"] = Element(1, "ORE")

        input.forEach { line ->
            val (_, output) = line.split("=>")

            val (outName, outQty) = regex.find(output)!!.let {
                Pair(it.groups[2]!!.value, it.groups[1]!!.value.toInt())
            }

            elemMap[outName] = Element(outQty, outName)
        }

        input.forEach { line ->
            val (inputs, output) = line.split("=>")
            val splitInputs = inputs.split(",")

            val (outName, _) = regex.find(output)!!.let {
                Pair(it.groups[2]!!.value, it.groups[1]!!.value.toInt())
            }

            splitInputs.map {
                regex.find(it)!!.let {
                    Pair(it.groups[2]!!.value, it.groups[1]!!.value.toInt())
                }
            }.forEach { (elemName, qty) ->
                elemMap[elemName]!!.let { toProduce ->
                    elemMap[outName]!!.producedBy[toProduce] = qty
                }
            }
        }

        val needed = NeverNullMap<Element, Int> {0}

        needed[elemMap["FUEL"]!!] = 1

        while (!needed.containsKey(elemMap["ORE"]!!) || needed.size > 1) {
            val cur = needed.keys.maxBy { it.distanceToOre() }!!
            val quantity = needed[cur]

            needed.remove(cur)

            var timesNeeded = quantity / cur.quantity
            if (quantity % cur.quantity != 0)
                timesNeeded += 1

            cur.producedBy.forEach { (elem, qty) ->
                needed[elem] += timesNeeded * qty
            }
        }

        return needed.values.single()
    }

    override fun part2() : Any {
        return ""
    }
}
