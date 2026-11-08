package org.cf.smalivm.core

import org.cf.smalivm.core.op.CheckCastOp
import org.cf.smalivm.core.op.ConstOp
import org.cf.smalivm.core.op.ConstClassOp
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
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import java.io.File

/**
 * Decodes each op family and executes it for one step, so a decode or arm error is localized before the
 * scheduler is involved. Bug class: an op that reads the wrong operand, keeps per-execution state, or emits the
 * wrong arm. Expected values come from the Dalvik bytecode spec ([BC]) and `docs/DALVIK-SEMANTICS.md`.
 */
class OpBuilderTest {
    private val classIndex = ClassIndex.open(File("src/test/resources/smali"))
    private val resolver = classIndex.currentResolver()
    private val int = VirtualType.of("I")
    private val long = VirtualType.of("J")
    private val noClassDefFound = VirtualType.of("Ljava/lang/NoClassDefFoundError;")
    private val FILLED_NEW_ARRAY = "Lfilled_new_array_test;"
    private val INSTANCE_OF = "Linstanceof_test;"
    private val CHECK_CAST = "Lcheck_cast_test;"
    private val CONST = "Lconst_test;"
    private val SGET = "Lsget_test;"
    private val SPUT = "Lsput_test;"
    private val IGET = "Liget_test;"
    private val IPUT = "Liput_test;"

    /** [className] defaults to the fixture most tests in this file drive. */
    private fun view(descriptor: String, className: String = "Lspine/spine;") =
        classIndex.requireMethodView(className, descriptor)

    private fun op(descriptor: String, address: Int) = OpBuilder.build(address, view(descriptor), resolver)

    private fun opAt(className: String, methodDescriptor: String, address: Int): Op =
        OpBuilder.build(address, view(methodDescriptor, className), resolver)

    private fun executeSingle(op: Op, frame: Frame): OpArm {
        val arm = op.execute(frame).arms.single()
        val applied = arm.update.applyTo(frame)
        applied.result?.let(frame::setResult)
        if (arm is OpArm.MoveFromSlot) {
            val value = when (arm.source) {
                SpecialSlotSource.RESULT -> frame.takeResult()?.value
                SpecialSlotSource.CAUGHT -> frame.takeCaught()?.value
            } ?: Value.Top
            frame.assign(arm.register, value)
        }
        return arm
    }

    private fun throwType(arm: OpArm.Throw): VirtualType = when (val evidence = arm.evidence) {
        is ThrowEvidence.Existing -> requireNotNull(evidence.value.type)
        is ThrowEvidence.Fresh -> evidence.recipe.kind.type
    }

    /** Asserts [op] reports [name] and its one arm on an entry frame of [view] throws `NoClassDefFoundError`. */
    private fun assertThrowsNoClassDefFoundError(op: Op, view: MethodView, name: String) {
        assertEquals(name, op.name, "the op reports its real opcode")
        val arms = op.execute(Frame.entryFrame(view)).arms
        assertEquals(1, arms.size, "a definite throw, not a fork: $arms")
        val arm = arms.single()
        assertTrue(arm is OpArm.Throw, "expected a Throw, got $arm")
        assertEquals(noClassDefFound, throwType(arm as OpArm.Throw))
    }

    @Test
    fun constAssignsItsLiteralAndContinues() {
        // Bug class: a const whose literal is decoded at execution time or not at all. Oracle: [BC]
        // `const/4 vA, #+B` writes the sign-extended literal to vA and falls through; the fixture's literal is 0x5.
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
        // Bug class: a wide value that does not reserve its register pair, so the high half reads back as an
        // independent value. Oracle: [BC] `const-wide vAA, #+BBBBBBBBBBBBBBBB` writes the pair (vAA, vAA+1).
        val v = view("returnWide()J")
        val frame = Frame.entryFrame(v)
        assertTrue(executeSingle(op("returnWide()J", 0), frame) is OpArm.Continue)
        val value = frame.peek(0) as Value.Known
        assertEquals(0x123456789L, value.raw)
        assertTrue(value.type.isWide)
        assertEquals(Value.Top, frame.read(1), "the wide high half is not independently readable")
    }

