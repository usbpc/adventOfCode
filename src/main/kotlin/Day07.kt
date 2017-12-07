import java.io.File
import kotlin.system.measureTimeMillis

data class Program(val name: String, var weight: Int = -1, val children: MutableList<Program>) {
    val subTowerWeight: Int
        get() = weight + children.sumBy {it.subTowerWeight}
    val subTowerSize: Int
        get() {
            var counter = 0
            var crr = children.firstOrNull()
            while (crr != null) {
                counter++
                crr = crr.children.firstOrNull()
            }
            return counter
        }
}

fun main(args: Array<String>) {
    val programMap = mutableMapOf<String, Program>()
    val createTime = measureTimeMillis {
        File("resources/day07.txt").bufferedReader().forEachLine {line ->
            val tokenized = line.split(' ')
            val childList = mutableListOf<Program>()
            if (tokenized.size > 3) {
                tokenized.drop(3).forEach {dirtyChild ->
                    //Remove those annoying comma
                    val child = dirtyChild.filter {it != ','}
                    if (child !in programMap) {
                        programMap[child] = Program(child, children = mutableListOf())
                    }
                    childList.add(programMap[child]!!)
                }
            }
            val weight = tokenized[1].drop(1).dropLast(1).toInt()
            if (tokenized[0] !in programMap) {
                programMap[tokenized[0]] = Program(tokenized[0], children = mutableListOf())
            }
            val current = programMap[tokenized[0]]!!
            current.children.addAll(childList)
            current.weight = weight
        }
    }
    val part1Time = measureTimeMillis {
        //Part 1
        val nodesWithParents = programMap.flatMap {it.value.children}.map {it.name}
        programMap.values.first {it.name !in nodesWithParents}.let {
            println("Part 1: ${it.name}")
        }
    }
    val part2Time = measureTimeMillis {
        //Part 2
        programMap.values.filter {program ->
            program.children.map {it.subTowerWeight}.let {
                it.max() != it.min()
            }
        }.minBy {it.subTowerSize}?.let {minimum ->
            val unique = minimum.children.first {weight ->
                minimum.children.map {it.subTowerWeight}.filter {it == weight.subTowerWeight}.count() == 1
            }
            val common = minimum.children.first {it.subTowerWeight != unique.subTowerWeight}
            val diff = unique.subTowerWeight - common.subTowerWeight
            println("Part 2: ${unique.weight - diff}")
        } ?: println("That didn't go as planned!")
    }
    println("Creating the datastructure took ${createTime}ms part 1 took ${part1Time}ms and part2 took ${part2Time}ms")
}