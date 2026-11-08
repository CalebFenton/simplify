package org.cf.smalivm.core

import java.util.Collections
import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference

/** A transaction-bound DEX reference that is not part of the current snapshot yet. */
sealed class StagedDexReference {
    internal abstract fun asReference(): Reference

    /** A DEX type reference staged for a typed exception handler or instruction. */
    class Type internal constructor(val descriptor: String) : StagedDexReference() {
        override fun asReference(): Reference = ImmutableTypeReference(descriptor)
    }

    /** A string staged for an instruction reference. */
    class StringLiteral internal constructor(val value: String) : StagedDexReference() {
        override fun asReference(): Reference = ImmutableStringReference(value)
    }

    /** A field reference staged for an instruction reference. */
    class Field internal constructor(
        val definingClass: String,
        val name: String,
        val type: String,
    ) : StagedDexReference() {
        override fun asReference(): Reference = ImmutableFieldReference(definingClass, name, type)
    }

    /** A method reference staged for an instruction reference. */
    class Method internal constructor(
        val definingClass: String,
        val name: String,
        parameterTypes: List<String>,
        val returnType: String,
    ) : StagedDexReference() {
        val parameterTypes: List<String> = Collections.unmodifiableList(parameterTypes.toList())

        override fun asReference(): Reference =
            ImmutableMethodReference(definingClass, name, parameterTypes, returnType)
    }

    /** A method prototype staged for an instruction reference. */
    class Prototype internal constructor(
        parameterTypes: List<String>,
        val returnType: String,
    ) : StagedDexReference() {
        val parameterTypes: List<String> = Collections.unmodifiableList(parameterTypes.toList())

        override fun asReference(): Reference = ImmutableMethodProtoReference(parameterTypes, returnType)
    }
}
