package org.cf.smalivm.core

import org.cf.smalivm.core.op.AGetOp
import org.cf.smalivm.core.op.APutOp
import org.cf.smalivm.core.op.ArrayLengthOp
import org.cf.smalivm.core.op.BinaryMathOp
import org.cf.smalivm.core.op.CheckCastOp
import org.cf.smalivm.core.op.CmpOp
import org.cf.smalivm.core.op.ConstOp
import org.cf.smalivm.core.op.FillArrayDataOp
import org.cf.smalivm.core.op.FilledNewArrayOp
import org.cf.smalivm.core.op.GotoOp
import org.cf.smalivm.core.op.IfComparison
import org.cf.smalivm.core.op.IfOp
import org.cf.smalivm.core.op.InstanceOfOp
import org.cf.smalivm.core.op.IGetOp
import org.cf.smalivm.core.op.IPutOp
import org.cf.smalivm.core.op.InvokeOp
import org.cf.smalivm.core.op.MonitorEnterOp
import org.cf.smalivm.core.op.MonitorExitOp
import org.cf.smalivm.core.op.MoveOp
import org.cf.smalivm.core.op.NewArrayOp
import org.cf.smalivm.core.op.NewInstanceOp
import org.cf.smalivm.core.op.NopOp
import org.cf.smalivm.core.op.ReturnOp
import org.cf.smalivm.core.op.SGetOp
import org.cf.smalivm.core.op.SPutOp
import org.cf.smalivm.core.op.SwitchDispatch
import org.cf.smalivm.core.op.SwitchOp
import org.cf.smalivm.core.op.ThrowOp
import org.cf.smalivm.core.op.UnaryMathOp
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderOffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OneRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.instruction.SwitchPayload
import com.android.tools.smali.dexlib2.iface.instruction.ThreeRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.TwoRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.VariableRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.WideLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.formats.ArrayPayload
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction12x
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction23x
import com.android.tools.smali.dexlib2.iface.reference.FieldReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.StringReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

abstract class Op internal constructor(val address: Int) {
    abstract val name: String

    /** Reads [frame] and returns one or more feasible arms. */
    internal abstract fun execute(frame: Frame): OpExecution

    /** Registers this op reads / assigns, for the graph's liveness queries (spec §9). */
    open val registersRead: Set<Int> get() = emptySet()
    open val registersAssigned: Set<Int> get() = emptySet()

    /**
     * The register set a single-destination op assigns: a wide (`J`/`D`) result owns [dest] **and**
     * `dest + 1`, because the high half of a pair is not an independent register.
     *
     * Exists so the rule is written once rather than per op. [ExecutionGraph.availableRegisters]
     * derives "free scratch register" from exactly these sets, so an op that named only the low half
     * of a wide result would offer a live pair's high half to the first consumer that wants scratch
     * space -- which corrupts the pair, silently, because [Frame.peek] answers null for a high half
     * and so every value query reads it as indistinguishable from unassigned.
     *
     * [isWide] deliberately has **no default**: every single-destination op must decide its result's
     * width where the opcode says what it is (decode time), rather than inherit a guess.
     */
    internal fun destinationRegisters(dest: Int, isWide: Boolean): Set<Int> =
        if (isWide) setOf(dest, dest + 1) else setOf(dest)

    override fun toString(): String = "$name@$address"
}

/**
 * An opcode this build does not implement yet. Aborting only this path -- rather than throwing --
 * is what lets opcode families land one plan at a time while every method stays executable and the
 * suite stays green. The run's completion status becomes Partial, so no consumer trusts consensus
 * from a truncated exploration (spec §6.4).
 */
internal class UnsupportedOp(address: Int, override val name: String) : Op(address) {
    override fun execute(frame: Frame): OpExecution = OpExecution.single(OpArm.Abort(AbortReason.UNSUPPORTED_OP))
}

/**
 * Decodes the instruction at an address into an [Op]. Every operand is resolved here, once, so ops
 * never touch dexlib2 or the class model at execution time -- they receive plain register numbers,
 * literals, target addresses and core [VirtualType]s. This file is therefore one of the three in the
 * core allowed to import `com.android.tools.smali.dexlib2` (with [ClassIndex] and [MethodView]); nothing under
 * `core/op/` may.
 */
internal object OpBuilder {

    private fun targetAddress(address: Int, instruction: Instruction, view: MethodView): Int =
        view.controlTargetAt(address) ?: when (instruction) {
            is BuilderOffsetInstruction -> instruction.target.codeAddress
            is OffsetInstruction -> address + instruction.codeOffset
            else -> error("${instruction.opcode.name} is not an offset instruction")
        }

    /**
     * [classIndex] is read by the allocation, type-test, field, and `aput-object` families for two different
     * reasons -- worth distinguishing, because
     * they are the two shapes every family that reaches the class model falls into.
     *  - **Resolve at decode time.** `new-array` and `filled-new-array` resolve their type reference and
     *    decide its loadability HERE (`isLoadable`); `instance-of` and `check-cast` do the same and refuse an
     *    unloadable operand as an [UnsupportedOp]; `const-class` resolves its operand to a host `Class` the
     *    same way ([ClassIndex.loadClassConstant]) and refuses an unresolvable one as an [UnsupportedOp] too.
     *    All five share one reason: resolvability is a static fact about the instruction, and `core/op/` may
     *    not reach the class model (the two-tier dependency rule). The `isLoadable` call is deliberately the
     *    same for the array pair, so design decision 4 is one ruling rather than two.
     *  - **Pass it down.** `aput` cannot resolve at decode time: `ArrayStoreException` is a question about the
     *    *runtime* type of the value against the *runtime* component type of the array, so what is passed
     *    down is the index itself, as the narrow [TypeHierarchy] interface it already implements, to be asked
     *    at execution time.
     *
     * Every other family this build implements needs neither and reads no `classIndex` at all. Invoke joins
     * the resolve-at-decode-time group in Task 20.
     */
    fun build(address: Int, view: MethodView, classIndex: ClassIndex): Op =
        build(address, view, classIndex.resolver(classIndex.currentSnapshot))

    fun build(address: Int, view: MethodView, resolver: ProgramSnapshotResolver): Op {
        if (view.isPayload(address)) return UnsupportedOp(address, view.payloadAt(address).opcode.name)
        val classIndex = resolver.classIndex
        val hierarchy = resolver.hierarchy
        val instruction = view.instructionAt(address)
        return when (instruction.opcode) {
            in NOP_OPCODES -> NopOp(address)
            in GOTO_OPCODES -> buildGoto(address, instruction, view)
            in IF_OPCODES -> buildIf(address, instruction, view)
            in SWITCH_OPCODES -> buildSwitch(address, instruction, view)
            in THROW_OPCODES -> buildThrow(address, instruction)
            in RETURN_OPCODES -> buildReturn(address, instruction)
            in MOVE_OPCODES -> buildMove(address, instruction)
            in CONST_OPCODES -> buildConst(address, instruction)
            in CONST_CLASS_OPCODES -> buildConstClass(address, instruction, classIndex)
            in UNARY_MATH_OPCODES -> buildUnaryMath(address, instruction)
            in BINARY_MATH_OPCODES -> buildBinaryMath(address, instruction)
            in CMP_OPCODES -> buildCmp(address, instruction)
            in NEW_ARRAY_OPCODES -> buildNewArray(address, instruction, classIndex)
            in NEW_INSTANCE_OPCODES -> buildNewInstance(address, instruction, classIndex)
            in ARRAY_LENGTH_OPCODES -> buildArrayLength(address, instruction)
            in AGET_OPCODES -> buildAGet(address, instruction)
            in APUT_OPCODES -> buildAPut(address, instruction, hierarchy, hierarchy)
            in FILL_ARRAY_DATA_OPCODES -> buildFillArrayData(address, instruction, view)
            in FILLED_NEW_ARRAY_OPCODES -> buildFilledNewArray(address, instruction, classIndex)
            in INSTANCE_OF_OPCODES -> buildInstanceOf(address, instruction, classIndex, hierarchy)
            in CHECK_CAST_OPCODES -> buildCheckCast(address, instruction, classIndex, hierarchy)
            in SGET_OPCODES -> buildSGet(address, instruction, classIndex)
            in SPUT_OPCODES -> buildSPut(address, instruction, classIndex)
            in IGET_OPCODES -> buildIGet(address, instruction, classIndex)
            in IPUT_OPCODES -> buildIPut(address, instruction, classIndex)
            in INVOKE_OPCODES -> buildInvoke(address, instruction, classIndex)
            in MONITOR_ENTER_OPCODES -> buildMonitorEnter(address, instruction)
            in MONITOR_EXIT_OPCODES -> buildMonitorExit(address, instruction)
            // A payload pseudo-instruction lands here, and that is correct: `.array-data` and the two
            // switch tables are DATA (see MethodView.payloadAddresses), so nothing should ever decode
            // one -- and if a malformed jump ever targets one, aborting that path is the fail-safe answer
            // rather than executing a table as code. [CON] B22 makes that normative for all three at
            // once, and Plan 8's `packed-switch`/`sparse-switch` inherit this branch unchanged: their
            // tables are resolved by `buildSwitch` at DECODE time, so the payload addresses stay
            // unreachable by control flow rather than being jumped to the way the oracle jumps to them.
            else -> UnsupportedOp(address, instruction.opcode.name)
        }
    }

