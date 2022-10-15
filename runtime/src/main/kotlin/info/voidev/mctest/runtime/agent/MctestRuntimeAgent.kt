@file:JvmName("MctestRuntimeAgent")

package info.voidev.mctest.runtime.agent

import java.lang.instrument.Instrumentation

fun premain(agentArgs: String?, inst: Instrumentation) {
//    inst.addTransformer(MctestClassFileTransformer(
//        installCheckClassAdapter = true,
//        installTraceClassVisitor = true,
//    ))
}
