package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode

/** A DEX section whose item count or size an edit can exceed. */
enum class DexSection(val sectionName: String) {
    TYPE_IDS("type_ids"),
    PROTO_IDS("proto_ids"),
    FIELD_IDS("field_ids"),
    METHOD_IDS("method_ids"),
    TYPE_LIST("type_list"),
}

/**
 * The rule an open method edit breaks, one case per rule. Code addresses before layout are the edited body's
 * logical addresses; [describe] renders the reason for people.
 */
sealed interface MethodEditRejectionReason {
    data object StaleMethodRevision : MethodEditRejectionReason
    data object StaleProgramRevision : MethodEditRejectionReason

    data class InvalidCodeAddress(val codeAddress: Int) : MethodEditRejectionReason
    data class PayloadCodeAddress(val codeAddress: Int) : MethodEditRejectionReason
    data class ConflictingCommands(val codeAddress: Int) : MethodEditRejectionReason
    data class EmptyInstructionList(val codeAddress: Int) : MethodEditRejectionReason

    data object ForeignAnchor : MethodEditRejectionReason
    data object ForeignLogicalRegister : MethodEditRejectionReason
    data object ForeignStagedReference : MethodEditRejectionReason

    data class UnencodableInstruction(val opcode: Opcode) : MethodEditRejectionReason
    data class TemplateOpcodeMismatch(val opcode: Opcode) : MethodEditRejectionReason
    data class LogicalRegisterCountMismatch(
        val opcode: Opcode,
        val registerOperands: Int,
        val logicalRegisters: Int,
    ) : MethodEditRejectionReason
    data class BaselineRegisterOutOfRange(val register: Int) : MethodEditRejectionReason
    data class WideScratchPairOutsideReservation(val offset: Int) : MethodEditRejectionReason
    data class ReferenceOperandCountMismatch(
        val opcode: Opcode,
        val expected: Int,
        val actual: Int,
    ) : MethodEditRejectionReason
    data class ReferenceKindMismatch(val opcode: Opcode) : MethodEditRejectionReason
    data object UnstagedReference : MethodEditRejectionReason

    data class MalformedDescriptor(val descriptor: String) : MethodEditRejectionReason
    data class MalformedMemberName(val name: String) : MethodEditRejectionReason
    data class InvalidTypeOperand(val opcode: Opcode, val descriptor: String) : MethodEditRejectionReason
    data class CatchTypeNotClass(val descriptor: String) : MethodEditRejectionReason
    data class CatchTypeNotThrowable(val descriptor: String) : MethodEditRejectionReason

    data class MissingControlTarget(val codeAddress: Int) : MethodEditRejectionReason
    data class BranchTargetNotExecutable(val codeAddress: Int) : MethodEditRejectionReason
    data class WrongPayloadTarget(val codeAddress: Int) : MethodEditRejectionReason
    data class SwitchCasesMismatchPayload(val codeAddress: Int) : MethodEditRejectionReason
    data class SwitchCaseTargetNotExecutable(val codeAddress: Int, val key: Int) : MethodEditRejectionReason
    data class UnownedPayload(val codeAddress: Int) : MethodEditRejectionReason
    data object PayloadAtMethodEntry : MethodEditRejectionReason
    data class FallsIntoPayload(val codeAddress: Int) : MethodEditRejectionReason
    data class FallsIntoMoveException(val codeAddress: Int) : MethodEditRejectionReason
    data class BranchIntoMoveException(val codeAddress: Int) : MethodEditRejectionReason
    data object FallsPastMethodEnd : MethodEditRejectionReason
    data object NoExecutableCode : MethodEditRejectionReason

    data class InvalidArrayElementWidth(val codeAddress: Int, val elementWidth: Int) : MethodEditRejectionReason
    data class SwitchPayloadTooLarge(val codeAddress: Int, val elementCount: Int) : MethodEditRejectionReason
    data class PackedSwitchKeysNotConsecutive(val codeAddress: Int) : MethodEditRejectionReason
    data class SparseSwitchKeysNotSorted(val codeAddress: Int) : MethodEditRejectionReason

