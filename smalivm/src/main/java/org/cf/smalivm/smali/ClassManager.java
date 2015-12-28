package org.cf.smalivm.smali;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import org.cf.smalivm.exception.UnknownAncestors;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.builder.BuilderTryBlock;
import org.jf.dexlib2.builder.MutableMethodImplementation;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderField;
import org.jf.dexlib2.writer.builder.BuilderMethod;
import org.jf.dexlib2.writer.builder.BuilderMethodParameter;
import org.jf.dexlib2.writer.builder.DexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class manager is responsible for loading Smali files into Dexlib2 objects and making them available.
 *
 * @author caleb
 *
 */
public class ClassManager {

    private static final Logger log = LoggerFactory.getLogger(ClassManager.class.getSimpleName());

    private final Map<String, BuilderClassDef> classNameToClassDef;

    private final Map<String, List<String>> classNameToFieldNameAndType;
    private final Map<String, SmaliFile> classNameToSmaliFile;
    private final DexBuilder dexBuilder;
    // Use separate DexBuilder to intern framework classes so they're not included in output dex
    private final DexBuilder frameworkDexBuilder = DexBuilder.makeDexBuilder();
    private final Map<String, BuilderMethod> methodDescriptorToMethod;
    private final Map<String, List<String>> methodDescriptorToParameterTypes;
    private final Map<String, List<BuilderTryBlock>> methodDescriptorToTryBlocks;
    private final SmaliFileFactory smaliFileFactory;

    private ClassManager(DexBuilder dexBuilder, boolean ignore) {
        this.dexBuilder = dexBuilder;
        classNameToClassDef = new HashMap<String, BuilderClassDef>();
        methodDescriptorToMethod = new HashMap<String, BuilderMethod>();
        methodDescriptorToParameterTypes = new HashMap<String, List<String>>();
        methodDescriptorToTryBlocks = new HashMap<String, List<BuilderTryBlock>>();
        classNameToFieldNameAndType = new HashMap<String, List<String>>();
        smaliFileFactory = new SmaliFileFactory();
        classNameToSmaliFile = new HashMap<String, SmaliFile>();
    }