    /** dexlib resolves the label, so the target is a real code address and never a raw offset. */
    private fun buildGoto(address: Int, instruction: Instruction, view: MethodView): GotoOp =
        GotoOp(address, instruction.opcode.name, targetAddress(address, instruction, view))

    /**
     * The twelve conditional branches. Two shapes and one op: `22t` names both operand registers,
     * `21t` names one and compares it against the integer zero, which [IfOp] selects on a null
     * [IfOp]`.rhsRegister` rather than on the mnemonic's `z`.
     *
     * The comparison is parsed from the mnemonic's own hyphen segment (`substringAfter('-')`), so
     * `if-eqz` and `if-eq` reach the same [IfComparison] without a `contains("-eq")` test -- which the
     * oracle uses (`opcode/IfOp.kt:120-126`) and which is order-dependent for no reason: `-le` is not a
     * substring of any other mnemonic here, but `contains` invites exactly the `rsub`/`sub` accident
     * [buildBinaryMath] documents.
     *
     * Target comes from dexlib's RESOLVED label, as [buildGoto] does, rather than from the oracle's
     * `address + codeOffset` arithmetic (`opcode/IfOp.kt:114-116`) -- so nothing downstream can mistake
     * an offset for an address. See [IfOp]'s KDoc for what that costs a later bytecode editor.
     */
    private fun buildIf(address: Int, instruction: Instruction, view: MethodView): IfOp {
        val name = instruction.opcode.name
        val comparison = when (val token = name.substringAfter('-').removeSuffix("z")) {
            "eq" -> IfComparison.EQ
            "ne" -> IfComparison.NE
            "lt" -> IfComparison.LT
            "ge" -> IfComparison.GE
            "gt" -> IfComparison.GT
            "le" -> IfComparison.LE
            // UNREACHABLE BY CONSTRUCTION, and therefore not a violation of the rule that every
            // decode-time failure degrades to an `UnsupportedOp` rather than throwing: `build`
            // dispatches here on `in IF_OPCODES`, and that set and this `when` name the SAME twelve
            // mnemonics. No input can reach it, so it is a programming-error assertion (a thirteenth
            // opcode added to the set without a comparison), not an input path. Same shape and same
            // reason as [buildAPut]'s `error`.
            else -> error("Unexpected if comparison '$token' in mnemonic '$name'")
        }
        return IfOp(
            address, name, comparison,
            targetAddress = targetAddress(address, instruction, view),
            lhsRegister = (instruction as OneRegisterInstruction).registerA,
            // `21t` is a OneRegisterInstruction and nothing more, so the cast decides the shape.
            rhsRegister = (instruction as? TwoRegisterInstruction)?.registerB,
        )
    }

    /**
     * `packed-switch` and `sparse-switch`, with the payload table decoded HERE, at build time -- which is
     * what lets [SwitchOp] return ordinary [Transition]s and lets the payload address stay unreachable by
     * control flow ([CON] B22). This is also the only place a `SwitchPayload` may be named at all:
     * `core/op/` may not import `com.android.tools.smali.dexlib2`.
     *
     * **Each element's offset is resolved against the SWITCH's address, not the payload's** -- **SPEC**,
     * from both payload tables: *"The targets are relative to the address of the switch opcode, not of
     * this table."* That is the fact the oracle had to recover by subtracting a hardcoded `3` from a
     * pseudo-return address it had stashed on the parent node (`opcode/SwitchPayloadOp.kt:26`, the
     * constant at `:69`); decoding here makes the switch's own address simply available.
     *
     * The **kind** is passed down as a [SwitchDispatch] rather than re-derived from the mnemonic at
     * execution time, per the ruling in [buildAPut]'s KDoc -- and it is load-bearing rather than tidy,
     * because dexlib2 normalizes packed and sparse into one `(key, offset)` list and the opcode is then
     * the only thing that says which dispatch rule applies (see [SwitchOp.fallsThroughOn]).
     *
     * A malformed payload target -- off the end, or pointing at something that is not a switch table --
     * makes this an [UnsupportedOp] rather than a decode throwable, exactly as [buildFillArrayData] does
     * and for the same reason: `UNSUPPORTED_OP` says *"this build cannot execute this instruction"*, which
     * is the true statement about malformed obfuscator output, while a decode failure inside
     * `buildSuccessor` becomes `Aborted(HOST_FAILURE)`, i.e. indistinguishable from a bug in this
     * codebase. [MethodView.hasAddress] is checked BEFORE [MethodView.instructionAt], which
     * `requireNotNull`s and throws (`CARRY-FORWARD.md`'s Plan 9 item 5 is the standing rule about that
     * pair).
     *
     * **BOTH guards are pinned, and NOT by a fixture, because smali cannot express either input.**
     * `OpBuilderTest.aSwitchWhoseTableIsMissingOrIsNotATableIsUnsupportedRatherThanAHostFailure` forges a
     * [MethodView] whose address map has the payload address removed, and a second whose payload address
     * holds an ordinary `return-void` — the device `CommitTest.forgedTryBlocks` and `ThrowRoutingTest`
     * already use for handler addresses no smali can express. **This is a measured difference from
     * [buildFillArrayData], not a shortcut:** a `fill-array-data` whose target is a `return-void` assembles
     * fine (Plan 7's fixture proves it), while `packed-switch v0, :not_a_table` is rejected by the
     * assembler itself — *"Switch instruction at address/index 0x1/1 does not refer to a payload
     * instruction."* So the fixture route is available to that twin and closed to this one, and without the
     * forgery these two lines were inert code: deleting both killed **0 of 947** tests, which a review
     * measured and the forged test now closes.
     *
     * **A malformed CASE target is deliberately NOT checked here.** It is not a decode failure: the op is
     * perfectly well formed and only one of its successors is impossible, so the engine's own
     * per-successor policy handles it -- `OnUnbuildable.SKIP_ONE` keeps the feasible siblings and reports
     * the loss as `AbortReason.MALFORMED_INPUT` (Plan 8 Task 2). Rejecting the whole instruction instead
     * would discard branches a device would take, which is strictly worse on obfuscated input. Pinned by
     * `core/op/SwitchOpTest.oneMalformedCaseTargetLosesItsOwnPathAndReportsMalformedInput`.
     */
    private fun buildSwitch(address: Int, instruction: Instruction, view: MethodView): Op {
        val unsupported = { UnsupportedOp(address, instruction.opcode.name) }
        val target = targetAddress(address, instruction, view)
        if (!view.hasAddress(target) || !view.isPayload(target)) return unsupported()
        val payload = view.payloadAt(target) as? SwitchPayload ?: return unsupported()
        val importedCases = view.switchCasesAt(address)
        return SwitchOp(
            address, instruction.opcode.name,
            selectorRegister = (instruction as OneRegisterInstruction).registerA,
            cases = if (importedCases.isNotEmpty()) {
                importedCases.map { (key, targetAddress) -> SwitchOp.Case(key, targetAddress) }
            } else {
                payload.switchElements.map { SwitchOp.Case(it.key, address + it.offset) }
            },
            // `31t` is three code units, so this is the `switchAddress + 3` [BC] describes -- read off
            // the instruction rather than written as a literal, which is the same number and one fewer
            // magic constant than the engine being replaced needed.
            fallThroughAddress = address + instruction.codeUnits,
            dispatch =
                if (instruction.opcode == Opcode.PACKED_SWITCH) SwitchDispatch.PACKED else SwitchDispatch.SPARSE,
        )
    }

    private fun buildThrow(address: Int, instruction: Instruction): ThrowOp =
        ThrowOp(address, (instruction as OneRegisterInstruction).registerA)

    private fun buildReturn(address: Int, instruction: Instruction): ReturnOp {
        val opcode = instruction.opcode
        // return-void is the only one with no register operand; casting it would throw.
        val register = if (opcode == Opcode.RETURN_VOID) null else (instruction as OneRegisterInstruction).registerA
        return ReturnOp(address, opcode.name, register)
    }

    private fun buildMove(address: Int, instruction: Instruction): MoveOp {
        val opcode = instruction.opcode
        val isWide = opcode in WIDE_MOVE_OPCODES
        // move-exception and move-result* name only a destination -- their source is a frame slot.
        val slot = when (opcode) {
            Opcode.MOVE_EXCEPTION -> MoveOp.Source.Caught
            Opcode.MOVE_RESULT, Opcode.MOVE_RESULT_WIDE, Opcode.MOVE_RESULT_OBJECT -> MoveOp.Source.Result
            else -> null
        }
        if (slot != null) {
            return MoveOp(address, opcode.name, (instruction as OneRegisterInstruction).registerA, isWide, slot)
        }
        val registers = instruction as TwoRegisterInstruction
        return MoveOp(
            address, opcode.name,
            toRegister = registers.registerA,
            isWide = isWide,
            source = MoveOp.Source.Register(registers.registerB),
        )
    }

