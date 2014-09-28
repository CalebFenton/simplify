package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.Map;

public class ClassState extends BaseState {

    private final String className;
    private final Map<String, Integer> fieldToRegister;

    public ClassState(ExecutionContext ectx, String className, int fieldCount) {
        super(ectx, fieldCount);

        fieldToRegister = new HashMap<String, Integer>(fieldCount);
        this.className = className;
    }

    ClassState(ClassState parent, ExecutionContext ectx) {
        super(parent, ectx);

        fieldToRegister = new HashMap<String, Integer>(parent.fieldToRegister);
        className = parent.className;
    }

    public void assignField(String fieldNameAndType, Object value) {
        int register = 0;
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(fieldNameAndType);
        assignRegister(register, value, sb.toString());
    }

    public boolean equals(ClassState other) {
        return this.toString().equals(other.toString());
    }

    public Object peekField(String fieldNameAndType) {
        // int register = getRegister(fieldNameAndType);
        int register = 0;
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(fieldNameAndType);
        return peekRegister(register, sb.toString());
    }

    public void pokeField(String fieldNameAndType, Object value) {
        int register = 0;
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(fieldNameAndType);
        pokeRegister(register, value, sb.toString());
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

    private int getRegister_no(String fieldNameAndType) {
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
