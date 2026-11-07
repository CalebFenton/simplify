package org.cf.smalivm.core

import java.util.Collections
import java.util.IdentityHashMap
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.ReferenceType
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

enum class MethodEditTransactionState {
    OPEN,
    COMMITTED,
    UNCHANGED,
    REJECTED,
    DISCARDED,
}

/** One pending method edit batch and all handles bound to that batch. */
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

    private data class IndexedEdits(
        val insertions: Map<BaselineInstructionId, List<DraftInsertion>>,
        val replacements: Map<BaselineInstructionId, List<DraftInstructionTemplate>>,
        val removals: Set<BaselineInstructionId>,
        val anchors: Map<LogicalCodePosition, List<PendingCodeAnchor>>,
    )

    private sealed interface CommandIndexResult {
        data class Accepted(val edits: IndexedEdits) : CommandIndexResult
        data class Rejected(val reason: MethodEditRejectionReason) : CommandIndexResult
    }

    private sealed interface TemplateResolution {
        data class Accepted(val instructions: List<DraftInstructionTemplate>) : TemplateResolution
        data class Rejected(val reason: MethodEditRejectionReason) : TemplateResolution
    }

    private sealed interface StagedReferenceResolution {
        data object Accepted : StagedReferenceResolution
        data class Rejected(val reason: MethodEditRejectionReason) : StagedReferenceResolution
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

    private data class PreparedMethod(
        val candidate: ApplicationMethodDefinitionCandidate,
    )

    private sealed interface PreparedMethodsResult {
        data class Accepted(val methods: List<PreparedMethod>) : PreparedMethodsResult
        data class Rejected(val reason: MethodEditRejectionReason) : PreparedMethodsResult
    }

    private val transactionIdentity = Any()
    private var workspace: ProgramWorkspace? = workspace
    private var revisionGuard: MethodEditRevisionGuard? = revisionGuard
    private var baselineBody: MethodBody? = methodView.methodBody
    private var baselineIdsByCodeAddress: Map<Int, BaselineInstructionId>? =
        methodView.instructionAddresses.associateWith { BaselineInstructionId() }
    private var baselinePositionsByCodeAddress: Map<Int, LogicalCodePosition>? =
        methodView.methodBody?.positionsByCodeAddress
    private var baselineInstructions: List<BaselineDraftInstruction>? = methodView.methodBody?.let { body ->
        body.instructions.map { record ->
            BaselineDraftInstruction(
                requireNotNull(baselineIdsByCodeAddress)[record.codeAddress],
                record.codeAddress,
                record.position,
                record.instruction,
                record.position in body.syntheticPayloadPaddingPositions,
            )
        }
    }
    private var baselinePayloadAddresses: Set<Int>? = methodView.payloadAddresses.toSet()
    private var commands: MutableList<EditCommand>? = mutableListOf()
    private var anchorRequests: MutableList<Pair<Int, PendingCodeAnchor>>? = mutableListOf()
    private var logicalRegisters: MutableList<LogicalRegister>? = mutableListOf()
    private var stagedReferences: MutableMap<StagedReferenceKey, StagedDexReference>? = linkedMapOf()
    private var stagedReferenceIdentities: MutableSet<StagedDexReference>? =
        Collections.newSetFromMap(IdentityHashMap())
    private var submittedTryRegions: List<TryRegionSpec>? = null
    private var nextCommandSequence = 0
    private var scratchWordCount = 0

    var state: MethodEditTransactionState = MethodEditTransactionState.OPEN
        private set

    fun pendingAnchorForCodeAddress(codeAddress: Int): PendingCodeAnchor {
        ensureOpen()
        val anchor = newPendingAnchor()
        requireNotNull(anchorRequests).add(codeAddress to anchor)
        return anchor
    }

    fun logicalRegister(register: Int): LogicalRegister {
        ensureOpen()
        return LogicalRegister(
            transactionIdentity,
            LogicalRegister.Allocation.Baseline(register),
        ).also { requireNotNull(logicalRegisters).add(it) }
    }

    fun reserveScratchRegisters(wordCount: Int): ScratchRegisterRange {
        ensureOpen()
        require(wordCount in 1..DexLimits.MAX_REGISTER_COUNT) {
            "Scratch-register word count must be in 1..${DexLimits.MAX_REGISTER_COUNT}: $wordCount"
        }
        check(scratchWordCount <= Int.MAX_VALUE - wordCount) { "Scratch-register reservation count overflow" }
        val firstOffset = scratchWordCount
        scratchWordCount += wordCount
        val registers = List(wordCount) { offset ->
            LogicalRegister(
                transactionIdentity,
                LogicalRegister.Allocation.Scratch(firstOffset + offset),
            ).also { requireNotNull(logicalRegisters).add(it) }
        }
        return ScratchRegisterRange(registers)
    }

    fun stageType(descriptor: String): StagedDexReference.Type {
        ensureOpen()
        val key = StagedReferenceKey.Type(descriptor)
        return stageReference(key) {
            StagedDexReference.Type(transactionIdentity, descriptor)
        } as StagedDexReference.Type
    }

    fun stageString(value: String): StagedDexReference.String {
        ensureOpen()
        val key = StagedReferenceKey.StringValue(value)
        return stageReference(key) {
            StagedDexReference.String(transactionIdentity, value)
        } as StagedDexReference.String
    }

    fun stageField(
        definingClass: String,
        name: String,
        type: String,
    ): StagedDexReference.Field {
        ensureOpen()
        val key = StagedReferenceKey.Field(definingClass, name, type)
        return stageReference(key) {
            StagedDexReference.Field(transactionIdentity, definingClass, name, type)
        } as StagedDexReference.Field
    }

    fun stageMethod(
        definingClass: String,
        name: String,
        parameterTypes: List<String>,
        returnType: String,
    ): StagedDexReference.Method {
        ensureOpen()
        val copiedParameterTypes = parameterTypes.toList()
        val key = StagedReferenceKey.Method(definingClass, name, copiedParameterTypes, returnType)
        return stageReference(key) {
            StagedDexReference.Method(transactionIdentity, definingClass, name, copiedParameterTypes, returnType)
        } as StagedDexReference.Method
    }

    fun stagePrototype(
        parameterTypes: List<String>,
        returnType: String,
    ): StagedDexReference.Prototype {
        ensureOpen()
        val copiedParameterTypes = parameterTypes.toList()
        val key = StagedReferenceKey.Prototype(copiedParameterTypes, returnType)
        return stageReference(key) {
            StagedDexReference.Prototype(transactionIdentity, copiedParameterTypes, returnType)
        } as StagedDexReference.Prototype
    }

    private fun stageReference(
        key: StagedReferenceKey,
        create: () -> StagedDexReference,
    ): StagedDexReference {
        val references = requireNotNull(stagedReferences)
        references[key]?.let { return it }
        val reference = create()
        references[key] = reference
        requireNotNull(stagedReferenceIdentities).add(reference)
        return reference
    }

    fun setTryRegions(tryRegionSpecs: List<TryRegionSpec>) {
        ensureOpen()
        submittedTryRegions = tryRegionSpecs.toList().map(TryRegionSpec::detachedCopy)
    }

    fun insertInstructionsBefore(
        codeAddress: Int,
        instructionTemplates: List<InstructionTemplate>,
    ): InsertedInstructionAnchors {
        ensureOpen()
        val copied = copyTemplates(instructionTemplates)
        val anchors = List(copied.size) { newPendingAnchor() }
        requireNotNull(commands).add(EditCommand.Insert(codeAddress, copied, anchors, nextCommandSequence++))
        return InsertedInstructionAnchors(anchors)
    }

    fun replaceInstruction(
        codeAddress: Int,
        instructionTemplates: List<InstructionTemplate>,
    ) {
        ensureOpen()
        requireNotNull(commands).add(
            EditCommand.Replace(codeAddress, copyTemplates(instructionTemplates), nextCommandSequence++),
        )
    }

    fun removeInstruction(codeAddress: Int) {
        ensureOpen()
        requireNotNull(commands).add(EditCommand.Remove(codeAddress, nextCommandSequence++))
    }

    fun removeInstructions(codeAddresses: Collection<Int>) {
        ensureOpen()
        codeAddresses.toList().forEach { codeAddress ->
            requireNotNull(commands).add(EditCommand.Remove(codeAddress, nextCommandSequence++))
        }
    }

    fun commit(): MethodEditCommitResult {
        ensureOpen()
        return try {
            commitOpen()
        } catch (failure: Throwable) {
            if (state == MethodEditTransactionState.OPEN) finish(MethodEditTransactionState.REJECTED)
            throw failure
        }
    }

    fun discard() {
        ensureOpen()
        finish(MethodEditTransactionState.DISCARDED)
    }

    private fun commitOpen(): MethodEditCommitResult {
        val workspace = requireNotNull(workspace)
        val revisionGuard = requireNotNull(revisionGuard)
        revisionGuard.validate(workspace)?.let { return reject(it) }
        val currentSnapshot = workspace.currentSnapshot
        val currentMethod = requireNotNull(currentSnapshot.applicationMethod(revisionGuard.methodSignature))

        when (val result = resolveStagedReferences()) {
            StagedReferenceResolution.Accepted -> Unit
            is StagedReferenceResolution.Rejected -> return reject(result.reason)
        }
        val indexedEdits = when (val result = indexCommands(currentSnapshot.dexReferenceClosure)) {
            is CommandIndexResult.Accepted -> result.edits
            is CommandIndexResult.Rejected -> return reject(result.reason)
        }
        val stagedReferenceClosure = DexReferenceClosure.fromStagedReferences(
            requireNotNull(stagedReferences).values,
        )
        val stagedCatchTypeDescriptors = submittedTryRegions.orEmpty().flatMapTo(linkedSetOf()) { region ->
            region.catchHandlers.filterIsInstance<CatchHandlerSpec.Typed>().map { it.exceptionType.descriptor }
        }
        stagedCatchTypeDescriptors.forEach { descriptor ->
            MethodEditValidator.validateCatchTypeDescriptor(descriptor)?.let { return reject(it) }
            val facts = currentSnapshot.hierarchy.throwableHierarchy.facts(VirtualType.of(descriptor))
            if (facts.kind == ThrowableTypeKind.INTERFACE || facts.throwableRelation == TypeRelation.NO) {
                return reject(MethodEditRejectionReason.InvalidTryRegion("A typed catch is not a Throwable type"))
            }
        }
        if (requireNotNull(commands).isEmpty() && submittedTryRegions == null && scratchWordCount == 0) {
            finish(MethodEditTransactionState.UNCHANGED)
            return MethodEditCommitResult.Unchanged
        }
        if (submittedTryRegions == null) {
            MethodEditValidator.validateInheritedMetadata(requireNotNull(baselineBody))?.let { return reject(it) }
        }

        val resolvedEdit = MethodBodyDraft.resolve(
            requireNotNull(baselineBody),
            requireNotNull(baselineInstructions),
            indexedEdits.insertions,
            indexedEdits.replacements,
            indexedEdits.removals,
            indexedEdits.anchors,
            resolvedTryRegionSpecs(),
        )
        val draftConstruction = MethodBodyDraft.build(resolvedEdit)
        val draft = draftConstruction.draft
        if (draft.instructionCount == 0) {
            return reject(
                MethodEditRejectionReason.StructuralValidationFailure("A concrete method needs executable code"),
            )
        }

        val unresolvedBody = draft.toLogicalBody()
        val allocation = when (
            val result = ScratchRegisterAllocator.allocate(
                unresolvedBody,
                currentMethod.declaration,
                scratchWordCount,
            )
        ) {
            is ScratchRegisterAllocation.Accepted -> result
            is ScratchRegisterAllocation.Rejected -> return reject(result.reason)
        }
        val logicalBody = allocation.body
        MethodEditValidator.validateLogicalBody(logicalBody)?.let { return reject(it) }
        val logicalCandidateReferences = DexReferenceClosure.fromMethod(currentMethod.declaration, logicalBody)
        val projectedReferences = DexReferenceClosure.projectedProgramClosure(
            currentSnapshot.applicationClasses.values,
            currentMethod.methodSignature,
            logicalCandidateReferences,
        )
        DexLimits.validate(projectedReferences, currentSnapshot.applicationClasses.values)?.let {
            return reject(it)
        }
        val candidateEncoding = DexLimits.stringEncoding(
            projectedReferences.strings.size,
            currentSnapshot.stringInstructionEncoding,
        )
        val triggersJumboRelayout =
            currentSnapshot.stringInstructionEncoding == StringInstructionEncoding.CONST_STRING &&
                candidateEncoding == StringInstructionEncoding.CONST_STRING_JUMBO
        val layout = MethodLayoutSolver.solve(logicalBody, candidateEncoding)
        MethodEditValidator.validateLayout(layout, currentSnapshot, projectedReferences)?.let { return reject(it) }

        val assembledBody = MethodBodyAssembler.assemble(layout)
        val candidate = ApplicationMethodDefinitionCandidate.from(
            currentMethod,
            assembledBody,
            currentSnapshot.hierarchy,
        )
        MethodEditValidator.validateApplicationMethodDefinitionCandidate(
            candidate,
            currentSnapshot,
            stagedReferenceClosure,
        )?.let {
            return reject(it)
        }
        if (!triggersJumboRelayout && assembledBody.hasSameEncodingAs(requireNotNull(currentMethod.body))) {
            finish(MethodEditTransactionState.UNCHANGED)
            return MethodEditCommitResult.Unchanged
        }

        val preparedMethods = when (
            val result = prepareAffectedMethods(
                currentSnapshot,
                PreparedMethod(candidate),
                candidateEncoding,
                projectedReferences,
                triggersJumboRelayout,
            )
        ) {
            is PreparedMethodsResult.Accepted -> result.methods
            is PreparedMethodsResult.Rejected -> return reject(result.reason)
        }

        val revisionsBySignature = LinkedHashMap<String, ApplicationMethodRevision>(preparedMethods.size)
        val replacementsBySignature = LinkedHashMap<String, ApplicationMethodDefinition>(preparedMethods.size)
        preparedMethods.forEach { prepared ->
            val methodRevision = workspace.newApplicationMethodRevision(prepared.candidate.methodSignature)
            revisionsBySignature[prepared.candidate.methodSignature] = methodRevision
            replacementsBySignature[prepared.candidate.methodSignature] =
                ApplicationMethodDefinition.fromCandidate(prepared.candidate, methodRevision)
        }
        var applicationClasses = currentSnapshot.applicationClasses
        replacementsBySignature.values.groupBy { it.declaration.definingClass }.forEach { (type, replacements) ->
            val replacementMap = replacements.associateBy(ApplicationMethodDefinition::methodSignature)
            val editedClass = requireNotNull(currentSnapshot.applicationClass(type)).withMethods(replacementMap)
            applicationClasses = applicationClasses.putting(type, editedClass)
        }
        val programRevision = workspace.newProgramRevision()
        val candidateSnapshot = ProgramSnapshot.candidate(
            currentSnapshot,
            programRevision,
            applicationClasses,
            candidateEncoding,
        )
        val editedMethod = requireNotNull(candidateSnapshot.applicationMethod(candidate.methodSignature))
        val editedMethodView = MethodView.of(editedMethod, programRevision)
        val positionsByAnchor = draftConstruction.positionsByPendingAnchor.mapValues { (_, position) ->
            requireNotNull(allocation.positionsAfterAllocation[position])
        }
        val codeAddressesByAnchor = positionsByAnchor.mapValues { (_, position) ->
            requireNotNull(layout.codeAddressesByPosition[position])
        }
        val result = MethodEditCommitResult.Committed(
            editedMethodView,
            programRevision,
            revisionsBySignature,
            codeAddressesByAnchor,
        )

        workspace.publish(candidateSnapshot)
        finish(MethodEditTransactionState.COMMITTED)
        return result
    }

    private fun indexCommands(currentReferences: DexReferenceClosure): CommandIndexResult {
        val commands = requireNotNull(commands)
        for (codeAddress in commands.map(EditCommand::codeAddress).distinct().sortedDescending()) {
            val reason = invalidAddressReason(codeAddress)
            if (reason != null) {
                return CommandIndexResult.Rejected(reason)
            }
        }
        for (codeAddress in requireNotNull(anchorRequests).map(Pair<Int, PendingCodeAnchor>::first).distinct()) {
            if (codeAddress !in requireNotNull(baselinePositionsByCodeAddress)) {
                return CommandIndexResult.Rejected(MethodEditRejectionReason.InvalidCodeAddress(codeAddress))
            }
        }

        val ids = requireNotNull(baselineIdsByCodeAddress)
        val insertions = LinkedHashMap<BaselineInstructionId, MutableList<DraftInsertion>>()
        val replacements = LinkedHashMap<BaselineInstructionId, List<DraftInstructionTemplate>>()
        val removals = LinkedHashSet<BaselineInstructionId>()
        val knownAnchors = buildSet {
            requireNotNull(anchorRequests).mapTo(this) { it.second }
            commands.filterIsInstance<EditCommand.Insert>().flatMapTo(this) { it.anchors }
        }
        submittedTryRegions?.forEach { region ->
            if (region.catchHandlers.isEmpty()) {
                return CommandIndexResult.Rejected(
                    MethodEditRejectionReason.InvalidTryRegion(
                        "A protected range has no exception handler",
                    ),
                )
            }
            val catchAllIndexes = region.catchHandlers.mapIndexedNotNull { index, handler ->
                index.takeIf { handler is CatchHandlerSpec.CatchAll }
            }
            if (catchAllIndexes.size > 1 ||
                (catchAllIndexes.isNotEmpty() && catchAllIndexes.single() != region.catchHandlers.lastIndex)
            ) {
                return CommandIndexResult.Rejected(
                    MethodEditRejectionReason.InvalidTryRegion(
                        "A catch-all handler must be unique and last",
                    ),
                )
            }
            val anchors = buildList {
                add(region.start)
                add(region.end)
                region.catchHandlers.mapTo(this) { it.target }
            }
            if (anchors.any { !it.belongsTo(transactionIdentity) || it !in knownAnchors }) {
                return CommandIndexResult.Rejected(
                    MethodEditRejectionReason.InvalidTryRegion(
                        "A try-region anchor does not belong to this transaction",
                    ),
                )
            }
            for (handler in region.catchHandlers) {
                if (handler is CatchHandlerSpec.Typed &&
                    (!handler.exceptionType.belongsTo(transactionIdentity) ||
                        handler.exceptionType !in requireNotNull(stagedReferenceIdentities))
                ) {
                    return CommandIndexResult.Rejected(
                        MethodEditRejectionReason.InvalidTryRegion(
                            "A staged catch type does not belong to this transaction",
                        ),
                    )
                }
            }
        }
        for (command in commands.sortedWith(compareByDescending<EditCommand> { it.codeAddress }.thenBy { it.sequence })) {
            val id = requireNotNull(ids[command.codeAddress])
            when (command) {
                is EditCommand.Insert -> {
                    when (val result = resolveTemplates(command.templates, knownAnchors, currentReferences)) {
                        is TemplateResolution.Accepted -> {
                            insertions.getOrPut(id, ::mutableListOf).add(
                                DraftInsertion(result.instructions, command.anchors, command.sequence),
                            )
                        }
                        is TemplateResolution.Rejected -> return CommandIndexResult.Rejected(result.reason)
                    }
                }
                is EditCommand.Replace -> {
                    if (id in replacements || id in removals) {
                        return CommandIndexResult.Rejected(
                            MethodEditRejectionReason.ConflictingCommands(command.codeAddress),
                        )
                    }
                    when (val result = resolveTemplates(command.templates, knownAnchors, currentReferences)) {
                        is TemplateResolution.Accepted -> {
                            replacements[id] = result.instructions
                        }
                        is TemplateResolution.Rejected -> return CommandIndexResult.Rejected(result.reason)
                    }
                }
                is EditCommand.Remove -> {
                    if (id in removals || id in replacements) {
                        return CommandIndexResult.Rejected(
                            MethodEditRejectionReason.ConflictingCommands(command.codeAddress),
                        )
                    }
                    removals += id
                }
            }
        }
        val positions = requireNotNull(baselinePositionsByCodeAddress)
        val anchors = requireNotNull(anchorRequests).groupBy(
            keySelector = { (codeAddress, _) -> requireNotNull(positions[codeAddress]) },
            valueTransform = { it.second },
        )
        return CommandIndexResult.Accepted(
            IndexedEdits(insertions, replacements, removals, anchors),
        )
    }

    private fun resolveStagedReferences(): StagedReferenceResolution {
        val references = requireNotNull(stagedReferences).values
        for (reference in references) {
            MethodEditValidator.validateStagedReference(reference)?.let {
                return StagedReferenceResolution.Rejected(it)
            }
        }
        return StagedReferenceResolution.Accepted
    }

    private fun resolvedTryRegionSpecs(): List<DraftTryRegionSpec>? = submittedTryRegions?.map { region ->
        DraftTryRegionSpec(
            region.start,
            region.end,
            region.catchHandlers.map { handler ->
                when (handler) {
                    is CatchHandlerSpec.Typed -> DraftCatchHandlerSpec(handler.exceptionType.descriptor, handler.target)
                    is CatchHandlerSpec.CatchAll -> DraftCatchHandlerSpec(null, handler.target)
                }
            },
        )
    }

    private fun prepareAffectedMethods(
        snapshot: ProgramSnapshot,
        target: PreparedMethod,
        encoding: StringInstructionEncoding,
        projectedReferences: DexReferenceClosure,
        triggersJumboRelayout: Boolean,
    ): PreparedMethodsResult {
        if (!triggersJumboRelayout) return PreparedMethodsResult.Accepted(listOf(target))

        val prepared = ArrayList<PreparedMethod>()
        prepared += target
        val targetSignature = target.candidate.methodSignature
        val collateralMethods = snapshot.applicationClasses.values
            .sortedBy(ApplicationClassDefinition::type)
            .flatMap { definition -> definition.methods.sortedBy(ApplicationMethodDefinition::methodSignature) }
            .filter { method ->
                method.methodSignature != targetSignature && method.body?.hasConstStringInstruction == true
            }
        for (method in collateralMethods) {
            val layout = MethodLayoutSolver.solve(requireNotNull(method.body), encoding)
            MethodEditValidator.validateLayout(layout, snapshot, projectedReferences)?.let {
                return PreparedMethodsResult.Rejected(it)
            }
            val body = MethodBodyAssembler.assemble(layout)
            val candidate = ApplicationMethodDefinitionCandidate.from(method, body, snapshot.hierarchy)
            MethodEditValidator.validateApplicationMethodDefinitionCandidate(candidate, snapshot)?.let {
                return PreparedMethodsResult.Rejected(it)
            }
            prepared += PreparedMethod(candidate)
        }
        return PreparedMethodsResult.Accepted(prepared)
    }

    private fun resolveTemplates(
        templates: List<InstructionTemplate>,
        knownAnchors: Set<PendingCodeAnchor>,
        currentReferences: DexReferenceClosure,
    ): TemplateResolution {
        if (templates.isEmpty()) {
            return TemplateResolution.Rejected(
                MethodEditRejectionReason.InvalidInstructionTemplate("An insertion or replacement cannot be empty"),
            )
        }
        val instructions = ArrayList<DraftInstructionTemplate>(templates.size)
        for (template in templates) {
            val detachedInstruction = when (template) {
                is InstructionTemplate.Encoded -> template.instruction
                is InstructionTemplate.Branch -> template.instruction
                is InstructionTemplate.FillArrayData -> template.instruction
                is InstructionTemplate.Switch -> template.instruction
                is InstructionTemplate.Payload -> template.instruction
            }
            if (detachedInstruction == null) {
                return TemplateResolution.Rejected(
                    MethodEditRejectionReason.InvalidInstructionTemplate(
                        when (template) {
                            is InstructionTemplate.Encoded -> template.failureMessage
                            is InstructionTemplate.Branch -> template.failureMessage
                            is InstructionTemplate.FillArrayData -> template.failureMessage
                            is InstructionTemplate.Switch -> template.failureMessage
                            is InstructionTemplate.Payload -> template.failureMessage
                        } ?: "The instruction could not be detached",
                    ),
                )
            }
            val referenceOperands = when (template) {
                is InstructionTemplate.Encoded -> template.referenceOperands
                is InstructionTemplate.Branch -> template.referenceOperands
                is InstructionTemplate.FillArrayData -> template.referenceOperands
                is InstructionTemplate.Switch -> template.referenceOperands
                is InstructionTemplate.Payload -> template.referenceOperands
            }
            validateReferenceOperands(detachedInstruction, referenceOperands, currentReferences)?.let { return it }
            val instruction = MethodBodyAssembler.materializeReferenceOperands(detachedInstruction, referenceOperands)
            val logicalRegisters = when (template) {
                is InstructionTemplate.Encoded -> template.logicalRegisters
                is InstructionTemplate.Branch -> template.logicalRegisters
                is InstructionTemplate.FillArrayData -> template.logicalRegisters
                is InstructionTemplate.Switch -> template.logicalRegisters
                is InstructionTemplate.Payload -> null
            }
            if (logicalRegisters != null) {
                if (logicalRegisters.size != registerOperandCount(instruction)) {
                    return invalidTemplate(
                        "Logical register count ${logicalRegisters.size} does not match " +
                            "${registerOperandCount(instruction)} encoded register operands",
                    )
                }
                if (logicalRegisters.any { !it.belongsTo(transactionIdentity) || it !in requireNotNull(this.logicalRegisters) }) {
                    return invalidTemplate("A logical register does not belong to this transaction")
                }
                val invalidBaseline = logicalRegisters.map(LogicalRegister::allocation)
                    .filterIsInstance<LogicalRegister.Allocation.Baseline>()
                    .firstOrNull { it.register !in 0 until requireNotNull(baselineBody).registerCount }
                if (invalidBaseline != null) {
                    return invalidTemplate("Baseline register v${invalidBaseline.register} is outside the method register file")
                }
            }
            when (template) {
                is InstructionTemplate.Encoded -> {
                    if (instruction is OffsetInstruction || instruction.opcode in OpcodeGroups.PAYLOAD) {
                        return invalidTemplate("Encoded templates cannot contain offsets or payload data")
                    }
                    instructions += DraftInstructionTemplate(
                        instruction,
                        logicalRegisters = logicalRegisters,
                        referenceOperands = referenceOperands,
                    )
                }
                is InstructionTemplate.Branch -> {
                    if (instruction.opcode !in OpcodeGroups.BRANCH) {
                        return invalidTemplate("Branch templates require a conditional or goto instruction")
                    }
                    invalidTarget(template.target, knownAnchors)?.let { return TemplateResolution.Rejected(it) }
                    instructions += DraftInstructionTemplate(
                        instruction,
                        template.target,
                        logicalRegisters = logicalRegisters,
                        referenceOperands = referenceOperands,
                    )
                }
                is InstructionTemplate.FillArrayData -> {
                    if (instruction.opcode != Opcode.FILL_ARRAY_DATA) {
                        return invalidTemplate("Fill-array-data templates require a fill-array-data instruction")
                    }
                    invalidTarget(template.target, knownAnchors)?.let { return TemplateResolution.Rejected(it) }
                    instructions += DraftInstructionTemplate(
                        instruction,
                        template.target,
                        logicalRegisters = logicalRegisters,
                        referenceOperands = referenceOperands,
                    )
                }
                is InstructionTemplate.Switch -> {
                    if (instruction.opcode !in OpcodeGroups.SWITCH) {
                        return invalidTemplate("Switch templates require a packed-switch or sparse-switch instruction")
                    }
                    invalidTarget(template.payloadTarget, knownAnchors)?.let {
                        return TemplateResolution.Rejected(it)
                    }
                    for (case in template.switchCases) {
                        invalidTarget(case.target, knownAnchors)?.let { return TemplateResolution.Rejected(it) }
                    }
                    instructions += DraftInstructionTemplate(
                        instruction,
                        template.payloadTarget,
                        template.switchCases.map { case -> DraftSwitchCase(case.key, case.target) },
                        logicalRegisters,
                        referenceOperands,
                    )
                }
                is InstructionTemplate.Payload -> {
                    if (instruction.opcode !in OpcodeGroups.PAYLOAD) {
                        return invalidTemplate("Payload templates require array or switch payload data")
                    }
                    instructions += DraftInstructionTemplate(instruction, referenceOperands = referenceOperands)
                }
            }
        }
        return TemplateResolution.Accepted(instructions)
    }

    private fun validateReferenceOperands(
        instruction: com.android.tools.smali.dexlib2.iface.instruction.Instruction,
        operands: List<DexReferenceOperand>,
        currentReferences: DexReferenceClosure,
    ): TemplateResolution.Rejected? {
        val expectedTypes = when (instruction) {
            is DualReferenceInstruction -> listOf(instruction.referenceType, instruction.referenceType2)
            is ReferenceInstruction -> listOf(instruction.referenceType)
            else -> emptyList()
        }
        if (operands.size != expectedTypes.size) {
            return invalidTemplate(
                "Instruction ${instruction.opcode} requires ${expectedTypes.size} reference operands, " +
                    "but ${operands.size} were supplied",
            )
        }
        operands.forEachIndexed { index, operand ->
            when (operand) {
                is DexReferenceOperand.Existing -> {
                    val operandClosure = DexReferenceClosure.fromReferences(listOf(operand.reference))
                    if (!currentReferences.containsAll(operandClosure)) {
                        return invalidTemplate("An existing DEX reference is absent from the current snapshot")
                    }
                }
                is DexReferenceOperand.Staged -> if (
                    !operand.reference.belongsTo(transactionIdentity) ||
                    operand.reference !in requireNotNull(stagedReferenceIdentities)
                ) {
                    return invalidTemplate("A staged DEX reference does not belong to this transaction")
                }
            }
            val reference = operand.asReference()
            if (ReferenceType.getReferenceType(reference) != expectedTypes[index]) {
                return invalidTemplate("A DEX reference operand has the wrong kind for ${instruction.opcode}")
            }
            if (reference is TypeReference) {
                MethodEditValidator.validateTypeOperand(instruction.opcode, reference.type)?.let {
                    return TemplateResolution.Rejected(it)
                }
            }
        }
        return null
    }

    private fun invalidTarget(
        target: PendingCodeAnchor,
        knownAnchors: Set<PendingCodeAnchor>,
    ): MethodEditRejectionReason? = if (!target.belongsTo(transactionIdentity) || target !in knownAnchors) {
        MethodEditRejectionReason.InvalidControlTarget("The target anchor does not belong to this transaction")
    } else {
        null
    }

    private fun invalidTemplate(message: String): TemplateResolution.Rejected =
        TemplateResolution.Rejected(MethodEditRejectionReason.InvalidInstructionTemplate(message))

    private fun registerOperandCount(instruction: com.android.tools.smali.dexlib2.iface.instruction.Instruction): Int = when (instruction) {
        is FiveRegisterInstruction -> instruction.registerCount
        is RegisterRangeInstruction -> instruction.registerCount
        is ThreeRegisterInstruction -> 3
        is TwoRegisterInstruction -> 2
        is OneRegisterInstruction -> 1
        else -> 0
    }

    private fun invalidAddressReason(codeAddress: Int): MethodEditRejectionReason? = when {
        codeAddress in requireNotNull(baselinePayloadAddresses) ->
            MethodEditRejectionReason.PayloadCodeAddress(codeAddress)
        codeAddress !in requireNotNull(baselineIdsByCodeAddress) ->
            MethodEditRejectionReason.InvalidCodeAddress(codeAddress)
        else -> null
    }

    private fun copyTemplates(instructionTemplates: List<InstructionTemplate>): List<InstructionTemplate> =
        instructionTemplates.toList().map { template ->
            when (template) {
                is InstructionTemplate.Encoded -> template.detachedCopy()
                is InstructionTemplate.Branch -> template.detachedCopy()
                is InstructionTemplate.FillArrayData -> template.detachedCopy()
                is InstructionTemplate.Switch -> template.detachedCopy()
                is InstructionTemplate.Payload -> template.detachedCopy()
            }
        }

    private fun newPendingAnchor(): PendingCodeAnchor = PendingCodeAnchor(transactionIdentity)

    private fun reject(reason: MethodEditRejectionReason): MethodEditCommitResult.Rejected {
        finish(MethodEditTransactionState.REJECTED)
        return MethodEditCommitResult.Rejected(reason)
    }

    private fun finish(terminalState: MethodEditTransactionState) {
        check(state == MethodEditTransactionState.OPEN) { "Method edit transaction is already closed" }
        state = terminalState
        commands?.clear()
        commands = null
        anchorRequests?.clear()
        anchorRequests = null
        logicalRegisters?.clear()
        logicalRegisters = null
        stagedReferences?.clear()
        stagedReferences = null
        stagedReferenceIdentities?.clear()
        stagedReferenceIdentities = null
        submittedTryRegions = null
        baselineInstructions = null
        baselineIdsByCodeAddress = null
        baselinePositionsByCodeAddress = null
        baselinePayloadAddresses = null
        baselineBody = null
        scratchWordCount = 0
        workspace = null
        revisionGuard = null
    }

    private fun ensureOpen() {
        check(state == MethodEditTransactionState.OPEN) { "Method edit transaction is already closed: $state" }
    }
}
