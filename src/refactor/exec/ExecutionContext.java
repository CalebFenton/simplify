package refactor.exec;

import java.util.logging.Logger;

import org.jf.util.SparseArray;

import simplify.Main;
import simplify.exec.RegisterStore;

public class ExecutionContext {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final SparseArray<RegisterStore> registers;
    private final int callDepth;

    ExecutionContext(int callDepth) {
        this(callDepth, 0);
    }

    ExecutionContext(int callDepth, int registerCount) {
        registers = new SparseArray<RegisterStore>(registerCount);

        this.callDepth = callDepth;
    }

    ExecutionContext(ExecutionContext ectx) {
        registers = new SparseArray<RegisterStore>(ectx.registers.size());
        for (int i = 0; i < registers.size(); i++) {
            int key = ectx.registers.keyAt(i);
            RegisterStore value = ectx.registers.get(key);
            registers.put(key, value);
        }

        this.callDepth = ectx.callDepth;
    }

    public int getRegisterCount() {
        return registers.size();
    }

    public void addRegister(int register, RegisterStore rs) {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        // for (int i = 2; i < ste.length; i++) {
        // sb.append("\n\t").append(ste[i]);
        // }
        log.finer("Adding register @" + register + " rs:" + rs + sb.toString());

        registers.put(register, rs);
    }

    public void addRegister(int register, String type, Object value, int index) {
        RegisterStore current = new RegisterStore(type, value);
        current.getReferenced().add(index);
        addRegister(register, current);
    }

    public RegisterStore getRegister(int register, int index) {
        RegisterStore rs = peekRegister(register);

        /*
         * Indexed access to a register implies it's being "read" by some instruction. Mark it as used at this
         * instruction index so the nop code optimizer knows not to remove this line.
         */
        rs.getReferenced().add(index);

        return rs;
    }

    public Object getRegisterValue(int register, int index) {
        // Convenience for those who don't care about the type
        return getRegister(register, index).getValue();
    }

    public int getCallDepth() {
        return callDepth;
    }

    public RegisterStore peekRegister(int register) {
        RegisterStore rs = registers.get(register);

        if (rs == null) {
            log.warning("r" + register + " is being read but is null, likely a mistake!\n" + this);
        }

        return rs;
    }

    public String peekRegisterType(int register) {
        RegisterStore rs = peekRegister(register);

        return rs.getType();
    }

    public Object peekRegisterValue(int register) {
        RegisterStore rs = peekRegister(register);

        return rs.getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("registers: ").append(registers.size());
        sb.append(", call depth: ").append(callDepth).append("\n");

        for (int i = 0; i < registers.size(); i++) {
            RegisterStore rs = registers.get(i);
            if (rs == null) {
                continue;
            }

            sb.append("[").append("r").append(i).append(": ").append(rs).append(",\n");
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }

}
