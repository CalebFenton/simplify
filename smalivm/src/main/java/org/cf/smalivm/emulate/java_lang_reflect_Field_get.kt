package org.cf.smalivm.emulate

import org.cf.smalivm.type.VirtualType
import org.cf.smalivm2.*
import org.cf.util.ClassNameUtils
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal class java_lang_reflect_Field_get : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode?, vm: VirtualMachine2): Array<out UnresolvedChild> {
        val value = state.peekParameter(0)!!
        val instance = state.peekParameter(1)!!
        val field = value.value as Field
        val accessFlags = field.modifiers
        val fieldClassName: String = ClassNameUtils.toInternal(field.declaringClass)
        if (!field.isAccessible) {
            // TODO: Caller node *may* be null if user executes as top level method. Handle more gracefully.
            val callingClass = callerNode!!.method.definingClass
            val fieldClass = callerNode.classManager.getVirtualClass(fieldClassName)
            val accessException = checkForAccessException(callingClass, fieldClass, accessFlags)
            if (accessException != null) {
                throwException(accessException.first, accessException.second)
            }
        }

        val getValue = if (callerNode!!.configuration.isSafe(fieldClassName)) {
            try {
                val getObject = field[instance]
                val type: String = ClassNameUtils.toInternal(field.type)
                Value.wrap(getObject, type)
            } catch (e: Exception) {
                val callingClass = callerNode.method.definingClass
                val message = e.message!!.replace(java_lang_reflect_Field_get::class.java.name, callingClass.binaryName)
                return throwException(e.javaClass, message)
            }
        } else {
            if (!Modifier.isStatic(accessFlags)) {
                // Instance field lookup isn't supported yet.
                val internalName: String = ClassNameUtils.toInternal(field.type)
                Value.unknown(internalName)
            }
            val className = ClassNameUtils.toInternal(field.declaringClass)
            val fieldClass = callerNode.classManager.getVirtualClass(className)
            val virtualField = fieldClass.getField(field.name)!!
            state.peekField(virtualField)
        }
        state.assignReturnRegister(getValue)
        return finish()
    }

    private fun checkForAccessException(callingClass: VirtualType, fieldClass: VirtualType, accessFlags: Int): Pair<Class<out Throwable>, String?>? {
        if (Modifier.isPublic(accessFlags) || callingClass == fieldClass || callingClass.isInnerClassOf(fieldClass)) {
            return null
        }
        val isPrivate = Modifier.isPrivate(accessFlags)
        val isProtected = Modifier.isProtected(accessFlags)
        val isPackagePrivate = !(isProtected || isPrivate)
        if (isPrivate ||
            isProtected && !callingClass.instanceOf(fieldClass) ||
            isPackagePrivate && !callingClass.isSamePackageOf(fieldClass)
        ) {
            val error =
                "${callingClass.binaryName} can't access a member of ${fieldClass.binaryName} with modifiers \"${Modifier.toString(accessFlags)}\""
            return Pair(IllegalAccessException::class.java, error)
        }
        return null
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_reflect_Field_get::class.java.simpleName)
    }
}
