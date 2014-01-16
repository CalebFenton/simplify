package simplify.exec.instruction;

import java.util.logging.Logger;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.jf.dexlib2.builder.BuilderInstruction;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction21t;
import org.jf.dexlib2.iface.instruction.formats.Instruction22t;

import simplify.Main;
import simplify.exec.InstructionExecutor;
import simplify.exec.MethodExecutionContext;
import simplify.exec.UnknownValue;

public class IfInstruction {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static int[] execute(MethodExecutionContext ectx, Instruction instruction, int index) {
        Object A;
        Object B;
        if (instruction instanceof Instruction22t) {
            // if-* vA, vB, :label
            Instruction22t instr = (Instruction22t) instruction;
            A = ectx.getRegisterValue(instr.getRegisterA(), index);
            B = ectx.getRegisterValue(instr.getRegisterB(), index);
        } else {
            // if-*z vA, vB, :label
            Instruction21t instr = (Instruction21t) instruction;
            A = ectx.getRegisterValue(instr.getRegisterA(), index);
            B = 0;
        }

        int codeAddress = ((BuilderInstruction) instruction).getLocation().getCodeAddress();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int targetAddress = codeAddress + branchOffset;

        // Ambiguous predicate. Must take both branches.
        if ((A instanceof UnknownValue) || (B instanceof UnknownValue)) {
            return new int[] { targetAddress, InstructionExecutor.NEXT_INSTRUCTION };
        }

        int cmp = CompareToBuilder.reflectionCompare(A, B);
        log.finer("IF instruction, compare: " + A + " vs " + B + " = " + cmp);
        String ifType = instruction.getOpcode().name;
        if (ifType.endsWith("z")) {
            // Normalize opcode name because already accounted for compare zero's when assigning B.
            ifType.substring(0, ifType.length() - 1);
        }

        int result = InstructionExecutor.NEXT_INSTRUCTION;
        if ((ifType.endsWith("eq") && (cmp == 0)) || (ifType.endsWith("ne") && (cmp != 0))
                        || (ifType.endsWith("lt") && (cmp < 0)) || (ifType.endsWith("le") && (cmp <= 0))
                        || (ifType.endsWith("gt") && (cmp > 0)) || (ifType.endsWith("ge") && (cmp >= 0))) {
            result = targetAddress;
        }

        return new int[] { result };
    }

}