    /**
     * Dalvik's const opcodes encode only a literal's width, so narrow forms are tagged `I` and wide forms `J`
     * (DALVIK-SEMANTICS.md, "Registers and the null idiom"). Each row asserts the whole value: dexlib's
     * `NarrowLiteralInstruction` extends `WideLiteralInstruction`, so a wide literal read through the narrow
     * accessor truncates to 32 bits and mistags as `I` without throwing. `const-wide/32` shares Format31i with
     * `const` and is the form exposed to that slip. The test program contains a `const-string/jumbo`, so the importer
     * encodes every string constant in it as `const-string/jumbo`, and the spine's `const-string` decodes as that form.
     */
    @TestFactory
    fun constantFormsDecodeTheirLiteralUnderTheirWidthTag(): List<DynamicTest> = listOf(
        ConstCase("const/4", "returnFive()I", 0, Value.Known(0x5, int)),
        ConstCase("const/16", "nopThenConst()I", 2, Value.Known(0x2a, int)),
        ConstCase("const", "constThirtyTwo()I", 0, Value.Known(0x42424242, int)),
        ConstCase("const/high16", "constHighSixteen()I", 0, Value.Known(0x42420000, int)),
        ConstCase(
            "const-string/jumbo", "returnString()Ljava/lang/String;", 0,
            Value.Known("spine", VirtualType.of("Ljava/lang/String;")),
        ),
        ConstCase("const-wide", "returnWide()J", 0, Value.Known(0x123456789L, long)),
        ConstCase("const-wide/16", "returnWideSixteen()J", 0, Value.Known(0x2aL, long)),
        ConstCase("const-wide/32", "returnWideThirtyTwo()J", 0, Value.Known(0x1b7740L, long)),
        ConstCase("const-wide/high16", "returnWideHighSixteen()J", 0, Value.Known(0x4242000000000000L, long)),
    ).map { case ->
        DynamicTest.dynamicTest("${case.mnemonic} -> ${case.expected}") {
            val frame = Frame.entryFrame(view(case.method))
            val op = op(case.method, case.address)
            assertEquals(case.mnemonic, op.name)
            assertTrue(executeSingle(op, frame) is OpArm.Continue)
            // A wide literal also assigns its pair's high half; only the low register holds the value.
            assertEquals(case.expected, frame.peek(op.registersAssigned.minOrNull()!!))
        }
    }

