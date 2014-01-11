package simplify.exec;

import java.util.List;
import java.util.logging.Logger;

import simplify.Simplifier;

import com.google.common.collect.LinkedListMultimap;

public class ExecutionContext {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private final LinkedListMultimap<Integer, RegisterStore> registers;
    private RegisterStore returnRegister;

    public ExecutionContext() {
        registers = LinkedListMultimap.create();
    }

    public void addRegister(int register, String type, Object value, int index) {
        RegisterStore current = new RegisterStore(type, value);
        current.referenced.add(index);
        registers.put(register, current);
    }

    protected void addRegister(int register, RegisterStore value) {
        registers.put(register, value);
    }

    public void updateOrAddRegister(int register, String type, Object value, int index) {
        // "update" means type is unchanged
        RegisterStore current = getRegister(register, index);
        if (current == null) {
            addRegister(register, type, value, index);
            current = getRegister(register, index);
        }
        current.value = value;
    }

    public Object getRegisterValue(int register, int index) {
        RegisterStore current = getRegister(register, index);
        current.used.add(index);
        return current.value;
    }

    public Object getRegisterValue(int register) {
        // Only used with simplifier
        List<RegisterStore> historical = registers.get(register);
        RegisterStore current = historical.get(historical.size() - 1);

        return current.value;
    }

    public String getRegisterType(int register) {
        List<RegisterStore> historical = registers.get(register);
        RegisterStore current = historical.get(historical.size() - 1);

        return current.type;
    }

    protected RegisterStore getRegister(int register, int index) {
        List<RegisterStore> historical = registers.get(register);

        if (historical.size() == 0) {
            return null;
        }

        RegisterStore current = historical.get(historical.size() - 1);
        current.referenced.add(index);

        return current;
    }

    protected void setReturnRegister(int register, int index) {
        returnRegister = getRegister(register, index);
        returnRegister.used.add(index);
    }

    protected RegisterStore getReturnRegister(int index) {
        return returnRegister;
    }

    @Override
    public ExecutionContext clone() {
        ExecutionContext myClone = new ExecutionContext();
        for (Integer register : registers.keySet()) {
            for (RegisterStore value : registers.get(register)) {
                myClone.addRegister(register, value.clone());
            }
        }

        return myClone;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Integer key : registers.keySet()) {
            for (RegisterStore register : registers.get(key)) {
                sb.append("r").append(key).append(": ").append(register).append("\n");
            }
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }
}
