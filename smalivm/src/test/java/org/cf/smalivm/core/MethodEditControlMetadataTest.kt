package org.cf.smalivm.core

import java.lang.reflect.Modifier
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.collections.immutable.toPersistentMap
import com.android.tools.smali.dexlib2.DexFileFactory
import com.android.tools.smali.dexlib2.HiddenApiRestriction
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.Annotation
import com.android.tools.smali.dexlib2.immutable.ImmutableClassDef
import com.android.tools.smali.dexlib2.immutable.ImmutableDexFile
import com.android.tools.smali.dexlib2.immutable.ImmutableExceptionHandler
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodImplementation
import com.android.tools.smali.dexlib2.immutable.ImmutableMethodParameter
import com.android.tools.smali.dexlib2.immutable.ImmutableTryBlock
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEndLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableEpilogueBegin
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableLineNumber
import com.android.tools.smali.dexlib2.immutable.debug.ImmutablePrologueEnd
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableRestartLocal
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableSetSourceFile
import com.android.tools.smali.dexlib2.immutable.debug.ImmutableStartLocal
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableArrayPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction10x
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction21t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction31t
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutablePackedSwitchPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableSparseSwitchPayload
import com.android.tools.smali.dexlib2.immutable.instruction.ImmutableSwitchElement
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class MethodEditControlMetadataTest {
    @TempDir
    lateinit var root: Path
    private var fixtureSequence = 0

    @Test
    fun addressChangingEditsRoundTripAndExecuteEveryBaselineBranchAndPayloadFamily() {
        val index = index(CONTROL_SOURCE)
        val expected = mapOf(
            GOTO to 2,
            GOTO_16 to 2,
            GOTO_32 to 2,
            CONDITIONAL to 2,
            TWO_REGISTER_CONDITIONAL to 2,
            ARRAY to 7,
            PACKED to 3,
            SPARSE to 4,
        )
        val expectedControlOpcodes = mapOf(
            GOTO to Opcode.GOTO,
            GOTO_16 to Opcode.GOTO_16,
            GOTO_32 to Opcode.GOTO_32,
            CONDITIONAL to Opcode.IF_EQZ,
            TWO_REGISTER_CONDITIONAL to Opcode.IF_EQ,
            ARRAY to Opcode.FILL_ARRAY_DATA,
            PACKED to Opcode.PACKED_SWITCH,
            SPARSE to Opcode.SPARSE_SWITCH,
        )
        val expectedPayloadOpcodes = mapOf(
            ARRAY to Opcode.ARRAY_PAYLOAD,
            PACKED to Opcode.PACKED_SWITCH_PAYLOAD,
            SPARSE to Opcode.SPARSE_SWITCH_PAYLOAD,
        )

        for ((signature, resultValue) in expected) {
            val view = requireNotNull(index.methodView(signature))
            val controlAddress = view.instructionAddresses.first { address ->
                view.opcodeAt(address) in CONTROL_OPCODES
            }
            val insertionAddress = view.instructionAddresses.first { it > controlAddress }
            val edit = VmServices(index).methodEditor.beginEdit(view)
            edit.insertInstructionsBefore(
                insertionAddress,
                listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
            )

            val committed = edit.commit() as MethodEditCommitResult.Committed

            assertEquals(resultValue, executeInt(index, committed.editedMethodView))
            assertTrue(
                committed.editedMethodView.instructionAddresses.any {
                    committed.editedMethodView.opcodeAt(it) == expectedControlOpcodes.getValue(signature)
                },
            )
            expectedPayloadOpcodes[signature]?.let { payloadOpcode ->
                val payloadAddress = committed.editedMethodView.payloadAddresses.single()
                assertEquals(payloadOpcode, committed.editedMethodView.payloadAt(payloadAddress).opcode)
            }
        }

        val reloaded = reload(index)
        expected.forEach { (signature, resultValue) ->
            val view = requireNotNull(reloaded.methodView(signature))
            assertEquals(resultValue, executeInt(reloaded, view))
            assertTrue(view.instructionAddresses.any { view.opcodeAt(it) == expectedControlOpcodes.getValue(signature) })
            expectedPayloadOpcodes[signature]?.let { payloadOpcode ->
                assertEquals(payloadOpcode, view.payloadAt(view.payloadAddresses.single()).opcode)
            }
        }
    }

    @Test
    fun conditionalExpansionRoundTripsAndExecutesTheOriginalTarget() {
        val index = index(CONDITIONAL_EXPANSION_SOURCE)
        val view = requireNotNull(index.methodView(EXPANDED_CONDITIONAL))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val fallthroughAddress = view.instructionAddresses.first { view.opcodeAt(it) == Opcode.CONST_4 && it > 0 }
        edit.insertInstructionsBefore(
            fallthroughAddress,
            List(32_765) { InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP)) },
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        val opcodes = committed.editedMethodView.instructionAddresses.map(committed.editedMethodView::opcodeAt)

        assertTrue(Opcode.IF_NEZ in opcodes)
        assertTrue(Opcode.GOTO_32 in opcodes)
        assertEquals(2, executeInt(index, committed.editedMethodView))

        val reloaded = reload(index)
        assertEquals(2, executeInt(reloaded, requireNotNull(reloaded.methodView(EXPANDED_CONDITIONAL))))
    }

    @Test
    fun detachedBranchAndPayloadTemplatesRoundTripAndExecute() {
        val index = index(TEMPLATE_SOURCE)
        val editor = VmServices(index).methodEditor

        val branchView = requireNotNull(index.methodView(TEMPLATE_BRANCH))
        val branchEdit = editor.beginEdit(branchView)
        val returnTarget = branchEdit.pendingAnchorForCodeAddress(branchView.terminalInstructionAddresses.single())
        branchEdit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Branch(
                    ImmutableInstruction10t(Opcode.GOTO, 0),
                    returnTarget,
                ),
            ),
        )
        assertTrue(branchEdit.commit() is MethodEditCommitResult.Committed)

        val arrayView = requireNotNull(index.methodView(TEMPLATE_ARRAY))
        val arrayEdit = editor.beginEdit(arrayView)
        val arrayDeadAddress = opcodeAddress(arrayView, Opcode.NOP)
        val arrayPayload = arrayEdit.insertInstructionsBefore(
            arrayDeadAddress,
            listOf(InstructionTemplate.Payload(ImmutableArrayPayload(4, listOf(7)))),
        ).anchors.single()
        val arrayOwnerAddress = opcodeAddress(arrayView, Opcode.GOTO)
        arrayEdit.insertInstructionsBefore(
            arrayOwnerAddress,
            listOf(
                InstructionTemplate.FillArrayData(
                    ImmutableInstruction31t(Opcode.FILL_ARRAY_DATA, 0, 0),
                    arrayPayload,
                ),
            ),
        )
        val arrayResult = arrayEdit.commit() as MethodEditCommitResult.Committed
        assertEquals(7, executeInt(index, arrayResult.editedMethodView))

        insertSwitchTemplate(index, TEMPLATE_PACKED, packed = true, key = 0, expected = 5)
        insertSwitchTemplate(index, TEMPLATE_SPARSE, packed = false, key = 6, expected = 7)

        val reloaded = reload(index)
        assertTrue(VmServices(reloaded).newSession(requireNotNull(reloaded.methodView(TEMPLATE_BRANCH))).run().trusted() != null)
        assertEquals(7, executeInt(reloaded, requireNotNull(reloaded.methodView(TEMPLATE_ARRAY))))
        assertEquals(5, executeInt(reloaded, requireNotNull(reloaded.methodView(TEMPLATE_PACKED))))
        assertEquals(7, executeInt(reloaded, requireNotNull(reloaded.methodView(TEMPLATE_SPARSE))))
    }

    @Test
    fun sharedSwitchPayloadStaysSharedOnlyWhileOwnerRelativeEncodingsMatch() {
        val shared = sharedPayloadIndex(arrayPayload = false)
        val sharedView = requireNotNull(shared.methodView(SHARED_SWITCH))
        val keepEdit = VmServices(shared).methodEditor.beginEdit(sharedView)
        keepEdit.insertInstructionsBefore(
            0,
            listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
        )
        val kept = (keepEdit.commit() as MethodEditCommitResult.Committed).editedMethodView
        val keptOwners = kept.instructionAddresses.filter { kept.opcodeAt(it) == Opcode.PACKED_SWITCH }
        assertEquals(1, kept.payloadAddresses.size)
        assertEquals(kept.controlTargetAt(keptOwners[0]), kept.controlTargetAt(keptOwners[1]))

        val split = sharedPayloadIndex(arrayPayload = false)
        val splitView = requireNotNull(split.methodView(SHARED_SWITCH))
        val splitEdit = VmServices(split).methodEditor.beginEdit(splitView)
        splitEdit.insertInstructionsBefore(
            3,
            listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
        )
        val splitResult = (splitEdit.commit() as MethodEditCommitResult.Committed).editedMethodView
        val splitOwners = splitResult.instructionAddresses.filter { splitResult.opcodeAt(it) == Opcode.PACKED_SWITCH }

        assertEquals(2, splitResult.payloadAddresses.size)
        assertNotEquals(splitResult.controlTargetAt(splitOwners[0]), splitResult.controlTargetAt(splitOwners[1]))
        assertEquals(5, splitResult.switchCasesAt(splitOwners[0]).single().second)
        assertEquals(9, splitResult.switchCasesAt(splitOwners[1]).single().second)
        assertTrue(VmServices(split).newSession(splitResult).run().trusted() != null)

        val reloaded = reload(split)
        val reloadedView = requireNotNull(reloaded.methodView(SHARED_SWITCH))
        assertEquals(2, reloadedView.payloadAddresses.size)
        assertTrue(VmServices(reloaded).newSession(reloadedView).run().trusted() != null)
    }

    @Test
    fun validSharedArrayPayloadRemainsSharedAfterOwnerOffsetsChange() {
        val index = sharedPayloadIndex(arrayPayload = true)
        val view = requireNotNull(index.methodView(SHARED_ARRAY))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        edit.insertInstructionsBefore(
            3,
            listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
        )

        val edited = (edit.commit() as MethodEditCommitResult.Committed).editedMethodView
        val owners = edited.instructionAddresses.filter { edited.opcodeAt(it) == Opcode.FILL_ARRAY_DATA }

        assertEquals(1, edited.payloadAddresses.size)
        assertEquals(edited.controlTargetAt(owners[0]), edited.controlTargetAt(owners[1]))
        val reloaded = reload(index)
        assertEquals(1, requireNotNull(reloaded.methodView(SHARED_ARRAY)).payloadAddresses.size)
    }

    @Test
    fun allSevenDebugItemKindsTransferForInsertReplaceAndRemove() {
        val operations: List<(MethodEditTransaction) -> Unit> = listOf(
            { edit ->
                edit.insertInstructionsBefore(
                    0,
                    listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
                )
            },
            { edit ->
                edit.replaceInstruction(
                    0,
                    listOf(
                        InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP)),
                        InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP)),
                    ),
                )
            },
            { edit -> edit.removeInstruction(0) },
        )

        operations.forEach { operation ->
            val classIndex = index(DEBUG_BASE_SOURCE)
            val implementation = ImmutableMethodImplementation(
                1,
                listOf(
                    ImmutableInstruction10x(Opcode.NOP),
                    ImmutableInstruction10x(Opcode.RETURN_VOID),
                ),
                emptyList(),
                debugItemsAt(0),
            )
            installBody(classIndex, DEBUG_METHOD, MethodBody.import(implementation))
            val edit = VmServices(classIndex).methodEditor.beginEdit(requireNotNull(classIndex.methodView(DEBUG_METHOD)))
            operation(edit)

            val committed = edit.commit() as MethodEditCommitResult.Committed
            assertDebugItemsAtStart(committed.editedMethodView.methodBody)

            val reloaded = reload(classIndex)
            assertDebugItemsAtStart(requireNotNull(reloaded.methodView(DEBUG_METHOD)).methodBody)
        }
    }

    @Test
    fun inheritedTryPositionsMoveAndEmptyRangesAreRemovedWithoutPublicTryEditing() {
        val index = index(TRY_SOURCE)
        val editor = VmServices(index).methodEditor
        val preserveView = requireNotNull(index.methodView(TRY_PRESERVE))
        val preserve = editor.beginEdit(preserveView)
        preserve.insertInstructionsBefore(
            0,
            listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
        )

        val preserved = (preserve.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertEquals(2, preserved.tryBlocks.single().endAddress)
        assertTrue(VmServices(index).newSession(preserved).run().trusted() != null)

        val removeView = requireNotNull(index.methodView(TRY_REMOVE))
        val remove = editor.beginEdit(removeView)
        val moveExceptionAddress = removeView.instructionAddresses.single {
            removeView.opcodeAt(it) == Opcode.MOVE_EXCEPTION
        }
        remove.removeInstructions(listOf(0, moveExceptionAddress))
        val removed = (remove.commit() as MethodEditCommitResult.Committed).editedMethodView
        assertTrue(removed.tryBlocks.isEmpty())

        val reloaded = reload(index)
        assertEquals(2, requireNotNull(reloaded.methodView(TRY_PRESERVE)).tryBlocks.single().endAddress)
        assertTrue(requireNotNull(reloaded.methodView(TRY_REMOVE)).tryBlocks.isEmpty())
    }

    @Test
    fun malformedInheritedTryRangesRejectBeforeDraftConstruction() {
        val instructions = listOf(
            ImmutableInstruction10x(Opcode.NOP),
            ImmutableInstruction10x(Opcode.NOP),
            ImmutableInstruction10x(Opcode.RETURN_VOID),
            com.android.tools.smali.dexlib2.immutable.instruction.ImmutableInstruction11x(Opcode.MOVE_EXCEPTION, 0),
            ImmutableInstruction10x(Opcode.RETURN_VOID),
        )
        val malformedImplementations = listOf(
            ImmutableMethodImplementation(
                1,
                instructions,
                listOf(
                    ImmutableTryBlock(0, 2, listOf(ImmutableExceptionHandler(null, 3))),
                    ImmutableTryBlock(1, 1, listOf(ImmutableExceptionHandler(null, 3))),
                ),
                emptyList(),
            ),
            ImmutableMethodImplementation(
                1,
                instructions,
                listOf(
                    ImmutableTryBlock(0, 2, listOf(ImmutableExceptionHandler(null, 3))),
                    ImmutableTryBlock(0, 2, listOf(ImmutableExceptionHandler("Ljava/lang/Exception;", 3))),
                ),
                emptyList(),
            ),
        )

        // dexlib2 try blocks are (start, code-unit count): [0, 2) and [1, 2) overlap; equal ranges merge handlers.
        val expectedReasons = listOf(MethodEditRejectionReason.OverlappingTryRanges, MethodEditRejectionReason.CatchAllNotLast)
        for ((implementation, expected) in malformedImplementations.zip(expectedReasons)) {
            val index = index(MALFORMED_BASE_SOURCE)
            installBody(index, MALFORMED_TRY, MethodBody.import(implementation))
            val snapshot = index.currentSnapshot
            val edit = VmServices(index).methodEditor.beginEdit(requireNotNull(index.methodView(MALFORMED_TRY)))
            edit.insertInstructionsBefore(
                0,
                listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
            )

            val result = edit.commit() as MethodEditCommitResult.Rejected

            assertEquals(expected, result.reason)
            assertSame(snapshot, index.currentSnapshot)
        }
    }

    @Test
    fun finalPayloadOwnerRemovalDeletesThePayloadAndOrdinaryFlowIntoPayloadRejects() {
        val index = index(CONTROL_SOURCE)
        val view = requireNotNull(index.methodView(ARRAY))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        edit.removeInstruction(opcodeAddress(view, Opcode.FILL_ARRAY_DATA))
        val committed = edit.commit() as MethodEditCommitResult.Committed
        assertTrue(committed.editedMethodView.payloadAddresses.isEmpty())
        assertEquals(0, executeInt(index, committed.editedMethodView))

        val flowIndex = index(PAYLOAD_FLOW_SOURCE)
        val flowView = requireNotNull(flowIndex.methodView(PAYLOAD_FLOW))
        val flowEdit = VmServices(flowIndex).methodEditor.beginEdit(flowView)
        val returnAddress = flowView.terminalInstructionAddresses.single()
        val payload = flowEdit.insertInstructionsBefore(
            returnAddress,
            listOf(
                InstructionTemplate.Payload(
                    ImmutablePackedSwitchPayload(listOf(ImmutableSwitchElement(0, 0))),
                ),
            ),
        ).anchors.single()
        // The case loops back to the switch, so the only fault is the nop falling into the payload.
        val caseTarget = flowEdit.pendingAnchorForCodeAddress(0)
        flowEdit.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Switch(
                    ImmutableInstruction31t(Opcode.PACKED_SWITCH, 0, 0),
                    payload,
                    listOf(InstructionTemplate.Switch.Case(0, caseTarget)),
                ),
            ),
        )

        val rejected = flowEdit.commit() as MethodEditCommitResult.Rejected
        // packed-switch occupies 3 code units and nop 1, so the payload starts at logical address 4.
        assertEquals(MethodEditRejectionReason.FallsIntoPayload(4), rejected.reason)
    }

    @Test
    fun payloadAtMethodEntryAndZeroOffsetConditionalRejectWithoutPublication() {
        val index = index(TEMPLATE_SOURCE)
        val editor = VmServices(index).methodEditor
        val arrayView = requireNotNull(index.methodView(TEMPLATE_ARRAY))
        val payloadEntry = editor.beginEdit(arrayView)
        val payload = payloadEntry.insertInstructionsBefore(
            0,
            listOf(InstructionTemplate.Payload(ImmutableArrayPayload(4, listOf(7)))),
        ).anchors.single()
        payloadEntry.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.FillArrayData(
                    ImmutableInstruction31t(Opcode.FILL_ARRAY_DATA, 0, 0),
                    payload,
                ),
            ),
        )

        val payloadResult = payloadEntry.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.PayloadAtMethodEntry, payloadResult.reason)

        val branchView = requireNotNull(index.methodView(TEMPLATE_PACKED))
        val selfBranch = editor.beginEdit(branchView)
        val entry = selfBranch.pendingAnchorForCodeAddress(0)
        selfBranch.insertInstructionsBefore(
            0,
            listOf(
                InstructionTemplate.Branch(
                    ImmutableInstruction21t(Opcode.IF_EQZ, 0, 0),
                    entry,
                ),
            ),
        )

        val branchResult = selfBranch.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.ZeroBranchOffset(0), branchResult.reason)
    }

    @Test
    fun emptySwitchTemplateCommitsAndAlwaysFallsThrough() {
        val index = index(TEMPLATE_SOURCE)
        val view = requireNotNull(index.methodView(TEMPLATE_PACKED))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val deadAddress = opcodeAddress(view, Opcode.NOP)
        val payload = edit.insertInstructionsBefore(
            deadAddress,
            listOf(InstructionTemplate.Payload(ImmutablePackedSwitchPayload(emptyList()))),
        ).anchors.single()
        edit.insertInstructionsBefore(
            opcodeAddress(view, Opcode.GOTO),
            listOf(
                InstructionTemplate.Switch(
                    ImmutableInstruction31t(Opcode.PACKED_SWITCH, 0, 0),
                    payload,
                    emptyList(),
                ),
            ),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed

        assertEquals(0, executeInt(index, committed.editedMethodView))
    }

    @Test
    fun byteIdenticalControlFlowBodyIsUnchanged() {
        val index = index(CONTROL_SOURCE)
        val snapshot = index.currentSnapshot
        val view = requireNotNull(index.methodView(GOTO))
        val returnAddress = view.terminalInstructionAddresses.single()
        val edit = VmServices(index).methodEditor.beginEdit(view)
        edit.replaceInstruction(returnAddress, listOf(InstructionTemplate.Encoded(view.instructionAt(returnAddress))))

        val result = edit.commit()

        assertSame(MethodEditCommitResult.Unchanged, result)
        assertSame(snapshot, index.currentSnapshot)
    }

    @Test
    fun registerValidationUsesControlFlowInsteadOfFileOrder() {
        val validIndex = index(CFG_VALID_SOURCE)
        val validView = requireNotNull(validIndex.methodView(CFG_VALID))
        val validEdit = VmServices(validIndex).methodEditor.beginEdit(validView)
        validEdit.insertInstructionsBefore(0, listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))))

        val committed = validEdit.commit() as MethodEditCommitResult.Committed

        assertEquals(1, executeInt(validIndex, committed.editedMethodView))

        val invalidIndex = index(CFG_INVALID_SOURCE)
        val snapshot = invalidIndex.currentSnapshot
        val invalidEdit = VmServices(invalidIndex).methodEditor.beginEdit(
            requireNotNull(invalidIndex.methodView(CFG_INVALID)),
        )
        invalidEdit.removeInstruction(0)

        val rejected = invalidEdit.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.ReadBeforeAssignment(codeAddress = 0, register = 0), rejected.reason)
        assertSame(snapshot, invalidIndex.currentSnapshot)
    }

    @Test
    fun gotoWideningIsMonotonicInBothDirectionsAndSurvivesReload() {
        val index = index(GOTO_WIDEN_SOURCE)
        for (signature in listOf(FORWARD_WIDEN, BACKWARD_WIDEN)) {
            val view = requireNotNull(index.methodView(signature))
            val edit = VmServices(index).methodEditor.beginEdit(view)
            edit.insertInstructionsBefore(
                1,
                List(32_768) { InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP)) },
            )

            val committed = edit.commit() as MethodEditCommitResult.Committed

            assertEquals(Opcode.GOTO_32, committed.editedMethodView.opcodeAt(opcodeAddress(committed.editedMethodView, Opcode.GOTO_32)))
        }

        val reloaded = reload(index)
        for (signature in listOf(FORWARD_WIDEN, BACKWARD_WIDEN)) {
            val view = requireNotNull(reloaded.methodView(signature))
            assertTrue(view.instructionAddresses.any { view.opcodeAt(it) == Opcode.GOTO_32 })
        }
    }

    @Test
    fun incomingLabelsTransferToInsertReplacementAndNextSurvivor() {
        val insertIndex = index(LABEL_SOURCE)
        val insertView = requireNotNull(insertIndex.methodView(LABEL_METHOD))
        val insertTarget = requireNotNull(insertView.controlTargetAt(opcodeAddress(insertView, Opcode.GOTO)))
        val insert = VmServices(insertIndex).methodEditor.beginEdit(insertView)
        val inserted = insert.insertInstructionsBefore(
            insertTarget,
            listOf(InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP))),
        ).anchors.single()
        val insertedResult = insert.commit() as MethodEditCommitResult.Committed
        val insertedGoto = opcodeAddress(insertedResult.editedMethodView, Opcode.GOTO)
        assertEquals(
            insertedResult.codeAddressesByPendingAnchor.getValue(inserted),
            insertedResult.editedMethodView.controlTargetAt(insertedGoto),
        )

        val replaceIndex = index(LABEL_SOURCE)
        val replaceView = requireNotNull(replaceIndex.methodView(LABEL_METHOD))
        val replaceTarget = requireNotNull(replaceView.controlTargetAt(opcodeAddress(replaceView, Opcode.GOTO)))
        val replace = VmServices(replaceIndex).methodEditor.beginEdit(replaceView)
        val replaceAnchor = replace.pendingAnchorForCodeAddress(replaceTarget)
        replace.replaceInstruction(
            replaceTarget,
            listOf(
                InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP)),
                InstructionTemplate.Encoded(ImmutableInstruction10x(Opcode.NOP)),
            ),
        )
        val replaced = replace.commit() as MethodEditCommitResult.Committed
        assertEquals(
            replaced.codeAddressesByPendingAnchor.getValue(replaceAnchor),
            replaced.editedMethodView.controlTargetAt(opcodeAddress(replaced.editedMethodView, Opcode.GOTO)),
        )

        val removeIndex = index(LABEL_SOURCE)
        val removeView = requireNotNull(removeIndex.methodView(LABEL_METHOD))
        val removeTarget = requireNotNull(removeView.controlTargetAt(opcodeAddress(removeView, Opcode.GOTO)))
        val remove = VmServices(removeIndex).methodEditor.beginEdit(removeView)
        val removeAnchor = remove.pendingAnchorForCodeAddress(removeTarget)
        remove.removeInstruction(removeTarget)
        val removed = remove.commit() as MethodEditCommitResult.Committed
        assertEquals(
            removed.codeAddressesByPendingAnchor.getValue(removeAnchor),
            removed.editedMethodView.controlTargetAt(opcodeAddress(removed.editedMethodView, Opcode.GOTO)),
        )
        assertEquals(Opcode.CONST_4, removed.editedMethodView.opcodeAt(removed.codeAddressesByPendingAnchor.getValue(removeAnchor)))
    }

    @Test
    fun foreignLogicalTargetsRejectBeforeDraftConstruction() {
        val index = index(LABEL_SOURCE)
        val editor = VmServices(index).methodEditor
        val view = requireNotNull(index.methodView(LABEL_METHOD))
        val first = editor.beginEdit(view)
        val foreignTarget = first.pendingAnchorForCodeAddress(view.terminalInstructionAddresses.single())
        val second = editor.beginEdit(view)
        second.insertInstructionsBefore(
            0,
            listOf(InstructionTemplate.Branch(ImmutableInstruction10t(Opcode.GOTO, 0), foreignTarget)),
        )

        val result = second.commit() as MethodEditCommitResult.Rejected

        assertEquals(MethodEditRejectionReason.ForeignAnchor, result.reason)
        first.discard()
    }

    private fun debugItemsAt(codeAddress: Int) = listOf(
        ImmutableLineNumber(codeAddress, 17),
        ImmutableStartLocal(codeAddress, 0, "value", "I", "TI;"),
        ImmutableEndLocal(codeAddress, 0, "value", "I", "TI;"),
        ImmutableRestartLocal(codeAddress, 0, "value", "I", "TI;"),
        ImmutablePrologueEnd(codeAddress),
        ImmutableEpilogueBegin(codeAddress),
        ImmutableSetSourceFile(codeAddress, "task56.smali"),
    )

    private fun assertDebugItemsAtStart(body: MethodBody) {
        val items = body.implementation.debugItems.toList()
        assertEquals(7, items.size)
        assertTrue(items.all { it.codeAddress == 0 })
        assertEquals(
            setOf(
                ImmutableLineNumber::class,
                ImmutableStartLocal::class,
                ImmutableEndLocal::class,
                ImmutableRestartLocal::class,
                ImmutablePrologueEnd::class,
                ImmutableEpilogueBegin::class,
                ImmutableSetSourceFile::class,
            ),
            items.map { it::class }.toSet(),
        )
    }

    private fun installBody(index: ClassIndex, methodSignature: String, body: MethodBody) {
        val snapshot = index.currentSnapshot
        val method = requireNotNull(snapshot.applicationMethod(methodSignature))
        val editedMethod = ApplicationMethodDefinition.withBody(
            method,
            body,
            index.programWorkspace.newApplicationMethodRevision(method.methodSignature),
        )
        val definingClass = method.methodSignature.definingClass.descriptor
        val editedClass = requireNotNull(snapshot.applicationClass(definingClass)).withMethod(editedMethod)
        val candidate = ProgramSnapshot.candidate(
            snapshot,
            index.programWorkspace.newProgramRevision(),
            snapshot.applicationClasses
                .mapValues { (type, definition) -> if (type == definingClass) editedClass else definition }
                .toPersistentMap(),
        )
        index.programWorkspace.publish(candidate)
    }

    private fun insertSwitchTemplate(
        index: ClassIndex,
        signature: String,
        packed: Boolean,
        key: Int,
        expected: Int,
    ) {
        val view = requireNotNull(index.methodView(signature))
        val edit = VmServices(index).methodEditor.beginEdit(view)
        val deadAddress = opcodeAddress(view, Opcode.NOP)
        val payloadInstruction = if (packed) {
            ImmutablePackedSwitchPayload(listOf(ImmutableSwitchElement(key, 0)))
        } else {
            ImmutableSparseSwitchPayload(listOf(ImmutableSwitchElement(key, 0)))
        }
        val payload = edit.insertInstructionsBefore(
            deadAddress,
            listOf(InstructionTemplate.Payload(payloadInstruction)),
        ).anchors.single()
        val caseAddress = view.instructionAddresses
            .filter { view.opcodeAt(it) == Opcode.CONST_4 }
            .last()
        val caseTarget = edit.pendingAnchorForCodeAddress(caseAddress)
        val ownerAddress = opcodeAddress(view, Opcode.GOTO)
        val ownerOpcode = if (packed) Opcode.PACKED_SWITCH else Opcode.SPARSE_SWITCH
        edit.insertInstructionsBefore(
            ownerAddress,
            listOf(
                InstructionTemplate.Switch(
                    ImmutableInstruction31t(ownerOpcode, 0, 0),
                    payload,
                    listOf(InstructionTemplate.Switch.Case(key, caseTarget)),
                ),
            ),
        )

        val committed = edit.commit() as MethodEditCommitResult.Committed
        assertEquals(expected, executeInt(index, committed.editedMethodView))
    }

    private fun sharedPayloadIndex(arrayPayload: Boolean): ClassIndex {
        val implementation = if (arrayPayload) {
            ImmutableMethodImplementation(
                2,
                listOf(
                    ImmutableInstruction31t(Opcode.FILL_ARRAY_DATA, 0, 8),
                    ImmutableInstruction31t(Opcode.FILL_ARRAY_DATA, 1, 5),
                    ImmutableInstruction10x(Opcode.RETURN_VOID),
                    ImmutableInstruction10x(Opcode.NOP),
                    ImmutableArrayPayload(4, listOf(7)),
                ),
                emptyList(),
                emptyList(),
            )
        } else {
            ImmutableMethodImplementation(
                1,
                listOf(
                    ImmutableInstruction31t(Opcode.PACKED_SWITCH, 0, 10),
                    ImmutableInstruction10t(Opcode.GOTO, 5),
                    ImmutableInstruction31t(Opcode.PACKED_SWITCH, 0, 6),
                    ImmutableInstruction10t(Opcode.GOTO, 1),
                    ImmutableInstruction10x(Opcode.RETURN_VOID),
                    ImmutableInstruction10x(Opcode.NOP),
                    ImmutablePackedSwitchPayload(listOf(ImmutableSwitchElement(0, 4))),
                ),
                emptyList(),
                emptyList(),
            )
        }
        val signature = if (arrayPayload) "run([I[I)V" else "run(I)V"
        val type = if (arrayPayload) "Ltask56/SharedArray;" else "Ltask56/SharedSwitch;"
        return dexIndex(type, signature, implementation)
    }

    private fun dexIndex(type: String, signature: String, implementation: ImmutableMethodImplementation): ClassIndex {
        val name = signature.substringBefore('(')
        val parameters = signature.substringAfter('(').substringBefore(')').let(::parseParameterTypes)
        val returnType = signature.substringAfter(')')
        val method = ImmutableMethod(
            type,
            name,
            parameters.map { ImmutableMethodParameter(it, emptySet<Annotation>(), null) },
            returnType,
            Modifier.PUBLIC or Modifier.STATIC,
            emptySet<Annotation>(),
            emptySet<HiddenApiRestriction>(),
            implementation,
        )
        val definition = ImmutableClassDef(
            type,
            Modifier.PUBLIC,
            "Ljava/lang/Object;",
            emptyList(),
            null,
            emptySet(),
            emptyList(),
            listOf(method),
        )
        val dexFile = root.resolve("fixture-${fixtureSequence++}.dex")
        DexFileFactory.writeDexFile(dexFile.toString(), ImmutableDexFile(Opcodes.forApi(20), listOf(definition)))
        return ClassIndex.open(dexFile.toFile(), 20)
    }

    private fun parseParameterTypes(descriptor: String): List<String> {
        val result = mutableListOf<String>()
        var index = 0
        while (index < descriptor.length) {
            val start = index
            while (descriptor[index] == '[') index++
            index = if (descriptor[index] == 'L') descriptor.indexOf(';', index) + 1 else index + 1
            result += descriptor.substring(start, index)
        }
        return result
    }

    private fun index(source: String): ClassIndex {
        val directory = Files.createDirectory(root.resolve("source-${fixtureSequence++}"))
        return classIndexFromSource(directory, source)
    }

    private fun reload(index: ClassIndex): ClassIndex {
        val snapshot = index.currentSnapshot
        val dexFile = root.resolve("round-trip-${fixtureSequence++}.dex")
        Files.write(dexFile, ProgramDexWriter().writeToBytes(snapshot))
        return ClassIndex.open(dexFile.toFile(), snapshot.outputApiPolicy.apiLevel)
    }

    private fun opcodeAddress(view: MethodView, opcode: Opcode): Int =
        view.instructionAddresses.single { view.opcodeAt(it) == opcode }

    private fun executeInt(index: ClassIndex, view: MethodView): Int {
        val value = VmServices(index).newSession(view).run().trusted()!!.terminatingConsensus(0) as Value.Known
        return value.raw as Int
    }

    private companion object {
        val CONTROL_OPCODES = setOf(
            Opcode.GOTO,
            Opcode.GOTO_16,
            Opcode.GOTO_32,
            Opcode.IF_EQZ,
            Opcode.IF_EQ,
            Opcode.FILL_ARRAY_DATA,
            Opcode.PACKED_SWITCH,
            Opcode.SPARSE_SWITCH,
        )
        const val GOTO = "Ltask56/Control;->gotoShort()I"
        const val GOTO_16 = "Ltask56/Control;->goto16()I"
        const val GOTO_32 = "Ltask56/Control;->goto32()I"
        const val CONDITIONAL = "Ltask56/Control;->conditional()I"
        const val TWO_REGISTER_CONDITIONAL = "Ltask56/Control;->twoRegisterConditional()I"
        const val ARRAY = "Ltask56/Control;->arrayPayload()I"
        const val PACKED = "Ltask56/Control;->packedPayload()I"
        const val SPARSE = "Ltask56/Control;->sparsePayload()I"
        const val EXPANDED_CONDITIONAL = "Ltask56/Expand;->run()I"
        const val TEMPLATE_BRANCH = "Ltask56/Templates;->branch()V"
        const val TEMPLATE_ARRAY = "Ltask56/Templates;->arrayPayload()I"
        const val TEMPLATE_PACKED = "Ltask56/Templates;->packedPayload()I"
        const val TEMPLATE_SPARSE = "Ltask56/Templates;->sparsePayload()I"
        const val SHARED_SWITCH = "Ltask56/SharedSwitch;->run(I)V"
        const val SHARED_ARRAY = "Ltask56/SharedArray;->run([I[I)V"
        const val TRY_PRESERVE = "Ltask56/Try;->preserve()V"
        const val TRY_REMOVE = "Ltask56/Try;->remove()V"
        const val MALFORMED_TRY = "Ltask56/Malformed;->run()V"
        const val PAYLOAD_FLOW = "Ltask56/PayloadFlow;->run(I)V"
        const val DEBUG_METHOD = "Ltask56/Debug;->run()V"
        const val FORWARD_WIDEN = "Ltask56/Widen;->forward()V"
        const val BACKWARD_WIDEN = "Ltask56/Widen;->backward()V"
        const val LABEL_METHOD = "Ltask56/Label;->run()I"

        const val CONTROL_SOURCE = """
            .class public Ltask56/Control;
            .super Ljava/lang/Object;

            .method public static gotoShort()I
                .registers 1
                goto :target
                const/4 v0, 0x1
                :target
                const/4 v0, 0x2
                return v0
            .end method

            .method public static goto16()I
                .registers 1
                goto/16 :target
                const/4 v0, 0x1
                :target
                const/4 v0, 0x2
                return v0
            .end method

            .method public static goto32()I
                .registers 1
                goto/32 :target
                const/4 v0, 0x1
                :target
                const/4 v0, 0x2
                return v0
            .end method

            .method public static conditional()I
                .registers 1
                const/4 v0, 0x0
                if-eqz v0, :target
                const/4 v0, 0x1
                return v0
                :target
                const/4 v0, 0x2
                return v0
            .end method

            .method public static twoRegisterConditional()I
                .registers 2
                const/4 v0, 0x0
                const/4 v1, 0x0
                if-eq v0, v1, :target
                const/4 v0, 0x1
                return v0
                :target
                const/4 v0, 0x2
                return v0
            .end method

            .method public static arrayPayload()I
                .registers 2
                const/4 v0, 0x1
                new-array v0, v0, [I
                fill-array-data v0, :data
                const/4 v1, 0x0
                aget v0, v0, v1
                return v0
                :data
                .array-data 4
                    0x7
                .end array-data
            .end method

            .method public static packedPayload()I
                .registers 1
                const/4 v0, 0x0
                packed-switch v0, :data
                const/4 v0, 0x1
                return v0
                :case
                const/4 v0, 0x3
                return v0
                :data
                .packed-switch 0x0
                    :case
                .end packed-switch
            .end method

            .method public static sparsePayload()I
                .registers 1
                const/4 v0, 0x7
                sparse-switch v0, :data
                const/4 v0, 0x1
                return v0
                :case
                const/4 v0, 0x4
                return v0
                :data
                .sparse-switch
                    0x7 -> :case
                .end sparse-switch
            .end method
        """

        const val CONDITIONAL_EXPANSION_SOURCE = """
            .class public Ltask56/Expand;
            .super Ljava/lang/Object;

            .method public static run()I
                .registers 1
                const/4 v0, 0x0
                if-eqz v0, :target
                const/4 v0, 0x1
                return v0
                :target
                const/4 v0, 0x2
                return v0
            .end method
        """

        const val TEMPLATE_SOURCE = """
            .class public Ltask56/Templates;
            .super Ljava/lang/Object;

            .method public static branch()V
                .registers 0
                nop
                return-void
            .end method

            .method public static arrayPayload()I
                .registers 2
                const/4 v0, 0x1
                new-array v0, v0, [I
                goto :after_data
                nop
                :after_data
                const/4 v1, 0x0
                aget v0, v0, v1
                return v0
            .end method

            .method public static packedPayload()I
                .registers 1
                const/4 v0, 0x0
                goto :done
                nop
                const/4 v0, 0x5
                :done
                return v0
            .end method

            .method public static sparsePayload()I
                .registers 1
                const/4 v0, 0x6
                goto :done
                nop
                const/4 v0, 0x7
                :done
                return v0
            .end method
        """

        const val TRY_SOURCE = """
            .class public Ltask56/Try;
            .super Ljava/lang/Object;

            .method public static preserve()V
                .registers 1
                :try_start
                nop
                :try_end
                return-void
                :handler
                move-exception v0
                return-void
                .catchall {:try_start .. :try_end} :handler
            .end method

            .method public static remove()V
                .registers 1
                :try_start
                nop
                :try_end
                return-void
                :handler
                move-exception v0
                return-void
                .catchall {:try_start .. :try_end} :handler
            .end method
        """

        const val MALFORMED_BASE_SOURCE = """
            .class public Ltask56/Malformed;
            .super Ljava/lang/Object;

            .method public static run()V
                .registers 1
                return-void
            .end method
        """

        const val PAYLOAD_FLOW_SOURCE = """
            .class public Ltask56/PayloadFlow;
            .super Ljava/lang/Object;

            .method public static run(I)V
                .registers 1
                nop
                return-void
            .end method
        """

        const val CFG_VALID = "Ltask56/CfgValid;->run()I"
        const val CFG_VALID_SOURCE = """
            .class public Ltask56/CfgValid;
            .super Ljava/lang/Object;

            .method public static run()I
                .registers 2
                goto :init
                :read
                move v1, v0
                return v1
                :init
                const/4 v0, 0x1
                goto :read
            .end method
        """

        const val CFG_INVALID = "Ltask56/CfgInvalid;->run()I"
        const val CFG_INVALID_SOURCE = """
            .class public Ltask56/CfgInvalid;
            .super Ljava/lang/Object;

            .method public static run()I
                .registers 1
                const/4 v0, 0x0
                if-eqz v0, :target
                const/4 v0, 0x1
                return v0
                :target
                const/4 v0, 0x2
                return v0
            .end method
        """

        const val DEBUG_BASE_SOURCE = """
            .class public Ltask56/Debug;
            .super Ljava/lang/Object;

            .method public static run()V
                .registers 1
                nop
                return-void
            .end method
        """

        const val GOTO_WIDEN_SOURCE = """
            .class public Ltask56/Widen;
            .super Ljava/lang/Object;

            .method public static forward()V
                .registers 0
                goto :target
                nop
                :target
                return-void
            .end method

            .method public static backward()V
                .registers 0
                :target
                nop
                goto :target
                return-void
            .end method
        """

        const val LABEL_SOURCE = """
            .class public Ltask56/Label;
            .super Ljava/lang/Object;

            .method public static run()I
                .registers 1
                goto :target
                const/4 v0, 0x1
                :target
                nop
                const/4 v0, 0x2
                return v0
            .end method
        """
    }
}
