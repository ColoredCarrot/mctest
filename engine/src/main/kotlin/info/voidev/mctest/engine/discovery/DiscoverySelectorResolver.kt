package info.voidev.mctest.engine.discovery

import info.voidev.mctest.engine.MctestEngineDescriptor
import info.voidev.mctest.runtimesdk.util.IsTestClassWithTests
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.support.discovery.EngineDiscoveryRequestResolver

object DiscoverySelectorResolver {
    private val resolver = EngineDiscoveryRequestResolver
        .builder<MctestEngineDescriptor>()
        .addClassContainerSelectorResolver(IsTestClassWithTests)
        .addSelectorResolver { ctx -> ClassSelectorResolver(ctx.classNameFilter) }
        .addSelectorResolver(MethodSelectorResolver())
        .addTestDescriptorVisitor { TestDescriptor.Visitor(TestDescriptor::prune) }
        .build()

    fun resolve(request: EngineDiscoveryRequest, engineDescriptor: MctestEngineDescriptor) {
        return resolver.resolve(request, engineDescriptor)
    }
}
