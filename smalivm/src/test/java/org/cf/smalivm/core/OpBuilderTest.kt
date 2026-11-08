package org.cf.smalivm.core

import org.cf.smalivm.core.op.CheckCastOp
import org.cf.smalivm.core.op.ConstOp
import org.cf.smalivm.core.op.ConstClassOp
import org.cf.smalivm.core.op.GotoOp
import org.cf.smalivm.core.op.InstanceOfOp
import org.cf.smalivm.core.op.InvokeOp
import org.cf.smalivm.core.op.MoveOp
import org.cf.smalivm.core.op.NopOp
import org.cf.smalivm.core.op.ReturnOp
import org.cf.smalivm.core.op.SGetOp
import org.cf.smalivm.core.op.SPutOp
import org.cf.smalivm.core.op.SwitchOp
import org.cf.smalivm.core.op.ThrowOp
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.Opcodes
import com.android.tools.smali.dexlib2.iface.instruction.formats.Instruction35c
import com.android.tools.smali.dexlib2.writer.builder.DexBuilder
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.io.File

/**
 * Bug class: ops that carried hidden control state and signalled successors through an untyped
 * command bag, so "what happens next" was not inspectable (spec §4.4, §5.2). Oracle: Dalvik
 * semantics for each opcode plus spec §4.4's transition model. Each op is driven directly here so
 * a decode or transition error is localized before the scheduler is involved (Task 5).
 */
class OpBuilderTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val int = VirtualType.of("I")
    private val long = VirtualType.of("J")
    private val FILLED_NEW_ARRAY = "Lfilled_new_array_test;"
    private val INSTANCE_OF = "Linstanceof_test;"
    private val CHECK_CAST = "Lcheck_cast_test;"
    private val CONST = "Lconst_test;"
    private val SGET = "Lsget_test;"
    private val SPUT = "Lsput_test;"
    private val IGET = "Liget_test;"
    private val IPUT = "Liput_test;"

    /** [className] defaults to the fixture every other test in this file drives. */
    private fun view(descriptor: String, className: String = "Lspine/spine;") =
        classIndex.requireMethodView(className, descriptor)

    private fun op(descriptor: String, address: Int) = OpBuilder.build(address, view(descriptor), classIndex)

    /**
     * [op], generalized to an arbitrary fixture class -- the arithmetic families' toString() test below
     * is the only caller that needs a class other than `Lspine/spine;`.
     */
    private fun opAt(className: String, methodDescriptor: String, address: Int): Op =
        OpBuilder.build(address, view(methodDescriptor, className), classIndex)

    private fun executeSingle(op: Op, frame: Frame): OpArm {
        val arm = op.execute(frame).arms.single()
        val applied = arm.update.applyTo(frame)
        applied.result?.let(frame::setResult)
        if (arm is OpArm.MoveFromSlot) {
            val value = when (arm.source) {
                SpecialSlotSource.RESULT -> frame.takeResult()
                SpecialSlotSource.CAUGHT -> frame.takeCaught()
            } ?: Value.Top
            frame.assign(arm.register, value)
        }
        return arm
    }

    private fun throwType(arm: OpArm.Throw): VirtualType = when (val evidence = arm.evidence) {
        is ThrowEvidence.Existing -> requireNotNull(evidence.value.type)
        is ThrowEvidence.Fresh -> evidence.recipe.kind.type
    }

    @Test
    fun constAssignsItsLiteralAndContinues() {
        // Bug class: a const whose literal is decoded at execution time (or not at all), so the
        // register is left unassigned or holds the wrong operand. Oracle: Dalvik's `const/4 vA, #+B`
        // writes the sign-extended literal to vA and falls through; the fixture's literal is 0x5.
        val v = view("returnFive()I")
        val frame = Frame.entryFrame(v)
        val op = op("returnFive()I", 0)
        assertTrue(op is ConstOp)
        assertTrue(executeSingle(op, frame) is OpArm.Continue)
        assertEquals(5, (frame.peek(0) as Value.Known).raw)
        assertEquals(setOf(0), op.registersAssigned)
    }

    @Test
    fun constWideAssignsAWideValue() {
        // Bug class: a wide literal decoded through the narrow accessor, truncating the top 32 bits,
        // and a wide value that does not reserve its register pair so the high half reads back as an
        // independent value. Oracle: Dalvik's `const-wide vAA, #+BBBBBBBBBBBBBBBB` writes a 64-bit
        // literal to the pair (vAA, vAA+1); spec §4.2 makes the high half unreadable.
        val v = view("returnWide()J")
        val frame = Frame.entryFrame(v)
        assertTrue(executeSingle(op("returnWide()J", 0), frame) is OpArm.Continue)
        val value = frame.peek(0) as Value.Known
        assertEquals(0x123456789L, value.raw)
        assertTrue(value.type.isWide)
        assertEquals(Value.Top, frame.read(1), "the wide high half is not independently readable")
    }

    @Test
    fun constStringAssignsTheStringLiteral() {
        // Bug class: a const-string decoded through the wrong accessor, so the register holds a
        // dexlib reference object (or its debug rendering, quotes and all) instead of the string.
        // Oracle: Dalvik's `const-string vAA, string@BBBB` puts a String reference in vAA; the
        // fixture's literal is "spine".
        val v = view("returnString()Ljava/lang/String;")
        val frame = Frame.entryFrame(v)
        assertTrue(executeSingle(op("returnString()Ljava/lang/String;", 0), frame) is OpArm.Continue)
        val value = frame.peek(0) as Value.Known
        assertEquals("spine", value.raw)
        assertEquals(VirtualType.of("Ljava/lang/String;"), value.type)
    }

    @Test
    fun constantFormsDecodeTheirLiteralUnderTheirWidthTag() {
        // Dalvik's const opcodes say only a literal's width: one narrow form serves int/char/byte/short/float
        // and one wide form serves long/double. So every narrow form is tagged I and every wide form J, and
        // readers that need a float or double read the bits through `Coerce`.
        //
        // Every case asserts the whole Value, raw included, because the raw is where a decode slip
        // shows up: dexlib's NarrowLiteralInstruction *extends* WideLiteralInstruction, so a wide
        // const routed to the narrow accessor truncates 64 bits to 32 and mistags as I WITHOUT
        // throwing. That trap is live for `const-wide/32`, which shares Format31i with the plain
        // `const` and so implements NarrowLiteralInstruction. `const-wide/high16` (Instruction21lh,
        // LongHatLiteralInstruction, NOT NarrowLiteralInstruction) would instead throw -- it fails
        // loudly, so /32 is the one that needs this test. A truncated wide also stops Frame from
        // reserving the pair's high half, since Frame keys that off type.isWide.
        //
        // All ten dispatched const opcodes are covered except `const-string/jumbo`, which needs a
        // dex with >65535 strings to assemble; its dispatch is pinned by the dispatch-table test.
        assertEquals(Value.Known(0x5, int), constantAt("returnFive()I", 0), "const/4")
        assertEquals(Value.Known(0x2a, int), constantAt("nopThenConst()I", 2), "const/16")
        assertEquals(Value.Known(0x42424242, int), constantAt("constThirtyTwo()I", 0), "const")
        assertEquals(Value.Known(0x42420000, int), constantAt("constHighSixteen()I", 0), "const/high16")
        assertEquals(
            Value.Known("spine", VirtualType.of("Ljava/lang/String;")),
            constantAt("returnString()Ljava/lang/String;", 0), "const-string",
        )
        assertEquals(
            Value.Known(0x123456789L, long), constantAt("returnWide()J", 0), "const-wide is tagged J",
        )
        assertEquals(
            Value.Known(0x2aL, long), constantAt("returnWideSixteen()J", 0), "const-wide/16 is tagged J",
        )
        assertEquals(
            Value.Known(0x1b7740L, long), constantAt("returnWideThirtyTwo()J", 0),
            "const-wide/32 is tagged J and keeps all 64 bits",
        )
        assertEquals(
            Value.Known(0x4242000000000000L, long), constantAt("returnWideHighSixteen()J", 0),
            "const-wide/high16 is tagged J",
        )
    }

    /**
     * An opcode without a decoder row decodes to an [UnsupportedOp] with no other signal, and most opcodes
     * appear in no fixture, so this pins the unsupported set independently of the table: it is exactly what
     * dexlib2's own metadata marks as payload data or ODEX-only, plus the method-handle family.
     */
    @Test
    fun exactlyPayloadsOdexFormsAndMethodHandleOpcodesAreUnsupported() {
        val methodHandles = setOf(
            Opcode.INVOKE_POLYMORPHIC, Opcode.INVOKE_POLYMORPHIC_RANGE,
            Opcode.INVOKE_CUSTOM, Opcode.INVOKE_CUSTOM_RANGE,
            Opcode.CONST_METHOD_HANDLE, Opcode.CONST_METHOD_TYPE,
        )
        val documented = Opcode.values().filter { it.format.isPayloadFormat || it.odexOnly() }.toSet() + methodHandles
        assertEquals(documented, Opcode.values().filterNot(OpBuilder::decodes).toSet())
    }

    /**
     * Executes the const at [address] and returns the value it assigned to its destination -- the
     * LOWEST register it assigns. Not `single()`: a wide literal assigns its destination AND the
     * reserved high half of the pair (`Op.destinationRegisters`), and only the destination holds the
     * whole value -- `Frame.peek` answers null for a high half.
     */
    private fun constantAt(descriptor: String, address: Int): Value {
        val frame = Frame.entryFrame(view(descriptor))
        val op = op(descriptor, address)
        executeSingle(op, frame)
        return frame.peek(op.registersAssigned.minOrNull()!!)!!
    }

    @Test
    fun moveCopiesRegisterAndContinues() {
        // Bug class: a two-register move with its operands swapped (dexlib's registerA is the
        // destination, registerB the source), which silently clobbers the source instead of copying
        // it, and mis-reported liveness. Oracle: Dalvik's `move vA, vB` copies vB into vA.
        val v = view("moveChain()I")
        val frame = Frame.entryFrame(v)
        frame.assign(0, Value.Known(9, int))
        val moveAddress = v.addresses.first { v.opcodeAt(it) == Opcode.MOVE }
        val op = op("moveChain()I", moveAddress)
        assertTrue(op is MoveOp)
        assertTrue(executeSingle(op, frame) is OpArm.Continue)
        assertEquals(Value.Known(9, int), frame.peek(1))
        assertEquals(setOf(0), op.registersRead)
        assertEquals(setOf(1), op.registersAssigned)
    }

    @Test
    fun moveExceptionConsumesTheCaughtSlot() {
        // Bug class: `move-exception` treated as an ordinary two-register move (it has no source
        // register), or a caught value left in the slot so a second handler entry re-reads a stale
        // exception. Oracle: Dalvik's `move-exception vAA` moves the caught throwable into vAA;
        // spec §4.2's slot lifecycle makes it produced once, moved once, then dead.
        val v = view("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(v)
        val caught = Value.Known("boom", VirtualType.of("Ljava/lang/RuntimeException;"))
        frame.setCaught(caught)
        val address = v.addresses.first { v.opcodeAt(it) == Opcode.MOVE_EXCEPTION }
        assertTrue(
            executeSingle(op("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;", address), frame) is
                OpArm.MoveFromSlot,
        )
        assertEquals(caught, frame.peek(0))
        assertNull(frame.takeCaught(), "move-exception consumes the slot")
    }

    @Test
    fun moveExceptionWithNoCaughtValueYieldsTop() {
        // Soundness: an unreachable/ill-formed handler entry must not fabricate a value.
        // Bug class: a null caught slot dereferenced (crashing the run) or defaulted to some
        // placeholder object a strategy would then trust. Oracle: spec §2.2 -- every "unsure"
        // collapses upward, so "nothing was caught" is Top, not a value.
        val v = view("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(v)
        val address = v.addresses.first { v.opcodeAt(it) == Opcode.MOVE_EXCEPTION }
        executeSingle(op("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;", address), frame)
        assertEquals(Value.Top, frame.read(0))
    }

    @Test
    fun returnEmitsReturnTransitionCarryingTheValue() {
        // Bug class: a return that terminates the path without carrying its value, so the graph's
        // consensus has nothing to reconcile, or that reports an empty transition list the scheduler
        // cannot distinguish from a stuck op. Oracle: Dalvik's `return vAA` ends the method with
        // vAA's value; spec §4.4 models that as one Return transition.
        val v = view("returnFive()I")
        val frame = Frame.entryFrame(v)
        frame.assign(0, Value.Known(5, int))
        val op = op("returnFive()I", 1)
        assertTrue(op is ReturnOp)
        assertEquals(OpArm.Return(Value.Known(5, int)), executeSingle(op, frame))
    }

    @Test
    fun returnVoidEmitsReturnWithNoValue() {
        // Bug class: `return-void` decoded as a one-register instruction (it has no register
        // operand), which either crashes on the cast or invents a return value out of register 0.
        // Oracle: Dalvik's `return-void` ends the method with no value; spec §4.4 carries null.
        val v = view("returnNothing()V")
        val address = v.addresses.first { v.opcodeAt(it) == Opcode.RETURN_VOID }
        assertEquals(OpArm.Return(null), executeSingle(op("returnNothing()V", address), Frame.entryFrame(v)))
    }

    @Test
    fun gotoJumpsToItsResolvedTarget() {
        // Bug class: a branch target taken as the raw code *offset* rather than the resolved label
        // address, which lands execution on an address that may not even start an instruction.
        // Oracle: Dalvik's `goto +AA` jumps to this instruction's address plus the offset; dexlib
        // resolves the label for us, and this fixture's label is on the trailing `return`.
        val v = view("gotoSkip()I")
        val gotoAddress = v.addresses.first { v.opcodeAt(it) == Opcode.GOTO }
        val op = op("gotoSkip()I", gotoAddress)
        assertTrue(op is GotoOp)
        val transitions = op.execute(Frame.entryFrame(v)).arms
        val jump = transitions.single()
        assertTrue(jump is OpArm.Jump, "goto emits exactly one Jump arm, got $jump")
        jump as OpArm.Jump
        assertTrue(jump.address > gotoAddress, "this fixture jumps forward")
        assertEquals(Opcode.RETURN, v.opcodeAt(jump.address), "the label lands on the return")
    }

    @Test
    fun nopJustContinues() {
        // Bug class: `nop` given any behavior at all -- it must not read, assign, or branch, because
        // it is also the padding in front of a switch/array payload. Oracle: Dalvik's `nop` does
        // nothing and falls through.
        val v = view("nopThenConst()I")
        val op = op("nopThenConst()I", 0)
        assertTrue(op is NopOp)
        assertTrue(executeSingle(op, Frame.entryFrame(v)) is OpArm.Continue)
        assertEquals(emptySet<Int>(), op.registersRead)
        assertEquals(emptySet<Int>(), op.registersAssigned)
    }

    @Test
    fun throwEmitsThrowTransitionWithTheRegisterValue() {
        // Bug class (the audit's headline exception defect): a throw that reports a *class and a
        // message* instead of the guest throwable itself, so the value the handler receives is a
        // reconstruction and its identity is lost. Oracle: Dalvik's `throw vAA` throws the object in
        // vAA; spec §7 requires that exact value to reach the handler.
        val v = view("throwUncaught(Ljava/lang/RuntimeException;)V")
        val frame = Frame.entryFrame(v)
        val thrown = Value.Known("boom", VirtualType.of("Ljava/lang/RuntimeException;"))
        frame.assign(1, thrown)
        val op = op("throwUncaught(Ljava/lang/RuntimeException;)V", 0)
        assertTrue(op is ThrowOp)
        assertEquals(ThrowEvidence.Existing(thrown), (executeSingle(op, frame) as OpArm.Throw).evidence)
    }

    @Test
    fun aMalformedWideFilledArrayAbortsThePathInsteadOfCrashing() {
        // Bug class: a refused instruction throwing out of the decoder or interpreter, which takes
        // down the whole run instead of just this path -- and a "did nothing" op that the scheduler
        // cannot tell apart from a terminating one. Oracle: spec §6.4 -- an unrepresentable opcode
        // aborts its path with UNSUPPORTED_OP and the run's status becomes Partial.
        val v = view("unsupportedWideFilledArray()I")
        val address = v.addresses.first { v.opcodeAt(it) == Opcode.FILLED_NEW_ARRAY }
        val op = op("unsupportedWideFilledArray()I", address)
        assertTrue(op is UnsupportedOp)
        val frame = Frame.entryFrame(v)
        assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, frame))
        assertEquals("filled-new-array", op.name)
        assertEquals(emptySet<Int>(), frame.registersAssigned(), "an aborted op must not half-apply its effect")
    }

    @Test
    fun oneOpInstanceDrivesTwoFramesIndependently() {
        // Bug class (the audit's shared-mutable-state defect, in its op-shaped form): an op that
        // caches per-execution state on itself. Ops are built once per address and shared by every
        // path that reaches it, so a cached value would leak one path's data into its sibling's.
        // Oracle: spec §5.2 -- an op is a stateless template; all mutable state lives in the Frame.
        val v = view("moveChain()I")
        val moveAddress = v.addresses.first { v.opcodeAt(it) == Opcode.MOVE }
        val op = op("moveChain()I", moveAddress)

        val first = Frame.entryFrame(v)
        first.assign(0, Value.Known(1, int))
        executeSingle(op, first)
        val second = Frame.entryFrame(v)
        second.assign(0, Value.Known(2, int))
        executeSingle(op, second)

        assertEquals(Value.Known(1, int), first.peek(1), "the first frame kept its own value")
        assertEquals(Value.Known(2, int), second.peek(1), "the second frame saw only its own value")
    }


    /**
     * The rendered form of an op is read by the debugger's instruction listing and Simplify's grapher
     * output, so it is behavior a consumer reads, not internal shape -- unlike the mock-verification
     * assertions the deleted `UnitTest` nests made. Rendering was genuinely uncovered elsewhere:
     * `UnaryMathOpTest.canDoubleToFloat` and
     * `CmpOpTest.canArg1IsUnknownReturnsUnknown` were the only surviving-family assertions on it, and
     * neither migrated. One case per instruction shape: three-register, `/2addr` (where vA is both
     * destination and first operand), and literal -- plus one each for [org.cf.smalivm.core.op.CmpOp]
     * and [org.cf.smalivm.core.op.UnaryMathOp], which have only one shape apiece.
     *
     * **Format change from the oracle, recorded here because there is no other consumer yet to notice
     * it**: the oracle's `BinaryMathOp.toString()` special-cases `/2addr` to print two operands
     * (`"add-int/2addr r0, r1"`, `opcode/BinaryMathOp.kt:73-83`), since dest == lhs there. This class
     * always renders three (`"add-int/2addr r0, r0, r1"`) -- clearer, and nothing downstream (debugger,
     * grapher) exists yet on either side to break, so a later plan reviving either should expect three
     * operands here, not two.
     *
     * **The destination-differs-from-first-operand case, previously a recorded gap.** Every other
     * fixture here has `destRegister == lhsRegister` (`add-int v0, v0, v1`, its `/2addr` and literal
     * forms, `RSubInt()V`) or `destRegister == srcRegister` for the unary case, so none of them can
     * observe a mutation that swapped the destination and first-operand fields in `toString()`'s output.
     * `divIntUnknownDividendWithCatch(I)V` -- appended for the differential's oracle-over-fork witness
     * -- is `div-int v0, v2, v0`, the first `BinaryMathOp` instruction in the three arithmetic fixture
     * files whose three operands are all distinguishable, so the last row below closes that gap. It is
     * asserted here rather than left implicit because the fixture exists for an unrelated reason and a
     * future edit to it would otherwise silently reopen the gap.
     */
    @Test
    fun opsRenderTheirOperandsInTheirDeclaredOrder() {
        assertEquals("add-int r0, r0, r1", opAt("Lbinary_math_test;", "addInt()V", 0).toString())
        assertEquals("add-int/2addr r0, r0, r1", opAt("Lbinary_math_test;", "addInt2Addr()V", 0).toString())
        assertEquals("add-int/lit8 r0, r0, 0xf", opAt("Lbinary_math_test;", "addIntLit8()V", 0).toString())
        assertEquals("double-to-float r0, r0", opAt("Lunary_math_test;", "doubleToFloat()V", 0).toString())
        assertEquals("cmp-long r0, r0, r2", opAt("Lcmp_test;", "cmpLong()V", 0).toString())
        assertEquals(
            "div-int r0, r2, r0",
            opAt("Lbinary_math_test;", "divIntUnknownDividendWithCatch(I)V", 0).toString(),
        )
        // `aget` is the first THREE-operand op whose operands are all genuinely distinct registers with
        // distinct MEANINGS (dest, array, index) rather than dest plus two interchangeable arithmetic
        // operands, so the rendering is worth pinning on the disjoint fixture rather than on the corpus's
        // `v0, v0, v1`. Same format as the oracle's `opcode/AGetOp.kt:56`, so Plan 10's listing agrees.
        assertEquals("aget r2, r0, r1", opAt("Laget_test;", "getWithThreeDistinctRegisters()V", 0).toString())
        // `aput` renders the SAME three registers in the same slots but with different MEANINGS: the first
        // is the VALUE, not a destination. `aput v2, v0, v1` and `aget v2, v0, v1` are therefore
        // indistinguishable by shape, which is exactly why both are pinned -- a decoder that read `aput`'s
        // operands as `aget`'s would still render three plausible registers in a plausible order.
        assertEquals("aput r2, r0, r1", opAt("Laput_test;", "put()V", 0).toString())
        // Re-homed from the two retired mockk change-detectors. Their interaction assertions pinned the
        // knowingly-deferred placeholder; only exact operand rendering was genuine consumer coverage.
        assertEquals(
            "iget r1, r0, Liget_test;->myInt:I",
            opAt(IGET, "getIntFromFreshObject()I", 2).toString(),
        )
        assertEquals(
            "iput r1, r0, Liput_test;->myInt:I",
            opAt(IPUT, "putInt()Liput_test;", 4).toString(),
        )
        // `fill-array-data` is the one op whose rendering DIVERGES from the engine being replaced, which
        // printed `:addr_<n>` with the payload's address. This core decodes the payload away at build time
        // and no longer carries that address, so the operand renders as the literal `:payload` -- recorded
        // next to the two other format divergences in `CARRY-FORWARD.md`'s Plan 10 item 3, and pinned here
        // because Plan 10's instruction listing is the consumer that will read it.
        assertEquals(
            "fill-array-data r0, :payload",
            opAt("Lfill_array_data_test;", "fillArrayDataInt()V", 0).toString(),
        )
        // BOTH `if` shapes, and this row is where the deleted `opcode/IfOpTest.UnitTest`'s one non-mock
        // assertion now lives (`"if-ge r2, r4, :addr_0"`). Adjudication in
        // `core/op/IfOpTest.mockkAdjudication`'s KDoc: mutating `IfOp.toString` kills no behavioral test,
        // so the rendering is genuinely uncovered elsewhere and is kept -- here rather than in the op's
        // own test file, for the same reason Plan 4's and Plan 7 Task 11's restorations are here.
        // The `*z` form renders ONE operand and the `22t` form two, so both need a row: an op that read
        // `registerB` off a `21t` instruction, or printed a phantom second operand, renders plausibly.
        assertEquals("if-eq r0, r1, :addr_3", opAt("Lif_test;", "ifEqual()V", 0).toString())
        assertEquals("if-eqz r0, :addr_3", opAt("Lif_test;", "ifEqualZero()V", 0).toString())
        // BOTH switch spellings, and this row is where the deleted `opcode/SwitchPayloadOpTest`'s coverage
        // now lives -- adjudication in `core/op/SwitchOpTest.mockkAdjudication`'s KDoc: mutating
        // `SwitchOp.toString` kills no behavioral test, so the rendering is genuinely uncovered elsewhere.
        // The numbers are ABSOLUTE ADDRESSES, which is `opcodes.md:174`'s Correctness Defect 19 fixed
        // rather than reproduced (the oracle printed `:addr_` followed by a relative OFFSET), and the
        // operand is the table rather than the fall-through address the oracle printed (defect 10). Both
        // fixtures' switch is at address 1 -- a `goto`/`nop` comes first on purpose, so that "offset from
        // the switch" and "offset from the method start" cannot be confused.
        assertEquals(
            "packed-switch r0, [1 -> :addr_5, 2 -> :addr_6, 3 -> :addr_7]",
            opAt("Lswitch_test;", "packedSwitch()V", 1).toString(),
        )
        assertEquals(
            "sparse-switch r0, [1 -> :addr_5, 2 -> :addr_6]",
            opAt("Lswitch_test;", "sparseSwitch()V", 1).toString(),
        )
        // Both filled-new-array rendering shapes are observable debugger output. Keep their assertions
        // here with the other op-family rendering cases rather than with execution-only array tests.
        assertEquals(
            "filled-new-array {r0, r1, r2}, [I",
            opAt(FILLED_NEW_ARRAY, "filledNewArrayThenMoveResult()V", 0).toString(),
        )
        assertEquals(
            "filled-new-array/range {r0 .. r5}, [I",
            opAt(FILLED_NEW_ARRAY, "filledNewArrayRangeThenMoveResult()V", 0).toString(),
        )
        // THE INPUT THE REPLACED ENGINE GOT WRONG, and which no corpus fixture reached. Its rule was
        // "more than five registers -> print the range spelling" -- a proxy for "is this the range opcode".
        // `filled-new-array/range {v0 .. v4}, [I` is perfectly legal, because `3rc`'s `A` field is 8 bits and
        // a range form is not required to exceed five registers, so for THAT instruction the size test prints
        // the NON-range braces while the opcode prints the range ones. The core spells it by opcode: the
        // mnemonic a disassembler must print is a property of the opcode, not of the register count. Recorded
        // as a deliberate format divergence next to the other three in CARRY-FORWARD.md's Plan 10 item 3.
        assertEquals(
            "filled-new-array/range {r0 .. r4}, [I",
            opAt(FILLED_NEW_ARRAY, "filledNewArrayRangeFive()V", 0).toString(),
        )
        // Re-homed from the two old mockk rendering tests. The second row also closes their false
        // positive: the old MonitorExitOpTest constructed MonitorEnterOp.
        assertEquals("monitor-enter r3", opAt("Lmonitor_test;", "enter(Ljava/lang/Object;)V", 0).toString())
        assertEquals("monitor-exit r2", opAt("Lmonitor_test;", "exit(Ljava/lang/Object;)V", 0).toString())
    }

    @Test
    fun aFilledNewArrayWithNoFillableComponentTypeIsUnsupportedRatherThanAHostFailure() {
        // Bug class: a type operand this opcode cannot fill reaching the op, where either a `requireNotNull`
        // on the component escapes the decoder -- `buildSuccessor` turns that into `Aborted(HOST_FAILURE)`,
        // indistinguishable from an engine bug -- or, worse, a wide component gets filled one word at a time
        // and the array silently holds half of each value.
        //
        // Two operands, one guard. `[J` is SPEC-forbidden ([BC]: the contents "must be single-word (that is,
        // no arrays of long or double...)") and is an ART verifier HARD failure citing that very page, so no
        // device ever reaches an interpreter for it. `I` is not an array type at all, violating [BC]'s "The
        // type must be an array type", so there is no component to fill. `UNSUPPORTED_OP` is the honest
        // answer to both -- "this build cannot execute this instruction" -- and it makes the run Partial,
        // which is the correct trust level for a method a device would refuse to run.
        //
        // This test is why the guard is not INERT: the brief declined a fixture for the `[J` row ("worth less
        // than the four rows above"), which would have left a main-code branch no mutation could kill.
        for (descriptor in listOf("filledNewArrayOfLongs()V", "filledNewArrayOfNonArrayType()V")) {
            val v = view(descriptor, FILLED_NEW_ARRAY)
            val op = OpBuilder.build(0, v, classIndex)
            assertTrue(op is UnsupportedOp, "$descriptor must decode to an UnsupportedOp, got $op")
            assertEquals("filled-new-array", op.name, "and it reports the real opcode, not a placeholder")
            assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, Frame.entryFrame(v)))
        }
    }

    /**
     * **The type operand's resolution failure is a guest `NoClassDefFoundError` thrown at EXECUTE time, and
     * this is where the DECODE half of that is pinned: the instruction is not refused.**
     *
     * 🔴 **Plan 9 Task 15 (ROW-23) reversed the Plan 8 expectation this test used to carry.** It asserted
     * `is UnsupportedOp` plus `Abort(UNSUPPORTED_OP)`; the authority for the change is the `[DEVICE]`
     * measurement of the whole resolution-failure family --
     * `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`, `tools/art-runner/fixtures/Fx_InstanceOfAbsentType.smali`
     * against `Fx_InstanceOfPresentType.smali` -- which outranks the "the type is [ART]-only, so refuse"
     * caution Plan 8 recorded (`docs/DALVIK-SEMANTICS.md#resolution-failure-is-one-family-tiered-once`).
     * dexlib2 said `instance-of` can throw all along, and it was right.
     *
     * **This is a DECODE-time test, so it asserts the transition rather than a graph.** The behavioural
     * assertions -- that the throw escapes a `catch (Exception)`, that it fires even on a null source -- live
     * in `core/op/InstanceOfOpTest`. What only this file can see is that the absent type produces a real
     * [InstanceOfOp] rather than an [UnsupportedOp]: a build that "fixed" this by refusing again would still
     * pass a throw-shaped test if the refusal were mistaken for a throw, and would fail here.
     *
     * The CONTROL matters as much as the assertion: without it, "an absent type decodes to an InstanceOfOp"
     * also passes against a build that ignores the type operand entirely.
     */
    @Test
    fun anInstanceOfAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("instanceOfString()V", INSTANCE_OF)
        assertTrue(
            OpBuilder.build(0, present, classIndex) is InstanceOfOp,
            "the CONTROL: a resolvable operand decodes to a real op, so the throw below is about the type",
        )

        val v = view("instanceOfAbsentClass()V", INSTANCE_OF)
        val op = OpBuilder.build(0, v, classIndex)
        assertTrue(op is InstanceOfOp, "an unresolvable type operand throws at execute; it is not refused")
        assertEquals("instance-of", op.name, "and it reports the real opcode, not a placeholder")
        val transitions = op.execute(Frame.entryFrame(v)).arms
        assertEquals(1, transitions.size, "a definite throw, not a fork")
        val throwTransition = transitions.single()
        assertTrue(throwTransition is OpArm.Throw, "expected a Throw, got $throwTransition")
        assertEquals(
            VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
            throwType(throwTransition as OpArm.Throw),
            "an Error via LinkageError, NOT a checked Exception -- that is the whole of ROW-23",
        )
        // The CONTROL's other half, and the one that makes the assertion above about ABSENCE rather than
        // about `instance-of` throwing unconditionally: a resolvable operand on the same op must continue.
        assertTrue(
            executeSingle(OpBuilder.build(0, present, classIndex), Frame.entryFrame(present)) is OpArm.Continue,
            "a resolvable operand still falls through -- it is resolution failing, not the op",
        )

        // A PRIMITIVE operand is the other half of the same gate and must NOT throw: `isLoadable`
        // answers true for a primitive (there is no class to find), and [BC] gives that case a definite
        // answer -- "this will always result in 0 being stored if C refers to a primitive type" -- which
        // only the executing op can produce. Throwing here would replace a SPEC answer with a linking error.
        val primitive = view("instanceOfPrimitiveInt()V", INSTANCE_OF)
        assertTrue(
            OpBuilder.build(0, primitive, classIndex) is InstanceOfOp,
            "a primitive type operand is unverifiable input, not unresolvable input",
        )
        assertTrue(
            executeSingle(OpBuilder.build(0, primitive, classIndex), Frame.entryFrame(primitive)) is OpArm.Continue,
            "and it must ANSWER rather than throw -- `isLoadable` is true for a primitive",
        )
    }

    /**
     * The same choice for `check-cast`, and it needs its own test rather than riding on `instance-of`'s:
     * the two opcodes are decoded by **different** functions ([OpBuilder]'s `buildInstanceOf` and
     * `buildCheckCast`), because one is `22c` with two registers and the other is `21c` with one -- so a
     * missing `isLoadable` guard in either is invisible to the other's test.
     *
     * `check-cast`'s stakes are strictly higher than `instance-of`'s, which is why the answer is worth
     * pinning twice: `instance-of` against an absent type would fold a boolean, while `check-cast` would
     * have to choose between inventing a definite `ClassCastException` (an infeasible throw) and inventing
     * a definite success. **The measured device answer is neither, and it is also not Plan 8's whole-method
     * `Partial`:** it is a definite guest `NoClassDefFoundError`, an `Error`. 🔴 Plan 9 Task 15 (ROW-23)
     * reversed this test's expectation on that authority --
     * `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)`, cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`, `tools/art-runner/fixtures/Fx_CheckCastAbsentType.smali`.
     *
     * The CONTROL matters as much as the assertion: without it, "an absent type decodes to a CheckCastOp"
     * also passes against a build where the type operand is never consulted.
     */
    @Test
    fun aCheckCastAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("castToString()V", CHECK_CAST)
        assertTrue(
            OpBuilder.build(0, present, classIndex) is CheckCastOp,
            "the CONTROL: a resolvable operand decodes to a real op, so the throw below is about the type",
        )

        val v = view("castToAbsentClass()V", CHECK_CAST)
        val op = OpBuilder.build(0, v, classIndex)
        assertTrue(op is CheckCastOp, "an unresolvable type operand throws at execute; it is not refused")
        assertEquals("check-cast", op.name, "and it reports the real opcode, not a placeholder")
        val transitions = op.execute(Frame.entryFrame(v)).arms
        assertEquals(1, transitions.size, "a definite throw, not a fork")
        val throwTransition = transitions.single()
        assertTrue(throwTransition is OpArm.Throw, "expected a Throw, got $throwTransition")
        assertEquals(
            VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
            throwType(throwTransition as OpArm.Throw),
            "an Error via LinkageError, NOT a ClassCastException and NOT a checked Exception",
        )
        // The control's other half: a resolvable cast on an unset (Top) register does NOT throw a linking
        // error. Without this, "the absent type throws" also passes against a build where every check-cast
        // throws NoClassDefFoundError.
        assertTrue(
            OpBuilder.build(0, present, classIndex).execute(Frame.entryFrame(present)).arms
                .none { it is OpArm.Throw && throwType(it) == VirtualType.of("Ljava/lang/NoClassDefFoundError;") },
            "a resolvable cast raises no linking error -- it is resolution failing, not the op",
        )
    }

    /**
     * `const-class` has its own decoder row, so its resolution guard is invisible to the type ops' tests. An
     * absent class decodes to an op that throws a guest `NoClassDefFoundError` at execute, as a device does
     * (`tools/art-runner/fixtures/Fx_ConstClassAbsentType.smali`); the control shows a resolvable class
     * decodes to the same op and falls through.
     */
    @Test
    fun aConstClassAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("constClassLocal()V", CONST)
        assertTrue(
            OpBuilder.build(0, present, classIndex) is ConstClassOp,
            "the CONTROL: a resolvable class-constant decodes to a real ConstClassOp",
        )
        assertTrue(
            executeSingle(OpBuilder.build(0, present, classIndex), Frame.entryFrame(present)) is OpArm.Continue,
            "and it falls through, so the throw below is not `every const-class throws`",
        )

        val v = view("constClassUnknown()V", CONST)
        val op = OpBuilder.build(0, v, classIndex)
        assertTrue(op is ConstClassOp, "an unresolvable class-constant throws at execute; it is not refused")
        assertEquals("const-class", op.name, "and it reports the real opcode, not a placeholder")
        val transitions = op.execute(Frame.entryFrame(v)).arms
        assertEquals(1, transitions.size, "a definite throw, not a fork")
        val throwTransition = transitions.single()
        assertTrue(throwTransition is OpArm.Throw, "expected a Throw, got $throwTransition")
        assertEquals(
            VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
            throwType(throwTransition as OpArm.Throw),
            "an Error via LinkageError -- the oracle's checked ClassNotFoundException is the divergence",
        )
    }

    /**
     * The same decode-time ruling for the static-field family, and the build-BRANCH pins Task 18 owes
     * (Plan 8 item 10's rule; `aConstClassAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal` is the
     * precedent): the dispatch-set test pins the opcode SET, not the `in SGET_OPCODES -> buildSGet(...)` /
     * `in SPUT_OPCODES -> buildSPut(...)` BRANCHES -- deleting a family's branch drops its opcodes to
     * `else -> UnsupportedOp` while the set literal stays intact. ONE test, TWO controls, because the two
     * families are two separate `when` rows and an `is SGetOp` pin cannot see a mis-wired `buildSPut`.
     *
     * The absent-type rows ride the controls. `Lsome/unknown/classzzzzz;` is not in the loaded set, so
     * resolution fails at decode time and the op THROWS at execute -- a guest `NoClassDefFoundError`,
     * **`[DEVICE]`-measured for `sget` specifically** (`Fx_SGetAbsentType` against `Fx_SGetPresentType`,
     * Tier A `-Xint` cross-validated Tier B nterp; `StaticFieldLookup.NoSuchClass`'s KDoc carries the full
     * citation, and `sput` rides the same measurement -- same symbolic reference, same resolution path).
     * Op build never fails on the host for an absent type; `staticUnknownClassFieldThrowsException` pins
     * the same guest error at execute.
     *
     * The resolvable control's execute answers `Transition.InitClass`, NOT `Continue`, and asserting that
     * exactly is deliberate: resolution-before-class-init is the preamble's stated order (an absent class
     * cannot be initialized), so the control doubles as the decode-surface witness that a resolvable
     * `sget` on an uninitialized class asks for init -- which is what makes the absent-type row's DIRECT
     * throw (no InitClass first) about resolution failing rather than about the op throwing unconditionally.
     */
    @Test
    fun aSGetOrSPutAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("getStaticIntLiteral()V", SGET)
        val control = OpBuilder.build(0, present, classIndex)
        assertTrue(
            control is SGetOp,
            "the CONTROL: a resolvable field decodes to a real SGetOp, so the throw below is about the " +
                "type -- and deleting the sget build branch drops this to UnsupportedOp",
        )
        assertEquals(
            OpArm.InitClass(VirtualType.of(SGET)),
            executeSingle(control, Frame.entryFrame(present)),
            "a resolvable sget on an uninitialized class asks for class init first -- it does not throw",
        )

        val v = view("getStaticUnknownClassField()V", SGET)
        val op = OpBuilder.build(0, v, classIndex)
        assertTrue(op is SGetOp, "an unresolvable field reference throws at execute; it is not refused")
        assertEquals("sget", op.name, "and it reports the real opcode, not a placeholder")
        val transitions = op.execute(Frame.entryFrame(v)).arms
        assertEquals(1, transitions.size, "a definite throw -- not a fork, and not an InitClass prelude")
        val throwTransition = transitions.single()
        assertTrue(throwTransition is OpArm.Throw, "expected a Throw, got $throwTransition")
        assertEquals(
            VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
            throwType(throwTransition as OpArm.Throw),
            "an Error via LinkageError -- the oracle's host RuntimeException at op-build time is the divergence",
        )

        // The WRITING family's own control and absent-type row: `buildSPut` is a separate `when` row, so
        // neither assertion above can see it. The value register is never read on the failing path --
        // resolution comes first -- which is why an entry frame with v0 unassigned works here.
        val sputPresent = view("putStaticInt()V", SPUT)
        assertTrue(
            OpBuilder.build(0, sputPresent, classIndex) is SPutOp,
            "the sput CONTROL: deleting the sput build branch drops this to UnsupportedOp",
        )
        val sputAbsent = view("putStaticUnknownClassField()V", SPUT)
        val sputOp = OpBuilder.build(0, sputAbsent, classIndex)
        assertTrue(sputOp is SPutOp, "sput's unresolvable reference throws at execute; it is not refused")
        assertEquals("sput", sputOp.name, "and it reports the real opcode, not a placeholder")
        val sputThrow = sputOp.execute(Frame.entryFrame(sputAbsent)).arms.single()
        assertTrue(sputThrow is OpArm.Throw, "expected a Throw, got $sputThrow")
        assertEquals(
            VirtualType.of("Ljava/lang/NoClassDefFoundError;"),
            throwType(sputThrow as OpArm.Throw),
            "the same family answer through the shared preamble -- the two ops must not drift apart",
        )
    }

    /**
     * 🔴 **`buildConstClass` HAS THREE OUTCOMES, NOT TWO, AND THIS PINS THE THIRD** -- the one the KDoc used
     * to call unreachable. Added in Task 15's fix round after review measured the claim false.
     *
     * `isLoadable` asks the smali class model; `loadClassConstant` asks the host `SmaliClassLoader`. When the
     * model says **yes** and the host loader still cannot produce a `Class`, that is an ENGINE limitation, not
     * a device behaviour -- the class exists on a real device -- so the honest answer is [UnsupportedOp] and a
     * guest `NoClassDefFoundError` would be spec §2.2's invented-infeasible-path defect.
     *
     * **And it is common.** `SmaliClassLoader.findClass` throws `ClassNotFoundException` for **every** name
     * starting with `java.` (`dex/SmaliClassLoader.kt:43-57`), then `loadClass` falls back to the JVM parent
     * (`:32-36`), which has no Android-only `java.*` class. Measured over the bundled framework model:
     * **506 of 2431 `java.*` descriptors** satisfy both halves, so a `const-class` on any of them makes the
     * run `Partial` -- and an obfuscated APK can name one. `Ljava/lang/DexCache;` is the witness here:
     * ordinary, stable, and in the bundled model.
     *
     * **Both halves of the predicate are asserted rather than assumed**, so if the framework submodule ever
     * stops shipping `DexCache` -- or the host JVM starts shipping it -- this fails loudly naming which half
     * moved, instead of silently testing nothing. That is the whole reason the two `assert`s precede the
     * decode assertion rather than being folded into it.
     *
     * Reproduce the population count with `isLoadable(VirtualType.of(n)) && loadClassConstant(n) == null` over
     * the bundled framework class names; it is deliberately not asserted here, because sweeping 2431
     * descriptors through the host loader costs ~50s and this file is not the place for it.
     */
    @Test
    fun aConstClassOnAModelledButHostUnloadableClassIsUnsupportedRatherThanAResolutionFailure() {
        val hostUnloadable = "Ljava/lang/DexCache;"
        assertTrue(
            classIndex.isLoadable(VirtualType.of(hostUnloadable)),
            "the first half: $hostUnloadable IS in the bundled class model, so it is NOT absent",
        )
        assertNull(
            classIndex.loadClassConstant(hostUnloadable),
            "the second half: the host loader still cannot produce a Class for it (java.* is refused, and " +
                "the JVM parent has no Android-only java.* class)",
        )

        val v = view("constClassHostUnloadable()V", CONST)
        val op = OpBuilder.build(0, v, classIndex)
        assertTrue(
            op is UnsupportedOp,
            "an engine limitation is UNSUPPORTED_OP, never a guest NoClassDefFoundError -- the class exists " +
                "on a device, so throwing would invent an infeasible path on a fifth of the framework: $op",
        )
        assertEquals("const-class", op.name, "and it reports the real opcode, not a placeholder")
        assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, Frame.entryFrame(v)))

        // THE CONTRAST that makes the assertion above about this ROW rather than about const-class in general:
        // a genuinely ABSENT class takes the other branch and throws. Same opcode, same builder, two answers.
        val absent = view("constClassUnknown()V", CONST)
        assertTrue(
            OpBuilder.build(0, absent, classIndex) is ConstClassOp,
            "an ABSENT class still decodes to a throwing ConstClassOp -- the two failure modes stay distinct",
        )
    }

    @Test
    fun aFillArrayDataWhoseTargetIsNotAnArrayPayloadIsUnsupportedRatherThanAHostFailure() {
        // Bug class: a failed cast (or `MethodView.instructionAt`'s `requireNotNull`) escaping the decoder,
        // which `buildSuccessor` converts to `Aborted(HOST_FAILURE)` -- indistinguishable from an engine bug
        // -- where `UNSUPPORTED_OP` is the true statement about malformed obfuscator output.
        // `CARRY-FORWARD.md`'s Plan 9 item 5 is the standing rule about that pair. The fixture's
        // fill-array-data "branches" to an ordinary `return-void`, which smali assembles happily because the
        // operand is just a label.
        val v = view("fillArrayDataWithMalformedPayloadTarget()V", "Lfill_array_data_test;")
        assertTrue(v.payloadAddresses.isEmpty(), "the fixture deliberately contains no payload at all")
        val op = OpBuilder.build(0, v, classIndex)
        assertTrue(op is UnsupportedOp, "a malformed payload target is unsupported, not a decode failure")
        assertEquals("fill-array-data", op.name, "and it reports the real opcode, not a placeholder")
        assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, Frame.entryFrame(v)))
    }

    /**
     * The switch twin of the test above, and it needs a **forged view** because smali cannot express the
     * input -- which is a measured difference between the two payload-bearing families, not a shortcut.
     *
     * `fill-array-data`'s malformed target IS assemblable (the fixture above "branches" to a `return-void`
     * and smali assembles it happily, because that operand is just a label). A switch's is **rejected by
     * the assembler**: `packed-switch v0, :not_a_table` fails with *"Switch instruction at address/index
     * 0x1/1 does not refer to a payload instruction."* — measured, from the assembler's own stderr, while
     * trying to write the fixture. So `OpBuilder.buildSwitch`'s two guards had no reachable input at all
     * and deleting both killed **0 of 947** tests, which is what a review measured and what this closes.
     *
     * A forged [MethodView] is the device this repository already uses for exactly this situation --
     * `CommitTest.forgedTryBlocks` and `ThrowRoutingTest` forge handler addresses no smali can express --
     * and it is not a synthetic case in the pejorative sense: `MethodView`'s constructor is `internal`, and
     * Plan 13's editor is a real future producer of a view whose address map disagrees with what the
     * instructions' own labels point at. That is precisely the case `buildFillArrayData`'s disclosure names
     * as the reason its own `hasAddress` guard is kept.
     *
     * Both guards, one per forgery, so a partial revert cannot hide behind the other: the payload address
     * **missing** (which without the guard reaches `instructionAt`'s throwing `requireNotNull`), and the
     * payload address holding **an ordinary instruction** (which without the guard is a failed cast). Both
     * escape the decoder as a `Throwable`, which `buildSuccessor` converts to `Aborted(HOST_FAILURE)` --
     * indistinguishable from a bug in this codebase -- where `UNSUPPORTED_OP` is the true statement about
     * malformed obfuscator output.
     */
    @Test
    fun aSwitchWhoseTableIsMissingOrIsNotATableIsUnsupportedRatherThanAHostFailure() {
        val real = view("packedSwitch()V", "Lswitch_test;")
        val payload = real.payloadAddresses.single()
        assertTrue(
            OpBuilder.build(1, real, classIndex) is SwitchOp,
            "the CONTROL: the unforged fixture decodes to a real switch, so the forgeries below are the " +
                "only thing making the guards fire",
        )

        val missing = forgedView("Lswitch_test;", "packedSwitch()V") { it.remove(payload) }
        val onMissing = OpBuilder.build(1, missing, classIndex)
        assertTrue(onMissing is UnsupportedOp, "a payload target that is not an address is unsupported, got $onMissing")
        assertEquals("packed-switch", onMissing.name, "and it reports the real opcode, not a placeholder")
        assertEquals(
            OpArm.Abort(AbortReason.UNSUPPORTED_OP),
            executeSingle(onMissing, Frame.entryFrame(missing)),
        )

        // The payload address now holds the method's `return-void`, i.e. an instruction that is not a table.
        val notATable = forgedView("Lswitch_test;", "packedSwitch()V") { it[payload] = it.getValue(4) }
        val onNotATable = OpBuilder.build(1, notATable, classIndex)
        assertTrue(onNotATable is UnsupportedOp, "a payload target that is not a table is unsupported, got $onNotATable")
        assertEquals("packed-switch", onNotATable.name)
        assertEquals(
            OpArm.Abort(AbortReason.UNSUPPORTED_OP),
            executeSingle(onNotATable, Frame.entryFrame(notATable)),
        )
    }

    /**
     * A real fixture method with its address→instruction map edited and rebuilt through the body importer, so a
     * payload target can name an address the method does not contain or one holding something else. No smali can
     * express it, because the assembler resolves every label and refuses a switch target that is not a payload.
     */
    private fun forgedView(
        className: String,
        methodDescriptor: String,
        edit: (MutableMap<Int, com.android.tools.smali.dexlib2.iface.instruction.Instruction>) -> Unit,
    ): MethodView {
        val real = classIndex.requireMethodView(className, methodDescriptor)
        val instructions = real.addresses.associateWithTo(LinkedHashMap()) {
            if (real.isPayload(it)) real.payloadAt(it) else real.instructionAt(it)
        }
        edit(instructions)
        return classIndex.rebuiltView(real, instructions = instructions.values.toList())
    }

    /**
     * A `35c` instruction has five register fields, but its count nibble can declare up to fifteen in a
     * hostile dex. Reading past the five would invent reads of `v0`, so the instruction is unsupported for
     * `invoke-*` exactly as for `filled-new-array`. The assembler refuses such a count, so the view is forged.
     */
    @Test
    fun aFiveRegisterInvokeDeclaringMoreThanFiveRegistersIsUnsupported() {
        val descriptor = "returnsThrowsOrCallsHost(II)I"
        val real = view(descriptor)
        val address = real.addresses.first { real.opcodeAt(it) == Opcode.INVOKE_DIRECT }
        assertTrue(OpBuilder.build(address, real, classIndex) is InvokeOp, "the CONTROL: the real invoke decodes")

        val forged = forgedView("Lspine/spine;", descriptor) {
            val invoke = it.getValue(address) as Instruction35c
            it[address] = object : Instruction35c by invoke {
                override fun getRegisterCount(): Int = 6
            }
        }
        val op = OpBuilder.build(address, forged, classIndex)
        assertTrue(op is UnsupportedOp, "a 35c naming six registers is unsupported, got $op")
        assertEquals("invoke-direct", op.name)
    }

    @Test
    fun aPayloadPseudoInstructionDecodesToAnUnsupportedOpSoNothingCanExecuteIt() {
        // The other half of design decision 3, at the decode surface: `.array-data` is DATA
        // (MethodView.payloadAddresses), so it must have no executable meaning. If a malformed jump ever
        // targets one, aborting that path is the fail-safe answer rather than running a table as code.
        val v = view("fillArrayDataInt()V", "Lfill_array_data_test;")
        val op = OpBuilder.build(v.payloadAddresses.single(), v, classIndex)
        assertTrue(op is UnsupportedOp, "a payload pseudo-instruction is not an executable op")
        assertEquals("array-payload", op.name)
        assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, Frame.entryFrame(v)))
    }
}
