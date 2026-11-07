package org.cf.smalivm.core

import com.android.tools.smali.dexlib2.immutable.ImmutableField

/** Immutable application field declaration and output metadata. */
class ApplicationFieldDefinition internal constructor(internal val declaration: ImmutableField) {
    val name: String get() = declaration.name
    val type: String get() = declaration.type
    val accessFlags: Int get() = declaration.accessFlags
}
