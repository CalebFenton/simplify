package org.cf.demoapp

import org.cf.smalivm.core.MethodDescriptor
import org.cf.smalivm.core.ReflectionPolicy
import org.cf.smalivm.core.StaticFieldDescriptor
import org.cf.smalivm.core.VirtualType

/** Only the host operations used by the checked-in demo entrypoints are authorized. */
internal fun demoReflectionPolicy(): ReflectionPolicy {
    val string = VirtualType.of("Ljava/lang/String;")
    val builder = VirtualType.of("Ljava/lang/StringBuilder;")
    return ReflectionPolicy(
        // The general-purpose defaults intentionally deny several mutable StringBuilder operations.
        // This embedding explicitly opts in to the exact constructor, append overloads and reads
        // needed by its fixtures. Effects retain the policy's conservative STRONG default.
        exactSafeMethods = setOf(
            MethodDescriptor(string, "<init>", "([B)V"),
            MethodDescriptor(builder, "<init>", "()V"),
            MethodDescriptor(builder, "append", "(I)Ljava/lang/StringBuilder;"),
            MethodDescriptor(builder, "append", "(J)Ljava/lang/StringBuilder;"),
            MethodDescriptor(builder, "append", "(C)Ljava/lang/StringBuilder;"),
            MethodDescriptor(builder, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
            MethodDescriptor(builder, "toString", "()Ljava/lang/String;"),
            MethodDescriptor(VirtualType.of("Ljava/lang/Integer;"), "parseInt", "(Ljava/lang/String;)I"),
            MethodDescriptor(VirtualType.of("Ljava/lang/Long;"), "parseLong", "(Ljava/lang/String;)J"),
        ),
        // A real non-null receiver prevents spurious null-receiver paths at println. Reading this
        // field does not authorize any PrintStream method: our exact emulator handles the output.
        exactSafeStaticFields = setOf(
            StaticFieldDescriptor(
                VirtualType.of("Ljava/lang/System;"),
                "out",
                VirtualType.of("Ljava/io/PrintStream;"),
            ),
        ),
    )
}