    private fun buildConst(address: Int, instruction: Instruction): ConstOp {
        val opcode = instruction.opcode
        val destRegister = (instruction as OneRegisterInstruction).registerA
        val value = when (opcode) {
            Opcode.CONST_STRING, Opcode.CONST_STRING_JUMBO ->
                Value.Known(((instruction as ReferenceInstruction).reference as StringReference).string, STRING)
            Opcode.CONST_WIDE, Opcode.CONST_WIDE_16, Opcode.CONST_WIDE_32, Opcode.CONST_WIDE_HIGH16 ->
                Value.Known((instruction as WideLiteralInstruction).wideLiteral, LONG)
            else -> Value.Known((instruction as NarrowLiteralInstruction).narrowLiteral, INT)
        }
        return ConstOp(address, opcode.name, destRegister, value)
    }

    /**
     * `const-class vAA, type@BBBB` -- a class constant. Its own family and its own `build` branch (not a case
     * inside [buildConst]) because it is the ONE `const*` form that reaches the class model: it resolves a
     * type operand at decode time, exactly as [buildInstanceOf] and [buildCheckCast] do, and for the same two
     * reasons (resolvability is a static fact about the instruction, and `core/op/` may not reach the class
     * model). [buildConst]'s eight literal/string forms need none of that and are left untouched.
     *
     * **The value is a HOST `Class` typed `Ljava/lang/Class;`, resolved WITHOUT initializing it** -- see
     * [ClassIndex.loadClassConstant] for the whole of decisions 2 (host, not a [HeapRef]) and 3 (no
     * `<clinit>`; the node's effect is genuinely `NONE`). The resulting [ConstOp] declares no side effect,
     * pinned by `core/op/ConstOpTest.aConstClassLoadReportsNoSideEffect` and
     * `aConstClassLoadDoesNotInitializeItsClass`.
     *
     * **An operand naming a class ABSENT from the loaded set builds an op that throws a guest
     * `NoClassDefFoundError` at execute** -- never a host throw and never a whole-method rejection
     * (Global Constraints). *That* the failure is a linking error is INHERITED off [JVMS] `ldc`; *which*
     * exception is measured, `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`
     * (`docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once`,
     * `tools/art-runner/fixtures/Fx_ConstClassAbsentType.smali`). 🔴 **Plan 9 Task 15 (ROW-23) replaced Plan
     * 8's decode-time [UnsupportedOp] refusal with that throw**, for the reasons on [buildInstanceOf].
     *
     * The oracle instead deferred the load to execute time and returned the caught host
     * `ClassNotFoundException` AS the constant (`opcode/ConstOp.kt:34-36,75-77`) -- a checked **`Exception`**,
     * so its `catch (Exception)` handler caught it. That is the divergence that matters: a device's `Error` is
     * not caught there. Registered on `core/op/ConstOpTest.nonExistentClassNameThrowsException`, with the
     * handler-reachability consequence tested directly by that file's `catch`-clause trio.
     *
     * 🔴 **TWO PREDICATES, AND THEY ARE NOT THE SAME QUESTION -- the load is attempted FIRST and
     * `isLoadable` only decides which failure a failed load is.** Getting this order backwards is a real
     * defect that Task 15 shipped and a test caught: `ClassIndex.isLoadable` asks the SMALI CLASS MODEL
     * (`ClassManager`, i.e. the guest classes plus the bundled framework smali -- this engine's model of the
     * device's class world), while `loadClassConstant` asks the host [org.cf.smalivm.dex.SmaliClassLoader],
     * which **also delegates to its JVM parent**. So a compiled host class with no smali behind it -- for
     * instance `core/op/ConstOpTest`'s `ThrowingClinitProbe`, which exists precisely because `ClassBuilder`
     * stubs every guest `<clinit>` to a bare `RETURN` and so cannot witness an initialisation -- is
     * host-loadable and *not* in the model. Asking `isLoadable` first turned that legitimate resolvable input
     * into a `NoClassDefFoundError` and broke `aConstClassLoadDoesNotInitializeItsClass`.
     *
     * A successful load is proof the class exists, so it is never a resolution failure. Only a FAILED load
     * needs the model's opinion, and then the two answers mean different things:
     *  - **not in the model** -> Dalvik's resolution failure, the guest `NoClassDefFoundError` above;
     *  - **in the model but the host loader still could not produce a `Class`** -> an engine limitation, not
     *    a device behaviour, so [UnsupportedOp] is the honest report. A definite throw there would invent an
     *    infeasible path, and an `error(...)`/`require` would crash the engine on ordinary input.
     *
     * 🔴 **THAT THIRD ROW IS COMMON, NOT A DEFENSIVE STUB -- and this KDoc previously claimed "no current
     * input reaches" it, which was measured false.** `SmaliClassLoader.findClass` throws
     * `ClassNotFoundException` for **every** name starting with `java.`
     * (`dex/SmaliClassLoader.kt:43-57`), and `loadClass` then falls back to the JVM parent
     * (`:32-36`), which of course has no Android-only `java.*` class. So every bundled framework descriptor
     * that the host JVM does not also ship lands here. **Measured over the bundled framework model: 506 of
     * 2431 `java.*` descriptors** satisfy `isLoadable(t) && loadClassConstant(d) == null` -- over a fifth of
     * it, including ordinary classes like `Ljava/lang/DexCache;` and `Ljava/lang/Daemons$Daemon;`. A real APK
     * can name one, and a `const-class` on it makes the run `Partial`. Pinned by
     * `OpBuilderTest.aConstClassOnAModelledButHostUnloadableClassIsUnsupportedRatherThanAResolutionFailure`.
     * Do not "simplify" this row into the throw: the class genuinely exists on a device, so the throw would be
     * the invented-infeasible-path defect on a fifth of the framework.
     *
     * The residue is that `const-class` has one resolution channel its four family siblings do not, so a
     * host-only class with no smali (`core/op/ConstOpTest`'s `ThrowingClinitProbe`) throws under
     * `check-cast`/`instance-of`/`new-array`/`filled-new-array` and resolves here. **That** direction really
     * is test-suite-only -- no APK contains a class the class model has never heard of -- and the alternative
     * (making `const-class` refuse a class it can demonstrably produce a `Class` object for) would lose real
     * behaviour to buy uniformity on unreachable input.
     *
     * The resolved `Class` is identity-stable across executions -- [ClassIndex.classLoader] caches each
     * defined class -- so two `const-class` on ONE class yield the SAME reference, which `if-eq` folds to a
     * provable equality rather than a fork (`core/op/IfOpTest.twoConstClassResultsForOneClassMustBeOneReferenceAndNotAFork`,
     * [VER] via `docs/DALVIK-SEMANTICS.md#const-class-21c`).
     */
    private fun buildConstClass(address: Int, instruction: Instruction, classIndex: ClassIndex): Op {
        val destRegister = (instruction as OneRegisterInstruction).registerA
        val descriptor = ((instruction as ReferenceInstruction).reference as TypeReference).type
        // THE LOAD IS ATTEMPTED FIRST, and the order matters -- see the KDoc's "two predicates" paragraph.
        // A successful load is itself proof the class exists, so it can never be a resolution failure.
        val hostClass = classIndex.loadClassConstant(descriptor)
        if (hostClass != null) return ConstOp(address, instruction.opcode.name, destRegister, Value.Known(hostClass, CLASS))
        // The load failed, so `isLoadable` decides WHICH failure this is.
        val type = classIndex.typeOf(descriptor)
        if (!classIndex.isLoadable(type)) {
            // Absent from the loaded set: Dalvik's resolution failure, a guest NoClassDefFoundError at
            // execute. The value is `Unknown(Class)` rather than absent so `registersAssigned` still
            // declares the narrow width the resolvable case declares; it is never assigned, because the op
            // throws first.
            return ConstOp(
                address, instruction.opcode.name, destRegister,
                Value.Unknown(CLASS), unresolvedClassOperand = type,
            )
        }
        return UnsupportedOp(address, instruction.opcode.name)
    }

    /**
     * Result type is the mnemonic's LAST hyphen-segment. `int-to-byte` -> `B`, `neg-long` -> `J`.
     * This is the only decode-time fact about the value that is reliable: the source register's
     * declared type may be any of the several types Dalvik's ambiguous const opcodes produce.
     */
    private fun buildUnaryMath(address: Int, instruction: Instruction): UnaryMathOp {
        val registers = instruction as TwoRegisterInstruction
        val name = instruction.opcode.name
        val resultType = when (name.substringAfterLast('-')) {
            "int" -> INT
            "long" -> LONG
            "float" -> FLOAT
            "double" -> DOUBLE
            "byte" -> BYTE
            "char" -> CHAR
            "short" -> SHORT
            else -> error("Unexpected unary math result type in mnemonic '$name'")
        }
        return UnaryMathOp(address, name, registers.registerA, registers.registerB, resultType)
    }

