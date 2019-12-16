package advent2019

import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import xyz.usbpc.utils.repeat
import java.lang.StringBuilder
import kotlin.math.min

class Day16(override val adventOfCode: AdventOfCode) : Day {
    override val day = 16

    val input = adventOfCode.getInput(2019, day).map { Character.getNumericValue(it) }
    //val input = "12345678".map { Character.getNumericValue(it) }
    //val input = "02935109699940807407585447034323".map { Character.getNumericValue(it) }

    class PatternGenerator(val num: Int): Iterator<Int>, Iterable<Int> {
        override fun iterator(): Iterator<Int> = this

        val startOffset = num - 1
        val endOffset = 2*startOffset

        val posToPosOffset = num*4
        val posToNegOff = num*2

        var cur = 0

        companion object {
            val basePattern = listOf(0, 1, 0, -1)
        }

        fun getPositiveRangesUntil(lastIndex: Int) : List<Pair<Int, Int>>{
            val ret = mutableListOf<Pair<Int, Int>>()

            var counter = 0
            var cur : Pair<Int, Int>

            do {
                cur = positiveRange(counter++)
                ret.add(cur)
            } while (cur.second <= lastIndex)

            if (ret.last().first > lastIndex) {
                return ret.dropLast(1)
            }

            return ret.apply {
                val tmp = ret.last().first to lastIndex
                removeAt(this.lastIndex)
                add(tmp)
            }
        }

        fun getNegativeRangesUntil(lastIndex: Int) : List<Pair<Int, Int>>{
            val ret = mutableListOf<Pair<Int, Int>>()

            var counter = 0
            var cur : Pair<Int, Int>

            do {
                cur = negativeRange(counter++)
                ret.add(cur)
            } while (cur.second <= lastIndex)

            if (ret.last().first > lastIndex) {
                return ret.dropLast(1)
            }

            return ret.apply {
                val tmp = ret.last().first to lastIndex
                removeAt(this.lastIndex)
                add(tmp)
            }
        }

        fun positiveRange(off: Int) : Pair<Int, Int> {
            return startOffset + off*posToPosOffset to endOffset + off*posToPosOffset
        }

        fun negativeRange(off: Int): Pair<Int, Int> {
            positiveRange(off).let {
                return it.first + posToNegOff to it.second + posToNegOff
            }
        }

        override fun hasNext(): Boolean = true
        override fun next() = basePattern[((cur+++1)/num) % basePattern.size]
    }

    infix fun Int.mmod(other: Int) = ((this % other) + other) % other

    infix fun Int.absmod(other: Int) = abs(this % other)

    override fun part1() : Any {
        var cur = input.toMutableList()
        var other = input.toMutableList()

        val ranges = (1..cur.size).map { PatternGenerator(it) }.map { it.getNegativeRangesUntil(cur.lastIndex) to it.getPositiveRangesUntil(cur.lastIndex) }.toList()

        //println(ranges.first().first)

        repeat(100) {
            for (i in 0..cur.lastIndex) {
                val pos = ranges[i].second.map { cur.subList(it.first, it.second+1) }.map {
                    //println("This is one of the pos lists: $it")
                    it.sum()
                }.sum()
                val neg = ranges[i].first.map { cur.subList(it.first, it.second+1) }.map {
                    //println("This is one of the neg lists: $it")
                    it.sum()
                }.sum()
                //println("pos: $pos, neg $neg")
                other[i] = (pos - neg) absmod 10
            }
            val tmp = cur
            cur = other
            other = tmp
            //println(cur)
        }

        return cur.take(8).fold(StringBuilder()) { a, b -> a.append(b) }
    }

    fun <E> List<E>.subListWindowed(size: Int, step: Int): List<List<E>> {
        val ret = mutableListOf<List<E>>()
        var cur = 0
        while (cur < this.size) {
            ret.add(this.subList(cur, min(cur+size, this.size)))
            cur += step
        }
        return ret
    }

    override fun part2() : Any {
        //val cur = input.toMutableList()
        val cur = input.repeat(10_000).flatten().toMutableList()
        val list = IntArray(cur.size+1)


        //println(ranges.first().first)

        repeat(100) {
            println("Am in iteration $it")
            list[list.lastIndex-1] = cur.last()
            for (i in cur.size-2 downTo 0) {
                list[i] = list[i+1] + cur[i]
            }
           for (pos in 0 until cur.size) {
               var chunk = pos
               var tmp = 0
               while (chunk < cur.size) {

                   tmp += list[chunk] - list[min(chunk + pos, cur.lastIndex)+1]

                   chunk += (pos+1) * 2

                   if (chunk >= cur.size)
                       continue

                   tmp -= list[chunk] - list[min(chunk + pos, cur.lastIndex)+1]

                   chunk += (pos+1) * 2
               }
               cur[pos] = abs(tmp % 10)
           }
        }

        val toSkip = input.take(7).fold( StringBuilder() ) { builder, i -> builder.append(i) }.toString().toInt()

        //return cur.take(8).fold(StringBuilder()) { a, b -> a.append(b) }
        return cur.subList(toSkip, toSkip+8).fold(StringBuilder()) { a, b -> a.append(b) }
    }
}
