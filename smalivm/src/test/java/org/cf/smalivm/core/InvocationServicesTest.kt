package org.cf.smalivm.core

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Test

class InvocationServicesTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun independentOwnersIsolateEmulatorsAndReflectionPolicy() {
        val emulated = method("Lcalls/callee;", "returnsSeven", "()I")
        val first = owner(
            policy = ReflectionPolicy(exactSafeMethods = setOf(emulated)),
        )
        val second = owner()
        val registration = exactRegistration(emulated, handlesUnknownArguments = true, SideEffectLevel.WEAK)

        first.invocationServices.emulators.register(registration)

        assertSame(registration, first.invocationServices.emulators.find(emulated))
        assertNull(second.invocationServices.emulators.find(emulated))
        assertEquals(
            ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE),
            first.invocationServices.reflectionPolicy.decide(emulated),
        )
        assertEquals(
            ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY),
            second.invocationServices.reflectionPolicy.decide(emulated),
        )
    }


    @Test
    fun exactAndPredicateRegistrationAndReplacementAreDeterministic() {
        val registry = EmulatorRegistry()
        val target = method("Lcalls/callee;", "returnsSeven", "()I")
        val firstPredicate = predicateRegistration("calls", SideEffectLevel.WEAK) {
            it.definingClass == type("Lcalls/callee;")
        }
        val secondPredicate = predicateRegistration("returns", SideEffectLevel.NONE) {
            it.name == "returnsSeven"
        }
        registry.register(firstPredicate)
        registry.register(secondPredicate)
        assertSame(firstPredicate, registry.find(target))

        val replacementPredicate = predicateRegistration("calls", SideEffectLevel.STRONG) { false }
        registry.register(replacementPredicate)
        assertEquals(listOf(replacementPredicate, secondPredicate), registry.registrations())
        assertSame(secondPredicate, registry.find(target))

        val exact = exactRegistration(target, handlesUnknownArguments = false, SideEffectLevel.WEAK)
        registry.register(exact)
        assertSame(exact, registry.find(target))

        val exactReplacement = exactRegistration(target, handlesUnknownArguments = true, SideEffectLevel.NONE)
        registry.register(exactReplacement)
        assertSame(exactReplacement, registry.find(target))
        assertEquals(listOf(exactReplacement, replacementPredicate, secondPredicate), registry.registrations())
    }


    @Test
    fun reflectionPolicyUsesUnsafeThenSafeMethodThenSafeClassThenDefaultDeny() {
        val overlap = method("Ljava/lang/String;", "valueOf", "(I)Ljava/lang/String;")
        val exactSafe = method("Ljava/lang/String;", "length", "()I")
        val classSafe = method("Ljava/lang/String;", "charAt", "(I)C")
        val denied = method("Ljava/lang/System;", "currentTimeMillis", "()J")
        val policy = ReflectionPolicy(
            exactUnsafeMethods = setOf(overlap),
            exactSafeMethods = setOf(overlap, exactSafe),
            safeClasses = setOf(type("Ljava/lang/String;")),
        )

        assertEquals(
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE),
            policy.decide(overlap),
        )
        assertEquals(
            ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE),
            policy.decide(exactSafe),
        )
        assertEquals(
            ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS),
            policy.decide(classSafe),
        )
        assertEquals(
            ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY),
            policy.decide(denied),
        )
    }

    @Test
    fun aRegistrationReplacingADefaultEmulatorIsUsedByANewSession() {
        val forName = method("Ljava/lang/Class;", "forName", "(Ljava/lang/String;)Ljava/lang/Class;")
        val view = classIndex.requireMethodView(
            "Lbackend/class_for_name_test;",
            "missingClassRoutesGuestClassNotFoundException()Ljava/lang/Class;",
        )
        val defaults = VmServices(classIndex).newSession(view).run()
        assertEquals(listOf("Ljava/lang/ClassNotFoundException;"), CoreTester.escapingTypes(defaults))

        val vm = VmServices(classIndex)
        vm.invocationServices.emulators.register(
            EmulatorRegistration(
                EmulatorMatcher.Exact(forName),
                EmulatorCapability(handlesUnknownArguments = false),
                SideEffectLevel.NONE,
                EmulatorHandler {
                    SynchronousCallResult.Returned(HostReturn.Value(HostValue.TypedNull(type("Ljava/lang/Class;"))))
                },
            ),
        )
        val replaced = vm.newSession(view).run()

        assertEquals(emptyList<String>(), CoreTester.escapingTypes(replaced))
        assertEquals(listOf(Outcome.Returned(Value.Known(null, type("Ljava/lang/Class;")))), replaced.outcomes)
    }

    @Test
    fun anExtraRegistrationOverridesRunnableBytecodeInANewSession() {
        val vm = VmServices(classIndex)
        vm.invocationServices.emulators.register(returning(RETURNS_SEVEN, 8))

        val graph = vm.newSession(callerView()).run()

        assertEquals(listOf(Outcome.Returned(Value.Known(8, type("I")))), graph.outcomes)
    }

    @Test
    fun aSessionKeepsTheRegistrationsItWasCreatedWith() {
        val vm = VmServices(classIndex)
        val earlier = vm.newSession(callerView())

        vm.invocationServices.emulators.register(returning(RETURNS_SEVEN, 8))
        val later = vm.newSession(callerView())

        assertEquals(listOf(Outcome.Returned(Value.Known(7, type("I")))), earlier.run().outcomes)
        assertEquals(listOf(Outcome.Returned(Value.Known(8, type("I")))), later.run().outcomes)
    }

    private fun callerView() = classIndex.requireMethodView("Lcalls/caller;", "callsCalleeAndReturnsItsResult()I")

    private fun returning(target: MethodDescriptor, value: Int) = EmulatorRegistration(
        EmulatorMatcher.Exact(target),
        EmulatorCapability(handlesUnknownArguments = false),
        SideEffectLevel.NONE,
        EmulatorHandler { SynchronousCallResult.Returned(HostReturn.Value(HostValue.Primitive(value, type("I")))) },
    )


    private fun owner(policy: ReflectionPolicy = ReflectionPolicy()): VmServices =
        VmServices(classIndex, policy)


    private fun exactRegistration(
        target: MethodDescriptor,
        handlesUnknownArguments: Boolean,
        effect: SideEffectLevel,
    ) = EmulatorRegistration(
        EmulatorMatcher.Exact(target),
        EmulatorCapability(handlesUnknownArguments),
        effect,
        EmulatorHandler { SynchronousCallResult.Returned(HostReturn.Void) },
    )

    private fun predicateRegistration(
        id: String,
        effect: SideEffectLevel,
        predicate: (MethodDescriptor) -> Boolean,
    ) = EmulatorRegistration(
        EmulatorMatcher.Predicate(id, predicate),
        EmulatorCapability(handlesUnknownArguments = false),
        effect,
        EmulatorHandler { SynchronousCallResult.Returned(HostReturn.Void) },
    )

    private fun method(definingClass: String, name: String, proto: String) =
        MethodDescriptor(type(definingClass), name, proto)

    private fun type(descriptor: String) = VirtualType.of(descriptor)

    private companion object {
        val RETURNS_SEVEN = MethodDescriptor(VirtualType.of("Lcalls/callee;"), "returnsSeven", "()I")
    }
}