    /**
     * Three instruction shapes, and the `/2addr` one is the trap: `vA` is BOTH the destination and
     * the first operand, so reading `registerB` as the lhs makes `add-int/2addr v0, v1` compute
     * `v1 + v1` -- still a plausible number, which is why it needs stating.
     *
     * Order matters for a different reason than the const family's: `Instruction22s` (`/lit16`,
     * including the register-free `rsub-int`) and `Instruction22b` (`/lit8`) both implement
     * `NarrowLiteralInstruction`, while `Instruction23x` and `Instruction12x` do not -- so the three
     * cases are disjoint and the literal check must not come first only because it is the broadest.
     *
     * The operator and domain are parsed by hyphen-segment rather than by prefix, so `rsub-int` is
     * never read as `sub`: `name.substringBefore('-')` isolates "rsub" as its own token, where a
     * `startsWith` chain would need `rsub` checked before `sub` to get the same answer by luck rather
     * than by construction.
     */
    private fun buildBinaryMath(address: Int, instruction: Instruction): BinaryMathOp {
        val name = instruction.opcode.name
        val operator = when (val token = name.substringBefore('-')) {
            "add" -> BinaryMathOp.Operator.ADD
            "sub" -> BinaryMathOp.Operator.SUB
            "rsub" -> BinaryMathOp.Operator.RSUB
            "mul" -> BinaryMathOp.Operator.MUL
            "div" -> BinaryMathOp.Operator.DIV
            "rem" -> BinaryMathOp.Operator.REM
            "and" -> BinaryMathOp.Operator.AND
            "or" -> BinaryMathOp.Operator.OR
            "xor" -> BinaryMathOp.Operator.XOR
            "shl" -> BinaryMathOp.Operator.SHL
            "shr" -> BinaryMathOp.Operator.SHR
            "ushr" -> BinaryMathOp.Operator.USHR
            else -> error("Unexpected binary math operator '$token' in mnemonic '$name'")
        }
        val domain = when (val token = name.substringAfter('-').substringBefore('/')) {
            "int" -> INT
            "long" -> LONG
            "float" -> FLOAT
            "double" -> DOUBLE
            else -> error("Unexpected binary math domain '$token' in mnemonic '$name'")
        }
        val registers = instruction as TwoRegisterInstruction
        return when (instruction) {
            is Instruction23x -> BinaryMathOp(
                address, name, registers.registerA, registers.registerB,
                BinaryMathOp.Operand.Register(instruction.registerC), operator, domain,
            )
            is Instruction12x -> BinaryMathOp(
                address, name, registers.registerA, registers.registerA,
                BinaryMathOp.Operand.Register(instruction.registerB), operator, domain,
            )
            is NarrowLiteralInstruction -> BinaryMathOp(
                address, name, registers.registerA, registers.registerB,
                BinaryMathOp.Operand.Literal(instruction.narrowLiteral), operator, domain,
            )
            else -> error("Unexpected instruction shape for binary math op: $instruction")
        }
    }

    /**
     * The FIRST branch that reads [classIndex]. The type reference is resolved and its loadability
     * decided HERE, at decode time, because loadability is a static fact about the instruction's type
     * reference and `core/op/` may not reach the class model.
     */
    private fun buildNewArray(address: Int, instruction: Instruction, classIndex: ClassIndex): NewArrayOp {
        val registers = instruction as TwoRegisterInstruction
        val descriptor = ((instruction as ReferenceInstruction).reference as TypeReference).type
        val arrayType = classIndex.typeOf(descriptor)
        return NewArrayOp(
            address, instruction.opcode.name,
            destRegister = registers.registerA,
            lengthRegister = registers.registerB,
            arrayType = arrayType,
            loadable = classIndex.isLoadable(arrayType),
        )
    }

    /**
     * `new-instance` (21c): one register, one type reference. Joins [buildNewArray]'s shape exactly --
     * the type operand resolved and its loadability decided HERE, at decode time, for the family's two
     * reasons (loadability is a static fact about the instruction; `core/op/` may not reach the class
     * model). An unloadable class makes the op throw a guest `NoClassDefFoundError` at execute --
     * `new-instance`'s own row of the resolution-failure family, measured on both interpreter tiers by
     * Task 16 Step 0's `Fx_NewInstanceAbsentType` (the plan's Task 15 Step 4 deferred this row to the
     * task that lands the opcode). The class-init trigger is the OP's business, not this decoder's: it
     * is per-path state (`Frame.classInitAction`), where loadability is not.
     */
    private fun buildNewInstance(address: Int, instruction: Instruction, classIndex: ClassIndex): NewInstanceOp {
        val descriptor = ((instruction as ReferenceInstruction).reference as TypeReference).type
        val instanceType = classIndex.typeOf(descriptor)
        val fields = classIndex.instanceFieldDefaults(instanceType)
        return NewInstanceOp(
            address, instruction.opcode.name,
            destRegister = (instruction as OneRegisterInstruction).registerA,
            instanceType = instanceType,
            loadable = fields != null,
            fields = fields.orEmpty(),
        )
    }

    /**
     * The type operand is resolved -- and its loadability decided -- HERE, at decode time, exactly as
     * `new-array`'s is, for the same two reasons: loadability is a static fact about the instruction, and
     * `core/op/` may not reach the class model. **And it means exactly what `new-array`'s means: an absent
     * class is a guest `NoClassDefFoundError` thrown at EXECUTE time, before the register is read** ([ART]
     * resolves at `interpreter_switch_impl-inl.h:838-843` and reads the register only at `:847`).
     *
     * 🔴 **Plan 9 Task 15 (ROW-23) reversed Plan 8 here.** This function used to return a per-address
     * [UnsupportedOp] for an unloadable operand, on the stated grounds that a definite throw would commit to
     * an [ART]-only exception type. That premise is gone -- the type is now measured on both interpreter
     * tiers, `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` and `[DEVICE](Tier B, nterp default,
     * API 36)` -- and the refusal cost a whole-method `Partial` to avoid answering one address. All five
     * resolving opcodes now agree; see [org.cf.smalivm.core.op.resolutionFailure] for the family and
     * [InstanceOfOp]'s KDoc for the retracted argument, kept rather than deleted.
     *
     * **One consequence to state rather than leave inferable.** `ClassIndex.isInstanceOf`'s row 4 answers
     * `Cloneable`/`Serializable` for an array by comparing descriptor STRINGS, deliberately so that
     * neither interface has to be in the loaded set. That property survives for the **candidate** (an
     * array of an absent component class still answers here) but not for the **target**: an
     * `instance-of vA, vB, Labsent;` never reaches the predicate at all. On a device the two interfaces
     * always resolve, so this costs nothing real -- and it is measured rather than assumed:
     * `java/lang/Cloneable.smali` and `java/io/Serializable.smali` both ship in the framework the tests
     * load.
     */
    private fun buildInstanceOf(
        address: Int,
        instruction: Instruction,
        classIndex: ClassIndex,
        hierarchy: HierarchySnapshot,
    ): Op {
        val registers = instruction as TwoRegisterInstruction
        val descriptor = ((instruction as ReferenceInstruction).reference as TypeReference).type
        val targetType = classIndex.typeOf(descriptor)
        return InstanceOfOp(
            address, instruction.opcode.name,
            destRegister = registers.registerA,
            sourceRegister = registers.registerB,
            targetType = targetType,
            types = hierarchy,
            // The ONE class-model question this op needs, answered here because `core/op/` may not ask it.
            // `false` makes the op throw a guest `NoClassDefFoundError` at execute -- it does NOT refuse the
            // instruction (Plan 9 Task 15's reversal of Plan 8's `UnsupportedOp`; see [InstanceOfOp]'s KDoc).
            loadable = classIndex.isLoadable(targetType),
        )
    }

    /**
     * `check-cast`'s twin of [buildInstanceOf], and a SEPARATE function rather than a shared one: the two
     * formats differ (`21c` names one register, `22c` two), so folding them would mean a cast reading
     * `registerB` off an instruction that has none. What they do share -- resolve the type operand here, and
     * hand the op a `loadable` flag so an absent one throws a guest `NoClassDefFoundError` at execute -- is
     * one ruling applied twice rather than two rulings, and the argument for it lives on [buildInstanceOf].
     *
     * `check-cast`'s stakes are the higher of the two, which is why the behaviour is pinned separately by
     * `OpBuilderTest.aCheckCastAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`: an absent type
     * would make `instance-of` fold a boolean, while `check-cast` would have to choose between inventing a
     * definite `ClassCastException` and inventing a definite success -- and the measured device answer is
     * neither of those, nor the whole-method `Partial` Plan 8 chose, but a definite linking `Error`.
     */
    private fun buildCheckCast(
        address: Int,
        instruction: Instruction,
        classIndex: ClassIndex,
        hierarchy: HierarchySnapshot,
    ): Op {
        val register = instruction as OneRegisterInstruction
        val descriptor = ((instruction as ReferenceInstruction).reference as TypeReference).type
        val castType = classIndex.typeOf(descriptor)
        return CheckCastOp(
            address, instruction.opcode.name,
            targetRegister = register.registerA,
            castType = castType,
            types = hierarchy,
            throwableHierarchy = hierarchy.throwableHierarchy,
            loadable = classIndex.isLoadable(castType),
        )
    }

