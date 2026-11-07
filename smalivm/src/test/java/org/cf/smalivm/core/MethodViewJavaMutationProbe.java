package org.cf.smalivm.core;

import java.util.List;

final class MethodViewJavaMutationProbe {
    private MethodViewJavaMutationProbe() {}

    static boolean rejectsParameterMutation(List<ParameterSlot> parameters) {
        try {
            parameters.clear();
            return false;
        } catch (UnsupportedOperationException expected) {
            return true;
        }
    }
}
