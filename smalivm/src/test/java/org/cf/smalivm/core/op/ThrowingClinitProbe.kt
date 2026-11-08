package org.cf.smalivm.core.op

/**
 * A compiled class whose `<clinit>` throws, for `ConstOpTest.aConstClassLoadDoesNotInitializeItsClass`. A smali
 * fixture cannot serve: `ClassBuilder` gives every guest class a `<clinit>` that only returns, so initializing
 * one is unobservable. This class resolves through the class loader's parent and keeps its real initializer,
 * so a `const-class` that initialized it would throw. `const-class` resolves without initializing
 * (`docs/DALVIK-SEMANTICS.md`, `const-class` (21c)).
 */
internal object ThrowingClinitProbe {
    init {
        throw RuntimeException("const-class must not initialize the class it resolves")
    }
}
