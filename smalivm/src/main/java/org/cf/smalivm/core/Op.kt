package org.cf.smalivm.core

import org.cf.smalivm.core.op.AGetOp
import org.cf.smalivm.core.op.APutOp
import org.cf.smalivm.core.op.ArrayLengthOp
import org.cf.smalivm.core.op.BinaryMathOp
import org.cf.smalivm.core.op.BinaryOperation
import org.cf.smalivm.core.op.CheckCastOp
import org.cf.smalivm.core.op.CmpOp
import org.cf.smalivm.core.op.CmpOperation
import org.cf.smalivm.core.op.ConstClassOp
import org.cf.smalivm.core.op.ConstOp
import org.cf.smalivm.core.op.FillArrayDataOp
import org.cf.smalivm.core.op.FilledNewArrayOp
import org.cf.smalivm.core.op.FloatingOperator
import org.cf.smalivm.core.op.GotoOp
import org.cf.smalivm.core.op.IGetOp
import org.cf.smalivm.core.op.IPutOp
import org.cf.smalivm.core.op.IfComparison
import org.cf.smalivm.core.op.IfOp
import org.cf.smalivm.core.op.InstanceOfOp
import org.cf.smalivm.core.op.IntOperator
import org.cf.smalivm.core.op.InvokeOp
import org.cf.smalivm.core.op.LongOperator
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
import org.cf.smalivm.core.op.TypeOperand
import org.cf.smalivm.core.op.UnaryMathOp
import org.cf.smalivm.core.op.UnaryOperation
import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.builder.BuilderOffsetInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.NarrowLiteralInstruction
import com.android.tools.smali.dexlib2.iface.instruction.OffsetInstruction
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
import com.android.tools.smali.dexlib2.iface.reference.Reference
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
     * The register set a single-destination op assigns: a wide (`J`/`D`) result owns [dest] and `dest + 1`.
     * [ExecutionGraph.availableRegisters] derives free scratch registers from these sets, and a pair's high
     * half reads as unassigned, so naming only the low half would offer a live register as scratch.
     */
    internal fun destinationRegisters(dest: Int, isWide: Boolean): Set<Int> =
        if (isWide) setOf(dest, dest + 1) else setOf(dest)

    override fun toString(): String = "$name@$address"
}

/**
 * An instruction this build cannot execute. It aborts only its own path, and the run's completion becomes
 * Partial, so no consumer trusts a truncated exploration (spec §6.4).
 */
internal class UnsupportedOp(address: Int, override val name: String) : Op(address) {
    override fun execute(frame: Frame): OpExecution = OpExecution.single(OpArm.Abort(AbortReason.UNSUPPORTED_OP))
}

/** Builds the op for one instruction; its decoder row has already fixed every fact the opcode carries. */
private typealias Decoder = (Decoding) -> Op

/** One instruction being decoded, with its operands read off the dexlib2 instruction. */
private class Decoding(
    val address: Int,
    val instruction: Instruction,
    val view: MethodView,
    val resolver: ProgramSnapshotResolver,
) {
    val name: String get() = instruction.opcode.name
    val classIndex: ClassIndex get() = resolver.classIndex
    val unsupported: UnsupportedOp get() = UnsupportedOp(address, name)

    val registerA: Int get() = (instruction as OneRegisterInstruction).registerA
    val registerB: Int get() = (instruction as TwoRegisterInstruction).registerB
    val registerC: Int get() = (instruction as ThreeRegisterInstruction).registerC
    val reference: Reference get() = (instruction as ReferenceInstruction).reference
    val typeReference: VirtualType get() = classIndex.typeOf((reference as TypeReference).type)

    /** dexlib resolves the label, so the target is a real code address and never a raw offset. */
    val target: Int
        get() = view.controlTargetAt(address) ?: when (instruction) {
            is BuilderOffsetInstruction -> instruction.target.codeAddress
            is OffsetInstruction -> address + instruction.codeOffset
            else -> error("${instruction.opcode.name} is not an offset instruction")
        }

    /** The register list of a `35c` or `3rc` instruction. */
    val registerList: IntArray?
        get() {
            val count = (instruction as VariableRegisterInstruction).registerCount
            return when (instruction) {
                is RegisterRangeInstruction -> IntArray(count) { instruction.startRegister + it }
                is FiveRegisterInstruction -> intArrayOf(
                    instruction.registerC, instruction.registerD, instruction.registerE,
                    instruction.registerF, instruction.registerG,
                ).copyOf(count)
                else -> null
            }
        }

    /** A type operand, present when the class is in the loaded set. */
    fun typeOperand(type: VirtualType): TypeOperand<Unit> =
        if (classIndex.isLoadable(type)) TypeOperand.Present(type, Unit) else TypeOperand.Absent(type)
}

