package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.MethodLocation;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class OpCreator {

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(OpCreator.class.getSimpleName());
    private final VirtualMachine vm;
    private final TIntObjectMap<MethodLocation> addressToLocation;

    public OpCreator(VirtualMachine vm, TIntObjectMap<MethodLocation> addressToLocation) {
        this.vm = vm;
        this.addressToLocation = addressToLocation;
    }

    public Op create(MethodLocation location) {
        Instruction instruction = location.getInstruction();
        Opcode opcode = instruction.getOpcode();
        OpFactory opFactory = getOpFactory(opcode);

        return opFactory.create(location, addressToLocation, vm);
    }

    @SuppressWarnings("incomplete-switch")
    private static OpFactory getOpFactory(Opcode op) {
        OpFactory opFactory = null;
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
                opFactory = new BinaryMathOpFactory();
                break;

            case AGET:
            case AGET_BOOLEAN:
            case AGET_BYTE:
            case AGET_CHAR:
            case AGET_OBJECT:
            case AGET_SHORT:
            case AGET_WIDE:
                opFactory = new AGetOpFactory();
                break;

            case APUT:
            case APUT_BOOLEAN:
            case APUT_BYTE:
            case APUT_CHAR:
            case APUT_OBJECT:
            case APUT_SHORT:
            case APUT_WIDE:
                opFactory = new APutOpFactory();
                break;

            case ARRAY_LENGTH:
                opFactory = new ArrayLengthOpFactory();
                break;

            case ARRAY_PAYLOAD:
                opFactory = new FillArrayDataPayloadOpFactory();
                break;

            case CHECK_CAST:
                opFactory = new CheckCastOpFactory();
                break;

            case CMPG_DOUBLE:
            case CMPG_FLOAT:
            case CMPL_DOUBLE:
            case CMPL_FLOAT:
            case CMP_LONG:
                opFactory = new CmpOpFactory();
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
                opFactory = new ConstOpFactory();
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
                opFactory = new UnaryMathOpFactory();
                break;

            case FILLED_NEW_ARRAY:
            case FILLED_NEW_ARRAY_RANGE:
                opFactory = new FilledNewArrayOpFactory();
                break;

            case FILL_ARRAY_DATA:
                opFactory = new FillArrayDataOpFactory();
                break;

            case GOTO:
            case GOTO_16:
            case GOTO_32:
                opFactory = new GotoOpFactory();
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
                opFactory = new IfOpFactory();
                break;

            case IGET:
            case IGET_BOOLEAN:
            case IGET_BYTE:
            case IGET_CHAR:
            case IGET_OBJECT:
            case IGET_SHORT:
            case IGET_WIDE:
                opFactory = new IGetOpFactory();
                break;

            case IPUT:
            case IPUT_BOOLEAN:
            case IPUT_BYTE:
            case IPUT_CHAR:
            case IPUT_OBJECT:
            case IPUT_SHORT:
            case IPUT_WIDE:
                opFactory = new IPutOpFactory();
                break;

            case INSTANCE_OF:
                opFactory = new InstanceOfOpFactory();
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
                opFactory = new InvokeOpFactory();
                break;

            case MONITOR_ENTER:
                opFactory = new MonitorEnterOpFactory();
                break;
            case MONITOR_EXIT:
                opFactory = new MonitorExitOpFactory();
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
                opFactory = new MoveOpFactory();
                break;

            case NEW_ARRAY:
                opFactory = new NewArrayOpFactory();
                break;

            case NEW_INSTANCE:
                opFactory = new NewInstanceOpFactory();
                break;

            case NOP:
                opFactory = new NopOpFactory();
                break;

            case PACKED_SWITCH:
            case SPARSE_SWITCH:
                opFactory = new SwitchOpFactory();
                break;

            case PACKED_SWITCH_PAYLOAD:
            case SPARSE_SWITCH_PAYLOAD:
                opFactory = new SwitchPayloadOpFactory();
                break;

            case RETURN:
            case RETURN_WIDE:
            case RETURN_OBJECT:
                opFactory = new ReturnOpFactory();
                break;

            case RETURN_VOID:
                opFactory = new ReturnVoidOpFactory();
                break;

            case SGET:
            case SGET_BOOLEAN:
            case SGET_BYTE:
            case SGET_CHAR:
            case SGET_OBJECT:
            case SGET_SHORT:
            case SGET_WIDE:
                opFactory = new SGetOpFactory();
                break;

            case SPUT:
            case SPUT_BOOLEAN:
            case SPUT_BYTE:
            case SPUT_CHAR:
            case SPUT_OBJECT:
            case SPUT_SHORT:
            case SPUT_WIDE:
                opFactory = new SPutOpFactory();
                break;

            case THROW:
                opFactory = new ThrowOpFactory();
                break;
        }

        if (opFactory == null) {
            throw new RuntimeException("Unimplemented opcode: " + op);
        }

        return opFactory;
    }
}
