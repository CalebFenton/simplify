package org.cf.smalivm.type

import com.android.tools.smali.dexlib2.writer.builder.BuilderField
import com.android.tools.smali.dexlib2.formatter.DexFormatter
import java.lang.reflect.Modifier

class VirtualField internal constructor(val builderField: BuilderField, virtualClass: VirtualClass?) : VirtualClassItem(builderField, virtualClass) {

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