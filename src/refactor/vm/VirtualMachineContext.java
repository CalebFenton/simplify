package refactor.vm;

import java.util.logging.Logger;

import org.jf.util.SparseArray;

import simplify.Main;

public class VirtualMachineContext {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final SparseArray<RegisterStore> registers;
    private final int registerCount;

    VirtualMachineContext() {
        this(0);
    }

    VirtualMachineContext(int registerCount) {
        registers = new SparseArray<RegisterStore>(registerCount);

        this.registerCount = registerCount;
    }

    VirtualMachineContext(VirtualMachineContext other) {
        registerCount = other.getRegisterCount();
        registers = new SparseArray<RegisterStore>(registerCount);

        for (int i = 0; i < registerCount; i++) {
            RegisterStore registerStore = other.registers.get(i);
            registers.put(i, registerStore);
        }
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public void setRegister(int register, RegisterStore rs) {
        StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        StringBuilder sb = new StringBuilder();
        // for (int i = 2; i < ste.length; i++) {
        // sb.append("\n\t").append(ste[i]);
        // }
        log.info("Setting register @" + register + " rs:" + rs + sb.toString());

        registers.put(register, rs);
    }

    public void setRegister(int register, String type, Object value, int index) {
        RegisterStore current = new RegisterStore(type, value);
        current.getReferenced().add(index);
        setRegister(register, current);
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

    public RegisterStore peekRegister(int register) {
        RegisterStore rs = registers.get(register);

        if (rs == null) {
            log.warning("r" + register + " is being read but is null, likely a mistake! Context:\n" + this);
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

        int registerCount = getRegisterCount();
        sb.append("registers: ").append(registerCount);

        if (registerCount > 0) {
            sb.append("\n");
        }

        for (int i = 0; i < registerCount; i++) {
            RegisterStore rs = registers.get(i);
            if (rs == null) {
                continue;
            }

            int register = registers.keyAt(i);
            sb.append("[").append("r").append(register).append(": ").append(rs).append(",\n");
        }
        sb.setLength(sb.length() - 2);
        sb.append("]");

        return sb.toString();
    }
}
