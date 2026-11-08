package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableReferenceFactory

/** Detached reference operand used by an instruction template. */
public sealed interface DexReferenceOperand {
    /** A reference that already exists in the program. */
    public class Existing(reference: Reference) : DexReferenceOperand {
        internal val reference: Reference = ImmutableReferenceFactory.of(reference)
    }

    /** A reference staged in the current edit. */
    public class Staged(internal val reference: StagedDexReference) : DexReferenceOperand
}

internal fun DexReferenceOperand.asReference(): Reference = when (this) {
    is DexReferenceOperand.Existing -> reference
    is DexReferenceOperand.Staged -> reference.asReference()
}
