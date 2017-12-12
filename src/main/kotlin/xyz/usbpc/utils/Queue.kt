package xyz.usbpc.utils

class Queue<E> : Collection<E> {
    private val backing: MutableList<E> = mutableListOf()
    override val size: Int
        get() = backing.size

    override fun contains(element: E): Boolean {
        return backing.contains(element)
    }

    override fun containsAll(elements: Collection<E>): Boolean {
        return backing.containsAll(elements)
    }

    override fun iterator(): Iterator<E> {
        return backing.iterator()
    }

    fun add(obj: E) = backing.add(obj)
    fun remove(): E = backing.removeAt(0)

    override fun isEmpty(): Boolean = backing.isEmpty()
}