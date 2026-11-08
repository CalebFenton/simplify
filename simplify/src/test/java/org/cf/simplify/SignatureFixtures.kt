package org.cf.simplify

import org.cf.smalivm.core.ClassIndex
import org.cf.smalivm.core.MethodSignature
import org.cf.smalivm.core.MethodView

/*
 * Tests name methods by their smali spelling. These parse it with the production parser and call the typed API,
 * so a test that names a malformed signature fails at the parse.
 */

internal fun ClassIndex.methodView(signature: String): MethodView? = methodView(MethodSignature.parse(signature))

internal fun Optimizer.optimize(signature: String): Optimizer.Result = optimize(MethodSignature.parse(signature))
