package advent2018

import advent2017.Day08
import xyz.usbpc.aoc.Day
import xyz.usbpc.aoc.inputgetter.AdventOfCode
import java.lang.IllegalStateException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Day04(override val adventOfCode: AdventOfCode) : Day {
    override val day: Int = 4

    private sealed class LogEntryText {
        object WakesUp : LogEntryText()
        object FallsAsleep : LogEntryText()
        class ShiftBegins(val id: Int) : LogEntryText()
    }

    private data class LogEntry(val timestamp: LocalDateTime, val value: LogEntryText)

    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
    private val pattern = """\[(\d{4}-\d{2}-\d{2} \d{2}:\d{2})] (Guard #(\d+) begins shift|wakes up|falls asleep)""".toRegex()

    private val input = adventOfCode.getInput(2018, day).lines()
            .map { line ->
                val match = pattern.find(line) ?: throw IllegalStateException("While trying to parse $line")
                val timestamp = LocalDateTime.parse(match.groups[1]!!.value, formatter)
                val value = if (match.groups[3] != null) {
                    LogEntryText.ShiftBegins(match.groups[3]!!.value.toInt())
                } else {
                    if (match.groups[2]!!.value == "wakes up") {
                        LogEntryText.WakesUp
                    } else {
                        LogEntryText.FallsAsleep
                    }
                }
                LogEntry(timestamp, value)
            }.sortedBy { it.timestamp }

    private val guardMap = Day08.NeverNullMap<Int, IntArray> { IntArray(60) }

    override fun part1(): String {
        var curGuard = 0
        var index = 0
        while (index < input.size) {
            val entry = input[index]
            when(entry.value) {
                is LogEntryText.ShiftBegins -> curGuard = entry.value.id
                is LogEntryText.FallsAsleep -> {
                    val array = guardMap[curGuard]
                    for (i in entry.timestamp.minute until input[index+1].timestamp.minute) {
                        array[i]++
                    }
                    index++
                }
                is LogEntryText.WakesUp -> throw IllegalStateException("We should consume this on falls asleep, problem at index $index")
            }
            index++
        }

        val mostAsleep = guardMap.toList().maxBy { (_, array ) -> array.sum() }!!.first

        return "${mostAsleep * guardMap[mostAsleep].withIndex().maxBy { (_, value) -> value }!!.index }"
    }

    override fun part2(): String {

        val consistentAsleep = guardMap.toList().maxBy { (_, array ) -> array.max()!! }!!.first

        return "${ consistentAsleep * guardMap[consistentAsleep].withIndex().maxBy { (_, value) -> value }!!.index }"

    }


}