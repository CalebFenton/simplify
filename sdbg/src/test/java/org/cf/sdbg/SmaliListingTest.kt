package org.cf.sdbg

import org.cf.smalivm.core.Breakpoint
import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.core.Debugger
import org.cf.smalivm.core.MethodSignature
import org.cf.smalivm.core.VmServices
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File
import java.io.StringWriter

class SmaliListingTest {
    @Test
    fun executableIndexesSurviveLabelsDebugDirectivesAndAllPayloadKinds() {
        val services = services()
        val listing = SmaliListing(services)
        for ((descriptor, payload) in listOf(
            "packed()I" to ".packed-switch",
            "sparse()I" to ".sparse-switch",
            "array()[I" to ".array-data",
        )) {
            val signature = MethodSignature.parse("LDebugFixture;->$descriptor")
            val view = requireNotNull(services.classIndex.methodView(signature))
            val debugger = Debugger(services.newSession(view))
            val lastIndex = view.instructionCount - 1
            debugger.addBreakpoint(Breakpoint(signature, 0))
            debugger.addBreakpoint(Breakpoint(signature, lastIndex))
            val output = StringWriter()
            assertTrue(listing.writeMethod(output, signature, debugger))
            val text = output.toString()
            val instructions = INDEXED.findAll(text).toList()
            assertEquals((0..lastIndex).toList(), instructions.map { it.groupValues[1].toInt() }, text)
            for (instruction in instructions) {
                val index = instruction.groupValues[1].toInt()
                val address = view.codeAddressAtInstructionIndex(index)
                assertEquals(view.opcodeAt(address).name, instruction.groupValues[3], text)
                assertEquals(address, instruction.groupValues[4].toInt(16), text)
            }
            assertEquals("b>", instructions.first().groupValues[2], text)
            assertEquals("b", instructions.last().groupValues[2], text)
            assertTrue(text.contains(payload), text)
            assertTrue(text.contains(":${if (descriptor.startsWith("array")) "array" else if (descriptor.startsWith("packed")) "pswitch" else "sswitch"}"), text)
            assertTrue(view.payloadAddresses.any { it < view.codeAddressAtInstructionIndex(lastIndex) },
                "Fixture must exercise executable instructions after payload data")

            val range = StringWriter()
            assertTrue(listing.writeMethod(range, signature, debugger, lastIndex, lastIndex))
            assertEquals(listOf(lastIndex), INDEXED.findAll(range.toString()).map { it.groupValues[1].toInt() }.toList())
            assertTrue(range.toString().contains(payload), "Range listings retain payload and label context")
        }
    }

    @Test
    fun classListingPreservesDeclarationsAndDoesNotLeakMarkersAcrossMethods() {
        val services = services()
        val view = requireNotNull(services.classIndex.methodView(MethodSignature.parse("LDebugFixture;->exercise()I")))
        val debugger = Debugger(services.newSession(view))
        debugger.addBreakpoint(Breakpoint(view.signature, 0))
        val output = StringWriter()
        val listing = SmaliListing(services)
        listing.writeClass(output, "LDebugFixture;", debugger)
        val text = output.toString()
        assertTrue(text.contains(".field public static total:I"), text)
        assertTrue(text.contains("LDebugFixture;->bodyless()V"), text)
        assertTrue(text.contains(".source \"DebugFixture.java\""), text)
        assertEquals(1, INDEXED.findAll(text).count { it.groupValues[2].contains('>') }, text)
        assertEquals(1, INDEXED.findAll(text).count { it.groupValues[2].contains('b') }, text)
        assertFalse(listing.writeMethod(StringWriter(), MethodSignature.parse("LDebugFixture;->missing()V"), debugger))
        val bodyless = StringWriter()
        assertTrue(listing.writeMethod(bodyless, MethodSignature.parse("LDebugFixture;->bodyless()V"), debugger))
        assertFalse(INDEXED.containsMatchIn(bodyless.toString()))
    }

    private fun services(): VmServices {
        val input = File(requireNotNull(javaClass.getResource("/debugger")).toURI())
        return VmServices(ClassIndex.open(File(input.path)))
    }

    private companion object {
        val INDEXED = Regex("^(\\d+):([b>]*)\\s+(\\S+).*#@([0-9a-f]+)$", RegexOption.MULTILINE)
    }
}
