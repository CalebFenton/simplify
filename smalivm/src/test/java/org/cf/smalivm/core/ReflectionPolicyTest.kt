package org.cf.smalivm.core

import org.cf.util.ClassNameUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotSame
import org.junit.jupiter.api.Test

class ReflectionPolicyTest {
    @Test
    fun defaultPolicyLoadsFreshPolicyFromItsResources() {
        val first = ReflectionPolicy.defaults()
        val second = ReflectionPolicy.defaults()

        assertNotSame(first, second)
        assertEquals(ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS), first.decide(method("Ljava/lang/Integer;", "intValue", "()I")))
        assertEquals(ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE), first.decide(method("Ljava/lang/Class;", "getName", "()Ljava/lang/String;")))
        assertEquals(ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE), first.decide(method("Ljava/lang/Math;", "random", "()D")))
        assertEquals(first.decide(method("Ljava/lang/Integer;", "intValue", "()I")), second.decide(method("Ljava/lang/Integer;", "intValue", "()I")))
    }

    @Test
    fun everyClassWideSafeClassExistsOnTheHost() {
        val loader = ReflectionPolicy::class.java.classLoader
        val missing = ReflectionPolicy.loadDefinitions(loader, "safe_classes.cfg").filter { descriptor ->
            runCatching { Class.forName(ClassNameUtils.internalToBinary(descriptor), false, loader) }.isFailure
        }

        assertEquals(emptyList<String>(), missing)
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
    fun classWideAuthorizationCoversOnlyReceiversOfListedClasses() {
        val clear = method("Ljava/util/AbstractList;", "clear", "()V")
        val policy = ReflectionPolicy(safeClasses = setOf(type("Ljava/util/AbstractList;"), type("Ljava/util/ArrayList;")))

        assertEquals(ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS), policy.decide(clear, receiverClass = type("Ljava/util/ArrayList;")))
        assertEquals(
            ReflectionDecision.Denied(ReflectionRule.UNSAFE_RECEIVER),
            policy.decide(clear, receiverClass = type("Ljava/util/ArrayList\$SubList;")),
        )
    }

    @Test
    fun constructorCopiesInputCollections() {
        val target = method("Ljava/lang/String;", "length", "()I")
        val safe = mutableSetOf(target)
        val policy = ReflectionPolicy(exactSafeMethods = safe)
        safe.clear()

        assertEquals(ReflectionDecision.Allowed(ReflectionRule.EXACT_SAFE), policy.decide(target))
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
    fun stringMethodsThatReadTheHostLocaleOrRegexEngineAreDenied() {
        val policy = ReflectionPolicy.defaults()
        val denied = listOf(
            method("Ljava/lang/String;", "toLowerCase", "()Ljava/lang/String;"),
            method("Ljava/lang/String;", "toUpperCase", "()Ljava/lang/String;"),
            method("Ljava/lang/String;", "format", "(Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/String;"),
            method("Ljava/lang/String;", "split", "(Ljava/lang/String;)[Ljava/lang/String;"),
            method("Ljava/lang/String;", "matches", "(Ljava/lang/String;)Z"),
        )

        denied.forEach { target ->
            assertEquals(ReflectionDecision.Denied(ReflectionRule.EXACT_UNSAFE), policy.decide(target), target.toString())
        }
        assertEquals(
            ReflectionDecision.Allowed(ReflectionRule.SAFE_CLASS),
            policy.decide(method("Ljava/lang/String;", "toLowerCase", "(Ljava/util/Locale;)Ljava/lang/String;")),
        )
    }

    private fun method(owner: String, name: String, proto: String) = MethodSignature(type(owner), name, proto)
    private fun type(descriptor: String) = VirtualType.of(descriptor)
}
