package info.voidev.mctest.engine

import info.voidev.mctest.engine.config.JUnitMctestConfig
import info.voidev.mctest.engine.discovery.MctestDiscoverySelectorResolver
import info.voidev.mctest.engine.execution.McTestExecutor
import org.junit.platform.engine.EngineDiscoveryRequest
import org.junit.platform.engine.ExecutionRequest
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestEngine
import org.junit.platform.engine.UniqueId
import java.util.Optional

class MctestEngine : TestEngine {

    override fun getId() = MctestEngineDescriptor.ENGINE_ID

    override fun getGroupId() = Optional.of("info.voidev.mctest")

    override fun getArtifactId() = Optional.of("engine")

    override fun discover(discoveryRequest: EngineDiscoveryRequest, uniqueId: UniqueId): TestDescriptor {
        val root = MctestEngineDescriptor(uniqueId, JUnitMctestConfig(discoveryRequest.configurationParameters))

        // Populate the root descriptor by running our different selector resolvers
        MctestDiscoverySelectorResolver.resolve(discoveryRequest, root)

        return root
    }

    override fun execute(request: ExecutionRequest) {
        val root = request.rootTestDescriptor
        require(root is MctestEngineDescriptor)

        McTestExecutor(root, request.engineExecutionListener, request.configurationParameters).execute()
    }

}
