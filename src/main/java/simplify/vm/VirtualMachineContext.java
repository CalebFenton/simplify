package simplify.vm;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntIntMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.logging.Logger;

import org.apache.commons.lang3.ClassUtils;

import simplify.Main;
import simplify.SmaliClassUtils;
import util.SparseArray;

import com.rits.cloning.Cloner;

public class VirtualMachineContext {

    private static final Cloner cloner = new Cloner();

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private VirtualMachineContext parent;
    private final int registerCount;
    private final TIntList registersAssigned;
    private final TIntList registersRead;
    private final SparseArray<Object> valuePool;
    private final TIntIntMap registerToValuePoolIndex;

    // private final SparseArray<Object> registerToValue;

    VirtualMachineContext() {
        this(0);
    }

    VirtualMachineContext(int registerCount) {
        // The number of instances of contexts in memory could be very high. Allocate minimally.
        registersAssigned = new TIntArrayList(0);
        registersRead = new TIntArrayList(0);
        // registerToValue = new SparseArray<Object>(0);
        valuePool = new SparseArray<Object>(0);
        registerToValuePoolIndex = new TIntIntHashMap(valuePool.size());

        this.registerCount = registerCount;
    }

    VirtualMachineContext(VirtualMachineContext parent) {
        this(parent.registerCount);
        this.parent = parent;
    }

    public void assignRegister(int register, Object value) {
        getRegistersAssigned().add(register);

        pokeRegister(register, value);
    }

    public void assignRegisterAndUpdateIdentities(int register, Object value) {
        Object oldValue = peekRegister(register);

        // When replacing an uninitialized instance object, need to update all registers that also point to that object.
        // This would be a lot easier if Dalvik's "new-instance" or Java's "new" instruction were available at compile
        // time.
        for (int currentRegister : registerToValuePoolIndex.keys()) {
            int poolIndex = registerToValuePoolIndex.get(currentRegister);
            Object currentValue = valuePool.get(poolIndex);
            if (oldValue == currentValue) {
                assignRegister(currentRegister, value);
            }
        }
    }

    public VirtualMachineContext getParent() {
        return parent;
    }

    public int getRegisterCount() {
        return registerCount;
    }

    public TIntList getRegistersAssigned() {
        return registersAssigned;
    }

    public TIntList getRegistersRead() {
        return registersRead;
    }

    public SparseArray<Object> getRegisterToValue() {
        // Utility method that knows how to skip longs
        SparseArray<Object> result = new SparseArray<Object>(registerToValuePoolIndex.size());
        for (int register : registerToValuePoolIndex.keys()) {
            int poolIndex = registerToValuePoolIndex.get(register);
            Object value = valuePool.get(poolIndex);
            result.put(register, value);

            // Longs are fatties and take up two registers
            if (SmaliClassUtils.getValueType(value).equals("J")) {
                register++;
            }
        }

        return result;
    }

    public boolean hasRegister(int register) {
        return registerToValuePoolIndex.containsKey(register);
    }

    public Object peekRegister(int register) {
        Object result = null;
        VirtualMachineContext currentContext = this;
        TIntSet reassigned = new TIntHashSet();
        while (currentContext != null) {
            // First look for value in this context. If not found, look through ancestors.
            int valuePoolIndex = registerToValuePoolIndex.get(register);
            result = currentContext.valuePool.get(valuePoolIndex);
            if (result != null) {
                break;
            }

            // Keep track of any registers assigned from starting context to context with target register. When bringing
            // down identical registers, exclude those that have been re-assigned.
            for (int key : currentContext.registerToValuePoolIndex.keys()) {
                if (key != register) {
                    reassigned.add(key);
                }
            }

            currentContext = currentContext.parent;
        }

        if (result == null) {
            log.warning("r" + register + " is being read but is null, likely a mistake!");
            Thread.dumpStack();
        } else {
            if (currentContext != this) {
                // Got context from an ancestor. Store a clone to not alter history.
                // Store any identical object references in other registers as the same clone to maintain identity.
                Object valueClone = cloneRegisterValue(result);
                pokeRegister(register, valueClone);

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
                            pokeRegister(parentRegister, valueClone);
                        }
                    }

                    result = valueClone;
                }
            }
        }

        return result;
    }

    Object cloneRegisterValue(Object value) {
        Object result = cloner.deepClone(value);

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

        int valuePoolIndex = valuePool.indexOfValue(value);
        if (valuePoolIndex == -1) {
            valuePoolIndex = valuePool.size();
        }
        valuePool.put(valuePoolIndex, value);
        registerToValuePoolIndex.put(register, valuePoolIndex);

        log.fine("Setting r" + register + " -> " + registerToString(register) + sb.toString());
    }

    public Object readRegister(int register) {
        getRegistersRead().add(register);

        return peekRegister(register);
    }

    public void removeRegister(int register) {
        registerToValuePoolIndex.remove(register);
        // keep object reference in pool
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (registerCount > 0) {
            sb.append("registers: ").append(registerCount).append("\n");
            sb.append("[");
            for (int register : registerToValuePoolIndex.keys()) {
                if (register < 0) {
                    // Subclasses handle displaying special registers < 0.
                    continue;
                }

                sb.append("r").append(register).append(": ").append(registerToString(register)).append(",\n");
            }
            if (registerToValuePoolIndex.size() > 0) {
                sb.setLength(sb.length() - 2);
            }
            sb.append("]");
        }

        return sb.toString();
    }

    public boolean wasRegisterAssigned(int register) {
        return getRegistersAssigned().contains(register);
    }

    public boolean wasRegisterRead(int register) {
        Object value = peekRegister(register);

        if (ClassUtils.isPrimitiveOrWrapper(value.getClass()) || (value.getClass() == String.class)) {
            /*
             * This is a hack which suggests maintaining register types. Primitives are stored internally as their
             * wrappers. They'll equals() even if they're different object instances and the check in the else below
             * will cause any register containing a value which might also be contained in a primitive wrapper to appear
             * to be referencing the same object, which throws off the optimizer.
             */
            return registersRead.contains(register);
        } else {
            /*
             * It's not enough to examine registersRead for object references. v0 and v1 may contain the same object,
             * and v0 is never read.
             */
            TIntList registers = getRegistersRead();
            for (int i = 0; i < registers.size(); i++) {
                int currentRegister = registers.get(i);
                Object currentValue = peekRegister(currentRegister);

                if (value == currentValue) {
                    return true;
                }
            }
        }

        return false;
    }

    protected String registerToString(int register) {
        StringBuilder result = new StringBuilder();
        Object value = peekRegister(register);
        result.append("type=").append(SmaliClassUtils.getValueType(value)).append(", value=").append(value.toString())
        .append(", hc=").append(value.hashCode());

        return result.toString();
    }
}
