package xyz.usbpc.utils

inline fun whileCount(condition: () -> Boolean, block: () -> Unit): Int {
    var counter = 0
    while (condition()) {
        counter++
        block()
    }
    return counter
}