/**
 * Decodes the instruction at an address into an [Op]. Every operand is resolved here, once, so ops receive
 * register numbers, literals, target addresses and core types and never touch dexlib2 or the class model.
 *
 * Each opcode maps to one decoder row that carries the opcode's operator, domain, comparison or store kind
 * as a typed value. A malformed instruction (a payload target that is not a payload) decodes to an
 * [UnsupportedOp] rather than throwing, because a decode throwable becomes
 * `Aborted(HOST_FAILURE)`, indistinguishable from an engine bug.
 */
internal object OpBuilder {
    fun build(address: Int, view: MethodView, classIndex: ClassIndex): Op =
        build(address, view, classIndex.resolver(classIndex.currentSnapshot))

    fun build(address: Int, view: MethodView, resolver: ProgramSnapshotResolver): Op {
        // A payload is data: something that jumps to one aborts rather than executing a table as code.
        if (view.isPayload(address)) return UnsupportedOp(address, view.payloadAt(address).opcode.name)
        val instruction = view.instructionAt(address)
        val decoding = Decoding(address, instruction, view, resolver)
        return decoderFor(instruction.opcode)?.invoke(decoding) ?: decoding.unsupported
    }

    /** Whether [opcode] has a decoder row; every other opcode decodes to an [UnsupportedOp]. */
    internal fun decodes(opcode: Opcode): Boolean = decoderFor(opcode) != null

