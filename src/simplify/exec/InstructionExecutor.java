package simplify.exec;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.ExceptionHandler;
import org.jf.dexlib2.iface.TryBlock;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;
import org.jf.dexlib2.iface.instruction.SwitchElement;
import org.jf.dexlib2.iface.instruction.SwitchPayload;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction11n;
import org.jf.dexlib2.iface.instruction.formats.Instruction11x;
import org.jf.dexlib2.iface.instruction.formats.Instruction21c;
import org.jf.dexlib2.iface.instruction.formats.Instruction21s;
import org.jf.dexlib2.iface.instruction.formats.Instruction22c;
import org.jf.dexlib2.iface.reference.StringReference;
import org.jf.dexlib2.writer.builder.BuilderClassDef;

import simplify.Main;
import simplify.exec.instruction.BinaryMathInstruction;
import simplify.exec.instruction.IfInstruction;
import simplify.exec.instruction.InvokeInstruction;
import simplify.exec.instruction.MoveInstruction;
import simplify.exec.instruction.NewArrayInstruction;
import simplify.exec.instruction.NewInstanceInstruction;

public class InstructionExecutor {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static final int NEXT_INSTRUCTION = -1;

    public static List<Integer> execute(List<BuilderClassDef> classes,
                    List<? extends TryBlock<? extends ExceptionHandler>> tryBlocks, MethodExecutionContext ectx,
                    BuilderInstruction instruction) {

        int index = instruction.getLocation().getIndex();
        log.fine("Executing instruction: " + instruction.getOpcode().name + " @" + index);

        List<Integer> childOffsets = new ArrayList<Integer>();
        boolean handled = false;
        int[] offsets;
        switch (instruction.getOpcode()) {
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
            BinaryMathInstruction.execute(ectx, (TwoRegisterInstruction) instruction, index);
            handled = true;
            break;
        case AGET:
            break;
        case AGET_BOOLEAN:
            break;
        case AGET_BYTE:
            break;
        case AGET_CHAR:
            break;
        case AGET_OBJECT:
            break;
        case AGET_SHORT:
            break;
        case AGET_WIDE:
            break;
        case APUT:
            break;
        case APUT_BOOLEAN:
            break;
        case APUT_BYTE:
            break;
        case APUT_CHAR:
            break;
        case APUT_OBJECT:
            break;
        case APUT_SHORT:
            break;
        case APUT_WIDE:
            break;
        case ARRAY_LENGTH:
            break;
        case ARRAY_PAYLOAD:
            break;
        case CHECK_CAST:
            break;
        case CMPG_DOUBLE:
            break;
        case CMPG_FLOAT:
            break;
        case CMPL_DOUBLE:
            break;
        case CMPL_FLOAT:
            break;
        case CMP_LONG:
            break;
        case CONST:
            break;
        case CONST_16:
            handle_CONST_16(ectx, (Instruction21s) instruction, index);
            handled = true;
            break;
        case CONST_4:
            handle_CONST_4(ectx, (Instruction11n) instruction, index);
            handled = true;
            break;
        case CONST_CLASS:
            break;
        case CONST_HIGH16:
            break;
        case CONST_STRING:
            handle_CONST_STRING(ectx, (Instruction21c) instruction, index);
            handled = true;
        case CONST_STRING_JUMBO:
            break;
        case CONST_WIDE:
            break;
        case CONST_WIDE_16:
            break;
        case CONST_WIDE_32:
            break;
        case CONST_WIDE_HIGH16:
            break;
        case DOUBLE_TO_FLOAT:
            break;
        case DOUBLE_TO_INT:
            break;
        case DOUBLE_TO_LONG:
            break;
        case FILLED_NEW_ARRAY:
            break;
        case FILLED_NEW_ARRAY_RANGE:
            break;
        case FILL_ARRAY_DATA:
            break;
        case FLOAT_TO_DOUBLE:
            break;
        case FLOAT_TO_INT:
            break;
        case FLOAT_TO_LONG:
            break;
        case GOTO:
        case GOTO_16:
        case GOTO_32:
            childOffsets.add(handle_GOTO(instruction));
            handled = true;
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
            offsets = IfInstruction.execute(ectx, instruction, index);
            for (int offset : offsets) {
                childOffsets.add(offset);
            }
            handled = true;
            break;
        case IGET:
        case IGET_BOOLEAN:
        case IGET_BYTE:
        case IGET_CHAR:
        case IGET_OBJECT:
        case IGET_SHORT:
        case IGET_WIDE:
            handle_IGET(ectx, (Instruction22c) instruction, index);
            handled = true;
            break;
        case INSTANCE_OF:
            break;
        case INT_TO_BYTE:
            break;
        case INT_TO_CHAR:
            break;
        case INT_TO_DOUBLE:
            break;
        case INT_TO_FLOAT:
            break;
        case INT_TO_LONG:
            break;
        case INT_TO_SHORT:
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
            InvokeInstruction.execute(ectx, instruction, index, classes);
            handled = true;
            break;
        case IPUT:
        case IPUT_BOOLEAN:
        case IPUT_BYTE:
        case IPUT_CHAR:
        case IPUT_OBJECT:
        case IPUT_SHORT:
        case IPUT_WIDE:
            handle_IPUT(ectx, (Instruction22c) instruction, index);
            handled = true;
            break;
        case LONG_TO_DOUBLE:
            break;
        case LONG_TO_FLOAT:
            break;
        case LONG_TO_INT:
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
            MoveInstruction.execute(ectx, (TwoRegisterInstruction) instruction, index);
            handled = true;
            break;
        case MOVE_EXCEPTION:
            break;
        case MOVE_RESULT:
        case MOVE_RESULT_OBJECT:
        case MOVE_RESULT_WIDE:
            handle_MOVE_RESULT(ectx, (Instruction11x) instruction, index);
            handled = true;
            break;
        case NEG_DOUBLE:
            break;
        case NEG_FLOAT:
            break;
        case NEG_INT:
            break;
        case NEG_LONG:
            break;
        case NEW_ARRAY:
            NewArrayInstruction.execute(ectx, (Instruction22c) instruction, index);
            handled = true;
            break;
        case NEW_INSTANCE:
            NewInstanceInstruction.execute(ectx, (Instruction21c) instruction, index);
            handled = true;
            break;
        case NOP:
            handled = true;
            break;
        case NOT_INT:
            break;
        case NOT_LONG:
            break;
        case PACKED_SWITCH:
            break;
        case PACKED_SWITCH_PAYLOAD:
            break;
        case RETURN:
        case RETURN_WIDE:
        case RETURN_OBJECT:
            handle_RETURN(ectx, (Instruction11x) instruction, index);
            handled = true;
            break;
        case RETURN_VOID:
            handled = true;
            break;
        case SGET:
        case SGET_BOOLEAN:
        case SGET_BYTE:
        case SGET_CHAR:
        case SGET_OBJECT:
        case SGET_SHORT:
        case SGET_WIDE:
            handle_SGET(ectx, (Instruction21c) instruction, index);
            handled = true;
            break;
        case SPARSE_SWITCH:
            break;
        case SPARSE_SWITCH_PAYLOAD:
            break;
        case SPUT:
        case SPUT_BOOLEAN:
        case SPUT_BYTE:
        case SPUT_CHAR:
        case SPUT_OBJECT:
        case SPUT_SHORT:
        case SPUT_WIDE:
            handle_SPUT(ectx, (Instruction21c) instruction, index);
            handled = true;
            break;
        case THROW:
            break;
        default:
            log.warning("Unknown instruction to dexlib. Shouldn't happen.");
            break;
        }

        Opcode op = instruction.getOpcode();
        if (op.canContinue() && (childOffsets.size() == 0)) {
            childOffsets.add(NEXT_INSTRUCTION);
        }

        if (op.canThrow()) {
            childOffsets.addAll(handlePotentialExceptions(instruction, tryBlocks));
        }

        // If the op is unhandled, take all possible branches or set any values to unknown.
        if (!handled) {
            if (instruction instanceof OffsetInstruction) {
                int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
                int codeAddress = instruction.getLocation().getCodeAddress();
                childOffsets.add(codeAddress + branchOffset);
            } else if (instruction instanceof SwitchPayload) {
                List<? extends SwitchElement> elements = ((SwitchPayload) instruction).getSwitchElements();
                for (SwitchElement element : elements) {
                    childOffsets.add(element.getOffset());
                }
            }

            if (op.setsRegister()) {
                OneRegisterInstruction instr = (OneRegisterInstruction) instruction;
                ectx.addRegister(instr.getRegisterA(), "?", new UnknownValue(), index);
            }

            if (op.setsResult()) {
                ectx.setResultRegister(new RegisterStore("?", new UnknownValue()));
            }
        }

        return childOffsets;
    }

