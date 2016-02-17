package org.cf.smalivm.smali;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.ClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AncestorEnumerator {

    private static final Logger log = LoggerFactory.getLogger(AncestorEnumerator.class.getSimpleName());

    private final ClassManager classManager;
    private final ClassLoader classLoader;
    private final Map<String, Set<String>> classNameToAncestorNames;

    public AncestorEnumerator(ClassManager classManager, ClassLoader classLoader) {
        this.classManager = classManager;
        this.classLoader = classLoader;
        classNameToAncestorNames = new HashMap<String, Set<String>>();
    }

    public Set<String> enumerate(String className) {
        Set<String> ancestorNames = enumerate(className, new HashSet<String>());
        classNameToAncestorNames.put(className, ancestorNames);

        return ancestorNames;
    }

    private Set<String> enumerate(String className, Set<String> ancestorNames) {
        if (ancestorNames.contains(className)) {
            return new HashSet<String>();
        }

        if (classManager.isLocalClass(className)) {
            // Prefer class manager to avoid having to load the class which fails for java.lang.* classes which are
            // unique to Android, among other complexities.
            ClassDef klazz = classManager.getClass(className);
            for (String interfaceName : klazz.getInterfaces()) {
                enumerate(interfaceName, ancestorNames);
                ancestorNames.add(interfaceName);
            }
            String superName = klazz.getSuperclass();
            if (superName != null) {
                enumerate(superName, ancestorNames);
                ancestorNames.add(superName);
            }
        } else {
            String binaryName = ClassNameUtils.internalToBinary(className);
            Class<?> klazz;
            try {
                klazz = classLoader.loadClass(binaryName);
            } catch (ClassNotFoundException e) {
                log.error("Error loading class " + className, e);
                return ancestorNames;
            }
            for (Class<?> interfaceClass : klazz.getInterfaces()) {
                String internalName = ClassNameUtils.toInternal(interfaceClass);
                enumerate(internalName, ancestorNames);
                ancestorNames.add(internalName);
            }
            if (klazz.getSuperclass() != null) {
                String internalName = ClassNameUtils.toInternal(klazz.getSuperclass());
                enumerate(internalName, ancestorNames);
                ancestorNames.add(internalName);
            }
        }

        return ancestorNames;
    }

}
