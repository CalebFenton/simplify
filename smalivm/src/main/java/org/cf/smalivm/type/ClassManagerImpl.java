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
public class ClassManagerImpl implements ClassManager {

    private static final Logger log = LoggerFactory.getLogger(ClassManagerImpl.class.getSimpleName());

    private final HashMap<String, VirtualClass> classNameToClass;
    private final Map<String, SmaliFile> classNameToSmaliFile;

    private final SmaliFileFactory smaliFileFactory;
    private final DexBuilder dexBuilder;
    // Use separate DexBuilder to intern framework classes to avoid including in output dex
    private final DexBuilder frameworkDexBuilder = new DexBuilder(Opcodes.getDefault());

    private Set<String> frameworkClassNames = null;
    private Set<String> nonFrameworkClassNames = null;

    ClassManagerImpl(DexBuilder dexBuilder) {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles());
    }

    private ClassManagerImpl(DexBuilder dexBuilder, boolean internalOnly) {
        this.dexBuilder = dexBuilder;
        smaliFileFactory = new SmaliFileFactory();
        classNameToClass = new HashMap<>();
        classNameToSmaliFile = new HashMap<>();
    }

    ClassManagerImpl(DexBuilder dexBuilder, File smaliPath) throws IOException {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles(smaliPath));
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names, including framework
     */
    @Override
    public Set<String> getClassNames() {
        return classNameToSmaliFile.keySet();
    }

    /**
     * @return DexBuilder
     */
    @Override
    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    /**
     * Does not load any Smali files.
     *
     * @return all framework class names
     */
    @Override
    public Set<String> getFrameworkClassNames() {
        if (frameworkClassNames == null) {
            frameworkClassNames = classNameToSmaliFile.keySet()
                .parallelStream()
                .filter(smaliFileFactory::isFrameworkClass)
                .collect(Collectors.toSet());
        }

        return frameworkClassNames;
    }

    @Override
    public Collection<VirtualClass> getLoadedClasses() {
        return classNameToClass.values();
    }

    @Override
    public VirtualMethod getMethod(String methodSignature) {
        String[] parts = methodSignature.split("->");
        String className = parts[0];
        String methodDescriptor = parts[1];

        return getMethod(className, methodDescriptor);
    }

    @Override
    public VirtualMethod getMethod(String className, String methodDescriptor) {
        VirtualType virtualClass = getVirtualClass(className);

        return virtualClass.getMethod(methodDescriptor);
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names which are not part of the framework
     */
    @Override
    public Set<String> getNonFrameworkClassNames() {
        if (nonFrameworkClassNames == null) {
            nonFrameworkClassNames = classNameToSmaliFile.keySet()
                .parallelStream()
                .filter(className -> !smaliFileFactory.isFrameworkClass(className))
                .collect(Collectors.toSet());
        }

        return nonFrameworkClassNames;
    }

    @Override
    public VirtualClass getVirtualClass(Class<?> klazz) {
        return getVirtualClass(ClassNameUtils.toInternal(klazz));
    }

    @Override
    public VirtualClass getVirtualClass(String className) {
        return (VirtualClass) getVirtualType(className);
    }

    @Override
    public VirtualType getVirtualType(TypeReference typeReference) {
        char first = typeReference.charAt(0);
        if (first == 'L') {
            String className = typeReference.getType();
            parseClassIfNecessary(className);

            return classNameToClass.get(className);
        } else if (first == '[') {
            return new VirtualArray(typeReference, this);
        } else if (ClassNameUtils.isPrimitive(typeReference.getType())) {
            return new VirtualPrimitive(typeReference);
        } else {
            // TODO: throw IllegalArgumentException? Or Maybe before it's interned?
            throw new RuntimeException("Unrecognized type: " + typeReference);
        }
    }

    @Override
    public VirtualType getVirtualType(Class<?> typeClass) {
        return getVirtualType(ClassNameUtils.toInternal(typeClass));
    }

    @Override
    public VirtualType getVirtualType(String typeSignature) {
        // TODO: consider validating the type Signature here rather than in getVirtualType
        TypeReference typeReference = getFrameworkDexBuilder().internTypeReference(typeSignature);

        return getVirtualType(typeReference);
    }

    @Override
    public boolean isFrameworkClass(VirtualType virtualClass) {
        return smaliFileFactory.isFrameworkClass(virtualClass.getName());
    }

    @Override
    public boolean isSafeFrameworkClass(VirtualType virtualClass) {
        return smaliFileFactory.isSafeFrameworkClass(virtualClass.getName());
    }

    public DexBuilder getFrameworkDexBuilder() {
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
        VirtualClass virtualClass = new VirtualClass(classDef, this);
        classNameToClass.put(className, virtualClass);
    }

}
