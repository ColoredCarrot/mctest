package info.voidev.mctest.engine.discovery

import info.voidev.mctest.api.meta.MCVersion
import info.voidev.mctest.engine.MctestEngineDescriptor
import info.voidev.mctest.engine.plan.tree.ClassTestDescriptor
import info.voidev.mctest.engine.plan.tree.LeafTestDescriptor
import info.voidev.mctest.engine.plan.tree.MethodTestDescriptor
import info.voidev.mctest.runtimesdk.util.IsMcTestMethod
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersion
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersioning
import org.junit.platform.engine.DiscoverySelector
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver.InitializationContext
import org.junit.platform.engine.support.discovery.SelectorResolver
import org.junit.platform.engine.support.discovery.SelectorResolver.*
import org.junit.platform.engine.support.discovery.SelectorResolver.Resolution.unresolved
import java.lang.reflect.Method
import java.util.Optional

class MethodSelectorResolver(private val globalCtx: InitializationContext<MctestEngineDescriptor>) : SelectorResolver {

    override fun resolve(selector: MethodSelector, context: Context): Resolution {
        return resolveMethod(selector.javaClass, selector.javaMethod, context)
    }

    override fun resolve(selector: UniqueIdSelector, context: Context): Resolution {
        val uniqueId = selector.uniqueId
        val segment = uniqueId.lastSegment

        if (segment.type != MethodTestDescriptor.SEGMENT_TYPE) {
            return unresolved()
        }

        val testDescriptor = context.addToParent(
            { DiscoverySelectors.selectUniqueId(uniqueId.removeLastSegment()) },
            { parent ->
                parent as ClassTestDescriptor
                val method = parent.testClass.declaredMethods
                    .firstOrNull { MethodSignature(it) == MethodSignature(segment.value) }
                Optional.ofNullable(
                    method?.let { createMethodTestDescriptor(it, parent) }
                )
            }
        ).orElse(null)
            ?: return unresolved()

        return Resolution.match(Match.exact(testDescriptor) { findChildren(testDescriptor, context) })
    }

    private fun resolveMethod(klass: Class<*>, method: Method, context: Context): Resolution {
        if (!IsMcTestMethod.test(method)) {
            return unresolved()
        }

        val testDescriptor = context.addToParent(
            { DiscoverySelectors.selectClass(klass) },
            { parent -> Optional.of(createMethodTestDescriptor(method, parent)) }
        ).orElse(null)
            ?: return unresolved()

        return Resolution.match(Match.exact(testDescriptor) { findChildren(testDescriptor, context) })
    }

    private fun createMethodTestDescriptor(method: Method, parent: TestDescriptor): MethodTestDescriptor {
        return MethodTestDescriptor(
            parent.uniqueId.append(MethodTestDescriptor.SEGMENT_TYPE, MethodSignature(method).asString),
            method
        )
    }

    private fun findChildren(methodTest: MethodTestDescriptor, context: Context): Set<DiscoverySelector> {
        val range = findAllowableVersionRange(methodTest)
        val versions = globalCtx.engineDescriptor.config.minecraftVersionStrategy.apply(range)
        return versions.mapTo(HashSet()) { minecraftVersion ->
            DiscoverySelectors.selectUniqueId(
                methodTest.uniqueId.append(LeafTestDescriptor.SEGMENT_TYPE, minecraftVersion.toString())
            )
        }
    }

    private fun findAllowableVersionRange(method: MethodTestDescriptor): ClosedRange<MinecraftVersion> {
        val annot = method.method.getAnnotation(MCVersion::class.java) ?: MCVersion()
        val min =
            if (annot.min.isNotEmpty()) MinecraftVersion(annot.min) else MinecraftVersioning.supportedRange.start
        val max =
            if (annot.max.isNotEmpty()) MinecraftVersion(annot.max) else MinecraftVersioning.supportedRange.endInclusive
        return min..max
    }
}
