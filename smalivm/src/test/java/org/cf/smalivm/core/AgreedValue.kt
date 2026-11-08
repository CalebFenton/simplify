package org.cf.smalivm.core

/** The value every path agreed on; fails the test with the actual answer when they did not agree. */
internal fun <T> Agreement<T>.agreed(): T =
    (this as? Agreement.Agreed)?.value ?: throw AssertionError("expected the paths to agree, but the answer was $this")
