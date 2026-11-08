package org.cf.smalivm.core

import java.lang.reflect.Modifier
import java.nio.file.Files
import java.nio.file.Path
import com.android.tools.smali.dexlib2.AnnotationVisibility
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35c
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableFieldReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodProtoReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableTypeReference
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class MethodReferenceStagingTest {
    @TempDir
    lateinit var root: Path
    private var fixtureSequence = 0

    @Test
    fun duplicateRequestsReturnTheSameTransactionReference() {
        val index = index(SOURCE)
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
        val parameters = mutableListOf("I")

        assertSame(edit.stageType("Ljava/lang/String;"), edit.stageType("Ljava/lang/String;"))
        assertSame(edit.stageString("value"), edit.stageString("value"))
        assertSame(
            edit.stageField("Lstaging/References;", "field", "I"),
            edit.stageField("Lstaging/References;", "field", "I"),
        )
        val method = edit.stageMethod("Lstaging/References;", "method", parameters, "V")
        val prototype = edit.stagePrototype(parameters, "V")
        parameters.clear()
        assertSame(method, edit.stageMethod("Lstaging/References;", "method", listOf("I"), "V"))
        assertSame(prototype, edit.stagePrototype(listOf("I"), "V"))

        assertSame(MethodEditCommitResult.Unchanged, edit.commit())

    }

    @Test
    fun arrayMethodOwnersAndVoidTypeConstantsAreValid() {
        val index = index(SOURCE)
        val editor = VmServices(index).methodEditor
        val methodEdit = editor.beginEdit(requireNotNull(index.methodView(TARGET)))
        assertSame(
            methodEdit.stageMethod("[I", "clone", emptyList(), "Ljava/lang/Object;"),
            methodEdit.stageMethod("[I", "clone", emptyList(), "Ljava/lang/Object;"),
        )
        assertSame(MethodEditCommitResult.Unchanged, methodEdit.commit())

        val typeEdit = editor.beginEdit(requireNotNull(index.methodView(TARGET)))
        val voidType = typeEdit.stageType("V")
        typeEdit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(Opcode.CONST_CLASS, 0, ImmutableTypeReference("Ljava/lang/Object;")),
                    DexReferenceOperand.Staged(voidType),
                ),
            ),
        )

        val committed = typeEdit.commit() as MethodEditCommitResult.Committed
        val dex = DexBackedDexFile(
            index.currentSnapshot.outputApiPolicy.opcodes(),
            ProgramDexWriter().writeToBytes(index.currentSnapshot),
        )
        val inserted = dex.classes.single { it.type == CLASS_TYPE }.methods
            .single { it.name == "target" }.implementation!!.instructions.first() as ReferenceInstruction

        assertEquals(Opcode.CONST_CLASS, committed.editedMethodView.opcodeAt(0))
        assertEquals("V", (inserted.reference as TypeReference).type)
    }

    @Test
    fun malformedDescriptorsRejectWithoutChangingTheSnapshotOrOutput() {
        val index = index(SOURCE)
        val snapshot = index.currentSnapshot
        val writer = ProgramDexWriter()
        val output = writer.writeToBytes(snapshot)
        val invalidRequests = listOf<Pair<(MethodEditTransaction) -> StagedDexReference, MethodEditRejectionReason>>(
            { edit: MethodEditTransaction -> edit.stageType("[V") } to MethodEditRejectionReason.MalformedDescriptor("[V"),
            { edit: MethodEditTransaction -> edit.stageField("I", "field", "I") } to
                MethodEditRejectionReason.MalformedDescriptor("I"),
            { edit: MethodEditTransaction -> edit.stageField("Lstaging/References;", "bad/name", "I") } to
                MethodEditRejectionReason.MalformedMemberName("bad/name"),
            { edit: MethodEditTransaction -> edit.stageField("Lstaging/References;", "field", "V") } to
                MethodEditRejectionReason.MalformedDescriptor("V"),
            { edit: MethodEditTransaction -> edit.stageMethod("I", "method", emptyList(), "V") } to
                MethodEditRejectionReason.MalformedDescriptor("I"),
            { edit: MethodEditTransaction -> edit.stageMethod("Lstaging/References;", "bad/name", emptyList(), "V") } to
                MethodEditRejectionReason.MalformedMemberName("bad/name"),
            { edit: MethodEditTransaction -> edit.stageMethod("Lstaging/References;", "method", listOf("V"), "V") } to
                MethodEditRejectionReason.MalformedDescriptor("V"),
            { edit: MethodEditTransaction -> edit.stageMethod("Lstaging/References;", "method", emptyList(), "[V") } to
                MethodEditRejectionReason.MalformedDescriptor("[V"),
            { edit: MethodEditTransaction -> edit.stagePrototype(listOf("Q"), "V") } to
                MethodEditRejectionReason.MalformedDescriptor("Q"),
            { edit: MethodEditTransaction -> edit.stagePrototype(emptyList(), "[V") } to
                MethodEditRejectionReason.MalformedDescriptor("[V"),
        )

        for ((request, expected) in invalidRequests) {
            val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
            request(edit)

            val result = edit.commit() as MethodEditCommitResult.Rejected

            assertEquals(expected, result.reason)
            assertSame(snapshot, index.currentSnapshot)
            assertArrayEquals(output, writer.writeToBytes(index.currentSnapshot))
        }
    }

    @Test
    fun stagedOperandsRejectForeignReferencesAndWrongReferenceKinds() {
        val index = index(SOURCE)
        val editor = VmServices(index).methodEditor
        val view = requireNotNull(index.methodView(TARGET))
        val foreign = editor.beginEdit(view)
        val foreignString = foreign.stageString("foreign")
        val local = editor.beginEdit(view)
        local.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("placeholder")),
                    DexReferenceOperand.Staged(foreignString),
                ),
            ),
        )

        val foreignResult = local.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.ForeignStagedReference, foreignResult.reason)
        foreign.discard()

        val wrongKind = editor.beginEdit(view)
        val string = wrongKind.stageString("wrong-kind")
        wrongKind.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(Opcode.CONST_CLASS, 0, ImmutableTypeReference("Ljava/lang/Object;")),
                    DexReferenceOperand.Staged(string),
                ),
            ),
        )

        val wrongKindResult = wrongKind.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.ReferenceKindMismatch(Opcode.CONST_CLASS), wrongKindResult.reason)

        val invalidNewInstance = editor.beginEdit(view)
        val arrayType = invalidNewInstance.stageType("[I")
        invalidNewInstance.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(Opcode.NEW_INSTANCE, 0, ImmutableTypeReference("Ljava/lang/Object;")),
                    DexReferenceOperand.Staged(arrayType),
                ),
            ),
        )

        val invalidNewInstanceResult = invalidNewInstance.commit() as MethodEditCommitResult.Rejected

        assertEquals(
            MethodEditRejectionReason.InvalidTypeOperand(Opcode.NEW_INSTANCE, "[I"),
            invalidNewInstanceResult.reason,
        )

        val unstagedOperand = editor.beginEdit(view)
        unstagedOperand.stageMethod("Lstaging/References;", "transitive-only", emptyList(), "V")
        unstagedOperand.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(
                        Opcode.CONST_STRING,
                        0,
                        ImmutableStringReference("transitive-only"),
                    ),
                ),
            ),
        )

        val unstagedResult = unstagedOperand.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.UnstagedReference, unstagedResult.reason)
    }

    @Test
    fun projectedFieldOverflowRejectsWithoutChangingTheSnapshotOrOutput() {
        val index = fieldBoundaryIndex()
        val snapshot = index.currentSnapshot
        val writer = ProgramDexWriter()
        val output = writer.writeToBytes(snapshot)
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
        val field = edit.stageField("Lstaging/overflow/NewOwner;", "newField", "Lstaging/overflow/NewType;")
        edit.insertInstructionsBefore(
            0,
            listOf(
                staged(
                    ImmutableInstruction21c(
                        Opcode.SGET_OBJECT,
                        0,
                        ImmutableFieldReference("Lplaceholder;", "field", "Ljava/lang/Object;"),
                    ),
                    field,
                ),
            ),
        )

        val result = edit.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.DexReferenceLimit(DexSection.FIELD_IDS), result.reason)
        assertSame(snapshot, index.currentSnapshot)
        assertArrayEquals(output, writer.writeToBytes(index.currentSnapshot))
    }

    @Test
    fun everyStagedReferenceSurvivesDexWriteAndReload() {
        val index = api30Index()
        val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(API30_TARGET)))
        val string = edit.stageString(STAGED_STRING)
        val type = edit.stageType(STAGED_TYPE)
        val field = edit.stageField(STAGED_OWNER, STAGED_FIELD, "I")
        val method = edit.stageMethod(STAGED_OWNER, STAGED_METHOD, listOf("I"), "V")
        val prototype = edit.stagePrototype(listOf("I"), "V")
        val scratch = edit.reserveScratchRegisters(1)
        edit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Encoded(
                    ImmutableInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("placeholder")),
                    DexReferenceOperand.Staged(string),
                    listOf(scratch[0]),
                ),
                staged(
                    ImmutableInstruction21c(Opcode.CONST_CLASS, 1, ImmutableTypeReference("Ljava/lang/Object;")),
                    type,
                ),
                staged(
                    ImmutableInstruction21c(
                        Opcode.SGET,
                        2,
                        ImmutableFieldReference("Lplaceholder;", "field", "I"),
                    ),
                    field,
                ),
                InstructionTemplate.Encoded(
                    ImmutableInstruction35c(
                        Opcode.INVOKE_STATIC,
                        1,
                        2,
                        0,
                        0,
                        0,
                        0,
                        ImmutableMethodReference("Lplaceholder;", "method", emptyList(), "V"),
                    ),
                    DexReferenceOperand.Staged(method),
                    listOf(scratch[0]),
                ),
                staged(
                    ImmutableInstruction21c(
                        Opcode.CONST_METHOD_TYPE,
                        3,
                        ImmutableMethodProtoReference(emptyList(), "V"),
                    ),
                    prototype,
                ),
            ),
        )

        val committed = edit.commit()

        assertTrue(
            committed is MethodEditCommitResult.Committed,
            (committed as? MethodEditCommitResult.Rejected)?.reason.toString(),
        )
        assertEquals(5, (committed as MethodEditCommitResult.Committed).editedMethodView.registerCount)
        val snapshot = index.currentSnapshot
        val dexFile = root.resolve("staged-references.dex")
        Files.write(dexFile, ProgramDexWriter().writeToBytes(snapshot))
        val reloaded = ClassIndex.open(dexFile.toFile(), 30).currentSnapshot

        assertEquals(snapshot.dexReferenceClosure.strings, reloaded.dexReferenceClosure.strings)
        assertEquals(snapshot.dexReferenceClosure.types, reloaded.dexReferenceClosure.types)
        assertEquals(snapshot.dexReferenceClosure.fields, reloaded.dexReferenceClosure.fields)
        assertEquals(snapshot.dexReferenceClosure.methods, reloaded.dexReferenceClosure.methods)
        assertEquals(snapshot.dexReferenceClosure.prototypes, reloaded.dexReferenceClosure.prototypes)
        assertEquals(snapshot.dexReferenceClosure.methodHandles, reloaded.dexReferenceClosure.methodHandles)
        assertEquals(snapshot.dexReferenceClosure.callSites, reloaded.dexReferenceClosure.callSites)
        assertTrue(STAGED_STRING in reloaded.dexReferenceClosure.strings)
        assertTrue(STAGED_TYPE in reloaded.dexReferenceClosure.types)
        assertTrue(reloaded.dexReferenceClosure.fields.any { it.name == STAGED_FIELD })
        assertTrue(reloaded.dexReferenceClosure.methods.any { it.name == STAGED_METHOD })
        assertTrue(reloaded.dexReferenceClosure.prototypes.any { it.parameterTypes == listOf("I") })
    }

    @Test
    fun transitiveFieldStringsTriggerOneAtomicJumboRelayoutAndStayWide() {
        val index = stringBoundaryIndex()
        val services = VmServices(index)
        val snapshotA = index.currentSnapshot
        val targetA = requireNotNull(snapshotA.applicationMethod(TARGET))
        val collateralA = requireNotNull(snapshotA.applicationMethod(COLLATERAL))
        val unaffectedA = requireNotNull(snapshotA.applicationMethod(UNAFFECTED))
        val collateralViewA = requireNotNull(index.methodView(COLLATERAL))
        val edit = services.methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
        val field = edit.stageField(CLASS_TYPE, STAGED_FIELD, "I")
        edit.insertInstructionsBefore(
            0,
            listOf(
                staged(
                    ImmutableInstruction21c(
                        Opcode.SGET,
                        0,
                        ImmutableFieldReference("Lplaceholder;", "field", "I"),
                    ),
                    field,
                ),
            ),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val snapshotB = index.currentSnapshot
        val collateralB = requireNotNull(snapshotB.applicationMethod(COLLATERAL))
        val collateralViewB = requireNotNull(index.methodView(COLLATERAL))

        assertEquals(StringInstructionEncoding.CONST_STRING_JUMBO, snapshotB.stringInstructionEncoding)
        assertEquals(setOf(TARGET, COLLATERAL), committed.createdMethodRevisionsBySignature.keys.map { it.toString() }.toSet())
        assertNotSame(targetA.methodRevision, snapshotB.applicationMethod(TARGET)?.methodRevision)
        assertNotSame(collateralA.methodRevision, collateralB.methodRevision)
        assertSame(unaffectedA, snapshotB.applicationMethod(UNAFFECTED))
        assertSame(snapshotA.frameworkCatalog, snapshotB.frameworkCatalog)
        assertEquals(Opcode.CONST_STRING_JUMBO, collateralViewB.opcodeAt(0))
        assertThrows(IllegalArgumentException::class.java) { services.newSession(collateralViewA) }

        val later = services.methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
        val laterString = later.stageString("000-later-string")
        val returnAddress = requireNotNull(index.methodView(TARGET)).terminalInstructionAddresses.single()
        later.insertInstructionsBefore(
            returnAddress,
            listOf(
                staged(
                    ImmutableInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("placeholder")),
                    laterString,
                ),
            ),
        )
        val laterCommit = later.commit() as MethodEditCommitResult.Committed
        val snapshotC = index.currentSnapshot
        val collateralViewC = requireNotNull(index.methodView(COLLATERAL))

        assertEquals(Opcode.CONST_STRING_JUMBO, laterCommit.editedMethodView.opcodeAt(returnAddress))
        assertSame(collateralB, snapshotC.applicationMethod(COLLATERAL))
        assertArrayEquals(collateralViewB.instructionAddresses, collateralViewC.instructionAddresses)
        assertEquals(Opcode.CONST_STRING_JUMBO, collateralViewC.opcodeAt(0))
        assertEquals(StringInstructionEncoding.CONST_STRING_JUMBO, snapshotC.stringInstructionEncoding)
    }

    @Test
    fun referencesFromAnObsoleteEditedDefinitionDoNotEnterCurrentOutput() {
        val index = index(SOURCE)
        val services = VmServices(index)
        val add = services.methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
        val string = add.stageString(OBSOLETE_STRING)
        add.insertInstructionsBefore(
            0,
            listOf(
                staged(
                    ImmutableInstruction21c(Opcode.CONST_STRING, 0, ImmutableStringReference("placeholder")),
                    string,
                ),
            ),
        )
        add.commit() as MethodEditCommitResult.Committed
        val obsoleteSnapshot = index.currentSnapshot
        assertTrue(OBSOLETE_STRING in obsoleteSnapshot.dexReferenceClosure.strings)

        val remove = services.methodEditor.beginEdit(requireNotNull(index.methodView(TARGET)))
        remove.removeInstruction(0)
        remove.commit() as MethodEditCommitResult.Committed
        val current = index.currentSnapshot
        val dex = DexBackedDexFile(current.outputApiPolicy.opcodes(), ProgramDexWriter().writeToBytes(current))
        val strings = dex.getReferences(ReferenceType.STRING).filterIsInstance<StringReference>().map { it.string }

        assertFalse(OBSOLETE_STRING in current.dexReferenceClosure.strings)
        assertFalse(OBSOLETE_STRING in strings)
        assertTrue(OBSOLETE_STRING in obsoleteSnapshot.dexReferenceClosure.strings)
    }

    private fun staged(
        instruction: com.android.tools.smali.dexlib2.iface.instruction.Instruction,
        reference: StagedDexReference,
    ): InstructionTemplate = InstructionTemplate.Encoded(instruction, DexReferenceOperand.Staged(reference))

    private fun index(source: String): ClassIndex {
        val directory = Files.createDirectory(root.resolve("fixture-${fixtureSequence++}"))
        return classIndexFromSource(directory, source)
    }

    private fun api30Index(): ClassIndex {
        val implementation = ImmutableMethodImplementation(
            4,
            listOf(ImmutableInstruction10x(Opcode.RETURN_VOID)),
            emptyList(),
            emptyList(),
        )
        val method = ImmutableMethod(
            API30_CLASS,
            "run",
            emptyList(),
            "V",
            Modifier.PUBLIC or Modifier.STATIC,
            emptySet(),
            emptySet(),
            implementation,
        )
        val definition = ImmutableClassDef(
            API30_CLASS,
            Modifier.PUBLIC,
            "Ljava/lang/Object;",
            emptyList(),
            null,
            emptySet(),
            emptyList(),
            listOf(method),
        )
        val dexFile = root.resolve("api30-input.dex")
        DexFileFactory.writeDexFile(dexFile.toString(), ImmutableDexFile(Opcodes.forApi(30), listOf(definition)))
        return ClassIndex.open(dexFile.toFile(), 30)
    }

    /** A fresh import of [SOURCE] whose string section holds exactly 65,536 entries. */
    private fun stringBoundaryIndex(): ClassIndex {
        val annotationType = "Lstaging/StringBoundary;"
        val existingStrings = index(SOURCE).currentSnapshot.dexReferenceClosure.strings
        var stringCount = existingStrings.size + if (annotationType in existingStrings) 0 else 1
        val annotation = StringBuilder("\n.annotation runtime $annotationType\n")
        var sequence = 0
        while (stringCount < 65_536) {
            val name = "boundary_$sequence"
            sequence++
            if (name in existingStrings) continue
            annotation.append(name).append(" = 0x0\n")
            stringCount++
        }
        annotation.append(".end annotation\n")
        val index = index(SOURCE.trimIndent() + annotation)
        assertEquals(65_536, index.currentSnapshot.dexReferenceClosure.strings.size)
        return index
    }

    /** A fresh import of [SOURCE] whose field section holds exactly 65,536 entries. */
    private fun fieldBoundaryIndex(): ClassIndex {
        val annotation = StringBuilder("\n.annotation runtime Lstaging/FieldBoundary;\nvalues = {\n")
        for (sequence in 0 until 65_536) {
            val owner = "Lstaging/overflow/Owner${sequence % 41};"
            val name = "field${sequence / 41 % 41}"
            val type = "Lstaging/overflow/Type${sequence / (41 * 41)};"
            if (sequence > 0) annotation.append(",\n")
            annotation.append(owner).append("->").append(name).append(':').append(type)
        }
        annotation.append("\n}\n.end annotation\n")
        val index = index(SOURCE.trimIndent() + annotation)
        assertEquals(65_536, index.currentSnapshot.dexReferenceClosure.fields.size)
        return index
    }

    private companion object {
        const val CLASS_TYPE = "Lstaging/References;"
        const val TARGET = "Lstaging/References;->target()V"
        const val COLLATERAL = "Lstaging/References;->collateral()V"
        const val UNAFFECTED = "Lstaging/References;->unaffected()V"
        const val API30_CLASS = "Lstaging/Api30References;"
        const val API30_TARGET = "Lstaging/Api30References;->run()V"
        const val STAGED_STRING = "task59-staged-string"
        const val STAGED_OWNER = "Lstaging/StagedOwner;"
        const val STAGED_TYPE = "[Lstaging/StagedOwner;"
        const val STAGED_FIELD = "stagedField"
        const val STAGED_METHOD = "stagedMethod"
        const val OBSOLETE_STRING = "task59-obsolete-only"
        const val SOURCE = """
            .class public Lstaging/References;
            .super Ljava/lang/Object;

            .method public static target()V
                .registers 1
                return-void
            .end method

            .method public static collateral()V
                .registers 1
                const-string v0, "collateral"
                goto :done
                nop
                :done
                return-void
            .end method

            .method public static unaffected()V
                .registers 0
                return-void
            .end method
        """
    }
}
