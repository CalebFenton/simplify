package org.cf.smalivm.core

import org.cf.smalivm.core.op.CheckCastOp
import org.cf.smalivm.core.op.ConstOp
import org.cf.smalivm.core.op.GotoOp
import org.cf.smalivm.core.op.InstanceOfOp
import org.cf.smalivm.core.op.MoveOp
import org.cf.smalivm.core.op.NopOp
import org.cf.smalivm.core.op.ReturnOp
import org.cf.smalivm.core.op.SGetOp
import org.cf.smalivm.core.op.SPutOp
import org.cf.smalivm.core.op.SwitchOp
import org.cf.smalivm.core.op.ThrowOp
import com.android.tools.smali.dexlib2.Opcode
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
        is ThrowEvidence.Fresh -> evidence.recipe.type
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

    @Test
    fun opBuilderDispatchesExactlyTheOpcodesThisBuildImplements() {
        // Bug class, and the one no fixture-driven test can catch: an opcode dropped from the
        // dispatch table degrades *silently*. It falls to `else`, becomes an UnsupportedOp, and
        // emits an Abort(UNSUPPORTED_OP) byte-for-byte identical to the fixture's deliberate
        // wide-component filled-new-array -- so every method using that instruction aborts with no signal
        // anywhere. Deleting `Opcode.MOVE_WIDE` today breaks nothing else in this suite, because
        // spine.smali contains no move-wide: 15 of the dispatched opcodes appear in no fixture at
        // all. Naming the supported set in production and pinning it here is therefore the only
        // fixture-independent guard. Oracle: the plan's Task 4 dispatch table (plan lines
        // 1190-1204), transcribed literally below -- NOT read back from OpBuilder.
        val expected = setOf(
            Opcode.NOP,
            Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32,
            Opcode.IF_EQ, Opcode.IF_NE, Opcode.IF_LT, Opcode.IF_GE, Opcode.IF_GT, Opcode.IF_LE,
            Opcode.IF_EQZ, Opcode.IF_NEZ, Opcode.IF_LTZ, Opcode.IF_GEZ, Opcode.IF_GTZ, Opcode.IF_LEZ,
            Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH,
            Opcode.THROW,
            Opcode.RETURN_VOID, Opcode.RETURN, Opcode.RETURN_WIDE, Opcode.RETURN_OBJECT,
            Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16,
            Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16,
            Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16,
            Opcode.MOVE_EXCEPTION,
            Opcode.MOVE_RESULT, Opcode.MOVE_RESULT_WIDE, Opcode.MOVE_RESULT_OBJECT,
            Opcode.CONST, Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST_HIGH16,
            Opcode.CONST_WIDE, Opcode.CONST_WIDE_16, Opcode.CONST_WIDE_32, Opcode.CONST_WIDE_HIGH16,
            Opcode.CONST_STRING, Opcode.CONST_STRING_JUMBO,
            Opcode.CONST_CLASS,
            Opcode.NEG_INT, Opcode.NOT_INT, Opcode.NEG_LONG, Opcode.NOT_LONG,
            Opcode.NEG_FLOAT, Opcode.NEG_DOUBLE,
            Opcode.INT_TO_LONG, Opcode.INT_TO_FLOAT, Opcode.INT_TO_DOUBLE,
            Opcode.LONG_TO_INT, Opcode.LONG_TO_FLOAT, Opcode.LONG_TO_DOUBLE,
            Opcode.FLOAT_TO_INT, Opcode.FLOAT_TO_LONG, Opcode.FLOAT_TO_DOUBLE,
            Opcode.DOUBLE_TO_INT, Opcode.DOUBLE_TO_LONG, Opcode.DOUBLE_TO_FLOAT,
            Opcode.INT_TO_BYTE, Opcode.INT_TO_CHAR, Opcode.INT_TO_SHORT,
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
            Opcode.CMP_LONG, Opcode.CMPL_FLOAT, Opcode.CMPG_FLOAT, Opcode.CMPL_DOUBLE, Opcode.CMPG_DOUBLE,
            Opcode.NEW_ARRAY,
            Opcode.NEW_INSTANCE,
            Opcode.ARRAY_LENGTH,
            Opcode.AGET, Opcode.AGET_WIDE, Opcode.AGET_OBJECT,
            Opcode.AGET_BOOLEAN, Opcode.AGET_BYTE, Opcode.AGET_CHAR, Opcode.AGET_SHORT,
            Opcode.APUT, Opcode.APUT_WIDE, Opcode.APUT_OBJECT,
            Opcode.APUT_BOOLEAN, Opcode.APUT_BYTE, Opcode.APUT_CHAR, Opcode.APUT_SHORT,
            Opcode.FILL_ARRAY_DATA,
            Opcode.FILLED_NEW_ARRAY, Opcode.FILLED_NEW_ARRAY_RANGE,
            Opcode.INSTANCE_OF,
            Opcode.CHECK_CAST,
            Opcode.SGET, Opcode.SGET_WIDE, Opcode.SGET_OBJECT,
            Opcode.SGET_BOOLEAN, Opcode.SGET_BYTE, Opcode.SGET_CHAR, Opcode.SGET_SHORT,
            Opcode.SPUT, Opcode.SPUT_WIDE, Opcode.SPUT_OBJECT,
            Opcode.SPUT_BOOLEAN, Opcode.SPUT_BYTE, Opcode.SPUT_CHAR, Opcode.SPUT_SHORT,
            Opcode.IGET, Opcode.IGET_WIDE, Opcode.IGET_OBJECT,
            Opcode.IGET_BOOLEAN, Opcode.IGET_BYTE, Opcode.IGET_CHAR, Opcode.IGET_SHORT,
            Opcode.IPUT, Opcode.IPUT_WIDE, Opcode.IPUT_OBJECT,
            Opcode.IPUT_BOOLEAN, Opcode.IPUT_BYTE, Opcode.IPUT_CHAR, Opcode.IPUT_SHORT,
            Opcode.INVOKE_STATIC, Opcode.INVOKE_STATIC_RANGE,
            Opcode.INVOKE_DIRECT, Opcode.INVOKE_DIRECT_RANGE,
            Opcode.INVOKE_SUPER, Opcode.INVOKE_SUPER_RANGE,
            Opcode.INVOKE_VIRTUAL, Opcode.INVOKE_VIRTUAL_RANGE,
            Opcode.INVOKE_INTERFACE, Opcode.INVOKE_INTERFACE_RANGE,
            Opcode.MONITOR_ENTER, Opcode.MONITOR_EXIT,
        )
        assertEquals(expected, OpBuilder.supportedOpcodes)
        // Guards the same direction as the const-class line below, for the opcodes that must stay OUT:
        // the `.array-data` pseudo-instruction and BOTH switch tables are DATA
        // (MethodView.payloadAddresses), so dispatching one would give a payload an executable meaning --
        // exactly what design decision 3 (arrays) and design decision 1 (switch) remove. **The two switch
        // rows now bite for the first time**: Plan 8 Task 6 dispatches PACKED_SWITCH/SPARSE_SWITCH two
        // lines above, so "the switch opcode is supported and its table is not" is a live distinction
        // here rather than a pair of trivially-true assertions about an unimplemented family.
        assertFalse(Opcode.ARRAY_PAYLOAD in OpBuilder.supportedOpcodes, "a payload is data, not code")
        assertFalse(Opcode.PACKED_SWITCH_PAYLOAD in OpBuilder.supportedOpcodes, "and so are both switch tables")
        assertFalse(Opcode.SPARSE_SWITCH_PAYLOAD in OpBuilder.supportedOpcodes, "and so are both switch tables")
        assertTrue(Opcode.PACKED_SWITCH in OpBuilder.supportedOpcodes, "while the switch INSTRUCTION is code")
        assertTrue(Opcode.SPARSE_SWITCH in OpBuilder.supportedOpcodes, "while the switch INSTRUCTION is code")
        // The POSITIVE pin, flipped from the negative one in the same Task 10 commit that dispatched the
        // opcode -- and added to the `expected` set literal above at the same time, or this test fails
        // either way. `const-class` is its OWN family ([OpBuilder]'s `CONST_CLASS_OPCODES`/`buildConstClass`)
        // rather than a case inside the plain `const` decode, because it is the one const form that resolves
        // a type operand through the class model, the same shape `instance-of`/`check-cast` take. Its value
        // is a HOST `Class` resolved WITHOUT initialization (Plan 8 Task 10, decisions 2-3); an unresolvable
        // operand degrades to `UnsupportedOp`, pinned separately below.
        assertTrue(Opcode.CONST_CLASS in OpBuilder.supportedOpcodes, "const-class landed in Plan 8 Task 10")
        // The POSITIVE pin for the type op's sibling, flipped from a negative one by Plan 8 Task 9 in the
        // same commit that dispatched the opcode. It shares `instance-of`'s predicate (`castOutcome`) and
        // maps three of its five outcomes to different answers -- a null register PASSES a cast and FAILS an
        // instance test, and an ambiguous one forks a cast while it merely un-folds an instance test. The
        // two entries below are separate lines rather than one membership check over a "type ops" group,
        // but that separateness does NOT catch a mis-wired build branch -- membership in `supportedOpcodes`
        // is independent of the `when` dispatch. The branch is pinned behaviorally (`CheckCastOpTest`/
        // `InstanceOfOpTest`, which build + execute through `CoreTester`) and by the `is CheckCastOp`/
        // `is InstanceOfOp` controls in `aCheckCastAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`
        // and `anInstanceOfAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`.
        assertTrue(Opcode.CHECK_CAST in OpBuilder.supportedOpcodes, "check-cast landed in Plan 8 Task 9")
        assertTrue(Opcode.INSTANCE_OF in OpBuilder.supportedOpcodes, "and its sibling in Task 8")
        // The POSITIVE pin for the allocation half of Plan 9, flipped in the same commit that dispatched
        // the opcode (Task 17). The INVOKE half is deliberately still absent -- `invoke-direct` on the
        // freshly allocated instance is Task 20's, which is why `ratchet-new-instance` remains in the
        // registry re-scoped to the `<init>` call rather than retiring here (see Fixtures.kt).
        assertTrue(Opcode.NEW_INSTANCE in OpBuilder.supportedOpcodes, "new-instance landed in Plan 9 Task 17")
        assertTrue(Opcode.INVOKE_DIRECT in OpBuilder.supportedOpcodes, "the <init> call landed in Task 20")
        // The POSITIVE pins for the static-field family, flipped in the same commit that dispatched all
        // twelve opcodes (Plan 9 Task 18) and added them to the `expected` set literal above. TWO pins
        // because they are TWO `when` rows on purpose (`OpBuilder`'s SGET_OPCODES/SPUT_OPCODES KDoc): the
        // ops share a preamble and not their answers, so one membership line could not witness both
        // dispatches. The build BRANCHES are pinned behaviorally by
        // `aSGetOrSPutAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal`'s `is SGetOp`/`is SPutOp`
        // controls -- membership here is independent of the `when` dispatch, as the check-cast note above
        // says.
        assertTrue(Opcode.SGET in OpBuilder.supportedOpcodes, "sget landed in Plan 9 Task 18")
        assertTrue(Opcode.SPUT in OpBuilder.supportedOpcodes, "and its writing sibling in the same task")
        assertTrue(Opcode.IGET in OpBuilder.supportedOpcodes, "iget landed in Plan 9 Task 19")
        assertTrue(Opcode.IPUT in OpBuilder.supportedOpcodes, "and its writing sibling in the same task")
        // ...and the ODEX-only forms stay OUT deliberately: never present in an APK's classes.dex, so
        // dispatching one would claim coverage no input can exercise (SGET_OPCODES' KDoc names the rule).
        assertFalse(Opcode.SGET_VOLATILE in OpBuilder.supportedOpcodes, "an ODEX form is not APK input")
        assertFalse(Opcode.SPUT_VOLATILE in OpBuilder.supportedOpcodes, "an ODEX form is not APK input")
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
     * The same decode-time refusal for `const-class`, and it needs its own test rather than riding on the
     * type ops': `const-class` is decoded by a THIRD function ([OpBuilder]'s `buildConstClass`) and is its
     * OWN family, so a missing resolution guard there is invisible to `instance-of`'s and `check-cast`'s
     * tests.
     *
     * **This is also the build-BRANCH pin this task owes** (`CARRY-FORWARD.md` Plan 8 item 10; the plan adds
     * the fifteenth family here). The dispatch-set test one method up covers the opcode SET, not the
     * `in CONST_CLASS_OPCODES -> buildConstClass(...)` BRANCH -- Plan 7 measured that deleting a family's
     * branch left the set test green while other tests died. **Option (b): a per-family assertion that the
     * family's fixture decodes to the expected `Op` subclass** -- the CONTROL below. Deleting the branch
     * drops `const-class` to `else -> UnsupportedOp`, so `is ConstOp` fails while the set stays intact.
     * Option (a) -- a fixture sweep with an exclusion list -- was rejected: the file already has one
     * (`everyFixtureAddressDecodesToASupportedOpThatEmitsAtLeastOneTransition`, over `Lspine/spine;`), and
     * `const-class`'s witnesses live in `Lconst_test;`, several of which exist in order to THROW rather than
     * to fall through; feeding them to a sweep would require growing an exclusion list to carry fixtures
     * whose whole purpose is the answer this test asserts directly. (Before Plan 9 Task 15,
     * `constClassUnknown` existed in order to decode to `UnsupportedOp`; it now decodes to a throwing
     * `ConstOp`, which is the same reason for the same rejection.)
     *
     * `check-cast`'s stakes argument applies a third time: an absent type would make this fold a wrong
     * `Class` (or invent one). 🔴 **Plan 9 Task 15 (ROW-23) replaced Plan 8's `Aborted(UNSUPPORTED_OP)`
     * expectation with the measured device answer** -- a definite guest `NoClassDefFoundError`,
     * `[DEVICE](Tier A, -Xint, art_standalone 0_git20251009-r2)` cross-validated
     * `[DEVICE](Tier B, nterp default, API 36)`,
     * `tools/art-runner/fixtures/Fx_ConstClassAbsentType.smali` against `Fx_ConstClassPresentType.smali`.
     * **The build-branch pin survives the change unaltered**, because it rides on the CONTROL (a resolvable
     * `const-class` must decode to a `ConstOp`), not on the refusal.
     */
    @Test
    fun aConstClassAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("constClassLocal()V", CONST)
        assertTrue(
            OpBuilder.build(0, present, classIndex) is ConstOp,
            "the CONTROL: a resolvable class-constant decodes to a real ConstOp, so the throw below is " +
                "about the type -- and deleting the const-class build branch drops this to UnsupportedOp",
        )
        assertTrue(
            executeSingle(OpBuilder.build(0, present, classIndex), Frame.entryFrame(present)) is OpArm.Continue,
            "and it falls through, so the throw below is not `every const-class throws`",
        )

        val v = view("constClassUnknown()V", CONST)
        val op = OpBuilder.build(0, v, classIndex)
        assertTrue(op is ConstOp, "an unresolvable class-constant throws at execute; it is not refused")
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
            OpBuilder.build(0, absent, classIndex) is ConstOp,
            "an ABSENT class still decodes to a throwing ConstOp -- the two failure modes stay distinct",
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
     * A real fixture's [MethodView] with its address→instruction map EDITED, so a payload target can name
     * an address the view does not contain or one holding something else. Same device and same reason as
     * `CommitTest.forgedTryBlocks`: no smali can express it, because the assembler resolves every label and
     * -- for a switch specifically -- refuses a target that is not a payload.
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
        return MethodView(
            signature = real.signature,
            definingClass = real.definingClass,
            returnType = real.returnType,
            isStatic = real.isStatic,
            registerCount = real.registerCount,
            parameters = real.parameters,
            tryBlocks = real.tryBlocks,
            addressToInstruction = instructions,
            programRevision = real.programRevision,
            methodRevision = real.methodRevision,
        )
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
