package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.Opcode
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ClassForNameIntegrationTest {
    private val classIndex = CoreTester.classIndex
    private val owner = CoreTester.vmServices
    private val classType = VirtualType.of("Ljava/lang/Class;")
    private val fixtureType = VirtualType.of(FIXTURE)

    @Test
    fun oneArgumentOverloadHasTheOnlyDefaultClassForNameRegistration() {
        val oneArgument = MethodDescriptor(
            VirtualType.of("Ljava/lang/Class;"),
            "forName",
            "(Ljava/lang/String;)Ljava/lang/Class;",
        )
        val threeArguments = MethodDescriptor(
            VirtualType.of("Ljava/lang/Class;"),
            "forName",
            "(Ljava/lang/String;ZLjava/lang/ClassLoader;)Ljava/lang/Class;",
        )

        assertTrue(owner.invocationServices.emulators.find(oneArgument)?.matcher is EmulatorMatcher.Exact)
        assertNull(owner.invocationServices.emulators.find(threeArguments))
    }

    @Test
    fun knownLocalClassLoadsWithoutHostInitialization() {
        assertFalse(ClassForNameHostInitSentinel.initialized)

        val graph = session("knownLocalClassLoadsWithoutHostInitialization()Ljava/lang/Class;").run()
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known

        assertEquals("org.cf.smalivm.core.ClassForNameHostInitProbe", (returned.raw as Class<*>).name)
        assertFalse(ClassForNameHostInitSentinel.initialized)
        assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun sourceInvalidDexBinaryNameLoadsAsAStableLocalClassToken() {
        val first = session("sourceInvalidLocalBinaryNameLoads()Ljava/lang/Class;").run()
        val second = session("sourceInvalidLocalBinaryNameLoads()Ljava/lang/Class;").run()
        val expected = classIndex.currentResolver().loadClassConstant(OBFUSCATED_FIXTURE)
        val firstToken = ((first.outcomes.single() as Outcome.Returned).value as Value.Known).raw
        val secondToken = ((second.outcomes.single() as Outcome.Returned).value as Value.Known).raw

        assertSame(expected, firstToken)
        assertSame(firstToken, secondToken)
        assertEquals(SideEffectLevel.NONE, first.explored.methodSideEffectLevel())
        assertNull(first.nodePile(invokeAddress(first.methodView)).last().hostFailure)
    }

    @Test
    fun guestInitializationIsVisibleThenRetryReturnsStableClass() {
        val session = session("guestInitializationIsVisibleThenRetryReturnsStableClass()Ljava/lang/Class;")
        val graph = session.run()
        val invokeAddress = invokeAddress(session.methodView)
        val invokes = graph.nodePile(invokeAddress)
        val initializerNodes = graphNodes(invokes.first()).filter { it.view.signature == "$FIXTURE-><clinit>()V" }
        val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known

        assertEquals(2, invokes.size, "Class.forName must retry at the same invoke address")
        assertTrue(initializerNodes.isNotEmpty(), "guest initialization must be scheduler-visible")
        assertEquals(1, staticInt(graph, "guestInitCount"), "the guest initializer must run once")
        assertSame(classIndex.currentResolver().loadClassConstant(FIXTURE), returned.raw)
        assertEquals(classType, returned.type)
        assertTrue(
            graph.nodePile(session.methodView.nextAddress(invokeAddress)).single().opExecutionStarted,
            "move-result-object must execute after the retry returned its stable token",
        )
    }

    @Test
    fun guestInitializerEffectSurvivesRetry() {
        val graph = session("guestInitializerEffectSurvivesRetry()I").run()

        assertEquals(Value.Known(0x29, VirtualType.of("I")), (graph.outcomes.single() as Outcome.Returned).value)
        assertEquals(0x29, staticInt(graph, "sentinel"))
        assertEquals(1, staticInt(graph, "guestInitCount"))
        assertEquals(SideEffectLevel.STRONG, graph.explored.methodSideEffectLevel())
    }

    @Test
    fun missingClassRoutesGuestClassNotFoundException() {
        val graph = session("missingClassRoutesGuestClassNotFoundException()Ljava/lang/Class;").run()

        assertEquals(listOf("Ljava/lang/ClassNotFoundException;"), CoreTester.escapingTypes(graph))
        assertTrue(graph.outcomes.single() is Outcome.Threw)
        assertNull(graph.nodePile(invokeAddress(graph.methodView)).single().hostFailure)
    }

    @Test
    fun unknownClassNameReturnsConservativeTypedUnknown() {
        val view = view("unknownNameReturnsUnknownClass(Ljava/lang/String;)Ljava/lang/Class;")
        val frame = Frame.entryFrame(view).also {
            it.assign(view.parameters.single().register, Value.Unknown(VirtualType.of("Ljava/lang/String;")))
        }

        val graph = owner.newSession(view, entryFrame = frame).run()
        val invoke = graph.nodePile(invokeAddress(view)).single()

        assertEquals(Value.Unknown(classType), (graph.outcomes.single() as Outcome.Returned).value)
        assertEquals(SideEffectLevel.STRONG, invoke.sideEffectLevel)
        assertTrue(invoke.invocationDiagnostic?.contains("reflection policy denied") == true)
        assertNull(invoke.hostFailure)
    }

    @Test
    fun legalArrayBinaryNamesReturnClassTokensWithoutHostInitialization() {
        val cases = mapOf(
            "[I" to IntArray::class.java,
            "[Ljava.lang.String;" to Array<String>::class.java,
        )

        for ((name, expected) in cases) {
            val graph = forName(name)
            val returned = (graph.outcomes.single() as Outcome.Returned).value as Value.Known
            assertSame(expected, returned.raw, name)
            assertEquals(SideEffectLevel.NONE, graph.explored.methodSideEffectLevel(), name)
        }
    }

    @Test
    fun invalidBinaryNamesRouteGuestClassNotFoundException() {
        val invalidNames = listOf(
            "int",
            "Ljava/lang/String;",
            "java/lang/String",
            "backend..EmptyComponent",
            ".backend.LeadingEmptyComponent",
            "backend.TrailingEmptyComponent.",
            "backend.\u0000Nul",
            "[",
            "[V",
            "[Ljava.lang.String",
            "[Ljava/lang/String;",
            "[[Q",
        )

        for (name in invalidNames) {
            val graph = forName(name)
            assertEquals(listOf("Ljava/lang/ClassNotFoundException;"), CoreTester.escapingTypes(graph), name)
            assertNull(graph.nodePile(invokeAddress(graph.methodView)).single().hostFailure, name)
        }
    }

    /** `Class.forName` takes a binary name; ART and the JVM do not retry `.` as the nested-class `$`. */
    @Test
    fun canonicalNestedClassNameIsNotABinaryName() {
        val canonical = forName("java.util.Map.Entry")
        assertEquals(listOf("Ljava/lang/ClassNotFoundException;"), CoreTester.escapingTypes(canonical))
        assertNull(canonical.nodePile(invokeAddress(canonical.methodView)).single().hostFailure)

        val binary = forName("java.util.Map\$Entry")
        assertEquals(emptyList<String>(), CoreTester.escapingTypes(binary))
    }

    private fun session(descriptor: String): ExecutionSession = owner.newSession(view(descriptor))

    private fun view(descriptor: String): MethodView =
        classIndex.requireMethodView(FIXTURE, descriptor)

    private fun forName(name: String): ExecutionGraph {
        val view = view("unknownNameReturnsUnknownClass(Ljava/lang/String;)Ljava/lang/Class;")
        val frame = Frame.entryFrame(view).also {
            it.assign(view.parameters.single().register, Value.Known(name, VirtualType.of("Ljava/lang/String;")))
        }
        return owner.newSession(view, entryFrame = frame).run()
    }

    private fun invokeAddress(view: MethodView): Int =
        view.addresses.single { view.opcodeAt(it) == Opcode.INVOKE_STATIC }

    private fun staticInt(graph: ExecutionGraph, name: String): Int {
        val descriptor = ClassIndex.fieldDescriptorOf(fixtureType, name, "I")
        return (graph.explored.terminatingStaticFieldConsensus(fixtureType, descriptor) as Value.Known).raw as Int
    }

    private fun graphNodes(root: ExecutionNode): List<ExecutionNode> = buildList {
        fun collect(node: ExecutionNode) {
            add(node)
            node.children.forEach(::collect)
        }
        collect(root)
    }

    private companion object {
        const val FIXTURE = "Lbackend/class_for_name_test;"
        const val OBFUSCATED_FIXTURE = "Lbackend/1-obfuscated;"
    }
}
