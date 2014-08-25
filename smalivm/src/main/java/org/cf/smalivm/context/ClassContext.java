package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.Map;

public class ClassContext extends BaseContext {

    private final Map<String, Integer> fieldToRegister;

    public ClassContext(int fieldCount) {
        super(fieldCount);

        fieldToRegister = new HashMap<String, Integer>(fieldCount);
    }

    private int getRegister(String fieldReference) {
        int register = 0;
        if (!fieldToRegister.containsKey(fieldReference)) {
            register = fieldToRegister.size();
            fieldToRegister.put(fieldReference, register);
        } else {
            register = fieldToRegister.get(fieldReference);
        }

        return register;
    }

    public Object readField(String fieldReference) {
        // It might be necessary to split up static / instance references later, when class contexts are passed around.
        int register = getRegister(fieldReference);

        return readRegister(register);
    }

    public Object peekField(String fieldReference) {
        int register = getRegister(fieldReference);

        return peekRegister(register);
    }

    public void assignField(String fieldReference, Object value) {
        int register = getRegister(fieldReference);
        assignRegister(register, value);
    }

    public void pokeField(String fieldReference, Object value) {
        int register = getRegister(fieldReference);
        pokeRegister(register, value);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Fields:\n");
        for (String fieldReference : fieldToRegister.keySet()) {
            sb.append(fieldReference).append(" = ").append(peekField(fieldReference)).append("\n");
        }
        sb.setLength(sb.length() - 1);

        return sb.toString();
    }

    public boolean equals(ClassContext other) {
        return this.toString().equals(other.toString());
    }

}
