package org.cf.smalivm2

import org.cf.smalivm.SideEffect
import org.cf.smalivm.VirtualMachine
import org.cf.smalivm.context.*
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.opcode.OpCreator
import org.cf.smalivm.type.ClassManager
import org.cf.smalivm.type.VirtualField
import org.cf.smalivm.type.VirtualMethod
import org.cf.smalivm.type.VirtualType
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.BuilderInstruction
import org.jf.dexlib2.builder.MethodLocation
import org.jf.dexlib2.builder.MutableMethodImplementation
import org.slf4j.LoggerFactory
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class ExecutionGraph2 {
    private val method: VirtualMethod
    private val classManager: ClassManager
    private val classLoader: SmaliClassLoader
    protected val addressToLocation: MutableMap<Int, MethodLocation>
    protected val locationToNodePile: MutableMap<MethodLocation, MutableList<ExecutionNode>>
    private val terminatingAddresses: IntArray

    constructor(method: VirtualMethod, classManager: ClassManager, classLoader: SmaliClassLoader) {
        this.method = method
        this.classManager = classManager
        this.classLoader = classLoader

        val implementation = method.implementation
        addressToLocation = buildAddressToLocation(implementation)
        locationToNodePile = buildLocationToNodePile(addressToLocation, this.classManager)
        terminatingAddresses = buildTerminatingAddresses(implementation.instructions)
    }

    constructor(other: ExecutionGraph2, shallowClone: Boolean = true) {
        classManager = other.classManager
        method = other.method
        classLoader = other.classLoader
        terminatingAddresses = other.terminatingAddresses
        addressToLocation = other.addressToLocation

        if (shallowClone) {
            // Need to make shallow clones when spawning an instruction graph from a template
            // This is to avoid the cost of re-creating the graph (with most of the cost being creating ops)
            locationToNodePile = HashMap()
            for (location: MethodLocation in other.locationToNodePile.keys) {
                val otherNodePile = other.locationToNodePile[location]!!
                val nodePile: MutableList<ExecutionNode> = ArrayList(otherNodePile.size)
                for (n in otherNodePile) {
                    nodePile.add(ExecutionNode(n))
                }
                locationToNodePile[location] = nodePile
            }
        } else {
            locationToNodePile = other.locationToNodePile
        }
    }

    override fun toString(): String {
        return "ExecutionGraph{$method}"
    }

    fun addNode(node: ExecutionNode) {
        val location = node.op.instruction?.location
        locationToNodePile[location]!!.add(node)
    }

    private fun getConsensusType(types: MutableSet<String>, items: Set<HeapItem>): String {
        if (types.size == 1) {
            return types.toTypedArray()[0]
        }
        var newAncestors = 0
        do {
            /*
             * Collapse type hierarchies to get the most common type. For example, if types includes ChildClass and ParentClass, then the consensus
             * type should be ParentClass.
             */
            collapseTypeHierarchies(types, classManager)
            if (types.size == 1) {
                return types.toTypedArray()[0]
            }
            if (types.size == 2 && types.contains("I")) {
                // Dalvik uses 0 constant to represent null value
                // https://calebfenton.github.io/2016/02/16/how-does-dalvik-handle-null-types/
                for (currentType: String in types) {
                    if (currentType.startsWith("L") && items.contains(HeapItem(0, "I"))) {
                        return currentType
                    }
                }
            }

            // If there are still multiple possible types, check for common immediate ancestors.
            val newTypes: MutableSet<String> = HashSet()
            for (type: String in types) {
                val virtualType = classManager.getVirtualType(type)
                for (ancestor: VirtualType in virtualType.immediateAncestors) {
                    newTypes.add(ancestor.name)
                }
            }
            types.addAll(newTypes)
            newAncestors = newTypes.size
            collapseTypeHierarchies(types, classManager)
        } while (newAncestors > 0)
        return CommonTypes.UNKNOWN
    }

    /**
     * @return Naturally sorted array of all unique addresses in the graph.
     */
    fun getAddresses(): IntArray {
        return addressToLocation.keys.sorted().toIntArray()
    }

    fun getAllPossiblyInitializedClasses(addresses: IntArray): Set<VirtualType> {
        val allClasses: MutableSet<VirtualType> = HashSet()
        for (address: Int in addresses) {
            val pile = getNodePile(address)
            for (node: ExecutionNode in pile) {
                allClasses.addAll(node.context.initializedClasses)
            }
        }
        return allClasses
    }

    fun getConnectedTerminatingAddresses(): IntArray {
        val addresses: TIntList = TIntLinkedList()
        for (address: Int in terminatingAddresses) {
            if (wasAddressReached(address)) {
                addresses.add(address)
            }
        }
        return addresses.toArray()
    }

    fun getFieldConsensus(addresses: IntArray, field: VirtualField): HeapItem {
        val virtualClass = field.definingClass
        val items: MutableSet<HeapItem> = HashSet()
        for (address: Int in addresses) {
            // If the class wasn't initialized in one path, it's unknown
            for (node: ExecutionNode in getNodePile(address)) {
                if (!node.context.isClassInitialized(virtualClass)) {
                    val type = field.type
                    return HeapItem.newUnknown(type)
                }
            }
            items.addAll(getFieldItems(address, field))
            if (1 != items.size) {
                // Since items is a set, size == 1 implies consensus
                if (log.isTraceEnabled()) {
                    log.trace("No consensus for {}, returning Unknown.", field)
                }
                return HeapItem.newUnknown(field.type)
            }
        }
        return items.toTypedArray()[0]
    }

    fun getFieldItems(address: Int, field: VirtualField): Set<HeapItem> {
        val nodePile = getNodePile(address)
        val items: MutableSet<HeapItem> = HashSet(nodePile.size)
        for (node: ExecutionNode in nodePile) {
            val context = node.context
            val cState = context.peekClassState(field.definingClass)
            val item = cState.peekField(field)
            items.add(item)
        }
        return items
    }

    fun getHighestClassSideEffectLevel(virtualClass: VirtualType): SideEffect.Level {
        val addresses = connectedTerminatingAddresses
        var result = SideEffect.Level.NONE
        for (address: Int in addresses) {
            val pile = getNodePile(address)
            for (node: ExecutionNode in pile) {
                val level = node.context.getClassSideEffectLevel(virtualClass)
                    ?: // Maybe the class wasn't initialized.
                    continue
                when (level) {
                    SideEffect.Level.STRONG -> return level
                    SideEffect.Level.WEAK -> result = level
                    SideEffect.Level.NONE -> {
                    }
                }
            }
        }
        return result
    }

    fun getHighestMethodSideEffectLevel(): SideEffect.Level {
        var result = SideEffect.Level.NONE
        for (node: ExecutionNode in this) {
            val op = node.op
            val level = op.sideEffectLevel
            when (level) {
                SideEffect.Level.STRONG -> return level
                SideEffect.Level.WEAK -> result = level
                SideEffect.Level.NONE -> {
                }
            }
        }
        return result
    }

    fun getHighestSideEffectLevel(): SideEffect.Level {
        var result = highestMethodSideEffectLevel
        if (result == SideEffect.Level.STRONG) {
            return result
        }
        val addresses = connectedTerminatingAddresses
        val allClasses = getAllPossiblyInitializedClasses(addresses)
        for (virtualClass: VirtualType in allClasses) {
            val level = getHighestClassSideEffectLevel(virtualClass)
            when (level) {
                SideEffect.Level.STRONG -> return level
                SideEffect.Level.WEAK -> result = level
                SideEffect.Level.NONE -> {
                }
            }
        }
        return result
    }

    fun getLocations(): Collection<MethodLocation> {
        return addressToLocation.values
    }

    fun getMethod(): VirtualMethod {
        return method
    }

    fun getNodeCount(): Int {
        val totalSize = locationToNodePile.size
        val templateCount = locationToNodePile.keys.size
        return totalSize - templateCount
    }

    fun getNodePile(address: Int): List<ExecutionNode> {
        var nodePile = getNodePileByAddress(address)
        nodePile = nodePile!!.subList(1, nodePile.size) // exclude template
        return nodePile
    }

    fun getOp(address: Int): Op {
        return getTemplateNode(address)!!.op
    }

    /**
     * {@see #getRegisterConsensus(int[], int)}
     *
     * @return consensus value over all `addresses` in `register` or [ ] if a consensus doesn't exist
     */
    override fun getRegisterConsensus(address: Int, register: Int): HeapItem {
        return getRegisterConsensus(intArrayOf(address), register)!!
    }

    /**
     * Look at the value in `register` at each of the `addresses`. If all values are
     * identical, that means there is a consensus. It means every exeuction path had the same value
     * at those particular `addresses` for that particular `register`. If there is more
     * than one value, it means it's not possible to know with certainty what the value is. For
     * example, consider the following method:
     *
     * <pre>`foo()I
     * v0 = readStringFromNetwork()Ljava/lang/String; // won't be executed because unsafe
     * if v0 == "the spice must flow":
     * v1 = 1
     * else:
     * v1 = 0
     * return v1`</pre>
     *
     *
     * Since the result of `readStringFromNetwork()Ljava/lang/String;` won't be known, because
     * it's probably unsafe to virtually execute, the `if v0` will be ambiguous and every
     * possible execution path will be taken. This means `return v1` could either be `1`
     * or `0`. This means there is no consensus. In this case, an [UnknownValue] will
     * be returned.
     *
     * @return consensus value over all `addresses` in `register` or [ ] if a consensus doesn't exist
     */
    override fun getRegisterConsensus(addresses: IntArray, register: Int): HeapItem? {
        val items: MutableSet<HeapItem> = HashSet()
        for (address: Int in addresses) {
            items.addAll(getRegisterItems(address, register))
        }
        if (items.size == 1) {
            return items.toTypedArray()[0]
        }
        log.trace("No consensus value for register {}; returning UnknownValue", register)

        // Determine correct type for UnknownValue
        val types: MutableSet<String> = HashSet(items.size)
        for (item: HeapItem? in items) {
            if (item == null) {
                // Register was not assigned in this execution path.
                // This can happen in short methods which terminate early.
                continue
            }
            types.add(item.type)
        }
        if (types.size == 0) {
            // May not have any items if an exception was thrown and checking return register
            log.warn(
                "No types for consensus; using *unknown* type! method={}, addresses={}, " + "register={}", getMethod().signature, addresses,
                register, types
            )
            return HeapItem.newUnknown(CommonTypes.UNKNOWN)
        } else {
            var type = getConsensusType(types, items)
            if (type == CommonTypes.UNKNOWN) {
                if (register == MethodState.ReturnRegister) {
                    log.warn(
                        "Strange: No consensus type for return register; using method return type, method={}, addresses={}, " + "register={}, "
                                + "types={}", getMethod().signature, addresses, register, types
                    )
                    type = method.returnType
                } else {
                    log.warn(
                        "No consensus type; using *unknown* type! method={}, addresses={}, " + "register={}, types={}",
                        getMethod().signature, addresses, register, types
                    )
                }
            }
            return HeapItem.newUnknown(type)
        }
    }

    override fun getRegisterConsensusValue(address: Int, register: Int): Any {
        val item = getRegisterConsensus(address, register)
        return (item.value)!!
    }

    override fun getRegisterConsensusValue(addresses: IntArray, register: Int): Any? {
        val item = getRegisterConsensus(addresses, register)
        return item!!.value
    }

    /**
     * This gives you every possible value for a given `address` and `register`.
     *
     * @return returns items at `address` in `register` for every execution path
     */
    fun getRegisterItems(address: Int, register: Int): Set<HeapItem> {
        val nodePile = getNodePile(address)
        val items: MutableSet<HeapItem> = HashSet(nodePile.size)
        for (node: ExecutionNode in nodePile) {
            val mState = node.context.methodState
            val item = mState.peekRegister(register)
            items.add((item)!!)
        }
        return items
    }

    fun spawnRootNode(): ExecutionNode {
        val node = getTemplateNode(METHOD_ROOT_ADDRESS)
        return ExecutionNode(node)
    }

    fun getTemplateNode(address: Int): ExecutionNode {
        val nodePile = getNodePileByAddress(address)
        return nodePile!![TEMPLATE_NODE_INDEX]
    }

    /**
     * @return all terminating addresses (return, throw)
     */
    fun getTerminatingAddresses(): IntArray {
        return terminatingAddresses
    }

    override fun getTerminatingContexts(): List<ExecutionContext> {
        val contexts: MutableList<ExecutionContext> = LinkedList()
        val addresses = connectedTerminatingAddresses
        for (address: Int in addresses) {
            contexts.addAll(
                getNodePile(address).stream().map({ obj: ExecutionNode -> obj.getContext() }).collect(Collectors.toList())
            )
        }
        return contexts
    }

    override fun getTerminatingFieldConsensus(field: VirtualField): HeapItem {
        val items = getTerminatingFieldConsensus(arrayOf(field))
        return (items[field])!!
    }

    override fun getTerminatingFieldConsensus(fields: Array<VirtualField>): Map<VirtualField, HeapItem> {
        val addresses = connectedTerminatingAddresses
        val result: MutableMap<VirtualField, HeapItem> = HashMap()
        for (field: VirtualField in fields) {
            val item = getFieldConsensus(addresses, field)
            result[field] = item
        }
        return result
    }

    override fun getTerminatingRegisterConsensus(register: Int): HeapItem? {
        val items = getTerminatingRegisterConsensus(intArrayOf(register))
        return items!![register]
    }

    override fun getTerminatingRegisterConsensus(registers: IntArray): Map<Int, HeapItem>? {
        val addresses = connectedTerminatingAddresses
        val result: MutableMap<Int, HeapItem> = HashMap(registers.size)
        for (register: Int in registers) {
            val item = getRegisterConsensus(addresses, register)
            result[register] = (item)!!
        }
        return result
    }

    override fun getVM(): VirtualMachine {
        return vm
    }

    override fun iterator(): MutableIterator<ExecutionNode> {
        return ExecutionGraphIterator(this)
    }

    override fun wasAddressReached(address: Int): Boolean {
        if (METHOD_ROOT_ADDRESS == address) {
            // Root is always reachable
            return true
        }

        // If this address was reached during execution there will be clones in the pile.
        val nodePile = getNodePileByAddress(address)
        if ((nodePile == null) || (1 > nodePile.size)) {
            log.warn("Node pile @{} has no template node.", address)
            return false
        }
        return nodePile.size > 1
    }

    override fun toSmali(): String {
        return toSmali(false)
    }

    override fun toSmali(includeAddress: Boolean): String {
        val addresses = addresses
        val sb = StringBuilder()
        for (address: Int in addresses) {
            val op = getOp(address)
            sb.append(op.toString())
            if (includeAddress) {
                sb.append(" # @").append(address)
            }
            sb.append('\n')
        }
        sb.setLength(sb.length - 1)
        return sb.toString()
    }

    protected fun getNodeIndex(node: ExecutionNode): Int {
        return getNodePile(node.address).indexOf(node)
    }

    private fun getNodePileByAddress(address: Int): MutableList<ExecutionNode> {
        val location = addressToLocation[address]
        return locationToNodePile[location]!!
    }

    companion object {
        protected val TEMPLATE_NODE_INDEX = 0
        protected val METHOD_ROOT_ADDRESS = 0

        private val log = LoggerFactory.getLogger(ExecutionGraph2::class.java.simpleName)

        protected fun buildAddressToLocation(implementation: MutableMethodImplementation): MutableMap<Int, MethodLocation> {
            val instructions = implementation.instructions
            val addressToLocation: MutableMap<Int, MethodLocation> = HashMap(instructions.size)
            for (instruction in instructions) {
                val location = instruction.location
                addressToLocation[location.codeAddress] = instruction.location
            }
            return addressToLocation
        }

        protected fun getOpCreator(addressToLocation: Map<Int, MethodLocation>, classManager: ClassManager): OpCreator {
            return OpCreator(addressToLocation, classManager)
        }

        private fun buildLocationToNodePile(addressToLocation: Map<Int, MethodLocation>, classManager: ClassManager): MutableMap<MethodLocation, MutableList<ExecutionNode>> {
            val opCreator = getOpCreator(addressToLocation, classManager)
            val locationToNodePile: MutableMap<MethodLocation, MutableList<ExecutionNode>> = HashMap()
            for (location in addressToLocation.values) {
                val op = opCreator.create(location)
                val node = ExecutionNode(op)

                // Most node piles will be a template node and 1+ ExecutionNodes.
                val pile: MutableList<ExecutionNode> = ArrayList(2)
                pile.add(node)
                locationToNodePile[location] = pile
            }
            return locationToNodePile
        }

        private fun buildTerminatingAddresses(instructions: List<BuilderInstruction>): IntArray {
            val addresses: MutableList<Int> = LinkedList()
            for (instruction in instructions) {
                val address = instruction.location.codeAddress
                if (instruction.opcode == Opcode.RETURN_VOID || instruction.opcode == Opcode.RETURN || instruction.opcode == Opcode.RETURN_WIDE || instruction.opcode == Opcode.RETURN_OBJECT || instruction.opcode == Opcode.THROW) {
                    addresses.add(address)
                }

            }
            return addresses.toIntArray()
        }

        private fun collapseTypeHierarchies(types: MutableSet<String>, classManager: ClassManager) {
            val typeList: MutableList<VirtualType> = LinkedList()
            for (type: String in types) {
                typeList.add(classManager.getVirtualType(type))
            }
            for (index in typeList.indices) {
                val currentType = typeList[index]
                for (i in typeList.indices) {
                    if (i == index) {
                        continue
                    }
                    val compareType = typeList[i]
                    if (currentType.isChildOf(compareType)) {
                        types.remove(currentType.name)
                        break
                    }
                }
            }
        }
    }
}
