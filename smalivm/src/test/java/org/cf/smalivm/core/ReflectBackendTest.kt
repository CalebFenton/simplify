package org.cf.smalivm.core

import java.util.ArrayList
import java.util.UUID
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ReflectBackendTest {
    private val classIndex = CoreTester.classIndex
    private val fixtureType = type("Lorg/cf/smalivm/core/ReflectBackendFixture;")
    private val intType = type("I")
    private val objectType = VirtualType.OBJECT
    private val stringType = type("Ljava/lang/String;")
    private val builderType = type("Ljava/lang/StringBuilder;")

    @Test
    fun backendBoundaryReceivesNoSchedulerStateOrIdentityTypes() {
        val forbidden = setOf(
            Value::class.java,
            HeapRef::class.java,
            Frame::class.java,
            ExecutionSession::class.java,
            ExecutionNode::class.java,
        )

        ReflectBackend::class.java.declaredMethods.forEach { method ->
            assertTrue(method.parameterTypes.none { it in forbidden }, method.toString())
        }
    }

    @Test
    fun defaultInvokerUsesEmulatorGuestReflectionThenOpaqueAndGuestPreventsReflectionProbe() {
        ReflectionExternalSentinel.reset()
        val target = method(fixtureType, "guestWins", "()I")
        val vm = vm(exactSafe = setOf(target))

        val graph = invoke(vm, target, InvokeKind.STATIC).run()

        assertEquals(Value.Known(7, intType), graph.outcomes.single().returnedValue())
        assertEquals(0, ReflectionExternalSentinel.externalMutations)
        assertNotSame(graph.nodePile(0).single().activation, graph.nodePile(0).single().children.single().activation)
    }

    @Test
    fun exactStaticVirtualAndInterfaceTargetsInvokeWithoutCompatibleOverloadSelection() {
        val overload = method(fixtureType, "overload", "(I)Ljava/lang/String;")
        val staticGraph = invoke(vm(exactSafe = setOf(overload)), overload, InvokeKind.STATIC, listOf(Value.Known(6, intType))).run()
        assertEquals(Value.Known("int:6", stringType), staticGraph.outcomes.single().returnedValue())

        val integerType = type("Ljava/lang/Integer;")
        val intValue = method(integerType, "intValue", "()I")
        val virtualGraph = invoke(VmServices(classIndex), intValue, InvokeKind.VIRTUAL, listOf(Value.Known(42, integerType))).run()
        assertEquals(Value.Known(42, intType), virtualGraph.outcomes.single().returnedValue())

        val listType = type("Ljava/util/List;")
        val size = method(listType, "size", "()I")
        val list = arrayListOf("a", "b")
        val interfaceCall = hostReceiverCall(VmServices(classIndex), size, InvokeKind.INTERFACE, listType, list)
        assertEquals(Value.Known(2, intType), interfaceCall.first.run().outcomes.single().returnedValue())
    }

    @Test
    fun exactNumberDescriptorDoesNotSelectTheCompatibleIntOverload() {
        val numberType = type("Ljava/lang/Number;")
        val target = method(fixtureType, "overload", "(Ljava/lang/Number;)Ljava/lang/String;")
        val graph = invoke(
            vm(exactSafe = setOf(target)),
            target,
            InvokeKind.STATIC,
            listOf(Value.Known(6, numberType)),
        ).run()

        assertEquals(Value.Known("number:6", stringType), graph.outcomes.single().returnedValue())
    }

    @Test
    fun covariantImplementationAndBridgeSelectBothExactReturnDescriptors() {
        val fixtureType = type("Lorg/cf/smalivm/core/ReflectCovariantFixture;")
        val targets = listOf(
            method(fixtureType, "value", "()Ljava/lang/String;"),
            method(fixtureType, "value", "()Ljava/lang/CharSequence;"),
        )

        targets.forEach { target ->
            val owner = vm(exactSafe = setOf(target))
            val (session, _) = hostReceiverCall(
                owner,
                target,
                InvokeKind.VIRTUAL,
                fixtureType,
                ReflectCovariantFixture(),
            )

            assertEquals(
                Value.Known("covariant", stringType),
                session.run().outcomes.single().returnedValue(),
                target.toString(),
            )
        }
    }

    @Test
    fun mismatchedJvmReturnDescriptorFailsBeforeTargetExecution() {
        ReflectionExternalSentinel.reset()
        val target = method(fixtureType, "mismatchedReturn", "()Ljava/lang/CharSequence;")
        val session = invoke(vm(exactSafe = setOf(target)), target, InvokeKind.STATIC)

        val node = session.step()!!

        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), node.outcome)
        assertTrue(node.hostFailure is NoSuchMethodException)
        assertEquals(0, ReflectionExternalSentinel.externalMutations)
    }

    @Test
    fun instancePrimitiveReturnUsesExactReceiverInvocation() {
        val integerType = type("Ljava/lang/Integer;")
        val target = method(integerType, "intValue", "()I")
        val graph = invoke(VmServices(classIndex), target, InvokeKind.VIRTUAL, listOf(Value.Known(42, integerType))).run()

        assertEquals(Value.Known(42, intType), graph.outcomes.single().returnedValue())
    }

    @Test
    fun canCastIntegerToByte() {
        val byteType = type("B")
        val target = method("Ljava/lang/Byte;", "valueOf", "(B)Ljava/lang/Byte;")
        val graph = invoke(VmServices(classIndex), target, InvokeKind.STATIC, listOf(Value.Known(6.toByte(), byteType))).run()

        assertEquals(Value.Known(6.toByte(), type("Ljava/lang/Byte;")), graph.outcomes.single().returnedValue())
    }

    @Test
    fun canGetShortValueOfShort() {
        val shortType = type("S")
        val target = method("Ljava/lang/Short;", "valueOf", "(S)Ljava/lang/Short;")
        val graph = invoke(VmServices(classIndex), target, InvokeKind.STATIC, listOf(Value.Known(5.toShort(), shortType))).run()

        assertEquals(Value.Known(5.toShort(), type("Ljava/lang/Short;")), graph.outcomes.single().returnedValue())
    }

    @Test
    fun exactConstructorOverloadInstallsHostObjectAtStableReceiverRefWithoutAResult() {
        val constructorType = type("Lorg/cf/smalivm/core/ReflectOverloadedConstructor;")
        val numberType = type("Ljava/lang/Number;")
        val target = method(constructorType, "<init>", "(Ljava/lang/Number;)V")
        val vm = vm(safeClasses = setOf(constructorType))
        val (session, ref, probedId) = constructorSession(vm, target, listOf(Value.Known(9, numberType)))

        val graph = session.run()
        val terminal = session.currentNode!!.frame
        val raw = terminal.hostObjectAt(ref) as ReflectOverloadedConstructor

        assertEquals(
            listOf(Outcome.Returned(null)),
            graph.outcomes,
            graph.addresses().flatMap(graph::nodePile).joinToString { "${it.address}:${it.hostFailure}" },
        )
        assertEquals("number:9", raw.selected)
        assertEquals(ref, (terminal.peek(0) as Value.Known).raw)
        assertEquals(probedId + 1, probeNextHeapId(terminal))
        assertEquals(constructorType, terminal.heapObject(ref)!!.type)
    }

    @Test
    fun constructorReusesDetachedArgumentAndReceiverRefsThroughLaterIdentityReturns() {
        val holderType = type("Lorg/cf/smalivm/core/ReflectArgumentHolder;")
        val constructor = method(holderType, "<init>", "(Ljava/lang/StringBuilder;)V")
        val getter = method(holderType, "get", "()Ljava/lang/StringBuilder;")
        val identity = method(fixtureType, "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;")
        val owner = vm(
            exactSafe = setOf(constructor, getter, identity),
            safeClasses = setOf(holderType),
        )
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val receiver = frame.allocateObject(holderType, emptyMap())
        val receiverRef = receiver.raw as HeapRef
        val argumentRef = frame.allocateHostObject(builderType, StringBuilder("argument"))
        frame.assign(0, receiver)
        frame.assign(1, Value.Known(argumentRef, builderType))

        val constructorSession = invoke(
            owner,
            constructor,
            InvokeKind.DIRECT,
            frame = frame,
            registers = intArrayOf(0, 1),
        )
        constructorSession.run()
        val constructed = constructorSession.currentNode!!.frame

        constructed.assign(0, Value.Known(receiverRef, holderType))
        val getterSession = invoke(owner, getter, InvokeKind.VIRTUAL, frame = constructed, registers = intArrayOf(0))
        val getterResult = getterSession.run().outcomes.single().returnedValue() as Value.Known
        assertEquals(argumentRef, getterResult.raw)

        val afterGetter = getterSession.currentNode!!.frame
        afterGetter.assign(0, Value.Known(receiverRef, objectType))
        val identitySession = invoke(owner, identity, InvokeKind.STATIC, frame = afterGetter, registers = intArrayOf(0))
        val identityResult = identitySession.run().outcomes.single().returnedValue() as Value.Known

        assertEquals(receiverRef, identityResult.raw)
        assertEquals(receiverRef, (identitySession.currentNode!!.frame.peek(0) as Value.Known).raw)
    }

    @Test
    fun booleanConstructorUpdatesExistingReceiver() {
        val booleanType = type("Ljava/lang/Boolean;")
        assertConstructor(booleanType, "(Z)V", listOf(Value.Known(true, type("Z"))), true)
    }

    @Test
    fun uuidConstructorBindsTwoWideArguments() {
        val uuidType = type("Ljava/util/UUID;")
        assertConstructor(
            uuidType,
            "(JJ)V",
            listOf(Value.Known(0x42L, type("J")), Value.Known(0x1337L, type("J"))),
            UUID(0x42L, 0x1337L),
        )
    }

    @Test
    fun characterConstructorUpdatesExistingReceiver() {
        val characterType = type("Ljava/lang/Character;")
        assertConstructor(characterType, "(C)V", listOf(Value.Known('a', type("C"))), 'a')
    }

    @Test
    fun unsupportedDirectAndSuperCallsDeclineBeforeExecution() {
        ReflectionExternalSentinel.reset()
        val target = method(fixtureType, "mutateExternal", "()V")
        val backend = ReflectBackend(ReflectionPolicy(exactSafeMethods = setOf(target)), classIndex.currentResolver())

        for (kind in listOf(InvokeKind.DIRECT, InvokeKind.SUPER)) {
            val probe = backend.probe(metadata(target, kind), MethodLookup.NoSuchMethod)
            assertTrue(probe is BackendProbe.Declined, kind.toString())
        }
        assertEquals(0, ReflectionExternalSentinel.externalMutations)
    }

    @Test
    fun unsupportedDirectAndSuperDeclineBeforeDefaultPolicyDenialSoLaterBackendRuns() {
        val target = method(fixtureType, "touch", "()I")
        for (kind in listOf(InvokeKind.DIRECT, InvokeKind.SUPER)) {
            val later = CountingExecuteBackend()
            val reflection = ReflectBackend(ReflectionPolicy(), classIndex.currentResolver())
            val invoker = invokerOf(classIndex, listOf(reflection, later))
            val prepared = PreparedInvocation(
                target,
                fixtureType,
                kind,
                intType,
                emptyList(),
                Value.Known("receiver", fixtureType),
                emptyList(),
            )
            val lookup = MethodLookup.NoBody(ResolvedMethod(target, intType, emptyList(), isSynchronized = false))

            val plan = invoker.planOn(InvocationPreparation.Ready(prepared, lookup, emptySet()), Frame.entryFrame(callerView()))

            assertTrue(plan is InvocationPlan.Execute, kind.toString())
            assertEquals(1, later.probes, kind.toString())
        }
    }

    @Test
    fun nullReceiverRoutesBeforeReflection() {
        ReflectionExternalSentinel.reset()
        val target = method(fixtureType, "touch", "()I")
        val graph = invoke(
            vm(exactSafe = setOf(target)),
            target,
            InvokeKind.VIRTUAL,
            listOf(Value.Known(null, fixtureType)),
        ).run()

        val thrown = (graph.outcomes.single() as Outcome.Threw).exception
        assertEquals(type("Ljava/lang/NullPointerException;"), thrown.type)
        assertEquals(0, ReflectionExternalSentinel.externalMutations)
    }

    @Test
    fun allowedUnknownAndUnmaterializableInputsDeclineToOpaqueAfterGuestDeclines() {
        val target = method(fixtureType, "overload", "(I)Ljava/lang/String;")
        val unknown = invoke(vm(exactSafe = setOf(target)), target, InvokeKind.STATIC, listOf(Value.Unknown(intType))).run()
        assertEquals(Value.Unknown(stringType), unknown.outcomes.single().returnedValue())
        assertEquals(SideEffectLevel.STRONG, unknown.sideEffectLevel(0))
        assertTrue(unknown.nodePile(0).single().invocationDiagnostic!!.contains("reflection"))

        val objectTarget = method(fixtureType, "acceptString", "(Ljava/lang/String;)I")
        val owner = vm(exactSafe = setOf(objectTarget))
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val tracked = frame.allocateObject(type("Lhash_code;"), emptyMap())
        frame.assign(0, Value.Known(tracked.raw, stringType))
        val unmaterializable = invoke(owner, objectTarget, InvokeKind.STATIC, frame = frame).run()
        assertEquals(Value.Unknown(intType), unmaterializable.outcomes.single().returnedValue())
        assertNull(unmaterializable.nodePile(0).single().hostFailure)
    }

    @Test
    fun safeFixtureMutatesOnlyVmOwnedReceiverAndArguments() {
        ReflectionExternalSentinel.reset()
        val target = method(fixtureType, "mutateOwned", "(Ljava/lang/StringBuilder;Ljava/util/List;)V")
        val owner = vm(exactSafe = setOf(target))
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val originalBuilder = StringBuilder("vm")
        val originalList = arrayListOf<String>()
        val builderRef = frame.allocateHostObject(builderType, originalBuilder)
        val listType = type("Ljava/util/List;")
        val listRef = frame.allocateHostObject(listType, originalList)
        frame.assign(0, Value.Known(builderRef, builderType))
        frame.assign(1, Value.Known(listRef, listType))

        val session = invoke(owner, target, InvokeKind.STATIC, frame = frame, registers = intArrayOf(0, 1))
        session.run()
        val terminal = session.currentNode!!.frame

        assertEquals("vm-owned", terminal.hostObjectAt(builderRef).toString())
        assertEquals(listOf("owned"), terminal.hostObjectAt(listRef))
        assertEquals("vm", originalBuilder.toString())
        assertTrue(originalList.isEmpty())
        assertEquals(0, ReflectionExternalSentinel.externalMutations)
    }

    @Test
    fun unsafeExternalStaticMutatorIsOpaqueStrongAndDoesNotRun() {
        ReflectionExternalSentinel.reset()
        val target = method(fixtureType, "mutateExternal", "()V")
        val owner = vm(exactUnsafe = setOf(target), safeClasses = setOf(fixtureType))

        val graph = invoke(owner, target, InvokeKind.STATIC).run()

        assertEquals(
            listOf(Outcome.Returned(null)),
            graph.outcomes,
            graph.addresses().flatMap(graph::nodePile).joinToString { "${it.address}:${it.hostFailure}" },
        )
        assertEquals(SideEffectLevel.STRONG, graph.sideEffectLevel(0))
        assertEquals(0, ReflectionExternalSentinel.externalMutations)
    }

    @Test
    fun defaultDeniedMethodIsTerminalAndDoesNotReachHostExecution() {
        ReflectionExternalSentinel.reset()
        val target = method(fixtureType, "mutateExternal", "()V")

        val graph = invoke(vm(), target, InvokeKind.STATIC).run()

        assertEquals(listOf(Outcome.Returned(null)), graph.outcomes)
        assertEquals(SideEffectLevel.STRONG, graph.sideEffectLevel(0))
        assertTrue(graph.nodePile(0).single().invocationDiagnostic!!.contains("DEFAULT_DENY"))
        assertEquals(0, ReflectionExternalSentinel.externalMutations)
    }

    @Test
    fun defaultPolicyDeniesMethodInvokeOnAReflectedHostMethod() {
        // Method.invoke runs whatever Method the guest supplies, so authorizing it would bypass every policy
        // decision on the inner target. Running String.length here would return the boxed 3.
        val methodInvoke = method("Ljava/lang/reflect/Method;", "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
        val graph = VmServices(classIndex).newSession(
            view("Lmethod_reflector_test;", "invokeStringLengthReflectively()Ljava/lang/Object;"),
        ).run()

        assertEquals(listOf(Outcome.Returned(Value.Unknown(objectType))), graph.outcomes)
        assertTrue(
            graph.addresses().flatMap(graph::nodePile).any { node ->
                node.invocationDiagnostic?.contains("reflection policy denied $methodInvoke by DEFAULT_DENY") == true
            },
        )
    }

    @Test
    fun authorizedMethodInvokeRunsOnlyAReflectedMethodThePolicyAlsoAuthorizes() {
        val methodType = type("Ljava/lang/reflect/Method;")
        val methodInvoke = method(methodType, "invoke", "(Ljava/lang/Object;[Ljava/lang/Object;)Ljava/lang/Object;")
        val reflected = method(fixtureType, "mutateExternal", "()V")
        val cases = listOf(
            emptySet<MethodDescriptor>() to 0,
            setOf(reflected) to 1,
        )
        for ((reflectedAuthorization, expectedMutations) in cases) {
            ReflectionExternalSentinel.reset()
            val vm = vm(exactSafe = setOf(methodInvoke) + reflectedAuthorization)
            val frame = Frame.entryFrame(callerView())
            val hostMethod = ReflectBackendFixture::class.java.getDeclaredMethod("mutateExternal")
            frame.assign(0, Value.Known(frame.allocateHostObject(methodType, hostMethod), methodType))
            frame.assign(1, Value.Known(null, objectType))
            frame.assign(2, Value.Known(null, type("[Ljava/lang/Object;")))

            val graph = invoke(vm, methodInvoke, InvokeKind.VIRTUAL, frame = frame, registers = intArrayOf(0, 1, 2)).run()

            assertEquals(expectedMutations, ReflectionExternalSentinel.externalMutations, reflectedAuthorization.toString())
            if (expectedMutations == 0) {
                assertEquals(listOf(Outcome.Aborted(AbortReason.HOST_FAILURE)), graph.outcomes)
                assertTrue(graph.nodePile(0).single().hostFailure is SecurityException)
            } else {
                assertEquals(listOf(Outcome.Returned(Value.Known(null, objectType))), graph.outcomes)
            }
        }
    }

    @Test
    fun exactUnsafeClassInitializerBlocksStaticAndConstructorBeforeHostClassResolution() {
        ReflectionExternalSentinel.reset()
        val staticType = type("Lorg/cf/smalivm/core/ReflectStaticInitProbe;")
        val staticTarget = method(staticType, "value", "()I")
        val staticPolicy = ReflectionPolicy(
            exactUnsafeMethods = setOf(method(staticType, "<clinit>", "()V")),
            exactSafeMethods = setOf(staticTarget),
        )
        val staticGraph = invoke(VmServices(classIndex, staticPolicy), staticTarget, InvokeKind.STATIC).run()
        assertEquals(Value.Unknown(intType), staticGraph.outcomes.single().returnedValue())
        assertEquals(0, ReflectionExternalSentinel.staticInitializations)

        val constructorType = type("Lorg/cf/smalivm/core/ReflectConstructorInitProbe;")
        val constructor = method(constructorType, "<init>", "()V")
        val constructorPolicy = ReflectionPolicy(
            exactUnsafeMethods = setOf(method(constructorType, "<clinit>", "()V")),
            exactSafeMethods = setOf(constructor),
            safeClasses = setOf(constructorType),
        )
        val (session, _, _) = constructorSession(VmServices(classIndex, constructorPolicy), constructor, emptyList())
        session.run()
        assertEquals(0, ReflectionExternalSentinel.constructorInitializations)
    }

    @Test
    fun exactUnsafeSuperclassInitializerBlocksChildStaticAndConstructorBeforeHostResolution() {
        ReflectionExternalSentinel.reset()
        val parentType = type("Lorg/cf/smalivm/core/ReflectUnsafeParent;")
        val childType = type("Lorg/cf/smalivm/core/ReflectSafeChild;")
        val staticTarget = method(childType, "value", "()I")
        val constructor = method(childType, "<init>", "()V")
        val policy = ReflectionPolicy(
            exactUnsafeMethods = setOf(method(parentType, "<clinit>", "()V")),
            exactSafeMethods = setOf(staticTarget, constructor),
            safeClasses = setOf(childType),
        )
        val owner = VmServices(classIndex, policy)

        val staticGraph = invoke(owner, staticTarget, InvokeKind.STATIC).run()
        assertEquals(Value.Unknown(intType), staticGraph.outcomes.single().returnedValue())
        val (constructorSession, _, _) = constructorSession(owner, constructor, emptyList())
        constructorSession.run()

        assertEquals(0, ReflectionExternalSentinel.parentInitializations)
        assertEquals(0, ReflectionExternalSentinel.childInitializations)
    }

    @Test
    fun primitiveBoxedReferenceWideAndVoidReturnsKeepDeclaredAndRuntimeTypes() {
        val primitiveCases = listOf(
            Triple("returnBoolean", "()Z", Value.Known(true, type("Z"))),
            Triple("returnByte", "()B", Value.Known(2.toByte(), type("B"))),
            Triple("returnChar", "()C", Value.Known('c', type("C"))),
            Triple("returnShort", "()S", Value.Known(4.toShort(), type("S"))),
            Triple("returnInt", "()I", Value.Known(5, intType)),
            Triple("returnLong", "()J", Value.Known(6L, type("J"))),
            Triple("returnFloat", "()F", Value.Known(7.5f, type("F"))),
            Triple("returnDouble", "()D", Value.Known(8.5, type("D"))),
        )
        primitiveCases.forEach { (name, proto, expected) ->
            val target = method(fixtureType, name, proto)
            assertEquals(expected, invoke(vm(exactSafe = setOf(target)), target, InvokeKind.STATIC).run().outcomes.single().returnedValue())
        }

        val boxed = method(fixtureType, "returnBoxedByte", "()Ljava/lang/Byte;")
        assertEquals(
            Value.Known(11.toByte(), type("Ljava/lang/Byte;")),
            invoke(vm(exactSafe = setOf(boxed)), boxed, InvokeKind.STATIC).run().outcomes.single().returnedValue(),
        )

        val reference = method(fixtureType, "returnList", "()Ljava/util/List;")
        val referenceSession = invoke(vm(exactSafe = setOf(reference)), reference, InvokeKind.STATIC)
        val referenceGraph = referenceSession.run()
        val returned = referenceGraph.outcomes.single().returnedValue() as Value.Known
        assertEquals(type("Ljava/util/ArrayList;"), returned.type)
        assertEquals(type("Ljava/util/ArrayList;"), referenceSession.currentNode!!.frame.heapObject(returned.raw as HeapRef)!!.type)

        val voidTarget = method(fixtureType, "appendVoid", "(Ljava/lang/StringBuilder;)V")
        val (voidSession, ref) = hostArgumentCall(vm(exactSafe = setOf(voidTarget)), voidTarget, StringBuilder("v"), builderType)
        assertEquals(listOf(Outcome.Returned(null)), voidSession.run().outcomes)
        assertEquals("v-void", voidSession.currentNode!!.frame.hostObjectAt(ref).toString())
    }

    @Test
    fun reflectedTypedNullReturnKeepsTheDeclaredStringType() {
        val target = method(fixtureType, "returnNullString", "()Ljava/lang/String;")
        val returned = invoke(vm(exactSafe = setOf(target)), target, InvokeKind.STATIC)
            .run().outcomes.single().returnedValue()

        assertEquals(Value.Known(null, stringType), returned)
    }

    @Test
    fun invocationTargetExceptionIsUnwrappedToTheOriginalTargetThrowable() {
        val target = method(fixtureType, "throwProvided", "(Ljava/lang/Throwable;)V")
        val backend = ReflectBackend(ReflectionPolicy(exactSafeMethods = setOf(target)), classIndex.currentResolver())
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val throwableType = type("Ljava/lang/IllegalStateException;")
        val original = IllegalStateException("target")
        val value = frame.allocateThrowable(throwableType, original)
        val prepared = PreparedInvocation(target, fixtureType, InvokeKind.STATIC, type("V"), listOf(type("Ljava/lang/Throwable;")), null, listOf(value))
        val selected = SelectedHostCall.from(prepared, frame, classIndex.classLoader)
        val selectedThrowable = selected.arguments.single() as Throwable

        val result = backend.execute(selected) as SynchronousCallResult.TargetThrew
        selected.finalize(frame)

        assertSame(selectedThrowable, result.throwable.throwable)
        assertEquals(throwableType, result.throwable.type)
    }

    @Test
    fun targetExceptionFinalizesMutationWithoutFallback() {
        val target = method(fixtureType, "mutateThenThrow", "(Ljava/lang/StringBuilder;)V")
        val policy = ReflectionPolicy(exactSafeMethods = setOf(target))
        val reflection = ReflectBackend(policy, classIndex.currentResolver())
        val later = CountingBackend()
        val invoker = invokerOf(classIndex, listOf(reflection, later))
        val owner = VmServices(classIndex, policy)
        val entry = view("Lcalls/caller;", "callsThrowingCalleeWithCatch()I")
        val frame = Frame.entryFrame(entry)
        val original = StringBuilder("before")
        val ref = frame.allocateHostObject(builderType, original)
        frame.assign(0, Value.Known(ref, builderType))
        val session = ExecutionSession.of(entry, owner, Limits(), frame, ::realOrInjected, invoker)

        session.run()

        assertEquals("before-thrown", session.currentNode!!.frame.hostObjectAt(ref).toString())
        assertEquals("before", original.toString())
        assertEquals(0, later.probes)
    }

    @Test
    fun reflectionMachineryFailuresAreInfrastructureFailuresAndNeverFallback() {
        val cases = listOf(
            method(fixtureType, "missingOnHost", "()I") to NoSuchMethodException::class.java,
            method(fixtureType, "privateValue", "()I") to IllegalAccessException::class.java,
        )
        cases.forEach { (target, failureType) ->
            val backend = ReflectBackend(ReflectionPolicy(exactSafeMethods = setOf(target)), classIndex.currentResolver())
            val result = executeDirect(backend, target)
            assertTrue(result is SynchronousCallResult.InfrastructureFailure, target.toString())
            assertTrue(failureType.isInstance((result as SynchronousCallResult.InfrastructureFailure).cause), target.toString())
        }

        val mismatch = method(fixtureType, "acceptString", "(Ljava/lang/String;)I")
        val mismatchBackend = ReflectBackend(ReflectionPolicy(exactSafeMethods = setOf(mismatch)), classIndex.currentResolver())
        val mismatchResult = executeDirect(mismatchBackend, mismatch, listOf(Value.Known(7, stringType)))
        assertTrue((mismatchResult as SynchronousCallResult.InfrastructureFailure).cause is IllegalArgumentException)

        val missingClass = method("Lhost/DefinitelyAbsent;", "value", "()I")
        val absentBackend = ReflectBackend(ReflectionPolicy(exactSafeMethods = setOf(missingClass)), classIndex.currentResolver())
        val absent = executeDirect(absentBackend, missingClass)
        assertTrue((absent as SynchronousCallResult.InfrastructureFailure).cause is ClassNotFoundException)

        val later = CountingBackend()
        val policy = ReflectionPolicy(exactSafeMethods = setOf(cases.first().first))
        val owner = VmServices(classIndex, policy)
        val session = invoke(
            owner,
            cases.first().first,
            InvokeKind.STATIC,
            invoker = invokerOf(classIndex, listOf(ReflectBackend(policy, classIndex.currentResolver()), later)),
        )
        val node = session.step()!!
        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), node.outcome)
        assertTrue(node.hostFailure is NoSuchMethodException)
        assertEquals(0, later.probes)
    }

    @Test
    fun targetLinkageFailureIsClassifiedAsTargetAndVirtualMachineErrorEscapes() {
        val linkage = method(fixtureType, "throwLinkage", "()V")
        val backend = ReflectBackend(ReflectionPolicy(exactSafeMethods = setOf(linkage)), classIndex.currentResolver())
        val result = executeDirect(backend, linkage) as SynchronousCallResult.TargetThrew
        assertEquals(type("Ljava/lang/IllegalAccessError;"), result.throwable.type)

        val fatal = method(fixtureType, "throwFatal", "()V")
        val session = invoke(vm(exactSafe = setOf(fatal)), fatal, InvokeKind.STATIC)
        assertThrows<StackOverflowError> { session.step() }
        assertNull(session.currentNode!!.hostFailure)
    }

    @Test
    fun defaultEffectIsStrongAndPolicyCanCarryAProvenNarrowerMethodEffect() {
        val integerType = type("Ljava/lang/Integer;")
        val target = method(integerType, "intValue", "()I")
        val strong = invoke(VmServices(classIndex), target, InvokeKind.VIRTUAL, listOf(Value.Known(1, integerType))).run()
        assertEquals(SideEffectLevel.STRONG, strong.sideEffectLevel(0))

        val policy = ReflectionPolicy(
            exactSafeMethods = setOf(target),
            methodEffects = mapOf(target to SideEffectLevel.NONE),
        )
        val narrow = invoke(VmServices(classIndex, policy), target, InvokeKind.VIRTUAL, listOf(Value.Known(1, integerType))).run()
        assertEquals(SideEffectLevel.NONE, narrow.sideEffectLevel(0))
    }

    @Test
    fun exactMutationPreservesPolicyDeclaredNoneAndImportsChangedContents() {
        // Bug class: exact VM-owned mutation is mistaken for an unmodeled external effect and raised to WEAK.
        // Oracle: Plan 11 Task 42 keeps declared NONE when reconciliation proves Equal or Different modeled state.
        val target = method(fixtureType, "appendVoid", "(Ljava/lang/StringBuilder;)V")
        val policy = ReflectionPolicy(
            exactSafeMethods = setOf(target),
            methodEffects = mapOf(target to SideEffectLevel.NONE),
        )
        val (session, ref) = hostArgumentCall(VmServices(classIndex, policy), target, StringBuilder("v"), builderType)

        val graph = session.run()

        assertEquals("v-void", session.currentNode!!.frame.hostObjectAt(ref).toString())
        assertEquals(SideEffectLevel.NONE, graph.sideEffectLevel(0))
        assertEquals(SideEffectLevel.NONE, graph.nodePile(0).single().activation.declaredEffect)
    }

    @Test
    fun unprovableFinalGraphRaisesPolicyDeclaredNoneToStrong() {
        // Bug class: declared NONE survives when exact host-graph reconciliation cannot prove modeled state.
        // Oracle: Plan 11 raises Unprovable reconciliation to STRONG while retaining the detached graph and ref.
        val target = method(fixtureType, "identityObject", "(Ljava/lang/Object;)Ljava/lang/Object;")
        val policy = ReflectionPolicy(
            exactSafeMethods = setOf(target),
            methodEffects = mapOf(target to SideEffectLevel.NONE),
        )
        val owner = VmServices(classIndex, policy)
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val ref = frame.allocateHostObject(objectType, Any())
        frame.assign(0, Value.Known(ref, objectType))

        val graph = invoke(owner, target, InvokeKind.STATIC, frame = frame, registers = intArrayOf(0)).run()

        assertEquals(SideEffectLevel.STRONG, graph.sideEffectLevel(0))
        assertEquals(ref, (graph.outcomes.single().returnedValue() as Value.Known).raw)
    }

    @Test
    fun failedPrefinalizeConstructorInstallationBecomesHostFailureAfterFinalization() {
        // Bug class: finalization failure keeps the backend's declared NONE and understates an unmodeled result.
        // Oracle: Plan 11 Task 42 raises finalization failure to STRONG on the invoke and its activation.
        val constructorType = type("Lorg/cf/smalivm/core/ReflectOverloadedConstructor;")
        val target = method(constructorType, "<init>", "(I)V")
        lateinit var session: ExecutionSession
        val backend = object : SynchronousBackend {
            override val name = "invalid-constructor-result"
            override fun probe(call: BackendInvocation, lookup: MethodLookup) = BackendProbe.Execute(SideEffectLevel.NONE)
            override fun execute(call: SelectedHostCall): SynchronousCallResult =
                ConstructedHostObjectResult(session.currentNode!!.frame)
        }
        val invoker = invokerOf(classIndex, listOf(backend))
        val owner = VmServices(classIndex, ReflectionPolicy(exactSafeMethods = setOf(target)))
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val receiver = frame.allocateObject(constructorType, emptyMap())
        frame.assign(0, receiver)
        frame.assign(1, Value.Known(1, intType))
        val resume = entry.nextAddress(0)
        session = ExecutionSession.of(entry, owner, Limits(), frame, { address, view, index ->
            when {
                view.signature != entry.signature -> OpBuilder.build(address, view, index)
                address == 0 -> invokeOp(address, target, intArrayOf(0, 1), InvokeKind.DIRECT)
                address == resume -> returnResult(address)
                else -> OpBuilder.build(address, view, index)
            }
        }, invoker)

        val node = session.step()!!

        assertEquals(Outcome.Aborted(AbortReason.HOST_FAILURE), node.outcome)
        assertTrue(node.hostFailure is HostGraphOwnershipException)
        assertEquals(SideEffectLevel.STRONG, node.sideEffectLevel)
        assertEquals(SideEffectLevel.STRONG, node.activation.declaredEffect)
    }

    @Test
    fun successfulCallUsesNormalContinuation() {
        val stringLength = method("Ljava/lang/String;", "length", "()I")
        val session = invoke(VmServices(classIndex), stringLength, InvokeKind.VIRTUAL, listOf(Value.Known("four", stringType)))
        val root = session.frontier.single()

        val graph = session.run()

        assertEquals(Value.Known(4, intType), graph.outcomes.single().returnedValue())
        assertEquals(1, root.children.size)
        assertSame(root.activation, root.children.single().activation)
    }

    private fun assertConstructor(type: VirtualType, proto: String, arguments: List<Value>, expected: Any) {
        val target = method(type, "<init>", proto)
        val (session, ref, probedId) = constructorSession(VmServices(classIndex), target, arguments)
        val graph = session.run()
        val terminal = session.currentNode!!.frame

        assertEquals(
            listOf(Outcome.Returned(null)),
            graph.outcomes,
            graph.addresses().flatMap(graph::nodePile).joinToString { "${it.address}:${it.hostFailure}" },
        )
        assertEquals(ref, (terminal.peek(0) as Value.Known).raw)
        assertEquals(expected, terminal.hostObjectAt(ref))
        assertEquals(probedId + 1, probeNextHeapId(terminal))
        assertEquals(type, terminal.heapObject(ref)!!.type)
    }

    /**
     * A session constructing [target] on a fresh tracked receiver, that receiver, and the next heap id probed
     * after seeding. The probe consumes that id, so a construction that allocates nothing leaves the next
     * probe one higher.
     */
    private fun constructorSession(
        vm: VmServices,
        target: MethodDescriptor,
        arguments: List<Value>,
    ): Triple<ExecutionSession, HeapRef, Int> {
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val receiver = frame.allocateObject(target.definingClass, emptyMap())
        val ref = receiver.raw as HeapRef
        frame.assign(0, receiver)
        var register = 1
        arguments.forEach { argument ->
            frame.assign(register, argument)
            register += if (requireNotNull(argument.type).isWide) 2 else 1
        }
        val registers = IntArray(register) { it }
        val probedId = probeNextHeapId(frame)
        return Triple(invoke(vm, target, InvokeKind.DIRECT, frame = frame, registers = registers), ref, probedId)
    }

    private fun hostReceiverCall(
        vm: VmServices,
        target: MethodDescriptor,
        kind: InvokeKind,
        declaredType: VirtualType,
        raw: Any,
    ): Pair<ExecutionSession, HeapRef> {
        val entry = callerView()
        val frame = Frame.entryFrame(entry)
        val ref = frame.allocateHostObject(declaredType, raw)
        frame.assign(0, Value.Known(ref, declaredType))
        return invoke(vm, target, kind, frame = frame, registers = intArrayOf(0)) to ref
    }

    private fun hostArgumentCall(
        vm: VmServices,
        target: MethodDescriptor,
        raw: Any,
        declaredType: VirtualType,
    ): Pair<ExecutionSession, HeapRef> = hostReceiverCall(vm, target, InvokeKind.STATIC, declaredType, raw)

    private fun invoke(
        vm: VmServices,
        target: MethodDescriptor,
        kind: InvokeKind,
        values: List<Value> = emptyList(),
        frame: Frame? = null,
        registers: IntArray = IntArray(values.size) { it },
        invoker: Invoker? = null,
    ): ExecutionSession {
        val entry = callerView()
        val entryFrame = frame ?: Frame.entryFrame(entry).also { seeded ->
            values.forEachIndexed(seeded::assign)
        }
        val resume = entry.nextAddress(0)
        val opFactory: (Int, MethodView, ClassIndex) -> Op = { address, view, index ->
            when {
                view.signature != entry.signature -> OpBuilder.build(address, view, index)
                address == 0 -> invokeOp(address, target, registers, kind)
                address == resume -> returnResult(address)
                else -> OpBuilder.build(address, view, index)
            }
        }
        return if (invoker == null) {
            ExecutionSession.of(entry, vm, Limits(), entryFrame, opFactory)
        } else {
            ExecutionSession.of(entry, vm, Limits(), entryFrame, opFactory, invoker)
        }
    }

    private fun realOrInjected(address: Int, view: MethodView, index: ClassIndex): Op =
        if (view.signature == "Lcalls/caller;->callsThrowingCalleeWithCatch()I" && address == 0) {
            invokeOp(
                address,
                method(fixtureType, "mutateThenThrow", "(Ljava/lang/StringBuilder;)V"),
                intArrayOf(0),
                InvokeKind.STATIC,
            )
        } else {
            OpBuilder.build(address, view, index)
        }

    private fun executeDirect(
        backend: ReflectBackend,
        target: MethodDescriptor,
        arguments: List<Value> = emptyList(),
    ): SynchronousCallResult {
        val frame = Frame.entryFrame(callerView())
        val prepared = PreparedInvocation(
            target,
            fixtureType,
            InvokeKind.STATIC,
            type(target.proto.substringAfter(')')),
            parameterTypes(target.proto),
            null,
            arguments,
        )
        val selected = SelectedHostCall.from(prepared, frame, classIndex.classLoader)
        return backend.execute(selected).also { selected.finalize(frame) }
    }

    private fun metadata(target: MethodDescriptor, kind: InvokeKind) = BackendInvocation(
        target,
        fixtureType,
        kind,
        type(target.proto.substringAfter(')')),
        parameterTypes(target.proto),
        if (kind == InvokeKind.STATIC) null else ProbeArgument(fixtureType, true, true, false, true),
        emptyList(),
    )

    private fun parameterTypes(proto: String): List<VirtualType> {
        val descriptors = proto.substringAfter('(').substringBefore(')')
        val result = mutableListOf<VirtualType>()
        var index = 0
        while (index < descriptors.length) {
            val start = index
            while (descriptors[index] == '[') index++
            index = if (descriptors[index] == 'L') descriptors.indexOf(';', index) + 1 else index + 1
            result += type(descriptors.substring(start, index))
        }
        return result
    }

    private fun invokeOp(address: Int, target: MethodDescriptor, registers: IntArray, kind: InvokeKind) =
        object : Op(address) {
            override val name = "invoke-reflection-test"
            override fun execute(frame: Frame) =
                listOf(Transition.Invoke(target, registers, kind)).asOpExecution()
        }

    private fun returnResult(address: Int) = object : Op(address) {
        override val name = "return-reflection-test-result"
        override fun execute(frame: Frame) = listOf(Transition.Return(frame.takeResult())).asOpExecution()
    }

    private fun vm(
        exactSafe: Set<MethodDescriptor> = emptySet(),
        exactUnsafe: Set<MethodDescriptor> = emptySet(),
        safeClasses: Set<VirtualType> = emptySet(),
    ) = VmServices(
        classIndex,
        ReflectionPolicy(exactUnsafeMethods = exactUnsafe, exactSafeMethods = exactSafe, safeClasses = safeClasses),
    )

    private fun method(owner: String, name: String, proto: String) = method(type(owner), name, proto)
    private fun method(owner: VirtualType, name: String, proto: String) = MethodDescriptor(owner, name, proto)
    private fun type(descriptor: String) = VirtualType.of(descriptor)
    private fun view(owner: String, descriptor: String) = classIndex.requireMethodView(owner, descriptor)
    private fun callerView() = view("Linvoke_virtual_test;", "invokeRangeManyParameters()V")
    private fun Outcome.returnedValue(): Value? = (this as Outcome.Returned).value
    private class CountingBackend : Backend {
        var probes = 0
        override val name = "later"
        override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
            probes++
            return BackendProbe.Declined("later")
        }
    }

    private class CountingExecuteBackend : SynchronousBackend {
        var probes = 0
        override val name = "later"
        override fun probe(call: BackendInvocation, lookup: MethodLookup): BackendProbe {
            probes++
            return BackendProbe.Execute(SideEffectLevel.NONE)
        }

        override fun execute(call: SelectedHostCall): SynchronousCallResult =
            SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(1, VirtualType.of("I"))))
    }
}