    data object ReversedTryRange : MethodEditRejectionReason
    data object EmptyTryRange : MethodEditRejectionReason
    data object TryRangeStartNotExecutable : MethodEditRejectionReason
    data object TryRangeWithoutHandler : MethodEditRejectionReason
    data object HandlerTargetNotExecutable : MethodEditRejectionReason
    data object CatchAllNotLast : MethodEditRejectionReason
    data object OverlappingTryRanges : MethodEditRejectionReason
    data class TooManyTryItems(val count: Int) : MethodEditRejectionReason
    data object HandlerTypeOutsideReferenceClosure : MethodEditRejectionReason
    data class HandlerListOffsetOverflow(val offset: Long) : MethodEditRejectionReason
    data class MoveExceptionOutsideHandler(val codeAddress: Int) : MethodEditRejectionReason

    data object DebugItemNotAtInstruction : MethodEditRejectionReason
    data class UnsupportedDebugItem(val debugItemType: Int) : MethodEditRejectionReason

    data class RegisterOverflow(val register: Int) : MethodEditRejectionReason
    data class RegisterCountReduced(val baselineRegisterCount: Int, val registerCount: Int) :
        MethodEditRejectionReason
    data class CodeSizeOverflow(val codeUnits: Long) : MethodEditRejectionReason
    data class OpcodeUnavailable(val opcode: Opcode, val apiLevel: Int) : MethodEditRejectionReason
    data class MisalignedPayload(val codeAddress: Int) : MethodEditRejectionReason
    data class BranchOffsetNotAtInstruction(val codeAddress: Int) : MethodEditRejectionReason
    data class ZeroBranchOffset(val codeAddress: Int) : MethodEditRejectionReason
    data class DexReferenceLimit(val section: DexSection) : MethodEditRejectionReason

    data class MoveResultWithoutResult(val codeAddress: Int) : MethodEditRejectionReason
    data class MoveResultTypeMismatch(val codeAddress: Int, val expected: Opcode) : MethodEditRejectionReason
    data class InvokeArgumentWordMismatch(
        val codeAddress: Int,
        val expectedWords: Int,
        val actualWords: Int,
    ) : MethodEditRejectionReason
    data class SplitWideArgument(val codeAddress: Int) : MethodEditRejectionReason
    data class ReturnOpcodeMismatch(val codeAddress: Int, val expected: Opcode) : MethodEditRejectionReason
    data class ReadBeforeAssignment(val codeAddress: Int, val register: Int) : MethodEditRejectionReason
    data class ReturnCategoryMismatch(val codeAddress: Int, val register: Int) : MethodEditRejectionReason

