package org.cf.sdbg

import org.cf.smalivm.core.FieldSignature
import org.cf.smalivm.core.MalformedSignatureException
import org.cf.smalivm.core.MethodSignature
import org.cf.smalivm.core.VirtualType

/** A class, method or field reference typed by the user, read with the dex signature grammar. */
internal sealed interface SmaliReference {
    /** `Lpkg/Name;` or an array type. */
    data class Type(val type: VirtualType) : SmaliReference

    data class Method(val signature: MethodSignature) : SmaliReference

    data class Field(val signature: FieldSignature) : SmaliReference

    data class Malformed(val reason: String) : SmaliReference

    companion object {
        /**
         * Reads [text] as a type descriptor, `Lpkg/Name;->name(params)return`, `Lpkg/Name;->name:type`, or, when
         * [currentClass] is given, `name(params)return` declared by it. When no form matches, the reason is the
         * failure of the form that read furthest into [text].
         */
        fun parse(text: String, currentClass: VirtualType?): SmaliReference {
            val type = try {
                VirtualType.of(text)
            } catch (_: IllegalArgumentException) {
                null
            }
            if (type != null) return Type(type)
            val failures = ArrayList<MalformedSignatureException>(3)
            attempt(failures) { MethodSignature.parse(text) }?.let { return Method(it) }
            attempt(failures) { FieldSignature.parse(text) }?.let { return Field(it) }
            if (currentClass != null) {
                attempt(failures) { MethodSignature.parseMember(currentClass, text) }?.let { return Method(it) }
            }
            return Malformed("'$text' is not a class, method, or field reference: ${failures.maxBy { it.offset }.reason}")
        }

        private inline fun <T> attempt(failures: MutableList<MalformedSignatureException>, read: () -> T): T? = try {
            read()
        } catch (failure: MalformedSignatureException) {
            failures += failure
            null
        }
    }
}
