package org.cf.demoapp

import org.cf.smalivm.core.MethodSignature
import org.cf.smalivm.core.ReflectionPolicy
import org.cf.smalivm.core.FieldSignature
import org.cf.smalivm.core.VirtualType

/** Authorizes only the host operations the demo guest methods use. */
internal fun demoReflectionPolicy(): ReflectionPolicy {
    val string = VirtualType.of("Ljava/lang/String;")
    val builder = VirtualType.of("Ljava/lang/StringBuilder;")
    return ReflectionPolicy(
        // The defaults deny mutable StringBuilder operations; the demo opts in to exactly these.
        exactSafeMethods = setOf(
            MethodSignature(string, "<init>", "([B)V"),
            MethodSignature(builder, "<init>", "()V"),
            MethodSignature(builder, "append", "(I)Ljava/lang/StringBuilder;"),
            MethodSignature(builder, "append", "(J)Ljava/lang/StringBuilder;"),
            MethodSignature(builder, "append", "(C)Ljava/lang/StringBuilder;"),
            MethodSignature(builder, "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;"),
            MethodSignature(builder, "toString", "()Ljava/lang/String;"),
            MethodSignature(VirtualType.of("Ljava/lang/Integer;"), "parseInt", "(Ljava/lang/String;)I"),
            MethodSignature(VirtualType.of("Ljava/lang/Long;"), "parseLong", "(Ljava/lang/String;)J"),
        ),
        // A non-null System.out avoids null-receiver paths at println, which PrintlnEmulator handles.
        exactSafeStaticFields = setOf(
            FieldSignature(
                VirtualType.of("Ljava/lang/System;"),
                "out",
                VirtualType.of("Ljava/io/PrintStream;"),
            ),
        ),
    )
}
