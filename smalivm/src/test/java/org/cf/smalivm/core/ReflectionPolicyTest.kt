package org.cf.smalivm.core

import org.cf.smalivm.configuration.Configuration
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test

class ReflectionPolicyTest {
    @Test
    fun defaultPolicyLoadsItsResourcesWithoutTheConfigurationSingleton() {
        val previous = Configuration.instance
        try {
            Configuration.instance = null

            val first = ReflectionPolicy.defaults()
            Configuration.instance()
            val second = ReflectionPolicy.defaults()

            assertNotSame(first, second)
            assertEquals(ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS), first.decide(method("Ljava/lang/Integer;", "intValue", "()I")))
            assertEquals(ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE), first.decide(method("Ljava/lang/Class;", "getName", "()Ljava/lang/String;")))
            assertEquals(ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE), first.decide(method("Ljava/lang/Math;", "random", "()D")))
            assertEquals(
                SideEffectLevel.NONE,
                first.effectOf(method("Ljava/lang/StringBuilder;", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;")),
            )
            assertEquals(SideEffectLevel.STRONG, first.effectOf(method("Ljava/lang/StringBuilder;", "capacity", "()I")))
            assertEquals(first.decide(method("Ljava/lang/Integer;", "intValue", "()I")), second.decide(method("Ljava/lang/Integer;", "intValue", "()I")))
        } finally {
            Configuration.instance = previous
        }
    }

    @Test
    fun exactUnsafeWinsExactSafeSafeClassAndHostInitializationAuthorization() {
        val overlap = method("Ljava/lang/String;", "valueOf", "(I)Ljava/lang/String;")
        val clinit = method("Ljava/lang/String;", "<clinit>", "()V")
        val policy = ReflectionPolicy(
            exactUnsafeMethods = setOf(overlap, clinit),
            exactSafeMethods = setOf(overlap),
            safeClasses = setOf(type("Ljava/lang/String;")),
        )

        assertEquals(ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE), policy.decide(overlap))
        assertEquals(
            ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE),
            policy.decide(
                method("Ljava/lang/String;", "length", "()I"),
                hostInitializationChain = listOf(type("Ljava/lang/String;")),
            ),
        )
    }

    @Test
    fun constructorCopiesInputCollectionsAndAuthorizationDoesNotImplyPurity() {
        val target = method("Ljava/lang/String;", "length", "()I")
        val safe = mutableSetOf(target)
        val effects = mutableMapOf(target to SideEffectLevel.NONE)
        val policy = ReflectionPolicy(exactSafeMethods = safe, methodEffects = effects)
        safe.clear()
        effects[target] = SideEffectLevel.WEAK

        assertEquals(ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE), policy.decide(target))
        assertEquals(SideEffectLevel.NONE, policy.effectOf(target))
        assertEquals(SideEffectLevel.STRONG, ReflectionPolicy(exactSafeMethods = setOf(target)).effectOf(target))
    }

    @Test
    fun globalThreadIoNetworkAndProviderClassesAreNotClassWideSafe() {
        val policy = ReflectionPolicy.defaults()
        val denied = listOf(
            method("Ljava/lang/ThreadLocal;", "set", "(Ljava/lang/Object;)V"),
            method("Ljava/util/Locale;", "setDefault", "(Ljava/util/Locale;)V"),
            method("Ljava/net/URL;", "openConnection", "()Ljava/net/URLConnection;"),
            method("Ljava/net/InetAddress;", "getByName", "(Ljava/lang/String;)Ljava/net/InetAddress;"),
            method("Ljava/lang/Object;", "wait", "()V"),
            method("Ljava/io/DataOutputStream;", "flush", "()V"),
            method("Ljava/nio/MappedByteBuffer;", "force", "()Ljava/nio/MappedByteBuffer;"),
            method("Ljava/nio/charset/spi/CharsetProvider;", "charsetForName", "(Ljava/lang/String;)Ljava/nio/charset/Charset;"),
            method("Ljava/util/Scanner;", "next", "()Ljava/lang/String;"),
            method("Ljava/util/Properties;", "store", "(Ljava/io/OutputStream;Ljava/lang/String;)V"),
            method("Ljava/util/concurrent/CountDownLatch;", "await", "()V"),
            method("Ljava/util/concurrent/ArrayBlockingQueue;", "take", "()Ljava/lang/Object;"),
            method("Ljava/util/Map;", "compute", "(Ljava/lang/Object;Ljava/util/function/BiFunction;)Ljava/lang/Object;"),
            method("Ljava/util/Map;", "forEach", "(Ljava/util/function/BiConsumer;)V"),
            method("Ljava/util/Random;", "<init>", "()V"),
            method("Ljava/util/UUID;", "randomUUID", "()Ljava/util/UUID;"),
            method("Lsun/security/util/Debug;", "println", "(Ljava/lang/String;)V"),
        )

        denied.forEach { target ->
            assertEquals(ReflectionDecision.Denied(ReflectionRule.DEFAULT_DENY), policy.decide(target), target.toString())
        }
    }

    @Test
    fun corpusListAndStringBuilderCallsUseExactSafeRules() {
        val policy = ReflectionPolicy.defaults()
        val exact = listOf(
            method("Ljava/util/List;", "add", "(Ljava/lang/Object;)Z"),
            method("Ljava/util/List;", "get", "(I)Ljava/lang/Object;"),
            method("Ljava/lang/StringBuilder;", "append", "(J)Ljava/lang/StringBuilder;"),
            method("Ljava/lang/StringBuilder;", "length", "()I"),
            method("Ljava/util/Map;", "get", "(Ljava/lang/Object;)Ljava/lang/Object;"),
            method("Ljava/util/Map;", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;"),
            method("Ljava/util/UUID;", "<init>", "(JJ)V"),
        )

        exact.forEach { target ->
            assertEquals(ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE), policy.decide(target), target.toString())
        }
    }

    private fun method(owner: String, name: String, proto: String) = MethodDescriptor(type(owner), name, proto)
    private fun type(descriptor: String) = VirtualType.of(descriptor)
}
