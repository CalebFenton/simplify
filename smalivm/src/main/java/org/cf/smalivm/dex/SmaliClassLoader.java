package org.cf.smalivm.dex;

import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.ClassManagerFactory;
import org.cf.smalivm.type.VirtualClass;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.ClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

public class SmaliClassLoader extends URLClassLoader {

    private static final Logger log = LoggerFactory.getLogger(SmaliClassLoader.class.getSimpleName());

    // This jar is created by FrameworkJarBuilder. It's a cache of framework classes to avoid
    // having to dynamically generate them.
    public static final String FRAMEWORK_STUBS_JAR = "/framework/android-framework.jar";

    private final Map<String, Class<?>> cachedClasses;
    private final ClassBuilder classBuilder;
    private final ClassManager classManager;

    public SmaliClassLoader(ClassManager classManager) {
        super(new URL[] { SmaliClassLoader.class.getResource(SmaliClassLoader.FRAMEWORK_STUBS_JAR) });
        cachedClasses = new HashMap<>();
        this.classBuilder = new ClassBuilder(classManager);
        this.classManager = classManager;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String smaliPath = args[0];
        String className = args[1];
        ClassManager classManager = new ClassManagerFactory().build(smaliPath);
        VirtualClass virtualClass = classManager.getVirtualClass(className);
        SmaliClassLoader classLoader = new SmaliClassLoader(classManager);
        Class<?> klazz = classLoader.loadClass(virtualClass.getSourceName());
    }

    private static String getPackageName(String className) {
        int i = className.lastIndexOf('.');
        if (i > 0) {
            return className.substring(0, i);
        } else {
            // No package name, e.g. LsomeClass;
            return null;
        }
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        String internalName = ClassNameUtils.binaryToInternal(name);
        /*
         * Framework classes should be loaded from smalivm's generated framework jar.
         * This is because the ObjectInstantiator will expect an empty default constructor
         * and one is added whenever classes are built.
         */
        if (classManager.getFrameworkClassNames().contains(internalName)) {
            Class<?> klazz = cachedClasses.get(name);
            if (klazz != null) {
                return klazz;
            }

            klazz = findClass(name);
            cachedClasses.put(name, klazz);
            return klazz;
        }
        return super.loadClass(name, resolve);
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        if (name.startsWith("java.")) {
            try {
                return super.loadClass(name, false);
            } catch (ClassNotFoundException e) {
            }

            log.warn("Unable to build and load prohibited class name: {}\nThis error is likely the result of using a class " +
                    "which references a java.* class only available on Android. There's no work-around at this time " +
                    "since loading protected classes is a huge pain.", name);
            throw new ClassNotFoundException(name);
        }

        Class<?> klazz = cachedClasses.get(name);
        if (klazz != null) {
            return klazz;
        }

        try {
            klazz =  super.findClass(name);
            cachedClasses.put(name, klazz);
            return klazz;
        } catch (ClassNotFoundException e) {
        }

        String internalName = ClassNameUtils.binaryToInternal(name);
        if (!classManager.getClassNames().contains(internalName)) {
            throw new ClassNotFoundException(name);
        }

        VirtualClass virtualClass = classManager.getVirtualClass(internalName);
        ClassDef classDef = virtualClass.getClassDef();

        byte[] b = classBuilder.build(classDef);
        klazz = defineClass(name, b, 0, b.length);
        cachedClasses.put(name, klazz);

        // No one ever tells you this is also necessary or you'll have a null package for the class.
        String packageName = getPackageName(name);
        if (packageName != null && getPackage(packageName) == null) {
            definePackage(getPackageName(name), null, null, null, null, null, null, null);
        }

        return klazz;
    }

}
