package simplify.vm;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MethodSideEfectAnalyzer {

    public static Map<String, Boolean> analyze(VirtualMachine vm) {
        Set<String> methodDescriptors = vm.getMethodDescriptors();
        Map<String, Boolean> result = new HashMap<String, Boolean>(methodDescriptors.size());
        for (String methodDescriptor : methodDescriptors) {
            boolean hasSideEffects = analyzeMethod(vm, methodDescriptor);
            result.put(methodDescriptor, hasSideEffects);
        }

        return result;
    }

    public static boolean analyzeMethod(VirtualMachine vm, String methodDescriptor) {
        ContextGraph graph = vm.execute(methodDescriptor);
        if (graph == null) {
            return true;
        }

        for (ContextNode node : graph) {
            if (node.getHandler().hasSideEffects()) {
                return true;
            }
        }

        return false;
    }
}