    /**
     * `sget*` (60-66, format 21c): one destination register, one field reference.
     *
     * **The field reference is RESOLVED here, at decode time** -- `ClassIndex.resolveStaticField` walks the
     * superclass chain and hands back the DECLARING class plus a fully-qualified descriptor, so
     * `core/op/` receives core [VirtualType]s and a `String` and never a `FieldReference` or a
     * `org.cf.smalivm.type.VirtualField` (the two-tier dependency rule). It joins the resolve-at-decode-time
     * group [build]'s KDoc describes, alongside `new-array`/`new-instance`/`instance-of`/`check-cast`/
     * `const-class`: which field an instruction names is a static fact about the instruction.
     *
     * **A failed resolution builds a THROWING op, never an [UnsupportedOp] and never a host crash.** Both
     * failures are guest errors thrown at execute time (see [org.cf.smalivm.core.op.resolutionFailure] and
     * `noSuchFieldFailure`), which is Plan 9 Task 15's ruling for the whole family and the reversal of Plan 8's
     * refusals. The oracle instead crashes the HOST here: `classManager.getVirtualClass(...)` raises for an
     * absent class (`type/ClassManager.kt:191-198`) and `getField(name)!!` NPEs for a missing member
     * (`opcode/SGetOp.kt:51-52`), killing the run rather than the path.
     *
     * The class-init trigger is the OP's business, not this decoder's: it is per-path state
     * (`Frame.classInitAction`), where resolution is not -- exactly the split [buildNewInstance] states.
     */
    private fun buildSGet(address: Int, instruction: Instruction, classIndex: ClassIndex): SGetOp =
        SGetOp(
            address, instruction.opcode.name,
            destRegister = (instruction as OneRegisterInstruction).registerA,
            field = resolveField(instruction, classIndex),
        )

    /**
     * `sput*` (67-6d, format 21c): the mirror of [buildSGet], with `registerA` the **value** register rather
     * than a destination -- so nothing here decides a width and [SPutOp] never calls
     * `Op.destinationRegisters`.
     *
     * The one semantic fact the mnemonic carries is decoded here rather than at execution time, per the ruling
     * on [buildAPut]: `sput-object` is the single form whose value operand sits in a reference context, and
     * therefore the single form that coerces a primitive-tagged register to `null`. See
     * [SPutOp.storesAReference].
     */
    private fun buildSPut(address: Int, instruction: Instruction, classIndex: ClassIndex): SPutOp =
        SPutOp(
            address, instruction.opcode.name,
            valueRegister = (instruction as OneRegisterInstruction).registerA,
            field = resolveField(instruction, classIndex),
            storesAReference = instruction.opcode == Opcode.SPUT_OBJECT,
        )

    /**
     * The `21c` field operand, resolved -- shared by [buildSGet] and [buildSPut] so a read and a write of one
     * instruction shape cannot resolve to two different cells.
     *
     * All three components of the dex reference are used: the defining class, the NAME and the TYPE. Passing
     * the type through is what stops two same-named statics of different types in one hierarchy from
     * collapsing into one cell, which the oracle's name-only lookup does
     * (`type/VirtualClass.java:83-98`, `docs/architecture/type-and-dex.md:116`).
     */
    private fun resolveField(instruction: Instruction, classIndex: ClassIndex): StaticFieldLookup {
        val reference = (instruction as ReferenceInstruction).reference as FieldReference
        return classIndex.resolveStaticField(
            classIndex.typeOf(reference.definingClass), reference.name, reference.type,
        )
    }

    private fun buildIGet(address: Int, instruction: Instruction, classIndex: ClassIndex): IGetOp {
        val registers = instruction as TwoRegisterInstruction
        return IGetOp(
            address,
            instruction.opcode.name,
            destRegister = registers.registerA,
            receiverRegister = registers.registerB,
            field = resolveInstanceField(instruction, classIndex),
        )
    }

    private fun buildIPut(address: Int, instruction: Instruction, classIndex: ClassIndex): IPutOp {
        val registers = instruction as TwoRegisterInstruction
        return IPutOp(
            address,
            instruction.opcode.name,
            valueRegister = registers.registerA,
            receiverRegister = registers.registerB,
            field = resolveInstanceField(instruction, classIndex),
            storesAReference = instruction.opcode == Opcode.IPUT_OBJECT,
        )
    }

    private fun resolveInstanceField(instruction: Instruction, classIndex: ClassIndex): InstanceFieldLookup {
        val reference = (instruction as ReferenceInstruction).reference as FieldReference
        return classIndex.resolveInstanceField(
            classIndex.typeOf(reference.definingClass), reference.name, reference.type,
        )
    }

    private fun buildArrayLength(address: Int, instruction: Instruction): ArrayLengthOp {
        val registers = instruction as TwoRegisterInstruction
        return ArrayLengthOp(address, instruction.opcode.name, registers.registerA, registers.registerB)
    }

    /**
     * The suffix changes exactly ONE thing for the core: whether the destination is a wide pair. The
     * element's value and declared type come from the tracked array, not from the mnemonic -- which is why
     * a single op serves all seven. See [AGetOp]'s KDoc for the tiering of that choice.
     *
     * All three operands are genuinely distinct in real code (`vAA` = dest, `vBB` = array, `vCC` = index),
     * and the whole corpus fixture is `v0, v0, v1`, so the order is pinned by
     * `AGetOpTest.theThreeOperandsAreNotInterchangeable` against a three-distinct-register fixture rather
     * than left to a coincidence.
     */
    private fun buildAGet(address: Int, instruction: Instruction): AGetOp {
        val registers = instruction as ThreeRegisterInstruction
        return AGetOp(
            address, instruction.opcode.name,
            destRegister = registers.registerA,
            arrayRegister = registers.registerB,
            indexRegister = registers.registerC,
            isWide = instruction.opcode == Opcode.AGET_WIDE,
        )
    }

    /**
     * The mirror of [buildAGet], with one difference that is not a transcription: **`registerA` is the
     * VALUE register, not a destination**, so nothing here decides an `isWide` and [APutOp] never calls
     * `Op.destinationRegisters`.
     *
     * And the suffix means something else again. For `aget` it changes only whether the destination is a
     * wide pair; for `aput` it drives **value coercion** -- `aput-boolean` of `0x1` stores `true`,
     * `aput-char` of `36` stores `'$'`, `aput-object` of `0` stores `null`. That is a semantic difference
     * the mnemonic is the only carrier of, so it is resolved here into an [APutOp.StoreKind] rather than
     * re-derived from `name.endsWith(...)` at execution time: the enum makes [APutOp]'s own `when`
     * exhaustive, so an eighth `aput*` opcode cannot fall through to `PLAIN` silently.
     *
     * All three operands are genuinely distinct in real code AND in the corpus fixture (`aput v2, v0, v1`,
     * unlike `aget_test.smali`'s `v0, v0, v1`), and the order is pinned on values by
     * `APutOpTest.theThreeOperandsAreNotInterchangeable` plus the rendering row in `OpBuilderTest`.
     *
     * [hierarchy] is the one thing this decoder passes down UNRESOLVED, and deliberately: `aput-object`'s
     * `ArrayStoreException` depends on the runtime type of the value and the runtime component type of the
     * array, so unlike `new-array`'s loadability there is nothing here to decide. The narrow [TypeHierarchy]
     * interface is handed over rather than the whole [ClassIndex], so `core/op/` keeps naming only core
     * abstractions -- see [APutOp.hierarchy].
     *
     * [superclasses] is the same object under a second narrow interface, needed because this op JOINS
     * element values (Plan 8 Task 7 made [join] hierarchy-aware). Two parameters rather than one
     * [ClassIndex] keeps the pattern above: an op names the capability it uses, not the provider. They are
     * separate interfaces rather than one because [TypeHierarchy]'s KDoc forbids widening it -- its
     * non-totality contract is load-bearing for its own two consumers.
     */
    private fun buildAPut(
        address: Int,
        instruction: Instruction,
        hierarchy: TypeHierarchy,
        superclasses: Superclasses,
    ): APutOp {
        val registers = instruction as ThreeRegisterInstruction
        val storeKind = when (instruction.opcode) {
            Opcode.APUT -> APutOp.StoreKind.PLAIN
            Opcode.APUT_WIDE -> APutOp.StoreKind.WIDE
            Opcode.APUT_OBJECT -> APutOp.StoreKind.OBJECT
            Opcode.APUT_BOOLEAN -> APutOp.StoreKind.BOOLEAN
            Opcode.APUT_BYTE -> APutOp.StoreKind.BYTE
            Opcode.APUT_CHAR -> APutOp.StoreKind.CHAR
            Opcode.APUT_SHORT -> APutOp.StoreKind.SHORT
            // Unreachable through `build`, which dispatches on `in APUT_OPCODES`; an `error` rather than a
            // default kind so a new opcode added to that set without a coercion fails loudly here.
            else -> error("Not an aput opcode: ${instruction.opcode}")
        }
        return APutOp(
            address, instruction.opcode.name,
            valueRegister = registers.registerA,
            arrayRegister = registers.registerB,
            indexRegister = registers.registerC,
            storeKind = storeKind,
            hierarchy = hierarchy,
            superclasses = superclasses,
        )
    }

