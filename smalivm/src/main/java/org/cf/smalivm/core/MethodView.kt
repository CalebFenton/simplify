package org.cf.smalivm.core

import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.iface.instruction.Instruction

/** One parameter's register and declared type. Wide parameters own [register] and `register + 1`. */
data class ParameterSlot(val register: Int, val type: VirtualType)

/**
 * A method's static structure, built from its [MethodBody] without executing anything: which addresses exist, what
 * instruction is at each, how control falls through, where try blocks are, and how registers are laid out. The view
 * exposes immutable dexlib instruction interfaces.
 */
class MethodView private constructor(
    val signature: String,
    val definingClass: VirtualType,
    val returnType: VirtualType,
    val isStatic: Boolean,
    parameters: List<ParameterSlot>,
    internal val methodBody: MethodBody,
    internal val frameworkMethodIdentity: FrameworkMethodIdentity?,
    val programRevision: ProgramRevision,
    val methodRevision: ApplicationMethodRevision?,
    val methodAccessFlags: Int,
) {
    val registerCount: Int = methodBody.registerCount
    private val addressToInstruction: Map<Int, Instruction> = methodBody.instructionsByCodeAddress
    val isSynchronized: Boolean = java.lang.reflect.Modifier.isSynchronized(methodAccessFlags) ||
        AccessFlags.DECLARED_SYNCHRONIZED.isSet(methodAccessFlags)
    val parameters: List<ParameterSlot> = java.util.Collections.unmodifiableList(parameters.toList())
    val tryBlocks: List<TryBlock> = java.util.Collections.unmodifiableList(
        methodBody.implementation.tryBlocks.map { block ->
            val handlers = block.exceptionHandlers
            TryBlock(
                block.startCodeAddress,
                block.startCodeAddress + block.codeUnitCount,
                handlers.filter { it.exceptionType != null }.map {
                    CatchClause(VirtualType.of(requireNotNull(it.exceptionType)), it.handlerCodeAddress)
                },
                handlers.firstOrNull { it.exceptionType == null }?.handlerCodeAddress,
            )
        },
    )

    private sealed interface CanonicalHandlerMetadata {
        data class Valid(val tables: PersistentList<HandlerTable>) : CanonicalHandlerMetadata
        data object MalformedOverlap : CanonicalHandlerMetadata
    }

    private val canonicalHandlers: CanonicalHandlerMetadata = canonicalizeHandlers(tryBlocks)

    private val storedInstructionAddresses: IntArray = addressToInstruction
        .filterValues { it.opcode !in OpcodeGroups.PAYLOAD }
        .keys
        .toIntArray()

    /** Executable instruction code addresses in body order. Each read returns a defensive copy. */
    val instructionAddresses: IntArray get() = storedInstructionAddresses.copyOf()

    private val storedPayloadAddresses: IntArray = addressToInstruction
        .filterValues { it.opcode in OpcodeGroups.PAYLOAD }
        .keys
        .toIntArray()

    /** Payload pseudo-instruction code addresses in body order. Each read returns a defensive copy. */
    val payloadAddresses: IntArray get() = storedPayloadAddresses.copyOf()

    private val storedAddresses: IntArray = addressToInstruction.keys.toIntArray()

    /** Every instruction and payload code address in body order. Each read returns a defensive copy. */
    val addresses: IntArray get() = storedAddresses.copyOf()

    /** Number of executable instructions, excluding payload pseudo-instructions. */
    val instructionCount: Int = storedInstructionAddresses.size

    /**
     * Lowest register holding a parameter; equals [registerCount] when there are none. Dalvik puts
     * parameters in the highest registers, so `[firstParameterRegister, registerCount)` is the
     * parameter region and everything below it is a local.
     */
    val firstParameterRegister: Int = parameters.minOfOrNull { it.register } ?: registerCount

    private val storedTerminalInstructionAddresses: IntArray = storedInstructionAddresses
        .filter { addressToInstruction.getValue(it).opcode in TERMINATING_OPCODES }
        .toIntArray()

    /** Executable path-ending instruction addresses. Each read returns a defensive copy. */
    val terminalInstructionAddresses: IntArray get() = storedTerminalInstructionAddresses.copyOf()

    /**
     * This method's control flow, built once per view; null when ART's verifier would reject the body's control
     * structure: a target that is not an instruction, a payload of the wrong kind, overlapping try ranges, or a
     * reachable instruction that falls off the code.
     */
    val controlFlow: MethodControlFlow? by lazy {
        MethodControlFlow.of(methodBody)?.takeUnless(MethodControlFlow::hasReachableFallOff)
    }

    /**
     * Addresses holding a payload pseudo-instruction — `.array-data` and the two switch tables. These are
     * **data, not code**: nothing branches *through* them and the core never executes one, so
     * `wasReached` is false there on every run. **SPEC**, and stated twice: [BC]'s "General design" says
     * such pseudo-instructions *"must never be encountered during the normal flow of execution"*, and
     * [CON] B22 makes it a verifier constraint — *"The packed-switch-data, sparse-switch-data, and
     * fill-array-data pseudo-instructions must not be reachable by control flow."*
     *
     * The public address array is defensive. Mutation of one returned array cannot change this view.
     *
     * Two consumers need it and neither can derive it: Plan 13's editor must not relocate a payload as if
     * it were an instruction, and Plan 14's dead-code strategy must not read an unreached payload as dead
     * code. The differential subtracts it from both engines' reached sets (see `SmaliVmUnderTest.run`).
     */
    fun isPayload(address: Int): Boolean = storedPayloadAddresses.binarySearch(address) >= 0

    fun hasAddress(address: Int): Boolean = addressToInstruction.containsKey(address)

    fun instructionAt(address: Int): Instruction {
        require(!isPayload(address)) { "Address $address is payload data in $signature" }
        return requireNotNull(addressToInstruction[address]) { "No instruction at address $address in $signature" }
    }

    fun payloadAt(address: Int): Instruction {
        require(isPayload(address)) { "Address $address is not payload data in $signature" }
        return requireNotNull(addressToInstruction[address]) { "No payload at address $address in $signature" }
    }

    internal fun controlTargetAt(address: Int): Int? = methodBody.controlTargetAt(address)

    internal fun switchCasesAt(address: Int): List<Pair<Int, Int>> = methodBody.switchCasesAt(address)

    fun opcodeAt(address: Int): Opcode = instructionAt(address).opcode

    fun codeAddressAtInstructionIndex(index: Int): Int {
        require(index in storedInstructionAddresses.indices) { "No instruction at index $index in $signature" }
        return storedInstructionAddresses[index]
    }

    fun instructionIndexAtCodeAddress(codeAddress: Int): Int? =
        storedInstructionAddresses.binarySearch(codeAddress).takeIf { it >= 0 }

    internal fun handlerTableAt(address: Int): HandlerTableLookup = when (val metadata = canonicalHandlers) {
        CanonicalHandlerMetadata.MalformedOverlap -> HandlerTableLookup.MalformedMetadata
        is CanonicalHandlerMetadata.Valid -> {
            val table = metadata.tables.firstOrNull { it.covers(address) }
            if (table == null) HandlerTableLookup.None else HandlerTableLookup.Found(table)
        }
    }

    /** The address immediately after [address], i.e. this instruction's width in 16-bit code units. */
    fun nextAddress(address: Int): Int = address + instructionAt(address).codeUnits

    /** Whether execution may fall through from [address] to [nextAddress]. False for returns, throws and gotos. */
    fun canContinue(address: Int): Boolean = opcodeAt(address).canContinue()

    internal fun hasSameExecutionIdentity(other: MethodView): Boolean {
        if (programRevision !== other.programRevision) return false
        return matchesActivationMethod(other)
    }

    internal fun matchesActivationMethod(other: MethodView): Boolean {
        return if (methodRevision != null || other.methodRevision != null) {
            programRevision === other.programRevision &&
                methodRevision !== null &&
                methodRevision === other.methodRevision
        } else {
            frameworkMethodIdentity !== null && frameworkMethodIdentity === other.frameworkMethodIdentity
        }
    }

    override fun toString(): String = signature

    companion object {
        private val TERMINATING_OPCODES = OpcodeGroups.RETURN + Opcode.THROW

        private data class HandlerRange(val startAddress: Int, val endAddress: Int)

        private fun canonicalizeHandlers(tryBlocks: List<TryBlock>): CanonicalHandlerMetadata {
            val clausesByRange = LinkedHashMap<HandlerRange, PersistentList.Builder<HandlerClause>>()
            for (block in tryBlocks) {
                val range = HandlerRange(block.startAddress, block.endAddress)
                val clauses = clausesByRange.getOrPut(range) { persistentListOf<HandlerClause>().builder() }
                for (catch in block.catches) {
                    clauses.add(HandlerClause.Typed(catch.exceptionType, catch.handlerAddress))
                }
                block.catchAllAddress?.let { clauses.add(HandlerClause.CatchAll(it)) }
            }
            val tables = clausesByRange.map { (range, clauses) ->
                HandlerTable(range.startAddress, range.endAddress, clauses.build())
            }.sortedWith(compareBy(HandlerTable::startAddress, HandlerTable::endAddress))

            var previousNonEmpty: HandlerTable? = null
            for (table in tables) {
                if (table.startAddress == table.endAddress) continue
                val previous = previousNonEmpty
                if (previous != null && table.startAddress < previous.endAddress) {
                    return CanonicalHandlerMetadata.MalformedOverlap
                }
                previousNonEmpty = table
            }
            return CanonicalHandlerMetadata.Valid(tables.toPersistentList())
        }

        internal fun of(
            method: ApplicationMethodDefinition,
            programRevision: ProgramRevision,
        ): MethodView = of(
            method.methodSignature,
            method.declaration.definingClass,
            method.declaration.returnType,
            method.declaration.accessFlags,
            method.declaration.parameterTypes.map(CharSequence::toString),
            requireNotNull(method.body) { "Cannot build a MethodView for a method with no body: ${method.methodSignature}" },
            programRevision,
            method.methodRevision,
        )

        internal fun of(
            method: FrameworkMethodDefinition,
            programRevision: ProgramRevision,
        ): MethodView = of(
            method.methodSignature,
            method.declaration.definingClass,
            method.declaration.returnType,
            method.declaration.accessFlags,
            method.declaration.parameterTypes.map(CharSequence::toString),
            requireNotNull(method.body) { "Cannot build a MethodView for a method with no body: ${method.methodSignature}" },
            programRevision,
            methodRevision = null,
            frameworkMethodIdentity = method.identity,
        )

        private fun of(
            signature: String,
            definingClass: String,
            returnType: String,
            accessFlags: Int,
            parameterTypes: List<String>,
            body: MethodBody,
            programRevision: ProgramRevision,
            methodRevision: ApplicationMethodRevision?,
            frameworkMethodIdentity: FrameworkMethodIdentity? = null,
        ): MethodView {
            val isStatic = java.lang.reflect.Modifier.isStatic(accessFlags)
            val types = buildList {
                if (!isStatic) add(VirtualType.of(definingClass))
                parameterTypes.mapTo(this, VirtualType::of)
            }
            val width = types.sumOf { if (it.isWide) 2 else 1 }
            var register = body.registerCount - width
            val parameters = types.map { type ->
                ParameterSlot(register, type).also { register += if (type.isWide) 2 else 1 }
            }
            return MethodView(
                signature,
                VirtualType.of(definingClass),
                VirtualType.of(returnType),
                isStatic,
                parameters,
                body,
                frameworkMethodIdentity,
                programRevision,
                methodRevision,
                accessFlags,
            )
        }
    }
}
