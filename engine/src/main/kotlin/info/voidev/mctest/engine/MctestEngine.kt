package info.voidev.mctest.engine

import info.voidev.mctest.engine.discovery.DiscoverySelectorResolver
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
        val descriptor = MctestEngineDescriptor(uniqueId)

        // Populate the root descriptor by running our different selector resolvers
        DiscoverySelectorResolver.resolve(discoveryRequest, descriptor)

        // TODO: If running Minecraft version-matrix tests, for each discovered test,
        //  generate one for each version

        return descriptor
    }

    override fun execute(request: ExecutionRequest) {
        val root = request.rootTestDescriptor
        require(root is MctestEngineDescriptor)

        McTestExecutor(root, request.engineExecutionListener, request.configurationParameters).execute()
    }

}
