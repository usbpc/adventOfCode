import java.io.File

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
    File("resources/day07.txt").bufferedReader().forEachLine {line ->
        val tokenized = line.split(' ')
        val childList = mutableListOf<Program>()
        if (tokenized.size > 3) {
            tokenized.drop(3).forEach {dirtyChild ->
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
    //Part 1
    programMap.filter {curr ->
        !programMap.flatMap {it.value.children}.map {it.name}.contains(curr.key)
    }.forEach {
        println("Part 1: ${it.key}")
    }
    //Part 2
    programMap.values.filter {program ->
        program.children.map {it.subTowerWeight}.let {
            it.max() != it.min()
        }
    }.minBy {it.subTowerSize}?.let {minimum ->
        val unique = minimum.children.first {weigth ->
            minimum.children.map {it.subTowerWeight}.filter {it == weigth.subTowerWeight}.count() == 1
        }
        val common = minimum.children.first {it.subTowerWeight != unique.subTowerWeight}
        val diff = unique.subTowerWeight - common.subTowerWeight
        println("Part 2: ${unique.weight - diff}")
    } ?: println("That didn't go as planned!")

}