    private fun decoderFor(opcode: Opcode): Decoder? = when (opcode) {
        Opcode.NOP -> { d -> NopOp(d.address) }
        Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32 -> { d -> GotoOp(d.address, d.name, d.target) }
        Opcode.IF_EQ, Opcode.IF_EQZ -> branch(IfComparison.EQ)
        Opcode.IF_NE, Opcode.IF_NEZ -> branch(IfComparison.NE)
        Opcode.IF_LT, Opcode.IF_LTZ -> branch(IfComparison.LT)
        Opcode.IF_GE, Opcode.IF_GEZ -> branch(IfComparison.GE)
        Opcode.IF_GT, Opcode.IF_GTZ -> branch(IfComparison.GT)
        Opcode.IF_LE, Opcode.IF_LEZ -> branch(IfComparison.LE)
        Opcode.PACKED_SWITCH -> switch(SwitchDispatch.PACKED)
        Opcode.SPARSE_SWITCH -> switch(SwitchDispatch.SPARSE)
        Opcode.THROW -> { d -> ThrowOp(d.address, d.registerA) }
        Opcode.RETURN_VOID -> { d -> ReturnOp(d.address, d.name, null) }
        Opcode.RETURN, Opcode.RETURN_WIDE, Opcode.RETURN_OBJECT -> { d -> ReturnOp(d.address, d.name, d.registerA) }
        Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16,
        Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16 -> move(isWide = false)
        Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16 -> move(isWide = true)
        Opcode.MOVE_EXCEPTION -> moveFromSlot(SpecialSlotSource.CAUGHT, isWide = false)
        Opcode.MOVE_RESULT, Opcode.MOVE_RESULT_OBJECT -> moveFromSlot(SpecialSlotSource.RESULT, isWide = false)
        Opcode.MOVE_RESULT_WIDE -> moveFromSlot(SpecialSlotSource.RESULT, isWide = true)
        // A const opcode states only its literal's width, so narrow forms are tagged `I` and wide forms `J`;
        // a float or double reader reinterprets the bits through `Coerce`.
        Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST, Opcode.CONST_HIGH16 -> { d ->
            ConstOp(d.address, d.name, d.registerA, Value.Known((d.instruction as NarrowLiteralInstruction).narrowLiteral, INT))
        }
        Opcode.CONST_WIDE_16, Opcode.CONST_WIDE_32, Opcode.CONST_WIDE, Opcode.CONST_WIDE_HIGH16 -> { d ->
            ConstOp(d.address, d.name, d.registerA, Value.Known((d.instruction as WideLiteralInstruction).wideLiteral, LONG))
        }
        Opcode.CONST_STRING, Opcode.CONST_STRING_JUMBO -> { d ->
            ConstOp(d.address, d.name, d.registerA, Value.Known((d.reference as StringReference).string, STRING))
        }
        Opcode.CONST_CLASS -> this::constClass
        Opcode.NEG_INT -> unary(UnaryOperation.NEG_INT)
        Opcode.NOT_INT -> unary(UnaryOperation.NOT_INT)
        Opcode.NEG_LONG -> unary(UnaryOperation.NEG_LONG)
        Opcode.NOT_LONG -> unary(UnaryOperation.NOT_LONG)
        Opcode.NEG_FLOAT -> unary(UnaryOperation.NEG_FLOAT)
        Opcode.NEG_DOUBLE -> unary(UnaryOperation.NEG_DOUBLE)
        Opcode.INT_TO_LONG -> unary(UnaryOperation.INT_TO_LONG)
        Opcode.INT_TO_FLOAT -> unary(UnaryOperation.INT_TO_FLOAT)
        Opcode.INT_TO_DOUBLE -> unary(UnaryOperation.INT_TO_DOUBLE)
        Opcode.LONG_TO_INT -> unary(UnaryOperation.LONG_TO_INT)
        Opcode.LONG_TO_FLOAT -> unary(UnaryOperation.LONG_TO_FLOAT)
        Opcode.LONG_TO_DOUBLE -> unary(UnaryOperation.LONG_TO_DOUBLE)
        Opcode.FLOAT_TO_INT -> unary(UnaryOperation.FLOAT_TO_INT)
        Opcode.FLOAT_TO_LONG -> unary(UnaryOperation.FLOAT_TO_LONG)
        Opcode.FLOAT_TO_DOUBLE -> unary(UnaryOperation.FLOAT_TO_DOUBLE)
        Opcode.DOUBLE_TO_INT -> unary(UnaryOperation.DOUBLE_TO_INT)
        Opcode.DOUBLE_TO_LONG -> unary(UnaryOperation.DOUBLE_TO_LONG)
        Opcode.DOUBLE_TO_FLOAT -> unary(UnaryOperation.DOUBLE_TO_FLOAT)
        Opcode.INT_TO_BYTE -> unary(UnaryOperation.INT_TO_BYTE)
        Opcode.INT_TO_CHAR -> unary(UnaryOperation.INT_TO_CHAR)
        Opcode.INT_TO_SHORT -> unary(UnaryOperation.INT_TO_SHORT)
        Opcode.ADD_INT, Opcode.ADD_INT_2ADDR, Opcode.ADD_INT_LIT8, Opcode.ADD_INT_LIT16 -> int(IntOperator.ADD)
        Opcode.SUB_INT, Opcode.SUB_INT_2ADDR -> int(IntOperator.SUB)
        Opcode.RSUB_INT, Opcode.RSUB_INT_LIT8 -> int(IntOperator.RSUB)
        Opcode.MUL_INT, Opcode.MUL_INT_2ADDR, Opcode.MUL_INT_LIT8, Opcode.MUL_INT_LIT16 -> int(IntOperator.MUL)
        Opcode.DIV_INT, Opcode.DIV_INT_2ADDR, Opcode.DIV_INT_LIT8, Opcode.DIV_INT_LIT16 -> int(IntOperator.DIV)
        Opcode.REM_INT, Opcode.REM_INT_2ADDR, Opcode.REM_INT_LIT8, Opcode.REM_INT_LIT16 -> int(IntOperator.REM)
        Opcode.AND_INT, Opcode.AND_INT_2ADDR, Opcode.AND_INT_LIT8, Opcode.AND_INT_LIT16 -> int(IntOperator.AND)
        Opcode.OR_INT, Opcode.OR_INT_2ADDR, Opcode.OR_INT_LIT8, Opcode.OR_INT_LIT16 -> int(IntOperator.OR)
        Opcode.XOR_INT, Opcode.XOR_INT_2ADDR, Opcode.XOR_INT_LIT8, Opcode.XOR_INT_LIT16 -> int(IntOperator.XOR)
        Opcode.SHL_INT, Opcode.SHL_INT_2ADDR, Opcode.SHL_INT_LIT8 -> int(IntOperator.SHL)
        Opcode.SHR_INT, Opcode.SHR_INT_2ADDR, Opcode.SHR_INT_LIT8 -> int(IntOperator.SHR)
        Opcode.USHR_INT, Opcode.USHR_INT_2ADDR, Opcode.USHR_INT_LIT8 -> int(IntOperator.USHR)
        Opcode.ADD_LONG, Opcode.ADD_LONG_2ADDR -> long(LongOperator.ADD)
        Opcode.SUB_LONG, Opcode.SUB_LONG_2ADDR -> long(LongOperator.SUB)
        Opcode.MUL_LONG, Opcode.MUL_LONG_2ADDR -> long(LongOperator.MUL)
        Opcode.DIV_LONG, Opcode.DIV_LONG_2ADDR -> long(LongOperator.DIV)
        Opcode.REM_LONG, Opcode.REM_LONG_2ADDR -> long(LongOperator.REM)
        Opcode.AND_LONG, Opcode.AND_LONG_2ADDR -> long(LongOperator.AND)
        Opcode.OR_LONG, Opcode.OR_LONG_2ADDR -> long(LongOperator.OR)
        Opcode.XOR_LONG, Opcode.XOR_LONG_2ADDR -> long(LongOperator.XOR)
        Opcode.SHL_LONG, Opcode.SHL_LONG_2ADDR -> long(LongOperator.SHL)
        Opcode.SHR_LONG, Opcode.SHR_LONG_2ADDR -> long(LongOperator.SHR)
        Opcode.USHR_LONG, Opcode.USHR_LONG_2ADDR -> long(LongOperator.USHR)
        Opcode.ADD_FLOAT, Opcode.ADD_FLOAT_2ADDR -> float(FloatingOperator.ADD)
        Opcode.SUB_FLOAT, Opcode.SUB_FLOAT_2ADDR -> float(FloatingOperator.SUB)
        Opcode.MUL_FLOAT, Opcode.MUL_FLOAT_2ADDR -> float(FloatingOperator.MUL)
        Opcode.DIV_FLOAT, Opcode.DIV_FLOAT_2ADDR -> float(FloatingOperator.DIV)
        Opcode.REM_FLOAT, Opcode.REM_FLOAT_2ADDR -> float(FloatingOperator.REM)
        Opcode.ADD_DOUBLE, Opcode.ADD_DOUBLE_2ADDR -> double(FloatingOperator.ADD)
        Opcode.SUB_DOUBLE, Opcode.SUB_DOUBLE_2ADDR -> double(FloatingOperator.SUB)
        Opcode.MUL_DOUBLE, Opcode.MUL_DOUBLE_2ADDR -> double(FloatingOperator.MUL)
        Opcode.DIV_DOUBLE, Opcode.DIV_DOUBLE_2ADDR -> double(FloatingOperator.DIV)
        Opcode.REM_DOUBLE, Opcode.REM_DOUBLE_2ADDR -> double(FloatingOperator.REM)
        Opcode.CMP_LONG -> cmp(CmpOperation.CMP_LONG)
        Opcode.CMPL_FLOAT -> cmp(CmpOperation.CMPL_FLOAT)
        Opcode.CMPG_FLOAT -> cmp(CmpOperation.CMPG_FLOAT)
        Opcode.CMPL_DOUBLE -> cmp(CmpOperation.CMPL_DOUBLE)
        Opcode.CMPG_DOUBLE -> cmp(CmpOperation.CMPG_DOUBLE)
        Opcode.NEW_ARRAY -> { d ->
            NewArrayOp(d.address, d.name, d.registerA, d.registerB, d.typeOperand(d.typeReference))
        }
        Opcode.NEW_INSTANCE -> this::newInstance
        Opcode.ARRAY_LENGTH -> { d -> ArrayLengthOp(d.address, d.name, d.registerA, d.registerB) }
        Opcode.AGET, Opcode.AGET_OBJECT, Opcode.AGET_BOOLEAN, Opcode.AGET_BYTE, Opcode.AGET_CHAR,
        Opcode.AGET_SHORT -> aget(isWide = false)
        Opcode.AGET_WIDE -> aget(isWide = true)
        // The array's component, not the suffix, decides how an element is narrowed.
        Opcode.APUT, Opcode.APUT_WIDE, Opcode.APUT_BOOLEAN, Opcode.APUT_BYTE, Opcode.APUT_CHAR,
        Opcode.APUT_SHORT -> aput(storesAReference = false)
        Opcode.APUT_OBJECT -> aput(storesAReference = true)
        Opcode.FILL_ARRAY_DATA -> this::fillArrayData
        Opcode.FILLED_NEW_ARRAY -> filledNewArray(isRange = false)
        Opcode.FILLED_NEW_ARRAY_RANGE -> filledNewArray(isRange = true)
        Opcode.INSTANCE_OF -> { d ->
            InstanceOfOp(d.address, d.name, d.registerA, d.registerB, d.typeOperand(d.typeReference), d.resolver.hierarchy)
        }
        Opcode.CHECK_CAST -> { d ->
            val hierarchy = d.resolver.hierarchy
            CheckCastOp(d.address, d.name, d.registerA, d.typeOperand(d.typeReference), hierarchy, hierarchy.throwableHierarchy)
        }
        Opcode.SGET, Opcode.SGET_WIDE, Opcode.SGET_OBJECT, Opcode.SGET_BOOLEAN, Opcode.SGET_BYTE, Opcode.SGET_CHAR,
        Opcode.SGET_SHORT -> { d -> SGetOp(d.address, d.name, d.registerA, staticField(d)) }
        Opcode.SPUT, Opcode.SPUT_WIDE, Opcode.SPUT_BOOLEAN, Opcode.SPUT_BYTE, Opcode.SPUT_CHAR,
        Opcode.SPUT_SHORT -> sput(storesAReference = false)
        Opcode.SPUT_OBJECT -> sput(storesAReference = true)
        Opcode.IGET, Opcode.IGET_WIDE, Opcode.IGET_OBJECT, Opcode.IGET_BOOLEAN, Opcode.IGET_BYTE, Opcode.IGET_CHAR,
        Opcode.IGET_SHORT -> { d -> IGetOp(d.address, d.name, d.registerA, d.registerB, instanceField(d)) }
        Opcode.IPUT, Opcode.IPUT_WIDE, Opcode.IPUT_BOOLEAN, Opcode.IPUT_BYTE, Opcode.IPUT_CHAR,
        Opcode.IPUT_SHORT -> iput(storesAReference = false)
        Opcode.IPUT_OBJECT -> iput(storesAReference = true)
        Opcode.INVOKE_STATIC, Opcode.INVOKE_STATIC_RANGE -> invoke(InvokeKind.STATIC)
        Opcode.INVOKE_DIRECT, Opcode.INVOKE_DIRECT_RANGE -> invoke(InvokeKind.DIRECT)
        Opcode.INVOKE_SUPER, Opcode.INVOKE_SUPER_RANGE -> invoke(InvokeKind.SUPER)
        Opcode.INVOKE_VIRTUAL, Opcode.INVOKE_VIRTUAL_RANGE -> invoke(InvokeKind.VIRTUAL)
        Opcode.INVOKE_INTERFACE, Opcode.INVOKE_INTERFACE_RANGE -> invoke(InvokeKind.INTERFACE)
        Opcode.MONITOR_ENTER -> { d -> MonitorEnterOp(d.address, d.registerA) }
        Opcode.MONITOR_EXIT -> { d -> MonitorExitOp(d.address, d.registerA) }
        // Payloads are data, decoded by the instruction that names them ([CON] B22).
        Opcode.PACKED_SWITCH_PAYLOAD, Opcode.SPARSE_SWITCH_PAYLOAD, Opcode.ARRAY_PAYLOAD,
        // Method handles and call sites are not modelled.
        Opcode.INVOKE_POLYMORPHIC, Opcode.INVOKE_POLYMORPHIC_RANGE, Opcode.INVOKE_CUSTOM, Opcode.INVOKE_CUSTOM_RANGE,
        Opcode.CONST_METHOD_HANDLE, Opcode.CONST_METHOD_TYPE,
        // Optimized-dex forms, never present in an APK's classes.dex.
        Opcode.IGET_VOLATILE, Opcode.IPUT_VOLATILE, Opcode.SGET_VOLATILE, Opcode.SPUT_VOLATILE,
        Opcode.IGET_OBJECT_VOLATILE, Opcode.IGET_WIDE_VOLATILE, Opcode.IPUT_WIDE_VOLATILE, Opcode.SGET_WIDE_VOLATILE,
        Opcode.SPUT_WIDE_VOLATILE, Opcode.IPUT_OBJECT_VOLATILE, Opcode.SGET_OBJECT_VOLATILE, Opcode.SPUT_OBJECT_VOLATILE,
        Opcode.THROW_VERIFICATION_ERROR, Opcode.EXECUTE_INLINE, Opcode.EXECUTE_INLINE_RANGE, Opcode.INVOKE_DIRECT_EMPTY,
        Opcode.INVOKE_OBJECT_INIT_RANGE, Opcode.RETURN_VOID_BARRIER, Opcode.RETURN_VOID_NO_BARRIER,
        Opcode.IGET_QUICK, Opcode.IGET_WIDE_QUICK, Opcode.IGET_OBJECT_QUICK, Opcode.IGET_BOOLEAN_QUICK,
        Opcode.IGET_BYTE_QUICK, Opcode.IGET_CHAR_QUICK, Opcode.IGET_SHORT_QUICK,
        Opcode.IPUT_QUICK, Opcode.IPUT_WIDE_QUICK, Opcode.IPUT_OBJECT_QUICK, Opcode.IPUT_BOOLEAN_QUICK,
        Opcode.IPUT_BYTE_QUICK, Opcode.IPUT_CHAR_QUICK, Opcode.IPUT_SHORT_QUICK,
        Opcode.INVOKE_VIRTUAL_QUICK, Opcode.INVOKE_VIRTUAL_QUICK_RANGE, Opcode.INVOKE_SUPER_QUICK,
        Opcode.INVOKE_SUPER_QUICK_RANGE -> null
    }