    ClassManager(DexBuilder dexBuilder) throws IOException {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles());
    }

    ClassManager(File smaliPath, DexBuilder dexBuilder) throws IOException {
        this(dexBuilder, false);
        cacheSmaliFiles(smaliFileFactory.getSmaliFiles(smaliPath));
    }

    private void addFieldNameAndTypes(BuilderClassDef classDef) {
        String className = ReferenceUtil.getReferenceString(classDef);
        Collection<BuilderField> fields = classDef.getFields();
        List<String> fieldNameAndTypes = new LinkedList<String>();
        for (BuilderField field : fields) {
            String fieldDescriptor = ReferenceUtil.getFieldDescriptor(field);
            String fieldNameAndType = fieldDescriptor.split("->")[1];
            fieldNameAndTypes.add(fieldNameAndType);
        }
        classNameToFieldNameAndType.put(className, fieldNameAndTypes);
    }

    private void addMethods(BuilderClassDef classDef) {
        for (BuilderMethod method : classDef.getMethods()) {
            String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
            methodDescriptorToMethod.put(methodDescriptor, method);
            addParameterTypes(method);
            addTryBlocks(method);
        }
    }

    private void addParameterTypes(BuilderMethod method) {
        List<? extends BuilderMethodParameter> builderParameters = method.getParameters();
        List<String> parameterTypes = new LinkedList<String>();
        for (BuilderMethodParameter builderParameter : builderParameters) {
            parameterTypes.add(builderParameter.getType());
        }

        int accessFlags = method.getAccessFlags();
        boolean isStatic = Modifier.isStatic(accessFlags);
        if (!isStatic) {
            // First "parameter" for non-static methods is instance reference
            parameterTypes.add(0, method.getDefiningClass());
        }

        String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        methodDescriptorToParameterTypes.put(methodDescriptor, parameterTypes);
    }

    private void addTryBlocks(BuilderMethod method) {
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
        MutableMethodImplementation implementation = (MutableMethodImplementation) method.getImplementation();
        if (implementation == null) {
            return;
        }
        methodDescriptorToTryBlocks.put(methodDescriptor, implementation.getTryBlocks());
    }

    private void cacheSmaliFiles(Set<SmaliFile> smaliFiles) {
        for (SmaliFile smaliFile : smaliFiles) {
            String className = smaliFile.getClassName();

            classNameToSmaliFile.put(className, smaliFile);
        }
    }

    private void dexifyClassIfNecessary(String typeDescriptor) {
        String[] parts = typeDescriptor.split("->");
        String className = parts[0];
        if (getLoadedClassNames().contains(className)) {
            return;
        }

        SmaliFile smaliFile = classNameToSmaliFile.get(className);
        BuilderClassDef classDef;
        try {
            boolean isFramework = smaliFileFactory.isFrameworkClass(className);
            InputStream is = smaliFile.open();
            DexBuilder builder = isFramework ? frameworkDexBuilder : dexBuilder;
            classDef = Dexifier.dexifySmaliFile(smaliFile.getPath(), is, builder);
            is.close();
        } catch (Exception e) {
            throw new RuntimeException("Error while loading class definition of " + typeDescriptor, e);
        }

        classNameToClassDef.put(className, classDef);
        addMethods(classDef);
        addFieldNameAndTypes(classDef);
    }

    private boolean isInstance(String childType, String targetType, Set<String> visited) throws UnknownAncestors {
        if (childType.equals(targetType)) {
            return true;
        }

        Set<String> parents = getAncestors(childType);
        for (String parent : parents) {
            if (visited.contains(parent)) {
                continue;
            }
            visited.add(parent);

            if (parent.equals(targetType)) {
                return true;
            } else if (isInstance(parent, targetType, visited)) {
                return true;
            }
        }

        return false;
    }

    Set<String> getAncestors(String className) throws UnknownAncestors {
        Set<String> parents = new HashSet<String>();
        if (isLocalClass(className)) {
            BuilderClassDef classDef = getClass(className);
            parents.addAll(classDef.getInterfaces());
            if (null != classDef.getSuperclass()) {
                parents.add(classDef.getSuperclass());
            }
        } else {
            String javaClass = ClassNameUtils.internalToBinary(className);
            try {
                Class<?> klazz = Class.forName(javaClass);
                Class<?>[] interfaces = klazz.getInterfaces();
                for (Class<?> interFace : interfaces) {
                    parents.add(ClassNameUtils.toInternal(interFace));
                }
                Class<?> superklazz = klazz.getSuperclass();
                if (null != superklazz) {
                    parents.add(ClassNameUtils.toInternal(superklazz));
                }
            } catch (ClassNotFoundException e) {
                throw new UnknownAncestors(className);
            }
        }

        return parents;
    }

    /**
     * Does not load any Smali files.
     * 
     * @return all framework class names
     */
    public Set<String> getFrameworkClassNames() {
        Set<String> classNames = new HashSet<String>();
        for (String className : classNameToSmaliFile.keySet()) {
            if (smaliFileFactory.isFrameworkClass(className)) {
                classNames.add(className);
            }
        }

        return classNames;
    }

    /**
     * Loads the class if it has not been loaded.
     *
     * @param className
     *            Fully qualified Smali class descriptor
     * @return class definition for the given class name
     */
    public @Nullable BuilderClassDef getClass(String className) {
        dexifyClassIfNecessary(className);

        return classNameToClassDef.get(className);
    }

    public Set<String> getClassAncestors(String className) {
        Set<String> ancestors = new HashSet<String>(3);
        ancestors.add(className);
        String superClass = className;
        do {
            BuilderClassDef classDef = getClass(superClass);
            superClass = classDef.getSuperclass();
            if (superClass != null) {
                ancestors.add(superClass);
            }
        } while (superClass != null);

        return ancestors;
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
     * 
     * @return DexBuilder
     */
    public DexBuilder getDexBuilder() {
        return dexBuilder;
    }

    public BuilderField getField(String className, String fieldName) {
        for (BuilderField field : getFields(className)) {
            if (fieldName.equals(field.getName())) {
                return field;
            }
        }

        return null;
    }

    /**
     *
     * @param className
     * @return field and type (e.g. myBool:Z) for given class including inherited fields
     */
    public List<String> getFieldNameAndTypes(String className) {
        dexifyClassIfNecessary(className);

        Set<String> ancestors = getClassAncestors(className);

        List<String> fieldNameAndTypes = new LinkedList<String>();
        for (String ancestor : ancestors) {
            fieldNameAndTypes.addAll(classNameToFieldNameAndType.get(ancestor));
        }

        return fieldNameAndTypes;
    }

    /**
     *
     * @param className
     * @return fields for given class
     */
    public Collection<BuilderField> getFields(String className) {
        BuilderClassDef classDef = getClass(className);

        return classDef.getFields();
    }

    /**
     * Gets all class names that have been parsed and loaded into dexlib objects.
     * 
     * @return loaded class names
     */
    public Set<String> getLoadedClassNames() {
        return classNameToClassDef.keySet();
    }

    /**
     * Returns the key set representing all local classes. Changing to this return object will change the underlying
     * local class pool. Does not load any Smali files.
     * 
     * @return
     */
    public Set<String> getLocalClassNames() {
        return classNameToSmaliFile.keySet();
    }

    /**
     *
     * @param methodDescriptor
     * @return {@code BuilderMethod} for methodDescriptor or null if not found
     */
    public BuilderMethod getMethod(String methodDescriptor) {
        dexifyClassIfNecessary(methodDescriptor);

        return methodDescriptorToMethod.get(methodDescriptor);
    }

    /**
     *
     * @param className
     * @param methodSignature
     * @return {@code BuilderMethod} for className and methodSignature, or null if not found
     */
    public BuilderMethod getMethod(String className, String methodSignature) {
        StringBuilder sb = new StringBuilder(className);
        sb.append("->").append(methodSignature);

        return getMethod(sb.toString());
    }

    /**
     *
     * @param className
     * @return set of all method descriptors for given className
     */
    public Set<String> getMethodDescriptors(String className) {
        dexifyClassIfNecessary(className);

        BuilderClassDef classDef = getClass(className);
        Set<String> methodNames = new HashSet<String>();
        for (BuilderMethod method : classDef.getMethods()) {
            String methodName = ReferenceUtil.getMethodDescriptor(method);
            methodNames.add(methodName);
        }

        return methodNames;
    }

    /**
     * Does not load any Smali files.
     *
     * @return all local class names which are not part of the framework
     */
    public Set<String> getNonFrameworkClassNames() {
        Set<String> classNames = new HashSet<String>();
        for (String className : classNameToSmaliFile.keySet()) {
            if (!smaliFileFactory.isFrameworkClass(className)) {
                classNames.add(className);
            }
        }

        return classNames;
    }

    /**
     * Get list of types for objects in parameter registers for a given method descriptor.
     * This is different {@code Utils.getParameterTypes} in that non-static methods also have
     * the 'this' (p0) reference type in the return value.
     * Note: For non-static method,
     * 
     * @param methodDescriptor
     * @return list of internal format parameter types
     */
    public List<String> getParameterTypes(String methodDescriptor) {
        dexifyClassIfNecessary(methodDescriptor);

        return methodDescriptorToParameterTypes.get(methodDescriptor);
    }

    /**
     *
     * @param methodDescriptor
     * @return try / catch blocks of given method
     */
    public List<BuilderTryBlock> getTryBlocks(String methodDescriptor) {
        dexifyClassIfNecessary(methodDescriptor);

        return methodDescriptorToTryBlocks.get(methodDescriptor);
    }

    public boolean isFrameworkClass(String typeName) {
        String className = typeName.split("->")[0];

        return smaliFileFactory.isFrameworkClass(className);
    }

    public boolean isInnerClass(String binaryChild, String binaryParent) {
        // TODO: easy - add tests and documentation
        if (binaryChild.startsWith(binaryParent + "$")) {
            return true;
        }

        return false;
    }

    public boolean isInstance(Class<?> childClass, Class<?> targetClass) throws UnknownAncestors {
        if ((childClass == null) || (targetClass == null)) {
            return false;
        }

        String childType = ClassNameUtils.toInternal(childClass);
        String targetType = ClassNameUtils.toInternal(targetClass);

        return isInstance(childType, targetType);
    }

    public boolean isInstance(String childType, String targetType) throws UnknownAncestors {
        // Arrays are objects
        // http://docs.oracle.com/javase/specs/jls/se8/html/jls-4.html#jls-4.3.1
        if (childType.startsWith("[") && targetType.equals("Ljava/lang/Object;")) {
            return true;
        }

        int childDimension = ClassNameUtils.getDimensionCount(childType);
        int targetDimension = ClassNameUtils.getDimensionCount(targetType);
        if (childDimension != targetDimension) {
            return false;
        }

        // Already ensured if array type, has the same dimensions. Compare base types now.
        String baseChild = ClassNameUtils.getComponentBase(childType);
        if (ClassNameUtils.isPrimitive(baseChild)) {
            baseChild = ClassNameUtils.binaryToInternal(ClassNameUtils.getWrapper(baseChild));
        }
        String baseTarget = ClassNameUtils.getComponentBase(targetType);
        if (ClassNameUtils.isPrimitive(baseTarget)) {
            baseTarget = ClassNameUtils.binaryToInternal(ClassNameUtils.getWrapper(baseTarget));
        }

        return isInstance(baseChild, baseTarget, new HashSet<String>());
    }

    /**
     * Returns true if a given class name is available at runtime as a {@code SmaliFile}.
     * This includes input and framework files.
     * 
     * @param className
     * @return true if the SmaliFile for className is available at runtime
     */
    public boolean isLocalClass(String className) {
        return classNameToSmaliFile.containsKey(className);
    }

    /**
     *
     * @param methodDescriptor
     * @return true if {@link=isLocalClass} is true, and method is defined for class
     */
    public boolean isLocalMethod(String methodDescriptor) {
        String[] parts = methodDescriptor.split("->");
        String className = parts[0];
        if (!isLocalClass(className)) {
            return false;
        }

        return getMethod(methodDescriptor) != null;
    }

    /**
     *
     * @param methodDescriptor
     * @return true if method is native, false otherwise
     */
    public boolean isNativeMethod(String methodDescriptor) {
        BuilderMethod method = getMethod(methodDescriptor);

        return Modifier.isNative(method.getAccessFlags());
    }

    public boolean isSafeFrameworkClass(String typeName) {
        String className = typeName.split("->")[0];

        return smaliFileFactory.isSafeFrameworkClass(className);
    }

    /**
     *
     * @param methodDescriptor
     * @return true if method has implementation (not abstract or native), false otherwise
     */
    public boolean methodHasImplementation(String methodDescriptor) {
        BuilderMethod method = getMethod(methodDescriptor);

        return null != method.getImplementation();
    }

}
