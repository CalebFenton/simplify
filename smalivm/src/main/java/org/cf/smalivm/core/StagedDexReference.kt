package org.cf.smalivm.core

import java.util.Collections
import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference

/** A transaction-bound DEX reference that is not part of the current snapshot yet. */
sealed class StagedDexReference protected constructor(
    private val transactionIdentity: Any,
) {
    internal fun belongsTo(identity: Any): Boolean = transactionIdentity === identity

    internal abstract fun asReference(): Reference

    /** A DEX type reference staged for a typed exception handler or instruction. */
    class Type internal constructor(
        transactionIdentity: Any,
        val descriptor: kotlin.String,
    ) : StagedDexReference(transactionIdentity) {
        override fun asReference(): Reference = ImmutableTypeReference(descriptor)
    }

    /** A string staged for an instruction reference. */
    class String internal constructor(
        transactionIdentity: Any,
        val value: kotlin.String,
    ) : StagedDexReference(transactionIdentity) {
        override fun asReference(): Reference = ImmutableStringReference(value)
    }

    /** A field reference staged for an instruction reference. */
    class Field internal constructor(
        transactionIdentity: Any,
        val definingClass: kotlin.String,
        val name: kotlin.String,
        val type: kotlin.String,
    ) : StagedDexReference(transactionIdentity) {
        override fun asReference(): Reference = ImmutableFieldReference(definingClass, name, type)
    }

    /** A method reference staged for an instruction reference. */
    class Method internal constructor(
        transactionIdentity: Any,
        val definingClass: kotlin.String,
        val name: kotlin.String,
        parameterTypes: List<kotlin.String>,
        val returnType: kotlin.String,
    ) : StagedDexReference(transactionIdentity) {
        val parameterTypes: List<kotlin.String> = Collections.unmodifiableList(parameterTypes.toList())

        override fun asReference(): Reference =
            ImmutableMethodReference(definingClass, name, parameterTypes, returnType)
    }

    /** A method prototype staged for an instruction reference. */
    class Prototype internal constructor(
        transactionIdentity: Any,
        parameterTypes: List<kotlin.String>,
        val returnType: kotlin.String,
    ) : StagedDexReference(transactionIdentity) {
        val parameterTypes: List<kotlin.String> = Collections.unmodifiableList(parameterTypes.toList())

        override fun asReference(): Reference = ImmutableMethodProtoReference(parameterTypes, returnType)
    }
}
