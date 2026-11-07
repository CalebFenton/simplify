package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import com.android.tools.smali.dexlib2.iface.instruction.DualReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.FiveRegisterInstruction
import com.android.tools.smali.dexlib2.iface.instruction.Instruction
import com.android.tools.smali.dexlib2.iface.instruction.ReferenceInstruction
import com.android.tools.smali.dexlib2.iface.instruction.RegisterRangeInstruction
import com.android.tools.smali.dexlib2.iface.reference.CallSiteReference
import com.android.tools.smali.dexlib2.iface.reference.MethodProtoReference
import com.android.tools.smali.dexlib2.iface.reference.MethodReference
import com.android.tools.smali.dexlib2.iface.reference.TypeReference

internal enum class RegisterAccess { READ, WRITE, READ_WRITE }

/** Verifier category of one register value: a narrow word, a reference word, or a wide register pair. */
internal enum class ValueCategory(val width: Int) {
    NARROW(1),
    REFERENCE(1),
    WIDE(2),
    ;

    companion object {
        fun ofDescriptor(descriptor: CharSequence): ValueCategory = when (descriptor.firstOrNull()) {
            'J', 'D' -> WIDE
            'L', '[' -> REFERENCE
            else -> NARROW
        }
    }
}

internal data class RegisterOperand(val access: RegisterAccess, val category: ValueCategory) {
    val reads: Boolean get() = access != RegisterAccess.WRITE
    val writes: Boolean get() = access != RegisterAccess.READ
}

/**
 * How each opcode reads and writes registers, per the Dalvik bytecode reference. Fixed-register formats get one
 * operand per register field in A, B, C order. Invokes and `filled-new-array` get one read per argument value; a
 * wide argument covers two consecutive argument words.
 */
internal object RegisterEffects {
    fun operands(instruction: Instruction): List<RegisterOperand> {
        if (instruction is FiveRegisterInstruction || instruction is RegisterRangeInstruction) {
            return argumentOperands(instruction)
        }
        return FIXED_OPERANDS[instruction.opcode] ?: error("No register model for ${instruction.opcode}")
    }

    /** The three-address form of a `binop/2addr` opcode. */
    fun threeAddressForm(twoAddress: Opcode): Opcode? = THREE_ADDRESS_FORMS[twoAddress]

    private fun argumentOperands(instruction: Instruction): List<RegisterOperand> {
        val reference = (instruction as? ReferenceInstruction)?.reference
        val parameterTypes: List<CharSequence> = when {
            instruction.opcode == Opcode.FILLED_NEW_ARRAY || instruction.opcode == Opcode.FILLED_NEW_ARRAY_RANGE -> {
                val component = (reference as TypeReference).type.removePrefix("[")
                return List(instruction.registerOperands().size) { read(ValueCategory.ofDescriptor(component)) }
            }
            instruction is DualReferenceInstruction ->
                listOf("L") + (instruction.reference2 as MethodProtoReference).parameterTypes
            reference is CallSiteReference -> reference.methodProto.parameterTypes
            reference is MethodReference ->
                if (instruction.opcode in STATIC_INVOKES) reference.parameterTypes else listOf("L") + reference.parameterTypes
            else -> return List(instruction.registerOperands().size) { read(ValueCategory.NARROW) }
        }
        return parameterTypes.map { read(ValueCategory.ofDescriptor(it)) }
    }

    private fun read(category: ValueCategory) = RegisterOperand(RegisterAccess.READ, category)
    private fun write(category: ValueCategory) = RegisterOperand(RegisterAccess.WRITE, category)
    private fun readWrite(category: ValueCategory) = RegisterOperand(RegisterAccess.READ_WRITE, category)

    private val STATIC_INVOKES = setOf(Opcode.INVOKE_STATIC, Opcode.INVOKE_STATIC_RANGE)

    private class BinaryOperation(
        val threeAddress: Opcode,
        val twoAddress: Opcode,
        val category: ValueCategory,
        val shiftDistance: Boolean = false,
    )

