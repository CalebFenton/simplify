package simplify;

import java.util.ArrayDeque;
import java.util.Deque;

public class ExecutionContext {
    private boolean hitAmbiguousBranch;

    private final Deque<RegisterContainer> registers;
    private final Deque<Integer> positions;
    private final Deque<String> methods;

    public ExecutionContext(String signature) {
        hitAmbiguousBranch = false;

        registers = new ArrayDeque<RegisterContainer>();
        positions = new ArrayDeque<Integer>();
        methods = new ArrayDeque<String>();

        pushMethod(signature);
        pushPosition(0);
    }

    public int getPosition() {
        return positions.getFirst();
    }

    public void setPosition(int position) {
        positions.pop();
        positions.addFirst(position);
    }

    public void incrementPosition() {
        setPosition(getPosition() + 1);
    }

    public void pushPosition(int position) {
        positions.push(position);
    }

    public void pushMethod(String signature) {
        methods.push(signature);
    }

    public String popMethod() {
        return methods.pop();
    }

    public String getMethod() {
        return methods.getFirst();
    }

    public void setAmbiguousBranch() {
        hitAmbiguousBranch = true;
    }

    public void addRegister(String register, String type, String value) {
        // update referenced
    }

    public void updateRegister(String register, String value) {
        // update referenced
    }

    public String getRegisterValue(String register) {
        // update used
        return null;
    }

}
