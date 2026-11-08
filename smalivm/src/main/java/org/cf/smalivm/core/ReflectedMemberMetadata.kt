package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference

/** Immutable identity data needed to replace one exact reflected method invocation with Dalvik bytecode. */
data class ReflectedMethodMetadata(
    val method: MethodReference,
    val modifiers: Int,
    val declaringClassModifiers: Int,
)

/** Immutable identity data needed to replace one exact reflected field read with Dalvik bytecode. */
data class ReflectedFieldMetadata(val field: FieldReference, val modifiers: Int)
