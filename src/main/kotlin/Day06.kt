import java.io.File

fun main(args: Array<String>) {
    val listOfInts = mutableListOf<Int>()
    File("resources/day06.txt").bufferedReader().useLines {lines ->
        lines.forEach {line ->
            line.split("\t").forEach { word ->
                listOfInts.add(word.toInt())
            }

        }
    }

    println(solve(listOfInts))
}

fun solve(thing: MutableList<Int>) : Int {
    val knownConfigurations = mutableSetOf<List<Int>>()
    var counter = 0
    while (!knownConfigurations.contains(thing)) {
        knownConfigurations.add(List(thing.size) {i-> thing[i] + 0} )
        counter++
        var biggestIndex = 0

        var biggestNum = 0
        thing.forEachIndexed {index, num ->
            if (num > biggestNum) {
                biggestNum = num
                biggestIndex = index
            }
        }
        thing[biggestIndex] = 0
        while (biggestNum > 0) {
            biggestNum--
            biggestIndex = (biggestIndex + 1) % thing.size
            thing[biggestIndex]++
        }
    }

    return knownConfigurations.size - knownConfigurations.indexOf(thing)
    return counter
}
