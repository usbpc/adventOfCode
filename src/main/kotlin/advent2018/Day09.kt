package advent2018

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.util.*

class Day09(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 9
    private val input = adventOfCode.getInput(2018, day).extractInts()

    private fun Int.isWorthPoints() = (this % 23) == 0

    private fun MutableList<Int>.insertMarble(curM: Int, nextNum: Int) : Int {
        val highIndex = (curM + 2) % this.size
        return if (highIndex == 0) {
            this.add(nextNum)
            this.size-1
        } else {
            this.add(highIndex, nextNum)
            highIndex
        }
    }

    private fun MutableList<Int>.removeMarble(curM: Int) : Pair<Int, Int> {
        val index = (curM-7+this.size) % this.size
        return index to this.removeAt(index)
    }

    private fun MutableList<Int>.printLikeAoc(elf: Int, curM: Int) : String {
        val out = StringBuilder()
        out.append('[')
        out.append(elf)
        out.append("]  ")
        this.withIndex()
                .forEach { (i, num) ->
            if (i == curM) {
                out.setCharAt(out.lastIndex, '(')
                out.append(num)
                out.append(')')
            } else {
                out.append(num)
            }
                    out.append(' ')
        }
        return out.toString()
    }

    class CirclePlayfield<E>(init: E) {
        var head: ListItem<E>
        init {
            head = ListItem(item = init)
        }

        fun insertMarble(num: E) {
            val before = head.next
            val after = before.next

            val new = ListItem(num)
            new.prev = before
            new.next = after

            before.next = new
            after.prev = new

            head = new
        }

        fun removeMarble() : E {
            val toRemove = head.prev.prev.prev.prev.prev.prev.prev

            toRemove.prev.next = toRemove.next
            toRemove.next.prev = toRemove.prev

            return toRemove.item
        }

        class ListItem<E>(val item: E) {
            var next: ListItem<E> = this
            var prev: ListItem<E> = this

        }
    }

    private fun calculateWinningScore(maxElf: Int, maxMarble: Int) : Long {
        val playarea = mutableListOf(0)

        val scores = LongArray(maxElf)

        var curMarble = 0
        var nextMarbleNum = 1

        var currentElf = 0

        while (nextMarbleNum <= maxMarble) {
            curMarble = if (nextMarbleNum.isWorthPoints()) {
                val (cm, points) = playarea.removeMarble(curMarble)
                scores[currentElf] += points.toLong() + nextMarbleNum++
                cm
            } else {
                playarea.insertMarble(curMarble, nextMarbleNum++)
            }
            //println(playarea.printLikeAoc(currentElf+1, curMarble))
            currentElf = (currentElf+1) % maxElf
        }
        return scores.max()!!
    }

    override fun part1(): String {
        return "" + calculateWinningScore(input[0], input[1])
    }

    override fun part2(): String {
        return "" + calculateWinningScore(input[0], input[1])
    }
}