    /** `22t` names both operands; `21t` names one and compares it with zero. */
    private fun branch(comparison: IfComparison): Decoder = { d ->
        IfOp(d.address, d.name, comparison, d.target, d.registerA, (d.instruction as? TwoRegisterInstruction)?.registerB)
    }

    /**
     * Each case's offset is relative to the switch, not to the payload, as both payload formats specify. A
     * payload target that is not a switch table makes the instruction unsupported; a malformed case target is
     * left to the scheduler, which drops only that successor and reports the loss.
     */
    private fun switch(dispatch: SwitchDispatch): Decoder = { d ->
        val payloadAddress = d.target
        val payload = payloadAddress.takeIf { d.view.hasAddress(it) && d.view.isPayload(it) }
            ?.let { d.view.payloadAt(it) as? SwitchPayload }
        if (payload == null) {
            d.unsupported
        } else {
            val importedCases = d.view.switchCasesAt(d.address)
            val cases = if (importedCases.isNotEmpty()) {
                importedCases.map { (key, target) -> SwitchOp.Case(key, target) }
            } else {
                payload.switchElements.map { SwitchOp.Case(it.key, d.address + it.offset) }
            }
            SwitchOp(d.address, d.name, d.registerA, cases, d.address + d.instruction.codeUnits, dispatch)
        }
    }

