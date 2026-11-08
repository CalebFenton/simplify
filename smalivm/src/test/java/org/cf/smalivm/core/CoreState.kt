package org.cf.smalivm.core

/**
 * Seeded or expected registers, arrays, and static fields for [CoreTester].
 * [UNKNOWN] becomes a typed [Value.Unknown].
 */
class CoreState {
    private val registerToValue = LinkedHashMap<Int, Value>()

    val registers: Map<Int, Value> get() = registerToValue

    /**
     * A host array to seed or expect, kept as the host array plus its declared type: seeding needs a heap to
     * allocate into, and an expectation is compared element-wise against heap consensus.
     *
     * [type] is the register's declared tag, stored verbatim and not required to be an array descriptor
     * (`AGetOpTest.canGetObject` tags an object array `Ljava/lang/String;`). A wide tag is not supported:
     * `Frame.assign` reserves `register + 1` for any `J`/`D` value, and there is no wide reference.
     *
     * The array object's own type comes from [hostArray]'s class, not from [type] (see [trueTypeOf]).
     */
    class HostArray(val hostArray: Any, val type: VirtualType) {
        override fun toString(): String = "$type${(0 until java.lang.reflect.Array.getLength(hostArray)).map {
            java.lang.reflect.Array.get(hostArray, it)
        }}"
    }

    private val registerToArray = LinkedHashMap<Int, HostArray>()

    val arrays: Map<Int, HostArray> get() = registerToArray

    /**
     * Static field values keyed by the fully-qualified descriptor `Lsget_test;->myBoolean:Z`, the form
     * `ClassIndex.fieldDescriptorOf` produces and `ClassStore` is keyed by, so a seed and a read cannot miss.
     */
    private val fieldToValue = LinkedHashMap<String, Value>()

    val staticFields: Map<String, Value> get() = fieldToValue

    /** Flat (register, raw, typeDescriptor) triples. */
    fun setRegisters(vararg params: Any?) {
        require(params.size % 3 == 0) { "setRegisters takes (register, raw, type) triples, got ${params.size} args" }
        var i = 0
        while (i < params.size) {
            setRegister(params[i] as Int, params[i + 1], params[i + 2] as String)
            i += 3
        }
    }

    fun setRegister(register: Int, raw: Any?, typeDescriptor: String) {
        // A host array becomes a tracked-array spec, never `Value.Known(hostArray)`: a VM-tracked mutable's raw
        // is a HeapRef, and a host array would be shared between forked siblings by `Frame.copy()`. Detected by
        // the raw's class because array register tags may be loose (see HostArray.type).
        if (raw != null && raw.javaClass.isArray) {
            registerToArray[register] = HostArray(raw, VirtualType.of(typeDescriptor))
            registerToValue.remove(register)
            return
        }
        registerToArray.remove(register)
        registerToValue[register] = valueOf(raw, typeDescriptor)
    }

    fun setRegister(register: Int, value: Value) {
        // Clear any array seed too, so the register is never in both maps.
        registerToArray.remove(register)
        registerToValue[register] = value
    }

    /**
     * Flat `(fieldNameAndType, raw)` pairs for one class; the declared type comes from the `:type` suffix.
     *
     * [className] must be the class that declares the field, which is what the engine keys its cell by
     * ([StaticFieldLookup.Resolved.descriptor]). A subclass would seed a cell no `sget` resolves to, so the
     * read answers the type default and the test fails loudly.
     */
    fun setFields(className: String, vararg params: Any?) {
        require(params.size % 2 == 0) { "setFields takes (fieldNameAndType, raw) pairs, got ${params.size} args" }
        var i = 0
        while (i < params.size) {
            val nameAndType = params[i] as String
            require(nameAndType.contains(':')) {
                "A field must be given as `name:typeDescriptor`; the declared type comes from the " +
                    "descriptor: '$nameAndType'"
            }
            setField(className, nameAndType, valueOf(params[i + 1], nameAndType.substringAfter(':')))
            i += 2
        }
    }

    fun setField(className: String, fieldNameAndType: String, value: Value) {
        fieldToValue["$className->$fieldNameAndType"] = value
    }

    fun getRegister(register: Int): Value? = registerToValue[register]

    override fun toString(): String = "CoreState(registers=$registerToValue, staticFields=$fieldToValue)"

    companion object {
        /**
         * "The test does not know this value." A sentinel rather than a `Value` because the lattice point
         * needs the declared type, which the caller supplies alongside it.
         */
        val UNKNOWN: Any = object {
            override fun toString(): String = "UNKNOWN"
        }

        private fun valueOf(raw: Any?, typeDescriptor: String): Value {
            val type = VirtualType.of(typeDescriptor)
            return if (raw === UNKNOWN) Value.Unknown(type) else Value.Known(raw, type)
        }

        /**
         * The host array's own type from its Java class. `IntArray::class.java.name` is `"[I"` and
         * `Array<String>::class.java.name` is `"[Ljava.lang.String;"`, so only `.` → `/` is needed.
         */
        internal fun arrayTypeOf(hostArray: Any): VirtualType {
            require(hostArray.javaClass.isArray) { "Not a host array: ${hostArray.javaClass}" }
            return VirtualType.of(hostArray.javaClass.name.replace('.', '/'))
        }

        /**
         * The array object's type for both allocation (`CoreTester.execute`) and [elementsOf], so the
         * allocated `TrackedArray.type` always agrees with the elements derived for it.
         *
         * The host array's own class wins over the tag, even when an element's raw does not match it. The
         * exception is an array containing [UNKNOWN]: `arrayOf(CoreState.UNKNOWN, 5)` is an `Object[]` only
         * because a primitive array has no slot for the sentinel, so the declared [HostArray.type] is the
         * only real type information.
         */
        internal fun trueTypeOf(spec: HostArray): VirtualType {
            val length = java.lang.reflect.Array.getLength(spec.hostArray)
            val sentineled = (0 until length).any { java.lang.reflect.Array.get(spec.hostArray, it) === UNKNOWN }
            return if (sentineled) spec.type else arrayTypeOf(spec.hostArray)
        }

        /**
         * A [HostArray]'s elements as lattice points, honoring [UNKNOWN] per element. The component type comes
         * from [trueTypeOf].
         */
        fun elementsOf(spec: HostArray): Array<Value> {
            val length = java.lang.reflect.Array.getLength(spec.hostArray)
            val component = trueTypeOf(spec).componentType!!
            return Array(length) { i ->
                when (val raw = java.lang.reflect.Array.get(spec.hostArray, i)) {
                    UNKNOWN -> Value.Unknown(component)
                    else -> Value.Known(raw, component)
                }
            }
        }
    }
}
