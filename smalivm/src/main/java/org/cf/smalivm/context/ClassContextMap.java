package org.cf.smalivm.context;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.cf.smalivm.SideEffect;

public class ClassContextMap {

    private final Map<String, ClassContext> classNameToClassContext;
    private final Map<String, SideEffect.Type> classNameToSideEffectType;

    public ClassContextMap() {
        classNameToClassContext = new HashMap<String, ClassContext>();
        classNameToSideEffectType = new HashMap<String, SideEffect.Type>();
    }

    ClassContextMap(ClassContextMap other) {
        this();

        for (String className : other.getClassContextNames()) {
            ClassContext cctx = other.getClassContext(className);
            setClassContext(className, cctx);
        }
    }

    public ClassContext getClassContext(String className) {
        return classNameToClassContext.get(className);
    }

    public void setClassContext(String className, ClassContext cctx) {
        classNameToClassContext.put(className, cctx);
    }

    public Set<String> getClassContextNames() {
        return classNameToClassContext.keySet();
    }

    public boolean isClassInitialized(String className) {
        return classNameToClassContext.containsKey(className);
    }

    public SideEffect.Type getSideEffectType(String className) {
        return classNameToSideEffectType.get(className);
    }

    public void setSideEffectType(String className, SideEffect.Type sideEffectType) {
        classNameToSideEffectType.put(className, sideEffectType);
    }

}
