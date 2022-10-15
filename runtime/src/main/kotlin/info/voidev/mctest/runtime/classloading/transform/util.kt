package info.voidev.mctest.runtime.classloading.transform

import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.util.CheckClassAdapter
import org.objectweb.asm.util.TraceClassVisitor
import java.io.PrintWriter

fun transformClassFileBytes(
    bytes: ByteArray,
    classVisitorFactory: (ClassVisitor) -> ClassVisitor,
    classLoader: ClassLoader,
    verify: Boolean = true,
    traceToStderr: Boolean = false,
): ByteArray {
    val cr = ClassReader(bytes)
    val cw = object : ClassWriter(cr, COMPUTE_MAXS or COMPUTE_FRAMES) {
        override fun getClassLoader() = classLoader
    }

    var cv: ClassVisitor = cw
    if (traceToStderr) cv = TraceClassVisitor(cv, PrintWriter(System.out))
    if (verify) cv = CheckClassAdapter(cv)
    cv = classVisitorFactory(cv)

    cr.accept(cv, 0)

    return cw.toByteArray()
}
