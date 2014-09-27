package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.Map;

public class ClassState extends BaseState {

    private final String className;
    private final Map<String, Integer> fieldToRegister;

    public ClassState(ExecutionContext ectx, String className, int fieldCount) {
        super(ectx, className, fieldCount);

        fieldToRegister = new HashMap<String, Integer>(fieldCount);
        this.className = className;
    }

    ClassState(ClassState parent, ExecutionContext ectx) {
        super(parent, ectx);

        fieldToRegister = new HashMap<String, Integer>(parent.fieldToRegister);
        className = parent.className;
    }

    public void assignField(String fieldNameAndType, Object value) {
        int register = getRegister(fieldNameAndType);
        assignRegister(register, value);
    }

    public boolean equals(ClassState other) {
        return this.toString().equals(other.toString());
    }

    public Object peekField(String fieldNameAndType) {
        int register = getRegister(fieldNameAndType);

        return peekRegister(register);
    }

    public void pokeField(String fieldNameAndType, Object value) {
        int register = getRegister(fieldNameAndType);
        pokeRegister(register, value);
    }

    public Object readField(String fieldNameAndType) {
        // It might be necessary to split up static / instance references later, when class contexts are passed around.
        int register = getRegister(fieldNameAndType);

        return readRegister(register);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fields:\n");
        for (String fieldNameAndType : fieldToRegister.keySet()) {
            sb.append(fieldNameAndType).append(" = ").append(peekField(fieldNameAndType)).append("\n");
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    private int getRegister(String fieldNameAndType) {
        int register = 0;
        if (!fieldToRegister.containsKey(fieldNameAndType)) {
            register = fieldToRegister.size();
            fieldToRegister.put(fieldNameAndType, register);
        } else {
            register = fieldToRegister.get(fieldNameAndType);
        }

        return register;
    }

    ClassState getChild(ExecutionContext childContext) {
        ClassState child = new ClassState(childContext, className, fieldToRegister.size());

        return child;
    }

}
