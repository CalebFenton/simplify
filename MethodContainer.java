package simplify;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MethodContainer {

    private final Map<String, Method> methods;

    public MethodContainer() {
        methods = new HashMap<String, Method>();
    }

    public void addMethod(String signature, String parentFile, List<String> lines, Map<String, Integer> jumps) {
        Method m = new Method(parentFile, signature, lines, jumps);
        methods.put(signature, m);
    }

    public Set<String> getMethodSignatures() {
        return methods.keySet();
    }

    public Method getMethod(String signature) {
        return methods.get(signature);
    }

    public Set<String> getModifiedMethods() {
        Set<String> result = new HashSet<String>();

        for (String signature : methods.keySet()) {
            if (methods.get(signature).isModified()) {
                result.add(signature);
            }
        }

        return result;
    }

}
