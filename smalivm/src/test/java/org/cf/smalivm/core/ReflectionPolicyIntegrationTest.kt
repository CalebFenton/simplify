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

    // A host value that depends on the analysis machine's clock, random state, identity hash, system
    // properties, default charset, time zone or class path differs from what the device computes, so default
    // policy must leave it Unknown instead of folding it.

    @Test
    fun strictMathRandomIsNotFolded() = assertNotFolded("strictMathRandom()D", "D")

    @Test
    fun systemCurrentTimeMillisIsNotFolded() = assertNotFolded("currentTimeMillis()J", "J")

    @Test
    fun systemNanoTimeIsNotFolded() = assertNotFolded("nanoTime()J", "J")

    @Test
    fun newDateReadsTheHostClockAndIsNotFolded() = assertNotFolded("newDateTime()J", "J")

    @Test
    fun dateToStringReadsTheHostTimeZoneAndIsNotFolded() =
        assertNotFolded("epochDateString()Ljava/lang/String;", "Ljava/lang/String;")

    @Test
    fun calendarGetInstanceReadsTheHostClockAndIsNotFolded() = assertNotFolded("calendarInstanceTime()J", "J")

    @Test
    fun newGregorianCalendarReadsTheHostClockAndIsNotFolded() = assertNotFolded("gregorianCalendarTime()J", "J")

    @Test
    fun sqlDateToStringReadsTheHostTimeZoneAndIsNotFolded() =
        assertNotFolded("epochSqlDateString()Ljava/lang/String;", "Ljava/lang/String;")

    @Test
    fun sqlTimestampToStringReadsTheHostTimeZoneAndIsNotFolded() =
        assertNotFolded("epochTimestampString()Ljava/lang/String;", "Ljava/lang/String;")

    @Test
    fun enumHashCodeReadsTheHostIdentityHashAndIsNotFolded() = assertNotFolded("enumIdentityHash()I", "I")

    @Test
    fun booleanGetBooleanReadsAHostSystemPropertyAndIsNotFolded() =
        assertNotFolded("booleanSystemProperty()Z", "Z")

    @Test
    fun integerGetIntegerReadsAHostSystemPropertyAndIsNotFolded() {
        assertNotFolded("integerSystemProperty()Ljava/lang/Integer;", "Ljava/lang/Integer;")
        assertNotFolded("integerSystemPropertyWithIntDefault()Ljava/lang/Integer;", "Ljava/lang/Integer;")
        assertNotFolded("integerSystemPropertyWithBoxedDefault()Ljava/lang/Integer;", "Ljava/lang/Integer;")
    }

    @Test
    fun longGetLongReadsAHostSystemPropertyAndIsNotFolded() {
        assertNotFolded("longSystemProperty()Ljava/lang/Long;", "Ljava/lang/Long;")
        assertNotFolded("longSystemPropertyWithLongDefault()Ljava/lang/Long;", "Ljava/lang/Long;")
        assertNotFolded("longSystemPropertyWithBoxedDefault()Ljava/lang/Long;", "Ljava/lang/Long;")
    }

    @Test
    fun urlEncoderDefaultCharsetEncodeIsNotFolded() =
        assertNotFolded("urlEncodeWithDefaultCharset()Ljava/lang/String;", "Ljava/lang/String;")

    @Test
    fun urlDecoderDefaultCharsetDecodeIsNotFolded() =
        assertNotFolded("urlDecodeWithDefaultCharset()Ljava/lang/String;", "Ljava/lang/String;")

    @Test
    fun packageLookupReadsTheHostClassPathAndIsNotFolded() =
        assertNotFolded("hostPackage()Ljava/lang/Package;", "Ljava/lang/Package;")

    private fun execute(descriptor: String, arguments: List<Value>): ExecutionGraph {
        val view = classIndex.requireMethodView(FIXTURE, descriptor)
        val vm = VmServices(classIndex)
        val frame = Frame.entryFrame(view)
        val admitted = frame.heap.admitHostSeeds(arguments.map(::HostSeed))
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

    private fun assertNotFolded(descriptor: String, returnType: String) {
        val graph = VmServices(classIndex).newSession(classIndex.requireMethodView(NONDETERMINISM, descriptor)).run()

        assertEquals(listOf(Outcome.Returned(Value.Unknown(VirtualType.of(returnType)))), graph.outcomes)
        assertPolicyDenial(graph)
    }

    private companion object {
        const val FIXTURE = "Linvoke_backend_test;"
        const val NONDETERMINISM = "Lhost_nondeterminism_test;"
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
