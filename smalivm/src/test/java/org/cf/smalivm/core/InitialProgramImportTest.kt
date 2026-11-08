package org.cf.smalivm.core

import java.lang.reflect.Modifier
import java.nio.file.Path
import kotlinx.collections.immutable.persistentMapOf
import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.type.ClassManagerFactory
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.builder.instruction.BuilderInstruction11n
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutablePackedSwitchPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableSwitchElement
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class InitialProgramImportTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun initialImportOwnsCompleteApplicationDeclarationsAndBodies() {
        val index = task53ClassIndex(root, SOURCE)
        val snapshot = index.currentSnapshot
        val definition = requireNotNull(snapshot.applicationClass("Ltask53/Fixture;"))

        assertEquals("Ljava/lang/Object;", definition.superclass)
        assertEquals("task53.smali", definition.sourceFile)
        assertEquals(1, definition.annotations.size)
        assertEquals(7, (definition.staticFields.single().declaration.initialValue as IntEncodedValue).value)
        assertNotNull(snapshot.applicationMethod("Ltask53/Fixture;->value()I")?.body)
        assertNull(snapshot.applicationMethod("Ltask53/Fixture;->nativeValue()I")?.body)
        assertTrue(snapshot.dexReferenceClosure.strings.contains("fixture-value"))
        assertEquals(20, snapshot.outputApiPolicy.apiLevel)
    }

    @Test
    fun initialSnapshotKeepsTheRequestedOutputApi() {
        java.nio.file.Files.writeString(root.resolve("task53.smali"), SOURCE.trimIndent())

        val snapshot = ClassIndex(ClassManagerFactory().build(root.toString(), 28)).currentSnapshot

        assertEquals(28, snapshot.outputApiPolicy.apiLevel)
    }

    @Test
    fun existingJumboInstructionSelectsMonotonicJumboEncoding() {
        val dexFile = root.resolve("existing-jumbo.dex")
        val implementation = ImmutableMethodImplementation(
            1,
            listOf(
                ImmutableInstruction31c(
                    Opcode.CONST_STRING_JUMBO,
                    0,
                    ImmutableStringReference("only-string"),
                ),
                ImmutableInstruction10x(Opcode.RETURN_VOID),
            ),
            emptyList(),
            emptyList(),
        )
        val method = ImmutableMethod(
            "Ltask53/ExistingJumbo;",
            "run",
            emptyList(),
            "V",
            Modifier.PUBLIC or Modifier.STATIC,
            emptySet(),
            emptySet(),
            implementation,
        )
        val definition = ImmutableClassDef(
            "Ltask53/ExistingJumbo;",
            Modifier.PUBLIC,
            "Ljava/lang/Object;",
            emptyList(),
            null,
            emptySet(),
            emptyList(),
            listOf(method),
        )
        DexFileFactory.writeDexFile(
            dexFile.toString(),
            ImmutableDexFile(Opcodes.forApi(SmaliParser.DEX_API_LEVEL), listOf(definition)),
        )

        val snapshot = ClassIndex(ClassManagerFactory().build(dexFile.toFile(), SmaliParser.DEX_API_LEVEL)).currentSnapshot

        assertEquals(StringInstructionEncoding.CONST_STRING_JUMBO, snapshot.stringInstructionEncoding)
    }

    @Test
    fun snapshotExecutionIgnoresLaterMutableSourceBodyMutation() {
        java.nio.file.Files.writeString(root.resolve("task53.smali"), SOURCE.trimIndent())
        val classManager = task53ClassManager(root)
        val index = ClassIndex(classManager)
        val imported = requireNotNull(index.methodView("Ltask53/Fixture;->value()I"))
        val mutableSource = requireNotNull(classManager.getVirtualMethod("Ltask53/Fixture;", "value()I"))

        mutableSource.implementation.replaceInstruction(1, BuilderInstruction11n(Opcode.CONST_4, 0, 2))

        val current = requireNotNull(index.methodView("Ltask53/Fixture;->value()I"))
        assertEquals(Opcode.CONST_STRING, imported.opcodeAt(0))
        assertEquals(Opcode.CONST_STRING, current.opcodeAt(0))
        assertEquals(imported.addresses.toList(), current.addresses.toList())
        val graph = VmServices(index).newSession(current).run()
        assertEquals(Value.Known(1, VirtualType.of("I")), graph.trusted()!!.terminatingConsensus(0))
    }

    @Test
    fun jumboRelayoutSplitsSharedSwitchPayloadWithoutChangingOwnerTargets() {
        val dexFile = root.resolve("shared-switch.dex")
        val implementation = ImmutableMethodImplementation(
            1,
            listOf(
                ImmutableInstruction31t(Opcode.PACKED_SWITCH, 0, 10),
                ImmutableInstruction31t(Opcode.PACKED_SWITCH, 0, 7),
                ImmutableInstruction10x(Opcode.RETURN_VOID),
                ImmutableInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("shared")),
                ImmutableInstruction10x(Opcode.RETURN_VOID),
                ImmutablePackedSwitchPayload(listOf(ImmutableSwitchElement(0, 6))),
            ),
            emptyList(),
            emptyList(),
        )
        val method = ImmutableMethod(
            "Ltask53/SharedSwitch;",
            "shared",
            listOf(ImmutableMethodParameter("I", emptySet(), null)),
            "V",
            Modifier.PUBLIC or Modifier.STATIC,
            emptySet(),
            emptySet(),
            implementation,
        )
        val classDefinition = ImmutableClassDef(
            "Ltask53/SharedSwitch;",
            Modifier.PUBLIC,
            "Ljava/lang/Object;",
            emptyList(),
            null,
            emptySet(),
            emptyList(),
            listOf(method),
        )
        DexFileFactory.writeDexFile(
            dexFile.toString(),
            ImmutableDexFile(Opcodes.forApi(SmaliParser.DEX_API_LEVEL), listOf(classDefinition)),
        )
        val manager = ClassManagerFactory().build(
            dexFile.toFile(),
            DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)),
        )
        val index = ClassIndex(manager)
        val originalClass = requireNotNull(index.currentSnapshot.applicationClass("Ltask53/SharedSwitch;"))
        val originalMethod = requireNotNull(index.currentSnapshot.applicationMethod("Ltask53/SharedSwitch;->shared(I)V"))
        val relaidBody = MethodBodyAssembler.assemble(
            MethodLayoutSolver.solve(
                requireNotNull(originalMethod.body),
                StringInstructionEncoding.CONST_STRING_JUMBO,
            ),
        )
        val relaidMethod = ApplicationMethodDefinition.withBody(
            originalMethod,
            relaidBody,
            index.programWorkspace.newApplicationMethodRevision(originalMethod.methodSignature),
        )
        val relaidClass = ApplicationClassDefinition(
            originalClass.type,
            originalClass.accessFlags,
            originalClass.superclass,
            originalClass.interfaces,
            originalClass.sourceFile,
            originalClass.annotations,
            originalClass.staticFields,
            originalClass.instanceFields,
            listOf(relaidMethod),
        )
        val candidate = ProgramSnapshot.candidate(
            index.currentSnapshot,
            index.programWorkspace.newProgramRevision(),
            persistentMapOf(relaidClass.type to relaidClass),
            StringInstructionEncoding.CONST_STRING_JUMBO,
        )
        index.programWorkspace.publish(candidate)
        val view = requireNotNull(index.methodView("Ltask53/SharedSwitch;->shared(I)V"))

        assertEquals(listOf(0 to 6), view.switchCasesAt(0))
        assertEquals(listOf(0 to 10), view.switchCasesAt(3))
        assertEquals(2, view.payloadAddresses.size)

        val output = root.resolve("shared-switch-output.dex").toFile()
        ProgramDexWriter().write(candidate, output)
        val reloaded = ClassIndex(
            ClassManagerFactory().build(
                output,
                DexBuilder(Opcodes.forApi(SmaliParser.DEX_API_LEVEL)),
            ),
        )
        val outputView = requireNotNull(reloaded.methodView("Ltask53/SharedSwitch;->shared(I)V"))
        assertEquals(listOf(0 to 6), outputView.switchCasesAt(0))
        assertEquals(listOf(0 to 10), outputView.switchCasesAt(3))
        assertEquals(2, outputView.payloadAddresses.size)
    }

    private companion object {
        const val SOURCE = """
            .class public abstract Ltask53/Fixture;
            .super Ljava/lang/Object;
            .source "task53.smali"

            .annotation runtime Ljava/lang/Deprecated;
            .end annotation

            .field public static final number:I = 0x7

            .method public static native nativeValue()I
            .end method

            .method public static value()I
                .registers 1
                const-string v0, "fixture-value"
                const/4 v0, 0x1
                return v0
            .end method
        """
    }
}
