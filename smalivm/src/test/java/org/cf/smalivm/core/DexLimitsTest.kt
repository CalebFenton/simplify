package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.iface.reference.Reference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DexLimitsTest {
    @Test
    fun indexedSectionsAccept65536ItemsAndReject65537() {
        for (section in listOf("type_ids", "proto_ids", "field_ids", "method_ids")) {
            assertNull(DexLimits.validateIndexedSection(section, 65_535))
            assertNull(DexLimits.validateIndexedSection(section, 65_536))
            val rejected = DexLimits.validateIndexedSection(section, 65_537)
            assertTrue(rejected is MethodEditRejectionReason.DexReferenceLimit)
            assertEquals(section, rejected?.section)
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
        assertEquals(
            "type_list",
            DexLimits.validateTypeListDataSize(0x1_0000_0000L)?.section,
        )
    }

    @Test
    fun transitiveClosuresRejectEachOverflowingIndexedSection() {
        val cases = listOf(
            "type_ids" to references {
                ImmutableTypeReference("Loverflow/T$it;")
            },
            "field_ids" to references {
                ImmutableFieldReference("Loverflow/Owner;", "field$it", "I")
            },
            "method_ids" to references {
                ImmutableMethodReference("Loverflow/Owner;", "method$it", emptyList(), "V")
            },
            "proto_ids" to references { value ->
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
