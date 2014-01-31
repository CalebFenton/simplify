package simplify.vm;

import java.util.logging.Logger;

import simplify.Main;
import simplify.SmaliClassUtils;
import util.SparseArray;

public class VirtualMachineContext {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private final SparseArray<RegisterStore> registerToStore;
    private final int registerCount;

    VirtualMachineContext() {
        this(0);
    }

    VirtualMachineContext(int registerCount) {
        // TODO: Profile "sparse register" idea, where each context only stores registerToStore changed during that
        // instruction, and getting register values is a matter of starting at the current context and tracing up
        // through ancestors until the sought after register value is reached. This is a cpu / memory trade off that
        // might be good overall given the number of contexts in memory while executing non-trivial methods.

        // Since there could potentially be many copies of this in memory, don't reserve space for registerToStore.
        registerToStore = new SparseArray<RegisterStore>(0);

        this.registerCount = registerCount;
    }

    VirtualMachineContext(VirtualMachineContext other) {
        registerCount = other.getRegisterCount();
        registerToStore = new SparseArray<RegisterStore>(other.registerToStore.size());

        for (int i = 0; i < other.registerToStore.size(); i++) {
            int outerKey = other.registerToStore.keyAt(i);
            RegisterStore otherRS = other.registerToStore.get(outerKey);
            if (otherRS == null) {
                continue;
            }

            if (registerToStore.get(outerKey) != null) {
                // If this register is already populated, it was cloned in the coming inner loop for sharing the same
                // value object reference with another register store and can skipped.
                continue;
            }

            /*
             * Some registerToStore may point to the same object, and this must persist across context clones. They wont
             * persist after the children of a branch. This won't be a problem because each possibility represents a
             * different execution path and they shouldn't need to examine each other except at optimization.
             */
            RegisterStore rsClone = new RegisterStore(otherRS);
            for (int j = 0; j < other.registerToStore.size(); j++) {
                if (i == j) {
                    continue;
                }

                int innerKey = other.registerToStore.keyAt(j);
                RegisterStore possibleMatch = other.registerToStore.get(innerKey);
                if (otherRS == possibleMatch) {
                    log.finer("cloning identity of r" + innerKey + " == r" + outerKey);
                    registerToStore.put(innerKey, rsClone);
                }
            }

            registerToStore.put(outerKey, rsClone);
        }
    }

    public int getRegisterCount() {
        return registerCount;
    }

    void removeRegister(int register) {
        registerToStore.delete(register);
    }

    public SparseArray<RegisterStore> getRegisterToStore() {
        SparseArray<RegisterStore> result = new SparseArray<RegisterStore>(registerToStore.size());
        for (int i = 0; i < registerToStore.size(); i++) {
            int register = registerToStore.keyAt(i);
            RegisterStore registerStore = peekRegister(register);
            result.put(register, registerStore);

            // Longs are fat and take up two registerToStore
            if (registerStore.getType().equals("J")) {
                register++;
            }
        }

        return result;
    }

    protected void pokeRegister(int register, RegisterStore rs) {
        StringBuilder sb = new StringBuilder();
        // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        // for (int i = 2; i < ste.length; i++) {
        // sb.append("\n\t").append(ste[i]);
        // }

        log.fine("Setting r" + register + " -> " + rs + sb.toString());

        registerToStore.put(register, rs);
    }

    public void pokeRegister(int register, String type, Object value) {
        pokeRegister(register, findRegisterStoreForValue(type, value));
    }

    private RegisterStore findRegisterStoreForValue(String type, Object value) {
        RegisterStore result = null;
        if (!SmaliClassUtils.isPrimitiveType(type) && !type.equals("Ljava/lang/String;")) {
            for (RegisterStore currentRS : registerToStore.getValues()) {
                if (currentRS.getValue() == value) {
                    result = currentRS;
                    break;
                }
            }
        }

        if (result == null) {
            result = new RegisterStore(type, value);
        }

        return result;
    }

    private void setRegister(int register, RegisterStore rs, int address) {
        rs.addAssigned(address);

        pokeRegister(register, rs);
    }

    public void setRegister(int register, String type, Object value, int address) {
        setRegister(register, findRegisterStoreForValue(type, value), address);
    }

    public RegisterStore getRegister(int register, int address) {
        RegisterStore rs = peekRegister(register);
        rs.addRead(address);

        return rs;
    }

    public Object getRegisterValue(int register, int address) {
        // Convenience for those who don't care about the type
        return getRegister(register, address).getValue();
    }

    public RegisterStore peekRegister(int register) {
        RegisterStore result = registerToStore.get(register);

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
            for (int i = 0; i < registerToStore.size(); i++) {
                int register = registerToStore.keyAt(i);
                if (register < 0) {
                    continue;
                }
                RegisterStore rs = registerToStore.get(register);

                sb.append("r").append(register).append(": ").append(rs).append(",\n");
            }
            if (registerToStore.size() > 0) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
        }

        return sb.toString();
    }
}
