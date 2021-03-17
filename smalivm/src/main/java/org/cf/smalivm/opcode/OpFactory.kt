package org.cf.smalivm.opcode

import ExceptionFactory
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.jf.dexlib2.builder.MethodLocation

interface OpFactory {

    fun build(
        location: MethodLocation,
        addressToLocation: Map<Int, MethodLocation>,
        classManager: ClassManager,
        exceptionFactory: ExceptionFactory,
        classLoader: SmaliClassLoader,
        configuration: Configuration
    ): Op
}
