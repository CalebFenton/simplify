package org.cf.simplify

import org.cf.smalivm.core.*
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.*
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

class ConstantBuilderTest {
    @Test fun integralAndBooleanCarriersPreserveTheirLiteralWords() {
        for ((raw, type, bits) in listOf(
            Triple(false, "Z", 0), Triple(true, "Z", 1), Triple(1, "Z", 1),
            Triple(7.toByte(), "B", 7), Triple(Byte.MAX_VALUE, "B", 127),
            Triple(7.toShort(), "S", 7), Triple(Short.MAX_VALUE, "S", 32767),
            Triple('A', "C", 65), Triple(7, "I", 7), Triple(-8, "I", -8),
            Triple(32767, "I", 32767), Triple(-32768, "I", -32768),
            Triple(50000, "I", 50000), Triple(Int.MAX_VALUE, "I", Int.MAX_VALUE),
            Triple(Int.MIN_VALUE, "I", Int.MIN_VALUE),
        )) {
            val instruction = replace(raw, type, false)
            assertEquals(bits, (instruction as NarrowLiteralInstruction).narrowLiteral)
            assertEquals(when (bits) { in -8..7 -> Opcode.CONST_4; in -32768..32767 -> Opcode.CONST_16; else -> Opcode.CONST }, instruction.opcode)
        }
    }

    @Test fun wideIntegerBoundariesUseSignedEncodingsWithoutTruncation() {
        for (bits in listOf(7L, -8L, 32767L, -32768L, 32768L, -32769L, Int.MAX_VALUE.toLong(),
            Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong() + 1, Long.MAX_VALUE, Long.MIN_VALUE)) {
            val instruction = replace(bits, "J", true)
            assertEquals(bits, (instruction as WideLiteralInstruction).wideLiteral)
            assertEquals(when (bits) {
                in -32768L..32767L -> Opcode.CONST_WIDE_16
                in Int.MIN_VALUE.toLong()..Int.MAX_VALUE.toLong() -> Opcode.CONST_WIDE_32
                else -> Opcode.CONST_WIDE
            }, instruction.opcode)
        }
    }

    @Test fun floatingValuesPreserveIntegralValuesSignedZeroInfinityAndNanPayloadBits() {
        for (bits in listOf(0, Int.MIN_VALUE, 0x3f800000, 0x3fc00000, 0x7f800000, 0x7fc01234, 0x00ff0000)) {
            val instruction = replace(Float.fromBits(bits), "F", false)
            assertEquals(bits, (instruction as NarrowLiteralInstruction).narrowLiteral)
        }
        for (bits in listOf(0L, Long.MIN_VALUE, 0x3ff0000000000000L, 0x3ff0000000000001L,
            0x7ff0000000000000L, 0x7ff8000000001234L)) {
            val instruction = replace(Double.fromBits(bits), "D", true)
            assertEquals(bits, (instruction as WideLiteralInstruction).wideLiteral)
        }
    }

    @Test fun integralCarriersUnderFloatingTagsAlreadyRepresentBits() {
        assertEquals(5, (replace(5, "F", false) as NarrowLiteralInstruction).narrowLiteral)
        assertEquals(32768L, (replace(32768L, "D", true) as WideLiteralInstruction).wideLiteral)
        val double = Double.fromBits(0x3ff000003fc00000L)
        assertEquals(0x3fc00000, (replace(double, "F", false) as NarrowLiteralInstruction).narrowLiteral)
    }

    @Test fun stringsClassesAndNullUseStagedDexReferences() {
        val text = "Ever sift sand through a screen?"
        val string = replace(text, "Ljava/lang/Object;", false, objectValue = true, canonicalLiteral = true)
        assertEquals(Opcode.CONST_STRING, string.opcode)
        assertEquals(text, ((string as ReferenceInstruction).reference as StringReference).string)
        val klass = replace(String::class.java, "Ljava/lang/Class;", false, objectValue = true)
        assertEquals(Opcode.CONST_CLASS, klass.opcode)
        assertEquals("Ljava/lang/String;", ((klass as ReferenceInstruction).reference as TypeReference).type)
        assertEquals(0, (replace(null, "Ljava/lang/Object;", false, objectValue = true) as NarrowLiteralInstruction).narrowLiteral)
    }

    @Test fun unsupportedIdentityCarriersAndRegisterEncodingsAreDeclined() {
        val services = services()
        val view = requireNotNull(services.classIndex.methodView(OWNER + "builderNarrow()I"))
        val transaction = services.methodEditor.beginEdit(view)
        val snapshot = services.currentSnapshot
        val text = "not an interned allocation"
        val distinct = String(text.toCharArray())
        assertNotSame(text, distinct)
        // Unlike String(existingLiteral), this string has no pre-existing intern-pool entry.
        val freshHostString = java.util.UUID.randomUUID().toString()
        for ((raw, type) in listOf(
            distinct to "Ljava/lang/String;", freshHostString to "Ljava/lang/String;",
            text to "Ljava/lang/String;", Any() to "Ljava/lang/Object;", 7 to "Ljava/lang/Integer;",
            null to "I", 1.5f to "I", 1.5 to "I", 1.5 to "J", 5 to "D",
        )) assertNull(ConstantBuilder.build(Value.Known(raw, VirtualType.of(type)), 0, transaction), "$raw : $type")
        for (register in listOf(-1, 256))
            assertNull(ConstantBuilder.build(Value.Known(1, VirtualType.of("I")), register, transaction))
        assertSame(snapshot, services.currentSnapshot)
    }

    private fun replace(raw: Any?, type: String, wide: Boolean, objectValue: Boolean = false,
                        canonicalLiteral: Boolean = false): Instruction {
        val services = services()
        val method = when { objectValue -> "builderObject()Ljava/lang/Object;"; wide -> "builderWide()J"; else -> "builderNarrow()I" }
        val view = requireNotNull(services.classIndex.methodView(OWNER + method))
        val transaction = services.methodEditor.beginEdit(view)
        val template = requireNotNull(if (canonicalLiteral)
            ConstantBuilder.buildStringLiteral(raw as String, 0, transaction)
        else ConstantBuilder.build(Value.Known(raw, VirtualType.of(type)), 0, transaction))
        transaction.replaceInstruction(0, listOf(template))
        val result = transaction.commit()
        assertTrue(result is MethodEditCommitResult.Committed || result == MethodEditCommitResult.Unchanged, result.toString())
        return requireNotNull(services.classIndex.methodView(OWNER + method)).instructionAt(0)
    }

    private fun services() = VmServices(ClassIndex.open(File("src/test/resources/task65"), 20))
    companion object { private const val OWNER = "Ltask65/Constants;->" }
}
