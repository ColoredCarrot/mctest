package info.voidev.mctest.engine.execution

import info.voidev.mctest.api.meta.MCVersion
import info.voidev.mctest.engine.discovery.McTestDescriptor
import info.voidev.mctest.engine.server.platform.MinecraftPlatform
import info.voidev.mctest.engine.util.traverseTree
import org.junit.platform.engine.TestDescriptor

class MinecraftVersionInference {

    /**
     * Calculates an allowable Minecraft version range
     * from the `@MCVersion` annotations of all pending tests.
     *
     * @return A pair `(min, max)` where `min` and `max` are the minimum and maximum allowable versions, respectively.
     *         A `null` allowable version represents an unbounded version constraint.
     */
    fun <V : MinecraftPlatform.Version<V>> calculateAllowableRange(
        testRoot: TestDescriptor,
        minecraftPlatform: MinecraftPlatform<V>,
    ): Pair<V?, V?> {
        return traverseTree(
            root = testRoot,
            getChildren = { test -> test.children.asSequence() }
        )
            .filterIsInstance<McTestDescriptor>()
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
