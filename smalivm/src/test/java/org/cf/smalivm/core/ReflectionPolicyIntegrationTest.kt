package org.cf.smalivm.core

import java.net.URI
import java.util.Locale
import java.util.function.BiConsumer
import java.util.function.BiFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ReflectionPolicyIntegrationTest {
    private val classIndex = CoreTester.classIndex

    @Test
    fun threadLocalSetIsDeniedBeforeExecution() {
        // Bug class: class-wide ThreadLocal authorization mutates the current host thread outside VM state.
        // Oracle: default policy denies set before target execution, leaving the process ThreadLocal unchanged.
        val local = ThreadLocal<Any>()
        val graph = execute(
            "deniedThreadLocal(Ljava/lang/ThreadLocal;Ljava/lang/Object;)V",
            listOf(
                Value.Known(local, VirtualType.of("Ljava/lang/ThreadLocal;")),
                Value.Known("value", VirtualType.of("Ljava/lang/Object;")),
            ),
        )

        assertEquals(null, local.get())
        assertPolicyDenial(graph)
    }

    @Test
    fun localeSetDefaultIsDeniedBeforeExecution() {
        // Bug class: class-wide Locale authorization changes process-global locale.
        // Oracle: exact policy denial occurs before Locale.setDefault can change the process.
        val original = Locale.getDefault()
        try {
            val graph = execute(
                "deniedLocale(Ljava/util/Locale;)V",
                listOf(Value.Known(Locale.CANADA_FRENCH, VirtualType.of("Ljava/util/Locale;"))),
            )

            assertEquals(original, Locale.getDefault())
            assertPolicyDenial(graph)
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test
    fun urlOpenConnectionIsDeniedBeforeExecution() {
        // Bug class: class-wide URL authorization can select protocol handlers or perform network-like work.
        // Oracle: default policy denial is terminal before URL target execution.
        val graph = execute(
            "deniedUrl(Ljava/net/URL;)V",
            listOf(Value.Known(URI.create("http://127.0.0.1:9/").toURL(), VirtualType.of("Ljava/net/URL;"))),
        )

        assertPolicyDenial(graph)
    }

    @Test
    fun mapComputeIsDeniedBeforeCallbackExecution() {
        // Bug class: class-wide Map authorization executes an arbitrary callback outside reconciled state.
        // Oracle: policy denial occurs before BiFunction.apply.
        CallbackSentinel.calls = 0
        val graph = execute(
            "deniedMapCompute(Ljava/util/Map;Ljava/lang/Object;Ljava/util/function/BiFunction;)V",
            listOf(
                Value.Known(hashMapOf<Any?, Any?>(), VirtualType.of("Ljava/util/Map;")),
                Value.Known("key", VirtualType.OBJECT),
                Value.Known(CallbackSentinel(), VirtualType.of("Ljava/util/function/BiFunction;")),
            ),
        )

        assertEquals(0, CallbackSentinel.calls)
        assertPolicyDenial(graph)
    }

    @Test
    fun mapForEachIsDeniedBeforeCallbackExecution() {
        // Bug class: class-wide Map authorization executes BiConsumer callbacks with process-visible effects.
        // Oracle: policy denial occurs before BiConsumer.accept.
        CallbackSentinel.calls = 0
        val graph = execute(
            "deniedMapForEach(Ljava/util/Map;Ljava/util/function/BiConsumer;)V",
            listOf(
                Value.Known(hashMapOf<Any?, Any?>("key" to "value"), VirtualType.of("Ljava/util/Map;")),
                Value.Known(CallbackSentinel(), VirtualType.of("Ljava/util/function/BiConsumer;")),
            ),
        )

        assertEquals(0, CallbackSentinel.calls)
        assertPolicyDenial(graph)
    }

    @Test
    fun uuidRandomIsDeniedBeforeProviderExecution() {
        // Bug class: class-wide UUID authorization reaches SecureRandom/provider state.
        // Oracle: default policy denies randomUUID before target or provider execution.
        assertPolicyDenial(execute("deniedUuidRandom()V", emptyList()))
    }

    @Test
    fun randomConstructorIsDeniedBeforeGlobalSeedMutation() {
        // Bug class: class-wide Random authorization lets its constructor mutate the global seed uniquifier.
        // Oracle: default policy denies the constructor before host target execution.
        assertPolicyDenial(execute("deniedRandomConstructor()V", emptyList()))
    }

    private fun execute(descriptor: String, arguments: List<Value>): ExecutionGraph {
        val view = classIndex.requireMethodView(FIXTURE, descriptor)
        val vm = VmServices(classIndex)
        val frame = Frame.entryFrame(view, vm.hostObjectCloner)
        val admitted = frame.admitHostSeeds(arguments.map(::HostSeed))
        view.parameters.zip(admitted).forEach { (parameter, value) -> frame.assign(parameter.register, value) }
        return vm.newSession(view, entryFrame = frame).run()
    }

    private fun assertPolicyDenial(graph: ExecutionGraph) {
        assertEquals(CompletionStatus.Complete, graph.completion())
        assertTrue(graph.addresses().flatMap(graph::nodePile).any { node ->
            node.invocationDiagnostic?.contains("reflection policy denied") == true
        })
        assertTrue(graph.outcomes.single() is Outcome.Returned)
    }

    private companion object {
        const val FIXTURE = "Linvoke_backend_test;"
    }

    private class CallbackSentinel : BiFunction<Any?, Any?, Any?>, BiConsumer<Any?, Any?> {
        override fun apply(first: Any?, second: Any?): Any? {
            calls++
            return second
        }

        override fun accept(first: Any?, second: Any?) {
            calls++
        }

        companion object {
            var calls = 0
        }
    }
}
