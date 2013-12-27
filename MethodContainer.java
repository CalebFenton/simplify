package simplify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodContainer {

    private final Map<String, Method> methods;
    private final Set<String> modifiedMethods;

    public MethodContainer() {
        modifiedMethods = new HashSet<String>();
        methods = new HashMap<String, Method>();
    }

    public void addMethod(String signature, String parentFile, List<String> lines, Map<String, Integer> jumps) {
        Method m = new Method(parentFile, lines, jumps);
        methods.put(signature, m);
    }

    // returns null if no more instructions
    public String getLine(String signature, int line) {
        return methods.get(signature).getLine(line);
    }

    public int getJumpPosition(String signature, String label) {
        return methods.get(signature).getJumpPosition(label);
    }

    public Set<String> getMethodSignatures() {
        return methods.keySet();
    }

    public void setLine(String signature, int line, String value) {
        methods.get(signature).setLine(line, value);
    }

    public void removeLine(String signature, int line) {
        methods.get(signature).removeLine(line);
    }

    public String getParentFile(String signature) {
        return methods.get(signature).getParentFile();
    }

    public Set<String> getModifiedMethods() {
        return modifiedMethods;
    }

}
