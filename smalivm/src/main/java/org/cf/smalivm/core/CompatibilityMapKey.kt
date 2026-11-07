package org.cf.smalivm.core

import java.nio.ByteBuffer
import java.security.MessageDigest
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf

internal data class CompatibilityHashMetrics(
    val hashCalls: Int,
    val equalityEntriesVisited: Int,
)

private class MutableCompatibilityHashMetrics {
    var hashCalls = 0
    var equalityEntriesVisited = 0

    fun snapshot(): CompatibilityHashMetrics = CompatibilityHashMetrics(hashCalls, equalityEntriesVisited)
}

/** Immutable compatibility key with root fast-path, cached hash, and exact collision fallback. */
internal class CompatibilityMapKey<K, V> private constructor(
    private val backing: PersistentMap<K, V>,
    private val structuralHash: Int,
    private val fingerprint: CompatibilityFingerprint,
    private val metrics: MutableCompatibilityHashMetrics,
) : Map<K, V> by backing {
    fun put(key: K, value: V): CompatibilityMapKey<K, V> {
        val hadOld = backing.containsKey(key)
        val old = backing[key]
        val updated = backing.putting(key, value)
        if (updated === backing) return this
        val withoutOld = if (hadOld) structuralHash - entryHash(key, old) else structuralHash
        val withoutOldFingerprint = if (hadOld) fingerprint - entryFingerprint(key, old) else fingerprint
        return CompatibilityMapKey(
            updated,
            withoutOld + entryHash(key, value),
            withoutOldFingerprint + entryFingerprint(key, value),
            metrics,
        )
    }

    fun remove(key: K): CompatibilityMapKey<K, V> {
        if (!backing.containsKey(key)) return this
        val old = backing[key]
        return CompatibilityMapKey(
            backing.removing(key),
            structuralHash - entryHash(key, old),
            fingerprint - entryFingerprint(key, old),
            metrics,
        )
    }

    internal fun rootToken(): Any = backing

    internal fun hashMetrics(): CompatibilityHashMetrics = metrics.snapshot()

    override fun hashCode(): Int {
        metrics.hashCalls++
        return structuralHash
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CompatibilityMapKey<*, *>) return false
        if (backing === other.backing) return true
        if (structuralHash != other.structuralHash) return false
        if (fingerprint != other.fingerprint) return false
        metrics.equalityEntriesVisited += backing.size
        return backing == other.backing
    }

    override fun toString(): String = backing.toString()

    private fun entryHash(key: K, value: V?): Int = (key?.hashCode() ?: 0) xor (value?.hashCode() ?: 0)

    private fun entryFingerprint(key: K, value: V?): CompatibilityFingerprint {
        val digest = MessageDigest.getInstance("SHA-256")
        updateDigest(digest, compatibilityBytes(key))
        updateDigest(digest, compatibilityBytes(value))
        val bytes = digest.digest()
        val buffer = ByteBuffer.wrap(bytes)
        return CompatibilityFingerprint(buffer.long, buffer.long, buffer.long, buffer.long)
    }

    companion object {
        fun <K, V> empty(): CompatibilityMapKey<K, V> =
            CompatibilityMapKey(
                persistentMapOf(),
                0,
                CompatibilityFingerprint.ZERO,
                MutableCompatibilityHashMetrics(),
            )
    }
}

private data class CompatibilityFingerprint(
    val first: Long,
    val second: Long,
    val third: Long,
    val fourth: Long,
) {
    operator fun plus(other: CompatibilityFingerprint): CompatibilityFingerprint = CompatibilityFingerprint(
        first + other.first,
        second + other.second,
        third + other.third,
        fourth + other.fourth,
    )

    operator fun minus(other: CompatibilityFingerprint): CompatibilityFingerprint = CompatibilityFingerprint(
        first - other.first,
        second - other.second,
        third - other.third,
        fourth - other.fourth,
    )

    companion object {
        val ZERO = CompatibilityFingerprint(0, 0, 0, 0)
    }
}

private fun updateDigest(digest: MessageDigest, bytes: ByteArray) {
    digest.update(ByteBuffer.allocate(Int.SIZE_BYTES).putInt(bytes.size).array())
    digest.update(bytes)
}

private fun compatibilityBytes(value: Any?): ByteArray = when (value) {
    null -> byteArrayOf(0)
    is String -> "string:${value.length}:$value".toByteArray()
    is Int -> "int:$value".toByteArray()
    is Long -> "long:$value".toByteArray()
    is Boolean -> "boolean:$value".toByteArray()
    is Enum<*> -> "enum:${value.javaClass.name}:${value.name}".toByteArray()
    is VirtualType -> "type:${value.descriptor}".toByteArray()
    is TrackedFieldCompatibility -> trackedFieldCompatibilityBytes(value)
    else -> "hash:${value.javaClass.name}:${value.hashCode()}".toByteArray()
}

private fun trackedFieldCompatibilityBytes(value: TrackedFieldCompatibility): ByteArray {
    val metadata = value.metadata
    val domain = when (val fieldDomain = metadata.domain) {
        TrackedFieldDomain.Virtual -> "virtual"
        is TrackedFieldDomain.Host ->
            "host:${fieldDomain.declaringClass.name}:${System.identityHashCode(fieldDomain.declaringClass)}"
    }
    return listOf(
        metadata.declaringType.descriptor,
        metadata.name,
        metadata.descriptor,
        metadata.fieldType.descriptor,
        metadata.modifiers.toString(),
        metadata.visibility.name,
        metadata.storage.name,
        domain,
        value.accessibility.name,
    ).joinToString(separator = "\u0000").toByteArray()
}
