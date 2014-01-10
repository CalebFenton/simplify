package simplify.exec;

import java.util.ArrayList;
import java.util.List;

public class RegisterValue2 {

    private final List<Integer> used;
    private final List<Integer> declared;
    private final String type;
    private final Object value;

    public RegisterValue2(String type, Object value) {
        used = new ArrayList<Integer>();
        declared = new ArrayList<Integer>();

        this.type = type;
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public List<Integer> getUsed() {
        return used;
    }

    public void addUsed(int instructionIndex) {
        used.add(instructionIndex);
    }

    public List<Integer> getDeclared() {
        return declared;
    }

    public void addDeclared(int instructionIndex) {
        declared.add(instructionIndex);
    }

}