    /**
     * Decodes the payload HERE, at build time, which is what lets [FillArrayDataOp] do the whole fill in one
     * step and return `Continue` -- no jump to the pseudo-instruction, and none of the hidden control state
     * spec §5.2 names as a defect (the payload op being replaced read its return address and its target
     * register off its PARENT's state: `opcode/FillArrayDataPayloadOp.kt`'s
     * `getPsuedoInstructionReturnAddress()` and `parent.registersAssigned.toList()[0]`). This is also the
     * only place an `ArrayPayload` may be named at all -- `core/op/` may not import `com.android.tools.smali.dexlib2`.
     *
     * A malformed payload target -- off the end, or pointing at something that is not `.array-data` -- makes
     * this an [UnsupportedOp] rather than a decode throwable: a decode failure inside `buildSuccessor`
     * becomes `Aborted(HOST_FAILURE)`, which is indistinguishable from an engine bug, while `UNSUPPORTED_OP`
     * says "this build cannot execute this instruction", which is the true statement about malformed
     * obfuscator output. Both make the run `Partial`, so no consumer trusts it either way.
     *
     * **`AbortReason.MALFORMED_INPUT` exists now and this must NOT be re-pointed at it**, tempting as the
     * name is. `UNSUPPORTED_OP` is the only reason `ExecutionGraph.sideEffectLevel` derives `STRONG` from,
     * and that is doing real work here: a real `fill-array-data` writes an array this build could not
     * identify, so its effect must err upward (spec §4.5). `MALFORMED_INPUT` declares no effect, correctly,
     * because it names an address that is not in the method at all -- see that member's own KDoc. Relabelling
     * would silently turn a refusal into "no side effects, safe to delete".
     *
     * [MethodView.hasAddress] is checked BEFORE [MethodView.instructionAt], which does a `requireNotNull`
     * that throws -- `CARRY-FORWARD.md`'s Plan 9 item 5 is the standing rule about exactly this pair, and
     * the brief's `takeIf`-after-the-call spelling would have evaluated the throwing call first.
     *
     * **DISCLOSURE: the `hasAddress` half is untestable insurance, and only the cast half is covered.**
     * `fillArrayDataWithMalformedPayloadTarget()V` -- the fixture behind
     * `OpBuilderTest.aFillArrayDataWhoseTargetIsNotAnArrayPayloadIsUnsupportedRatherThanAHostFailure` --
     * targets an address that DOES exist (a `return-void`), so that test dies on the unguarded cast alone.
     * An off-the-end payload target is not constructible from smali at all: dexlib resolves the operand to a
     * placed `Label`, so an unplaced or out-of-range target cannot be assembled. The check is kept because
     * Plan 9 item 5 mandates the ordering for every `hasAddress`/`instructionAt` pair, and because a
     * synthetic or edited `MethodView` (Plan 13) can produce one -- **not** because a mutation kills a test.
     */
    private fun buildFillArrayData(address: Int, instruction: Instruction, view: MethodView): Op {
        val unsupported = { UnsupportedOp(address, instruction.opcode.name) }
        val target = targetAddress(address, instruction, view)
        if (!view.hasAddress(target) || !view.isPayload(target)) return unsupported()
        val payload = view.payloadAt(target) as? ArrayPayload ?: return unsupported()
        return FillArrayDataOp(
            address, instruction.opcode.name,
            arrayRegister = (instruction as OneRegisterInstruction).registerA,
            elementWidth = payload.elementWidth,
            payloadElements = payload.arrayElements,
        )
    }

    /**
     * The SECOND branch to read [classIndex] for `new-array`'s reason: the component's loadability is a
     * static fact about the instruction's type reference, and `core/op/` may not reach the class model. It is
     * deliberately the SAME `ClassIndex.isLoadable` call and therefore the same answer -- design decision 4
     * is one ruling for both array-allocating opcodes, not two.
     *
     * **The register list is read through the shape INTERFACES rather than through a size ladder.** The
     * engine being replaced spelled the non-range form as `when (size) { 5 -> …; 4 -> …; 3 -> …; 2 -> …;
     * 1 -> … }` -- six near-identical branches assigning `registerC..registerG` in reverse, which is exactly
     * where a `registerF`/`registerG` swap hides and which no behavioral fixture could catch, because its
     * only non-range fixture named three registers. `FiveRegisterInstruction` exposes all five accessors and
     * `VariableRegisterInstruction.registerCount` says how many are live, so one `copyOf(count)` covers all
     * five shapes with no branch to get wrong. `RegisterRangeInstruction.startRegister` does the same for the
     * range form.
     *
     * **Two inputs decode to [UnsupportedOp] rather than to an op**, and both are the honest "this build
     * cannot execute this instruction" rather than a guess:
     *  - a `J`/`D` component. **SPEC** forbids it ([BC]: *"no arrays of long or double"*) and ART makes it a
     *    verifier HARD failure citing that very page, so no device ever reaches an interpreter for it.
     *  - a type reference that is not an array at all, violating [BC]'s *"The type must be an array type"*.
     *    There is no component to fill, so there is nothing to model.
     * Both make the run `Partial`, which is the correct trust level for a method a device would refuse to
     * run. Pinned by `OpBuilderTest.aFilledNewArrayWithNoFillableComponentTypeIsUnsupportedRatherThanAHostFailure`
     * -- the guard would otherwise be inert code, and `FilledNewArrayOp` relies on it for a `requireNotNull`.
     *
     * [isRange] is passed as a decoded boolean rather than left to a `name.endsWith("/range")` test inside
     * the op, per the ruling in [buildAPut]'s KDoc: a semantic fact the mnemonic carries is resolved here,
     * once, not re-derived at execution time.
     */
    private fun buildFilledNewArray(address: Int, instruction: Instruction, classIndex: ClassIndex): Op {
        val unsupported = { UnsupportedOp(address, instruction.opcode.name) }
        val descriptor = ((instruction as ReferenceInstruction).reference as TypeReference).type
        val arrayType = classIndex.typeOf(descriptor)
        val component = arrayType.componentType
        if (component == null || component.isWide) return unsupported()
        val count = (instruction as VariableRegisterInstruction).registerCount
        val elementRegisters = when (instruction) {
            is RegisterRangeInstruction -> IntArray(count) { instruction.startRegister + it }
            is FiveRegisterInstruction -> {
                val five = intArrayOf(
                    instruction.registerC, instruction.registerD, instruction.registerE,
                    instruction.registerF, instruction.registerG,
                )
                // `35c` names exactly five register fields, so a count above five is malformed rather than
                // merely unusual. `copyOf` would pad it with register 0 SILENTLY, inventing reads of a
                // register the instruction never named -- refusing the instruction is the honest answer, and
                // it is the same answer a malformed `fill-array-data` target gets.
                if (count > five.size) return unsupported()
                five.copyOf(count)
            }
            else -> return unsupported()
        }
        return FilledNewArrayOp(
            address, instruction.opcode.name,
            elementRegisters = elementRegisters,
            arrayType = arrayType,
            loadable = classIndex.isLoadable(arrayType),
            isRange = instruction.opcode == Opcode.FILLED_NEW_ARRAY_RANGE,
        )
    }

    private fun buildInvoke(address: Int, instruction: Instruction, classIndex: ClassIndex): InvokeOp {
        val reference = (instruction as ReferenceInstruction).reference as MethodReference
        val count = (instruction as VariableRegisterInstruction).registerCount
        val argumentRegisters = when (instruction) {
            is RegisterRangeInstruction -> IntArray(count) { instruction.startRegister + it }
            is FiveRegisterInstruction -> {
                val five = intArrayOf(
                    instruction.registerC, instruction.registerD, instruction.registerE,
                    instruction.registerF, instruction.registerG,
                )
                five.copyOf(count)
            }
            else -> error("Unsupported invoke instruction shape: ${instruction::class.java.name}")
        }
        val kind = when (instruction.opcode) {
            Opcode.INVOKE_STATIC, Opcode.INVOKE_STATIC_RANGE -> InvokeKind.STATIC
            Opcode.INVOKE_DIRECT, Opcode.INVOKE_DIRECT_RANGE -> InvokeKind.DIRECT
            Opcode.INVOKE_SUPER, Opcode.INVOKE_SUPER_RANGE -> InvokeKind.SUPER
            Opcode.INVOKE_VIRTUAL, Opcode.INVOKE_VIRTUAL_RANGE -> InvokeKind.VIRTUAL
            Opcode.INVOKE_INTERFACE, Opcode.INVOKE_INTERFACE_RANGE -> InvokeKind.INTERFACE
            else -> error("Not a standard invoke opcode: ${instruction.opcode}")
        }
        val proto = "(${reference.parameterTypes.joinToString("")})${reference.returnType}"
        return InvokeOp(
            address,
            instruction.opcode.name,
            MethodDescriptor(classIndex.typeOf(reference.definingClass), reference.name, proto),
            argumentRegisters,
            kind,
        )
    }

