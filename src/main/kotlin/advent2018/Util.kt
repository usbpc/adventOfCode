package advent2018

val numberRegex = """-?\d+""".toRegex()

fun String.extractLongs() : LongArray {
    val numbers = numberRegex.findAll(this)
    val out = LongArray(numbers.count())
    var i = 0
    for (number in numbers) {
        out[i++] = number.value.toLong()
    }
    return out
}

fun String.extractInts() : IntArray {
    val numbers = numberRegex.findAll(this)
    val out = IntArray(numbers.count())
    var i = 0
    for (number in numbers) {
        out[i++] = number.value.toInt()
    }
    return out
}