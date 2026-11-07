package org.cf.smalivm.core.op

/**
 * Test-only support class for `core/op/ConstOpTest.aConstClassLoadDoesNotInitializeItsClass` (Plan 8
 * Task 10, decision 3). A **real** compiled class -- NOT a smali fixture -- whose `<clinit>` throws.
 *
 * The distinction is load-bearing and was measured: `SmaliClassLoader`/`ClassBuilder` build every guest
 * (smali) class with an EMPTY, stubbed `<clinit>` (`ClassBuilder.kt:152-155`, `visitClInitStub` emits a bare
 * `RETURN`), so a guest class's `<clinit>` can never run a real body on the host and initializing one is
 * unobservable. A `const-class` on such a class therefore cannot distinguish an initializing load from a
 * non-initializing one. This class is resolved through the class loader's PARENT (it is on the test
 * classpath, exactly as `Ljava/lang/Object;` is in `constClassRemote`), so it keeps its real throwing
 * `<clinit>` -- which is the shape in which the oracle's initializing-load defect (Correctness Defect 21,
 * `docs/architecture/opcodes.md:176`) actually bites.
 *
 * `const-class` resolves the host `Class` WITHOUT initializing it -- ART's entire `CONST_CLASS` handler is a
 * resolve, a `nullptr` check and a `SetVRegReference` (`interpreter_switch_impl-inl.h:755-767`), and **not**
 * merely that it passes `can_run_clinit = false`, which `NEW_INSTANCE` also passes while initializing
 * (`docs/DALVIK-SEMANTICS.md#const-class-21c`) -- so resolving this class stores its `Class` object and does
 * NOT run `<clinit>`. Were the load switched to the initializing overload, resolving it at decode time would
 * run `<clinit>`, which throws, and the const-class node could not be built.
 */
internal object ThrowingClinitProbe {
    init {
        throw RuntimeException("const-class must not initialize the class it resolves")
    }
}
