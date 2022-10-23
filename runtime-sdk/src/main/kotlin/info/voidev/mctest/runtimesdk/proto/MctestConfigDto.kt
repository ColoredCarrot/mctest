package info.voidev.mctest.runtimesdk.proto

import java.io.Serializable
import java.net.URI
import kotlin.io.path.Path

class MctestConfigDto(
    private val java_: String,
    private val dataDirectory_: String,
    private val runtimeJar_: String?,
    override val minecraftVersion: String?,
    override val downloadableServerJar: URI?,
    private val serverJarCacheDirectory_: String,
    private val serverDirectory_: String?,
    override val rmiPort: Int,
    override val runtimeJvmArgs: List<String>,
    override val runtimeBootstrapTimeoutMs: Long,
    override val serverStartTimeoutMs: Long,
    override val testPlayerJoinTimeoutMs: Long,
    override val runtimeGlobalTimeoutMs: Long,
) : MctestConfig, Serializable {
    override val java get() = Path(java_)
    override val dataDirectory get() = Path(dataDirectory_)
    override val runtimeJar get() = runtimeJar_?.let(::Path)
    override val serverJarCacheDirectory get() = Path(serverJarCacheDirectory_)
    override val serverDirectory get() = serverDirectory_?.let(::Path)
}
