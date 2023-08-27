package info.voidev.mctest.engine.execution

import info.voidev.mctest.api.Disabled
import info.voidev.mctest.engine.MctestEngineDescriptor
import info.voidev.mctest.engine.plan.tree.ClassTestDescriptor
import info.voidev.mctest.engine.plan.tree.LeafTestDescriptor
import info.voidev.mctest.engine.plan.tree.MethodTestDescriptor
import info.voidev.mctest.engine.server.TestableMinecraftServer
import info.voidev.mctest.engine.server.TestableServerSession
import info.voidev.mctest.engine.server.platform.spigot.SpigotPlatform
import info.voidev.mctest.engine.util.traverseTree
import info.voidev.mctest.engine.util.unwrapRmiExceptions
import info.voidev.mctest.runtimesdk.versioning.VersionSet
import info.voidev.mctest.runtimesdk.versioning.minecraft.MinecraftVersion
import org.junit.platform.engine.ConfigurationParameters
import org.junit.platform.engine.EngineExecutionListener
import org.junit.platform.engine.TestDescriptor
import org.junit.platform.engine.TestExecutionResult
import org.junit.platform.engine.reporting.ReportEntry
import org.objectweb.asm.Type
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.InvocationTargetException
import kotlin.system.measureTimeMillis

class McTestExecutor(
    private val root: MctestEngineDescriptor,
    private val listener: EngineExecutionListener,
    params: ConfigurationParameters,
) {

    fun execute() {
        listener.executionStarted(root)

        val versions = getAllVersions(root)

        // We don't need to start a server if there's no tests to be executed
        if (versions.isEmpty()) {
            listener.executionFinished(root, TestExecutionResult.successful())
            return
        }

        val serversByVersion = versions.associateWith { version ->
            TestableMinecraftServer(root.config, SpigotPlatform, version)
        }
        try {
            // Spin up the required servers
            // TODO parallelize
            for (server in serversByVersion.values) {
                server.start()

                val serverSession = server.requireActiveSession()
                val tookMillis = measureTimeMillis {
                    serverSession.engine.waitForServerToStart()
                }
                listener.reportingEntryPublished(
                    root,
                    ReportEntry.from("mctest.serverStartTimeMs", "%.2fs".format(tookMillis.toDouble() / 1000))
                )
            }

            // Run the tests
            for (child in root.children) {
                executeClass(child as ClassTestDescriptor, serversByVersion)
            }

            for (server in serversByVersion.values) {
                server.stop()
            }

            listener.executionFinished(root, TestExecutionResult.successful())
        } catch (ex: Throwable) {
            listener.executionFinished(root, TestExecutionResult.failed(ex))
        } finally {
            for (server in serversByVersion.values) {
                server.stop()
            }
        }
    }

    private fun executeClass(testClass: ClassTestDescriptor, servers: Map<MinecraftVersion, TestableMinecraftServer>) {
        getDisabledReason(testClass.testClass)?.also { disabledReason ->
            listener.executionSkipped(testClass, disabledReason)
            return
        }

        listener.executionStarted(testClass)

        try {
            // Run the tests inside the class
            for (child in testClass.children) {
                executeTestMethod(child as MethodTestDescriptor, servers)
            }

            listener.executionFinished(testClass, TestExecutionResult.successful())
        } catch (ex: Exception) {
            listener.executionFinished(testClass, TestExecutionResult.failed(ex))
        }
    }

    private fun executeTestMethod(testMethod: MethodTestDescriptor, servers: Map<MinecraftVersion, TestableMinecraftServer>) {
        listener.executionStarted(testMethod)
        try {

            for (child in testMethod.children) {
                executeLeaf(child as LeafTestDescriptor, servers[child.minecraftVersion]!!.requireActiveSession())
            }

            listener.executionFinished(testMethod, TestExecutionResult.successful())
        } catch (ex: Throwable) {
            listener.executionFinished(testMethod, TestExecutionResult.failed(ex))
            if (ex !is Exception) {
                throw ex
            }
        }
    }

    private fun executeLeaf(leaf: LeafTestDescriptor, serverSession: TestableServerSession) {
        getDisabledReason(leaf.method)?.also { disabledReason ->
            listener.executionSkipped(leaf, disabledReason)
            return
        }

        listener.executionStarted(leaf)

        try {
            // Test method should be executed on runtime side, not engine
            //  (=> use RMI service to request test execution)
            unwrapRmiExceptions {
                serverSession.runtime.runTestMethod(
                    leaf.method.declaringClass.name,
                    leaf.method.name,
                    Type.getMethodDescriptor(leaf.method)
                )
            }

            listener.executionFinished(leaf, TestExecutionResult.successful())
        } catch (ex: Throwable) {
            listener.executionFinished(
                leaf,
                TestExecutionResult.failed((ex as? InvocationTargetException)?.targetException ?: ex)
            )
        }
    }

    private fun getDisabledReason(testMethodOrClass: AnnotatedElement): String? {
        val annot = testMethodOrClass.getAnnotation(Disabled::class.java) ?: return null
        return annot.value.trim().ifEmpty { "$testMethodOrClass is @Disabled" }
    }

    /**
     * Given a root [TestDescriptor], calculate all Minecraft versions for which test servers need to be spun up.
     */
    private fun getAllVersions(testRoot: TestDescriptor): VersionSet<MinecraftVersion> {
        return traverseTree(
            root = testRoot,
            getChildren = { test -> test.children.asSequence() }
        )
            .filterIsInstance<LeafTestDescriptor>()
            .map { test -> test.minecraftVersion }
            .let { VersionSet(it.toList()) }
    }
}
