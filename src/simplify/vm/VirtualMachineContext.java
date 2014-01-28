package simplify.vm;

import java.util.logging.Logger;

import simplify.Main;
import util.SparseArray;

public class VirtualMachineContext {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final SparseArray<RegisterStore> registers;
    private final int registerCount;

    VirtualMachineContext() {
        this(0);
    }

    VirtualMachineContext(int registerCount) {
        // TODO: Profile "sparse register" idea, where each context only stores registers changed during that
        // instruction, and getting register values is a matter of starting at the current context and tracing up
        // through ancestors until the sought after register value is reached. This is a cpu / memory trade off that
        // might be good overall given the number of contexts in memory while executing non-trivial methods.

        // Since there could potentially be many copies of this in memory, don't reserve space for registers.
        registers = new SparseArray<RegisterStore>(0);

        this.registerCount = registerCount;
    }

    VirtualMachineContext(VirtualMachineContext other) {
        registerCount = other.getRegisterCount();
        registers = new SparseArray<RegisterStore>(other.registers.size());

        for (int i = 0; i < other.registers.size(); i++) {
            int outerKey = other.registers.keyAt(i);
            RegisterStore otherRS = other.registers.get(outerKey);
            if (otherRS == null) {
                continue;
            }

            if (registers.get(outerKey) != null) {
                // If this register is already populated, it was cloned in the coming inner loop for sharing the same
                // value object reference with another register store and can skipped.
                continue;
            }

            /*
             * Some registers may share the same object reference so this relationship must persist in clones. The
             * relationship will not persist after the children of an initial branch, however, but this won't be a
             * problem because each will be self-consistent and since each represents a possible execution path, they
             * shouldn't need to examine each other except at optimization.
             */
            RegisterStore rsClone = new RegisterStore(otherRS);
            for (int j = 0; j < registers.size(); j++) {
                int innerKey = registers.keyAt(j);
                if (outerKey == innerKey) {
                    // Identity
                    continue;
                }

                RegisterStore possibleMatch = registers.get(innerKey);
                if (otherRS.toString().equals(possibleMatch.toString())) {
                    log.fine("context clone, r" + innerKey + " == r" + outerKey);
                    registers.put(innerKey, rsClone);
                }
            }

            registers.put(outerKey, rsClone);
        }
    }

    public int getRegisterCount() {
        return registerCount;
    }

    void removeRegister(int register) {
        registers.delete(register);
    }

    public SparseArray<RegisterStore> getRegisterToStore() {
        SparseArray<RegisterStore> result = new SparseArray<RegisterStore>(registers.size());
        for (int i = 0; i < registers.size(); i++) {
            int register = registers.keyAt(i);
            RegisterStore registerStore = peekRegister(register);
            result.put(register, registerStore);

            // Longs are fat and take up two registers
            if (registerStore.getType().equals("J")) {
                register++;
            }
        }

        return result;
    }

    public void pokeRegister(int register, RegisterStore registerStore) {
        StringBuilder sb = new StringBuilder();
        // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        // for (int i = 2; i < ste.length; i++) {
        // sb.append("\n\t").append(ste[i]);
        // }
        log.fine("Setting r" + register + " -> " + registerStore + sb.toString());

        registers.put(register, registerStore);
    }

    public void setRegister(int register, RegisterStore registerStore, int address) {
        registerStore.addAssigned(address);
        pokeRegister(register, registerStore);
    }

    public void setRegister(int register, String type, Object value, int address) {
        RegisterStore registerStore = new RegisterStore(type, value);
        setRegister(register, registerStore, address);
    }

    public RegisterStore getRegister(int register, int address) {
        RegisterStore registerStore = peekRegister(register);
        registerStore.addRead(address);

        return registerStore;
    }

    public Object getRegisterValue(int register, int address) {
        // Convenience for those who don't care about the type
        return getRegister(register, address).getValue();
    }

    public RegisterStore peekRegister(int register) {
        RegisterStore result = registers.get(register);

        if ((result == null) && (register >= 0)) {
            log.warning("r" + register + " is being read but is null, likely a mistake! Context:\n" + this);
        }

        return result;
    }

    public String peekRegisterType(int register) {
        RegisterStore result = peekRegister(register);

        return result.getType();
    }

    public Object peekRegisterValue(int register) {
        RegisterStore result = peekRegister(register);

        return result.getValue();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (registerCount > 0) {
            sb.append("registers: ").append(registerCount).append("\n");
            sb.append("[");
            for (int i = 0; i < registers.size(); i++) {
                int register = registers.keyAt(i);
                RegisterStore rs = registers.get(register);
                if (register < 0) {
                    continue;
                }

                sb.append("r").append(register).append(": ").append(rs).append(",\n");
            }
            if (registers.size() > 0) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
        }

        return sb.toString();
    }
}
