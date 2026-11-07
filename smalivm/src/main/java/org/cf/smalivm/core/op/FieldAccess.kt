package org.cf.smalivm.core.op

import org.cf.smalivm.core.Coerce
import org.cf.smalivm.core.OpUpdate
import org.cf.smalivm.core.SideEffectLevel
import org.cf.smalivm.core.Value
import org.cf.smalivm.core.VirtualType
import org.cf.smalivm.core.readsAsNullReference

/**
 * The assignment a field read makes: a reference-typed field reads a cell holding primitive bits as `null`,
 * the way ART fetches a reference, and a volatile read records `WEAK`.
 */
internal fun fieldRead(destRegister: Int, cell: Value, fieldType: VirtualType, isVolatile: Boolean): OpUpdate {
    val loaded = if (fieldType.isReference && cell.readsAsNullReference) Value.Known(null, fieldType) else cell
    val assign = OpUpdate.assign(destRegister, loaded)
    return if (isVolatile) assign.then(OpUpdate.effect(SideEffectLevel.WEAK)) else assign
}

/**
 * The value a field write stores. Only the `-object` form reads its value operand as a reference, so only it
 * stores a primitive register as `null`; a narrow field keeps its own width ([Coerce.narrowStore]).
 */
internal fun fieldWrite(value: Value, fieldType: VirtualType, storesAReference: Boolean): Value =
    if (storesAReference && value.readsAsNullReference) Value.Known(null, fieldType) else Coerce.narrowStore(value, fieldType)
