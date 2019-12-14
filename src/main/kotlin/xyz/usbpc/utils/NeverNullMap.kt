package xyz.usbpc.utils

class NeverNullMap<K, V> private constructor(private val backing: MutableMap<K, V>, val default: () -> V) :
        MutableMap<K, V> by backing {

    constructor(default: () -> V) : this(mutableMapOf(), default)

    override operator fun get(key: K): V = backing.getOrPut(key, default)
    operator fun set(key: K, value: V) = backing.put(key, value)

    override fun toString(): String {
        return backing.toString()
    }
}