package org.cf.smalivm.smali;

import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.EnumerationState;
import net.bytebuddy.description.modifier.MethodArguments;
import net.bytebuddy.description.modifier.MethodManifestation;
import net.bytebuddy.description.modifier.ModifierContributor;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.SynchronizationState;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeDescription.Generic;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.DynamicType.Builder;
import net.bytebuddy.dynamic.DynamicType.Loaded;
import net.bytebuddy.dynamic.DynamicType.Unloaded;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.dynamic.scaffold.TypeValidation;
import net.bytebuddy.implementation.StubMethod;
import net.bytebuddy.jar.asm.Opcodes;

import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.cf.util.ClassNameUtils;
import org.cf.util.ClassNameUtils.TypeFormat;
import org.cf.util.Utils;
import org.jf.dexlib2.iface.Annotation;
import org.jf.dexlib2.iface.ClassDef;
import org.jf.dexlib2.iface.Field;
import org.jf.dexlib2.iface.Method;
import org.jf.dexlib2.util.ReferenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassBuilder {

    private static final Logger log = LoggerFactory.getLogger(ClassBuilder.class.getSimpleName());

    private final ArrayDeque<String> buildingClasses;
    private final ArrayDeque<Pair<String, ClassDef>> classesToBuild;
    private final ClassManager classManager;
    private final Map<String, Unloaded<?>> classNameToUnloaded;
    private final Set<String> shallowClasses;
    private ClassLoader classLoader;

    ClassBuilder(ClassManager manager) {
        this.classManager = manager;
        buildingClasses = new ArrayDeque<String>();
        classesToBuild = new ArrayDeque<Pair<String, ClassDef>>();
        classNameToUnloaded = new HashMap<String, Unloaded<?>>();
        shallowClasses = new HashSet<String>();
        classLoader = this.getClass().getClassLoader();
    }

    ClassBuilder(ClassManager manager, ClassLoader classLoader) {
        this(manager);
        this.classLoader = classLoader;
    }

    private Map<String, Class<?>> buildCache(Loaded<?> loaded) {
        Map<String, Class<?>> cache = new HashMap<String, Class<?>>();
        cache(cache, loaded.getLoaded());
        for (Class<?> klazz : loaded.getLoadedAuxiliaryTypes().values()) {
            cache(cache, klazz);
        }

        return cache;
    }

    private void buildClass(String className, ClassDef classDef) throws ClassNotFoundException {
        if (classNameToUnloaded.containsKey(className)) {
            return;
        }

        if (className.startsWith("android.net.http.HttpResponseCache")) {
            // TODO: these must be skipped because they implement java/net/ExtendedResponseCache
            // And that class cannot be easily loaded because it's a prohibited package
            return;
        }

        startBuilding(className);

        ByteBuddy buddy = new ByteBuddy().with(TypeValidation.DISABLED);
        Builder<?> builder = setSuperclass(buddy, classDef.getSuperclass());
        builder = setInterfaces(builder, classDef.getInterfaces());
        builder = setAnnotations(builder, classDef.getAnnotations());
        builder = setMethods(builder, classDef.getMethods());
        builder = setFields(builder, classDef.getFields());
        builder = builder.name(className);

        ModifierContributor.ForType[] modifiers = getTypeModifiers(classDef.getAccessFlags());
        builder = builder.modifiers(modifiers);

        Unloaded<?> unloaded = builder.make();
        classNameToUnloaded.put(className, unloaded);

        finishBuilding(className);
    }

    private TypeDescription buildLatentTypeDescription(String className, ClassDef classDef)
                    throws ClassNotFoundException {
        int modifiers = classDef.getAccessFlags();
        String superName = ClassNameUtils.internalToBinary(classDef.getSuperclass());
        Generic superType = buildTypeDescription(superName).asGenericType();
        List<Generic> interfaces = new LinkedList<Generic>();
        for (String interfaceName : classDef.getInterfaces()) {
            String binaryName = ClassNameUtils.internalToBinary(interfaceName);
            interfaces.add(buildTypeDescription(binaryName, false, true).asGenericType());
        }

        return new TypeDescription.Latent(className, modifiers, superType, interfaces);
    }

    private void buildQueued() throws ClassNotFoundException {
        do {
            Pair<String, ClassDef> p = classesToBuild.pop();
            String className = p.getLeft();
            ClassDef classDef = p.getRight();
            buildClass(className, classDef);
        } while (!classesToBuild.isEmpty());
    }

    private void buildShallowClass(String className, boolean isInterface) throws ClassNotFoundException {
        startBuilding(className);

        ByteBuddy buddy = new ByteBuddy().with(TypeValidation.DISABLED);
        Builder<?> builder = setSuperclass(buddy, "Ljava/lang/Object;");
        builder = builder.name(className);
        ModifierContributor.ForType[] modifiers = getShallowTypeModifiers(isInterface);
        builder = builder.modifiers(modifiers);

        Unloaded<?> unloaded = builder.make();
        classNameToUnloaded.put(className, unloaded);
        shallowClasses.add(className);

        finishBuilding(className);
    }

    private TypeDescription buildTypeDescription(String className) throws ClassNotFoundException {
        return buildTypeDescription(className, false, false);
    }

    private TypeDescription buildTypeDescription(String className, boolean isSuper, boolean isInterface)
                    throws ClassNotFoundException {
        if (className.equals("void")) {
            // ClassUtils.getClass doesn't handle void, and it must not be used as a latent type or methods will
            // silently fail to define..
            return new TypeDescription.ForLoadedType(Void.TYPE);
        }

        if (classNameToUnloaded.containsKey(className)) {
            Unloaded<?> unloaded = classNameToUnloaded.get(className);
            return unloaded.getTypeDescription();
        }

        try {
            Class<?> klazz = ClassUtils.getClass(classLoader, className);
            return new TypeDescription.ForLoadedType(klazz);
        } catch (ClassNotFoundException e) {
        }

        if (isBuildingNow(className)) {
            return TargetType.DESCRIPTION;
        }

        int dimensionCount = ClassNameUtils.getDimensionCount(className);
        if (dimensionCount > 0) {
            String baseClassName = ClassNameUtils.getComponentBase(className);
            if (baseClassName.startsWith("L")) {
                baseClassName = baseClassName.substring(1, baseClassName.length() - 1);
                if (isBuildingNow(baseClassName)) {
                    return TypeDescription.ArrayProjection.of(TargetType.DESCRIPTION, dimensionCount);
                } else {
                    TypeDescription typeDescription = buildTypeDescription(baseClassName);
                    return TypeDescription.ArrayProjection.of(typeDescription, dimensionCount);
                }
            }
        }

        String internalName = ClassNameUtils.binaryToInternal(className);
        if (classManager.isLocalClass(internalName)) {
            ClassDef classDef = classManager.getClass(internalName);
            if (!isBuilding(className)) {
                if (isSuper || isInterface) {
                    buildClass(className, classDef);
                    Unloaded<?> unloaded = classNameToUnloaded.get(className);
                    return unloaded.getTypeDescription();
                } else {
                    queueClass(className, classDef);
                }
            }

            return buildLatentTypeDescription(className, classDef);
        }

        // Throwing an exception when a referenced class cannot be found is too brittle. Instead, build a "shallow"
        // class which doesn't have all of the correct properties since they can't be known, but it at least acts like a
        // class object. Loading shallow classes should still return a class not found exception. We only create them
        // here to avoid copious exceptions.
        // throw new ClassNotFoundException(className);
        log.warn("Class {} is referenced but the definition is not available.", className);
        buildShallowClass(className, isInterface);
        Unloaded<?> unloaded = classNameToUnloaded.get(className);
        return unloaded.getTypeDescription();
    }

    private void finishBuilding(String className) {
        log.info("Finished building: {}", className);
        System.out.println("Finished building: " + className);
        buildingClasses.pop();
    }

    private Loaded<?> getLoadedTarget(Unloaded<?> target) {
        List<DynamicType> includeTypes = new LinkedList<DynamicType>(classNameToUnloaded.values());
        classNameToUnloaded.clear();
        target = target.include(includeTypes);
        Loaded<?> loaded = target.load(this.getClass().getClassLoader(), ClassLoadingStrategy.Default.WRAPPER);

        return loaded;
    }

    private ModifierContributor.ForMethod[] getMethodModifiers(int accessFlags) {
        List<ModifierContributor.ForMethod> mods = new LinkedList<ModifierContributor.ForMethod>();
        if (Modifier.isPublic(accessFlags)) {
            mods.add(Visibility.PUBLIC);
        } else if (Modifier.isProtected(accessFlags)) {
            mods.add(Visibility.PROTECTED);
        } else if (Modifier.isPrivate(accessFlags)) {
            mods.add(Visibility.PRIVATE);
        } else {
            mods.add(Visibility.PACKAGE_PRIVATE);
        }

        boolean isBridge = (accessFlags & Opcodes.ACC_BRIDGE) == Opcodes.ACC_BRIDGE;
        if (isBridge) {
            mods.add(MethodManifestation.BRIDGE);
        }

        if (Modifier.isFinal(accessFlags)) {
            mods.add(MethodManifestation.FINAL);
        }

        if (Modifier.isNative(accessFlags)) {
            mods.add(MethodManifestation.NATIVE);
        }

        if (Modifier.isAbstract(accessFlags)) {
            mods.add(MethodManifestation.ABSTRACT);
        }

        if (Modifier.isStatic(accessFlags)) {
            mods.add(Ownership.STATIC);
        } else {
            mods.add(Ownership.MEMBER);
        }

        if (Modifier.isStrict(accessFlags)) {
            // ???
        }

        if (Modifier.isSynchronized(accessFlags)) {
            mods.add(SynchronizationState.SYNCHRONIZED);
        }

        boolean isSynthetic = (accessFlags & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC;
        if (isSynthetic) {
            mods.add(SyntheticState.SYNTHETIC);
        }

        boolean isVarArgs = (accessFlags & Opcodes.ACC_VARARGS) == Opcodes.ACC_VARARGS;
        if (isVarArgs) {
            mods.add(MethodArguments.VARARGS);
        }

        return mods.toArray(new ModifierContributor.ForMethod[mods.size()]);
    }

    private ModifierContributor.ForType[] getShallowTypeModifiers(boolean isInterface) {
        List<ModifierContributor.ForType> mods = new LinkedList<ModifierContributor.ForType>();
        mods.add(Visibility.PUBLIC);
        if (isInterface) {
            mods.add(TypeManifestation.INTERFACE);
        }

        return mods.toArray(new ModifierContributor.ForType[mods.size()]);
    }

    private ModifierContributor.ForType[] getTypeModifiers(int accessFlags) {
        List<ModifierContributor.ForType> mods = new LinkedList<ModifierContributor.ForType>();
        if (Modifier.isPublic(accessFlags)) {
            mods.add(Visibility.PUBLIC);
        } else if (Modifier.isProtected(accessFlags)) {
            mods.add(Visibility.PROTECTED);
        } else if (Modifier.isPrivate(accessFlags)) {
            mods.add(Visibility.PRIVATE);
        } else {
            mods.add(Visibility.PACKAGE_PRIVATE);
        }

        if (Modifier.isStatic(accessFlags)) {
            mods.add(Ownership.STATIC);
        } else {
            mods.add(Ownership.MEMBER);
        }

        boolean isEnum = (accessFlags & Opcodes.ACC_ENUM) == Opcodes.ACC_ENUM;
        if (isEnum) {
            mods.add(EnumerationState.ENUMERATION);
        }

        boolean isSynthetic = (accessFlags & Opcodes.ACC_SYNTHETIC) == Opcodes.ACC_SYNTHETIC;
        if (isSynthetic) {
            mods.add(SyntheticState.SYNTHETIC);
        }

        if (Modifier.isAbstract(accessFlags)) {
            if (Modifier.isInterface(accessFlags)) {
                mods.add(TypeManifestation.INTERFACE);
            } else {
                mods.add(TypeManifestation.ABSTRACT);
            }
        }

        boolean isAnnotation = (accessFlags & Opcodes.ACC_ANNOTATION) == Opcodes.ACC_ANNOTATION;
        if (isAnnotation) {
            mods.add(TypeManifestation.ANNOTATION);
        }

        if (Modifier.isFinal(accessFlags)) {
            mods.add(TypeManifestation.FINAL);
        }

        return mods.toArray(new ModifierContributor.ForType[mods.size()]);
    }

    private boolean isAlreadyLoaded(String className) {
        try {
            ClassUtils.getClass(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private boolean isBuilding(String className) {
        return buildingClasses.contains(className);
    }

    private boolean isBuildingNow(String className) {
        return buildingClasses.peek().equals(className);
    }

    private boolean isProhibitedPackage(String binaryName) {
        // This is a prohibited class path and can't be instantiated.
        // These should (probably) all exist on the JVM anyway, so don't have to be loaded from framework smali.
        return binaryName.startsWith("java.");
    }

    private void queueClass(String className, ClassDef classDef) {
        // Must check if prohibited or already loaded here because queueClass() is called from multiple places.
        if (isProhibitedPackage(className)) {
            return;
        }

        if (isAlreadyLoaded(className)) {
            return;
        }

        if (className.startsWith("android.net.http.HttpResponseCache") || className
                        .startsWith("libcore.net.http.HttpResponseCache")) {
            // TODO: these must be skipped because they implement java/net/ExtendedResponseCache
            // And that class cannot be easily loaded because it's a prohibited package
            return;
        }

        Pair<String, ClassDef> p = Pair.of(className, classDef);
        classesToBuild.push(p);
    }

    private void queueClasses(Set<String> classNames) {
        for (String className : classNames) {
            String internalName = ClassNameUtils.toFormat(className, TypeFormat.INTERNAL);
            ClassDef classDef = classManager.getClass(internalName);
            String binaryName = ClassNameUtils.internalToBinary(internalName);
            queueClass(binaryName, classDef);
        }
    }

    private Unloaded<?> selectTarget() {
        String firstClassName = classNameToUnloaded.keySet().iterator().next();
        Unloaded<?> target = classNameToUnloaded.get(firstClassName);
        classNameToUnloaded.remove(firstClassName);

        return target;
    }

    private Builder<?> setAnnotations(Builder<?> builder, Set<? extends Annotation> annotations) {
        // TODO: implement
        for (Annotation annotation : annotations) {
            // System.out.println(annotation.getType());
        }

        return builder;
    }

    private Builder<?> setFields(Builder<?> builder, Iterable<? extends Field> fields) throws ClassNotFoundException {
        for (Field field : fields) {
            String binaryName = ClassNameUtils.internalToBinary(field.getType());
            TypeDescription fieldType = buildTypeDescription(binaryName);
            String fieldName = field.getName();
            int modifiers = field.getAccessFlags();
            builder = builder.defineField(fieldName, fieldType, modifiers);
        }

        return builder;
    }

    private Builder<?> setInterfaces(Builder<?> builder, List<String> interfaceNames) throws ClassNotFoundException {
        for (String interfaceName : interfaceNames) {
            String binaryName = ClassNameUtils.internalToBinary(interfaceName);
            TypeDescription interfaceType = buildTypeDescription(binaryName, false, true);
            builder = builder.implement(interfaceType);
        }

        return builder;
    }

    private Builder<?> setMethods(Builder<?> builder, Iterable<? extends Method> methods) throws ClassNotFoundException {
        for (Method method : methods) {
            String methodName = method.getName();
            if (methodName.equals("<clinit>") || methodName.equals("<init>")) {
                continue;
            }

            String methodDescriptor = ReferenceUtil.getMethodDescriptor(method);
            // Parse method descriptor instead of using ClassManager#getParameterTypes because
            // we only care about parameter types of the signature, not parameter register types.
            List<String> parameterTypeNames = Utils.getParameterTypes(methodDescriptor);
            List<TypeDescription> parameterTypes = new LinkedList<TypeDescription>();
            for (String parameterTypeName : parameterTypeNames) {
                String binaryName = ClassNameUtils.internalToBinary(parameterTypeName);
                parameterTypes.add(buildTypeDescription(binaryName));
            }

            ModifierContributor.ForMethod[] modifiers = getMethodModifiers(method.getAccessFlags());
            String binaryName = ClassNameUtils.internalToBinary(method.getReturnType());
            TypeDescription returnType = buildTypeDescription(binaryName);
            builder = builder.defineMethod(methodName, returnType, modifiers).withParameters(parameterTypes)
                            .intercept(StubMethod.INSTANCE);
        }

        return builder;
    }

    private Builder<?> setSuperclass(ByteBuddy buddy, String superClassName) throws ClassNotFoundException {
        String binaryName = ClassNameUtils.internalToBinary(superClassName);
        TypeDescription superType = buildTypeDescription(binaryName, true, false);

        return buddy.subclass(superType);
    }

    private void startBuilding(String className) {
        log.info("Building stub class for: {}", className);
        System.out.println("Building stub class for: " + className);
        buildingClasses.push(className);
    }

    public Map<String, Class<?>> build(Set<String> classNames) throws ClassNotFoundException {
        Loaded<?> loaded = buildLoaded(classNames);

        return buildCache(loaded);
    }

    public Map<String, Class<?>> build(String... classNames) throws ClassNotFoundException {
        Set<String> classes = new HashSet<String>(Arrays.asList(classNames));

        return build(classes);
    }

    public Loaded<?> buildLoaded(Set<String> classNames) throws ClassNotFoundException {
        queueClasses(classNames);
        buildQueued();

        Unloaded<?> target = selectTarget();
        Loaded<?> loaded = getLoadedTarget(target);

        return loaded;
    }

    public boolean isShallowClass(String className) {
        return shallowClasses.contains(className);
    }

    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    private static void cache(Map<String, Class<?>> cache, Class<?> klazz) {
        log.debug("Caching stub class: {}", klazz);
        System.out.println("Caching stub class: " + klazz);
        // String internalName = ClassNameUtils.toInternal(klazz);
        cache.put(klazz.getName(), klazz);
    }

}
