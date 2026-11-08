package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DexLimitsTest {
    @Test
    fun indexedSectionsAccept65536ItemsAndReject65537() {
        for (section in listOf(DexSection.TYPE_IDS, DexSection.PROTO_IDS, DexSection.FIELD_IDS, DexSection.METHOD_IDS)) {
            assertNull(DexLimits.validateIndexedSection(section, 65_535))
            assertNull(DexLimits.validateIndexedSection(section, 65_536))
            assertEquals(
                MethodEditRejectionReason.DexReferenceLimit(section),
                DexLimits.validateIndexedSection(section, 65_537),
            )
        }
    }

    @Test
    fun typeListProjectionIncludesHeadersEntriesAndFourByteAlignment() {
        assertEquals(28L, DexLimits.encodedTypeListDataSize(listOf(0, 1, 2, 3)))
        assertEquals(
            0xffff_fffcL,
            DexLimits.encodedTypeListDataSize(listOf(2_147_483_644)),
        )
        assertEquals(
            0x1_0000_0000L,
            DexLimits.encodedTypeListDataSize(listOf(2_147_483_645)),
        )
        assertNull(DexLimits.validateTypeListDataSize(0xffff_ffffL))
        assertEquals(DexSection.TYPE_LIST, DexLimits.validateTypeListDataSize(0x1_0000_0000L)?.section)
    }

    @Test
    fun transitiveClosuresRejectEachOverflowingIndexedSection() {
        val cases = listOf(
            DexSection.TYPE_IDS to references {
                ImmutableTypeReference("Loverflow/T$it;")
            },
            DexSection.FIELD_IDS to references {
                ImmutableFieldReference("Loverflow/Owner;", "field$it", "I")
            },
            DexSection.METHOD_IDS to references {
                ImmutableMethodReference("Loverflow/Owner;", "method$it", emptyList(), "V")
            },
            DexSection.PROTO_IDS to references { value ->
                val parameters = List(17) { bit -> if (value and (1 shl bit) == 0) "I" else "J" }
                ImmutableMethodProtoReference(parameters, "V")
            },
        )

        for ((section, references) in cases) {
            val closure = DexReferenceClosure.fromReferences(references)
            assertEquals(section, DexLimits.validate(closure, emptyList())?.section)
        }
    }

    private fun references(factory: (Int) -> Reference): Iterable<Reference> = Iterable {
        (0..65_536).asSequence().map(factory).iterator()
    }
}
