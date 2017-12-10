package xyz.usbpc.utils

inline fun <T, R> Iterable<T>.collect(obj: R, appender: R.(T) -> Unit): R {
    this.forEach {
        obj.appender(it)
    }
    return obj
}