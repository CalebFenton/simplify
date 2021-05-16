package org.cf.smalivm.type

import org.jf.dexlib2.writer.builder.BuilderField
import org.cf.smalivm.type.VirtualClass
import org.cf.smalivm.type.VirtualClassItem
import org.cf.smalivm.type.UnknownValue
import org.jf.dexlib2.ValueType
import org.jf.dexlib2.writer.builder.BuilderEncodedValues.BuilderEncodedValue
import org.jf.dexlib2.iface.value.ByteEncodedValue
import org.jf.dexlib2.iface.value.ShortEncodedValue
import org.jf.dexlib2.iface.value.CharEncodedValue
import org.jf.dexlib2.iface.value.IntEncodedValue
import org.jf.dexlib2.iface.value.LongEncodedValue
import org.jf.dexlib2.iface.value.FloatEncodedValue
import org.jf.dexlib2.iface.value.DoubleEncodedValue
import org.jf.dexlib2.iface.value.StringEncodedValue
import org.jf.dexlib2.iface.value.BooleanEncodedValue
import org.jf.dexlib2.formatter.DexFormatter
import java.lang.reflect.Modifier

class VirtualField internal constructor(val builderField: BuilderField, virtualClass: VirtualClass?) : VirtualClassItem(builderField, virtualClass) {

    // I *believe* these are the only types which can be literals
    val initialValue: Any?
        get() {
            if (!Modifier.isStatic(builderField.accessFlags)) {
                return UnknownValue()
            }
            val bev = builderField.initialValue
            if (bev != null) {
                // I *believe* these are the only types which can be literals
                when (bev.valueType) {
                    ValueType.BYTE -> return (bev as ByteEncodedValue).value
                    ValueType.SHORT -> return (bev as ShortEncodedValue).value
                    ValueType.CHAR -> return (bev as CharEncodedValue).value
                    ValueType.INT -> return (bev as IntEncodedValue).value
                    ValueType.LONG -> return (bev as LongEncodedValue).value
                    ValueType.FLOAT -> return (bev as FloatEncodedValue).value
                    ValueType.DOUBLE -> return (bev as DoubleEncodedValue).value
                    ValueType.STRING -> return (bev as StringEncodedValue).value
                    ValueType.BOOLEAN -> return (bev as BooleanEncodedValue).value
                }
            } else {
                when (type[0]) {
                    'Z' -> return false
                    'B' -> return 0.toByte()
                    'S' -> return 0.toShort()
                    'C' -> return '\u0000'
                    'I' -> return 0
                    'J' -> return 0L
                    'F' -> return 0f
                    'D' -> return 0.0
                }
            }
            return null
        }

    override fun getName(): String {
        return builderField.name
    }

    override fun getSignature(): String {
        return DexFormatter.INSTANCE.getFieldDescriptor(builderField)
    }

    override fun toString(): String {
        return signature
    }

    val type: String
        get() = builderField.type

    val isStatic: Boolean
        get() = Modifier.isStatic(builderField.accessFlags)
}