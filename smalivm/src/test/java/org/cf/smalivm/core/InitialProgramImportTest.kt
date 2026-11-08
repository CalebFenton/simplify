package org.cf.smalivm.core

import java.lang.reflect.Modifier
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.collections.immutable.persistentMapOf
import org.cf.smalivm.core.VerifierRejection.Reason.HANDLER_NOT_AN_INSTRUCTION
import org.cf.smalivm.core.VerifierRejection.Reason.NO_INSTRUCTIONS
import org.cf.smalivm.core.VerifierRejection.Reason.SWITCH_TABLE_OF_WRONG_KIND
import org.cf.smalivm.core.VerifierRejection.Reason.TARGET_NOT_AN_INSTRUCTION
import org.cf.smalivm.core.VerifierRejection.Reason.TRY_RANGE_OUTSIDE_INSTRUCTIONS
import org.cf.smalivm.dex.SmaliParser
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.Format
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.dexbacked.DexBackedDexFile
import com.android.tools.smali.dexlib2.iface.MethodImplementation
import com.android.tools.smali.dexlib2.iface.debug.LineNumber
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile
import com.android.tools.smali.dexlib2.immutable.ImmutableExceptionHandler
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableLineNumber
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11n
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction22s
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction35c
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutablePackedSwitchPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableSparseSwitchPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableSwitchElement
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableMethodReference
import com.android.tools.smali.dexlib2.immutable.reference.ImmutableStringReference
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class InitialProgramImportTest {
    @TempDir
    lateinit var root: Path

    @Test
    fun initialImportOwnsCompleteApplicationDeclarationsAndBodies() {
        val index = classIndexFromSource(root, SOURCE)
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
        Files.writeString(root.resolve("task53.smali"), SOURCE.trimIndent())

        val snapshot = ClassIndex.open(root.toFile(), 28).currentSnapshot

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

        val snapshot = ClassIndex.open(dexFile.toFile(), SmaliParser.DEX_API_LEVEL).currentSnapshot

        assertEquals(StringInstructionEncoding.CONST_STRING_JUMBO, snapshot.stringInstructionEncoding)
    }

    @Test
    fun importedSnapshotDoesNotRereadItsSourceFiles() {
        val source = root.resolve("task53.smali")
        Files.writeString(source, SOURCE.trimIndent())
        val index = ClassIndex.open(root.toFile())
        val imported = requireNotNull(index.methodView("Ltask53/Fixture;->value()I"))

        Files.writeString(source, SOURCE.trimIndent().replace("const/4 v0, 0x1", "const/4 v0, 0x2"))

        val current = requireNotNull(index.methodView("Ltask53/Fixture;->value()I"))
        assertEquals(imported.addresses.toList(), current.addresses.toList())
        val graph = VmServices(index).newSession(current).run()
        assertEquals(Value.Known(1, VirtualType.of("I")), graph.trusted()!!.terminatingConsensus(0))
        val reimported = ClassIndex.open(root.toFile())
        val reimportedView = requireNotNull(reimported.methodView("Ltask53/Fixture;->value()I"))
        val reimportedGraph = VmServices(reimported).newSession(reimportedView).run()
        assertEquals(Value.Known(2, VirtualType.of("I")), reimportedGraph.trusted()!!.terminatingConsensus(0))
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
        val index = ClassIndex.open(dexFile.toFile())
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
        val reloaded = ClassIndex.open(output)
        val outputView = requireNotNull(reloaded.methodView("Ltask53/SharedSwitch;->shared(I)V"))
        assertEquals(listOf(0 to 6), outputView.switchCasesAt(0))
        assertEquals(listOf(0 to 10), outputView.switchCasesAt(3))
        assertEquals(2, outputView.payloadAddresses.size)
    }

    /**
     * ART's method verifier rejects a non-range invoke whose four-bit count declares more than five registers
     * (`runtime/verifier/method_verifier.cc`, `kVerifyVarArg`: "invalid arg count"), and that fails the whole
     * declaring class. The DEX file verifier does not decode instructions, so the rest of the file loads. ART
     * throws `VerifyError` at the class's first use; here the class is outside the loaded set and the use throws
     * `NoClassDefFoundError`. Both are `LinkageError`s, so a `catch (Exception)` around the call is dead either way.
     */
    @Test
    fun anInvokeDeclaringSixRegistersRejectsItsClassAndTheRestLoads() {
        val index = hostileIndex(rejectedInvokeClass(), callerClass())
        val rejected = requireNotNull(index.currentSnapshot.applicationMethod("$REJECTED_INVOKE->sixRegisters()V"))

        assertEquals(
            VerifierRejection(VerifierRejection.Reason.ARGUMENT_COUNT_ABOVE_FIVE, 0),
            rejected.rejectedCode?.rejection,
        )
        assertNull(index.methodView("$REJECTED_INVOKE->sixRegisters()V"), "rejected code has no view to decode")
        assertFalse(index.currentSnapshot.hierarchy.isLoadable(VirtualType.of(REJECTED_INVOKE)))
        val caller = requireNotNull(index.methodView("$CALLER->callsRejectedClass()I"))
        val escaping = CoreTester.escapingTypes(VmServices(index).newSession(caller).run()).single()
        assertTrue(
            index.isSubtypeOf(VirtualType.of(escaping), VirtualType.of("Ljava/lang/LinkageError;")),
            "the call into the rejected class throws a LinkageError, got $escaping",
        )
    }

    /** Output keeps rejected code as the input encoded it, so the written program fails verification as the input did. */
    @Test
    fun rejectedCodeIsWrittenAsTheInputEncodedIt() {
        val tryStart = hostileClass(
            "Lhostile/TryStart;",
            runMethod("Lhostile/TryStart;", "", 1, CONST_THEN_RETURN, listOf(catchAll(1, 2, 2))),
        )
        val index = hostileIndex(rejectedInvokeClass(), tryStart)

        val written = DexBackedDexFile(
            Opcodes.forApi(SmaliParser.DEX_API_LEVEL),
            ProgramDexWriter().writeToBytes(index.currentSnapshot),
        )

        val invoke = written.code(REJECTED_INVOKE, "sixRegisters").instructions.first() as Instruction35c
        assertEquals(6, invoke.registerCount)
        assertEquals(CALLEE, invoke.reference)
        val tryBlock = written.code("Lhostile/TryStart;", "run").tryBlocks.single()
        assertEquals(1 to 2, tryBlock.startCodeAddress to tryBlock.codeUnitCount)
    }

    /**
     * Each method fails ART's method verifier (`runtime/verifier/method_verifier.cc`) with
     * `VERIFY_ERROR_BAD_CLASS_HARD`: "code item has no opcode"; "'try' block starts inside an instruction" and "bad
     * exception entry" (`ScanTryCatchBlocks`); "exception handler starts at bad address"; "invalid branch target" and
     * "invalid switch target" (`CheckAndMarkBranchTarget`, `CheckAndMarkSwitchTargets`); a switch naming no table of
     * its kind ("unaligned switch table", "wrong signature for switch table").
     * The file loads and only the declaring class leaves the loaded set.
     */
    @Test
    fun codeTheMethodVerifierRejectsRejectsOnlyItsClass() {
        data class Row(
            val shape: String,
            val parameters: String,
            val registers: Int,
            val instructions: List<Instruction>,
            val tryBlocks: List<ImmutableTryBlock>,
            val expected: VerifierRejection,
        )
        val rows = listOf(
            Row("no instructions", "", 1, emptyList(), emptyList(), rejection(NO_INSTRUCTIONS, 0)),
            Row(
                "try starts inside an instruction", "", 1, CONST_THEN_RETURN, listOf(catchAll(1, 2, 2)),
                rejection(TRY_RANGE_OUTSIDE_INSTRUCTIONS, 1),
            ),
            Row(
                "try is empty", "", 1, CONST_THEN_RETURN, listOf(catchAll(0, 0, 2)),
                rejection(TRY_RANGE_OUTSIDE_INSTRUCTIONS, 0),
            ),
            Row(
                "handler starts inside an instruction", "", 1, CONST_THEN_RETURN, listOf(catchAll(0, 2, 1)),
                rejection(HANDLER_NOT_AN_INSTRUCTION, 1),
            ),
            Row(
                "goto into an instruction", "", 1,
                listOf(ImmutableInstruction10t(Opcode.GOTO, 2)) + CONST_THEN_RETURN,
                emptyList(), rejection(TARGET_NOT_AN_INSTRUCTION, 2),
            ),
            Row(
                "goto past the code", "", 1,
                listOf(
                    ImmutableInstruction11n(Opcode.CONST_4, 0, 1),
                    ImmutableInstruction10t(Opcode.GOTO, 9),
                    ImmutableInstruction11x(Opcode.RETURN, 0),
                ),
                emptyList(), rejection(TARGET_NOT_AN_INSTRUCTION, 10),
            ),
            Row(
                "switch case into an instruction", "I", 2,
                listOf(ImmutableInstruction31t(Opcode.PACKED_SWITCH, 1, 6)) + CONST_THEN_RETURN +
                    ImmutablePackedSwitchPayload(listOf(ImmutableSwitchElement(0, 4))),
                emptyList(), rejection(TARGET_NOT_AN_INSTRUCTION, 4),
            ),
            Row(
                "switch naming an instruction that is not a table", "I", 2,
                listOf(
                    ImmutableInstruction31t(Opcode.PACKED_SWITCH, 1, 3),
                    ImmutableInstruction11n(Opcode.CONST_4, 0, 1),
                    ImmutableInstruction11x(Opcode.RETURN, 0),
                ),
                emptyList(), rejection(SWITCH_TABLE_OF_WRONG_KIND, 3),
            ),
            Row(
                "packed-switch naming a sparse table", "I", 2,
                listOf(ImmutableInstruction31t(Opcode.PACKED_SWITCH, 1, 6)) + CONST_THEN_RETURN +
                    ImmutableSparseSwitchPayload(listOf(ImmutableSwitchElement(0, 3))),
                emptyList(), rejection(SWITCH_TABLE_OF_WRONG_KIND, 6),
            ),
        )
        for ((ordinal, row) in rows.withIndex()) {
            val type = "Lhostile/Row$ordinal;"
            val index = hostileIndex(
                hostileClass(type, runMethod(type, row.parameters, row.registers, row.instructions, row.tryBlocks)),
                healthyClass(),
                file = "row$ordinal.dex",
            )
            val method = requireNotNull(index.currentSnapshot.applicationMethod("$type->run(${row.parameters})I"))
            assertEquals(row.expected, method.rejectedCode?.rejection, row.shape)
            assertFalse(index.currentSnapshot.hierarchy.isLoadable(VirtualType.of(type)), row.shape)
            assertTrue(index.currentSnapshot.hierarchy.isLoadable(VirtualType.of(HEALTHY)), row.shape)
        }
    }

    /**
     * ART's DEX file verifier (`libdexfile/dex/dex_file_verifier.cc`) refuses the whole file for these, so no class
     * of it runs: code on an abstract method or none on a concrete one (`CheckMethodAccessFlags`), registers fewer
     * than the parameter words (`CheckIntraCodeItem`'s "ins_size > registers_size"), and a try range or handler
     * outside the code (`CheckIntraCodeItem`, `CheckAndGetHandlerOffsets`).
     */
    @Test
    fun codeTheDexFileVerifierRefusesRefusesTheWholeInput() {
        val type = "Lhostile/Refused;"
        val static = Modifier.PUBLIC or Modifier.STATIC
        val refused = mapOf(
            "abstract method with code" to method(type, "", Modifier.PUBLIC or Modifier.ABSTRACT, 1, CONST_THEN_RETURN),
            "concrete method without code" to ImmutableMethod(type, "run", emptyList(), "I", static, null, null, null),
            "registers fewer than the parameter words" to method(type, "J", static, 1, CONST_THEN_RETURN),
            "try range past the code" to method(type, "", static, 1, CONST_THEN_RETURN, listOf(catchAll(0, 4, 2))),
            "handler past the code" to method(type, "", static, 1, CONST_THEN_RETURN, listOf(catchAll(0, 2, 7))),
        )
        for ((shape, method) in refused) {
            assertThrows(ProgramInputException::class.java, { hostileIndex(hostileClass(type, method)) }, shape)
        }
    }

    /**
     * ART's method verifier accepts a try range that ends inside an instruction and covers every instruction that
     * starts before the end (`ScanTryCatchBlocks`). The division by the literal zero at 0 starts inside [0, 1), so its
     * `ArithmeticException` reaches the catch-all handler.
     */
    @Test
    fun aTryRangeEndingInsideAnInstructionCoversThatInstruction() {
        val instructions = listOf(
            ImmutableInstruction22s(Opcode.DIV_INT_LIT16, 0, 1, 0),
            ImmutableInstruction11x(Opcode.RETURN, 0),
            ImmutableInstruction11n(Opcode.CONST_4, 0, -1),
            ImmutableInstruction11x(Opcode.RETURN, 0),
        )
        val type = "Lhostile/TryEnd;"
        val index = hostileIndex(hostileClass(type, runMethod(type, "I", 2, instructions, listOf(catchAll(0, 1, 3)))))

        val graph = VmServices(index).newSession(requireNotNull(index.methodView("$type->run(I)I"))).run()

        assertEquals(Value.Known(-1, VirtualType.of("I")), graph.trusted()!!.terminatingConsensus(0))
    }

    /**
     * ART checks no debug item address (`dex_file_verifier.cc`'s `CheckIntraDebugInfoItem` reads `DBG_ADVANCE_PC`
     * unbounded), and the line of a pc is that of the last position entry at or before it
     * (`CodeItemDebugInfoAccessor::GetLineNumForPc`, `libdexfile/dex/code_item_accessors-inl.h`). For the input's
     * entries at 1 and 100, the instruction at 0 has no line and the one at 2 has line 42; the output must agree.
     */
    @Test
    fun aDebugItemInsideOrPastTheCodeTakesEffectAtTheNextInstruction() {
        val type = "Lhostile/Lines;"
        val entries = listOf(ImmutableLineNumber(1, 42), ImmutableLineNumber(100, 43))
        val method = ImmutableMethod(
            type, "run", emptyList(), "I", Modifier.PUBLIC or Modifier.STATIC, null, null,
            ImmutableMethodImplementation(1, CONST_THEN_RETURN, emptyList(), entries),
        )
        val index = hostileIndex(hostileClass(type, method))

        val output = DexBackedDexFile(
            Opcodes.forApi(SmaliParser.DEX_API_LEVEL),
            ProgramDexWriter().writeToBytes(index.currentSnapshot),
        )

        val lines = output.code(type, "run").debugItems.filterIsInstance<LineNumber>()
        assertEquals(listOf(null, 42), listOf(0, 2).map { pc -> lines.lastOrNull { it.codeAddress <= pc }?.lineNumber })
    }

    private fun hostileIndex(vararg classes: ImmutableClassDef, file: String = "hostile.dex"): ClassIndex {
        val dexFile = root.resolve(file)
        DexFileFactory.writeDexFile(
            dexFile.toString(),
            ImmutableDexFile(Opcodes.forApi(SmaliParser.DEX_API_LEVEL), classes.toList()),
        )
        return ClassIndex.open(dexFile.toFile(), SmaliParser.DEX_API_LEVEL)
    }

    private fun hostileClass(type: String, vararg methods: ImmutableMethod) =
        ImmutableClassDef(
            type, Modifier.PUBLIC, "Ljava/lang/Object;", emptyList(), null, emptySet(), emptyList(), methods.toList(),
        )

    private fun method(
        type: String,
        parameters: String,
        accessFlags: Int,
        registers: Int,
        instructions: List<Instruction>,
        tryBlocks: List<ImmutableTryBlock> = emptyList(),
        name: String = "run",
        returnType: String = "I",
    ) = ImmutableMethod(
        type,
        name,
        if (parameters.isEmpty()) emptyList() else listOf(ImmutableMethodParameter(parameters, emptySet(), null)),
        returnType,
        accessFlags,
        emptySet(),
        emptySet(),
        ImmutableMethodImplementation(registers, instructions, tryBlocks, emptyList()),
    )

    private fun runMethod(
        type: String,
        parameters: String,
        registers: Int,
        instructions: List<Instruction>,
        tryBlocks: List<ImmutableTryBlock> = emptyList(),
    ) = method(type, parameters, Modifier.PUBLIC or Modifier.STATIC, registers, instructions, tryBlocks)

    private fun rejectedInvokeClass() = hostileClass(
        REJECTED_INVOKE,
        method(
            REJECTED_INVOKE, "", Modifier.PUBLIC or Modifier.STATIC, 5,
            listOf(SixRegisterInvoke(CALLEE), ImmutableInstruction10x(Opcode.RETURN_VOID)),
            name = "sixRegisters", returnType = "V",
        ),
        ImmutableMethod(
            REJECTED_INVOKE, "callee", List(5) { ImmutableMethodParameter("I", emptySet(), null) }, "V",
            Modifier.PUBLIC or Modifier.STATIC, emptySet(), emptySet(),
            ImmutableMethodImplementation(
                5, listOf(ImmutableInstruction10x(Opcode.RETURN_VOID)), emptyList(), emptyList(),
            ),
        ),
        method(
            REJECTED_INVOKE, "", Modifier.PUBLIC or Modifier.STATIC, 1,
            listOf(ImmutableInstruction11n(Opcode.CONST_4, 0, 7), ImmutableInstruction11x(Opcode.RETURN, 0)),
            name = "answer",
        ),
    )

    private fun callerClass() = hostileClass(
        CALLER,
        method(
            CALLER, "", Modifier.PUBLIC or Modifier.STATIC, 1,
            listOf(
                ImmutableInstruction35c(
                    Opcode.INVOKE_STATIC, 0, 0, 0, 0, 0, 0,
                    ImmutableMethodReference(REJECTED_INVOKE, "answer", emptyList(), "I"),
                ),
                ImmutableInstruction11x(Opcode.MOVE_RESULT, 0),
                ImmutableInstruction11x(Opcode.RETURN, 0),
            ),
            name = "callsRejectedClass",
        ),
    )

    private fun healthyClass() = hostileClass(HEALTHY, runMethod(HEALTHY, "", 1, CONST_THEN_RETURN))

    private fun catchAll(start: Int, codeUnits: Int, handler: Int) =
        ImmutableTryBlock(start, codeUnits, listOf(ImmutableExceptionHandler(null, handler)))

    private fun rejection(reason: VerifierRejection.Reason, codeAddress: Int) = VerifierRejection(reason, codeAddress)

    private fun DexBackedDexFile.code(type: String, name: String): MethodImplementation =
        requireNotNull(classes.single { it.type == type }.methods.single { it.name == name }.implementation)

    /** A non-range invoke whose count declares six registers, which dexlib2's own 35c instruction refuses to hold. */
    private class SixRegisterInvoke(private val method: MethodReference) :
        ImmutableInstruction(Opcode.INVOKE_STATIC), Instruction35c {
        override fun getFormat(): Format = Format.Format35c
        override fun getRegisterCount(): Int = 6
        override fun getRegisterC(): Int = 0
        override fun getRegisterD(): Int = 1
        override fun getRegisterE(): Int = 2
        override fun getRegisterF(): Int = 3
        override fun getRegisterG(): Int = 4
        override fun getReference(): MethodReference = method
        override fun getReferenceType(): Int = ReferenceType.METHOD
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

        const val REJECTED_INVOKE = "Lhostile/Invoke;"
        const val CALLER = "Lhostile/Caller;"
        const val HEALTHY = "Lhostile/Healthy;"
        val CALLEE = ImmutableMethodReference(REJECTED_INVOKE, "callee", List(5) { "I" }, "V")

        /** `const/16 v0, 7` at 0 (two code units), then `return v0` at 2. */
        val CONST_THEN_RETURN: List<Instruction> = listOf(
            ImmutableInstruction21s(Opcode.CONST_16, 0, 7),
            ImmutableInstruction11x(Opcode.RETURN, 0),
        )
    }
}
