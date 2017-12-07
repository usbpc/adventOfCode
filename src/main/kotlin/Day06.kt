import java.io.File

fun main(args: Array<String>) {
    val listOfInts = mutableListOf<Int>()
    File("resources/day06.txt").bufferedReader().useLines {lines ->
        lines.forEach {line ->
            line.split("\t").forEach {word ->
                listOfInts.add(word.toInt())
            }

        }
    }
    println(solve(listOfInts))
}

fun solve(thing: MutableList<Int>): Int {
    if (thing.isEmpty()) throw IllegalArgumentException("Thing is not allowed to be empty!")
    val knownConfigurations = mutableSetOf<List<Int>>()
    var counter = 0
    while (thing !in knownConfigurations) {
        knownConfigurations.add(thing.toList())
        counter++

        var (biggestIndex, biggestNum) = thing.withIndex().maxBy {it.value}!!

        thing[biggestIndex] = 0
        while (biggestNum > 0) {
            biggestNum--
            biggestIndex = (biggestIndex + 1) % thing.size
            thing[biggestIndex]++
        }
    }

    //return knownConfigurations.size - knownConfigurations.indexOf(thing)
    return counter
}