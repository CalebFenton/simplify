package org.cf.smalivm.core

import java.util.Collections
import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference

/** A transaction-bound DEX reference that is not part of the current snapshot yet. */
public sealed class StagedDexReference {
    internal abstract fun asReference(): Reference

    /** A DEX type reference staged for a typed exception handler or instruction. */
    public class Type internal constructor(internal val descriptor: String) : StagedDexReference() {
        override fun asReference(): Reference = ImmutableTypeReference(descriptor)
    }

    /** A string staged for an instruction reference. */
    public class StringLiteral internal constructor(internal val value: String) : StagedDexReference() {
        override fun asReference(): Reference = ImmutableStringReference(value)
    }

    /** A field reference staged for an instruction reference. */
    public class Field internal constructor(
        internal val definingClass: String,
        internal val name: String,
        internal val type: String,
    ) : StagedDexReference() {
        override fun asReference(): Reference = ImmutableFieldReference(definingClass, name, type)
    }

    /** A method reference staged for an instruction reference. */
    public class Method internal constructor(
        internal val definingClass: String,
        internal val name: String,
        parameterTypes: List<String>,
        internal val returnType: String,
    ) : StagedDexReference() {
        internal val parameterTypes: List<String> = Collections.unmodifiableList(parameterTypes.toList())

        override fun asReference(): Reference =
            ImmutableMethodReference(definingClass, name, parameterTypes, returnType)
    }

    /** A method prototype staged for an instruction reference. */
    public class Prototype internal constructor(
        parameterTypes: List<String>,
        internal val returnType: String,
    ) : StagedDexReference() {
        internal val parameterTypes: List<String> = Collections.unmodifiableList(parameterTypes.toList())

        override fun asReference(): Reference = ImmutableMethodProtoReference(parameterTypes, returnType)
    }
}
