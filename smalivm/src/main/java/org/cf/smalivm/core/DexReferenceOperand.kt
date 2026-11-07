package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableReferenceFactory

/** Detached reference operand used by an instruction template. */
sealed interface DexReferenceOperand {
    class Existing(reference: Reference) : DexReferenceOperand {
        val reference: Reference = ImmutableReferenceFactory.of(reference)
    }

    class Staged(val reference: StagedDexReference) : DexReferenceOperand
}

internal fun DexReferenceOperand.asReference(): Reference = when (this) {
    is DexReferenceOperand.Existing -> reference
    is DexReferenceOperand.Staged -> reference.asReference()
}