    private fun move(isWide: Boolean): Decoder = { d ->
        MoveOp(d.address, d.name, d.registerA, isWide, MoveOp.Source.Register(d.registerB))
    }

    private fun moveFromSlot(slot: SpecialSlotSource, isWide: Boolean): Decoder = { d ->
        MoveOp(d.address, d.name, d.registerA, isWide, MoveOp.Source.Slot(slot))
    }

    /**
     * The class is loaded first, because the host loader also resolves host-only classes the class model does
     * not contain. When the load fails, the class model decides which failure it is: a class absent from it
     * is a guest `NoClassDefFoundError`, and a class it contains that the host cannot load (many framework
     * `java.*` classes) is an engine limitation, so the instruction is unsupported.
     */
    private fun constClass(d: Decoding): Op {
        val descriptor = (d.reference as TypeReference).type
        val hostClass = d.classIndex.loadClassConstant(descriptor)
        val type = d.classIndex.typeOf(descriptor)
        return when {
            hostClass != null -> ConstClassOp(d.address, d.registerA, TypeOperand.Present(type, hostClass))
            !d.classIndex.isLoadable(type) -> ConstClassOp(d.address, d.registerA, TypeOperand.Absent(type))
            else -> d.unsupported
        }
    }

    private fun unary(operation: UnaryOperation): Decoder = { d ->
        UnaryMathOp(d.address, d.name, d.registerA, d.registerB, operation)
    }

