package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.formatter.DexFormatter
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MemberSignatureTest {
    private val owner = VirtualType.of("Lowner;")

    @Test
    fun parseReadsEachPartOfAMethodSignature() {
        val signature = MethodSignature.parse("Lpkg/Owner;->name(I[JLjava/lang/String;)[Z")

        assertEquals(VirtualType.of("Lpkg/Owner;"), signature.definingClass)
        assertEquals("name", signature.name)
        assertEquals(listOf("I", "[J", "Ljava/lang/String;"), signature.parameterTypes.map { it.descriptor })
        assertEquals(VirtualType.of("[Z"), signature.returnType)
    }

    @Test
    fun spellingMatchesDexlibsFormatterForMethodsAndFields() {
        // dexlib2's DexFormatter is the smali spelling every DEX tool prints; it shares no code with the parser.
        val method = ImmutableMethodReference("La/B;", "run", listOf("J", "[[La/C;"), "V")
        val field = ImmutableFieldReference("La/B;", "count", "[I")

        assertEquals(DexFormatter.INSTANCE.getMethodDescriptor(method), MethodSignature.of(method).toString())
        assertEquals(DexFormatter.INSTANCE.getFieldDescriptor(field), FieldSignature.of(field).toString())
        assertEquals(MethodSignature.of(method), MethodSignature.parse(DexFormatter.INSTANCE.getMethodDescriptor(method)))
        assertEquals(FieldSignature.of(field), FieldSignature.parse(DexFormatter.INSTANCE.getFieldDescriptor(field)))
    }

    @Test
    fun aMethodIsIdentifiedByItsReturnTypeToo() {
        // Dex resolves by name and full proto, so overloads that differ only in return type are distinct methods.
        assertNotEquals(MethodSignature.parse("Lowner;->m()I"), MethodSignature.parse("Lowner;->m()J"))
        assertEquals(MethodSignature.parse("Lowner;->m(I)V"), MethodSignature(owner, "m", "(I)V"))
        assertEquals(MethodSignature.parse("Lowner;->m(I)V"), MethodSignature.parseMember(owner, "m(I)V"))
    }

    @Test
    fun constructorAndClassInitializerAreTheirExactNames() {
        val constructor = MethodSignature.parse("Lowner;-><init>(I)V")
        val initializer = MethodSignature.parse("Lowner;-><clinit>()V")
        val ordinary = MethodSignature.parse("Lowner;->init()V")

        assertTrue(constructor.isConstructor)
        assertFalse(constructor.isClassInitializer)
        assertTrue(initializer.isClassInitializer)
        assertFalse(initializer.isConstructor)
        assertFalse(ordinary.isConstructor || ordinary.isClassInitializer)
    }

    @Test
    fun malformedMethodSignaturesAreRejected() {
        for (text in listOf(
            "Lowner;->m()[", // a trailing `[` names no component type
            "Lowner;->m(I[)V",
            "Lowner;m()V", // missing `->`
            "Lowner;-m()V",
            "Lowner;->m)V", // missing `(`
            "Lowner;->mV",
            "Lowner;->m(II", // missing `)`
            "Lowner;->()V", // empty name
            "Lowner;->m(V)V", // void is not a parameter type
            "Lowner;->m()VI", // text after the return type
            "Lowner->m()V", // owner class descriptor never closed
            "Lowner;->a.b()V", // `.` cannot appear in a member name
            "Lowner;->m(Ljava/lang/String)V",
            "",
        )) {
            assertThrows(MalformedSignatureException::class.java, { MethodSignature.parse(text) }, text)
        }
        assertThrows(MalformedSignatureException::class.java) { MethodSignature(owner, "m", "(I[)V") }
        assertThrows(MalformedSignatureException::class.java) {
            MethodSignature(owner, "", emptyList(), VirtualType.of("V"))
        }
        assertThrows(MalformedSignatureException::class.java) {
            MethodSignature(owner, "m", listOf(VirtualType.of("V")), VirtualType.of("V"))
        }
    }

    @Test
    fun malformedFieldSignaturesAreRejected() {
        for (text in listOf("Lowner;->f:V", "Lowner;->f", "Lowner;->:I", "Lowner;f:I", "Lowner;->f:I;", "Lowner;->f:[")) {
            assertThrows(MalformedSignatureException::class.java, { FieldSignature.parse(text) }, text)
        }
        assertThrows(MalformedSignatureException::class.java) { FieldSignature(owner, "f", VirtualType.of("V")) }
    }

    @Test
    fun theOffsetIsHowFarReadingGot() {
        // A reader trying several grammars reports the one that matched the most text.
        val badOwner = assertThrows(MalformedSignatureException::class.java) {
            MethodSignature.parse("La.b;->m()V")
        }
        val missingParenthesis = assertThrows(MalformedSignatureException::class.java) {
            MethodSignature.parse("Lowner;->m(II")
        }

        assertEquals(5, badOwner.offset)
        assertEquals(13, missingParenthesis.offset)
    }

    @Test
    fun argumentWordsGiveAWideParameterTwoWordsAfterTheReceiver() {
        // Dalvik invoke registers: receiver, then each argument; J and D take two words.
        val words = MethodSignature(owner, "m", "(J[[Ljava/lang/String;I)V").argumentWords(InvokeKind.VIRTUAL)

        assertEquals(
            listOf("Lowner;" to 0, "J" to 1, "[[Ljava/lang/String;" to 3, "I" to 4),
            words.arguments.map { it.type.descriptor to it.word },
        )
        assertEquals(5, words.wordCount)
    }
}