    private val BINARY_OPERATIONS: List<BinaryOperation> = buildList {
        fun add(threeAddress: Opcode, twoAddress: Opcode, category: ValueCategory, shift: Boolean = false) =
            add(BinaryOperation(threeAddress, twoAddress, category, shift))
        val n = ValueCategory.NARROW
        val w = ValueCategory.WIDE
        add(Opcode.ADD_INT, Opcode.ADD_INT_2ADDR, n)
        add(Opcode.SUB_INT, Opcode.SUB_INT_2ADDR, n)
        add(Opcode.MUL_INT, Opcode.MUL_INT_2ADDR, n)
        add(Opcode.DIV_INT, Opcode.DIV_INT_2ADDR, n)
        add(Opcode.REM_INT, Opcode.REM_INT_2ADDR, n)
        add(Opcode.AND_INT, Opcode.AND_INT_2ADDR, n)
        add(Opcode.OR_INT, Opcode.OR_INT_2ADDR, n)
        add(Opcode.XOR_INT, Opcode.XOR_INT_2ADDR, n)
        add(Opcode.SHL_INT, Opcode.SHL_INT_2ADDR, n)
        add(Opcode.SHR_INT, Opcode.SHR_INT_2ADDR, n)
        add(Opcode.USHR_INT, Opcode.USHR_INT_2ADDR, n)
        add(Opcode.ADD_LONG, Opcode.ADD_LONG_2ADDR, w)
        add(Opcode.SUB_LONG, Opcode.SUB_LONG_2ADDR, w)
        add(Opcode.MUL_LONG, Opcode.MUL_LONG_2ADDR, w)
        add(Opcode.DIV_LONG, Opcode.DIV_LONG_2ADDR, w)
        add(Opcode.REM_LONG, Opcode.REM_LONG_2ADDR, w)
        add(Opcode.AND_LONG, Opcode.AND_LONG_2ADDR, w)
        add(Opcode.OR_LONG, Opcode.OR_LONG_2ADDR, w)
        add(Opcode.XOR_LONG, Opcode.XOR_LONG_2ADDR, w)
        add(Opcode.SHL_LONG, Opcode.SHL_LONG_2ADDR, w, shift = true)
        add(Opcode.SHR_LONG, Opcode.SHR_LONG_2ADDR, w, shift = true)
        add(Opcode.USHR_LONG, Opcode.USHR_LONG_2ADDR, w, shift = true)
        add(Opcode.ADD_FLOAT, Opcode.ADD_FLOAT_2ADDR, n)
        add(Opcode.SUB_FLOAT, Opcode.SUB_FLOAT_2ADDR, n)
        add(Opcode.MUL_FLOAT, Opcode.MUL_FLOAT_2ADDR, n)
        add(Opcode.DIV_FLOAT, Opcode.DIV_FLOAT_2ADDR, n)
        add(Opcode.REM_FLOAT, Opcode.REM_FLOAT_2ADDR, n)
        add(Opcode.ADD_DOUBLE, Opcode.ADD_DOUBLE_2ADDR, w)
        add(Opcode.SUB_DOUBLE, Opcode.SUB_DOUBLE_2ADDR, w)
        add(Opcode.MUL_DOUBLE, Opcode.MUL_DOUBLE_2ADDR, w)
        add(Opcode.DIV_DOUBLE, Opcode.DIV_DOUBLE_2ADDR, w)
        add(Opcode.REM_DOUBLE, Opcode.REM_DOUBLE_2ADDR, w)
    }

    private val THREE_ADDRESS_FORMS: Map<Opcode, Opcode> =
        BINARY_OPERATIONS.associate { it.twoAddress to it.threeAddress }