    /**
     * An opcode without a decoder row decodes to an [UnsupportedOp] with no other signal, and most opcodes appear in
     * no fixture. dexlib2's own metadata gives the expected set: exactly the payload formats and ODEX-only opcodes, plus the
     * method-handle family the engine does not model, are unsupported.
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

    @Test
    fun moveCopiesRegisterAndContinues() {
        // Bug class: operands swapped (dexlib's registerA is the destination, registerB the source), which
        // clobbers the source. Oracle: [BC] `move vA, vB` copies vB into vA.
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
        // Bug class: `move-exception` treated as a two-register move, or the caught value left in the slot so
        // a second handler entry re-reads a stale exception. Oracle: [BC] `move-exception vAA` moves the caught
        // throwable into vAA; the slot holds it only until that move.
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
        assertNull(frame.takeCaught()?.value, "move-exception consumes the slot")
    }

    @Test
    fun moveExceptionWithNoCaughtValueYieldsTop() {
        // Bug class: an empty caught slot dereferenced, or defaulted to a placeholder a strategy would trust.
        // An ill-formed handler entry must not fabricate a value, so the register is Top.
        val v = view("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(v)
        val address = v.addresses.first { v.opcodeAt(it) == Opcode.MOVE_EXCEPTION }
        executeSingle(op("throwCaught(Ljava/lang/RuntimeException;)Ljava/lang/Object;", address), frame)
        assertEquals(Value.Top, frame.read(0))
    }

    @Test
    fun returnEmitsReturnArmCarryingTheValue() {
        // Bug class: a return that ends the path without its value, leaving the graph's consensus nothing to
        // reconcile. Oracle: [BC] `return vAA` ends the method with vAA's value.
        val v = view("returnFive()I")
        val frame = Frame.entryFrame(v)
        frame.assign(0, Value.Known(5, int))
        val op = op("returnFive()I", 1)
        assertTrue(op is ReturnOp)
        assertEquals(OpArm.Return(Value.Known(5, int)), executeSingle(op, frame))
    }

    @Test
    fun returnVoidEmitsReturnWithNoValue() {
        // Bug class: `return-void` decoded as a one-register instruction, which crashes on the cast or invents a
        // return value from register 0. Oracle: [BC] `return-void` ends the method with no value.
        val v = view("returnNothing()V")
        val address = v.addresses.first { v.opcodeAt(it) == Opcode.RETURN_VOID }
        assertEquals(OpArm.Return(null), executeSingle(op("returnNothing()V", address), Frame.entryFrame(v)))
    }

    @Test
    fun gotoJumpsToItsResolvedTarget() {
        // Bug class: a branch target taken as the raw code offset rather than the resolved address, landing on
        // an address that may not start an instruction. Oracle: [BC] `goto +AA` jumps to this instruction's
        // address plus the offset; this fixture's label is on the trailing `return`.
        val v = view("gotoSkip()I")
        val gotoAddress = v.addresses.first { v.opcodeAt(it) == Opcode.GOTO }
        val op = op("gotoSkip()I", gotoAddress)
        assertTrue(op is GotoOp)
        val jump = op.execute(Frame.entryFrame(v)).arms.single()
        assertTrue(jump is OpArm.Jump, "goto emits exactly one Jump arm, got $jump")
        jump as OpArm.Jump
        assertTrue(jump.address > gotoAddress, "this fixture jumps forward")
        assertEquals(Opcode.RETURN, v.opcodeAt(jump.address), "the label lands on the return")
    }

    @Test
    fun nopJustContinues() {
        // `nop` also pads in front of a payload, so it must not read, assign, or branch. Oracle: [BC] `nop`
        // does nothing.
        val v = view("nopThenConst()I")
        val op = op("nopThenConst()I", 0)
        assertTrue(op is NopOp)
        assertTrue(executeSingle(op, Frame.entryFrame(v)) is OpArm.Continue)
        assertEquals(emptySet<Int>(), op.registersRead)
        assertEquals(emptySet<Int>(), op.registersAssigned)
    }

    @Test
    fun throwEmitsThrowArmWithTheRegisterValue() {
        // Bug class: a throw that reports a class and message instead of the guest throwable, so the handler
        // receives a reconstruction. Oracle: [BC] `throw vAA` throws the object in vAA.
        val v = view("throwUncaught(Ljava/lang/RuntimeException;)V")
        val frame = Frame.entryFrame(v)
        val thrown = Value.Known("boom", VirtualType.of("Ljava/lang/RuntimeException;"))
        frame.assign(1, thrown)
        val op = op("throwUncaught(Ljava/lang/RuntimeException;)V", 0)
        assertTrue(op is ThrowOp)
        assertEquals(ThrowEvidence.Existing(thrown), (executeSingle(op, frame) as OpArm.Throw).evidence)
    }

    @Test
    fun oneOpInstanceDrivesTwoFramesIndependently() {
        // Bug class: an op that caches per-execution state on itself. One op per address is shared by every
        // path that reaches it, so all mutable state must live in the Frame.
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
     * sdbg's step command prints an op's rendering, so mnemonic and operand order are a consumer-visible format.
     * One row per instruction shape. Operands print in the instruction's field order (vA first); branch and switch
     * targets print as absolute addresses because every target is resolved at decode time.
     */
    @TestFactory
    fun opsRenderTheirOperandsInTheirDeclaredOrder(): List<DynamicTest> = listOf(
        rendering("add-int r0, r0, r1", "Lbinary_math_test;", "addInt()V"),
        // A `/2addr` form prints vA twice: it is both destination and first operand.
        rendering("add-int/2addr r0, r0, r1", "Lbinary_math_test;", "addInt2Addr()V"),
        rendering("add-int/lit8 r0, r0, 0xf", "Lbinary_math_test;", "addIntLit8()V"),
        rendering("double-to-float r0, r0", "Lunary_math_test;", "doubleToFloat()V"),
        rendering("cmp-long r0, r0, r2", "Lcmp_test;", "cmpLong()V"),
        // The only arithmetic fixture whose destination and first operand differ, so a swap of those fields fails.
        rendering("div-int r0, r2, r0", "Lbinary_math_test;", "divIntUnknownDividendWithCatch(I)V"),
        // `aget` and `aput` share the 23x shape with different first operands (destination against value), so
        // each family's field mapping needs its own row.
        rendering("aget r2, r0, r1", "Laget_test;", "getWithThreeDistinctRegisters()V"),
        rendering("aput r2, r0, r1", "Laput_test;", "put()V"),
        rendering("iget r1, r0, Liget_test;->myInt:I", IGET, "getIntFromFreshObject()I", 2),
        rendering("iput r1, r0, Liput_test;->myInt:I", IPUT, "putInt()Liput_test;", 4),
        // The payload is decoded into the op at build time, so no payload address is left to print.
        rendering("fill-array-data r0, :payload", "Lfill_array_data_test;", "fillArrayDataInt()V"),
        // The `22t` form renders two registers and the `*z` (21t) form one.
        rendering("if-eq r0, r1, :addr_3", "Lif_test;", "ifEqual()V"),
        rendering("if-eqz r0, :addr_3", "Lif_test;", "ifEqualZero()V"),
        // Both switches sit at address 1, so an offset relative to the switch differs from the absolute target.
        rendering("packed-switch r0, [1 -> :addr_5, 2 -> :addr_6, 3 -> :addr_7]", "Lswitch_test;", "packedSwitch()V", 1),
        rendering("sparse-switch r0, [1 -> :addr_5, 2 -> :addr_6]", "Lswitch_test;", "sparseSwitch()V", 1),
        rendering("filled-new-array {r0, r1, r2}, [I", FILLED_NEW_ARRAY, "filledNewArrayThenMoveResult()V"),
        rendering("filled-new-array/range {r0 .. r5}, [I", FILLED_NEW_ARRAY, "filledNewArrayRangeThenMoveResult()V"),
        // A 3rc instruction may name five or fewer registers, so the range spelling follows the opcode, not the
        // register count.
        rendering("filled-new-array/range {r0 .. r4}, [I", FILLED_NEW_ARRAY, "filledNewArrayRangeFive()V"),
        rendering("monitor-enter r3", "Lmonitor_test;", "enter(Ljava/lang/Object;)V"),
        rendering("monitor-exit r2", "Lmonitor_test;", "exit(Ljava/lang/Object;)V"),
    )

