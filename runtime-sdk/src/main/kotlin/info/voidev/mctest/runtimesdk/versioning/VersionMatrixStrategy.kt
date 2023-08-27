package info.voidev.mctest.runtimesdk.versioning

import java.io.Serializable

sealed class VersionMatrixStrategy : Serializable {

    abstract fun <V : Version<V>> apply(range: ClosedRange<V>): VersionSet<V>

    object MostRecent : VersionMatrixStrategy() {
        override fun <V : Version<V>> apply(range: ClosedRange<V>): VersionSet<V> {
            return VersionSet(range.endInclusive)
        }

        private fun readResolve(): Any = MostRecent
    }

    object OldestAndNewest : VersionMatrixStrategy() {
        override fun <V : Version<V>> apply(range: ClosedRange<V>): VersionSet<V> {
            return VersionSet(range.start, range.endInclusive)
        }

        private fun readResolve(): Any = OldestAndNewest
    }

    //class Matrix(val step: UInt) : VersionMatrixStrategy()
}
