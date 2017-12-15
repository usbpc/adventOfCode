package xyz.usbpc.utils

inline fun <T, R> Iterable<T>.collect(obj: R, appender: R.(T) -> Any?): R {
    this.forEach {
        obj.appender(it)
    }
    return obj
}

fun <T: Any> Sequence<T>.constrainCount(count: Long): Sequence<T> = CountConstrainedSequence(this, count)
private class CountConstrainedSequence<out T: Any>(private val sequence: Sequence<T>, private var counter: Long): Sequence<T> {
    override fun iterator(): Iterator<T> {
        return object: Iterator<T> {
            private val internalIterator = sequence.iterator()
            override fun hasNext(): Boolean {
                return internalIterator.hasNext() && --counter > 0
            }

            override fun next(): T = internalIterator.next()

        }
    }
}