    private fun buildMonitorEnter(address: Int, instruction: Instruction): MonitorEnterOp =
        MonitorEnterOp(address, (instruction as OneRegisterInstruction).registerA)

    private fun buildMonitorExit(address: Int, instruction: Instruction): MonitorExitOp =
        MonitorExitOp(address, (instruction as OneRegisterInstruction).registerA)

    /** Format23x backs all five compare opcodes: one three-register shape, narrow `I` result always. */
    private fun buildCmp(address: Int, instruction: Instruction): CmpOp {
        val registers = instruction as ThreeRegisterInstruction
        return CmpOp(address, instruction.opcode.name, registers.registerA, registers.registerB, registers.registerC)
    }

    // A const opcode states only its literal's width: one narrow form serves int/char/byte/short/float and
    // one wide form serves long/double. So the raw is the literal's bit pattern, an Int under `I` for the
    // narrow forms and a Long under `J` for every wide form, and a float or double reader reinterprets those
    // bits through `Coerce`.
    private val INT = VirtualType.of("I")
    private val LONG = VirtualType.of("J")
    private val DOUBLE = VirtualType.of("D")
    private val STRING = VirtualType.of("Ljava/lang/String;")
    private val FLOAT = VirtualType.of("F")
    private val BYTE = VirtualType.of("B")
    private val CHAR = VirtualType.of("C")
    private val SHORT = VirtualType.of("S")
    // The declared tag of a `const-class` value. The RAW is the resolved host `Class`; see [buildConstClass].
    private val CLASS = VirtualType.of("Ljava/lang/Class;")

    // The dispatch table, named rather than spelled as `when` branch labels. A branch label can
    // only be deleted *silently*: the opcode falls to `else`, becomes an `UnsupportedOp`, and emits
    // an `Abort` byte-for-byte identical to a deliberately unsupported opcode's -- so every method
    // using it aborts forever with no signal, and a fixture-driven test can never notice unless the
    // fixture happens to contain that opcode. As sets they are data, and
    // `supportedOpcodes` below is asserted against a literal transcription of this table in
    // `OpBuilderTest`, so a deletion fails a test no matter what any fixture contains.
    // Single-opcode families get sets too, so that the invariant covers every branch with no gaps.
    // The sets are disjoint, so `build`'s branch order is presentation only -- but keep the wide
    // const guards ahead of the narrow ones anyway: `Instruction31i` (`const-wide/32`) implements
    // `NarrowLiteralInstruction`, so a wide const reaching the narrow branch would truncate to 32
    // bits and mistag as `I` without throwing.
    private val NOP_OPCODES = setOf(Opcode.NOP)
    private val THROW_OPCODES = setOf(Opcode.THROW)
    private val GOTO_OPCODES = setOf(Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32)

    /**
     * All twelve `if-*` opcodes, both shapes in one set because both build the same op. Ordered
     * `22t` then `21t` so the pairing is readable; `build` dispatches on membership, not on order.
     */
    private val IF_OPCODES = setOf(
        Opcode.IF_EQ, Opcode.IF_NE, Opcode.IF_LT, Opcode.IF_GE, Opcode.IF_GT, Opcode.IF_LE,
        Opcode.IF_EQZ, Opcode.IF_NEZ, Opcode.IF_LTZ, Opcode.IF_GEZ, Opcode.IF_GTZ, Opcode.IF_LEZ,
    )
    /**
     * The two `31t` switches. **The two PAYLOAD opcodes are deliberately not here** -- they are data, not
     * code ([CON] B22, and [MethodView.PAYLOAD_OPCODES] is the one shared set that says which opcodes are
     * payloads), so they must stay unsupported for the same reason `ARRAY_PAYLOAD` does: dispatching one
     * would give a payload an executable meaning, which is exactly what decoding the table at build time
     * removes. `OpBuilderTest` asserts their absence positively.
     */
    private val SWITCH_OPCODES = setOf(Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH)
    private val RETURN_OPCODES =
        setOf(Opcode.RETURN_VOID, Opcode.RETURN, Opcode.RETURN_WIDE, Opcode.RETURN_OBJECT)
    private val MOVE_OPCODES = setOf(
        Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16,
        Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16,
        Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16,
        Opcode.MOVE_EXCEPTION,
        Opcode.MOVE_RESULT, Opcode.MOVE_RESULT_WIDE, Opcode.MOVE_RESULT_OBJECT,
    )

    /**
     * The subset of [MOVE_OPCODES] whose destination holds a wide (`J`/`D`) value and therefore owns
     * two registers. Read by [buildMove] alone, and the reason it is a named set rather than an
     * inline mnemonic test: the pair's high half is invisible in every value query
     * ([Frame.peek] answers null for it), so omitting one opcode here is a silent unsoundness in
     * [ExecutionGraph.availableRegisters] rather than a visibly wrong answer.
     */
    private val WIDE_MOVE_OPCODES = setOf(
        Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16, Opcode.MOVE_RESULT_WIDE,
    )
    private val CONST_OPCODES = setOf(
        Opcode.CONST, Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST_HIGH16,
        Opcode.CONST_WIDE, Opcode.CONST_WIDE_16, Opcode.CONST_WIDE_32, Opcode.CONST_WIDE_HIGH16,
        Opcode.CONST_STRING, Opcode.CONST_STRING_JUMBO,
    )

    /**
     * `const-class`, its OWN single-opcode family and `build` branch -- deliberately NOT folded into
     * [CONST_OPCODES]. It is the one `const*` form that resolves a type operand through the class model, so
     * it takes the same shape as [INSTANCE_OF_OPCODES]/[CHECK_CAST_OPCODES] (its own branch, needing
     * [ClassIndex]) rather than [buildConst]'s literal decode. See [buildConstClass]; the fifteenth and last
     * op family this plan adds (`CARRY-FORWARD.md` Plan 8 item 10 is this branch's own coverage).
     */
    private val CONST_CLASS_OPCODES = setOf(Opcode.CONST_CLASS)
    private val UNARY_MATH_OPCODES = setOf(
        Opcode.NEG_INT, Opcode.NOT_INT, Opcode.NEG_LONG, Opcode.NOT_LONG,
        Opcode.NEG_FLOAT, Opcode.NEG_DOUBLE,
        Opcode.INT_TO_LONG, Opcode.INT_TO_FLOAT, Opcode.INT_TO_DOUBLE,
        Opcode.LONG_TO_INT, Opcode.LONG_TO_FLOAT, Opcode.LONG_TO_DOUBLE,
        Opcode.FLOAT_TO_INT, Opcode.FLOAT_TO_LONG, Opcode.FLOAT_TO_DOUBLE,
        Opcode.DOUBLE_TO_INT, Opcode.DOUBLE_TO_LONG, Opcode.DOUBLE_TO_FLOAT,
        Opcode.INT_TO_BYTE, Opcode.INT_TO_CHAR, Opcode.INT_TO_SHORT,
    )
    private val CMP_OPCODES = setOf(
        Opcode.CMP_LONG, Opcode.CMPL_FLOAT, Opcode.CMPG_FLOAT, Opcode.CMPL_DOUBLE, Opcode.CMPG_DOUBLE,
    )
    private val NEW_ARRAY_OPCODES = setOf(Opcode.NEW_ARRAY)
    private val NEW_INSTANCE_OPCODES = setOf(Opcode.NEW_INSTANCE)
    private val ARRAY_LENGTH_OPCODES = setOf(Opcode.ARRAY_LENGTH)
    private val AGET_OPCODES = setOf(
        Opcode.AGET, Opcode.AGET_WIDE, Opcode.AGET_OBJECT,
        Opcode.AGET_BOOLEAN, Opcode.AGET_BYTE, Opcode.AGET_CHAR, Opcode.AGET_SHORT,
    )
    private val APUT_OPCODES = setOf(
        Opcode.APUT, Opcode.APUT_WIDE, Opcode.APUT_OBJECT,
        Opcode.APUT_BOOLEAN, Opcode.APUT_BYTE, Opcode.APUT_CHAR, Opcode.APUT_SHORT,
    )

    /**
     * The `ARRAY_PAYLOAD` pseudo-opcode is deliberately NOT here. It is data, not code
     * ([MethodView.payloadAddresses]), so it must stay unsupported: dispatching it would give the payload an
     * executable meaning, which is the whole thing design decision 3 removes.
     */
    private val FILL_ARRAY_DATA_OPCODES = setOf(Opcode.FILL_ARRAY_DATA)

    /**
     * Both spellings dispatch to one op: the range form differs only in how its register list is encoded
     * (and in how [FilledNewArrayOp.toString] spells the braces), never in what it does.
     */
    private val FILLED_NEW_ARRAY_OPCODES = setOf(Opcode.FILLED_NEW_ARRAY, Opcode.FILLED_NEW_ARRAY_RANGE)

