import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.Op
import org.cf.util.ClassNameUtils

public class ExceptionFactory internal constructor(private val classLoader: SmaliClassLoader) {
    @JvmOverloads
    // TODO: op isn't needed? remove this? maybe this whole factory can get moved to node
    fun build(op: Op, exceptionClass: Class<out Throwable>, message: String? = null): Throwable {
        try {
            val ctor = exceptionClass.getDeclaredConstructor(String::class.java)
            ctor.isAccessible = true
            return ctor.newInstance(message)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Exception()
    }

//    @JvmOverloads
//    fun build(op: Op, className: String, message: String? = null): Throwable {
//        val binaryName = ClassNameUtils.internalToBinary(className)
//        try {
//            val exceptionClass = classLoader.loadClass(binaryName) as Class<Throwable>
//            return build(op, exceptionClass, message)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return Exception()
//    }
}

