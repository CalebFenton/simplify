package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test

class MethodDescriptorTest {
    private val owner = VirtualType.of("Lowner;")

    @Test
    fun argumentWordsGiveAWideParameterTwoWordsAfterTheReceiver() {
        // Dalvik invoke registers: receiver, then each argument; J and D take two words.
        val words = MethodDescriptor(owner, "m", "(J[[Ljava/lang/String;I)V").argumentWords(InvokeKind.VIRTUAL)

        assertEquals(
            listOf("Lowner;" to 0, "J" to 1, "[[Ljava/lang/String;" to 3, "I" to 4),
            words.arguments.map { it.type.descriptor to it.word },
        )
        assertEquals(5, words.wordCount)
    }

    @Test
    fun aProtoEndingInAnArrayPrefixWithNoComponentTypeIsRejected() {
        // A dex type id can hold any string; `[` with no component is not a type descriptor.
        assertThrows(IllegalArgumentException::class.java) {
            MethodDescriptor(owner, "m", "(I[)V").argumentWords(InvokeKind.STATIC)
        }
    }

    @Test
    fun aClassTypeWithNoTerminatingSemicolonIsRejected() {
        assertThrows(IllegalArgumentException::class.java) {
            MethodDescriptor(owner, "m", "(Ljava/lang/String)V").argumentWords(InvokeKind.STATIC)
        }
    }
}
