package simplify.vm;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.logging.Logger;

import simplify.Main;
import simplify.SmaliClassUtils;
import util.SparseArray;

import com.rits.cloning.Cloner;

public class VirtualMachineContext {

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private static final Cloner cloner = new Cloner();

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
        getRegistersAssigned().add(register);

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
        TIntSet reassigned = new TIntHashSet(0);
        while (currentContext != null) {
            // If register is not known here, ask ancestors.
            result = currentContext.registerToValue.get(register);
            if (result != null) {
                break;
            }

            // Keep track of any registers between this and the context with the target register. When bringing down any
            // identical registers to the target, exclude them since they've been reassigned.
            for (int i = 0; i < currentContext.registerToValue.size(); i++) {
                int key = currentContext.registerToValue.keyAt(i);
                if (key != register) {
                    reassigned.add(key);
                }
            }

            currentContext = currentContext.parent;
        }

        if (result == null) {
            log.warning("r" + register + " is being read but is null, likely a mistake!");
        } else {
            if (currentContext != this) {
                // Got context from an ancestor. Store a clone to not alter history.
                // Store any identical object references in other registers as the same clone to maintain identity.
                Object myClone = cloner.deepClone(result);
                registerToValue.put(register, myClone);

                // Ancestor may have identical registers. Bring those down too.
                if (currentContext != null) {
                    SparseArray<Object> parentRegisterToValue = currentContext.getRegisterToValue();
                    for (int i = 0; i < parentRegisterToValue.size(); i++) {
                        int parentRegister = parentRegisterToValue.keyAt(i);
                        if ((register == parentRegister) || reassigned.contains(parentRegister)) {
                            continue;
                        }

                        Object value = parentRegisterToValue.get(parentRegister);
                        if (result == value) {
                            registerToValue.put(parentRegister, myClone);
                        }
                    }

                    result = myClone;
                }
            }
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
        getRegistersRead().add(register);

        return peekRegister(register);
    }

    protected String registerToString(int register) {
        StringBuilder result = new StringBuilder();

        Object value = registerToValue.get(register);
        result.append("type=").append(SmaliClassUtils.getValueType(value)).append(", value=").append(value.toString())
                        .append(", hc=").append(value.hashCode());

        return result.toString();
    }

    public void removeRegister(int register) {
        registerToValue.delete(register);
    }

    public boolean hasRegister(int register) {
        return registerToValue.indexOfKey(register) >= 0;
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
                    // Subclasses handle displaying special registers < 0.
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

    public void assignRegisterAndUpdateIdentities(int register, Object value) {
        Object oldValue = peekRegister(register);

        // When replacing an unintialized instance object, need to update all registers that also point to that object.
        // This would be a lot easier if Dalvik's "new-instance" or Java's "new" instruction were available at compile
        // time.
        for (int i = 0; i < registerToValue.size(); i++) {
            int currentRegister = registerToValue.keyAt(i);
            Object currentValue = registerToValue.get(currentRegister);
            if (oldValue == currentValue) {
                assignRegister(currentRegister, value);
            }
        }
    }

    VirtualMachineContext(VirtualMachineContext parent) {
        this(parent.registerCount);
        this.parent = parent;
    }

    public TIntList getRegistersAssigned() {
        return registersAssigned;
    }

    public TIntList getRegistersRead() {
        return registersRead;
    }
}
