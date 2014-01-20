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
            RegisterStore otherRS = other.registers.get(i);
            if (otherRS == null) {
                continue;
            }

            if (registers.get(i) != null) {
                // If this register is already populated, it was cloned in the coming inner loop for sharing the same
                // value object reference with another register store.
                continue;
            }

            /*
             * Some registers may share the same object reference so this relationship must persist in clones. The
             * relationship will not persist after the children of an initial branch, however, but this won't be a
             * problem because each will be self-consistent and since each represents a possible execution path, they
             * shouldn't need to examine each other except at optimization.
             */
            RegisterStore rsClone = new RegisterStore(otherRS);
            for (int j = 0; j < registerCount; j++) {
                if (j == i) {
                    // Identity
                    continue;
                }

                RegisterStore possibleMatch = registers.get(j);
                if (possibleMatch == null) {
                    continue;
                }

                if (otherRS.getValue() == possibleMatch.getValue()) {
                    log.finest("context clone, r" + j + " == r" + i);
                    registers.put(j, rsClone);
                }
            }
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

    public void setRegister(int register, String type, Object value, int address) {
        RegisterStore current = new RegisterStore(type, value);
        current.getReferenced().add(address);
        setRegister(register, current);
    }

    public RegisterStore getRegister(int register, int address) {
        RegisterStore rs = peekRegister(register);

        /*
         * Addressed access to a register implies it's being "read" by some instruction. The optimizer needs to know if
         * a register was used and wear for no-op removal.
         */
        rs.getReferenced().add(address);

        return rs;
    }

    public Object getRegisterValue(int register, int address) {
        // Convenience for those who don't care about the type
        return getRegister(register, address).getValue();
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
