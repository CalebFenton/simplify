package org.cf.smalivm

import com.google.common.base.Defaults
import org.slf4j.LoggerFactory
import java.lang.reflect.InvocationTargetException

object ObjectInstantiator {
    private val log = LoggerFactory.getLogger(ObjectInstantiator::class.java.simpleName)

    @Throws(InstantiationException::class, IllegalAccessException::class, IllegalArgumentException::class, InvocationTargetException::class)
    fun newInstance(klazz: Class<*>): Any {
        if (Enum::class.java.isAssignableFrom(klazz)) {
            return newEnumInstance(klazz)
        }
        var newInstance: Any
        try {
            // Try it without any funny business.
            newInstance = klazz.newInstance()
        } catch (e: Exception) {
            if (e !is InstantiationException || e !is IllegalAccessException) {
                throw e
            }
            log.trace("{} has no default constructor, picking another", klazz)

            // Alright, we'll try it the hard way.
            val ctor = klazz.declaredConstructors[0]
            ctor.isAccessible = true // the little games we play, java...
            val args = arrayOfNulls<Any>(ctor.parameterCount)
            val parameterTypes = ctor.parameterTypes
            var i = 0
            while (i < args.size) {
                val parameterType = parameterTypes[i]
                args[i] = Defaults.defaultValue(parameterType)
                i++
            }
            newInstance = ctor.newInstance(*args)
        }
        return newInstance
    }

    private fun newEnumInstance(klazz: Class<*>): Any {
        return java.lang.Enum.valueOf(klazz as Class<out Enum<*>?>, "\$shadow_instance")!!
    }
}