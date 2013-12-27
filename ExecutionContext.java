package simplify;

import java.util.ArrayDeque;
import java.util.Deque;

public class ExecutionContext {
    private boolean hitAmbiguousBranch;

    private final Deque<RegisterContainer> registerStack;
    private final Deque<Integer> positionStack;
    private final Deque<String> methodStack;
    private final MethodContainer methods;

    public ExecutionContext(MethodContainer methods, String signature) {
        hitAmbiguousBranch = false;

        registerStack = new ArrayDeque<RegisterContainer>();
        positionStack = new ArrayDeque<Integer>();
        methodStack = new ArrayDeque<String>();

        this.methods = methods;

        enterMethod(signature);
    }

    public Integer getPosition() {
        return positionStack.peekFirst();
    }

    public void setPosition(int position) {
        positionStack.pop();
        positionStack.addFirst(position);
    }

    public void incrementPosition() {
        setPosition(getPosition() + 1);
    }

    public void enterMethod(String signature) {
        methodStack.push(signature);
        positionStack.push(0);
    }

    public void exitMethod() {
        positionStack.pop();
        methodStack.pop();
    }

    public String getMethod() {
        return methodStack.getFirst();
    }

    public int getJumpPosition(String label) {
        return methods.getJumpPosition(getMethod(), label);
    }

    public void setAmbiguousBranch() {
        hitAmbiguousBranch = true;
    }

    public boolean inAmbiguousBranch() {
        return hitAmbiguousBranch;
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
