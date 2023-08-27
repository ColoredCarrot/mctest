package info.voidev.mctest.engine.execution

import info.voidev.mctest.api.meta.MCVersion
import info.voidev.mctest.engine.plan.tree.AbstractElementBasedMcTestDescriptor
import info.voidev.mctest.engine.server.platform.MinecraftPlatform
import info.voidev.mctest.engine.util.traverseTree
import info.voidev.mctest.runtimesdk.versioning.Version
import org.junit.platform.engine.TestDescriptor

@Deprecated("This class is no longer used")
class MinecraftVersionInference {

    /**
     * Calculates a Minecraft version range
     * from the `@MCVersion` annotations of all pending tests.
     *
     * @return A pair `(min, max)` where `min` and `max` are the minimum and maximum declared versions, respectively.
     *         A `null` version represents an unbounded version constraint.
     */
    fun <V : Version<V>> getLowestAndHighestVersions(
        testRoot: TestDescriptor,
        minecraftPlatform: MinecraftPlatform<V>,
    ): Pair<V?, V?> {
        return traverseTree(
            root = testRoot,
            getChildren = { test -> test.children.asSequence() }
        )
            .filterIsInstance<AbstractElementBasedMcTestDescriptor>()
            .map { test ->
                val annot = test.element.getAnnotation(MCVersion::class.java) ?: MCVersion()
                val min = if (annot.min.isNotEmpty()) minecraftPlatform.resolveVersion(annot.min) else null
                val max = if (annot.max.isNotEmpty()) minecraftPlatform.resolveVersion(annot.max) else null
                min to max
            }
            .fold(Pair<V?, V?>(null, null)) { (accMin, accMax), (min, max) ->

                val newMin = when {
                    accMin == null -> min
                    min == null -> accMin
                    min < accMin -> min
                    else -> accMin
                }

                val newMax = when {
                    accMax == null -> max
                    max == null -> accMax
                    max > accMax -> max
                    else -> accMax
                }

                newMin to newMax
            }

    }
}
