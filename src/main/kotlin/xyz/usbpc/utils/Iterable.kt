package xyz.usbpc.utils

inline fun <T, R> Iterable<T>.collect(obj: R, appender: R.(T) -> Any?): R {
    this.forEach {
        obj.appender(it)
    }
    return obj
}