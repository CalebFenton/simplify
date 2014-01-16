package simplify.exec.instruction;

import java.util.logging.Logger;

import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction12x;
import org.jf.dexlib2.iface.instruction.formats.Instruction22b;
import org.jf.dexlib2.iface.instruction.formats.Instruction22s;
import org.jf.dexlib2.iface.instruction.formats.Instruction23x;

import simplify.Main;
import simplify.exec.MethodExecutionContext;
import simplify.exec.UnknownValue;

public class BinaryMathInstruction {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static void execute(MethodExecutionContext ectx, TwoRegisterInstruction instruction, int index) {
        int destination = instruction.getRegisterA();

        Object[] args = getLeftAndRight(ectx, instruction, index);
        Object lhs = args[0];
        Object rhs = args[1];

        String opName = instruction.getOpcode().name;
        log.finest(opName + " lhs:" + lhs + ", rhs:" + rhs);

        Object result = new UnknownValue();
        if (!(lhs instanceof UnknownValue) && !(rhs instanceof UnknownValue)) {
            result = getResult(opName, lhs, rhs);

            if (result == null) {
                log.warning("Goof in binary math because null result!");
            }
        }

        // Destination register should be same as lhs op
        String type = ectx.peekRegisterType(instruction.getRegisterB());
        ectx.addRegister(destination, type, result, index);
    }

    private static Object getResult(String opName, Object lhs, Object rhs) {
        Object result = null;

        // TODO: shifts are different from this : http://source.android.com/devices/tech/dalvik/dalvik-bytecode.html
        if (opName.startsWith("add")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs + (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs + (Long) rhs;
            } else if (opName.contains("-float")) {
                result = (Long) lhs + (Long) rhs;
            } else if (opName.contains("-double")) {
                result = (Long) lhs + (Long) rhs;
            }
        } else if (opName.startsWith("sub")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs - (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs - (Long) rhs;
            } else if (opName.contains("-float")) {
                result = (Long) lhs - (Long) rhs;
            } else if (opName.contains("-double")) {
                result = (Long) lhs - (Long) rhs;
            }
        } else if (opName.startsWith("mul")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs * (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs * (Long) rhs;
            } else if (opName.contains("-float")) {
                result = (Long) lhs * (Long) rhs;
            } else if (opName.contains("-double")) {
                result = (Long) lhs * (Long) rhs;
            }
        } else if (opName.startsWith("div")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs / (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs / (Long) rhs;
            } else if (opName.contains("-float")) {
                result = (Long) lhs / (Long) rhs;
            } else if (opName.contains("-double")) {
                result = (Long) lhs / (Long) rhs;
            }
        } else if (opName.startsWith("rem")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs % (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs % (Long) rhs;
            } else if (opName.contains("-float")) {
                result = (Long) lhs % (Long) rhs;
            } else if (opName.contains("-double")) {
                result = (Long) lhs % (Long) rhs;
            }
        } else if (opName.startsWith("and")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs & (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs & (Long) rhs;
            }
        } else if (opName.startsWith("or")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs | (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs | (Long) rhs;
            }
        } else if (opName.startsWith("xor")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs ^ (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs ^ (Long) rhs;
            }
        } else if (opName.startsWith("shl")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs << (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs << (Long) rhs;
            }
        } else if (opName.startsWith("shr")) {
            if (opName.contains("-int")) {
                result = (Integer) lhs >> (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs >> (Long) rhs;
            }
        } else if (opName.startsWith("ushr")) {
            if (opName.contains("-int")) {
                // You don't see this operator every day.
                result = (Integer) lhs >>> (Integer) rhs;
            } else if (opName.contains("-long")) {
                result = (Long) lhs >>> (Long) rhs;
            }
        } else if (opName.startsWith("rsub")) {
            result = (Integer) rhs - (Integer) lhs;
        }

        return result;
    }

    private static Object[] getLeftAndRight(MethodExecutionContext ectx, Instruction instruction, int index) {
        Object lhs = null;
        Object rhs = null;
        if (instruction instanceof Instruction23x) {
            // add-int vAA, vBB, vCC
            Instruction23x instr = (Instruction23x) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterB(), index);
            rhs = ectx.getRegisterValue(instr.getRegisterC(), index);
        } else if (instruction instanceof Instruction12x) {
            // add-int/2addr vAA, vBB
            Instruction12x instr = (Instruction12x) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterA(), index);
            rhs = ectx.getRegisterValue(instr.getRegisterB(), index);
        } else if (instruction instanceof Instruction22s) {
            // add-int/lit16 vAA, vBB, #CCCC
            Instruction22s instr = (Instruction22s) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterB(), index);
            rhs = instr.getWideLiteral();
        } else if (instruction instanceof Instruction22b) {
            // add-int/lit8 vAA, vBB, #CC
            Instruction22b instr = (Instruction22b) instruction;
            lhs = ectx.getRegisterValue(instr.getRegisterB(), index);
            rhs = instr.getNarrowLiteral();
        }

        return new Object[] { lhs, rhs };
    }
}
