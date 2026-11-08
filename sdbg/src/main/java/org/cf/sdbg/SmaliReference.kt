package org.cf.sdbg

import org.cf.smalivm.core.VirtualType

/** A class, method or field reference typed by the user, parsed against the dex descriptor grammar. */
internal sealed interface SmaliReference {
    /** `Lpkg/Name;` or an array type. */
    data class Type(val type: VirtualType) : SmaliReference

    /** `name(params)return`; [owner] is null when the user omits the `Lpkg/Name;->` qualifier. */
    data class Method(val owner: VirtualType?, val descriptor: String) : SmaliReference

    /** `Lpkg/Name;->name:type`. */
    data class Field(val owner: VirtualType, val name: String, val type: VirtualType) : SmaliReference

    data class Malformed(val reason: String) : SmaliReference

    companion object {
        fun parse(text: String): SmaliReference {
            val arrow = text.indexOf("->")
            if (arrow < 0) {
                return when {
                    text.startsWith('L') || text.startsWith('[') ->
                        typeOf(text)?.let(::Type) ?: Malformed("malformed class descriptor '$text'; expected Lpkg/Name;")
                    '(' in text -> method(null, text)
                    else -> Malformed("'$text' is not a class descriptor, method, or Lpkg/Name;->member reference")
                }
            }
            val ownerText = text.substring(0, arrow)
            val owner = typeOf(ownerText)?.takeIf { it.isReference }
                ?: return Malformed("malformed owner '$ownerText'; expected Lpkg/Name;")
            val member = text.substring(arrow + 2)
            return when {
                '(' in member -> method(owner, member)
                ':' in member -> field(owner, member)
                else -> Malformed("malformed member '$member'; expected name(params)return or name:type")
            }
        }

        private fun method(owner: VirtualType?, descriptor: String): SmaliReference {
            val open = descriptor.indexOf('(')
            val close = descriptor.indexOf(')')
            val malformed = Malformed("malformed method '$descriptor'; expected name(params)return")
            if (close < open || !isMemberName(descriptor.substring(0, open))) return malformed
            if (!isParameterList(descriptor.substring(open + 1, close))) return malformed
            typeOf(descriptor.substring(close + 1)) ?: return malformed
            return Method(owner, descriptor)
        }

        private fun field(owner: VirtualType, member: String): SmaliReference {
            val name = member.substringBefore(':')
            val type = typeOf(member.substringAfter(':'))
            if (!isMemberName(name) || type == null || type.descriptor == "V") {
                return Malformed("malformed field '$member'; expected name:type")
            }
            return Field(owner, name, type)
        }

        /** Dex member names exclude these delimiters; only constructors use angle brackets. */
        private fun isMemberName(name: String): Boolean = name == "<init>" || name == "<clinit>" ||
            name.isNotEmpty() && name.none { it.isWhitespace() || it in "()/;[:.<>" }

        private fun isParameterList(parameters: String): Boolean {
            var start = 0
            while (start < parameters.length) {
                var end = start
                while (end < parameters.length && parameters[end] == '[') end++
                if (end == parameters.length) return false
                end = if (parameters[end] == 'L') parameters.indexOf(';', end) + 1 else end + 1
                if (end == 0) return false
                val type = typeOf(parameters.substring(start, end)) ?: return false
                if (type.descriptor == "V") return false
                start = end
            }
            return true
        }

        private fun typeOf(descriptor: String): VirtualType? = try {
            VirtualType.of(descriptor)
        } catch (e: IllegalArgumentException) {
            null
        }
    }
}
