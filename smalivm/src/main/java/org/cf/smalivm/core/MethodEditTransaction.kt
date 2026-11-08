package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.reference.TypeReference
import org.cf.smalivm.core.MethodEditRejectionReason.*

/**
 * One pending method edit batch. Every handle it hands out (anchors, logical registers, staged references) is
 * recorded in its open state, so membership in that state is what makes a handle belong to this batch.
 */
class MethodEditTransaction internal constructor(
    workspace: ProgramWorkspace,
    methodView: MethodView,
    revisionGuard: MethodEditRevisionGuard,
) {
    private sealed interface EditCommand {
        val codeAddress: Int
        val sequence: Int

        data class Insert(
            override val codeAddress: Int,
            val templates: List<InstructionTemplate>,
            val anchors: List<PendingCodeAnchor>,
            override val sequence: Int,
        ) : EditCommand

        data class Replace(
            override val codeAddress: Int,
            val templates: List<InstructionTemplate>,
            override val sequence: Int,
        ) : EditCommand

        data class Remove(
            override val codeAddress: Int,
            override val sequence: Int,
        ) : EditCommand
    }

    /** Commands keyed by baseline code address. */
    private data class IndexedEdits(
        val insertions: Map<Int, List<DraftInsertion>>,
        val replacements: Map<Int, List<DraftInstructionTemplate>>,
        val removals: Set<Int>,
        val anchors: Map<LogicalCodePosition, List<PendingCodeAnchor>>,
    )

    private sealed interface Checked<out T> {
        data class Accepted<T>(val value: T) : Checked<T>
        data class Rejected(val reason: MethodEditRejectionReason) : Checked<Nothing>
    }

    private sealed interface StagedReferenceKey {
        data class Type(val descriptor: String) : StagedReferenceKey
        data class StringValue(val value: String) : StagedReferenceKey
        data class Field(val definingClass: String, val name: String, val type: String) : StagedReferenceKey
        data class Method(
            val definingClass: String,
            val name: String,
            val parameterTypes: List<String>,
            val returnType: String,
        ) : StagedReferenceKey
        data class Prototype(val parameterTypes: List<String>, val returnType: String) : StagedReferenceKey
    }

    private class OpenState(
        val workspace: ProgramWorkspace,
        val revisionGuard: MethodEditRevisionGuard,
        val baselineBody: MethodBody,
        val instructionAddresses: Set<Int>,
        val payloadAddresses: Set<Int>,
    ) {
        val commands = ArrayList<EditCommand>()
        val anchorRequests = ArrayList<Pair<Int, PendingCodeAnchor>>()
        val logicalRegisters = HashSet<LogicalRegister>()
        val stagedReferences = LinkedHashMap<StagedReferenceKey, StagedDexReference>()
        val stagedReferenceHandles = HashSet<StagedDexReference>()
        var submittedTryRegions: List<TryRegionSpec>? = null
        var nextCommandSequence = 0
        var scratchWordCount = 0
    }

    private var openState: OpenState? = OpenState(
        workspace,
        revisionGuard,
        methodView.methodBody,
        methodView.instructionAddresses.toSet(),
        methodView.payloadAddresses.toSet(),
    )

    /** False once [commit] or [discard] has run; every other operation then throws. */
    val isOpen: Boolean get() = openState != null

    fun pendingAnchorForCodeAddress(codeAddress: Int): PendingCodeAnchor {
        val anchor = PendingCodeAnchor()
        open().anchorRequests.add(codeAddress to anchor)
        return anchor
    }

    fun logicalRegister(register: Int): LogicalRegister =
        LogicalRegister(LogicalRegister.Allocation.Baseline(register)).also { open().logicalRegisters.add(it) }

    fun reserveScratchRegisters(wordCount: Int): ScratchRegisterRange {
        val open = open()
        require(wordCount in 1..DexLimits.MAX_REGISTER_COUNT) {
            "Scratch-register word count must be in 1..${DexLimits.MAX_REGISTER_COUNT}: $wordCount"
        }
        check(open.scratchWordCount <= Int.MAX_VALUE - wordCount) { "Scratch-register reservation count overflow" }
        val firstOffset = open.scratchWordCount
        open.scratchWordCount += wordCount
        val registers = List(wordCount) { offset ->
            LogicalRegister(LogicalRegister.Allocation.Scratch(firstOffset + offset))
        }
        open.logicalRegisters.addAll(registers)
        return ScratchRegisterRange(registers)
    }

    fun stageType(descriptor: String): StagedDexReference.Type =
        stageReference(StagedReferenceKey.Type(descriptor)) {
            StagedDexReference.Type(descriptor)
        } as StagedDexReference.Type

    fun stageString(value: String): StagedDexReference.StringLiteral =
        stageReference(StagedReferenceKey.StringValue(value)) {
            StagedDexReference.StringLiteral(value)
        } as StagedDexReference.StringLiteral

    fun stageField(
        definingClass: String,
        name: String,
        type: String,
    ): StagedDexReference.Field =
        stageReference(StagedReferenceKey.Field(definingClass, name, type)) {
            StagedDexReference.Field(definingClass, name, type)
        } as StagedDexReference.Field

    fun stageMethod(
        definingClass: String,
        name: String,
        parameterTypes: List<String>,
        returnType: String,
    ): StagedDexReference.Method {
        val copiedParameterTypes = parameterTypes.toList()
        return stageReference(StagedReferenceKey.Method(definingClass, name, copiedParameterTypes, returnType)) {
            StagedDexReference.Method(definingClass, name, copiedParameterTypes, returnType)
        } as StagedDexReference.Method
    }

    fun stagePrototype(
        parameterTypes: List<String>,
        returnType: String,
    ): StagedDexReference.Prototype {
        val copiedParameterTypes = parameterTypes.toList()
        return stageReference(StagedReferenceKey.Prototype(copiedParameterTypes, returnType)) {
            StagedDexReference.Prototype(copiedParameterTypes, returnType)
        } as StagedDexReference.Prototype
    }

    private fun stageReference(
        key: StagedReferenceKey,
        create: () -> StagedDexReference,
    ): StagedDexReference {
        val open = open()
        return open.stagedReferences.getOrPut(key) { create().also(open.stagedReferenceHandles::add) }
    }

    fun setTryRegions(tryRegionSpecs: List<TryRegionSpec>) {
        open().submittedTryRegions = tryRegionSpecs.toList()
    }

    fun insertInstructionsBefore(
        codeAddress: Int,
        instructionTemplates: List<InstructionTemplate>,
    ): InsertedInstructionAnchors {
        val open = open()
        val templates = instructionTemplates.toList()
        val anchors = List(templates.size) { PendingCodeAnchor() }
        open.commands.add(EditCommand.Insert(codeAddress, templates, anchors, open.nextCommandSequence++))
        return InsertedInstructionAnchors(anchors)
    }

    fun replaceInstruction(
        codeAddress: Int,
        instructionTemplates: List<InstructionTemplate>,
    ) {
        val open = open()
        open.commands.add(EditCommand.Replace(codeAddress, instructionTemplates.toList(), open.nextCommandSequence++))
    }

    fun removeInstruction(codeAddress: Int) {
        val open = open()
        open.commands.add(EditCommand.Remove(codeAddress, open.nextCommandSequence++))
    }

    fun removeInstructions(codeAddresses: Collection<Int>) {
        val open = open()
        codeAddresses.toList().forEach { codeAddress ->
            open.commands.add(EditCommand.Remove(codeAddress, open.nextCommandSequence++))
        }
    }

    /** Validates and publishes the batch. The transaction closes whatever the outcome, including a thrown failure. */
    fun commit(): MethodEditCommitResult {
        val open = open()
        try {
            return commitOpen(open)
        } finally {
            openState = null
        }
    }

    fun discard() {
        open()
        openState = null
    }

    private fun open(): OpenState = checkNotNull(openState) { "Method edit transaction is already closed" }

    private fun commitOpen(open: OpenState): MethodEditCommitResult {
        open.revisionGuard.validate(open.workspace)?.let { return MethodEditCommitResult.Rejected(it) }
        val currentSnapshot = open.workspace.currentSnapshot
        val currentMethod = requireNotNull(currentSnapshot.applicationMethod(open.revisionGuard.methodSignature))

        open.stagedReferences.values.firstNotNullOfOrNull(MethodEditValidator::validateStagedReference)?.let {
            return MethodEditCommitResult.Rejected(it)
        }
        val indexedEdits = when (val result = indexCommands(open, currentSnapshot.dexReferenceClosure)) {
            is Checked.Accepted -> result.value
            is Checked.Rejected -> return MethodEditCommitResult.Rejected(result.reason)
        }
        validateCatchTypes(open, currentSnapshot)?.let { return MethodEditCommitResult.Rejected(it) }
        if (open.commands.isEmpty() && open.submittedTryRegions == null && open.scratchWordCount == 0) {
            return MethodEditCommitResult.Unchanged
        }
        // The draft drops inherited ranges that collapse, so malformed inherited ranges are rejected first.
        if (open.submittedTryRegions == null) {
            MethodEditValidator.validateLogicalBody(open.baselineBody)?.let { return MethodEditCommitResult.Rejected(it) }
        }

        val resolved = MethodBodyDraft.resolve(
            open.baselineBody,
            indexedEdits.insertions,
            indexedEdits.replacements,
            indexedEdits.removals,
            indexedEdits.anchors,
            open.submittedTryRegions,
        )
        if (resolved.body.instructions.none { it.instruction.opcode !in OpcodeGroups.PAYLOAD }) {
            return MethodEditCommitResult.Rejected(NoExecutableCode)
        }
        val allocation = when (
            val result = ScratchRegisterAllocator.allocate(resolved.body, currentMethod.declaration, open.scratchWordCount)
        ) {
            is ScratchRegisterAllocation.Accepted -> result
            is ScratchRegisterAllocation.Rejected -> return MethodEditCommitResult.Rejected(result.reason)
        }
        val logicalBody = allocation.body
        MethodEditValidator.validateLogicalBody(logicalBody)?.let { return MethodEditCommitResult.Rejected(it) }

        val projectedReferences = DexReferenceClosure.projectedProgramClosure(
            currentSnapshot.applicationClasses.values,
            currentMethod.methodSignature,
            DexReferenceClosure.fromMethod(currentMethod.declaration, logicalBody),
        )
        DexLimits.validate(projectedReferences, currentSnapshot.applicationClasses.values)?.let {
            return MethodEditCommitResult.Rejected(it)
        }
        val encoding = DexLimits.stringEncoding(projectedReferences.strings.size, currentSnapshot.stringInstructionEncoding)
        val triggersJumboRelayout = currentSnapshot.stringInstructionEncoding != encoding
        val layout = MethodLayoutSolver.solve(logicalBody, encoding)
        MethodEditValidator.validateLayout(layout, currentSnapshot, projectedReferences)?.let {
            return MethodEditCommitResult.Rejected(it)
        }

        val assembledBody = MethodBodyAssembler.assemble(layout)
        val candidate = ApplicationMethodDefinitionCandidate.from(currentMethod, assembledBody, currentSnapshot.hierarchy)
        MethodEditValidator.validateApplicationMethodDefinitionCandidate(
            candidate,
            currentSnapshot,
            DexReferenceClosure.fromStagedReferences(open.stagedReferences.values),
        )?.let { return MethodEditCommitResult.Rejected(it) }
        if (!triggersJumboRelayout && assembledBody.hasSameEncodingAs(requireNotNull(currentMethod.body))) {
            return MethodEditCommitResult.Unchanged
        }

        val candidates = if (triggersJumboRelayout) {
            when (val result = relayoutForJumboStrings(currentSnapshot, candidate, encoding, projectedReferences)) {
                is Checked.Accepted -> result.value
                is Checked.Rejected -> return MethodEditCommitResult.Rejected(result.reason)
            }
        } else {
            listOf(candidate)
        }
        val codeAddressesByAnchor = resolved.positionsByPendingAnchor.mapValues { (_, position) ->
            layout.codeAddressesByPosition.getValue(allocation.positionsAfterAllocation.getValue(position))
        }
        return publish(open.workspace, currentSnapshot, candidates, encoding, codeAddressesByAnchor)
    }

    /** Publishes [candidates]; the first is the edited method. */
    private fun publish(
        workspace: ProgramWorkspace,
        currentSnapshot: ProgramSnapshot,
        candidates: List<ApplicationMethodDefinitionCandidate>,
        encoding: StringInstructionEncoding,
        codeAddressesByAnchor: Map<PendingCodeAnchor, Int>,
    ): MethodEditCommitResult.Committed {
        val revisionsBySignature = LinkedHashMap<String, ApplicationMethodRevision>(candidates.size)
        val replacements = candidates.map { candidate ->
            val methodRevision = workspace.newApplicationMethodRevision(candidate.methodSignature)
            revisionsBySignature[candidate.methodSignature] = methodRevision
            ApplicationMethodDefinition.fromCandidate(candidate, methodRevision)
        }
        var applicationClasses = currentSnapshot.applicationClasses
        replacements.groupBy { it.declaration.definingClass }.forEach { (type, classReplacements) ->
            val replacementMap = classReplacements.associateBy(ApplicationMethodDefinition::methodSignature)
            val editedClass = requireNotNull(currentSnapshot.applicationClass(type)).withMethods(replacementMap)
            applicationClasses = applicationClasses.putting(type, editedClass)
        }
        val programRevision = workspace.newProgramRevision()
        val candidateSnapshot = ProgramSnapshot.candidate(currentSnapshot, programRevision, applicationClasses, encoding)
        val editedMethod = requireNotNull(candidateSnapshot.applicationMethod(candidates.first().methodSignature))
        val result = MethodEditCommitResult.Committed(
            MethodView.of(editedMethod, programRevision),
            programRevision,
            revisionsBySignature,
            codeAddressesByAnchor,
        )
        workspace.publish(candidateSnapshot)
        return result
    }

    private fun indexCommands(open: OpenState, currentReferences: DexReferenceClosure): Checked<IndexedEdits> {
        val commands = open.commands
        for (codeAddress in commands.map(EditCommand::codeAddress).distinct().sortedDescending()) {
            invalidAddressReason(open, codeAddress)?.let { return Checked.Rejected(it) }
        }
        for ((codeAddress, _) in open.anchorRequests) {
            if (codeAddress !in open.baselineBody.positionsByCodeAddress) {
                return Checked.Rejected(InvalidCodeAddress(codeAddress))
            }
        }

        val knownAnchors = buildSet {
            open.anchorRequests.mapTo(this) { it.second }
            commands.filterIsInstance<EditCommand.Insert>().flatMapTo(this) { it.anchors }
        }
        open.submittedTryRegions?.forEach { region ->
            val anchors = listOf(region.start, region.end) + region.catchHandlers.map(CatchHandlerSpec::target)
            if (anchors.any { it !in knownAnchors }) return Checked.Rejected(ForeignAnchor)
            val stagedTypes = region.catchHandlers.filterIsInstance<CatchHandlerSpec.Typed>().map { it.exceptionType }
            if (stagedTypes.any { it !in open.stagedReferenceHandles }) return Checked.Rejected(ForeignStagedReference)
        }

        val insertions = LinkedHashMap<Int, MutableList<DraftInsertion>>()
        val replacements = LinkedHashMap<Int, List<DraftInstructionTemplate>>()
        val removals = LinkedHashSet<Int>()
        val commandOrder = compareByDescending<EditCommand> { it.codeAddress }.thenBy { it.sequence }
        for (command in commands.sortedWith(commandOrder)) {
            val address = command.codeAddress
            when (command) {
                is EditCommand.Insert -> {
                    val resolved = when (
                        val result = resolveTemplates(open, address, command.templates, knownAnchors, currentReferences)
                    ) {
                        is Checked.Accepted -> result.value
                        is Checked.Rejected -> return result
                    }
                    insertions.getOrPut(address, ::mutableListOf).add(DraftInsertion(resolved, command.anchors))
                }
                is EditCommand.Replace -> {
                    if (address in replacements || address in removals) {
                        return Checked.Rejected(ConflictingCommands(address))
                    }
                    replacements[address] = when (
                        val result = resolveTemplates(open, address, command.templates, knownAnchors, currentReferences)
                    ) {
                        is Checked.Accepted -> result.value
                        is Checked.Rejected -> return result
                    }
                }
                is EditCommand.Remove -> {
                    if (address in removals || address in replacements) {
                        return Checked.Rejected(ConflictingCommands(address))
                    }
                    removals += address
                }
            }
        }
        val anchors = open.anchorRequests.groupBy(
            keySelector = { (codeAddress, _) -> open.baselineBody.positionsByCodeAddress.getValue(codeAddress) },
            valueTransform = { it.second },
        )
        return Checked.Accepted(IndexedEdits(insertions, replacements, removals, anchors))
    }

    private fun invalidAddressReason(open: OpenState, codeAddress: Int): MethodEditRejectionReason? = when (codeAddress) {
        in open.payloadAddresses -> PayloadCodeAddress(codeAddress)
        !in open.instructionAddresses -> InvalidCodeAddress(codeAddress)
        else -> null
    }

    private fun validateCatchTypes(open: OpenState, snapshot: ProgramSnapshot): MethodEditRejectionReason? {
        val descriptors = open.submittedTryRegions.orEmpty().flatMapTo(linkedSetOf()) { region ->
            region.catchHandlers.filterIsInstance<CatchHandlerSpec.Typed>().map { it.exceptionType.descriptor }
        }
        for (descriptor in descriptors) {
            MethodEditValidator.validateCatchTypeDescriptor(descriptor)?.let { return it }
            val facts = snapshot.hierarchy.facts(VirtualType.of(descriptor))
            if (facts.kind == ThrowableTypeKind.INTERFACE || facts.throwableRelation == TypeRelation.NO) {
                return CatchTypeNotThrowable(descriptor)
            }
        }
        return null
    }

    /**
     * Re-encodes every other method holding `const-string` as `const-string/jumbo`. Only operand widths change, so
     * register flow and logical shape are unchanged and the relayout needs layout validation only.
     */
    private fun relayoutForJumboStrings(
        snapshot: ProgramSnapshot,
        target: ApplicationMethodDefinitionCandidate,
        encoding: StringInstructionEncoding,
        projectedReferences: DexReferenceClosure,
    ): Checked<List<ApplicationMethodDefinitionCandidate>> {
        val prepared = arrayListOf(target)
        val collateralMethods = snapshot.applicationClasses.values
            .sortedBy(ApplicationClassDefinition::type)
            .flatMap { definition -> definition.methods.sortedBy(ApplicationMethodDefinition::methodSignature) }
            .filter { method ->
                method.methodSignature != target.methodSignature && method.body?.hasConstStringInstruction == true
            }
        for (method in collateralMethods) {
            val layout = MethodLayoutSolver.solve(requireNotNull(method.body), encoding)
            MethodEditValidator.validateLayout(layout, snapshot, projectedReferences)?.let {
                return Checked.Rejected(it)
            }
            val body = MethodBodyAssembler.assemble(layout)
            prepared += ApplicationMethodDefinitionCandidate.from(method, body, snapshot.hierarchy)
        }
        return Checked.Accepted(prepared)
    }

    private fun resolveTemplates(
        open: OpenState,
        codeAddress: Int,
        templates: List<InstructionTemplate>,
        knownAnchors: Set<PendingCodeAnchor>,
        currentReferences: DexReferenceClosure,
    ): Checked<List<DraftInstructionTemplate>> {
        if (templates.isEmpty()) return Checked.Rejected(EmptyInstructionList(codeAddress))
        val resolved = ArrayList<DraftInstructionTemplate>(templates.size)
        for (template in templates) {
            resolved += when (val result = resolveTemplate(open, template, knownAnchors, currentReferences)) {
                is Checked.Accepted -> result.value
                is Checked.Rejected -> return result
            }
        }
        return Checked.Accepted(resolved)
    }

    private fun resolveTemplate(
        open: OpenState,
        template: InstructionTemplate,
        knownAnchors: Set<PendingCodeAnchor>,
        currentReferences: DexReferenceClosure,
    ): Checked<DraftInstructionTemplate> {
        val detached = template.instruction ?: return Checked.Rejected(UnencodableInstruction(template.opcode))
        val referenceOperands = template.referenceOperands
        validateReferenceOperands(open, detached, referenceOperands, currentReferences)?.let {
            return Checked.Rejected(it)
        }
        val instruction = MethodBodyAssembler.materializeReferenceOperands(detached, referenceOperands)
        val logicalRegisters = template.logicalRegisters
        if (logicalRegisters != null) {
            validateLogicalRegisters(open, instruction, logicalRegisters)?.let { return Checked.Rejected(it) }
        }
        val opcode = instruction.opcode
        val fitsTemplate = when (template) {
            is InstructionTemplate.Encoded -> instruction !is OffsetInstruction && opcode !in OpcodeGroups.PAYLOAD
            is InstructionTemplate.Branch -> opcode in OpcodeGroups.BRANCH
            is InstructionTemplate.FillArrayData -> opcode == Opcode.FILL_ARRAY_DATA
            is InstructionTemplate.Switch -> opcode in OpcodeGroups.SWITCH
            is InstructionTemplate.Payload -> opcode in OpcodeGroups.PAYLOAD
        }
        if (!fitsTemplate) return Checked.Rejected(TemplateOpcodeMismatch(opcode))
        val switchCases = (template as? InstructionTemplate.Switch)?.switchCases.orEmpty()
        val targets = listOfNotNull(template.target) + switchCases.map(InstructionTemplate.Switch.Case::target)
        if (targets.any { it !in knownAnchors }) return Checked.Rejected(ForeignAnchor)
        return Checked.Accepted(
            DraftInstructionTemplate(instruction, template.target, switchCases, logicalRegisters, referenceOperands),
        )
    }

    private fun validateLogicalRegisters(
        open: OpenState,
        instruction: Instruction,
        logicalRegisters: List<LogicalRegister>,
    ): MethodEditRejectionReason? {
        val registerOperands = instruction.registerOperands().size
        if (logicalRegisters.size != registerOperands) {
            return LogicalRegisterCountMismatch(instruction.opcode, registerOperands, logicalRegisters.size)
        }
        if (logicalRegisters.any { it !in open.logicalRegisters }) return ForeignLogicalRegister
        val invalidBaseline = logicalRegisters.map(LogicalRegister::allocation)
            .filterIsInstance<LogicalRegister.Allocation.Baseline>()
            .firstOrNull { it.register !in 0 until open.baselineBody.registerCount }
        return invalidBaseline?.let { BaselineRegisterOutOfRange(it.register) }
    }

    private fun validateReferenceOperands(
        open: OpenState,
        instruction: Instruction,
        operands: List<DexReferenceOperand>,
        currentReferences: DexReferenceClosure,
    ): MethodEditRejectionReason? {
        val expectedTypes = when (instruction) {
            is DualReferenceInstruction -> listOf(instruction.referenceType, instruction.referenceType2)
            is ReferenceInstruction -> listOf(instruction.referenceType)
            else -> emptyList()
        }
        if (operands.size != expectedTypes.size) {
            return ReferenceOperandCountMismatch(instruction.opcode, expectedTypes.size, operands.size)
        }
        operands.forEachIndexed { index, operand ->
            when (operand) {
                is DexReferenceOperand.Existing ->
                    if (!currentReferences.containsAll(DexReferenceClosure.fromReferences(listOf(operand.reference)))) {
                        return UnstagedReference
                    }
                is DexReferenceOperand.Staged ->
                    if (operand.reference !in open.stagedReferenceHandles) return ForeignStagedReference
            }
            val reference = operand.asReference()
            if (ReferenceType.getReferenceType(reference) != expectedTypes[index]) {
                return ReferenceKindMismatch(instruction.opcode)
            }
            if (reference is TypeReference) {
                MethodEditValidator.validateTypeOperand(instruction.opcode, reference.type)?.let { return it }
            }
        }
        return null
    }
}
