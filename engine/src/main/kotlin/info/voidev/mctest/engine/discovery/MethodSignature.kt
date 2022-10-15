package info.voidev.mctest.engine.discovery

import java.lang.reflect.Method

@JvmInline
value class MethodSignature(val asString: String) {

    constructor(m: Method) : this(buildSignature(m))

    companion object {
        private fun buildSignature(m: Method) = buildString {
            append(m.name)
            append('(')
            m.parameterTypes.joinTo(this, ", ") { it.name }
            append(')')
        }
    }
}