    /**
     * The two type-test opcodes, in TWO named sets rather than one "type ops" set, because they share a
     * predicate ([org.cf.smalivm.core.castOutcome]) and not their answers: three of its five outcomes map
     * differently, a cast can FORK where an instance test never does, and only the cast re-tags its operand.
     * One set would invite one `build` branch, and one branch would give `check-cast` `instance-of`'s
     * semantics -- a mis-wire the behavioral tests catch (`CheckCastOpTest`/`InstanceOfOpTest` build and
     * execute each op through `CoreTester`) along with the `is CheckCastOp`/`is InstanceOfOp` controls in
     * `OpBuilderTest`'s absent-type decode tests. The two membership assertions do NOT catch it:
     * membership in `supportedOpcodes` is independent of the `when` dispatch, so both stay green even if
     * `check-cast` decoded to an `InstanceOfOp`.
     */
    private val INSTANCE_OF_OPCODES = setOf(Opcode.INSTANCE_OF)
    private val CHECK_CAST_OPCODES = setOf(Opcode.CHECK_CAST)

    /**
     * The seven `sget*` and seven `sput*` forms, in TWO named sets for [INSTANCE_OF_OPCODES]/[CHECK_CAST_OPCODES]'s
     * reason: they share a preamble (resolution, then class init) and not their answers -- one assigns a
     * register and declares no effect, the other reads one and declares `WEAK`. One set would invite one
     * `build` branch, and one branch would give `sput` `sget`'s semantics, which is a mis-wire the membership
     * assertions in `OpBuilderTest` cannot see (membership is independent of the `when` dispatch) and the
     * behavioral tests can.
     *
     * **The `*_VOLATILE` / `*_JUMBO` ODEX forms are deliberately absent.** They are optimized-dex opcodes,
     * never present in an APK's `classes.dex` and never emitted by the assembler these fixtures go through, so
     * dispatching them would claim coverage no input can exercise. They stay [UnsupportedOp] -- which makes a
     * run `Partial` rather than wrong, the correct trust level for an instruction this build has never seen.
     */
    private val SGET_OPCODES = setOf(
        Opcode.SGET, Opcode.SGET_WIDE, Opcode.SGET_OBJECT,
        Opcode.SGET_BOOLEAN, Opcode.SGET_BYTE, Opcode.SGET_CHAR, Opcode.SGET_SHORT,
    )
    private val SPUT_OPCODES = setOf(
        Opcode.SPUT, Opcode.SPUT_WIDE, Opcode.SPUT_OBJECT,
        Opcode.SPUT_BOOLEAN, Opcode.SPUT_BYTE, Opcode.SPUT_CHAR, Opcode.SPUT_SHORT,
    )
    /** The seven read and seven write instance-field forms. ODEX quick/volatile forms remain unsupported. */
    private val IGET_OPCODES = setOf(
        Opcode.IGET, Opcode.IGET_WIDE, Opcode.IGET_OBJECT,
        Opcode.IGET_BOOLEAN, Opcode.IGET_BYTE, Opcode.IGET_CHAR, Opcode.IGET_SHORT,
    )
    private val IPUT_OPCODES = setOf(
        Opcode.IPUT, Opcode.IPUT_WIDE, Opcode.IPUT_OBJECT,
        Opcode.IPUT_BOOLEAN, Opcode.IPUT_BYTE, Opcode.IPUT_CHAR, Opcode.IPUT_SHORT,
    )
    private val INVOKE_OPCODES = setOf(
        Opcode.INVOKE_STATIC, Opcode.INVOKE_STATIC_RANGE,
        Opcode.INVOKE_DIRECT, Opcode.INVOKE_DIRECT_RANGE,
        Opcode.INVOKE_SUPER, Opcode.INVOKE_SUPER_RANGE,
        Opcode.INVOKE_VIRTUAL, Opcode.INVOKE_VIRTUAL_RANGE,
        Opcode.INVOKE_INTERFACE, Opcode.INVOKE_INTERFACE_RANGE,
    )
    // Separate sets and build branches keep enter/exit identity observable even though their current
    // ownership-free behavior is the same.
    private val MONITOR_ENTER_OPCODES = setOf(Opcode.MONITOR_ENTER)
    private val MONITOR_EXIT_OPCODES = setOf(Opcode.MONITOR_EXIT)

    /**
     * All four domains. `float`/`double` carry only five operators each (`add`/`sub`/`mul`/`div`/`rem`
     * -- Dalvik has no logical or shift form for them), which is why [BinaryMathOp.floatingResult]'s
     * `else` branch is unreachable through this table rather than merely undertested.
     */
    private val BINARY_MATH_OPCODES = setOf(
        Opcode.ADD_INT, Opcode.ADD_INT_2ADDR, Opcode.ADD_INT_LIT8, Opcode.ADD_INT_LIT16,
        Opcode.SUB_INT, Opcode.SUB_INT_2ADDR,
        Opcode.RSUB_INT, Opcode.RSUB_INT_LIT8,
        Opcode.MUL_INT, Opcode.MUL_INT_2ADDR, Opcode.MUL_INT_LIT8, Opcode.MUL_INT_LIT16,
        Opcode.DIV_INT, Opcode.DIV_INT_2ADDR, Opcode.DIV_INT_LIT8, Opcode.DIV_INT_LIT16,
        Opcode.REM_INT, Opcode.REM_INT_2ADDR, Opcode.REM_INT_LIT8, Opcode.REM_INT_LIT16,
        Opcode.AND_INT, Opcode.AND_INT_2ADDR, Opcode.AND_INT_LIT8, Opcode.AND_INT_LIT16,
        Opcode.OR_INT, Opcode.OR_INT_2ADDR, Opcode.OR_INT_LIT8, Opcode.OR_INT_LIT16,
        Opcode.XOR_INT, Opcode.XOR_INT_2ADDR, Opcode.XOR_INT_LIT8, Opcode.XOR_INT_LIT16,
        Opcode.SHL_INT, Opcode.SHL_INT_2ADDR, Opcode.SHL_INT_LIT8,
        Opcode.SHR_INT, Opcode.SHR_INT_2ADDR, Opcode.SHR_INT_LIT8,
        Opcode.USHR_INT, Opcode.USHR_INT_2ADDR, Opcode.USHR_INT_LIT8,
        Opcode.ADD_LONG, Opcode.ADD_LONG_2ADDR, Opcode.SUB_LONG, Opcode.SUB_LONG_2ADDR,
        Opcode.MUL_LONG, Opcode.MUL_LONG_2ADDR, Opcode.DIV_LONG, Opcode.DIV_LONG_2ADDR,
        Opcode.REM_LONG, Opcode.REM_LONG_2ADDR, Opcode.AND_LONG, Opcode.AND_LONG_2ADDR,
        Opcode.OR_LONG, Opcode.OR_LONG_2ADDR, Opcode.XOR_LONG, Opcode.XOR_LONG_2ADDR,
        Opcode.SHL_LONG, Opcode.SHL_LONG_2ADDR, Opcode.SHR_LONG, Opcode.SHR_LONG_2ADDR,
        Opcode.USHR_LONG, Opcode.USHR_LONG_2ADDR,
        Opcode.ADD_FLOAT, Opcode.ADD_FLOAT_2ADDR, Opcode.SUB_FLOAT, Opcode.SUB_FLOAT_2ADDR,
        Opcode.MUL_FLOAT, Opcode.MUL_FLOAT_2ADDR, Opcode.DIV_FLOAT, Opcode.DIV_FLOAT_2ADDR,
        Opcode.REM_FLOAT, Opcode.REM_FLOAT_2ADDR,
        Opcode.ADD_DOUBLE, Opcode.ADD_DOUBLE_2ADDR, Opcode.SUB_DOUBLE, Opcode.SUB_DOUBLE_2ADDR,
        Opcode.MUL_DOUBLE, Opcode.MUL_DOUBLE_2ADDR, Opcode.DIV_DOUBLE, Opcode.DIV_DOUBLE_2ADDR,
        Opcode.REM_DOUBLE, Opcode.REM_DOUBLE_2ADDR,
    )

    /**
     * Every opcode [build] decodes to a real op; anything else becomes an [UnsupportedOp]. Exposed
     * only so the dispatch-coverage test can pin it -- the individual family sets stay private.
     * Declared after them because an object's properties initialize in declaration order.
     */
    internal val supportedOpcodes: Set<Opcode> =
        NOP_OPCODES + THROW_OPCODES + GOTO_OPCODES + IF_OPCODES + SWITCH_OPCODES +
            RETURN_OPCODES + MOVE_OPCODES + CONST_OPCODES + CONST_CLASS_OPCODES +
            UNARY_MATH_OPCODES + BINARY_MATH_OPCODES + CMP_OPCODES + NEW_ARRAY_OPCODES +
            NEW_INSTANCE_OPCODES + ARRAY_LENGTH_OPCODES + AGET_OPCODES + APUT_OPCODES + FILL_ARRAY_DATA_OPCODES +
            FILLED_NEW_ARRAY_OPCODES + INSTANCE_OF_OPCODES + CHECK_CAST_OPCODES +
            SGET_OPCODES + SPUT_OPCODES + IGET_OPCODES + IPUT_OPCODES + INVOKE_OPCODES +
            MONITOR_ENTER_OPCODES + MONITOR_EXIT_OPCODES
}
