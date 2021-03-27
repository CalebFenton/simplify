package org.cf.smalivm.emulate

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.ExecutionState
import org.cf.smalivm2.Value
import org.cf.smalivm2.VirtualMachine2
import org.cf.util.ClassNameUtils
import org.slf4j.LoggerFactory
import java.lang.reflect.Field
import java.lang.reflect.Modifier

internal class java_lang_reflect_Field_get : EmulatedMethodCall() {
    override fun execute(state: ExecutionState, callerNode: ExecutionNode, vm: VirtualMachine2) {
        val value = state.peekParameter(0)!!
        val instance = state.peekParameter(1)!!
        val field = value.value as Field
        val accessFlags = field.modifiers
        val fieldClassName: String = ClassNameUtils.toInternal(field.declaringClass)
        if (!field.isAccessible) {
            val callingClass = callerNode.method.definingClass
            val fieldClass = callerNode.classManager.getVirtualClass(fieldClassName)
            if (!checkAccess(callingClass, fieldClass, accessFlags)) {
                // TODO: should this throw an exception?
                return
            }
        }

        val getItem = get(field, instance, callerNode.method, state, fieldClassName, accessFlags, vm.classManager, vm.configuration)
            ?: // Must have thrown an exception
            return
        state.assignReturnRegister(getItem)
    }

    private fun checkAccess(callingClass: VirtualType, fieldClass: VirtualType, accessFlags: Int): Boolean {
        if (Modifier.isPublic(accessFlags)) {
            return true
        }
        if (callingClass == fieldClass) {
            return true
        }
        if (callingClass.isInnerClassOf(fieldClass)) {
            return true
        }
        val isPrivate = Modifier.isPrivate(accessFlags)
        val isProtected = Modifier.isProtected(accessFlags)
        val isPackagePrivate = !(isProtected || isPrivate)
        if (isPrivate || isProtected && !callingClass.instanceOf(fieldClass) ||
            isPackagePrivate && !callingClass.isSamePackageOf(fieldClass)
        ) {
            val error =
                "${callingClass.binaryName} can't access a member of ${fieldClass.binaryName} with modifiers \"${Modifier.toString(accessFlags)}\""
            addException(IllegalAccessException::class.java, error)
            return false
        }
        return true
    }

    private operator fun get(
        field: Field,
        instance: Any,
        callerMethod: VirtualMethod,
        state: ExecutionState,
        fieldClassName: String,
        accessFlags: Int,
        classManager: ClassManager,
        configuration: Configuration
    ): Value? {
        return if (configuration.isSafe(fieldClassName)) {
            getSafeField(field, instance, callerMethod)
        } else {
            if (!Modifier.isStatic(accessFlags)) {
                // Instance field lookup isn't supported yet.
                val internalName: String = ClassNameUtils.toInternal(field.type)
                return Value.unknown(internalName)
            }
            getVirtualField(field, state, classManager)
        }
    }

    private fun getSafeField(field: Field, instance: Any, callerMethod: VirtualMethod): Value? {
        return try {
            val getObject = field[instance]
            val type: String = ClassNameUtils.toInternal(field.type)
            Value.wrap(getObject, type)
        } catch (e: IllegalArgumentException) {
            val callingClass = callerMethod.definingClass
            val message = e.message!!.replace(java_lang_reflect_Field_get::class.java.name, callingClass.binaryName)
            addException(e.javaClass, message)
            return null
        } catch (e: IllegalAccessException) {
            val callingClass = callerMethod.definingClass
            val message = e.message!!.replace(java_lang_reflect_Field_get::class.java.name, callingClass.binaryName)
            addException(e.javaClass, message)
            return null
        }
    }

    private fun getVirtualField(field: Field, state: ExecutionState, classManager: ClassManager): Value {
        val className = ClassNameUtils.toInternal(field.declaringClass)
        val fieldClass = classManager.getVirtualClass(className)
        val virtualField = fieldClass.getField(field.name)!!
        return state.peekField(virtualField)
    }

    companion object {
        private val log = LoggerFactory.getLogger(java_lang_reflect_Field_get::class.java.simpleName)
    }
}
