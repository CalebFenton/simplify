package simplify.vm;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;

import java.util.logging.Logger;

import simplify.Main;
import simplify.SmaliClassUtils;
import util.SparseArray;

public class VirtualMachineContext {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private VirtualMachineContext parent;
    private final int registerCount;
    private final TIntList registersAssigned;
    private final TIntList registersRead;
    private final SparseArray<Object> registerToValue;

    VirtualMachineContext() {
        this(0);
    }

    VirtualMachineContext(int registerCount) {
        // The number of instances of contexts in memory could be very high. Allocate minimally.
        registerToValue = new SparseArray<Object>(0);
        registersAssigned = new TIntArrayList(0);
        registersRead = new TIntArrayList(0);

        this.registerCount = registerCount;
    }

    public void assignRegister(int register, Object value) {
        registersAssigned.add(register);

        pokeRegister(register, value);
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public SparseArray<Object> getRegisterToValue() {
        // Massage weird registers out of registerToValue map, like the second register number of a wide register.
        SparseArray<Object> result = new SparseArray<Object>(registerToValue.size());
        for (int i = 0; i < registerToValue.size(); i++) {
            int register = registerToValue.keyAt(i);
            Object value = peekRegister(register);
            result.put(register, value);

            // Longs are fatties and take up two registers
            if (SmaliClassUtils.getValueType(value).equals("J")) {
                register++;
            }
        }

        return result;
    }

    public Object peekRegister(int register) {
        Object result = null;
        VirtualMachineContext currentContext = this;
        do {
            // If register is not known here, ask ancestors.
            result = currentContext.registerToValue.get(register);
            currentContext = currentContext.parent;
        } while ((result == null) && (currentContext != null));

        if ((result == null) && (register >= 0)) {
            log.warning("r" + register + " is being read but is null, likely a mistake!");
        }

        return result;
    }

    public String peekRegisterType(int register) {
        Object value = peekRegister(register);

        return SmaliClassUtils.getValueType(value);
    }

    public void pokeRegister(int register, Object value) {
        StringBuilder sb = new StringBuilder();
        // StackTraceElement[] ste = Thread.currentThread().getStackTrace();
        // for (int i = 2; i < ste.length; i++) {
        // sb.append("\n\t").append(ste[i]);
        // }

        registerToValue.put(register, value);

        log.fine("Setting r" + register + " -> " + registerToString(register) + sb.toString());
    }

    public Object readRegister(int register) {
        registersRead.add(register);

        return peekRegister(register);
    }

    protected String registerToString(int register) {
        StringBuilder result = new StringBuilder();
        Object value = registerToValue.get(register);

        result.append("type=").append(SmaliClassUtils.getValueType(value)).append(" value=").append(value.toString());

        return result.toString();
    }

    public void removeRegister(int register) {
        registerToValue.delete(register);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (registerCount > 0) {
            sb.append("registers: ").append(registerCount).append("\n");
            sb.append("[");
            for (int i = 0; i < registerToValue.size(); i++) {
                int register = registerToValue.keyAt(i);
                if (register < 0) {
                    // Result and return registers use keys <0 and subclasses handle displaying them.
                    continue;
                }

                sb.append("r").append(register).append(": ").append(registerToString(register)).append(",\n");
            }
            if (registerToValue.size() > 0) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
        }

        return sb.toString();
    }

    VirtualMachineContext(VirtualMachineContext parent) {
        this(parent.registerCount);

        for (int i = 0; i < parent.registerToValue.size(); i++) {
            int register = parent.registerToValue.keyAt(i);
            Object value = parent.registerToValue.get(register);

            pokeRegister(register, value);
        }
    }
}
