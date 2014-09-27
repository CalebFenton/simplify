package org.cf.smalivm.opcode;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OpFactory {

    private enum OpType {
        AGET,
        APUT,
        ARRAY_LENGTH,
        ARRAY_PAYLOAD,
        BINARY_MATH,
        CHECK_CAST,
        COMPARE,
        CONST,
        FILL_ARRAY_DATA,
        FILLED_NEW_ARRAY,
        GOTO,
        IF,
        INSTANCE_OF,
        INVOKE,
        MONITOR,
        MOVE,
        NEW_ARRAY,
        NEW_INSTANCE,
        RETURN,
        RETURN_VOID,
        SGET,
        SPUT,
        SWITCH,
        SWITCH_PAYLOAD,
        UNARY_MATH,
        UNIMPLEMENTED
    };

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(OpFactory.class.getSimpleName());

    @SuppressWarnings("incomplete-switch")
    private static OpType getFactoryType(Opcode op) {
        OpType result = OpType.UNIMPLEMENTED;

        switch (op) {
        case ADD_DOUBLE:
        case ADD_DOUBLE_2ADDR:
        case ADD_FLOAT:
        case ADD_FLOAT_2ADDR:
        case ADD_INT:
        case ADD_INT_2ADDR:
        case ADD_INT_LIT16:
        case ADD_INT_LIT8:
        case ADD_LONG:
        case ADD_LONG_2ADDR:
        case AND_INT:
        case AND_INT_2ADDR:
        case AND_INT_LIT16:
        case AND_INT_LIT8:
        case AND_LONG:
        case AND_LONG_2ADDR:
        case DIV_DOUBLE:
        case DIV_DOUBLE_2ADDR:
        case DIV_FLOAT:
        case DIV_FLOAT_2ADDR:
        case DIV_INT:
        case DIV_INT_2ADDR:
        case DIV_INT_LIT16:
        case DIV_INT_LIT8:
        case DIV_LONG:
        case DIV_LONG_2ADDR:
        case MUL_DOUBLE:
        case MUL_DOUBLE_2ADDR:
        case MUL_FLOAT:
        case MUL_FLOAT_2ADDR:
        case MUL_INT:
        case MUL_INT_2ADDR:
        case MUL_INT_LIT16:
        case MUL_INT_LIT8:
        case MUL_LONG:
        case MUL_LONG_2ADDR:
        case OR_INT:
        case OR_INT_2ADDR:
        case OR_INT_LIT16:
        case OR_INT_LIT8:
        case OR_LONG:
        case OR_LONG_2ADDR:
        case REM_DOUBLE:
        case REM_DOUBLE_2ADDR:
        case REM_FLOAT:
        case REM_FLOAT_2ADDR:
        case REM_INT:
        case REM_INT_2ADDR:
        case REM_INT_LIT16:
        case REM_INT_LIT8:
        case REM_LONG:
        case REM_LONG_2ADDR:
        case RSUB_INT:
        case RSUB_INT_LIT8:
        case SHL_INT:
        case SHL_INT_2ADDR:
        case SHL_INT_LIT8:
        case SHL_LONG:
        case SHL_LONG_2ADDR:
        case SHR_INT:
        case SHR_INT_2ADDR:
        case SHR_INT_LIT8:
        case SHR_LONG:
        case SHR_LONG_2ADDR:
        case SUB_DOUBLE:
        case SUB_DOUBLE_2ADDR:
        case SUB_FLOAT:
        case SUB_FLOAT_2ADDR:
        case SUB_INT:
        case SUB_INT_2ADDR:
        case SUB_LONG:
        case SUB_LONG_2ADDR:
        case USHR_INT:
        case USHR_INT_2ADDR:
        case USHR_INT_LIT8:
        case USHR_LONG:
        case USHR_LONG_2ADDR:
        case XOR_INT:
        case XOR_INT_2ADDR:
        case XOR_INT_LIT16:
        case XOR_INT_LIT8:
        case XOR_LONG:
        case XOR_LONG_2ADDR:
            result = OpType.BINARY_MATH;
            break;

        case AGET:
        case AGET_BOOLEAN:
        case AGET_BYTE:
        case AGET_CHAR:
        case AGET_OBJECT:
        case AGET_SHORT:
        case AGET_WIDE:
            result = OpType.AGET;
            break;

        case APUT:
        case APUT_BOOLEAN:
        case APUT_BYTE:
        case APUT_CHAR:
        case APUT_OBJECT:
        case APUT_SHORT:
        case APUT_WIDE:
            result = OpType.APUT;
            break;

        case ARRAY_LENGTH:
            result = OpType.ARRAY_LENGTH;
            break;

        case ARRAY_PAYLOAD:
            result = OpType.ARRAY_PAYLOAD;
            break;

        case CHECK_CAST:
            result = OpType.CHECK_CAST;
            break;

        case CMPG_DOUBLE:
        case CMPG_FLOAT:
        case CMPL_DOUBLE:
        case CMPL_FLOAT:
        case CMP_LONG:
            break;

        case CONST:
        case CONST_16:
        case CONST_4:
        case CONST_CLASS:
        case CONST_HIGH16:
        case CONST_STRING:
        case CONST_STRING_JUMBO:
        case CONST_WIDE:
        case CONST_WIDE_16:
        case CONST_WIDE_32:
        case CONST_WIDE_HIGH16:
            result = OpType.CONST;
            break;

        case DOUBLE_TO_FLOAT:
        case DOUBLE_TO_INT:
        case DOUBLE_TO_LONG:
        case FLOAT_TO_DOUBLE:
        case FLOAT_TO_INT:
        case FLOAT_TO_LONG:
        case INT_TO_BYTE:
        case INT_TO_CHAR:
        case INT_TO_DOUBLE:
        case INT_TO_FLOAT:
        case INT_TO_LONG:
        case INT_TO_SHORT:
        case LONG_TO_DOUBLE:
        case LONG_TO_FLOAT:
        case LONG_TO_INT:
        case NEG_DOUBLE:
        case NEG_FLOAT:
        case NEG_INT:
        case NEG_LONG:
        case NOT_INT:
        case NOT_LONG:
            result = OpType.UNARY_MATH;
            break;

        case FILLED_NEW_ARRAY:
        case FILLED_NEW_ARRAY_RANGE:
            result = OpType.FILLED_NEW_ARRAY;
            break;

        case FILL_ARRAY_DATA:
            result = OpType.FILL_ARRAY_DATA;
            break;

        case GOTO:
        case GOTO_16:
        case GOTO_32:
            result = OpType.GOTO;
            break;

        case IF_EQ:
        case IF_GE:
        case IF_GT:
        case IF_LE:
        case IF_LT:
        case IF_NE:
        case IF_EQZ:
        case IF_GEZ:
        case IF_GTZ:
        case IF_LEZ:
        case IF_LTZ:
        case IF_NEZ:
            result = OpType.IF;
            break;

        case IGET:
        case IGET_BOOLEAN:
        case IGET_BYTE:
        case IGET_CHAR:
        case IGET_OBJECT:
        case IGET_SHORT:
        case IGET_WIDE:
        case IPUT:
        case IPUT_BOOLEAN:
        case IPUT_BYTE:
        case IPUT_CHAR:
        case IPUT_OBJECT:
        case IPUT_SHORT:
        case IPUT_WIDE:
            break;

        case INSTANCE_OF:
            break;

        case INVOKE_DIRECT:
        case INVOKE_INTERFACE:
        case INVOKE_STATIC:
        case INVOKE_SUPER:
        case INVOKE_VIRTUAL:
        case INVOKE_DIRECT_RANGE:
        case INVOKE_INTERFACE_RANGE:
        case INVOKE_STATIC_RANGE:
        case INVOKE_SUPER_RANGE:
        case INVOKE_VIRTUAL_RANGE:
            result = OpType.INVOKE;
            break;

        case MONITOR_ENTER:
            break;
        case MONITOR_EXIT:
            break;

        case MOVE:
        case MOVE_16:
        case MOVE_FROM16:
        case MOVE_OBJECT:
        case MOVE_OBJECT_16:
        case MOVE_OBJECT_FROM16:
        case MOVE_WIDE:
        case MOVE_WIDE_16:
        case MOVE_WIDE_FROM16:
        case MOVE_EXCEPTION:
        case MOVE_RESULT:
        case MOVE_RESULT_OBJECT:
        case MOVE_RESULT_WIDE:
            result = OpType.MOVE;
            break;

        case NEW_ARRAY:
            result = OpType.NEW_ARRAY;
            break;

        case NEW_INSTANCE:
            result = OpType.NEW_INSTANCE;
            break;

        case NOP:
            // Handled by default.
            break;

        case PACKED_SWITCH:
        case SPARSE_SWITCH:
            result = OpType.SWITCH;
            break;

        case PACKED_SWITCH_PAYLOAD:
        case SPARSE_SWITCH_PAYLOAD:
            result = OpType.SWITCH_PAYLOAD;
            break;

        case RETURN:
        case RETURN_WIDE:
        case RETURN_OBJECT:
            result = OpType.RETURN;
            break;

        case RETURN_VOID:
            result = OpType.RETURN_VOID;
            break;

        case SGET:
        case SGET_BOOLEAN:
        case SGET_BYTE:
        case SGET_CHAR:
        case SGET_OBJECT:
        case SGET_SHORT:
        case SGET_WIDE:
            result = OpType.SGET;
            break;

        case SPUT:
        case SPUT_BOOLEAN:
        case SPUT_BYTE:
        case SPUT_CHAR:
        case SPUT_OBJECT:
        case SPUT_SHORT:
        case SPUT_WIDE:
            result = OpType.SPUT;
            break;

        case THROW:
            break;
        }

        return result;
    }

    private final VirtualMachine vm;

    public OpFactory(VirtualMachine vm, String methodDescriptor) {
        this.vm = vm;
    }

    public Op create(BuilderInstruction instruction, int address) {
        Op result = null;
        OpType factoryType = getFactoryType(instruction.getOpcode());
        switch (factoryType) {
        case BINARY_MATH:
            result = BinaryMathOp.create(instruction, address);
            break;
        case CONST:
            result = ConstOp.create(instruction, address, vm);
            break;
        case GOTO:
            result = GotoOp.create(instruction, address);
            break;
        case IF:
            result = IfOp.create(instruction, address);
            break;
        case SWITCH:
            result = SwitchOp.create(instruction, address);
            break;
        case SWITCH_PAYLOAD:
            result = SwitchPayloadOp.create(instruction, address);
            break;
        case UNIMPLEMENTED:
            result = UnimplementedOp.create(instruction, address);
            break;
        case ARRAY_LENGTH:
            result = ArrayLengthOp.create(instruction, address);
            break;
        case ARRAY_PAYLOAD:
            result = FillArrayDataPayloadOp.create(instruction, address);
            break;
        case CHECK_CAST:
            result = CheckCastOp.create(instruction, address, vm);
            break;
        case COMPARE:
            // TODO: implement
            break;
        case FILLED_NEW_ARRAY:
            result = FilledNewArrayOp.create(instruction, address, vm);
            break;
        case FILL_ARRAY_DATA:
            result = FillArrayDataOp.create(instruction, address);
            break;
        case INSTANCE_OF:
            // TODO: implement
            break;
        case INVOKE:
            result = InvokeOp.create(instruction, address, vm);
            break;
        case MONITOR:
            break;
        case MOVE:
            result = MoveOp.create(instruction, address);
            break;
        case NEW_ARRAY:
            result = NewArrayOp.create(instruction, address, vm);
            break;
        case NEW_INSTANCE:
            result = NewInstanceOp.create(instruction, address, vm);
            break;
        case AGET:
            result = AGetOp.create(instruction, address);
            break;
        case APUT:
            result = APutOp.create(instruction, address);
            break;
        case SGET:
            result = SGetOp.create(instruction, address, vm);
            break;
        case SPUT:
            result = SPutOp.create(instruction, address, vm);
            break;
        case RETURN:
            result = ReturnOp.create(instruction, address);
            break;
        case RETURN_VOID:
            result = ReturnVoidOp.create(instruction, address);
            break;
        case UNARY_MATH:
            result = UnaryMathOp.create(instruction, address);
            break;
        }

        return result;
    }

}
