package info.voidev.mctest.engine.discovery

import info.voidev.mctest.engine.MctestEngineDescriptor
import info.voidev.mctest.runtimesdk.util.IsTestClassWithTests
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver

object MctestDiscoverySelectorResolver {

    private val resolver = EngineDiscoveryRequestResolver
        .builder<MctestEngineDescriptor>()
        // Resolve packages, modules, etc. to lists of class selectors
        .addClassContainerSelectorResolver(IsTestClassWithTests)
        .addSelectorResolver { ctx -> ClassSelectorResolver(ctx.classNameFilter) }
        .addSelectorResolver { ctx -> MethodSelectorResolver(ctx) }
        .addSelectorResolver(LeafSelectorResolver())
        .addTestDescriptorVisitor { TestDescriptor.Visitor(TestDescriptor::prune) }
        .build()

    fun resolve(request: EngineDiscoveryRequest, engineDescriptor: MctestEngineDescriptor) {
        return resolver.resolve(request, engineDescriptor)
    }
}
