import java.io.File

class NeverNullMap<K, V>(val default: () -> V) {
    val innerMap = mutableMapOf<K, V>()
    operator fun get(key: K): V = innerMap.getOrPut(key, default)
    operator fun set(key: K, value: V) = innerMap.put(key, value)
}

fun main(args: Array<String>) {
    val myMap = NeverNullMap<String, Int> {0}
    val compMap = hashMapOf<String, (Int, Int) -> Boolean>(
            ">" to {x, y -> x > y},
            "<" to {x, y -> x < y},
            ">=" to {x, y -> x >= y},
            "<=" to {x, y -> x <= y},
            "==" to {x, y -> x == y},
            "!=" to {x, y -> x != y}
            )
    val lines = File("resources/day08.txt").readLines()
    var processMax = 0
    for (line in lines) {
        val tokens = line.split(' ')
        if (!compMap[tokens[5]]!!(myMap[tokens[4]], tokens[6].toInt())) continue
        when (tokens[1]) {
            "inc" -> myMap[tokens[0]] += tokens[2].toInt()
            "dec" -> myMap[tokens[0]] -= tokens[2].toInt()
            else -> throw IllegalStateException("What does ${tokens[1]} mean?")
        }
        processMax = maxOf(myMap.innerMap.maxBy {it.value}?.value ?: -1, processMax)
    }
    val doneMax = myMap.innerMap.maxBy {it.value}?.value ?: -1
    println("Part 1: $doneMax")
    println("Part 2: $processMax")
}