    private fun int(operator: IntOperator): Decoder = binary(BinaryOperation.IntDomain(operator))
    private fun long(operator: LongOperator): Decoder = binary(BinaryOperation.LongDomain(operator))
    private fun float(operator: FloatingOperator): Decoder = binary(BinaryOperation.FloatDomain(operator))
    private fun double(operator: FloatingOperator): Decoder = binary(BinaryOperation.DoubleDomain(operator))

    /** `23x` names three registers, `/2addr` (`12x`) uses `vA` as both destination and first operand, `/lit*` a literal. */
    private fun binary(operation: BinaryOperation): Decoder = { d ->
        when (val instruction = d.instruction) {
            is Instruction23x -> BinaryMathOp(
                d.address, d.name, d.registerA, d.registerB, BinaryMathOp.Operand.Register(instruction.registerC), operation,
            )
            is Instruction12x -> BinaryMathOp(
                d.address, d.name, d.registerA, d.registerA, BinaryMathOp.Operand.Register(instruction.registerB), operation,
            )
            is NarrowLiteralInstruction -> BinaryMathOp(
                d.address, d.name, d.registerA, d.registerB, BinaryMathOp.Operand.Literal(instruction.narrowLiteral), operation,
            )
            else -> d.unsupported
        }
    }

    private fun cmp(operation: CmpOperation): Decoder = { d ->
        CmpOp(d.address, d.name, d.registerA, d.registerB, d.registerC, operation)
    }

