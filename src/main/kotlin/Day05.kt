import java.io.File
import kotlin.system.measureNanoTime
import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val listOfNumber = mutableListOf<Int>()
    File("resources/day05.txt").bufferedReader().useLines {lines ->
        lines.forEach {line ->
            listOfNumber.add(line.toInt())
        }
    }
    var part1 = 0L
    var part2 = 0L
    val time1 = measureTimeMillis {
        part1 = countStepsUntilMazeExit(listOfNumber.toTypedArray())
    }
    val time2 = measureTimeMillis {
        part2 = countStepsUntilMazeExitPart2(listOfNumber.toTypedArray())
    }
    println("Part 1 took ${time1}ms and solution is $part1")
    println("Part 2 took ${time2}ms and solution is $part2")
}

fun countStepsUntilMazeExit(input: Array<Int>): Long {
    var currPos = 0
    var counter: Long = 0
    while (currPos < input.size && currPos >= 0) {
        counter++
        currPos += input[currPos]++
    }
    return counter
}

fun countStepsUntilMazeExitPart2(input: Array<Int>): Long {
    var currPos = 0
    var counter: Long = 0
    while (currPos < input.size && currPos >= 0) {
        counter++
        currPos +=
                if (input[currPos] >= 3) {
                    input[currPos]--
                } else {
                    input[currPos]++
                }
    }
    return counter
}