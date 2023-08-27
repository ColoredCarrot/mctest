package info.voidev.mctest.engine.plan.tree

import info.voidev.mctest.engine.server.platform.MinecraftPlatform
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersion
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.support.descriptor.MethodSource
import java.lang.reflect.Method

/**
 * Descriptor for a concrete test method + Minecraft platform + platform version combination.
 */
class LeafTestDescriptor(
    uniqueId: UniqueId,
    val method: Method,
    val minecraftPlatform: MinecraftPlatform<MinecraftVersion>,
    val minecraftVersion: MinecraftVersion,
) : AbstractElementBasedMcTestDescriptor(
    uniqueId = uniqueId,
    displayName = minecraftVersion.toString(),
    element = method,
    source = MethodSource.from(method),
) {

    override fun getType() = TestDescriptor.Type.TEST

    companion object {
        const val SEGMENT_TYPE = "leaf"
    }
}