    private static List<Integer> handlePotentialExceptions(BuilderInstruction instruction,
                    List<? extends TryBlock<? extends ExceptionHandler>> tryBlocks) {
        /*
         * Lazy mode: Assume any op that *could* throw *might* throw by adding it to children. TODO: make this more
         * accurate. will probably need massive table of which ops can throw what instructions, and combined with
         * dynamically looking up method exceptions and checking against them
         */
        List<Integer> result = new ArrayList<Integer>();
        Opcode op = instruction.getOpcode();
        int address = instruction.getLocation().getCodeAddress();
        for (TryBlock<? extends ExceptionHandler> tryBlock : tryBlocks) {
            int start = tryBlock.getStartCodeAddress();
            int end = start + tryBlock.getCodeUnitCount();
            if ((address < start) || (address > end)) {
                continue;
            }

            List<? extends ExceptionHandler> handlers = tryBlock.getExceptionHandlers();
            for (ExceptionHandler handler : handlers) {
                log.finest(op.name + " @" + instruction.getLocation().getIndex() + ", adding exception offset: "
                                + handler.getHandlerCodeAddress());
                result.add(handler.getHandlerCodeAddress());
            }
        }

        return result;
    }

    private static int handle_GOTO(BuilderInstruction instruction) {
        int codeAddress = instruction.getLocation().getCodeAddress();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();

        return codeAddress + branchOffset;
    }

