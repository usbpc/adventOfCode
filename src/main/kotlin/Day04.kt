import java.io.File

fun main(args: Array<String>) {
    var counter = 0
    var stupidCounter = 0
    File("resources/day04.txt").bufferedReader().useLines {lines ->
        lines.forEach {line ->
            if(line.isValidPassphrase())
                counter++
            if(line.isStupidValidPassphrase())
                stupidCounter++
        }
    }
    println("We have $counter valid passphrases!")
    println("We have $stupidCounter stupid valid passphrases!")
}

fun String.isStupidValidPassphrase() : Boolean {
    val stuff = this.split(" ").map {word ->
        val map = mutableMapOf<Char, Int>()
        word.forEach { char ->
            map.put(char, word.filter {it == char}.count())
        }
        map
    }
    stuff.forEach { word ->
        if (stuff.filter {it == word}.count() > 1) return false
    }
    return true
}

fun String.isValidPassphrase() : Boolean {
    val stuff = this.split(" ")
    stuff.forEach { word ->
        if (stuff.filter { it == word}.count() > 1) return false
    }
    return true
}