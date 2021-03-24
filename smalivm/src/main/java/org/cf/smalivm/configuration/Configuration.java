package org.cf.smalivm.configuration;

import org.cf.smalivm.type.VirtualType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

public class Configuration {

    private static final Logger log = LoggerFactory.getLogger(Configuration.class.getSimpleName());

    public static final String IMMUTABLE_CLASSES_PATH = "immutable_classes.cfg";
    public static final String SAFE_CLASSES_PATH = "safe_classes.cfg";
    public static final String SAFE_METHODS_PATH = "safe_methods.cfg";
    public static final String UNSAFE_METHODS_PATH = "unsafe_methods.cfg";
    public static final String FRAMEWORK_CLASSES_PATH = "framework_classes.cfg";
    public static final String SAFE_FRAMEWORK_CLASSES_PATH = "safe_framework_classes.cfg";

    private static Configuration instance = null;
    private final Set<String> immutableClasses;
    private final Set<String> safeClasses;
    private final Set<String> safeMethods;

    private final Set<String> unsafeMethods;

    private Configuration() {
        safeClasses = new HashSet<>(ConfigurationLoader.load(SAFE_CLASSES_PATH));
        safeMethods = new HashSet<>(ConfigurationLoader.load(SAFE_METHODS_PATH));
        unsafeMethods = new HashSet<>(ConfigurationLoader.load(UNSAFE_METHODS_PATH));
        immutableClasses = new HashSet<>(ConfigurationLoader.load(IMMUTABLE_CLASSES_PATH));
    }

    public static Configuration instance() {
        if (instance == null) {
            instance = new Configuration();
        }

        return instance;
    }

    public Set<String> getImmutableClasses() {
        return immutableClasses;
    }

    public Set<String> getSafeClasses() {
        return safeClasses;
    }

    public boolean isImmutable(String className) {
        return immutableClasses.contains(className);
    }

    public boolean isMutable(String className) {
        return !isImmutable(className);
    }

    /**
     * Safe classes are Java API classes which are safe to instantiate, load, and statically initialize. Safe methods are methods of Java classes
     * which are safe to execute. Only classes and methods which can have no possible side effects outside of SmaliVM should be configured as safe.
     */
    public boolean isSafe(String typeSignature) {
        String[] parts = typeSignature.split("->");
        String className = parts[0];

        if (safeClasses.contains(className) && !isUnsafeMethod(typeSignature)) {
            return true;
        }

        if (parts.length > 1) {
            // It's a method name
            if (safeMethods.contains(typeSignature)) {
                return true;
            }
        }

        return false;
    }

    public boolean isSafe(VirtualType virtualClass) {
        return isSafe(virtualClass.toString());
    }

    public boolean isUnsafeMethod(String methodDescriptor) {
        return unsafeMethods.contains(methodDescriptor);
    }

}