    private fun rendering(expected: String, className: String, method: String, address: Int = 0): DynamicTest =
        DynamicTest.dynamicTest(expected) { assertEquals(expected, opAt(className, method, address).toString()) }

    /**
     * [BC]: the type "must be an array type" whose contents "must be single-word (that is, no arrays of long or
     * double...)". The decoder refuses both, so the path aborts with `UNSUPPORTED_OP` instead of a host failure or
     * a wide component filled one word at a time (DALVIK-SEMANTICS.md, "`filled-new-array` (op 24, 35c) and
     * `/range` (op 25, 3rc)").
     */
    @TestFactory
    fun aFilledNewArrayWithNoFillableComponentTypeIsUnsupportedRatherThanAHostFailure(): List<DynamicTest> = listOf(
        "long[] component" to "filledNewArrayOfLongs()V",
        "non-array type I" to "filledNewArrayOfNonArrayType()V",
    ).map { (case, descriptor) ->
        DynamicTest.dynamicTest(case) {
            val v = view(descriptor, FILLED_NEW_ARRAY)
            val op = OpBuilder.build(0, v, resolver)
            assertTrue(op is UnsupportedOp, "expected an UnsupportedOp, got $op")
            assertEquals("filled-new-array", op.name, "the op reports its real opcode")
            val frame = Frame.entryFrame(v)
            assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, frame))
            assertEquals(emptySet<Int>(), frame.registersAssigned(), "an aborted op must not half-apply its effect")
        }
    }

    /**
     * An absent type operand decodes to a real op that throws a guest `NoClassDefFoundError` at execute
     * (DALVIK-SEMANTICS.md, "Resolution failure is one family, tiered once"; device fixture
     * `Fx_InstanceOfAbsentType`). The resolvable control rules out an op that throws unconditionally. A primitive
     * operand is loadable and [BC] gives it a definite answer ("always result in 0 being stored"), so it must not
     * throw.
     */
    @Test
    fun anInstanceOfAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("instanceOfString()V", INSTANCE_OF)
        val control = OpBuilder.build(0, present, resolver)
        assertTrue(control is InstanceOfOp, "a resolvable operand decodes to a real op")
        assertTrue(executeSingle(control, Frame.entryFrame(present)) is OpArm.Continue, "and falls through")

        val v = view("instanceOfAbsentClass()V", INSTANCE_OF)
        val op = OpBuilder.build(0, v, resolver)
        assertTrue(op is InstanceOfOp, "an unresolvable type operand throws at execute; it is not refused")
        assertThrowsNoClassDefFoundError(op, v, "instance-of")

        val primitive = view("instanceOfPrimitiveInt()V", INSTANCE_OF)
        val primitiveOp = OpBuilder.build(0, primitive, resolver)
        assertTrue(primitiveOp is InstanceOfOp, "a primitive type operand decodes to a real op")
        assertTrue(
            executeSingle(primitiveOp, Frame.entryFrame(primitive)) is OpArm.Continue,
            "a primitive operand answers rather than throws",
        )
    }

    /**
     * `check-cast` has its own decoder row, so it needs its own absent-type case: a guest `NoClassDefFoundError`,
     * neither `ClassCastException` nor success (device fixture `Fx_CheckCastAbsentType`). The control shows a
     * resolvable cast raises no linking error, so the throw is about resolution.
     */
    @Test
    fun aCheckCastAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("castToString()V", CHECK_CAST)
        val control = OpBuilder.build(0, present, resolver)
        assertTrue(control is CheckCastOp, "a resolvable operand decodes to a real op")
        assertTrue(
            control.execute(Frame.entryFrame(present)).arms
                .none { it is OpArm.Throw && throwType(it) == noClassDefFound },
            "a resolvable cast raises no linking error",
        )

        val v = view("castToAbsentClass()V", CHECK_CAST)
        val op = OpBuilder.build(0, v, resolver)
        assertTrue(op is CheckCastOp, "an unresolvable type operand throws at execute; it is not refused")
        assertThrowsNoClassDefFoundError(op, v, "check-cast")
    }

    /**
     * `const-class` has its own decoder row. An absent class decodes to an op that throws a guest
     * `NoClassDefFoundError` at execute (device fixture `Fx_ConstClassAbsentType`); a resolvable class falls through.
     */
    @Test
    fun aConstClassAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("constClassLocal()V", CONST)
        val control = OpBuilder.build(0, present, resolver)
        assertTrue(control is ConstClassOp, "a resolvable class constant decodes to a real op")
        assertTrue(executeSingle(control, Frame.entryFrame(present)) is OpArm.Continue, "and falls through")

        val v = view("constClassUnknown()V", CONST)
        val op = OpBuilder.build(0, v, resolver)
        assertTrue(op is ConstClassOp, "an unresolvable class constant throws at execute; it is not refused")
        assertThrowsNoClassDefFoundError(op, v, "const-class")
    }

    /**
     * `sget` and `sput` have separate decoder rows, so each gets a control and an absent-type row. A resolvable
     * `sget` on an uninitialized class answers `InitClass`; the absent row throws with no `InitClass` first because
     * resolution precedes initialization (device fixture `Fx_SGetAbsentType`). `sput` resolves the same field
     * reference through the same path (DALVIK-SEMANTICS.md, "Resolution failure is one family, tiered once").
     */
    @Test
    fun aSGetOrSPutAgainstAnAbsentTypeDecodesToAThrowingOpRatherThanARefusal() {
        val present = view("getStaticIntLiteral()V", SGET)
        val control = OpBuilder.build(0, present, resolver)
        assertTrue(control is SGetOp, "a resolvable field decodes to a real SGetOp")
        assertEquals(
            OpArm.InitClass(VirtualType.of(SGET)),
            executeSingle(control, Frame.entryFrame(present)),
            "a resolvable sget on an uninitialized class asks for class init first",
        )
        val v = view("getStaticUnknownClassField()V", SGET)
        val op = OpBuilder.build(0, v, resolver)
        assertTrue(op is SGetOp, "an unresolvable field reference throws at execute; it is not refused")
        assertThrowsNoClassDefFoundError(op, v, "sget")

        // The value register is never read on the failing path, so an entry frame with v0 unassigned works.
        assertTrue(OpBuilder.build(0, view("putStaticInt()V", SPUT), resolver) is SPutOp)
        val sputAbsent = view("putStaticUnknownClassField()V", SPUT)
        val sputOp = OpBuilder.build(0, sputAbsent, resolver)
        assertTrue(sputOp is SPutOp, "sput's unresolvable reference throws at execute; it is not refused")
        assertThrowsNoClassDefFoundError(sputOp, sputAbsent, "sput")
    }

    /**
     * A class the model holds but the host loader cannot load is an engine limitation, so `const-class` aborts with
     * `UNSUPPORTED_OP`; a guest `NoClassDefFoundError` would invent a path a device never takes. `SmaliClassLoader`
     * refuses every `java.` name and the JVM has no Android-only `java.*` class, so `Ljava/lang/DexCache;` is such a
     * class. Both halves of that premise are asserted, so a framework or JVM change fails here instead of silently
     * testing nothing.
     */
    @Test
    fun aConstClassOnAModelledButHostUnloadableClassIsUnsupportedRatherThanAResolutionFailure() {
        val hostUnloadable = "Ljava/lang/DexCache;"
        assertTrue(
            classIndex.currentSnapshot.hierarchy.isLoadable(VirtualType.of(hostUnloadable)),
            "$hostUnloadable is in the bundled class model, so it is not absent",
        )
        assertNull(
            classIndex.currentResolver().loadClassConstant(hostUnloadable),
            "the host loader cannot produce a Class for $hostUnloadable",
        )

        val v = view("constClassHostUnloadable()V", CONST)
        val op = OpBuilder.build(0, v, resolver)
        assertTrue(op is UnsupportedOp, "an engine limitation is UNSUPPORTED_OP, never a guest throw: $op")
        assertEquals("const-class", op.name, "the op reports its real opcode")
        assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, Frame.entryFrame(v)))

        assertTrue(
            OpBuilder.build(0, view("constClassUnknown()V", CONST), resolver) is ConstClassOp,
            "an absent class still decodes to a throwing ConstClassOp",
        )
    }

    @Test
    fun aFillArrayDataWhoseTargetIsNotAnArrayPayloadIsUnsupportedRatherThanAHostFailure() {
        // Bug class: a failed cast or `MethodView.instructionAt`'s `requireNotNull` escaping the decoder as a host
        // failure. The fixture's fill-array-data targets an ordinary `return-void`, which smali assembles because
        // the operand is just a label.
        val v = view("fillArrayDataWithMalformedPayloadTarget()V", "Lfill_array_data_test;")
        assertTrue(v.payloadAddresses.isEmpty(), "the fixture contains no payload")
        val op = OpBuilder.build(0, v, resolver)
        assertTrue(op is UnsupportedOp, "a malformed payload target is unsupported, not a decode failure")
        assertEquals("fill-array-data", op.name, "the op reports its real opcode")
        assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, Frame.entryFrame(v)))
    }

    /**
     * The switch counterpart of the test above. The assembler rejects a switch whose target is not a payload, so
     * the input is a forged view, one per guard: the payload address missing (`instructionAt`'s `requireNotNull`),
     * and the payload address holding an ordinary instruction (a failed cast).
     */
    @Test
    fun aSwitchWhoseTableIsMissingOrIsNotATableIsUnsupportedRatherThanAHostFailure() {
        val real = view("packedSwitch()V", "Lswitch_test;")
        val payload = real.payloadAddresses.single()
        assertTrue(OpBuilder.build(1, real, resolver) is SwitchOp, "the unforged fixture decodes to a real switch")

        val missing = forgedView("Lswitch_test;", "packedSwitch()V") { it.remove(payload) }
        val onMissing = OpBuilder.build(1, missing, resolver)
        assertTrue(onMissing is UnsupportedOp, "a payload target that is not an address is unsupported, got $onMissing")
        assertEquals("packed-switch", onMissing.name, "the op reports its real opcode")
        assertEquals(
            OpArm.Abort(AbortReason.UNSUPPORTED_OP),
            executeSingle(onMissing, Frame.entryFrame(missing)),
        )

        // The payload address now holds the method's `return-void`.
        val notATable = forgedView("Lswitch_test;", "packedSwitch()V") { it[payload] = it.getValue(4) }
        val onNotATable = OpBuilder.build(1, notATable, resolver)
        assertTrue(onNotATable is UnsupportedOp, "a payload target that is not a table is unsupported, got $onNotATable")
        assertEquals("packed-switch", onNotATable.name)
        assertEquals(
            OpArm.Abort(AbortReason.UNSUPPORTED_OP),
            executeSingle(onNotATable, Frame.entryFrame(notATable)),
        )
    }

    /**
     * A fixture method with its address→instruction map edited and rebuilt through the body importer, so a payload
     * target can name an address the method lacks or one holding something else, which no smali can express.
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

    @Test
    fun aPayloadPseudoInstructionDecodesToAnUnsupportedOpSoNothingCanExecuteIt() {
        // A payload is data. If a malformed jump targets one, aborting that path is the fail-safe answer rather
        // than running a table as code.
        val v = view("fillArrayDataInt()V", "Lfill_array_data_test;")
        val op = OpBuilder.build(v.payloadAddresses.single(), v, resolver)
        assertTrue(op is UnsupportedOp, "a payload pseudo-instruction is not an executable op")
        assertEquals("array-payload", op.name)
        assertEquals(OpArm.Abort(AbortReason.UNSUPPORTED_OP), executeSingle(op, Frame.entryFrame(v)))
    }

    private data class ConstCase(val mnemonic: String, val method: String, val address: Int, val expected: Value)
}