    private static void handle_CONST_16(MethodExecutionContext ectx, Instruction21s instruction, int index) {
        ectx.addRegister(instruction.getRegisterA(), "I", instruction.getNarrowLiteral(), index);
    }

    private static void handle_CONST_4(MethodExecutionContext ectx, Instruction11n instruction, int index) {
        ectx.addRegister(instruction.getRegisterA(), "I", instruction.getNarrowLiteral(), index);
    }

    private static void handle_CONST_STRING(MethodExecutionContext ectx, Instruction21c instruction, int index) {
        StringReference stringRef = (StringReference) instruction.getReference();
        ectx.addRegister(instruction.getRegisterA(), "Ljava/lang/String;", stringRef.getString(), index);
    }

    private static void handle_RETURN(MethodExecutionContext ectx, Instruction11x instruction, int index) {
        ectx.setReturnRegister(instruction.getRegisterA(), index);
    }

    private static void handle_IGET(MethodExecutionContext ectx, Instruction22c instruction, int index) {
        // We can't be sure what a member variable might have at any given time.
        // Another thread could have modified it.
        ectx.addRegister(instruction.getRegisterA(), "?", new UnknownValue(), index);
    }

    private static void handle_IPUT(MethodExecutionContext ectx, Instruction22c instruction, int index) {
        // No use setting instance member values, since can't be sure they're not changed.
    }

    private static void handle_SGET(MethodExecutionContext ectx, Instruction21c instruction, int index) {
        // TODO: static gets of framework classes can safely be retrieved
        ectx.addRegister(instruction.getRegisterA(), "?", new UnknownValue(), index);
    }

    private static void handle_SPUT(MethodExecutionContext ectx, Instruction21c instruction, int index) {
        // Not implemented.
    }

    private static void handle_MOVE_RESULT(MethodExecutionContext ectx, Instruction11x instruction, int index) {
        ectx.addRegister(instruction.getRegisterA(), ectx.getResultRegister());
    }

}
