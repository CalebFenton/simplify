package org.cf.smalivm.core

/**
 * Seeded or expected registers, result, caught exception, and static fields for [CoreTester].
 * [UNKNOWN] becomes a typed [Value.Unknown]; results and caught exceptions use separate slots.
 */
class CoreState {
    private val registerToValue = LinkedHashMap<Int, Value>()

    var result: Value? = null
        private set
    var caught: Value? = null
        private set

    val registers: Map<Int, Value> get() = registerToValue

    /**
     * A host array the corpus seeded or expects, kept as the HOST array plus its declared type rather
     * than as an already-built `Value`: seeding needs a heap to allocate into (which only [CoreTester]
     * has), and an expectation is compared element-wise against heap consensus, so neither side wants a
     * premature `Value`.
     *
     * [type] is the array **register's** declared type as the corpus wrote it, which is **not required to
     * be an array descriptor**: `core/op/AGetOpTest.canGetObject` tags an object array
     * `Ljava/lang/String;`. It is stored verbatim, because the register's tag is what the migrated
     * expectations assert.
     *
     * Cited by test NAME rather than by line range on purpose. This sentence used to say
     * `AGetOpTest.kt:52-59`, which was accurate for `opcode/AGetOpTest.kt` until Plan 7 Task 7 migrated and
     * deleted that file -- after which the bare filename resolved against `core/op/AGetOpTest.kt`, where
     * `:52-59` lands on `canGet`: an `IntArray` tagged `[I`, tag and object in perfect agreement, i.e. the
     * exact OPPOSITE of the claim it was cited for. A name does not drift when lines move.
     *
     * A **wide** tag on an array register is a different case and is NOT supported here: `Frame.assign`
     * reserves `register + 1` as `WideHigh` for any `J`/`D` value, so a `long[]` reference tagged `"J"`
     * destroys whatever the next slot holds. That is a category error in the tag -- there is no such thing
     * as a wide reference -- not a loose one, so the corpus seed is corrected rather than accommodated here.
     *
     * **The array OBJECT's type is a different thing and is derived from [hostArray]`.javaClass`, not from
     * [type]** — object over tag (see `HeapObject.TrackedArray`). A `String[]` really is a `String[]`, so
     * `aget` gets a correctly-typed element without any string surgery on the tag, and these fixtures pass
     * for the reason Dalvik passes them rather than because the oracle's
     * `array.type.replaceFirst("\\[", "")` (`AGetOp.kt:42`) happens to be a no-op on a tag with no `[`.
     * Do **not** reproduce that accident.
     */
    class HostArray(val hostArray: Any, val type: VirtualType) {
        override fun toString(): String = "$type${(0 until java.lang.reflect.Array.getLength(hostArray)).map {
            java.lang.reflect.Array.get(hostArray, it)
        }}"
    }

    private val registerToArray = LinkedHashMap<Int, HostArray>()

    val arrays: Map<Int, HostArray> get() = registerToArray

    /**
     * Static field values to seed or to expect, keyed by the **fully-qualified** descriptor
     * `Lsget_test;->myBoolean:Z` -- `ClassIndex.fieldDescriptorOf`'s form, which is also what the engine's
     * `ClassInitStore` is keyed by, so a seed and a read cannot miss each other.
     *
     * The corpus wrote these as `setFields(CLASS_NAME, "myBoolean:Z", true)` -- class and member separately,
     * with a `HashMap` per class (`TestState.setField`). Flattened to one key here for the reason the engine
     * flattens it: a bare `"myBoolean"` is the oracle's colliding key
     * (`docs/architecture/type-and-dex.md:116`) and the class-qualified form is the only one that cannot
     * collide. [setFields] keeps the corpus's own call shape, so a migrating test is edited by swapping
     * `TestState` for `CoreState` rather than rewritten.
     */
    private val fieldToValue = LinkedHashMap<String, Value>()

    val staticFields: Map<String, Value> get() = fieldToValue

    /** Flat (register, raw, typeDescriptor) triples, matching `TestState.setRegisters`. */
    fun setRegisters(vararg params: Any?) {
        require(params.size % 3 == 0) { "setRegisters takes (register, raw, type) triples, got ${params.size} args" }
        var i = 0
        while (i < params.size) {
            setRegister(params[i] as Int, params[i + 1], params[i + 2] as String)
            i += 3
        }
    }

    fun setRegister(register: Int, raw: Any?, typeDescriptor: String) {
        // A host array raw becomes a tracked-array SPEC, never a Value.Known(hostArray): the raw of a
        // VM-tracked mutable is a HeapRef (spec §4.1), and keeping the host array would let
        // `Frame.copy()` share one instance between forked siblings -- the audit's shared-mutated-state
        // defect. Detected by `javaClass.isArray` rather than by the type descriptor, because the corpus
        // tags array registers loosely (see HostArray.type) and the RAW is the reliable signal.
        if (raw != null && raw.javaClass.isArray) {
            registerToArray[register] = HostArray(raw, VirtualType.of(typeDescriptor))
            registerToValue.remove(register)
            return
        }
        registerToArray.remove(register)
        registerToValue[register] = valueOf(raw, typeDescriptor)
    }

    fun setRegister(register: Int, value: Value) {
        // Must clear registerToArray too, or a migrator writing setRegisters(0, IntArray(1), "[I")
        // then setRegister(0, Value.Unknown(...)) leaves register 0 in BOTH maps at once, which the
        // two 3-arg branches above already guard against (:76, :79) but this overload did not.
        registerToArray.remove(register)
        registerToValue[register] = value
    }

    /**
     * Flat `(fieldNameAndType, raw)` pairs for one class, matching `TestState.setFields` exactly -- including
     * that the DECLARED TYPE comes from the descriptor's own `:type` suffix rather than from a separate
     * argument, so `setFields(CLASS_NAME, "myBoolean:Z", true)` ports verbatim.
     *
     * [className] must be the class that **declares** the field, which is what the engine keys its cell by
     * ([StaticFieldLookup.Resolved.descriptor]). Naming a subclass of the declaring class would seed a cell no
     * `sget` resolves to -- so the read would answer the type default and the test would fail loudly rather
     * than pass vacuously, which is the direction to be wrong in.
     */
    fun setFields(className: String, vararg params: Any?) {
        require(params.size % 2 == 0) { "setFields takes (fieldNameAndType, raw) pairs, got ${params.size} args" }
        var i = 0
        while (i < params.size) {
            val nameAndType = params[i] as String
            require(nameAndType.contains(':')) {
                "A field must be given as `name:typeDescriptor` -- the declared type comes from the " +
                    "descriptor, exactly as TestState.setFields reads it: '$nameAndType'"
            }
            setField(className, nameAndType, valueOf(params[i + 1], nameAndType.substringAfter(':')))
            i += 2
        }
    }

    fun setField(className: String, fieldNameAndType: String, value: Value) {
        fieldToValue["$className->$fieldNameAndType"] = value
    }

    fun setResult(raw: Any?, typeDescriptor: String) {
        result = valueOf(raw, typeDescriptor)
    }

    fun setCaught(raw: Any?, typeDescriptor: String) {
        caught = valueOf(raw, typeDescriptor)
    }

    fun getRegister(register: Int): Value? = registerToValue[register]

    override fun toString(): String =
        "CoreState(registers=$registerToValue, staticFields=$fieldToValue, result=$result, caught=$caught)"

    companion object {
        /**
         * "The test does not know this value." Stands where the corpus wrote `UnknownValue()`. A
         * sentinel rather than a `Value` because the lattice point needs the declared type, which the
         * caller supplies alongside it.
         */
        val UNKNOWN: Any = object {
            override fun toString(): String = "UNKNOWN"
        }

        private fun valueOf(raw: Any?, typeDescriptor: String): Value {
            val type = VirtualType.of(typeDescriptor)
            return if (raw === UNKNOWN) Value.Unknown(type) else Value.Known(raw, type)
        }

        /**
         * The array OBJECT's true type, from the host array's own class — never from the register tag, which
         * the corpus deliberately lets lie. This is the object-over-tag rule's single implementation for the
         * harness; [elementsOf] and the seeding path both go through it, so there is one place to be wrong.
         *
         * `IntArray::class.java.name` is `"[I"` and `Array<String>::class.java.name` is
         * `"[Ljava.lang.String;"`, so the only transform needed is `.` → `/` inside a reference descriptor.
         */
        internal fun arrayTypeOf(hostArray: Any): VirtualType {
            require(hostArray.javaClass.isArray) { "Not a host array: ${hostArray.javaClass}" }
            return VirtualType.of(hostArray.javaClass.name.replace('.', '/'))
        }


        /**
         * The array's own truthful type (object over tag), for BOTH the allocation site
         * (`CoreTester.execute`) and [elementsOf] -- the single function each must call so they can never
         * disagree, per Plan 6's whole-branch review finding B2: `elementsOf` alone used to derive the
         * sentinel-aware component here while `CoreTester.execute` allocated the array OBJECT's type via
         * unconditional [arrayTypeOf], so a sentineled seed's `TrackedArray.type` (used for `aget`/`aput`
         * assignability and for consensus) could disagree with the very elements this function derives --
         * exactly the case [HeapObject.TrackedArray]'s KDoc says cannot happen ("established at allocation
         * from something that cannot lie").
         *
         * Ordinarily the host array's OWN class wins (object over tag): a genuine guest array's runtime
         * type is authoritative, even when an element's raw does not match it
         * (`core/op/ArrayLengthOpTest.canGetLengthForStringArray`
         * puts Strings in a `[I` and asserts only the length -- deliberate parity with the oracle's
         * laxness, not something to validate away). The one exception is when [UNKNOWN] itself appears as
         * an element: `arrayOf(CoreState.UNKNOWN, 5)` has **no real guest array behind it at all** -- it is
         * a `kotlin.Array<Any>` (`Object[]`) purely because a primitive `IntArray` has no slot for the
         * sentinel, so there is nothing authoritative to read off its class, and the declared
         * [HostArray.type] is the only real type information left.
         */
        internal fun trueTypeOf(spec: HostArray): VirtualType {
            val length = java.lang.reflect.Array.getLength(spec.hostArray)
            val sentineled = (0 until length).any { java.lang.reflect.Array.get(spec.hostArray, it) === UNKNOWN }
            return if (sentineled) spec.type else arrayTypeOf(spec.hostArray)
        }

        /**
         * A [HostArray]'s elements as lattice points. [UNKNOWN] is honored **per element** -- the fix for
         * the nested-sentinel sub-case `CARRY-FORWARD.md`'s Plans 6–7 item 3 used to flag (now closed;
         * see `STATUS.md`'s Plan 6 entry).
         *
         * Component type comes from [trueTypeOf], never re-derived here: see its KDoc for why allocation
         * and elements must go through the one function.
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
