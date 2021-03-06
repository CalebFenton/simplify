package org.cf.smalivm.opcode

import org.cf.smalivm.type.ClassManager
import org.jf.dexlib2.builder.MethodLocation

interface OpFactory {

    fun create(location: MethodLocation, addressToLocation: Map<Int, MethodLocation>, classManager: ClassManager, exceptionFactory: ExceptionFactory): Op
}
