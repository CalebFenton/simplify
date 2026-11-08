package org.cf.smalivm.core

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.api.assertThrows

class FieldGetAccessMatrixTest {
    private val classIndex = CoreTester.classIndex
    private val fieldGet = FieldGetEmulator(classIndex, ReflectionPolicy())

    @TestFactory
    fun accessMatrix(): List<DynamicTest> = buildList {
        for (case in accessCases) {
            check(case.legacyIds.size == case.variants.size)
            for ((variant, legacyId) in case.variants.zip(case.legacyIds)) {
                when (case.expected) {
                    ExpectedAccess.ALLOWED -> add(
                        accessTest(case, legacyId, variant, case.receiverType, ExpectedAccess.ALLOWED),
                    )
                    ExpectedAccess.DENIED -> add(
                        accessTest(case, legacyId, variant, case.receiverType, ExpectedAccess.DENIED),
                    )
                    ExpectedAccess.PROTECTED_RECEIVER_RULE -> {
                        add(accessTest(case, legacyId, variant, CHILD, ExpectedAccess.ALLOWED))
                        add(accessTest(case, legacyId, variant, DECLARING, ExpectedAccess.DENIED))
                    }
                }
            }
        }
    }

    @Test
    fun realPublicLookupAndGetReturnsTheGuestInstanceValue() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "readPublicInstance()I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(0x20, INTEGER), graph.outcomes.single().returnedValue())
    }

    /**
     * `Field.get` returns a wide static boxed in its wrapper (`java.lang.reflect.Field.get`), one reference
     * that fits the last register of the frame, whether the value is a literal, a default, or set by `<clinit>`.
     */
    @Test
    fun realStaticGetOfAWideFieldReturnsItsBoxedValue() {
        val cases = listOf(
            "readLong()" to 0x1122334455667788L,
            "readDouble()" to 2.5,
            "readDefaultLong()" to 0L,
            "readClinitLong()" to 7L,
        )
        for ((method, expected) in cases) {
            val graph = CoreTester.execute(WIDE_STATIC.descriptor, "${method}Ljava/lang/Object;")

            assertEquals(CompletionStatus.Complete, graph.completion(), method)
            assertEquals(
                Value.Known(expected, virtualTypeOfHostClass(expected.javaClass)),
                graph.outcomes.single().returnedValue(),
                method,
            )
        }
    }

    @Test
    fun realDeniedPrivateLookupAndGetRoutesIllegalAccessByTypeOnly() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "readPrivateInstanceDenied()I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun realAliasObservesSetAccessibleOnTheSameTrackedFieldRef() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "readPrivateInstanceWithAliasOverride()I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(0x22, INTEGER), graph.outcomes.single().returnedValue())
    }

    @Test
    fun realStaticOverrideInitializesTheDeclaringGuestClassBeforeReading() {
        val view = view(UNRELATED, "readInitializedStaticWithOverride()I")
        val vm = VmServices(classIndex)
        val session = vm.newSession(view)
        val root = session.frontier.single()

        val graph = session.run()
        val getStates = graph.addresses()
            .flatMap(graph::nodePile)
            .filter { it.invocationTarget() == FIELD_GET }
            .map { it.frame.initState(STATIC_INIT_FIELD) }

        assertEquals(Value.Known(0x55, INTEGER), graph.outcomes.single().returnedValue())
        assertEquals(listOf(ClassInitState.UNINITIALIZED, ClassInitState.INITIALIZED), getStates)
        assertEquals(ClassInitState.INITIALIZED, session.currentNode!!.frame.initState(STATIC_INIT_FIELD))
        assertTrue(allNodes(root).any { it.activation.classInitContext?.activeInitializer?.type == STATIC_INIT_FIELD })
    }

    @Test
    fun duplicateLookupGetsAFreshRefWithoutSharingAccessibleOverride() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "duplicateLookupDoesNotShareOverride()I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun branchOverrideKeepsEnabledAndDisabledSiblingHeapsIsolated() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "branchOverride(I)I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(Value.Known(0x13, INTEGER)), graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value })
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun callCompletionPartitionsIncompatibleAccessibilityBeforeCallerGet() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "branchOverrideThroughCall(I)I")
        val getNodes = graph.addresses()
            .flatMap(graph::nodePile)
            .filter { it.invocationTarget() == FIELD_GET }
        val firstAttempts = getNodes.filter { it.frame.initState(DECLARING) == ClassInitState.UNINITIALIZED }
        val accessibilities = firstAttempts.map { node ->
            val ref = (node.frame.peek(0) as Value.Known).raw as HeapRef
            node.frame.trackedFieldAt(ref)!!.accessibility
        }.toSet()

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(2, firstAttempts.size, "enabled and disabled callee returns must resume as separate callers")
        assertEquals(setOf(FieldAccessibility.LANGUAGE_CHECKS, FieldAccessibility.OVERRIDDEN), accessibilities)
        assertTrue(FieldAccessibility.UNKNOWN !in accessibilities)
        assertEquals(listOf(Value.Known(0x13, INTEGER)), graph.outcomes.filterIsInstance<Outcome.Returned>().map { it.value })
        assertEquals(listOf(ILLEGAL_ACCESS.descriptor), CoreTester.escapingTypes(graph))
    }

    @Test
    fun samePackageProtectedRealFlowIsAllowed() {
        val graph = CoreTester.execute(SAME_PACKAGE.descriptor, "readProtectedInstance()I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(Value.Known(0x21, INTEGER), graph.outcomes.single().returnedValue())
    }

    @Test
    fun inheritedPublicLookupRetainsTheOriginalDeclaringClass() {
        val result = lookup(CHILD, "publicInstanceField", CLASS_GET_FIELD)
        val operation = (result as IntrinsicCallResult).operation as IntrinsicOperation.AllocateTrackedField

        assertEquals(DECLARING, operation.metadata.declaringType)
        assertEquals("Lorg/cf/smalivm/DummyClass;->publicInstanceField:I", operation.metadata.descriptor)
        assertEquals(TrackedFieldDomain.Virtual, operation.metadata.domain)
    }

    @Test
    fun publicLookupSearchesInheritedInterfacesBeforeTheSuperclass() {
        val result = lookup(INTERFACE_IMPL, "interfaceValue", CLASS_GET_FIELD)
        val operation = (result as IntrinsicCallResult).operation as IntrinsicOperation.AllocateTrackedField

        assertEquals(INTERFACE, operation.metadata.declaringType)
        assertEquals("${INTERFACE.descriptor}->interfaceValue:I", operation.metadata.descriptor)
    }

    @Test
    fun publicLookupReportsPrivateFieldAsMissingBeforeFieldGet() {
        val result = lookup(DECLARING, "privateInstanceField", CLASS_GET_FIELD)

        assertEquals(NO_SUCH_FIELD, (result as SynchronousCallResult.TargetThrew).throwable.type)
    }

    @Test
    fun unknownFieldReceiverReturnsConservativeUnknownInsteadOfHostFailure() {
        val result = fieldGet.execute(
            emulatorCall(
                receiver = EmulatorValue.Unknown(FIELD),
                argument = EmulatorValue.Unknown(OBJECT),
                caller = UNRELATED,
            ),
        )

        assertEquals(IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(OBJECT)), result)
    }

    @Test
    fun realLookupAndGetOnNullRoutesGuestNpeWithoutHostFailure() {
        val graph = CoreTester.execute(UNRELATED.descriptor, "readPublicInstanceFromNull()I")

        assertEquals(CompletionStatus.Complete, graph.completion())
        assertEquals(listOf(NULL_POINTER.descriptor), CoreTester.escapingTypes(graph))
        assertTrue(graph.addresses().flatMap(graph::nodePile).all { it.hostFailure == null })
    }

    @Test
    fun virtualInstanceWrongTargetTypeRoutesGuestIllegalArgument() {
        val metadata = virtualField(FieldVisibility.PUBLIC, FieldStorage.INSTANCE)
        val result = fieldGet.execute(
            emulatorCall(
                receiver = TrackedFieldEmulatorValue(metadata, FieldAccessibility.LANGUAGE_CHECKS),
                argument = EmulatorValue.TrackedReference(UNRELATED),
                caller = UNRELATED,
            ),
        )

        assertEquals(ILLEGAL_ARGUMENT, (result as SynchronousCallResult.TargetThrew).throwable.type)
    }

    @Test
    fun trackedFieldHeapIdentitySharesAliasesButNotSiblingVersionsOrDuplicateLookups() {
        val frame = Frame.entryFrame(view(UNRELATED, "dummyMethod()V"))
        val metadata = virtualField(FieldVisibility.PRIVATE, FieldStorage.STATIC)
        val first = frame.allocateTrackedField(metadata)
        val alias = Value.Known(first.raw, FIELD)
        val sibling = frame.copy()
        val duplicate = frame.allocateTrackedField(metadata)
        val firstRef = first.raw as HeapRef

        assertTrue(frame.setTrackedFieldAccessibility(firstRef, FieldAccessibility.OVERRIDDEN))

        assertEquals(first, alias)
        assertNotEquals(first.raw, duplicate.raw)
        assertEquals(FieldAccessibility.OVERRIDDEN, frame.trackedFieldAt(firstRef)!!.accessibility)
        assertEquals(FieldAccessibility.LANGUAGE_CHECKS, sibling.trackedFieldAt(firstRef)!!.accessibility)
    }

    @Test
    fun setAccessibleFalseRequestsRestorationOfLanguageChecks() {
        val metadata = virtualField(FieldVisibility.PRIVATE, FieldStorage.INSTANCE)
        val result = FieldSetAccessibleEmulator().execute(
            EmulatorInvocation(
                MethodDescriptor(FIELD, "setAccessible", "(Z)V"),
                UNRELATED,
                InvokeKind.VIRTUAL,
                VirtualType.of("V"),
                listOf(VirtualType.of("Z")),
                TrackedFieldEmulatorValue(metadata, FieldAccessibility.OVERRIDDEN),
                listOf(EmulatorValue.Known(VirtualType.of("Z"), false)),
                initializedSnapshot(),
            ),
        )

        assertEquals(
            IntrinsicCallResult(
                IntrinsicOperation.SetTrackedFieldAccessible(metadata, FieldAccessibility.LANGUAGE_CHECKS),
            ),
            result,
        )
    }

    @Test
    fun setAccessibleRegistrationDeclaresWeakEffectAndTheLaterAliasReadUsesTheOverride() {
        val vm = VmServices(classIndex)
        val target = MethodDescriptor(FIELD, "setAccessible", "(Z)V")
        val registration = requireNotNull(vm.invocationServices.emulators.find(target))

        val graph = CoreTester.execute(UNRELATED.descriptor, "readPrivateInstanceWithAliasOverride()I")
        val setAccessibleNode = graph.addresses()
            .flatMap(graph::nodePile)
            .single { it.invocationTarget() == FIELD_SET_ACCESSIBLE }

        assertEquals(SideEffectLevel.WEAK, registration.effect)
        assertEquals(SideEffectLevel.WEAK, setAccessibleNode.sideEffectLevel)
        assertEquals(Value.Known(0x22, INTEGER), graph.outcomes.single().returnedValue())
    }

    @Test
    fun executableHeapMergeRejectsAccessibilityDisagreementBeforeInvocation() {
        val first = HeapStore(HeapIds())
        val metadata = virtualField(FieldVisibility.PRIVATE, FieldStorage.STATIC)
        val ref = first.allocateTrackedField(metadata)
        val sibling = first.copy()
        assertTrue(first.setTrackedFieldAccessibility(ref, FieldAccessibility.OVERRIDDEN))

        assertThrows<IllegalArgumentException> {
            HeapStore.merge(listOf(first, sibling), classIndex)
        }
    }

    @Test
    fun hostReadRechecksPolicyInsteadOfTrustingConstructedMetadata() {
        val metadata = hostMetadata(HostFieldFixture::class.java.getDeclaredField("mutable"))
        val result = FieldGetEmulator(classIndex, ReflectionPolicy()).execute(
            emulatorCall(
                receiver = TrackedFieldEmulatorValue(metadata, FieldAccessibility.OVERRIDDEN),
                argument = EmulatorValue.Known(HOST_FIXTURE, HostFieldFixture()),
                caller = UNRELATED,
            ),
        )

        assertEquals(IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(BUILDER)), result)
    }

    @Test
    fun hostLookupRetainsTheOriginalDeclaringClassToken() {
        val hostType = virtualTypeOfHostClass(HostFieldFixture::class.java)
        val policy = ReflectionPolicy(safeClasses = setOf(hostType))
        val lookup = FieldLookupEmulator(classIndex, policy)
        val result = lookup.execute(
            EmulatorInvocation(
                MethodDescriptor(CLASS, "getDeclaredField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;"),
                UNRELATED,
                InvokeKind.VIRTUAL,
                FIELD,
                listOf(STRING),
                EmulatorValue.Known(CLASS, HostFieldFixture::class.java),
                listOf(EmulatorValue.Known(STRING, "mutable")),
                initializedSnapshot(),
            ),
        )
        val metadata = ((result as IntrinsicCallResult).operation as IntrinsicOperation.AllocateTrackedField).metadata

        assertSame(HostFieldFixture::class.java, (metadata.domain as TrackedFieldDomain.Host).declaringClass)
    }

    @Test
    fun hostPackageEqualityRequiresTheSameDefiningLoader() {
        val reflected = FieldHostFixture::class.java.getDeclaredField("packageValue")
        val metadata = hostMetadata(reflected)
        val policy = ReflectionPolicy(safeClasses = setOf(metadata.declaringType))
        val result = FieldGetEmulator(classIndex, policy).execute(
            emulatorCall(
                receiver = TrackedFieldEmulatorValue(metadata, FieldAccessibility.LANGUAGE_CHECKS),
                argument = EmulatorValue.Known(HOST_PACKAGE_FIXTURE, FieldHostFixture()),
                caller = HOST_PACKAGE_CALLER,
            ),
        )

        assertEquals(ILLEGAL_ACCESS, (result as SynchronousCallResult.TargetThrew).throwable.type)
    }

    @Test
    fun realHostLookupAllocatesTrackedFieldAndPreservesMutableResultIdentity() {
        val policy = ReflectionPolicy(safeClasses = setOf(HOST_FIXTURE))
        val vm = VmServices(classIndex, policy)
        val entry = view(HOST_PACKAGE_CALLER, "readHostMutable(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(entry)
        val fixtureRef = frame.allocateHostObject(HOST_FIXTURE, HostFieldFixture())
        frame.assign(entry.parameters.single().register, Value.Known(fixtureRef, HOST_FIXTURE))
        val session = vm.newSession(entry, entryFrame = frame)

        val graph = session.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
        val terminal = session.currentNode!!.frame
        val receiver = terminal.hostObjectAt(fixtureRef) as HostFieldFixture

        assertSame(receiver.mutable, terminal.hostObjectAt(returned.raw as HeapRef))
    }

    @Test
    fun sameDescriptorFromAnotherLoaderCannotReuseHostPolicyAuthorization() {
        val duplicateClass = duplicateFieldHostFixtureClass()
        val metadata = hostMetadata(duplicateClass.getDeclaredField("publicStatic"))
        val exactField = StaticFieldDescriptor(metadata.declaringType, metadata.name, metadata.fieldType)
        val policies = listOf(
            ReflectionPolicy(safeClasses = setOf(metadata.declaringType)),
            ReflectionPolicy(exactSafeStaticFields = setOf(exactField)),
        )

        for (policy in policies) {
            val result = FieldGetEmulator(classIndex, policy).execute(
                emulatorCall(
                    receiver = TrackedFieldEmulatorValue(metadata, FieldAccessibility.LANGUAGE_CHECKS),
                    argument = EmulatorValue.Unknown(OBJECT),
                    caller = UNRELATED,
                ),
            )
            assertEquals(IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(BUILDER)), result)
        }
    }

    @Test
    fun unsafeInitializerAnywhereInTheModelledChainIncludingInterfacesDeniesTheStaticRead() {
        // Comparable declares no default method, so a JVM would not initialize it for Integer (JLS 12.4.1);
        // authorization still checks every superinterface the class model gives Integer.
        val metadata = hostMetadata(java.lang.Integer::class.java.getDeclaredField("MAX_VALUE"))
        val comparable = VirtualType.of("Ljava/lang/Comparable;")
        val unsafeInterface = ReflectionPolicy(
            exactUnsafeMethods = setOf(MethodDescriptor(comparable, "<clinit>", "()V")),
            safeClasses = setOf(metadata.declaringType),
        )
        val authorized = ReflectionPolicy(safeClasses = setOf(metadata.declaringType))

        val denied = hostStaticGet(metadata, unsafeInterface)
        val allowed = hostStaticGet(metadata, authorized)

        assertEquals(IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(INTEGER)), denied)
        // Integer.MAX_VALUE is 0x7fffffff (JLS 4.2.1).
        assertEquals(
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.Reference(2147483647, INTEGER))),
            allowed,
        )
    }

    @Test
    fun hostStaticOfAClassAbsentFromTheClassModelIsNotAuthorized() {
        val metadata = hostMetadata(HostFieldFixture::class.java.getDeclaredField("staticMutable"))

        val result = hostStaticGet(metadata, ReflectionPolicy(safeClasses = setOf(metadata.declaringType)))

        assertEquals(IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(BUILDER)), result)
    }

    @Test
    fun authorizedHostStaticReadIgnoresReceiverAndImportsMutableIdentity() {
        val metadata = hostMetadata(java.util.Collections::class.java.getDeclaredField("EMPTY_LIST"))
        val policy = ReflectionPolicy(safeClasses = setOf(metadata.declaringType))
        val vm = VmServices(classIndex, policy)
        val entry = view(SPINE, "throwTwoHandlers(Ljava/lang/Object;)Ljava/lang/Object;")
        val frame = Frame.entryFrame(entry)
        frame.assign(0, frame.allocateTrackedField(metadata))
        frame.assign(1, Value.Unknown(OBJECT))
        val session = invokeFieldGetOnce(vm, entry, frame)

        val graph = session.run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known

        assertSame(java.util.Collections.EMPTY_LIST, session.currentNode!!.frame.hostObjectAt(returned.raw as HeapRef))
    }

    @Test
    fun overriddenReadOfAHostFieldTheJvmDoesNotOpenReturnsUnknown() {
        // After setAccessible(true) the guest's read of the private field is legal, so the guest gets a value.
        // The test JVM opens only java.base/java.lang to this analyser, so it cannot read java.util.ArrayList.size
        // and the only sound answer is an unknown int.
        val metadata = hostMetadata(java.util.ArrayList::class.java.getDeclaredField("size"))
        val policy = ReflectionPolicy(safeClasses = setOf(metadata.declaringType))

        val result = FieldGetEmulator(classIndex, policy).execute(
            emulatorCall(
                receiver = TrackedFieldEmulatorValue(metadata, FieldAccessibility.OVERRIDDEN),
                argument = EmulatorValue.Known(metadata.declaringType, arrayListOf("element")),
                caller = UNRELATED,
            ),
        )

        assertEquals(IntrinsicCallResult(IntrinsicOperation.ReturnUnknown(INTEGER)), result)
    }

    @Test
    fun trackedMetadataRejectsContradictoryDescriptorAndStorage() {
        assertThrows<IllegalArgumentException> {
            TrackedFieldValue(
                DECLARING,
                "value",
                "Lwrong/Owner;->value:I",
                INT,
                Modifier.PUBLIC,
                FieldVisibility.PUBLIC,
                FieldStorage.INSTANCE,
                TrackedFieldDomain.Virtual,
            )
        }
        assertThrows<IllegalArgumentException> {
            virtualField(
                FieldVisibility.PUBLIC,
                FieldStorage.INSTANCE,
                modifiers = Modifier.PUBLIC or Modifier.STATIC,
            )
        }
    }

    private fun assertAllowed(case: AccessCase, variant: FieldVariant, receiverType: VirtualType) {
        val metadata = virtualField(variant.visibility, variant.storage)
        val expected = matrixValue(variant)
        val graph = executeMatrixRead(case.caller, metadata, receiverType, expected)

        assertEquals(CompletionStatus.Complete, graph.completion(), case.authority)
        assertEquals(Value.Known(expected, INTEGER), graph.outcomes.single().returnedValue(), case.authority)
    }

    private fun assertDenied(case: AccessCase, variant: FieldVariant, receiverType: VirtualType) {
        val metadata = virtualField(variant.visibility, variant.storage)
        val result = fieldGet.execute(
            emulatorCall(
                receiver = TrackedFieldEmulatorValue(metadata, FieldAccessibility.LANGUAGE_CHECKS),
                argument = EmulatorValue.TrackedReference(receiverType),
                caller = case.caller,
            ),
        )

        assertEquals(ILLEGAL_ACCESS, (result as SynchronousCallResult.TargetThrew).throwable.type, case.authority)
    }

    private fun accessTest(
        case: AccessCase,
        legacyId: String,
        variant: FieldVariant,
        receiverType: VirtualType,
        expected: ExpectedAccess,
    ): DynamicTest {
        val condition = "caller=${case.caller.descriptor}, receiver=${receiverType.descriptor}, " +
            "visibility=${variant.visibility}, storage=${variant.storage}"
        val ruleId = case.target.substringAfter('[').substringBeforeLast(']')
        val name = "rule=$ruleId; legacy=$legacyId; condition=$condition; expected=$expected"
        return DynamicTest.dynamicTest(name) {
            when (expected) {
                ExpectedAccess.ALLOWED -> assertAllowed(case, variant, receiverType)
                ExpectedAccess.DENIED -> assertDenied(case, variant, receiverType)
                ExpectedAccess.PROTECTED_RECEIVER_RULE -> error("protected receiver rule needs a concrete result")
            }
        }
    }

    private fun executeMatrixRead(
        caller: VirtualType,
        metadata: TrackedFieldValue,
        receiverType: VirtualType,
        expected: Int,
    ): ExecutionGraph {
        val vm = VmServices(classIndex)
        val entry = view(caller, "matrix(Ljava/lang/Object;Ljava/lang/Object;)I")
        val frame = Frame.entryFrame(entry)
        frame.assign(0, frame.allocateTrackedField(metadata))
        if (metadata.storage == FieldStorage.INSTANCE) {
            val receiver = frame.allocateObject(
                receiverType,
                requireNotNull(classIndex.instanceFieldDefaults(receiverType)),
            )
            val receiverRef = receiver.raw as HeapRef
            assertTrue(frame.writeField(receiverRef, metadata.descriptor, Value.Known(expected, INT)))
            frame.assign(1, receiver)
        } else {
            frame.completeClassInit(metadata.declaringType)
            frame.writeStaticField(metadata.descriptor, Value.Known(expected, INT))
            frame.assign(1, Value.Known(null, OBJECT))
        }
        return invokeFieldGetOnce(vm, entry, frame).run()
    }

    private fun matrixValue(variant: FieldVariant): Int =
        (variant.visibility.ordinal + 1) * 100 + if (variant.storage == FieldStorage.STATIC) 2 else 1

    private fun hostStaticGet(
        metadata: TrackedFieldValue,
        policy: ReflectionPolicy,
    ): SynchronousCallResult = FieldGetEmulator(classIndex, policy).execute(
        emulatorCall(
            receiver = TrackedFieldEmulatorValue(metadata, FieldAccessibility.LANGUAGE_CHECKS),
            argument = EmulatorValue.Unknown(OBJECT),
            caller = UNRELATED,
        ),
    )

    private fun duplicateFieldHostFixtureClass(): Class<*> {
        val resource = "/${FieldHostFixture::class.java.name.replace('.', '/')}.class"
        val bytes = requireNotNull(FieldHostFixture::class.java.getResourceAsStream(resource)).use { it.readBytes() }
        val loader = object : ClassLoader(null) {
            fun defineCopy(): Class<*> = defineClass(FieldHostFixture::class.java.name, bytes, 0, bytes.size)
        }
        return loader.defineCopy()
    }

    private fun allNodes(root: ExecutionNode): List<ExecutionNode> {
        val nodes = mutableListOf<ExecutionNode>()
        val pending = ArrayDeque<ExecutionNode>()
        pending.add(root)
        while (pending.isNotEmpty()) {
            val node = pending.removeFirst()
            nodes += node
            node.children.forEach(pending::addLast)
        }
        return nodes
    }

    private fun lookup(type: VirtualType, name: String, target: MethodDescriptor): SynchronousCallResult {
        val lookup = FieldLookupEmulator(classIndex, ReflectionPolicy())
        return lookup.execute(
            EmulatorInvocation(
                target,
                UNRELATED,
                InvokeKind.VIRTUAL,
                FIELD,
                listOf(STRING),
                EmulatorValue.Known(CLASS, classIndex.loadClassConstant(type.descriptor)),
                listOf(EmulatorValue.Known(STRING, name)),
                initializedSnapshot(),
            ),
        )
    }

    private fun emulatorCall(
        receiver: EmulatorValue,
        argument: EmulatorValue,
        caller: VirtualType,
    ): EmulatorInvocation = EmulatorInvocation(
        FIELD_GET,
        caller,
        InvokeKind.VIRTUAL,
        OBJECT,
        listOf(OBJECT),
        receiver,
        listOf(argument),
        initializedSnapshot(),
    )

    private fun initializedSnapshot(): ClassInitStatusSnapshot =
        ClassInitStore().also { it.complete(DECLARING) }.statusSnapshot()

    private fun virtualField(
        visibility: FieldVisibility,
        storage: FieldStorage,
        modifiers: Int = visibility.modifier or if (storage == FieldStorage.STATIC) Modifier.STATIC else 0,
    ): TrackedFieldValue {
        val prefix = when (visibility) {
            FieldVisibility.PUBLIC -> "public"
            FieldVisibility.PROTECTED -> "protected"
            FieldVisibility.PACKAGE -> "packagePrivate"
            FieldVisibility.PRIVATE -> "private"
        }
        val name = "$prefix${if (storage == FieldStorage.STATIC) "Static" else "Instance"}Field"
        return TrackedFieldValue(
            DECLARING,
            name,
            "${DECLARING.descriptor}->$name:I",
            INT,
            modifiers,
            visibility,
            storage,
            TrackedFieldDomain.Virtual,
        )
    }

    private fun hostMetadata(field: Field): TrackedFieldValue {
        val declaringType = virtualTypeOfHostClass(field.declaringClass)
        val fieldType = virtualTypeOfHostClass(field.type)
        return TrackedFieldValue(
            declaringType,
            field.name,
            ClassIndex.fieldDescriptorOf(declaringType, field.name, fieldType.descriptor),
            fieldType,
            field.modifiers,
            visibilityOf(field.modifiers),
            if (Modifier.isStatic(field.modifiers)) FieldStorage.STATIC else FieldStorage.INSTANCE,
            TrackedFieldDomain.Host(field.declaringClass),
        )
    }

    private fun view(type: VirtualType, method: String): MethodView =
        classIndex.requireMethodView(type.descriptor, method)

    private fun invokeFieldGetOnce(vm: VmServices, entry: MethodView, frame: Frame): ExecutionSession {
        val next = entry.nextAddress(0)
        return vm.newSession(entry, Limits(), frame) { address, view, resolver ->
            when {
                view.signature != entry.signature -> OpBuilder.build(address, view, resolver)
                address == 0 -> InvokeTestOp(address, FIELD_GET, intArrayOf(0, 1))
                address == next -> ReturnResultTestOp(address)
                else -> OpBuilder.build(address, view, resolver)
            }
        }
    }

    private data class FieldVariant(val visibility: FieldVisibility, val storage: FieldStorage)

    private data class AccessCase(
        val target: String,
        val authority: String,
        val legacyIds: Set<String>,
        val caller: VirtualType,
        val variants: List<FieldVariant>,
        val expected: ExpectedAccess,
        val receiverType: VirtualType = DECLARING,
    )

    private enum class ExpectedAccess { ALLOWED, DENIED, PROTECTED_RECEIVER_RULE }

    private class InvokeTestOp(
        address: Int,
        val target: MethodDescriptor,
        private val registers: IntArray,
    ) : Op(address) {
        override val name: String = "field-get-test-invoke"
        override fun execute(frame: Frame): OpExecution =
            OpExecution.single(OpArm.Invoke(target, registers, InvokeKind.VIRTUAL))
    }

    private class ReturnResultTestOp(address: Int) : Op(address) {
        override val name: String = "field-get-test-return"
        override fun execute(frame: Frame): OpExecution =
            OpExecution.single(OpArm.Return.fromValue(frame.takeResult()))
    }

    internal companion object {
        val INT = VirtualType.of("I")
        /** `Field.get` wraps a primitive field's value in its wrapper class (`java.lang.reflect.Field.get`). */
        val INTEGER = VirtualType.of("Ljava/lang/Integer;")
        val OBJECT = VirtualType.OBJECT
        val STRING = VirtualType.of("Ljava/lang/String;")
        val CLASS = VirtualType.of("Ljava/lang/Class;")
        val FIELD = VirtualType.of("Ljava/lang/reflect/Field;")
        val BUILDER = VirtualType.of("Ljava/lang/StringBuilder;")
        val ILLEGAL_ACCESS = VirtualType.of("Ljava/lang/IllegalAccessException;")
        val ILLEGAL_ARGUMENT = VirtualType.of("Ljava/lang/IllegalArgumentException;")
        val NULL_POINTER = VirtualType.of("Ljava/lang/NullPointerException;")
        val NO_SUCH_FIELD = VirtualType.of("Ljava/lang/NoSuchFieldException;")
        val DECLARING = VirtualType.of("Lorg/cf/smalivm/DummyClass;")
        val NEST = VirtualType.of("Lorg/cf/smalivm/DummyClass\$DummyInnerClass;")
        val SAME_PACKAGE = VirtualType.of("Lorg/cf/smalivm/SamePackageClass;")
        val CHILD = VirtualType.of("Lorg/cf/smalivm/unrelated/DummyChildClass;")
        val UNRELATED = VirtualType.of("Lorg/cf/smalivm/unrelated/UnrelatedClass;")
        val INTERFACE = VirtualType.of("Lorg/cf/smalivm/FieldInterface;")
        val INTERFACE_IMPL = VirtualType.of("Lorg/cf/smalivm/FieldInterfaceImpl;")
        val HOST_PACKAGE_CALLER = VirtualType.of("Lorg/cf/smalivm/core/HostPackageCaller;")
        val STATIC_INIT_FIELD = VirtualType.of("Lorg/cf/smalivm/StaticInitField;")
        val WIDE_STATIC = VirtualType.of("Lorg/cf/smalivm/WideStaticField;")
        val SPINE = VirtualType.of("Lspine/spine;")
        val HOST_FIXTURE = virtualTypeOfHostClass(HostFieldFixture::class.java)
        val HOST_PACKAGE_FIXTURE = virtualTypeOfHostClass(FieldHostFixture::class.java)

        val FIELD_GET = MethodDescriptor(FIELD, "get", "(Ljava/lang/Object;)Ljava/lang/Object;")
        val FIELD_SET_ACCESSIBLE = MethodDescriptor(FIELD, "setAccessible", "(Z)V")
        val CLASS_GET_FIELD = MethodDescriptor(CLASS, "getField", "(Ljava/lang/String;)Ljava/lang/reflect/Field;")


        private val accessCases = listOf(
            ownerCase("sameDeclaringClassAllAccess", "same declaring class", "P11-FG-FWC", DECLARING),
            ownerCase("sameNestAllAccess", "same nest", "P11-FG-FWIC", NEST),
            case("publicUniversal", "public access is universal", ids("P11-FG-FWCCUP-001", "P11-FG-FWCCUP-007"), CHILD, both(FieldVisibility.PUBLIC), ExpectedAccess.ALLOWED, receiver = CHILD),
            case("protectedCrossPackageReceiver", "JLS protected cross-package receiver restriction", ids("P11-FG-FWCCUP-002"), CHILD, instance(FieldVisibility.PROTECTED), ExpectedAccess.PROTECTED_RECEIVER_RULE, receiver = CHILD),
            case("crossPackagePackageDenied", "cross-package package access is denied", ids("P11-FG-FWCCUP-003", "P11-FG-FWCCUP-009"), CHILD, both(FieldVisibility.PACKAGE), ExpectedAccess.DENIED),
            case("crossPackagePrivateDenied", "cross-package private access is denied", ids("P11-FG-FWCCUP-005", "P11-FG-FWCCUP-011"), CHILD, both(FieldVisibility.PRIVATE), ExpectedAccess.DENIED),
            case("protectedStaticSubclass", "protected static subclass access has no receiver restriction", ids("P11-FG-FWCCUP-008"), CHILD, static(FieldVisibility.PROTECTED), ExpectedAccess.ALLOWED),
            case("samePackagePublic", "same-package public access", ids("P11-FG-FSPC-001", "P11-FG-FSPC-007"), SAME_PACKAGE, both(FieldVisibility.PUBLIC), ExpectedAccess.ALLOWED),
            case("samePackageProtected", "JLS same-package protected access is allowed", ids("P11-FG-FSPC-002", "P11-FG-FSPC-008"), SAME_PACKAGE, both(FieldVisibility.PROTECTED), ExpectedAccess.ALLOWED),
            case("samePackagePackage", "same-package package access is allowed", ids("P11-FG-FSPC-004", "P11-FG-FSPC-010"), SAME_PACKAGE, both(FieldVisibility.PACKAGE), ExpectedAccess.ALLOWED),
            case("samePackagePrivateDenied", "same-package private access is denied", ids("P11-FG-FSPC-005", "P11-FG-FSPC-011"), SAME_PACKAGE, both(FieldVisibility.PRIVATE), ExpectedAccess.DENIED),
            case("unrelatedPublic", "unrelated public access", ids("P11-FG-FWUC-001", "P11-FG-FWUC-008"), UNRELATED, both(FieldVisibility.PUBLIC), ExpectedAccess.ALLOWED),
            case("unrelatedProtectedDenied", "unrelated protected access is denied", ids("P11-FG-FWUC-002", "P11-FG-FWUC-009"), UNRELATED, both(FieldVisibility.PROTECTED), ExpectedAccess.DENIED),
            case("unrelatedPackageDenied", "unrelated package access is denied", ids("P11-FG-FWUC-004", "P11-FG-FWUC-011"), UNRELATED, both(FieldVisibility.PACKAGE), ExpectedAccess.DENIED),
            case("unrelatedPrivateDenied", "unrelated private access is denied", ids("P11-FG-FWUC-006", "P11-FG-FWUC-013"), UNRELATED, both(FieldVisibility.PRIVATE), ExpectedAccess.DENIED),
        )


        private fun ownerCase(
            target: String,
            authority: String,
            prefix: String,
            caller: VirtualType,
        ): AccessCase = AccessCase(
            matrixTarget(target),
            authority,
            (1..8).mapTo(linkedSetOf()) { "$prefix-${it.toString().padStart(3, '0')}" },
            caller,
            FieldVisibility.entries.map { FieldVariant(it, FieldStorage.INSTANCE) } +
                FieldVisibility.entries.map { FieldVariant(it, FieldStorage.STATIC) },
            ExpectedAccess.ALLOWED,
        )

        private fun case(
            target: String,
            authority: String,
            ids: Set<String>,
            caller: VirtualType,
            variants: List<FieldVariant>,
            expected: ExpectedAccess,
            receiver: VirtualType = DECLARING,
        ) = AccessCase(
            matrixTarget(target),
            authority,
            ids,
            caller,
            variants,
            expected,
            receiver,
        )

        private fun ids(vararg values: String): Set<String> = values.toSet()
        private fun matrixTarget(name: String): String = "FieldGetAccessMatrixTest.accessMatrix[$name]"
        private fun instance(visibility: FieldVisibility) = listOf(FieldVariant(visibility, FieldStorage.INSTANCE))
        private fun static(visibility: FieldVisibility) = listOf(FieldVariant(visibility, FieldStorage.STATIC))
        private fun both(visibility: FieldVisibility) = instance(visibility) + static(visibility)
    }
}

private class HostFieldFixture {
    @JvmField
    val mutable = StringBuilder("host-value")

    companion object {
        @JvmField
        val staticMutable = StringBuilder("host-static")
    }
}

private fun Outcome.returnedValue(): Value? = (this as Outcome.Returned).value

private fun ExecutionNode.invocationTarget(): MethodDescriptor? {
    val invoke = op as? org.cf.smalivm.core.op.InvokeOp ?: return null
    return (invoke.execute(frame).arms.single() as OpArm.Invoke).target
}
