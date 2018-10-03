package org.cf.smalivm.type;

import org.cf.smalivm.dex.SmaliFile;
import org.cf.smalivm.dex.SmaliFileFactory;
import org.cf.smalivm.dex.SmaliParser;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.Opcodes;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The class manager is responsible for loading Smali files into Dexlib2 objects and making them available.
 */
public class ClassManager {

    private static final Logger log = LoggerFactory.getLogger(ClassManager.class.getSimpleName());

    private final HashMap<String, VirtualClass> classNameToClass;
    private final Map<String, SmaliFile> classNameToSmaliFile;

    private final SmaliFileFactory smaliFileFactory;
    private final DexBuilder dexBuilder;
    // Use separate DexBuilder to intern framework classes to avoid including in output dex
    private final DexBuilder frameworkDexBuilder = new DexBuilder(Opcodes.getDefault());

    private Set<String> frameworkClassNames = null;
    private Set<String> nonFrameworkClassNames = null;

    ClassManager(DexBuilder dexBuilder) {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles());
    }

    private ClassManager(DexBuilder dexBuilder, boolean internalOnly) {
        this.dexBuilder = dexBuilder;
        smaliFileFactory = new SmaliFileFactory();
        classNameToClass = new HashMap<>();
        classNameToSmaliFile = new HashMap<>();
        VirtualType.setClassManager(this);
    }

    ClassManager(DexBuilder dexBuilder, File smaliPath) throws IOException {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles(smaliPath));
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names, including framework
     */
    public Set<String> getClassNames() {
        return classNameToSmaliFile.keySet();
    }

    /**
     * @return DexBuilder
     */
    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    /**
     * Does not load any Smali files.
     *
     * @return all framework class names
     */
    public Set<String> getFrameworkClassNames() {
        if (frameworkClassNames == null) {
            frameworkClassNames = classNameToSmaliFile.keySet().stream()
                    .filter(smaliFileFactory::isFrameworkClass)
                    .collect(Collectors.toSet());
        }

        return frameworkClassNames;
    }

    public Collection<VirtualClass> getLoadedClasses() {
        return classNameToClass.values();
    }

    public VirtualMethod getMethod(String methodSignature) {
        String[] parts = methodSignature.split("->");
        String className = parts[0];
        String methodDescriptor = parts[1];

        return getMethod(className, methodDescriptor);
    }

    public VirtualMethod getMethod(String className, String methodDescriptor) {
        VirtualType virtualClass = getVirtualClass(className);

        return virtualClass.getMethod(methodDescriptor);
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names which are not part of the framework
     */
    public Set<String> getNonFrameworkClassNames() {
        if (nonFrameworkClassNames == null) {
            nonFrameworkClassNames = classNameToSmaliFile.keySet().stream()
                    .filter(className -> !smaliFileFactory.isFrameworkClass(className))
                    .collect(Collectors.toSet());
        }

        return nonFrameworkClassNames;
    }

    public VirtualClass getVirtualClass(Class<?> klazz) {
        return getVirtualClass(ClassNameUtils.toInternal(klazz));
    }

    public VirtualClass getVirtualClass(String className) {
        return (VirtualClass) getVirtualType(className);
    }

    public VirtualType getVirtualType(TypeReference typeReference) {
        char first = typeReference.charAt(0);
        if (first == 'L') {
            String className = typeReference.getType();
            parseClassIfNecessary(className);

            return classNameToClass.get(className);
        } else if (first == '[') {
            return new VirtualArray(typeReference);
        } else if (ClassNameUtils.isPrimitive(typeReference.getType())) {
            return new VirtualPrimitive(typeReference);
        } else {
            throw new RuntimeException("Unrecognized type: " + typeReference);
        }
    }

    public VirtualType getVirtualType(Class<?> typeClass) {
        return getVirtualType(ClassNameUtils.toInternal(typeClass));
    }

    public VirtualType getVirtualType(String typeSignature) {
        TypeReference typeReference = getFrameworkDexBuilder().internTypeReference(typeSignature);

        return getVirtualType(typeReference);
    }

    public boolean isFrameworkClass(VirtualType virtualClass) {
        return smaliFileFactory.isFrameworkClass(virtualClass.getName());
    }

    public boolean isSafeFrameworkClass(VirtualType virtualClass) {
        return smaliFileFactory.isSafeFrameworkClass(virtualClass.getName());
    }

    DexBuilder getFrameworkDexBuilder() {
        return frameworkDexBuilder;
    }

    private void cacheSmaliFiles(Set<SmaliFile> smaliFiles) {
        for (SmaliFile smaliFile : smaliFiles) {
            classNameToSmaliFile.put(smaliFile.getClassName(), smaliFile);
        }
    }

    private BuilderClassDef parseClass(String className, DexBuilder builder) {
        SmaliFile smaliFile = classNameToSmaliFile.get(className);
        BuilderClassDef classDef;
        try {
            InputStream is = smaliFile.open();
            classDef = SmaliParser.parse(smaliFile.getPath(), is, builder);
            is.close();
        } catch (Exception e) {
            throw new RuntimeException("Error loading class definition: " + className, e);
        }

        return classDef;
    }

    private void parseClassIfNecessary(String className) {
        if (classNameToClass.containsKey(className)) {
            return;
        }

        SmaliFile smaliFile = classNameToSmaliFile.get(className);
        if (smaliFile == null) {
            throw new RuntimeException("Can't find Smali file for " + className);
        }

        boolean isFramework = smaliFileFactory.isFrameworkClass(className);
        DexBuilder builder = isFramework ? frameworkDexBuilder : dexBuilder;
        BuilderClassDef classDef = parseClass(className, builder);
        VirtualClass virtualClass = new VirtualClass(classDef);
        classNameToClass.put(className, virtualClass);
    }

}
