package info.voidev.mctest.runtimesdk.versioning

import java.util.TreeSet

class VersionSet<V : Version<V>> private constructor(val versions: List<V>, primary_ctor_marker: Int) :
    List<V> by versions {

    constructor(versions: Collection<V>) : this(TreeSet(versions).toList(), 0)

    constructor(vararg versions: V) : this(versions.asList())
}
