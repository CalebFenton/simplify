package org.cf.smalivm.opcode

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.UnknownValue
import org.cf.smalivm2.ExecutionNode
import org.cf.smalivm2.UnresolvedChild
import org.cf.smalivm2.Value
import org.cf.util.Utils
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.iface.instruction.NarrowLiteralInstruction
import org.jf.dexlib2.iface.instruction.TwoRegisterInstruction
import org.jf.dexlib2.iface.instruction.formats.Instruction12x
import org.jf.dexlib2.iface.instruction.formats.Instruction23x
import org.slf4j.LoggerFactory

class BinaryMathOp internal constructor(
    location: MethodLocation,
    child: MethodLocation,
    private val destRegister: Int,
    private val arg1Register: Int,
) : Op(location, child, Pair(ArithmeticException::class.java, "/ by zero")) {

    private val mathOperandType = getMathOperandType(name)
    private val mathOperator = getMathOp(name)
    private var arg2Register = 0
    private var hasLiteral = false
    private var narrowLiteral = 0

    internal constructor(
        location: MethodLocation,
        child: MethodLocation,
        destRegister: Int,
        arg1Register: Int,
        otherValue: Int,
        hasLiteral: Boolean,
    ) : this(location, child, destRegister, arg1Register) {
        this.hasLiteral = hasLiteral
        if (hasLiteral) {
            narrowLiteral = otherValue
        } else {
            arg2Register = otherValue
        }
    }

    override val registersReadCount = if (hasLiteral) 1 else 2
    override val registersAssignedCount = 1

    override fun execute(node: ExecutionNode): Array<out UnresolvedChild> {
        val lhs = node.state.readRegister(arg1Register)
        val rhs: Value = if (hasLiteral) {
            Value.wrap(narrowLiteral, CommonTypes.INTEGER)
        } else {
            node.state.readRegister(arg2Register)
        }

        val result = if (lhs.isKnown && rhs.isKnown) {
            getResult(lhs, rhs)
        } else {
            UnknownValue()
        }

        if (result is Throwable) {
            return throwException(result)
        }
        node.state.assignRegister(destRegister, result, mathOperandType.type)
        return finishOp()
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

    private fun getResult(lhs: Value, rhs: Value): Any {
        return when (mathOperandType) {
            MathOperandType.INT -> doIntegerOperation(mathOperator, lhs.toInteger(), rhs.toInteger())
            MathOperandType.LONG -> doLongOperation(mathOperator, lhs.toLong(), rhs.toLong())
            MathOperandType.FLOAT -> doFloatOperation(mathOperator, lhs.toFloat(), rhs.toFloat())
            MathOperandType.DOUBLE -> doDoubleOperation(mathOperator, lhs.toDouble(), rhs.toDouble())
        }
    }

    private enum class MathOperandType(val type: String) {
        DOUBLE(CommonTypes.DOUBLE), FLOAT(CommonTypes.FLOAT), INT(CommonTypes.INTEGER), LONG(CommonTypes.LONG);
    }

    private enum class MathOperator {
        ADD, AND, DIV, MUL, OR, REM, RSUB, SHL, SHR, SUB, USHR, XOR
    }

    companion object : OpFactory {
        private val log = LoggerFactory.getLogger(BinaryMathOp::class.java.simpleName)

        override fun build(
            location: MethodLocation,
            addressToLocation: Map<Int, MethodLocation>,
            classManager: ClassManager,
            classLoader: SmaliClassLoader,
            configuration: Configuration
        ): Op {
            val child = Utils.getNextLocation(location, addressToLocation)
            val instruction = location.instruction as BuilderInstruction
            val instr = location.instruction as TwoRegisterInstruction
            val destRegister = instr.registerA
            var arg1Register = instr.registerB
            if (instruction is Instruction23x) {
                // add-int vAA, vBB, vCC
                val arg2Register = (instruction as Instruction23x).registerC
                return BinaryMathOp(location, child, destRegister, arg1Register, arg2Register, false)
            } else if (instruction is Instruction12x) {
                // add-int/2addr vAA, vBB
                arg1Register = instr.registerA
                val arg2Register = (instruction as Instruction12x).registerB
                return BinaryMathOp(location, child, destRegister, arg1Register, arg2Register, false)
            } else if (instruction is NarrowLiteralInstruction) {
                // Instruction22b - add-int/lit8 vAA, vBB, #CC
                // Instruction22s - add-int/lit16 vAA, vBB, #CCCC
                val arg2Literal = (instruction as NarrowLiteralInstruction).narrowLiteral
                return BinaryMathOp(location, child, destRegister, arg1Register, arg2Literal, true)
            } else {
                throw IllegalArgumentException("Unexpected instruction format for binary math op: $instruction")
            }
        }

        private fun doDoubleOperation(mathOperator: MathOperator, lhs: Double, rhs: Double): Any {
            return when (mathOperator) {
                MathOperator.ADD -> lhs + rhs
                MathOperator.DIV -> lhs / rhs
                MathOperator.MUL -> lhs * rhs
                MathOperator.REM -> lhs % rhs
                MathOperator.SUB -> lhs - rhs
                else -> {
                    log.error("Unexpected math operator for double operation: {}", mathOperator)
                    lhs
                }
            }
        }

        private fun doFloatOperation(mathOperator: MathOperator, lhs: Float, rhs: Float): Any {
            return when (mathOperator) {
                MathOperator.ADD -> lhs + rhs
                MathOperator.DIV -> lhs / rhs
                MathOperator.MUL -> lhs * rhs
                MathOperator.REM -> lhs % rhs
                MathOperator.SUB -> lhs - rhs
                else -> {
                    log.error("Unexpected math operator for float operation: {}", mathOperator)
                    lhs
                }
            }
        }

        private fun doIntegerOperation(mathOperator: MathOperator, lhs: Int, rhs: Int): Any {
            try {
                return when (mathOperator) {
                    MathOperator.ADD -> lhs + rhs
                    MathOperator.AND -> lhs and rhs
                    MathOperator.DIV -> lhs / rhs
                    MathOperator.MUL -> lhs * rhs
                    MathOperator.OR -> lhs or rhs
                    MathOperator.REM -> lhs % rhs
                    MathOperator.RSUB -> rhs - lhs
                    MathOperator.SHL -> lhs shl (rhs and 0x1f)
                    MathOperator.SHR -> lhs shr (rhs and 0x1f)
                    MathOperator.SUB -> lhs - rhs
                    MathOperator.USHR -> lhs ushr (rhs and 0x1f)
                    MathOperator.XOR -> lhs xor rhs
                }
            } catch (e: ArithmeticException) {
                return e
            }
        }

        private fun doLongOperation(mathOperator: MathOperator, lhs: Long, rhs: Long): Any {
            return try {
                when (mathOperator) {
                    MathOperator.ADD -> lhs + rhs
                    MathOperator.AND -> lhs and rhs
                    MathOperator.DIV -> lhs / rhs
                    MathOperator.MUL -> lhs * rhs
                    MathOperator.OR -> lhs or rhs
                    MathOperator.REM -> lhs % rhs
                    MathOperator.SHL -> lhs shl rhs.toInt()
                    MathOperator.SHR -> lhs shr rhs.toInt()
                    MathOperator.SUB -> lhs - rhs
                    MathOperator.USHR -> lhs ushr rhs.toInt()
                    MathOperator.XOR -> lhs xor rhs
                    else -> {
                        log.error("Unexpected math operator for long operation: {}", mathOperator)
                        lhs
                    }
                }
            } catch (e: ArithmeticException) {
                e
            }
        }

        private fun getMathOp(opName: String): MathOperator {
            return when {
                opName.startsWith("add") -> MathOperator.ADD
                opName.startsWith("sub") -> MathOperator.SUB
                opName.startsWith("mul") -> MathOperator.MUL
                opName.startsWith("div") -> MathOperator.DIV
                opName.startsWith("rem") -> MathOperator.REM
                opName.startsWith("and") -> MathOperator.AND
                opName.startsWith("or") -> MathOperator.OR
                opName.startsWith("xor") -> MathOperator.XOR
                opName.startsWith("shl") -> MathOperator.SHL
                opName.startsWith("shr") -> MathOperator.SHR
                opName.startsWith("ushr") -> MathOperator.USHR
                opName.startsWith("rsub") -> MathOperator.RSUB
                else -> throw IllegalArgumentException("Unexpected binary math op name: $opName")
            }
        }

        private fun getMathOperandType(opName: String): MathOperandType {
            return when {
                opName.contains("-int") -> MathOperandType.INT
                opName.contains("-double") -> MathOperandType.DOUBLE
                opName.contains("-float") -> MathOperandType.FLOAT
                opName.contains("-long") -> MathOperandType.LONG
                else -> throw IllegalArgumentException("Unexpected binary math op operand: $opName")
            }
        }
    }
}
