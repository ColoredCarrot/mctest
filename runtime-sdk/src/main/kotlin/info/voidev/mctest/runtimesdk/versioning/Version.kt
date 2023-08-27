package info.voidev.mctest.runtimesdk.versioning

abstract class Version<V : Version<V>> : Comparable<V> {

    abstract override fun toString(): String

    open fun equals(other: V): Boolean {
        return toString() == other.toString()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || other.javaClass != javaClass) {
            return false
        }
        @Suppress("UNCHECKED_CAST")
        return equals(other as V)
    }

    override fun hashCode() = javaClass.hashCode() xor toString().hashCode()
}
