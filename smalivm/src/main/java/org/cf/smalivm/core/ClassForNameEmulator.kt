package org.cf.smalivm.core

import org.cf.util.ClassNameUtils

/** Implements the one-argument Class.forName contract without initializing a host class. */
internal class ClassForNameEmulator(private val classIndex: ClassIndex) : EmulatorHandler {
    override fun execute(call: EmulatorInvocation): SynchronousCallResult {
        val argument = call.arguments.single()
        if (argument !is EmulatorValue.Known) {
            return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(call.returnType))
        }
        val binaryName = argument.javaValue
        if (binaryName == null) {
            return targetThrow(VirtualType.of("Ljava/lang/NullPointerException;"), NullPointerException())
        }
        if (binaryName !is String) {
            return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(call.returnType))
        }
        if (!isClassForNameBinaryName(binaryName)) return classNotFound(binaryName)

        val type = try {
            VirtualType.of(ClassNameUtils.binaryToInternal(binaryName))
        } catch (_: IllegalArgumentException) {
            return classNotFound(binaryName)
        }
        val indexed = classIndex.isLoadable(type)
        if (indexed && type.isReference && !type.isArray && !classIndex.isFrameworkClass(type)) {
            when (call.classInitStatus.actionFor(type)) {
                ClassInitAction.Proceed -> Unit
                ClassInitAction.StartInitialization -> return SynchronousCallResult.NeedsClassInit(type)
                is ClassInitAction.ThrowFailed ->
                    return IntrinsicCallResult(IntrinsicOperation.RouteFailedClassInit(type))
            }
        }
        val classToken = classIndex.loadClassConstant(type.descriptor)
        if (!indexed && classToken == null) return classNotFound(binaryName)
        if (classToken == null) {
            return IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(call.returnType))
        }
        return SynchronousCallResult.Returned(
            HostReturn.Value(HostValue.Reference(classToken, call.returnType)),
        )
    }

    private fun classNotFound(binaryName: String): SynchronousCallResult = targetThrow(
        VirtualType.of("Ljava/lang/ClassNotFoundException;"),
        ClassNotFoundException(binaryName),
    )

    private fun targetThrow(type: VirtualType, throwable: Throwable): SynchronousCallResult =
        SynchronousCallResult.TargetThrew(HostThrowable(type, throwable))

    private fun isClassForNameBinaryName(name: String): Boolean {
        if (!name.startsWith('[')) return isBinaryClassName(name)
        val componentStart = name.indexOfFirst { it != '[' }
        if (componentStart !in 1..MAX_ARRAY_DIMENSIONS) return false
        return when (name[componentStart]) {
            'Z', 'B', 'C', 'S', 'I', 'J', 'F', 'D' -> componentStart == name.lastIndex
            'L' -> name.endsWith(';') &&
                componentStart + 2 < name.length &&
                isBinaryClassName(name.substring(componentStart + 1, name.lastIndex))
            else -> false
        }
    }

    private fun isBinaryClassName(name: String): Boolean {
        if (name in PRIMITIVE_NAMES || name.any { it == '/' || it == ';' || it == '[' || it == '\u0000' }) {
            return false
        }
        return name.split('.').all(String::isNotEmpty)
    }

    private companion object {
        const val MAX_ARRAY_DIMENSIONS = 255
        val PRIMITIVE_NAMES = setOf(
            "boolean", "byte", "char", "short", "int", "long", "float", "double", "void",
        )
    }
}
