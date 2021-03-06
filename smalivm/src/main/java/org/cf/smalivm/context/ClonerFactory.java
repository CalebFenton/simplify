package org.cf.smalivm.context;

import com.rits.cloning.Cloner;
import com.rits.cloning.ObjenesisInstantiationStrategy;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.configuration.Configuration;
import org.cf.smalivm.type.ClassManager;
import org.cf.util.ClassNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

class ClonerFactory {

    private static final Logger log = LoggerFactory.getLogger(ClonerFactory.class.getSimpleName());

    private static final Map<ClassManager, Cloner> cache = new WeakHashMap<>();

    /**
     * This builds a fresh cloner. This is necessary because Cloner does some caching of classes which is a problem
     * because classes are dynamically generated. If multiple virtual machines are used, any classes of instances that
     * were cloned in the first virtual machine will be cached. The second virtual machine will have a different class
     * loader and will dynamically generate different classes.
     *
     * The reason there is some ClassManager related caching is to speed up tests, i.e. to prevent having to read
     * configuration, create classes, and create a new cloner for every test.
     */
    static Cloner instance(ClassManager classManager, ClassLoader classLoader, Configuration configuration) {
        Cloner cloner = cache.get(classManager);
        if (cloner != null) {
            return cloner;
        }

        Set<String> immutableClasses = configuration.getImmutableClasses();

        cloner = new Cloner(new ObjenesisInstantiationStrategy());
        for (String immutableClass : immutableClasses) {
            /*
             * Avoid creating a cloner because it's expensive -- it has to build and load lots of classes.
             * To keep things fast and avoid some warnings, don't bother with:
             * - primitives
             * - inner classes
             * - protected packages
             */
            if (immutableClass.length() <= 1 || immutableClass.contains("$") || immutableClass.startsWith("Ljava/")) {
                continue;
            }

            String binaryName = ClassNameUtils.internalToBinary(immutableClass);
            Class<?> klazz;
            try {
                klazz = classLoader.loadClass(binaryName);
            } catch (ClassNotFoundException e) {
                if (log.isErrorEnabled()) {
                    log.error("Unable to load immutable class (not found): {}", immutableClass);
                }
                continue;
            }
            cloner.dontClone(klazz);
        }
        cache.put(classManager, cloner);

        return cloner;
    }

}
