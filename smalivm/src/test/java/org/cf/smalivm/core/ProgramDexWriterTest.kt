package org.cf.smalivm.core

import java.lang.reflect.Modifier
import java.nio.file.Path
import kotlinx.collections.immutable.persistentMapOf
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.AnnotationVisibility
import com.android.tools.smali.dexlib2.MethodHandleType
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.MethodHandleReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableAnnotation
import com.android.tools.smali.dexlib2.immutable.ImmutableAnnotationElement
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction45cc
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableCallSiteReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodHandleReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.value.ImmutableArrayEncodedValue
import com.android.tools.smali.dexlib2.immutable.value.ImmutableStringEncodedValue
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.io.TempDir

class ProgramDexWriterTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun freshWriterRoundTripsOnlySnapshotApplicationDeclarations() {
        val index = classIndexFromSource(root, SOURCE)
        val snapshot = index.currentSnapshot
        val bytes = ProgramDexWriter().writeToBytes(snapshot)
        val dex = DexBackedDexFile(snapshot.outputApiPolicy.opcodes(), bytes)

        assertEquals(setOf("Lwriter/Output;"), dex.classes.mapTo(linkedSetOf()) { it.type })
        val output = dex.classes.single()
        assertEquals("output.smali", output.sourceFile)
        assertEquals(1, output.annotations.size)
        val field = output.staticFields.single()
        assertEquals(11, (field.initialValue as IntEncodedValue).value)
        assertEquals(
            "field-only",
            (field.annotations.single().elements.single().value as StringEncodedValue).value,
        )
        assertNull(output.methods.single { it.name == "nativeValue" }.implementation)
        assertEquals(Opcode.CONST_4, output.methods.single { it.name == "value" }.implementation!!.instructions.first().opcode)
        val hash = output.methods.single { it.name == "hash" }
        assertEquals("value", hash.parameters.single().name)
        assertEquals(42, hash.implementation!!.debugItems.filterIsInstance<com.android.tools.smali.dexlib2.iface.debug.LineNumber>().single().lineNumber)
        assertFalse(dex.classes.any { it.type == "Ljava/lang/Object;" })
        assertTrue(
            dex.getReferences(ReferenceType.METHOD)
                .filterIsInstance<MethodReference>()
                .any { it.definingClass == "Ljava/lang/Object;" },
        )
    }

    @Test
    fun editingOneLoadedClassPreservesTheCompleteInputDex() {
        java.nio.file.Files.writeString(root.resolve("output.smali"), SOURCE.trimIndent())
        java.nio.file.Files.writeString(
            root.resolve("unvisited.smali"),
            """
                .class public abstract Lwriter/Unvisited;
                .super Ljava/lang/Object;
                .field public static final marker:I = 0x17
                .method public abstract untouched()I
                .end method
            """.trimIndent(),
        )
        val input = root.resolve("input.dex").toFile()
        DexFileFactory.writeDexFile(
            input.toString(),
            ImmutableDexFile(Opcodes.forApi(20), ProgramInput.read(root.toFile(), 20).classes),
        )
        val index = ClassIndex.open(input, 20)
        val snapshotA = index.currentSnapshot
        val before = ProgramDexWriter().writeToBytes(snapshotA)
        val services = VmServices(index)
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView("Lwriter/Output;->value()I")))
        edit.replaceInstruction(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n(Opcode.CONST_4, 0, 6),
                ),
            ),
        )
        val result = edit.commit() as MethodEditCommitResult.Committed
        assertEquals(
            Value.Known(6, VirtualType.of("I")),
            services.newSession(result.editedMethodView).run().trusted()!!.terminatingConsensus(0),
        )

        val output = root.resolve("output.dex").toFile()
        ProgramDexWriter().write(index.currentSnapshot, output)
        val dex = DexBackedDexFile(Opcodes.forApi(20), output.readBytes())
        assertEquals(setOf("Lwriter/Output;", "Lwriter/Unvisited;"), dex.classes.map { it.type }.toSet())
        val unvisited = dex.classes.single { it.type == "Lwriter/Unvisited;" }
        assertEquals(23, (unvisited.staticFields.single().initialValue as IntEncodedValue).value)
        assertEquals("untouched", unvisited.methods.single().name)
        assertNull(unvisited.methods.single().implementation)
        val reloaded = ClassIndex.open(output, 20)
        val view = requireNotNull(reloaded.methodView("Lwriter/Output;->value()I"))
        assertEquals(
            Value.Known(6, VirtualType.of("I")),
            VmServices(reloaded).newSession(view).run().trusted()!!.terminatingConsensus(0),
        )
        assertArrayEquals(before, ProgramDexWriter().writeToBytes(snapshotA))
    }

    @Test
    fun snapshotWriterDoesNotRetainReferencesFromAnObsoleteApplicationMethodDefinition() {
        val index = classIndexFromSource(root, SOURCE)
        val initial = index.currentSnapshot
        val writer = ProgramDexWriter()
        writer.writeToBytes(initial)
        val original = requireNotNull(initial.applicationClass("Lwriter/Output;"))
        val obsolete = original.methods.single { it.declaration.name == "obsolete" }
        val replacementBody = MethodBody.import(
            ImmutableMethodImplementation(
                0,
                listOf(ImmutableInstruction10x(Opcode.RETURN_VOID)),
                emptyList(),
                emptyList(),
            ),
        ).let(MethodLayoutSolver::solve).let(MethodBodyAssembler::assemble)
        val replacement = ApplicationMethodDefinition.withBody(
            obsolete,
            replacementBody,
            index.programWorkspace.newApplicationMethodRevision(obsolete.methodSignature),
        )
        val current = ApplicationClassDefinition(
            original.type,
            original.accessFlags,
            original.superclass,
            original.interfaces,
            original.sourceFile,
            original.annotations,
            original.staticFields,
            original.instanceFields,
            original.methods.map { if (it === obsolete) replacement else it },
        )
        val snapshot = ProgramSnapshot.candidate(
            initial,
            index.programWorkspace.newProgramRevision(),
            persistentMapOf(current.type to current),
        )

        val dex = DexBackedDexFile(initial.outputApiPolicy.opcodes(), writer.writeToBytes(snapshot))
        val strings = dex.getReferences(ReferenceType.STRING).filterIsInstance<StringReference>().map { it.string }
        assertFalse("obsolete-only" in strings)
    }

    @Test
    fun snapshotWriterPreservesBothInvokePolymorphicReferences() {
        val dexFile = root.resolve("polymorphic.dex")
        val target = ImmutableMethodReference("Ljava/lang/invoke/MethodHandle;", "invokeExact", emptyList(), "V")
        val prototype = ImmutableMethodProtoReference(emptyList(), "V")
        val implementation = ImmutableMethodImplementation(
            1,
            listOf(
                ImmutableInstruction45cc(Opcode.INVOKE_POLYMORPHIC, 1, 0, 0, 0, 0, 0, target, prototype),
                ImmutableInstruction10x(Opcode.RETURN_VOID),
            ),
            emptyList(),
            emptyList(),
        )
        val method = ImmutableMethod(
            "Lwriter/Polymorphic;",
            "call",
            emptyList(),
            "V",
            Modifier.PUBLIC or Modifier.STATIC,
            emptySet(),
            emptySet(),
            implementation,
        )
        val definition = ImmutableClassDef(
            "Lwriter/Polymorphic;",
            Modifier.PUBLIC,
            "Ljava/lang/Object;",
            emptyList(),
            null,
            emptySet(),
            emptyList(),
            listOf(method),
        )
        DexFileFactory.writeDexFile(dexFile.toString(), ImmutableDexFile(Opcodes.forApi(30), listOf(definition)))
        val snapshot = ClassIndex.open(dexFile.toFile(), 30).currentSnapshot

        val dex = DexBackedDexFile(snapshot.outputApiPolicy.opcodes(), ProgramDexWriter().writeToBytes(snapshot))
        val instruction = dex.classes.single().methods.single().implementation!!.instructions
            .filterIsInstance<DualReferenceInstruction>()
            .single()

        assertEquals(ReferenceType.METHOD, instruction.referenceType)
        assertEquals(ReferenceType.METHOD_PROTO, instruction.referenceType2)
    }

    @Test
    fun shortStringPolicyRejectsAReferenceSetThatRequiresJumboIndexes() {
        val baseline = classIndexFromSource(root, SOURCE).currentSnapshot
        val annotation = ImmutableAnnotation(
            AnnotationVisibility.RUNTIME,
            "Lwriter/ManyStrings;",
            listOf(
                ImmutableAnnotationElement(
                    "values",
                    ImmutableArrayEncodedValue(
                        List(65_537) { index -> ImmutableStringEncodedValue("string-$index") },
                    ),
                ),
            ),
        )
        val definition = ApplicationClassDefinition(
            "Lwriter/TooManyStrings;",
            Modifier.PUBLIC,
            "Ljava/lang/Object;",
            emptyList(),
            null,
            setOf(annotation),
            emptyList(),
            emptyList(),
            emptyList(),
        )
        val revisionAuthority = ProgramRevisionAuthority()
        val identity = revisionAuthority.programIdentity
        val snapshot = ProgramSnapshot.initial(
            identity,
            revisionAuthority.newProgramRevision(),
            persistentMapOf(definition.type to definition),
            OutputApiPolicy(20),
            StringInstructionEncoding.CONST_STRING,
            baseline.hierarchy,
            baseline.frameworkCatalog,
        )

        assertThrows<IllegalStateException> { ProgramDexWriter().writeToBytes(snapshot) }
        val destination = root.resolve("atomic-output.dex").toFile()
        val previous = byteArrayOf(1, 2, 3, 4)
        destination.writeBytes(previous)

        assertThrows<IllegalStateException> { ProgramDexWriter().write(snapshot, destination) }
        assertArrayEquals(previous, destination.readBytes())
        assertTrue(destination.parentFile.listFiles().none { it.name.startsWith("simplify-${destination.name}-") })
    }

    @Test
    fun callSiteDisplayNameDoesNotEnterTheDexStringPool() {
        val dexFile = root.resolve("call-site.dex")
        val bootstrap = ImmutableMethodReference("Lwriter/Bootstrap;", "bootstrap", emptyList(), "V")
        val handle: MethodHandleReference = ImmutableMethodHandleReference(MethodHandleType.INVOKE_STATIC, bootstrap)
        val callSite = ImmutableCallSiteReference(
            "dexlib-display-name",
            handle,
            "encoded-method-name",
            ImmutableMethodProtoReference(emptyList(), "V"),
            emptyList(),
        )
        val implementation = ImmutableMethodImplementation(
            0,
            listOf(
                ImmutableInstruction35c(Opcode.INVOKE_CUSTOM, 0, 0, 0, 0, 0, 0, callSite),
                ImmutableInstruction10x(Opcode.RETURN_VOID),
            ),
            emptyList(),
            emptyList(),
        )
        val method = ImmutableMethod(
            "Lwriter/CallSite;",
            "call",
            emptyList(),
            "V",
            Modifier.PUBLIC or Modifier.STATIC,
            emptySet(),
            emptySet(),
            implementation,
        )
        val definition = ImmutableClassDef(
            "Lwriter/CallSite;",
            Modifier.PUBLIC,
            "Ljava/lang/Object;",
            emptyList(),
            null,
            emptySet(),
            emptyList(),
            listOf(method),
        )
        DexFileFactory.writeDexFile(dexFile.toString(), ImmutableDexFile(Opcodes.forApi(30), listOf(definition)))
        val snapshot = ClassIndex.open(dexFile.toFile(), 30).currentSnapshot

        val dex = DexBackedDexFile(snapshot.outputApiPolicy.opcodes(), ProgramDexWriter().writeToBytes(snapshot))
        val strings = dex.getReferences(ReferenceType.STRING).filterIsInstance<StringReference>().map { it.string }

        assertFalse("dexlib-display-name" in strings)
        assertTrue("encoded-method-name" in strings)
    }

    @Test
    fun snapshotWriterRebindsTypedCatchHandlers() {
        val snapshot = classIndexFromSource(root, TYPED_CATCH_SOURCE).currentSnapshot

        val dex = DexBackedDexFile(snapshot.outputApiPolicy.opcodes(), ProgramDexWriter().writeToBytes(snapshot))
        val tryBlock = dex.classes.single().methods.single().implementation!!.tryBlocks.single()

        assertEquals("Ljava/lang/Exception;", tryBlock.exceptionHandlers.single().exceptionType)
    }

    private companion object {
        const val SOURCE = """
            .class public abstract Lwriter/Output;
            .super Ljava/lang/Object;
            .source "output.smali"

            .annotation runtime Ljava/lang/Deprecated;
            .end annotation

            .field public static final number:I = 0xb
                .annotation runtime Lwriter/FieldMark;
                    value = "field-only"
                .end annotation
            .end field

            .method public static native nativeValue()I
            .end method

            .method public static value()I
                .registers 1
                const/4 v0, 0x5
                return v0
            .end method

            .method public static hash(Ljava/lang/Object;)I
                .registers 1
                .param p0, "value"
                .line 42
                invoke-virtual {p0}, Ljava/lang/Object;->hashCode()I
                move-result p0
                return p0
            .end method

            .method public static obsolete()V
                .registers 1
                const-string v0, "obsolete-only"
                return-void
            .end method
        """

        const val TYPED_CATCH_SOURCE = """
            .class public Lwriter/TypedCatch;
            .super Ljava/lang/Object;

            .method public static run()V
                .registers 1
                :try_start
                nop
                :try_end
                return-void
                :handler
                move-exception v0
                return-void
                .catch Ljava/lang/Exception; {:try_start .. :try_end} :handler
            .end method
        """

    }
}
