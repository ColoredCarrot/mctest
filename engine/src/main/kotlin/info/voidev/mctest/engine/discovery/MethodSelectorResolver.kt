package info.voidev.mctest.engine.discovery

import info.voidev.mctest.runtimesdk.util.IsMcTestMethod
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.UniqueId
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.MethodSelector
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.support.discovery.SelectorResolver
import org.junit.platform.engine.support.discovery.SelectorResolver.Context
import org.junit.platform.engine.support.discovery.SelectorResolver.Resolution
import java.lang.reflect.Method
import java.util.Optional

class MethodSelectorResolver : SelectorResolver {

    override fun resolve(selector: MethodSelector, context: Context): Resolution {
        return resolveMethod(selector.javaClass, selector.javaMethod, context)
    }

    override fun resolve(selector: UniqueIdSelector, context: Context): Resolution {
        val uniqueId = selector.uniqueId
        val segment = uniqueId.lastSegment

        if (segment.type != MethodTestDescriptor.SEGMENT_TYPE) {
            return Resolution.unresolved()
        }

        val testDescriptor = context.addToParent(
            { DiscoverySelectors.selectUniqueId(uniqueId.removeLastSegment()) },
            { parent ->
                parent as ClassTestDescriptor
                val method = parent.testClass.declaredMethods.firstOrNull { MethodSignature(it) == MethodSignature(segment.value) }
                Optional.ofNullable(
                    method?.let { createMethodTestDescriptor(it, parent) }
                )
            }
        ).orElse(null)
            ?: return Resolution.unresolved()

        return Resolution.match(SelectorResolver.Match.exact(testDescriptor))
    }

    private fun resolveMethod(klass: Class<*>, method: Method, context: Context): Resolution {
        if (!IsMcTestMethod.test(method)) {
            return Resolution.unresolved()
        }

        val testDescriptor = context.addToParent(
            { DiscoverySelectors.selectClass(klass) },
            { parent -> Optional.of(createMethodTestDescriptor(method, parent)) }
        ).orElse(null)
            ?: return Resolution.unresolved()

        return Resolution.match(SelectorResolver.Match.exact(testDescriptor))
    }

    private fun createMethodTestDescriptor(method: Method, parent: TestDescriptor): MethodTestDescriptor {
        return MethodTestDescriptor(
            createUniqueId(method, parent),
            method
        )
    }

    private fun createUniqueId(method: Method, parent: TestDescriptor): UniqueId {
        val segment = MethodSignature(method).asString

        return parent.uniqueId.append(MethodTestDescriptor.SEGMENT_TYPE, segment)
    }

}
