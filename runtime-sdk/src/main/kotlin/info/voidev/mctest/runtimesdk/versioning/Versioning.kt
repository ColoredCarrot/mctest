package info.voidev.mctest.runtimesdk.versioning

abstract class Versioning<V : Version<V>>(val supportedRange: ClosedRange<V>) {

    @Throws(MalformedVersionException::class)
    abstract fun resolve(versionString: String): V
}
