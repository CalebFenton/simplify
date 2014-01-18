package refactor.op;

import org.jf.dexlib2.Opcode;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OneRegisterInstruction;

public class UnimplementedOpHandlerFactory {

    static UnimplementedOpHandler create(Instruction instruction, int index) {
        Opcode op = instruction.getOpcode();
        UnimplementedOpHandler result = null;
        if (op.setsRegister()) {
            // Can assume it has at least one register
            int destRegister = ((OneRegisterInstruction) instruction).getRegisterA();
            result = new UnimplementedOpHandler(index, op.canContinue(), op.canThrow(), op.name, destRegister);
        } else {
            result = new UnimplementedOpHandler(index, op.canContinue(), op.canThrow(), op.name);
        }

        return result;
    }
}
