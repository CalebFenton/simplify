package org.cf.smalivm.smali;

import java.util.HashSet;
import java.util.Set;

import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;

public class ClassDependencyCollector {

    public static Set<String> collect(ClassDef classDef) {
        Set<String> dependencies = new HashSet<String>();

        dependencies.add(classDef.getType());
        dependencies.add(classDef.getSuperclass());
        dependencies.addAll(classDef.getInterfaces());
        dependencies.addAll(collectMethodDependencies(classDef.getMethods()));
        dependencies.addAll(collectFieldDependencies(classDef.getFields()));

        return dependencies;
    }

    private static Set<String> collectMethodDependencies(Iterable<? extends Method> methods) {
        Set<String> dependencies = new HashSet<String>();
        for (Method method : methods) {
            for (CharSequence parameterType : method.getParameterTypes()) {
                dependencies.add(parameterType.toString());
            }
        }

        return dependencies;
    }

    private static Set<String> collectFieldDependencies(Iterable<? extends Field> fields) {
        Set<String> dependencies = new HashSet<String>();
        for (Field field : fields) {
            dependencies.add(field.getType());
        }

        return dependencies;
    }

}
