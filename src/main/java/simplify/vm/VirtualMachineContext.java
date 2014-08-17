package simplify.vm;

import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

import java.util.logging.Logger;

import org.apache.commons.lang3.ClassUtils;

import simplify.Main;
import simplify.SmaliClassUtils;

import com.rits.cloning.Cloner;

public class VirtualMachineContext {

    private static final Cloner cloner = new Cloner();

    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    private VirtualMachineContext parent;
    private final int registerCount;
    private final TIntList registersAssigned;
    private final TIntList registersRead;
    private final TIntObjectMap<Object> registerToValue;

    VirtualMachineContext() {
        this(0);
    }

    VirtualMachineContext(int registerCount) {
        // The number of instances of contexts in memory could be very high. Allocate minimally.
        registersAssigned = new TIntArrayList(0);
        registersRead = new TIntArrayList(0);
        registerToValue = new TIntObjectHashMap<Object>();

        // This is locals + parameters
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
        for (int currentRegister : registerToValue.keys()) {
            Object currentValue = registerToValue.get(currentRegister);
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

    public TIntObjectMap<Object> getRegisterToValue() {
        return registerToValue;

        // TODO: verify registerToValue no longer needs to skip longs. this should be done when context is created. or
        // even overloaded by methodcontext. vm context shouldn't need to know about smali.
        // Utility method that knows how to skip longs
        // Longs are fatties and take up two registers
        // if (SmaliClassUtils.getValueType(value).equals("J")) {
        // register++;
        // }
    }

    public boolean hasRegister(int register) {
        return registerToValue.containsKey(register);
    }

    VirtualMachineContext getAncestorWithRegister(int register) {
        VirtualMachineContext result = this;
        do {
            if (result.hasRegister(register)) {
                return result;
            }

            // Princess is in another castle!
            result = result.getParent();
        } while (result != null);

        return result;
    }

    static TIntSet getReassignedRegistersBetweenChildAndAncestorContext(VirtualMachineContext child,
                    VirtualMachineContext ancestor) {
        VirtualMachineContext current = child;
        TIntSet result = new TIntHashSet();
        // Up to, but not including ancestor
        while ((current.getParent() != ancestor) && (current != ancestor)) {
            result.addAll(current.getRegisterToValue().keys());
            current = current.getParent();
        }

        return result;
    }

    protected Object[] peekWithTargetContext(int register) {
        /*
         * Since executing a method may create many context clones, all clones start off empty of register values. They
         * are "pulled down" from ancestors when accessed, along with any other registers with identical values in the
         * ancestor, excluding any registers that have been overwritten in between.
         */
        VirtualMachineContext targetContext = getAncestorWithRegister(register);
        if (targetContext == null) {
            log.warning("r" + register + " is being read but is null, likely a mistake!");
            Thread.dumpStack();
            return new Object[] { null, null };
        }

        if (targetContext == this) {
            return new Object[] { getRegisterToValue().get(register), targetContext };
        }

        /*
         * Got context from an ancestor. Clone the value so changes don't alter history. Also, pull down any identical
         * object references in the target context, so both registers will point to the new clone. E.g. same object
         * reference is in v0 and v1, and v1 is peeked, so also pull down v0. and we peek v1, also pull down v0
         */
        TIntObjectMap<Object> targetRegisterToValue = targetContext.getRegisterToValue();
        Object targetValue = targetRegisterToValue.get(register);
        TIntSet reassigned = getReassignedRegistersBetweenChildAndAncestorContext(this, targetContext);
        Object cloneValue = cloneRegisterValue(targetValue);
        for (int targetRegister : targetRegisterToValue.keys()) {
            if (!reassigned.contains(targetRegister)) {
                Object currentValue = targetRegisterToValue.get(targetRegister);
                if (targetValue == currentValue) {
                    pokeRegister(targetRegister, cloneValue);
                }
            }

        }

        return new Object[] { cloneValue, targetContext };
    }

    public Object peekRegister(int register) {
        return peekWithTargetContext(register)[0];
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

        log.fine("Setting r" + register + " -> " + registerValueToString(value) + sb.toString());
        registerToValue.put(register, value);
    }

    public Object readRegister(int register) {
        getRegistersRead().add(register);

        return peekRegister(register);
    }

    public void removeRegister(int register) {
        registerToValue.remove(register);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getRegisterCount() > 0) {
            sb.append("registers: ").append(getRegisterCount()).append("\n");
            sb.append("[");
            for (int register : registerToValue.keys()) {
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

    protected String registerValueToString(Object value) {
        StringBuilder result = new StringBuilder();
        result.append("type=").append(SmaliClassUtils.getValueType(value)).append(", value=").append(value.toString())
        .append(", hc=").append(value.hashCode());

        return result.toString();
    }

    protected String registerToString(int register) {
        Object value = peekRegister(register);

        return registerValueToString(value);
    }
}