    private fun newInstance(d: Decoding): Op {
        val type = d.typeReference
        val fields = d.classIndex.instanceFieldDefaults(type)
        val instance = if (fields != null) TypeOperand.Present(type, fields) else TypeOperand.Absent(type)
        return NewInstanceOp(d.address, d.name, d.registerA, instance)
    }

    private fun aget(isWide: Boolean): Decoder = { d ->
        AGetOp(d.address, d.name, d.registerA, d.registerB, d.registerC, isWide)
    }

    private fun aput(storesAReference: Boolean): Decoder = { d ->
        val hierarchy = d.resolver.hierarchy
        APutOp(d.address, d.name, d.registerA, d.registerB, d.registerC, storesAReference, hierarchy, hierarchy)
    }

    /** A payload target that is not `.array-data` makes the instruction unsupported. */
    private fun fillArrayData(d: Decoding): Op {
        val target = d.target
        val payload = target.takeIf { d.view.hasAddress(it) && d.view.isPayload(it) }
            ?.let { d.view.payloadAt(it) as? ArrayPayload }
            ?: return d.unsupported
        return FillArrayDataOp(d.address, d.name, d.registerA, payload.elementWidth, payload.arrayElements)
    }

    /**
     * A `J`/`D` component ([BC]: "no arrays of long or double") and a non-array type are verifier hard
     * failures that no device executes, so both decode to an [UnsupportedOp].
     */
    private fun filledNewArray(isRange: Boolean): Decoder = { d ->
        val arrayType = d.typeReference
        val component = arrayType.componentType
        val registers = d.registerList
        if (component == null || component.isWide || registers == null) {
            d.unsupported
        } else {
            FilledNewArrayOp(d.address, d.name, registers, d.typeOperand(arrayType), isRange)
        }
    }

    private fun sput(storesAReference: Boolean): Decoder = { d ->
        SPutOp(d.address, d.name, d.registerA, staticField(d), storesAReference)
    }

    private fun iput(storesAReference: Boolean): Decoder = { d ->
        IPutOp(d.address, d.name, d.registerA, d.registerB, instanceField(d), storesAReference)
    }

    /** The field's defining class, name and type all take part, so same-named fields of different types stay apart. */
    private fun staticField(d: Decoding): StaticFieldLookup {
        val reference = d.reference as FieldReference
        return d.classIndex.resolveStaticField(d.classIndex.typeOf(reference.definingClass), reference.name, reference.type)
    }

    private fun instanceField(d: Decoding): InstanceFieldLookup {
        val reference = d.reference as FieldReference
        return d.classIndex.resolveInstanceField(d.classIndex.typeOf(reference.definingClass), reference.name, reference.type)
    }

    private fun invoke(kind: InvokeKind): Decoder = { d ->
        val registers = d.registerList
        if (registers == null) {
            d.unsupported
        } else {
            val reference = d.reference as MethodReference
            val proto = "(${reference.parameterTypes.joinToString("")})${reference.returnType}"
            InvokeOp(
                d.address, d.name,
                MethodDescriptor(d.classIndex.typeOf(reference.definingClass), reference.name, proto),
                registers, kind,
            )
        }
    }

    private val INT = VirtualType.of("I")
    private val LONG = VirtualType.of("J")
    private val STRING = VirtualType.of("Ljava/lang/String;")

}
