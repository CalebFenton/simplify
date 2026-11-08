package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

class EffectMetadataTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))

    private fun graph(method: String): ExecutionGraph = ExecutionSession.of(
        classIndex.requireMethodView("Leffect_metadata_test;", method),
        classIndex,
        Limits(),
    ).run()

    @Test
    fun synchronizedMethodEntryAndItsInvokeSiteAreWeak() {
        val direct = graph("synchronizedPure()I")
        assertTrue(
            direct.methodView.isSynchronized,
            "synchronized metadata was lost; accessFlags=0x${direct.methodView.methodAccessFlags.toString(16)}, " +
                "isStatic=${direct.methodView.isStatic}",
        )
        assertEquals(SideEffectLevel.WEAK, direct.explored.sideEffectLevel(0))
        assertEquals(SideEffectLevel.WEAK, direct.trusted()!!.methodSideEffectLevel())

        val caller = graph("callSynchronized()I")
        val invoke = caller.methodView.instructionAddresses.single {
            caller.methodView.opcodeAt(it) == Opcode.INVOKE_STATIC
        }
        assertEquals(SideEffectLevel.WEAK, caller.explored.sideEffectLevel(invoke))
        assertEquals(SideEffectLevel.WEAK, caller.trusted()!!.methodSideEffectLevel())
    }

    @Test
    fun volatileReadAndWriteAreWeakWhilePlainReadRemainsNone() {
        val read = graph("readVolatile()I")
        assertEquals(SideEffectLevel.WEAK, read.explored.sideEffectLevel(0))
        assertEquals(SideEffectLevel.WEAK, read.trusted()!!.methodSideEffectLevel())

        val write = graph("writeVolatile()V")
        val sput = write.methodView.instructionAddresses.single {
            write.methodView.opcodeAt(it) == Opcode.SPUT
        }
        assertEquals(SideEffectLevel.WEAK, write.explored.sideEffectLevel(sput))

        val plain = graph("readPlain()I")
        assertEquals(SideEffectLevel.NONE, plain.explored.sideEffectLevel(0))
    }

    @Test
    fun volatileReadEffectAggregatesOntoTheInvokeSite() {
        val caller = graph("callVolatileRead()I")
        val invoke = caller.methodView.instructionAddresses.single {
            caller.methodView.opcodeAt(it) == Opcode.INVOKE_STATIC
        }
        assertEquals(SideEffectLevel.WEAK, caller.explored.sideEffectLevel(invoke))
        assertEquals(SideEffectLevel.WEAK, caller.trusted()!!.methodSideEffectLevel())
    }
}
