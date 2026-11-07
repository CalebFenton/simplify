package org.cf.smalivm.core

import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class EffectMetadataTest {
    private val classIndex = ClassIndex(
        ClassManagerFactory().build(
            "src/test/resources/smali",
            DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)),
        ),
    )

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
        assertEquals(SideEffectLevel.WEAK, direct.sideEffectLevel(0))
        assertEquals(SideEffectLevel.WEAK, direct.trusted()!!.methodSideEffectLevel())

        val caller = graph("callSynchronized()I")
        val invoke = caller.methodView.instructionAddresses.single {
            caller.methodView.opcodeAt(it) == Opcode.INVOKE_STATIC
        }
        assertEquals(SideEffectLevel.WEAK, caller.sideEffectLevel(invoke))
        assertEquals(SideEffectLevel.WEAK, caller.trusted()!!.methodSideEffectLevel())
    }

    @Test
    fun volatileReadAndWriteAreWeakWhilePlainReadRemainsNone() {
        val read = graph("readVolatile()I")
        assertEquals(SideEffectLevel.WEAK, read.sideEffectLevel(0))
        assertEquals(SideEffectLevel.WEAK, read.trusted()!!.methodSideEffectLevel())

        val write = graph("writeVolatile()V")
        val sput = write.methodView.instructionAddresses.single {
            write.methodView.opcodeAt(it) == Opcode.SPUT
        }
        assertEquals(SideEffectLevel.WEAK, write.sideEffectLevel(sput))

        val plain = graph("readPlain()I")
        assertEquals(SideEffectLevel.NONE, plain.sideEffectLevel(0))
    }

    @Test
    fun volatileReadEffectAggregatesOntoTheInvokeSite() {
        val caller = graph("callVolatileRead()I")
        val invoke = caller.methodView.instructionAddresses.single {
            caller.methodView.opcodeAt(it) == Opcode.INVOKE_STATIC
        }
        assertEquals(SideEffectLevel.WEAK, caller.sideEffectLevel(invoke))
        assertEquals(SideEffectLevel.WEAK, caller.trusted()!!.methodSideEffectLevel())
    }
}
