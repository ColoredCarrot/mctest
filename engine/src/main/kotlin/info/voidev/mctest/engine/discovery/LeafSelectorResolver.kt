package info.voidev.mctest.engine.discovery

import info.voidev.mctest.engine.plan.tree.LeafTestDescriptor
import info.voidev.mctest.engine.plan.tree.MethodTestDescriptor
import info.voidev.mctest.engine.server.platform.spigot.SpigotPlatform
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersion
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.support.discovery.SelectorResolver
import java.util.Optional

class LeafSelectorResolver : SelectorResolver {

    override fun resolve(selector: UniqueIdSelector, context: SelectorResolver.Context): SelectorResolver.Resolution {
        val uniqueId = selector.uniqueId
        val segment = uniqueId.lastSegment

        if (segment.type != LeafTestDescriptor.SEGMENT_TYPE) {
            return SelectorResolver.Resolution.unresolved()
        }

        val testDescriptor = context.addToParent(
            { DiscoverySelectors.selectUniqueId(uniqueId.removeLastSegment()) },
            { parent ->
                parent as MethodTestDescriptor
                val mcVersion = MinecraftVersion(segment.value)
                Optional.of(LeafTestDescriptor(uniqueId, parent.method, SpigotPlatform, mcVersion))
            }
        ).orElse(null)
            ?: return SelectorResolver.Resolution.unresolved()

        return SelectorResolver.Resolution.match(SelectorResolver.Match.exact(testDescriptor))
    }
}
