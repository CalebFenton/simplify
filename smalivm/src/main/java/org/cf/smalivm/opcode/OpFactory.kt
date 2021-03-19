package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.jf.dexlib2.builder.MethodLocation

interface OpFactory {

    // Ops have access to all these through the node, but the goal is to front-load as much work as possible during op creation to avoid
    // that work during virtual execution.
    fun build(
        location: MethodLocation,
        addressToLocation: Map<Int, MethodLocation>,
        classManager: ClassManager,
        exceptionFactory: ExceptionFactory,
        classLoader: SmaliClassLoader,
        configuration: Configuration
    ): Op
}
