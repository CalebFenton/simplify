package org.cf.smalivm.opcode

import org.cf.smalivm.*
import org.cf.smalivm.context.ExecutionNode
import org.cf.smalivm.context.HeapItem
import org.cf.smalivm.context.MethodState
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.opcode.BinaryMathOp
import org.cf.smalivm.type.UnknownValue
import org.cf.util.Utils
import org.jf.dexlib2.builder.MethodLocation
import org.slf4j.LoggerFactory

class BinaryMathOp private constructor(
    location: MethodLocation, child: MethodLocation, private val destRegister: Int, private val arg1Register: Int,
    exceptionFactory: ExceptionFactory
) : MethodStateOp(location, child) {
    private val mathOperandType: MathOperandType?
    private val mathOperator: MathOperator?
    private var arg2Register = 0
    private var hasLiteral = false
    private var narrowLiteral = 0

    internal constructor(
        location: MethodLocation, child: MethodLocation, destRegister: Int, arg1Register: Int, otherValue: Int,
        hasLiteral: Boolean, exceptionFactory: ExceptionFactory
    ) : this(location, child, destRegister, arg1Register, exceptionFactory) {
        this.hasLiteral = hasLiteral
        if (hasLiteral) {
            narrowLiteral = otherValue
        } else {
            arg2Register = otherValue
        }
    }

    override fun execute(node: ExecutionNode, mState: MethodState) {
        val lhsItem = mState.readRegister(arg1Register)
        val rhsItem: HeapItem
        rhsItem = if (hasLiteral) {
            HeapItem(narrowLiteral, CommonTypes.INTEGER)
        } else {
            mState.readRegister(arg2Register)
        }
        var result: Any? = null
        if (!lhsItem.isUnknown && !rhsItem.isUnknown) {
            result = getResult(lhsItem.value, rhsItem.value)
            if (result is Throwable) {
                node.setException(result)
                node.clearChildren()
                return
            } else {
                node.clearExceptions()
            }
        }
        if (null == result) {
            result = UnknownValue()
        }
        mState.assignRegister(destRegister, result, mathOperandType!!.type)
    }

    override fun toString(): String {
        val sb = StringBuilder(name)
        sb.append(" r").append(destRegister).append(", r")
        val is2Addr = name.endsWith("/2addr")
        if (is2Addr) {
            sb.append(arg2Register)
        } else {
            sb.append(arg1Register)
            if (hasLiteral) {
                sb.append(", 0x").append(Integer.toHexString(narrowLiteral))
            } else {
                sb.append(", r").append(arg2Register)
            }
        }
        return sb.toString()
    }

    @Nonnull
    private fun getResult(lhs: Any?, rhs: Any?): Any? {
        var lhs = lhs
        var rhs = rhs
        val result: Any?
        when (mathOperandType) {
            MathOperandType.INT -> {
                lhs = Utils.getIntegerValue(lhs)
                rhs = Utils.getIntegerValue(rhs)
                result = doIntegerOperation(mathOperator, lhs, rhs)
            }
            MathOperandType.LONG -> {
                lhs = Utils.getLongValue(lhs)
                rhs = Utils.getLongValue(rhs)
                result = doLongOperation(mathOperator, lhs, rhs)
            }
            MathOperandType.FLOAT -> {
                lhs = Utils.getFloatValue(lhs)
                rhs = Utils.getFloatValue(rhs)
                result = doFloatOperation(mathOperator, lhs, rhs)
            }
            MathOperandType.DOUBLE -> {
                lhs = Utils.getDoubleValue(lhs)
                rhs = Utils.getDoubleValue(rhs)
                result = doDoubleOperation(mathOperator, lhs, rhs)
            }
            else -> throw RuntimeException("Unknown math operand class!")
        }
        return result
    }

    private enum class MathOperandType(val type: String) {
        DOUBLE(CommonTypes.DOUBLE), FLOAT(CommonTypes.FLOAT), INT(CommonTypes.INTEGER), LONG(CommonTypes.LONG);

    }

    private enum class MathOperator {
        ADD, AND, DIV, MUL, OR, REM, RSUB, SHL, SHR, SUB, USHR, XOR
    }

    companion object {
        private val log = LoggerFactory.getLogger(BinaryMathOp::class.java.simpleName)
        private fun doDoubleOperation(mathOperator: MathOperator?, lhs: Double?, rhs: Double?): Any? {
            var result: Any? = null
            when (mathOperator) {
                MathOperator.ADD -> result = lhs!! + rhs!!
                MathOperator.DIV -> result = lhs!! / rhs!!
                MathOperator.MUL -> result = lhs!! * rhs!!
                MathOperator.REM -> result = lhs!! % rhs!!
                MathOperator.SUB -> result = lhs!! - rhs!!
            }
            return result
        }

        private fun doFloatOperation(mathOperator: MathOperator?, lhs: Float?, rhs: Float?): Any? {
            var result: Any? = null
            when (mathOperator) {
                MathOperator.ADD -> result = lhs!! + rhs!!
                MathOperator.DIV -> result = lhs!! / rhs!!
                MathOperator.MUL -> result = lhs!! * rhs!!
                MathOperator.REM -> result = lhs!! % rhs!!
                MathOperator.SUB -> result = lhs!! - rhs!!
            }
            return result
        }

        private fun doIntegerOperation(mathOperator: MathOperator?, lhs: Int?, rhs: Int?): Any? {
            var result: Any? = null
            try {
                when (mathOperator) {
                    MathOperator.ADD -> result = lhs!! + rhs!!
                    MathOperator.AND -> result = lhs!! and rhs!!
                    MathOperator.DIV -> result = lhs!! / rhs!!
                    MathOperator.MUL -> result = lhs!! * rhs!!
                    MathOperator.OR -> result = lhs!! or rhs!!
                    MathOperator.REM -> result = lhs!! % rhs!!
                    MathOperator.RSUB -> result = rhs!! - lhs!!
                    MathOperator.SHL -> result = lhs!! shl (rhs!! and 0x1f)
                    MathOperator.SHR -> result = lhs!! shr (rhs!! and 0x1f)
                    MathOperator.SUB -> result = lhs!! - rhs!!
                    MathOperator.USHR -> result = lhs!! ushr (rhs!! and 0x1f)
                    MathOperator.XOR -> result = lhs!! xor rhs!!
                }
            } catch (e: ArithmeticException) {
                return e
            }
            return result
        }

        private fun doLongOperation(mathOperator: MathOperator?, lhs: Long?, rhs: Long?): Any? {
            var result: Any? = null
            try {
                when (mathOperator) {
                    MathOperator.ADD -> result = lhs!! + rhs!!
                    MathOperator.AND -> result = lhs!! and rhs!!
                    MathOperator.DIV -> result = lhs!! / rhs!!
                    MathOperator.MUL -> result = lhs!! * rhs!!
                    MathOperator.OR -> result = lhs!! or rhs!!
                    MathOperator.REM -> result = lhs!! % rhs!!
                    MathOperator.SHL -> result = lhs!! shl rhs
                    MathOperator.SHR -> result = lhs!! shr rhs
                    MathOperator.SUB -> result = lhs!! - rhs!!
                    MathOperator.USHR -> result = lhs!! ushr rhs
                    MathOperator.XOR -> result = lhs!! xor rhs!!
                }
            } catch (e: ArithmeticException) {
                return e
            }
            return result
        }

        private fun getMathOp(opName: String?): MathOperator? {
            var result: MathOperator? = null
            if (opName!!.startsWith("add")) {
                result = MathOperator.ADD
            } else if (opName.startsWith("sub")) {
                result = MathOperator.SUB
            } else if (opName.startsWith("mul")) {
                result = MathOperator.MUL
            } else if (opName.startsWith("div")) {
                result = MathOperator.DIV
            } else if (opName.startsWith("rem")) {
                result = MathOperator.REM
            } else if (opName.startsWith("and")) {
                result = MathOperator.AND
            } else if (opName.startsWith("or")) {
                result = MathOperator.OR
            } else if (opName.startsWith("xor")) {
                result = MathOperator.XOR
            } else if (opName.startsWith("shl")) {
                result = MathOperator.SHL
            } else if (opName.startsWith("shr")) {
                result = MathOperator.SHR
            } else if (opName.startsWith("ushr")) {
                result = MathOperator.USHR
            } else if (opName.startsWith("rsub")) {
                result = MathOperator.RSUB
            }
            return result
        }

        private fun getMathOperandType(opName: String?): MathOperandType? {
            var result: MathOperandType? = null
            if (opName!!.contains("-int")) {
                result = MathOperandType.INT
            } else if (opName.contains("-double")) {
                result = MathOperandType.DOUBLE
            } else if (opName.contains("-float")) {
                result = MathOperandType.FLOAT
            } else if (opName.contains("-long")) {
                result = MathOperandType.LONG
            }
            return result
        }
    }

    init {
        mathOperator = getMathOp(name)
        mathOperandType = getMathOperandType(name)
        addException(exceptionFactory.build(this, ArithmeticException::class.java, "/ by zero"))
    }
}