object ReflectionExternalSentinel {
    var externalMutations = 0
    var staticInitializations = 0
    var constructorInitializations = 0
    var parentInitializations = 0
    var childInitializations = 0

    fun reset() {
        externalMutations = 0
        staticInitializations = 0
        constructorInitializations = 0
        parentInitializations = 0
        childInitializations = 0
    }
}

class ReflectBackendFixture {
    fun touch(): Int {
        ReflectionExternalSentinel.externalMutations++
        return 1
    }

    companion object {
        @JvmField val fatal = StackOverflowError("fatal")
        @JvmStatic fun overload(value: Int): String = "int:$value"
        @JvmStatic fun overload(value: Number): String = "number:$value"
        @JvmStatic fun mismatchedReturn(): String {
            ReflectionExternalSentinel.externalMutations++
            return "wrong descriptor"
        }
        @JvmStatic fun returnNullString(): String? = null
        @JvmStatic fun identityObject(value: Any): Any = value

        @JvmStatic fun mutateOwned(builder: StringBuilder, list: MutableList<String>) {
            builder.append("-owned")
            list += "owned"
        }

        @JvmStatic fun mutateExternal() {
            ReflectionExternalSentinel.externalMutations++
        }

        @JvmStatic fun returnBoolean() = true
        @JvmStatic fun returnByte(): Byte = 2
        @JvmStatic fun returnChar(): Char = 'c'
        @JvmStatic fun returnShort(): Short = 4
        @JvmStatic fun returnInt() = 5
        @JvmStatic fun returnLong() = 6L
        @JvmStatic fun returnFloat() = 7.5f
        @JvmStatic fun returnDouble() = 8.5
        @JvmStatic fun returnBoxedByte(): Byte? = 11
        @JvmStatic fun returnList(): List<String> = ArrayList(listOf("runtime"))
        @JvmStatic fun returnNestedThrowable(): Throwable =
            IllegalStateException("returned", IllegalArgumentException("cause"))
        @JvmStatic fun appendVoid(builder: StringBuilder) { builder.append("-void") }
        @JvmStatic fun throwProvided(throwable: Throwable) {
            throw throwable
        }
        @JvmStatic fun throwNested() {
            throw IllegalStateException("outer", IllegalArgumentException("cause"))
        }
        @JvmStatic fun throwCauseCycle() {
            val outer = LinkedCauseThrowable("outer")
            val cause = LinkedCauseThrowable("cause")
            outer.linkedCause = cause
            cause.linkedCause = outer
            throw outer
        }
        @JvmStatic fun mutateThenThrow(builder: StringBuilder) {
            builder.append("-thrown")
            throw IllegalStateException("target")
        }
        @JvmStatic fun throwLinkage() {
            throw IllegalAccessError("guest linkage")
        }
        @JvmStatic fun throwFatal() {
            throw fatal
        }
        @JvmStatic private fun privateValue() = 3
        @JvmStatic fun acceptString(value: String): Int = value.length
        @JvmStatic fun guestWins(): Int {
            ReflectionExternalSentinel.externalMutations++
            return 99
        }
    }
}

private class LinkedCauseThrowable(message: String) : RuntimeException(message) {
    var linkedCause: Throwable? = null

    override val cause: Throwable?
        get() = linkedCause
}

class ReflectOverloadedConstructor {
    val selected: String

    constructor(value: Int) { selected = "int:$value" }
    constructor(value: Number) { selected = "number:$value" }
}

interface ReflectCovariantContract {
    fun value(): CharSequence
}

class ReflectCovariantFixture : ReflectCovariantContract {
    override fun value(): String = "covariant"
}

class ReflectStaticInitProbe {
    companion object {
        init { ReflectionExternalSentinel.staticInitializations++ }
        @JvmStatic fun value() = 1
    }
}

class ReflectConstructorInitProbe {
    init { ReflectionExternalSentinel.constructorInitializations++ }
}

class ReflectArgumentHolder(private val value: StringBuilder) {
    fun get(): StringBuilder = value
}

open class ReflectUnsafeParent {
    companion object {
        init { ReflectionExternalSentinel.parentInitializations++ }
    }
}

class ReflectSafeChild : ReflectUnsafeParent() {
    companion object {
        init { ReflectionExternalSentinel.childInitializations++ }
        @JvmStatic fun value() = 1
    }
}