    private val FIXED_OPERANDS: Map<Opcode, List<RegisterOperand>> = buildMap {
        val n = ValueCategory.NARROW
        val r = ValueCategory.REFERENCE
        val w = ValueCategory.WIDE
        fun model(operands: List<RegisterOperand>, vararg opcodes: Opcode) = opcodes.forEach { put(it, operands) }

        model(
            emptyList(),
            Opcode.NOP, Opcode.RETURN_VOID, Opcode.RETURN_VOID_BARRIER, Opcode.RETURN_VOID_NO_BARRIER,
            Opcode.GOTO, Opcode.GOTO_16, Opcode.GOTO_32, Opcode.THROW_VERIFICATION_ERROR,
            Opcode.ARRAY_PAYLOAD, Opcode.PACKED_SWITCH_PAYLOAD, Opcode.SPARSE_SWITCH_PAYLOAD,
        )
        model(listOf(write(n), read(n)), Opcode.MOVE, Opcode.MOVE_FROM16, Opcode.MOVE_16)
        model(listOf(write(w), read(w)), Opcode.MOVE_WIDE, Opcode.MOVE_WIDE_FROM16, Opcode.MOVE_WIDE_16)
        model(listOf(write(r), read(r)), Opcode.MOVE_OBJECT, Opcode.MOVE_OBJECT_FROM16, Opcode.MOVE_OBJECT_16)
        model(listOf(write(n)), Opcode.MOVE_RESULT, Opcode.CONST_4, Opcode.CONST_16, Opcode.CONST, Opcode.CONST_HIGH16)
        model(
            listOf(write(w)),
            Opcode.MOVE_RESULT_WIDE, Opcode.CONST_WIDE_16, Opcode.CONST_WIDE_32, Opcode.CONST_WIDE,
            Opcode.CONST_WIDE_HIGH16,
        )
        model(
            listOf(write(r)),
            Opcode.MOVE_RESULT_OBJECT, Opcode.MOVE_EXCEPTION, Opcode.CONST_STRING, Opcode.CONST_STRING_JUMBO,
            Opcode.CONST_CLASS, Opcode.NEW_INSTANCE, Opcode.CONST_METHOD_HANDLE, Opcode.CONST_METHOD_TYPE,
        )
        model(listOf(read(n)), Opcode.RETURN, Opcode.PACKED_SWITCH, Opcode.SPARSE_SWITCH)
        model(listOf(read(w)), Opcode.RETURN_WIDE)
        model(listOf(read(r)), Opcode.RETURN_OBJECT, Opcode.MONITOR_ENTER, Opcode.MONITOR_EXIT, Opcode.THROW, Opcode.FILL_ARRAY_DATA)
        model(listOf(readWrite(r)), Opcode.CHECK_CAST)
        model(listOf(write(n), read(r)), Opcode.INSTANCE_OF, Opcode.ARRAY_LENGTH)
        model(listOf(write(r), read(n)), Opcode.NEW_ARRAY)
        model(listOf(write(n), read(n), read(n)), Opcode.CMPL_FLOAT, Opcode.CMPG_FLOAT)
        model(listOf(write(n), read(w), read(w)), Opcode.CMPL_DOUBLE, Opcode.CMPG_DOUBLE, Opcode.CMP_LONG)
        model(listOf(read(n), read(n)), Opcode.IF_EQ, Opcode.IF_NE, Opcode.IF_LT, Opcode.IF_GE, Opcode.IF_GT, Opcode.IF_LE)
        model(listOf(read(n)), Opcode.IF_EQZ, Opcode.IF_NEZ, Opcode.IF_LTZ, Opcode.IF_GEZ, Opcode.IF_GTZ, Opcode.IF_LEZ)

        fun arrayAccess(category: ValueCategory, get: Opcode, putOpcode: Opcode) {
            put(get, listOf(write(category), read(r), read(n)))
            put(putOpcode, listOf(read(category), read(r), read(n)))
        }
        arrayAccess(n, Opcode.AGET, Opcode.APUT)
        arrayAccess(w, Opcode.AGET_WIDE, Opcode.APUT_WIDE)
        arrayAccess(r, Opcode.AGET_OBJECT, Opcode.APUT_OBJECT)
        arrayAccess(n, Opcode.AGET_BOOLEAN, Opcode.APUT_BOOLEAN)
        arrayAccess(n, Opcode.AGET_BYTE, Opcode.APUT_BYTE)
        arrayAccess(n, Opcode.AGET_CHAR, Opcode.APUT_CHAR)
        arrayAccess(n, Opcode.AGET_SHORT, Opcode.APUT_SHORT)

        fun instanceField(category: ValueCategory, gets: List<Opcode>, puts: List<Opcode>) {
            gets.forEach { put(it, listOf(write(category), read(r))) }
            puts.forEach { put(it, listOf(read(category), read(r))) }
        }
        instanceField(
            n,
            listOf(
                Opcode.IGET, Opcode.IGET_BOOLEAN, Opcode.IGET_BYTE, Opcode.IGET_CHAR, Opcode.IGET_SHORT,
                Opcode.IGET_VOLATILE, Opcode.IGET_QUICK, Opcode.IGET_BOOLEAN_QUICK, Opcode.IGET_BYTE_QUICK,
                Opcode.IGET_CHAR_QUICK, Opcode.IGET_SHORT_QUICK,
            ),
            listOf(
                Opcode.IPUT, Opcode.IPUT_BOOLEAN, Opcode.IPUT_BYTE, Opcode.IPUT_CHAR, Opcode.IPUT_SHORT,
                Opcode.IPUT_VOLATILE, Opcode.IPUT_QUICK, Opcode.IPUT_BOOLEAN_QUICK, Opcode.IPUT_BYTE_QUICK,
                Opcode.IPUT_CHAR_QUICK, Opcode.IPUT_SHORT_QUICK,
            ),
        )
        instanceField(
            w,
            listOf(Opcode.IGET_WIDE, Opcode.IGET_WIDE_VOLATILE, Opcode.IGET_WIDE_QUICK),
            listOf(Opcode.IPUT_WIDE, Opcode.IPUT_WIDE_VOLATILE, Opcode.IPUT_WIDE_QUICK),
        )
        instanceField(
            r,
            listOf(Opcode.IGET_OBJECT, Opcode.IGET_OBJECT_VOLATILE, Opcode.IGET_OBJECT_QUICK),
            listOf(Opcode.IPUT_OBJECT, Opcode.IPUT_OBJECT_VOLATILE, Opcode.IPUT_OBJECT_QUICK),
        )

        fun staticField(category: ValueCategory, gets: List<Opcode>, puts: List<Opcode>) {
            gets.forEach { put(it, listOf(write(category))) }
            puts.forEach { put(it, listOf(read(category))) }
        }
        staticField(
            n,
            listOf(Opcode.SGET, Opcode.SGET_BOOLEAN, Opcode.SGET_BYTE, Opcode.SGET_CHAR, Opcode.SGET_SHORT, Opcode.SGET_VOLATILE),
            listOf(Opcode.SPUT, Opcode.SPUT_BOOLEAN, Opcode.SPUT_BYTE, Opcode.SPUT_CHAR, Opcode.SPUT_SHORT, Opcode.SPUT_VOLATILE),
        )
        staticField(w, listOf(Opcode.SGET_WIDE, Opcode.SGET_WIDE_VOLATILE), listOf(Opcode.SPUT_WIDE, Opcode.SPUT_WIDE_VOLATILE))
        staticField(
            r,
            listOf(Opcode.SGET_OBJECT, Opcode.SGET_OBJECT_VOLATILE),
            listOf(Opcode.SPUT_OBJECT, Opcode.SPUT_OBJECT_VOLATILE),
        )

        fun unary(destination: ValueCategory, source: ValueCategory, vararg opcodes: Opcode) =
            model(listOf(write(destination), read(source)), *opcodes)
        unary(
            n, n,
            Opcode.NEG_INT, Opcode.NOT_INT, Opcode.NEG_FLOAT, Opcode.INT_TO_FLOAT, Opcode.FLOAT_TO_INT,
            Opcode.INT_TO_BYTE, Opcode.INT_TO_CHAR, Opcode.INT_TO_SHORT,
        )
        unary(
            w, w,
            Opcode.NEG_LONG, Opcode.NOT_LONG, Opcode.NEG_DOUBLE, Opcode.LONG_TO_DOUBLE, Opcode.DOUBLE_TO_LONG,
        )
        unary(w, n, Opcode.INT_TO_LONG, Opcode.INT_TO_DOUBLE, Opcode.FLOAT_TO_LONG, Opcode.FLOAT_TO_DOUBLE)
        unary(n, w, Opcode.LONG_TO_INT, Opcode.LONG_TO_FLOAT, Opcode.DOUBLE_TO_INT, Opcode.DOUBLE_TO_FLOAT)

        for (operation in BINARY_OPERATIONS) {
            val distance = if (operation.shiftDistance) n else operation.category
            put(operation.threeAddress, listOf(write(operation.category), read(operation.category), read(distance)))
            put(operation.twoAddress, listOf(readWrite(operation.category), read(distance)))
        }
        model(
            listOf(write(n), read(n)),
            Opcode.ADD_INT_LIT16, Opcode.RSUB_INT, Opcode.MUL_INT_LIT16, Opcode.DIV_INT_LIT16, Opcode.REM_INT_LIT16,
            Opcode.AND_INT_LIT16, Opcode.OR_INT_LIT16, Opcode.XOR_INT_LIT16,
            Opcode.ADD_INT_LIT8, Opcode.RSUB_INT_LIT8, Opcode.MUL_INT_LIT8, Opcode.DIV_INT_LIT8, Opcode.REM_INT_LIT8,
            Opcode.AND_INT_LIT8, Opcode.OR_INT_LIT8, Opcode.XOR_INT_LIT8, Opcode.SHL_INT_LIT8, Opcode.SHR_INT_LIT8,
            Opcode.USHR_INT_LIT8,
        )
    }
}
