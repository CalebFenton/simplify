package org.cf.smalivm.smali;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliClassLoader extends ClassLoader {

    private static final Logger log = LoggerFactory.getLogger(SmaliClassLoader.class.getSimpleName());

    private static final String FRAMEWORK_STUBS_JAR = "/framework/framework-17.jar";

    private Map<String, Class<?>> cachedClasses;
    private final ClassBuilder classBuilder;
    private final ClassManager classManager;
    private URLClassLoader jarLoader;

    public SmaliClassLoader(ClassManager classManager) {
        super(SmaliClassLoader.class.getClassLoader());
        cachedClasses = new HashMap<String, Class<?>>();
        URL jarURL = SmaliClassLoader.class.getResource(FRAMEWORK_STUBS_JAR);
        jarLoader = new URLClassLoader(new URL[] { jarURL });
        this.classBuilder = new ClassBuilder(classManager, jarLoader);
        this.classManager = classManager;
    }

    private @Nullable Class<?> loadClassWithoutBuilding(String name) {
        Class<?> klazz = null;
        klazz = findLoadedClass(name);
        if (klazz != null) {
            return klazz;
        }

        try {
            klazz = super.loadClass(name);
            if (klazz != null) {
                return klazz;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        klazz = findClass(name);
        if (klazz != null) {
            return klazz;
        }

        return null;
    }

    @Override
    protected Class<?> findClass(String name) {
        try {
            return jarLoader.loadClass(name);
        } catch (ClassNotFoundException e) {
        }

        if (classBuilder.isShallowClass(name)) {
            return null;
        }

        return cachedClasses.get(name);
    }

    @Override
    public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
        Class<?> klazz = loadClassWithoutBuilding(name);
        if (klazz != null) {
            return klazz;
        }

        if (cachedClasses.isEmpty()) {
            Set<String> classNames = classManager.getNonFrameworkClassNames();
            // If I don't set this to null first, cachedClasses does not update.
            // VERY VERY STRANGE
            cachedClasses = null;
            cachedClasses = classBuilder.build(classNames);
        }

        klazz = findClass(name);
        if (klazz == null) {
            throw new ClassNotFoundException(name);
        }

        return klazz;
    }

}
