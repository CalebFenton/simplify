package simplify;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.logging.Logger;

public class ExecutionContext {

    private static final Logger log = Logger.getLogger(Simplifier.class.getSimpleName());

    private boolean hitAmbiguousBranch;

    private int currentLocalJumps;

    private final Deque<RegisterContainer> registerStack;
    private final Deque<Integer> positionStack;
    private final Deque<String> methodStack;
    private final MethodContainer methods;

    public ExecutionContext(MethodContainer methods, String signature) {
        hitAmbiguousBranch = false;

        currentLocalJumps = 0;

        registerStack = new ArrayDeque<RegisterContainer>();
        positionStack = new ArrayDeque<Integer>();
        methodStack = new ArrayDeque<String>();

        this.methods = methods;

        enterMethod(signature);
    }

    public Integer getPosition() {
        return positionStack.peek();
    }

    public int getLocalJumps() {
        return currentLocalJumps;
    }

    public int getCallDepth() {
        return methodStack.size();
    }

    public void setPosition(String label) {
        currentLocalJumps++;
        setPosition(getJumpPosition(label));
    }

    public void incrementPosition() {
        setPosition(getPosition() + 1);
    }

    public void enterMethod(String signature) {
        methodStack.push(signature);
        positionStack.push(0);
        registerStack.push(new RegisterContainer());
    }

    public void exitMethod() {
        log.info("Exiting method " + methodStack.peek() + " with context:\n" + this.toString());

        methodStack.pop();
        positionStack.pop();
        registerStack.pop();
    }

    public String getMethodSignature() {
        return methodStack.getFirst();
    }

    public String getStackTrace() {
        StringBuilder sb = new StringBuilder();

        String[] m = methodStack.toArray(new String[methodStack.size()]);
        Integer[] p = positionStack.toArray(new Integer[positionStack.size()]);
        for (int i = 0; i < m.length; i++) {
            String signature = m[i];
            Method method = methods.getMethod(signature);
            sb.append(signature).append('[').append(method.getClassFile()).append(':').append(p[i]).append("]\n");
        }

        return sb.deleteCharAt(sb.length() - 1).toString();
    }

    public void setAmbiguousBranch() {
        hitAmbiguousBranch = true;
    }

    public boolean inAmbiguousBranch() {
        return hitAmbiguousBranch;
    }

    public void addRegister(String name, Object value, String type) {
        RegisterContainer registers = registerStack.peek();
        registers.add(name, value, type, getPosition());
    }

    public void updateRegister(String name, Object value, String type) {
        RegisterContainer registers = registerStack.peek();
        registers.update(name, value, type, getPosition());
    }

    public Object getRegister(String name) {
        RegisterContainer registers = registerStack.peek();
        return registers.get(name, getPosition());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("hitAmbiguousBranch: ").append(hitAmbiguousBranch).append(", ");
        sb.append("currentLocalJumps: ").append(currentLocalJumps).append("\n");

        sb.append("registers:\n").append(registerStack.toString()).append("\n");

        sb.append("methods:\n[");
        String[] methodParts = methodStack.toArray(new String[methodStack.size()]);
        Integer[] positionParts = positionStack.toArray(new Integer[positionStack.size()]);
        for (int i = 0; i < methodParts.length; i++) {
            sb.append(methodParts[i]).append(':').append(positionParts[i]).append(',');
        }
        sb.deleteCharAt(sb.length() - 1).append(']');

        return sb.toString();
    }

    private void setPosition(int position) {
        positionStack.pop();
        positionStack.addFirst(position);
    }

    private int getJumpPosition(String label) {
        return methods.getMethod(getMethodSignature()).getJumpPosition(label);
    }
}
