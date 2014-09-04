package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.Map;

public class ClassContext extends BaseContext {

    private final Map<String, Integer> fieldToRegister;

    public ClassContext(int fieldCount) {
        super(fieldCount);

        fieldToRegister = new HashMap<String, Integer>(fieldCount);
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

    public Object readField(String fieldNameAndType) {
        // It might be necessary to split up static / instance references later, when class contexts are passed around.
        int register = getRegister(fieldNameAndType);

        return readRegister(register);
    }

    public Object peekField(String fieldNameAndType) {
        int register = getRegister(fieldNameAndType);

        return peekRegister(register);
    }

    public void assignField(String fieldNameAndType, Object value) {
        int register = getRegister(fieldNameAndType);
        assignRegister(register, value);
    }

    public void pokeField(String fieldNameAndType, Object value) {
        int register = getRegister(fieldNameAndType);
        pokeRegister(register, value);
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

    public boolean equals(ClassContext other) {
        return this.toString().equals(other.toString());
    }

}
