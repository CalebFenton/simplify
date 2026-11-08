package org.cf.demoapp

import org.cf.smalivm.core.MethodDescriptor
import org.cf.smalivm.core.ReflectionPolicy
import org.cf.smalivm.core.StaticFieldDescriptor
import org.cf.smalivm.core.VirtualType

/** Authorizes only the host operations the demo guest methods use. */
internal fun demoReflectionPolicy(): ReflectionPolicy {
    val string = VirtualType.of("Ljava/lang/String;")
    val builder = VirtualType.of("Ljava/lang/StringBuilder;")
    return ReflectionPolicy(
        // The defaults deny mutable StringBuilder operations; the demo opts in to exactly these.
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
        // A non-null System.out avoids null-receiver paths at println, which PrintlnEmulator handles.
        exactSafeStaticFields = setOf(
            StaticFieldDescriptor(
                VirtualType.of("Ljava/lang/System;"),
                "out",
                VirtualType.of("Ljava/io/PrintStream;"),
            ),
        ),
    )
}