    fun describe(): String = when (this) {
        StaleMethodRevision -> "The method changed after this edit began"
        StaleProgramRevision -> "The program changed after the analysis this edit is based on"
        is InvalidCodeAddress -> "No instruction starts at code address $codeAddress"
        is PayloadCodeAddress -> "Code address $codeAddress holds payload data, which commands cannot address"
        is ConflictingCommands -> "Several replace or remove commands target code address $codeAddress"
        is EmptyInstructionList -> "The insertion or replacement at code address $codeAddress has no instructions"
        ForeignAnchor -> "A code anchor belongs to another transaction"
        ForeignLogicalRegister -> "A logical register belongs to another transaction"
        ForeignStagedReference -> "A staged DEX reference belongs to another transaction"
        is UnencodableInstruction -> "A ${opcode.name} template cannot be encoded"
        is TemplateOpcodeMismatch -> "${opcode.name} does not fit its instruction template kind"
        is LogicalRegisterCountMismatch ->
            "${opcode.name} has $registerOperands register operands but $logicalRegisters logical registers"
        is BaselineRegisterOutOfRange -> "Baseline register v$register is outside the method register file"
        is WideScratchPairOutsideReservation ->
            "The wide operand at scratch offset $offset extends past its reserved scratch range"
        is ReferenceOperandCountMismatch ->
            "${opcode.name} requires $expected reference operands, but $actual were supplied"
        is ReferenceKindMismatch -> "A DEX reference operand has the wrong kind for ${opcode.name}"
        UnstagedReference -> "The edit uses a reference that was not staged and is absent from the snapshot"
        is MalformedDescriptor -> "Malformed type descriptor: $descriptor"
        is MalformedMemberName -> "Malformed member name: $name"
        is InvalidTypeOperand -> "$descriptor is not a valid type operand for ${opcode.name}"
        is CatchTypeNotClass -> "A typed catch requires a class descriptor: $descriptor"
        is CatchTypeNotThrowable -> "A typed catch is not a Throwable type: $descriptor"
        is MissingControlTarget -> "The instruction at $codeAddress has no logical target"
        is BranchTargetNotExecutable -> "The branch at $codeAddress does not target executable code"
        is WrongPayloadTarget -> "The instruction at $codeAddress does not target its kind of payload"
        is SwitchCasesMismatchPayload -> "The switch at $codeAddress has cases that differ from its payload keys"
        is SwitchCaseTargetNotExecutable ->
            "Case $key of the switch at $codeAddress does not target executable code"
        is UnownedPayload -> "Payload data at $codeAddress has no owning instruction"
        PayloadAtMethodEntry -> "Method entry cannot be payload data"
        is FallsIntoPayload -> "Ordinary control flow falls into payload data at $codeAddress"
        is FallsIntoMoveException -> "Ordinary control flow falls into move-exception at $codeAddress"
        is BranchIntoMoveException -> "An ordinary branch enters move-exception at $codeAddress"
        FallsPastMethodEnd -> "Control flow falls past the method end"
        NoExecutableCode -> "A concrete method needs executable code"
        is InvalidArrayElementWidth -> "The array payload at $codeAddress has invalid element width $elementWidth"
        is SwitchPayloadTooLarge ->
            "The switch payload at $codeAddress has $elementCount elements; the limit is ${DexLimits.MAX_SWITCH_ELEMENTS}"
        is PackedSwitchKeysNotConsecutive -> "The packed-switch keys at $codeAddress are not consecutive"
        is SparseSwitchKeysNotSorted -> "The sparse-switch keys at $codeAddress are not unique and sorted"
        ReversedTryRange -> "A protected range ends before it starts"
        EmptyTryRange -> "A laid-out protected range is empty"
        TryRangeStartNotExecutable -> "A protected range starts outside executable code"
        TryRangeWithoutHandler -> "A protected range has no exception handler"
        HandlerTargetNotExecutable -> "An exception handler does not target executable code"
        CatchAllNotLast -> "A catch-all handler must be unique and last"
        OverlappingTryRanges -> "Distinct protected ranges overlap"
        is TooManyTryItems -> "The method has $count try items; the limit is ${DexLimits.MAX_TRY_ITEMS}"
        HandlerTypeOutsideReferenceClosure -> "An exception handler type is absent from the projected DEX closure"
        is HandlerListOffsetOverflow ->
            "Encoded exception-handler offset $offset exceeds the limit ${DexLimits.MAX_HANDLER_OFFSET}"
        is MoveExceptionOutsideHandler -> "move-exception at $codeAddress does not start an exception handler"
        DebugItemNotAtInstruction -> "Debug metadata does not target an executable boundary"
        is UnsupportedDebugItem -> "Unsupported debug item type $debugItemType"
        is RegisterOverflow -> "Register v$register does not fit the method register file"
        is RegisterCountReduced ->
            "An edit cannot reduce the register count from $baselineRegisterCount to $registerCount"
        is CodeSizeOverflow -> "The method needs $codeUnits code units"
        is OpcodeUnavailable -> "${opcode.name} is unavailable at output API $apiLevel"
        is MisalignedPayload -> "The payload at $codeAddress is not aligned to an even code-unit address"
        is BranchOffsetNotAtInstruction -> "The branch at $codeAddress does not target an instruction boundary"
        is ZeroBranchOffset -> "The branch at $codeAddress has a zero offset"
        is DexReferenceLimit -> "The program exceeds the ${section.sectionName} limit"
        is MoveResultWithoutResult -> "The move-result at $codeAddress does not follow a result-producing instruction"
        is MoveResultTypeMismatch -> "The move-result at $codeAddress must be ${expected.name}"
        is InvokeArgumentWordMismatch ->
            "The invocation at $codeAddress uses $actualWords register words for $expectedWords argument words"
        is SplitWideArgument -> "The invocation at $codeAddress passes a wide argument in non-adjacent registers"
        is ReturnOpcodeMismatch -> "The return at $codeAddress must be ${expected.name} for the method return type"
        is ReadBeforeAssignment -> "Register v$register is read at $codeAddress before assignment"
        is ReturnCategoryMismatch -> "The return at $codeAddress reads the wrong value category from v$register"
    }
}
