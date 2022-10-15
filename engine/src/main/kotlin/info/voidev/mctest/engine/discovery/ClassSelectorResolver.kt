package info.voidev.mctest.engine.discovery

import info.voidev.mctest.runtimesdk.util.IsTestClassWithTests
import org.junit.platform.commons.util.ReflectionUtils
import org.junit.platform.engine.discovery.ClassSelector
import org.junit.platform.engine.discovery.DiscoverySelectors
import org.junit.platform.engine.discovery.UniqueIdSelector
import org.junit.platform.engine.support.discovery.SelectorResolver
import org.junit.platform.engine.support.discovery.SelectorResolver.Match
import org.junit.platform.engine.support.discovery.SelectorResolver.Resolution
import org.junit.platform.engine.support.discovery.SelectorResolver.Resolution.unresolved
import java.util.Optional
import java.util.function.Predicate

class ClassSelectorResolver(private val classNameFilter: Predicate<String>) : SelectorResolver {

    override fun resolve(selector: ClassSelector, context: SelectorResolver.Context): Resolution {
        return resolveClass(selector.javaClass, context)
    }

    override fun resolve(selector: UniqueIdSelector, context: SelectorResolver.Context): Resolution {
        val segment = selector.uniqueId.lastSegment

        if (segment.type != ClassTestDescriptor.SEGMENT_TYPE) {
            return unresolved()
        }

        val c = ReflectionUtils.tryToLoadClass(segment.value).toOptional().orElse(null)
            ?: return unresolved()

        return resolveClass(c, context)
    }

    private fun resolveClass(c: Class<*>, context: SelectorResolver.Context): Resolution {
        if (!IsTestClassWithTests.test(c) || !classNameFilter.test(c.name)) {
            return unresolved()
        }

        val testDescriptor = context.addToParent { parent ->
            Optional.of(
                ClassTestDescriptor(
                    parent.uniqueId.append(ClassTestDescriptor.SEGMENT_TYPE, c.name),
                    c
                )
            )
        }.orElse(null)
            ?: return unresolved()

        return Resolution.match(Match.exact(testDescriptor) {
            // Find child tests (=> the test methods in this class)
            // TODO: Support inherited test methods as well as nested classes
            c.declaredMethods.mapTo(LinkedHashSet()) { m ->
                DiscoverySelectors.selectMethod(c, m)
            }
        })
    }

}
