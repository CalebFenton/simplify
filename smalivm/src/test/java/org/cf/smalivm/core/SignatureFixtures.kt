package org.cf.smalivm.core

/*
 * Tests name methods by their smali spelling. These parse it with the production parser and call the typed
 * lookup, so a test that names a malformed signature fails at the parse.
 */

internal fun ClassIndex.methodView(signature: String): MethodView? = methodView(MethodSignature.parse(signature))

internal fun ClassIndex.requireMethodView(className: String, member: String): MethodView =
    requireMethodView(MethodSignature.parseMember(VirtualType.of(className), member))

internal fun ClassIndex.lookupMethod(definingClass: VirtualType, member: String): MethodLookup =
    lookupMethod(MethodSignature.parseMember(definingClass, member))

internal fun ProgramSnapshot.applicationMethod(signature: String): ApplicationMethodDefinition? =
    applicationMethod(MethodSignature.parse(signature))
