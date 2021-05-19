package org.cf.smalivm

import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualClass
import org.cf.smalivm.type.VirtualMethod
import org.jf.dexlib2.builder.BuilderTryBlock
import java.util.*


class ExceptionHandlerAddressResolver internal constructor(private val classManager: ClassManager, localMethod: VirtualMethod) {
    private val tryBlocks: List<BuilderTryBlock> = localMethod.tryBlocks

    fun resolve(exceptionClass: VirtualClass, address: Int): Int {
        var handlerAddress = findHandlerCodeAddress(exceptionClass, address, false)
        if (handlerAddress != -1) {
            return handlerAddress
        }
        handlerAddress = findHandlerCodeAddress(exceptionClass, address, true)
        return handlerAddress
    }

    /*
     * Given an exception type and an address in code, this will try and find the correct exception handler. This is
     * complicated by the fact that handlers may overlap. There may be 6 overlapping exception handlers over one
     * particular address each catching different exception types with one handler as a catch-all.
     * This will try to find the most "immediate" exception handler in terms of class hierarchy.  If a handler for
     * the exact exception type can't be found, it'll try to find one for the exception type's parent, parent's
     * parent, and so on. If catchAll is true, it'll look for catch-all exception handlers.
     */
    private fun findHandlerCodeAddress(exceptionClass: VirtualClass, address: Int, catchAll: Boolean): Int {
        val lineage: MutableList<VirtualClass> = LinkedList()
        lineage.add(exceptionClass)
        lineage.addAll(exceptionClass.ancestors)
        for (currentExceptionClass in lineage) {
            val className = currentExceptionClass.name
            for (tryBlock in tryBlocks) {
                val startAddress = tryBlock.startCodeAddress
                val endAddress = tryBlock.startCodeAddress + tryBlock.codeUnitCount
                val inTryBlock = address >= startAddress && address < endAddress
                if (!inTryBlock) {
                    continue
                }
                val handlers = tryBlock.exceptionHandlers
                if (catchAll) {
                    // If it's the last handler and it's null, it's a catchall
                    val handler = handlers[handlers.size - 1]
                    if (null == handler.exceptionType) {
                        return handler.handlerCodeAddress
                    }
                } else {
                    for (handler in handlers) {
                        val handlerType = handler.exceptionType
                        if (className == handlerType) {
                            return handler.handlerCodeAddress
                        }
                    }
                }
            }
            if (catchAll) {
                // This loop is unnecessary for catch-all because it doesn't look at the exception class.
                return -1
            }
        }
        return -1
    }

}
