package org.cf.smalivm.core

import java.lang.reflect.Modifier
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.util.ClassNameUtils
import com.android.tools.smali.dexlib2.AccessFlags
import com.android.tools.smali.dexlib2.ValueType
import com.android.tools.smali.dexlib2.iface.Field
import com.android.tools.smali.dexlib2.iface.value.BooleanEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ByteEncodedValue
import com.android.tools.smali.dexlib2.iface.value.CharEncodedValue
import com.android.tools.smali.dexlib2.iface.value.DoubleEncodedValue
import com.android.tools.smali.dexlib2.iface.value.FloatEncodedValue
import com.android.tools.smali.dexlib2.iface.value.IntEncodedValue
import com.android.tools.smali.dexlib2.iface.value.LongEncodedValue
import com.android.tools.smali.dexlib2.iface.value.ShortEncodedValue
import com.android.tools.smali.dexlib2.iface.value.StringEncodedValue
import com.android.tools.smali.dexlib2.immutable.ImmutableMethod

/**
 * Method, field, and class-initializer resolution over one immutable program snapshot. A session resolves through
 * one resolver, so it keeps answering for the revision it started on while edits publish newer ones. Hierarchy
 * questions are [hierarchy]'s.
 */
internal class ProgramSnapshotResolver internal constructor(
    val snapshot: ProgramSnapshot,
    private val index: ClassIndex,
) {
    val hierarchy: HierarchySnapshot get() = snapshot.hierarchy

    /** The program's one host class loader, shared by every resolver so `const-class` identity is program-wide. */
    val classLoader: SmaliClassLoader get() = index.classLoader

    /**
     * The host `Class` that [descriptor] names, loaded by exact binary name with `Class.forName(name, false,
     * loader)` semantics, or `null` when [classLoader] cannot produce it.
     *
     * `null` does not mean the class is absent: a class in the program that the host loader cannot define (an
     * Android-only `java.*` class) also answers `null`, and callers ask [HierarchySnapshot.isLoadable] to tell
     * the two apart. Loading never runs `<clinit>`: ART's `const-class` resolves without initializing.
     */
    fun loadClassConstant(descriptor: String): Class<*>? {
        if (descriptor.length == 1) {
            return if (descriptor == "V") Void.TYPE else ClassNameUtils.getPrimitiveClass(descriptor)
        }
        return try {
            Class.forName(ClassNameUtils.internalToBinary(descriptor), false, classLoader)
        } catch (_: ClassNotFoundException) {
            null
        } catch (_: LinkageError) {
            null
        }
    }

    /**
     * Looks [methodDescriptor] up from [definingClass]: the class and its superclasses, then the maximally-specific
     * superinterface methods. It answers symbolic resolution and, given a receiver's class, virtual selection.
     *
     * An absent class and an absent member are the guest `NoClassDefFoundError` and `NoSuchMethodError`. A
     * declared method with no body (abstract, native, a framework declaration, an array pseudo-method) is
     * [MethodLookup.NoBody]; a backend executes it. `<clinit>` is not inherited, so class initialization asks
     * [lookupClassInitializer] instead.
     */
    fun lookupMethod(definingClass: VirtualType, methodDescriptor: String): MethodLookup {
        if (!hierarchy.isLoadable(definingClass)) return MethodLookup.NoSuchClass
        // An array's members are `length`, `clone()` and Object's members (JLS 10.7), so existence is asked of
        // Object. An existing member answers NoBody rather than Object's body: array `clone()` is a runtime
        // intrinsic that Object.clone()'s framework bytecode cannot perform.
        if (definingClass.isArray) {
            val search = findSnapshotMethod(VirtualType.OBJECT, methodDescriptor)
            return when {
                search.method != null -> MethodLookup.NoBody(resolvedSnapshotMethod(search.method, definingClass))
                search.missingClass -> MethodLookup.NoSuchClass
                else -> MethodLookup.NoSuchMethod
            }
        }
        val search = findSnapshotMethod(definingClass, methodDescriptor)
        if (search.missingClass) return MethodLookup.NoSuchClass
        val method = search.method ?: return MethodLookup.NoSuchMethod
        if (search.defaultConflict) return MethodLookup.DefaultConflict(resolvedSnapshotMethod(method, definingClass))
        return lookupOf(method)
    }

    /** Resolves one invoke under its opcode-specific lookup rule. */
    fun resolveInvocation(
        target: MethodDescriptor,
        kind: InvokeKind,
        callerClass: VirtualType,
        receiverType: VirtualType?,
    ): InvocationLookup {
        val referenced = if (kind == InvokeKind.STATIC && target.descriptor == ClassIndex.CLASS_INITIALIZER_DESCRIPTOR) {
            lookupClassInitializer(target.definingClass)
        } else {
            lookupMethod(target.definingClass, target.descriptor)
        }
        if (referenced is MethodLookup.NoSuchClass || referenced is MethodLookup.NoSuchMethod) {
            return InvocationLookup.Resolved(referenced)
        }
        return when (kind) {
            InvokeKind.STATIC -> InvocationLookup.Resolved(referenced)
            InvokeKind.DIRECT -> InvocationLookup.Resolved(lookupDeclaredMethod(target.definingClass, target.descriptor))
            InvokeKind.SUPER -> if (hierarchy.isInterface(target.definingClass)) {
                resolveInterfaceSuper(callerClass, target)
            } else {
                val parent = try {
                    hierarchy.superclassOf(callerClass)
                } catch (_: ClassLoadFailure) {
                    null
                }
                InvocationLookup.Resolved(
                    if (parent == null) MethodLookup.NoSuchMethod else lookupMethod(parent, target.descriptor),
                )
            }
            InvokeKind.VIRTUAL, InvokeKind.INTERFACE -> if (receiverType != null) {
                InvocationLookup.Resolved(lookupMethod(receiverType, target.descriptor))
            } else {
                val candidates = dispatchCandidates(target.definingClass, target.descriptor)
                val candidateNames = java.util.Collections.unmodifiableSet(LinkedHashSet(candidates.keys))
                when {
                    !dispatchWorldIsComplete(target.definingClass) -> InvocationLookup.Ambiguous(candidateNames)
                    candidates.isEmpty() -> InvocationLookup.Resolved(referenced)
                    candidates.size > 1 -> InvocationLookup.Ambiguous(candidateNames)
                    // Instances of framework subtypes are not enumerated. One application candidate is the target
                    // only when it is the referenced method, the same answer as no application candidate.
                    frameworkSubtypesCanReceive(target.definingClass) &&
                        candidates.keys.single() != selectedSignature(referenced) ->
                        InvocationLookup.Ambiguous(candidateNames)
                    else -> InvocationLookup.Resolved(candidates.values.single(), candidateNames)
                }
            }
        }
    }

    /**
     * Whether unknown-receiver dispatch on [declaredType] is closed over the program: no application class has an
     * absent ancestor and no code can load classes at run time. Final classes, arrays, and primitives are closed.
     */
    fun dispatchWorldIsComplete(declaredType: VirtualType): Boolean {
        if (!declaredType.isReference || declaredType.isArray || hierarchy.isFinal(declaredType)) return true
        val ancestryComplete = index.hierarchyCompleteOverride ?: hierarchy.applicationAncestryComplete
        val dynamicLoading = index.dynamicLoadingEvidenceOverride ?: snapshot.dynamicLoadingEvidence.isPresent
        return ancestryComplete && !dynamicLoading
    }

    /**
     * Class initialization targets checked before reflection loads any host class: [type], its superclasses, and
     * every superinterface in sorted depth-first order. Throws [ClassLoadFailure] when an ancestor is absent.
     */
    fun hostInitializationChain(type: VirtualType): List<VirtualType> {
        if (!type.isReference || type.isArray) return listOf(type)
        val chain = linkedSetOf<VirtualType>()
        val pendingInterfaces = ArrayDeque<VirtualType>()

        fun pushInterfaces(owner: VirtualType) {
            hierarchy.directInterfaces(owner).sortedByDescending(VirtualType::descriptor)
                .forEach(pendingInterfaces::addLast)
        }

        var current: VirtualType? = type
        while (current != null) {
            chain += current
            pushInterfaces(current)
            while (pendingInterfaces.isNotEmpty()) {
                val interfaceType = pendingInterfaces.removeLast()
                if (chain.add(interfaceType)) pushInterfaces(interfaceType)
            }
            if (hierarchy.isInterface(current)) break
            current = hierarchy.superclassOf(current)
        }
        return java.util.Collections.unmodifiableList(chain.toList())
    }

    /**
     * [type]'s own declared `<clinit>()V`. A static initializer is not inherited, so a class that declares none
     * answers [MethodLookup.NoSuchMethod], the common case. An array or a primitive has no initializer.
     */
    fun lookupClassInitializer(type: VirtualType): MethodLookup {
        if (!type.isReference || type.isArray) return MethodLookup.NoSuchMethod
        if (!hierarchy.isLoadable(type)) return MethodLookup.NoSuchClass
        val declared = declaredSnapshotMethod(type.descriptor, ClassIndex.CLASS_INITIALIZER_DESCRIPTOR)
            ?: return MethodLookup.NoSuchMethod
        return lookupOf(declared)
    }

    /**
     * Whether [type] declares a `<clinit>()V`, without building a view. Skipping a framework initializer costs
     * precision only when one exists, so class-init policy asks this before raising a trigger's side effect.
     */
    fun hasClassInitializer(type: VirtualType): Boolean {
        if (!type.isReference || type.isArray || !hierarchy.isLoadable(type)) return false
        return declaredSnapshotMethod(type.descriptor, ClassIndex.CLASS_INITIALIZER_DESCRIPTOR) != null
    }

    /**
     * The static field a `sget*`/`sput*` reference names. A dex field reference is `(class, name, type)`, so two
     * statics differing only in type are two fields. The search follows JVMS 5.4.3.2 and ART
     * `Class::FindStaticField`: the class, then its direct superinterfaces recursively, then its superclass.
     * [StaticFieldLookup.Resolved] names the declaring class, so every spelling of one inherited static reads one
     * cell, and that class is the one a trigger initializes.
     *
     * A class absent anywhere in the search is [StaticFieldLookup.NoSuchClass], the guest `NoClassDefFoundError`.
     * A primitive or array [referencedClass] declares no statics.
     */
    fun resolveStaticField(referencedClass: VirtualType, name: String, fieldTypeDescriptor: String): StaticFieldLookup {
        if (!referencedClass.isReference || referencedClass.isArray) {
            return StaticFieldLookup.NoSuchField(ClassIndex.fieldDescriptorOf(referencedClass, name, fieldTypeDescriptor))
        }
        if (!hierarchy.isLoadable(referencedClass)) return StaticFieldLookup.NoSuchClass(referencedClass)
        val pending = ArrayDeque<String>()
        val searched = HashSet<String>()
        pending.addLast(referencedClass.descriptor)
        while (pending.isNotEmpty()) {
            val descriptor = pending.removeLast()
            if (!searched.add(descriptor)) continue
            val owner = VirtualType.of(descriptor)
            val declaration = hierarchy.declarations[descriptor] ?: return StaticFieldLookup.NoSuchClass(owner)
            val wanted = ClassIndex.fieldDescriptorOf(owner, name, fieldTypeDescriptor)
            declaredStaticFields(owner).firstOrNull { it.descriptor == wanted }?.let { declared ->
                return StaticFieldLookup.Resolved(
                    owner,
                    declared.descriptor,
                    declared.type,
                    Modifier.isVolatile(declared.modifiers),
                )
            }
            // Popped last-in first-out: every superinterface subtree, in declaration order, before the superclass.
            declaration.superclass?.let(pending::addLast)
            declaration.interfaces.asReversed().forEach(pending::addLast)
        }
        return StaticFieldLookup.NoSuchField(ClassIndex.fieldDescriptorOf(referencedClass, name, fieldTypeDescriptor))
    }

    /**
     * Resolves one `iget*`/`iput*` reference over the superclass chain; interfaces declare no instance fields. The
     * defining class in [InstanceFieldLookup.Resolved] qualifies the heap cell, so inherited aliases share one cell
     * and a shadowing declaration stays distinct.
     */
    fun resolveInstanceField(
        referencedClass: VirtualType,
        name: String,
        fieldTypeDescriptor: String,
    ): InstanceFieldLookup {
        val requested = ClassIndex.fieldDescriptorOf(referencedClass, name, fieldTypeDescriptor)
        if (!referencedClass.isReference || referencedClass.isArray) return InstanceFieldLookup.NoSuchField(requested)
        if (!hierarchy.isLoadable(referencedClass)) return InstanceFieldLookup.NoSuchClass(referencedClass)
        var current: VirtualType? = referencedClass
        while (current != null) {
            val definingClass = current
            val wanted = ClassIndex.fieldDescriptorOf(definingClass, name, fieldTypeDescriptor)
            declaredInstanceFields(definingClass).firstOrNull { it.descriptor == wanted }?.let { declared ->
                return InstanceFieldLookup.Resolved(
                    definingClass,
                    declared.descriptor,
                    declared.type,
                    Modifier.isVolatile(declared.modifiers),
                )
            }
            current = try {
                hierarchy.superclassOf(definingClass)
            } catch (_: ClassLoadFailure) {
                return InstanceFieldLookup.NoSuchClass(definingClass)
            }
        }
        return InstanceFieldLookup.NoSuchField(requested)
    }

    /**
     * Every instance cell a new object owns, inherited declarations included, each holding its declared type's
     * default. Null when the class or a superclass is absent.
     */
    fun instanceFieldDefaults(type: VirtualType): Map<String, Value>? {
        if (!type.isReference || type.isArray || !hierarchy.isLoadable(type)) return null
        val chain = mutableListOf<VirtualType>()
        var current: VirtualType? = type
        while (current != null) {
            chain += current
            current = try {
                hierarchy.superclassOf(current)
            } catch (_: ClassLoadFailure) {
                return null
            }
        }
        val fields = LinkedHashMap<String, Value>()
        for (declaringClass in chain.asReversed()) {
            for (field in declaredInstanceFields(declaringClass)) {
                fields[field.descriptor] = defaultValueOf(field.type)
            }
        }
        return fields
    }

    /**
     * [type]'s own declared static fields in dex order, each with its compile-time literal kept apart from the type
     * default ([StaticFieldDeclaration]). An inherited static belongs to its declaring class, so ancestors' fields
     * are not repeated. Empty for a primitive, an array, or an absent class: each contributes no statics.
     */
    fun declaredStaticFields(type: VirtualType): List<StaticFieldDeclaration> {
        if (!type.isReference || type.isArray) return emptyList()
        return snapshotFields(type, static = true).map { field ->
            val fieldType = VirtualType.of(field.type)
            StaticFieldDeclaration(
                descriptor = ClassIndex.fieldDescriptorOf(VirtualType.of(field.definingClass), field.name, field.type),
                type = fieldType,
                literal = literalOf(field, fieldType),
                modifiers = field.accessFlags,
            )
        }
    }

    /** [type]'s own declared instance fields, in dex order. */
    fun declaredInstanceFields(type: VirtualType): List<InstanceFieldDeclaration> {
        if (!type.isReference || type.isArray) return emptyList()
        return snapshotFields(type, static = false).map { field ->
            InstanceFieldDeclaration(
                descriptor = ClassIndex.fieldDescriptorOf(VirtualType.of(field.definingClass), field.name, field.type),
                type = VirtualType.of(field.type),
                modifiers = field.accessFlags,
            )
        }
    }

    /** [type]'s declared static and instance fields with exact access metadata. */
    fun declaredFields(type: VirtualType): List<FieldDeclaration> =
        declaredStaticFields(type).map { FieldDeclaration.of(type, it.descriptor, it.type, it.modifiers) } +
            declaredInstanceFields(type).map { FieldDeclaration.of(type, it.descriptor, it.type, it.modifiers) }

    private sealed interface SnapshotMethod {
        val declaration: ImmutableMethod

        class Application(val definition: ApplicationMethodDefinition) : SnapshotMethod {
            override val declaration get() = definition.declaration
        }

        class Framework(val definition: FrameworkMethodDefinition) : SnapshotMethod {
            override val declaration get() = definition.declaration
        }
    }

    private class SnapshotMethodSearch(
        val method: SnapshotMethod?,
        val missingClass: Boolean,
        val defaultConflict: Boolean = false,
    )

    /**
     * JVMS 5.4.3.3 over the snapshot: the class and its superclasses first, then the maximally-specific
     * superinterface methods, which exclude private and static interface methods. A single non-abstract maximal
     * method is the answer; several are a [SnapshotMethodSearch.defaultConflict]; with none, an abstract maximal
     * method answers.
     */
    private fun findSnapshotMethod(definingClass: VirtualType, descriptor: String): SnapshotMethodSearch {
        val interfaces = ArrayDeque<String>()
        var owner: String? = definingClass.descriptor
        while (owner != null) {
            declaredSnapshotMethod(owner, descriptor)?.let { return SnapshotMethodSearch(it, false) }
            val declaration = hierarchy.declarations[owner] ?: return SnapshotMethodSearch(null, true)
            declaration.interfaces.forEach(interfaces::addLast)
            owner = declaration.superclass
        }

        val candidates = LinkedHashMap<String, SnapshotMethod>()
        val visitedInterfaces = mutableSetOf<String>()
        var missingClass = false
        while (interfaces.isNotEmpty()) {
            val current = interfaces.removeFirst()
            if (!visitedInterfaces.add(current)) continue
            declaredSnapshotMethod(current, descriptor)
                ?.takeIf { !AccessFlags.PRIVATE.isSet(it.declaration.accessFlags) }
                ?.takeIf { !AccessFlags.STATIC.isSet(it.declaration.accessFlags) }
                ?.let { candidates[current] = it }
            val declaration = hierarchy.declarations[current]
            if (declaration == null) {
                missingClass = true
            } else {
                declaration.interfaces.forEach(interfaces::addLast)
            }
        }
        val maximal = candidates.filterKeys { candidate ->
            candidates.keys.none { other -> other != candidate && candidate in hierarchy.declaredAncestors(other) }
        }.values
        val nonAbstract = maximal.filterNot { AccessFlags.ABSTRACT.isSet(it.declaration.accessFlags) }
        return when (nonAbstract.size) {
            0 -> SnapshotMethodSearch(maximal.firstOrNull(), missingClass)
            1 -> SnapshotMethodSearch(nonAbstract.single(), missingClass)
            else -> SnapshotMethodSearch(nonAbstract.first(), missingClass, defaultConflict = true)
        }
    }

    private fun declaredSnapshotMethod(owner: String, descriptor: String): SnapshotMethod? {
        snapshot.applicationClass(owner)?.methodsByDescriptor?.get(descriptor)?.let {
            return SnapshotMethod.Application(it)
        }
        return snapshot.frameworkCatalog.declaredMethod(owner, descriptor)?.let(SnapshotMethod::Framework)
    }

    private fun lookupDeclaredMethod(definingClass: VirtualType, descriptor: String): MethodLookup {
        if (!hierarchy.isLoadable(definingClass)) return MethodLookup.NoSuchClass
        if (!definingClass.isReference || definingClass.isArray) return lookupMethod(definingClass, descriptor)
        return declaredSnapshotMethod(definingClass.descriptor, descriptor)?.let(::lookupOf) ?: MethodLookup.NoSuchMethod
    }

    private fun lookupOf(method: SnapshotMethod): MethodLookup {
        val resolved = resolvedSnapshotMethod(method)
        val view = when (method) {
            is SnapshotMethod.Application ->
                method.definition.body?.let { MethodView.of(method.definition, snapshot.programRevision) }
            is SnapshotMethod.Framework ->
                method.definition.body?.let { MethodView.of(method.definition, snapshot.programRevision) }
        }
        return if (view != null) MethodLookup.Runnable(view, resolved) else MethodLookup.NoBody(resolved)
    }

    private fun resolvedSnapshotMethod(
        method: SnapshotMethod,
        definingClass: VirtualType = VirtualType.of(method.declaration.definingClass),
    ): ResolvedMethod = ResolvedMethod(
        MethodDescriptor(
            definingClass,
            method.declaration.name,
            method.declaration.parameterTypes.joinToString(separator = "", prefix = "(", postfix = ")") +
                method.declaration.returnType,
        ),
        VirtualType.of(method.declaration.returnType),
        method.declaration.parameterTypes.map { VirtualType.of(it.toString()) },
        Modifier.isSynchronized(method.declaration.accessFlags) ||
            AccessFlags.DECLARED_SYNCHRONIZED.isSet(method.declaration.accessFlags),
    )

    private fun runnable(view: MethodView): MethodLookup.Runnable {
        val descriptor = view.signature.substringAfter("->")
        val name = descriptor.substringBefore('(')
        val proto = descriptor.substring(name.length)
        val parameterTypes = view.parameters.drop(if (view.isStatic) 0 else 1).map(ParameterSlot::type)
        return MethodLookup.Runnable(
            view,
            ResolvedMethod(MethodDescriptor(view.definingClass, name, proto), view.returnType, parameterTypes, view.isSynchronized),
        )
    }

    /** Dex 037 `invoke-super` against an interface method selects the most specific inherited default. */
    private fun resolveInterfaceSuper(callerClass: VirtualType, target: MethodDescriptor): InvocationLookup {
        val candidates = LinkedHashMap<String, MethodView>()
        for (name in hierarchy.declaredAncestors(callerClass.descriptor).sorted()) {
            val type = VirtualType.of(name)
            if (hierarchy.isFrameworkClass(type) || !hierarchy.isInterface(type)) continue
            val belowTarget = try {
                hierarchy.isSubtypeOf(type, target.definingClass)
            } catch (_: ClassLoadFailure) {
                false
            }
            if (!belowTarget) continue
            val lookup = lookupDeclaredMethod(type, target.descriptor)
            if (lookup is MethodLookup.Runnable) candidates[lookup.view.signature] = lookup.view
        }
        val mostSpecific = candidates.values.filter { candidate ->
            candidates.values.none { other ->
                other !== candidate && candidate.definingClass.descriptor in hierarchy.declaredAncestors(other.definingClass.descriptor)
            }
        }
        return when (mostSpecific.size) {
            1 -> InvocationLookup.Resolved(runnable(mostSpecific.single()))
            0 -> InvocationLookup.Resolved(lookupDeclaredMethod(target.definingClass, target.descriptor))
            else -> InvocationLookup.Ambiguous(mostSpecific.mapTo(linkedSetOf()) { it.signature })
        }
    }

    /**
     * The method each instantiable application subtype of [declaredType] selects, keyed by signature. A bodyless
     * or conflicting selection is a target like any other.
     */
    private fun dispatchCandidates(declaredType: VirtualType, descriptor: String): LinkedHashMap<String, MethodLookup> {
        val candidates = LinkedHashMap<String, MethodLookup>()
        for (type in hierarchy.instantiableApplicationSubtypes(declaredType)) {
            val lookup = lookupMethod(type, descriptor)
            selectedSignature(lookup)?.let { candidates.putIfAbsent(it, lookup) }
        }
        return candidates
    }

    /**
     * Framework classes, bundled or newer on the device, can extend or implement a non-final framework type. They
     * cannot name an application type.
     */
    private fun frameworkSubtypesCanReceive(declaredType: VirtualType): Boolean =
        hierarchy.isFrameworkClass(declaredType) && !hierarchy.isFinal(declaredType)

    private fun selectedSignature(lookup: MethodLookup): String? {
        val method = when (lookup) {
            is MethodLookup.Runnable -> lookup.method
            is MethodLookup.NoBody -> lookup.method
            is MethodLookup.DefaultConflict -> lookup.method
            MethodLookup.NoSuchClass, MethodLookup.NoSuchMethod -> return null
        }
        return "${method.target.definingClass.descriptor}->${method.target.descriptor}"
    }

    private fun snapshotFields(type: VirtualType, static: Boolean): List<Field> {
        val application = snapshot.applicationClass(type.descriptor)
        if (application != null) {
            val fields = if (static) application.staticFields else application.instanceFields
            return fields.map(ApplicationFieldDefinition::declaration)
        }
        val framework = snapshot.frameworkCatalog.classDefinition(type.descriptor) ?: return emptyList()
        return (if (static) framework.staticFields else framework.instanceFields).toList()
    }

    /**
     * The dex-encoded compile-time constant behind `.field public static x:I = 0x42`, or null when the field has
     * none. Only primitive and string constants are literals; any other encoded kind answers null, the same value
     * as the reference type's default.
     */
    private fun literalOf(field: Field, type: VirtualType): Value? {
        val encoded = field.initialValue ?: return null
        val raw: Any? = when (encoded.valueType) {
            ValueType.BYTE -> (encoded as ByteEncodedValue).value
            ValueType.SHORT -> (encoded as ShortEncodedValue).value
            ValueType.CHAR -> (encoded as CharEncodedValue).value
            ValueType.INT -> (encoded as IntEncodedValue).value
            ValueType.LONG -> (encoded as LongEncodedValue).value
            ValueType.FLOAT -> (encoded as FloatEncodedValue).value
            ValueType.DOUBLE -> (encoded as DoubleEncodedValue).value
            ValueType.STRING -> (encoded as StringEncodedValue).value
            ValueType.BOOLEAN -> (encoded as BooleanEncodedValue).value
            else -> return null
        }
        return Value.Known(raw, type)
    }
}
