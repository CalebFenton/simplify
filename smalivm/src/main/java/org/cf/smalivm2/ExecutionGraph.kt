package org.cf.smalivm2

import com.google.common.primitives.Ints
import org.cf.smalivm.SideEffectLevel
import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.dex.CommonTypes
import org.cf.smalivm.opcode.*
import org.cf.smalivm.type.*
import org.jf.dexlib2.Opcode
import org.jf.dexlib2.builder.*
import org.jf.dexlib2.writer.builder.DexBuilder
import org.slf4j.LoggerFactory
import java.util.*
import java.util.function.Consumer


internal class ExecutionGraphIterator(graph: ExecutionGraph2) : MutableIterator<ExecutionNode> {
    private val stack: Deque<ExecutionNode>

    override fun hasNext(): Boolean {
        return !stack.isEmpty()
    }

    override fun next(): ExecutionNode {
        val head = stack.poll()
        stack.addAll(head.children)
        return head
    }

    override fun remove() {
        throw UnsupportedOperationException()
    }

    init {
        stack = LinkedList()
        if (graph.getNodePile(0).size > 0) {
            stack.push(graph.root)
        }
    }
}

data class UnhandledVirtualException(val node: ExecutionNode, val exceptionClass: VirtualClass, val message: String?)

class ExecutionGraph2(
    val method: VirtualMethod,
    val vm: VirtualMachine2,
) : Iterable<ExecutionNode> {
    val classManager
        get() = vm.classManager
    val classLoader
        get() = vm.classLoader
    val configuration: Configuration
        get() = vm.configuration
    val dexBuilder: DexBuilder
        get() = classManager.dexBuilder
    val locationToOp
        get() = vm.methodToTemplateOps[method]!!

    // TODO: should terminating addresses be recalculated when graphs are rebuilt? same with address -> location
    val terminatingAddresses = buildTerminatingAddresses(vm.methodToTemplateOps[method]!!.keys)
    val addressToLocation = buildAddressToLocation(vm.methodToTemplateOps[method]!!.keys)
    val locationToNodePile: MutableMap<MethodLocation, MutableList<ExecutionNode>> = HashMap()
    val unhandledVirtualException: MutableList<UnhandledVirtualException> = LinkedList()

    // When ops are added, such as when unreflecting, need to execute in order to ensure
    // correct contexts for each op. Executing out of order may read registers that haven't been assigned yet.
    val reexecuteLocations: MutableSet<MethodLocation> = HashSet()
    val recreateLocations: MutableSet<MethodLocation> = HashSet()
    var recreateOrExecuteAgain = false

    var executionType: ExecutionType = ExecutionType.VIRTUAL
    val emulatedOrReflected: Boolean
        get() = executionType == ExecutionType.REFLECT || executionType == ExecutionType.EMULATE
    var aborted = false

    init {
        for (location in addressToLocation.values) {
            locationToNodePile[location] = ArrayList()
        }
    }
//    fun shallowClone(): ExecutionGraph2 {
//        // TODO: all the cloning is to avoid the work of rebuilding ops. is it really worth it?
//        // Need to make shallow clones when spawning an instruction graph from a template
//        // This is to avoid the cost of re-creating the graph (with most of the cost being creating ops)
//        val newLocationToNodePile: MutableMap<MethodLocation, MutableList<ExecutionNode>> = HashMap()
//        locationToNodePile.entries.forEach {
//            val nodePile: MutableList<ExecutionNode> = ArrayList(it.value.size)
//            for (n in it.value) {
//                nodePile.add(ExecutionNode(n))
//            }
//            newLocationToNodePile[it.key] = nodePile
//        }
//        for (location in locationToNodePile.keys) {
//            val otherNodePile = locationToNodePile[location]!!
//            val nodePile: MutableList<ExecutionNode> = ArrayList(otherNodePile.size)
//            for (n in otherNodePile) {
//                nodePile.add(ExecutionNode(n))
//            }
//            locationToNodePile[location] = nodePile
//        }
//        val clone = ExecutionGraph2(this.method, this.vm, this.addressToLocation, this.terminatingAddresses)
//
//    }

//    constructor(other: ExecutionGraph2, shallowClone: Boolean = true) {
//        classManager = other.classManager
//        method = other.method
//        classLoader = other.classLoader
//        terminatingAddresses = other.terminatingAddresses
//        addressToLocation = other.addressToLocation
//
//        if (shallowClone) {
//            // Need to make shallow clones when spawning an instruction graph from a template
//            // This is to avoid the cost of re-creating the graph (with most of the cost being creating ops)
//            locationToNodePile = HashMap()
//            for (location: MethodLocation in other.locationToNodePile.keys) {
//                val otherNodePile = other.locationToNodePile[location]!!
//                val nodePile: MutableList<ExecutionNode> = ArrayList(otherNodePile.size)
//                for (n in otherNodePile) {
//                    nodePile.add(ExecutionNode(n))
//                }
//                locationToNodePile[location] = nodePile
//            }
//        } else {
//            locationToNodePile = other.locationToNodePile
//        }
//    }

    override fun toString() = "ExecutionGraph{$method}"

    val root: ExecutionNode
        get() = getNodePile(METHOD_ROOT_ADDRESS)[0]

    fun addNode(node: ExecutionNode) {
        val location = node.op.instruction!!.location
        locationToNodePile[location]!!.add(node)
    }

    fun hasNode(node: ExecutionNode): Boolean {
        val location = node.op.instruction!!.location
        val pile = locationToNodePile[location]
        return pile?.contains(node) ?: false
    }

    private fun getConsensusType(types: MutableSet<String>, values: Set<Value?>): String {
        if (types.isEmpty()) {
            return CommonTypes.UNKNOWN_TYPE
        }
        if (types.size == 1) {
            return types.first()
        }
        var newAncestors: Int
        do {
            /*
             * Collapse type hierarchies to get the most common type. For example, if types includes ChildClass and ParentClass, then the consensus
             * type should be ParentClass.
             */
            collapseTypeHierarchies(types, classManager)
            if (types.size == 1) {
                return types.first()
            }
            if (types.size == 2 && types.contains(CommonTypes.INTEGER)) {
                // Dalvik uses 0 constant to represent null value
                // https://calebfenton.github.io/2016/02/16/how-does-dalvik-handle-null-types/
                for (currentType: String in types) {
                    if (currentType.startsWith("L") && values.contains(Value.wrap(0, CommonTypes.INTEGER))) {
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
        return CommonTypes.UNKNOWN_TYPE
    }

    /**
     * @return Naturally sorted array of all unique addresses in the graph.
     */
    fun getAddresses(): IntArray {
        return addressToLocation.keys.sorted().toIntArray()
    }

    fun getLocations(): Collection<MethodLocation> {
        return addressToLocation.values
    }

    fun getNodeCount(): Int {
        return locationToNodePile.size
    }

    fun getOp(address: Int): Op {
        val location = addressToLocation[address]!!
        return locationToOp[location]!!
    }

    fun spawnEntrypointNode(parent: ExecutionNode? = null, state: ExecutionState? = null): ExecutionNode {
        val op = getOp(METHOD_ROOT_ADDRESS)
        return when (state) {
            null -> ExecutionNode(op, method, classManager, classLoader, configuration, parent = parent)
            else -> ExecutionNode(op, method, classManager, classLoader, configuration, state, parent)
        }
    }

    protected fun getNodeIndex(node: ExecutionNode): Int {
        return getNodePile(node.address).indexOf(node)
    }

    fun getNodePile(address: Int): MutableList<ExecutionNode> {
        val location = addressToLocation[address]
        return locationToNodePile[location]!!
    }

//    fun getMutableParameterConsensus(addresses: IntArray, parameterRegister: Int): Value {
//        val firstNode = getNodePile(addresses[0])[0]
//        val value = firstNode.state.peekRegister(parameterRegister)!!
//        for (address in addresses) {
//            for (node in getNodePile(address)) {
//                val otherValue = node.state.peekRegister(parameterRegister)!!
//                if (value.value !== otherValue.value) {
//                    log.trace("No consensus for r{}. Returning unknown.", parameterRegister)
//                    return Value.unknown(value.type)
//                }
//            }
//        }
//        return value
//    }

    fun getAllPossiblyInitializedClasses(addresses: IntArray): Set<VirtualType> {
        val allClasses: MutableSet<VirtualType> = HashSet()
        for (address in addresses) {
            for (node in getNodePile(address)) {
                allClasses.union(node.state.initializedClasses.keys)
            }
        }
        return allClasses
    }

    fun getConnectedTerminatingAddresses(): IntArray {
        if (emulatedOrReflected) {
            // Emulated and reflected methods won't have populated graphs.
            // For emulated methods, all state changes happen in root node.
            return intArrayOf(METHOD_ROOT_ADDRESS)
        }
        val addresses = LinkedList<Int>()
        for (address in terminatingAddresses) {
            if (wasAddressReached(address)) {
                addresses.add(address)
            }
        }
        return addresses.toIntArray()
    }

    fun getFieldConsensus(addresses: IntArray, field: VirtualField): Value {
        val values: MutableSet<Value> = HashSet()
        for (address in addresses) {
            // If the class wasn't initialized in one path, it's unknown
            for (node in getNodePile(address)) {
                if (!node.state.isClassInitialized(field.definingClass)) {
                    return Value.unknown(field.type)
                }
            }
            values.addAll(getFieldItems(address, field))
            if (values.size != 1) {
                // Since items is a set, size == 1 implies consensus
                log.trace("No consensus for {}, returning Unknown.", field)
                return Value.unknown(field.type)
            }
        }
        return if (values.size > 0) {
            values.first()
        } else {
            log.trace("No values found for {}, addresses={}, returning Unknown.", field)
            return Value.unknown(field.type)
        }
    }

    fun getFieldItems(address: Int, field: VirtualField): Set<Value> {
        val nodePile = getNodePile(address)
        val values: MutableSet<Value> = HashSet(nodePile.size)
        for (node: ExecutionNode in nodePile) {
            val value = node.state.peekField(field)
            values.add(value)
        }
        return values
    }

    fun getHighestClassSideEffectLevel(virtualClass: VirtualType): SideEffectLevel {
        val addresses = getConnectedTerminatingAddresses()
        var result = SideEffectLevel.NONE
        for (address: Int in addresses) {
            for (node in getNodePile(address)) {
                // If null, class hasn't been initialized yet
                val level = node.state.getClassSideEffectLevel(virtualClass) ?: continue
                when (level) {
                    SideEffectLevel.STRONG -> return level
                    SideEffectLevel.WEAK -> result = level
                    SideEffectLevel.NONE -> {
                    }
                }
            }
        }
        return result
    }

    fun getHighestMethodSideEffectLevel(): SideEffectLevel {
        if (emulatedOrReflected) {
            return SideEffectLevel.NONE
        }
        var result = SideEffectLevel.NONE
        for (node: ExecutionNode in this) {
            when (val level = node.sideEffectLevel) {
                SideEffectLevel.STRONG -> return level
                SideEffectLevel.WEAK -> result = level
                SideEffectLevel.NONE -> {
                }
            }
        }
        return result
    }

    fun getHighestSideEffectLevel(): SideEffectLevel {
        var result = getHighestMethodSideEffectLevel()
        if (result == SideEffectLevel.STRONG) {
            return result
        }
        val addresses = getConnectedTerminatingAddresses()
        val allClasses = getAllPossiblyInitializedClasses(addresses)
        for (virtualClass in allClasses) {
            when (val level = getHighestClassSideEffectLevel(virtualClass)) {
                SideEffectLevel.STRONG -> return level
                SideEffectLevel.WEAK -> result = level
                SideEffectLevel.NONE -> {
                }
            }
        }
        return result
    }

    /**
     * {@see #getRegisterConsensus(int[], int)}
     *
     * @return consensus value over all `addresses` in `register` or [ ] if a consensus doesn't exist
     */
    fun getRegisterConsensus(address: Int, register: Int): Value {
        return getRegisterConsensus(intArrayOf(address), register)
    }

    /**
     * Check if all of the values for `register` at each address in `addresses` are identical. If they are, there is a consensus -- every execution
     * path had the same value for that register. If there is more than one value, there is no consensus. Consider the following method:
     *
     * <pre>`foo()I
     *  v0 = readStringFromNetwork()Ljava/lang/String; // won't be executed because unsafe
     *  if v0 == "the spice must flow":
     *    v1 = 1
     *  else:
     *    v1 = 0
     *   return v1`</pre>
     *
     * Since the return value of `readStringFromNetwork()Ljava/lang/String;` will be `UnknownValue', the `if v0` must take both execution paths
     * because `IfOp` won't be able to evaluate the predicate. This means `return v1` could either be `1` or `0`. There is no consensus. In this case,
     * an [UnknownValue] will be returned.
     *
     * @return consensus value for `register` at each address in `addresses`, `UnknownValue` if there was no consensus
     */
    fun getRegisterConsensus(addresses: IntArray, register: Int, treatAsParameter: Boolean = false): Value {
        val values: MutableSet<Value?> = HashSet()
        for (address in addresses) {
            values.addAll(getRegisterValues(address, register, treatAsParameter))
        }
        // Values are equal if they have the same type and value
        if (values.size == 1 && values.first() != null) {
            return values.first()!!
        }

        // Determine consensus type
        log.trace("No consensus value for r{} @{}; returning unknown.", register, addresses)
        val types: MutableSet<String> = HashSet(values.size)
        for (value in values) {
            if (value == null) {
                // Register was not assigned in this execution path.
                continue
            }
            types.add(value.type)
        }

        var type = getConsensusType(types, values)
        if (type == CommonTypes.UNKNOWN_TYPE) {
            if (register == ExecutionState.RETURN_REGISTER) {
                log.warn(
                    "No consensus type for return register; using method return type, method={}, addresses={}, register={}, types={}",
                    method.signature,
                    addresses,
                    register,
                    type
                )
                type = method.returnType
            } else {
                log.warn(
                    "No consensus type; using *unknown* type! method={}, addresses={}, register={}, types={}",
                    method.signature,
                    addresses,
                    register,
                    types
                )
            }
        }
        return Value.unknown(type)
    }

    fun getRegisterConsensusValue(address: Int, register: Int): Any? {
        val value = getRegisterConsensus(address, register)
        return value.raw
    }

    fun getRegisterConsensusValue(addresses: IntArray, register: Int): Any? {
        val value = getRegisterConsensus(addresses, register)
        return value.raw
    }

    /**
     * This gives you every possible value for a given `address` and `register`.
     *
     * @return returns items at `address` in `register` for every execution path
     */
    fun getRegisterValues(address: Int, register: Int, treatAsParameter: Boolean = false): Set<Value?> {
        val nodePile = getNodePile(address)
        val values: MutableSet<Value?> = HashSet(nodePile.size)
        for (node in nodePile) {
            val value = if (treatAsParameter) {
                node.state.peekParameter(register)
            } else {
                node.state.peekRegister(register)
            }
            values.add(value)
        }
        return values
    }

    fun getTerminatingStates(): Collection<ExecutionState> {
        return getStates(getConnectedTerminatingAddresses())
    }

    fun getStates(addresses: IntArray): Collection<ExecutionState> {
        val states: MutableList<ExecutionState> = LinkedList()
        for (address in addresses) {
            states.addAll(
                getNodePile(address).map { obj: ExecutionNode -> obj.state }
            )
        }
        return states
    }

    fun getTerminatingFieldConsensus(field: VirtualField): Value {
        val fieldToValue = getTerminatingFieldConsensus(arrayOf(field))
        return (fieldToValue[field])!!
    }

    fun getTerminatingFieldConsensus(fields: Array<VirtualField>): Map<VirtualField, Value> {
        val addresses = getConnectedTerminatingAddresses()
        val fieldToValue: MutableMap<VirtualField, Value> = HashMap()
        for (field in fields) {
            fieldToValue[field] = getFieldConsensus(addresses, field)
        }
        return fieldToValue
    }

    fun getTerminatingParameterConsensus(register: Int): Value {
        return getTerminatingRegisterConsensus(register, treatAsParameter = true)
    }

    fun getTerminatingRegisterConsensus(register: Int, treatAsParameter: Boolean = false): Value {
        val registerToValue = getTerminatingRegisterConsensus(intArrayOf(register), treatAsParameter = treatAsParameter)
        return registerToValue[register]!!
    }

    fun getTerminatingRegisterConsensus(registers: IntArray, treatAsParameter: Boolean = false): Map<Int, Value> {
        val registerToValue: MutableMap<Int, Value> = HashMap(registers.size)
        val addresses = getConnectedTerminatingAddresses()
        if (addresses.isNotEmpty()) {
            for (register in registers) {
                registerToValue[register] = getRegisterConsensus(addresses, register, treatAsParameter = treatAsParameter)
            }
        } else {
            log.warn("No connected terminating addresses for register consensus; registers=$registers")
            for (register in registers) {
                registerToValue[register] = Value.unknown(CommonTypes.UNKNOWN_TYPE)
            }
        }
        return registerToValue
    }

    override fun iterator(): MutableIterator<ExecutionNode> {
        return ExecutionGraphIterator(this)
    }

    fun wasAddressReached(address: Int): Boolean {
        val nodePile = getNodePile(address)
        return nodePile.size > 0
    }

    fun toSmali(): String {
        return toSmali(false)
    }

    fun toSmali(includeAddress: Boolean): String {
        val addresses = getAddresses()
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

    fun addInstruction(location: MethodLocation, instruction: BuilderInstruction) {
        val index = location.index
        method.implementation.addInstruction(index, instruction)
        val newLocation = instruction.location
        val oldLocation = method.implementation.instructions[index + 1].location
        try {
            val m = MethodLocation::class.java.getDeclaredMethod("mergeInto", MethodLocation::class.java)
            m.isAccessible = true
            m.invoke(oldLocation, newLocation)
        } catch (e: Exception) {
            log.error("Error invoking MethodLocation.mergeInto(). Wrong dexlib version?", e)
        }
        rebuildGraph()
    }

    fun addInstruction(address: Int, newInstruction: BuilderInstruction) {
        addInstruction(getLocation(address)!!, newInstruction)
    }

    fun getAvailableRegisters(address: Int): IntArray {
        // TODO: wouldn't this always be the same for every op? If so, can refactor getRegisterCount
        val registers = IntArray(getRegisterCount(address)) { it }
        val stack = ArrayDeque(getChildren(address))
        var node = stack.peek()
        if (node == null) {
            // No children. All registers available!
            assert(getOp(address) is ReturnOp || getOp(address) is ReturnVoidOp)
            return registers
        }
        val registersRead: MutableSet<Int> = HashSet()
        val registersAssigned: MutableSet<Int> = HashSet()
        while (stack.poll().also { node = it!! } != null) {
            for (register in registers) {
                if (registersRead.contains(register) || registersAssigned.contains(register)) {
                    continue
                }
                if (node.op.name.startsWith("move-result")) {
                    // The target and result registers will always be identical. This makes it seem as if the register
                    // has always been read since it was read when it was in the result register.
                    continue
                }
                if (node.state.wasRegisterRead(register)) {
                    registersRead.add(register)
                } else if (node.state.wasRegisterAssigned(register)) {
                    registersAssigned.add(register)
                }
            }
            stack.addAll(node.children)
        }

        return registers.filter { r -> !registersRead.contains(r) }.toIntArray()
    }

    fun getChildren(address: Int): Collection<ExecutionNode> {
        val children: MutableList<ExecutionNode> = LinkedList()
        val nodePile = getNodePile(address)
        for (node in nodePile) {
            children.addAll(node.children)
        }
        return children
    }

    fun getInstruction(address: Int): BuilderInstruction? {
        return getOp(address).instruction
    }

    fun getParentAddresses(address: Int): IntArray {
        val parentAddresses: MutableSet<Int> = HashSet()
        for (node in getNodePile(address)) {
            val parent = node.parent ?: continue
            parentAddresses.add(parent.address)
        }
        return Ints.toArray(parentAddresses)
    }

    fun getTryBlocks(): List<BuilderTryBlock> {
        return method.implementation.tryBlocks
    }

    fun removeInstruction(location: MethodLocation) {
        method.implementation.removeInstruction(location.index)
        removeEmptyTryCatchBlocks()
        rebuildGraph()
    }

    fun removeInstruction(address: Int) {
        removeInstruction(getLocation(address)!!)
    }

    fun removeInstructions(addresses: MutableList<Int>) {
        addresses.sortDescending()
        log.debug("Removing instructions: {}", addresses)
        addresses.forEach(Consumer { address -> removeInstruction(address) })
    }

    fun replaceInstruction(insertAddress: Int, instruction: BuilderInstruction) {
        val instructions: MutableList<BuilderInstruction> = LinkedList()
        instructions.add(instruction)
        replaceInstruction(insertAddress, instructions)
    }

    fun replaceInstruction(insertAddress: Int, instructions: List<BuilderInstruction>) {
        recreateOrExecuteAgain = false
        var address = insertAddress
        for (instruction in instructions) {
            addInstruction(address, instruction)
            address += instruction.codeUnits
        }
        val location = getLocation(address)!!
        recreateOrExecuteAgain = true
        removeInstruction(location)
    }

    fun getLocation(address: Int): MethodLocation? {
        return addressToLocation[address]
    }

    private fun getRegisterCount(address: Int): Int {
        return getNodePile(address)[0].state.registerCount
    }

//    private fun addToNodePile(newLocation: MethodLocation) {
//        // Returns node which need to be re-executed after graph / mappings are rebuilt
//        // E.g. branch offset instructions can't be created without accurate mappings
//        val oldIndex = newLocation.index + 1
//        var shiftedLocation: MethodLocation? = null
//        for (location in locationToNodePile.keys) {
//            if (location.index == oldIndex) {
//                shiftedLocation = location
//                break
//            }
//        }
//        assert(shiftedLocation != null)
//
//        val shiftedNodePile = locationToNodePile[shiftedLocation]!!
//        val newNodePile: MutableList<ExecutionNode> = ArrayList()
//        locationToNodePile[newLocation] = newNodePile
//        val shiftedOp = shiftedNodePile[0].op
//        val op = opBuilder.build(newLocation)
//        recreateLocations.add(newLocation)
//        reexecuteLocations.add(newLocation)
//        val isAutoAddedPadding = op is NopOp && (shiftedOp is FillArrayDataPayloadOp || shiftedOp is SwitchPayloadOp)
//        for (i in shiftedNodePile.indices) {
//            val newNode = ExecutionNode(op, method, classManager, classLoader, configuration)
//            newNodePile.add(i, newNode)
//            if (isAutoAddedPadding) {
//                // This type of padding is never reached and only exists for byte alignment.
//                break
//            }
//            if (i == TEMPLATE_NODE_INDEX) {
//                // Template nodes shouldn't have parents or children
//                continue
//            }
//
//            val shiftedNode = shiftedNodePile[i]
//            val shiftedParent = shiftedNode.parent
//            //var newContext: ExecutionContext?
//            if (shiftedParent != null) {
//                shiftedParent.removeChild(shiftedNode)
//                reparentNode(newNode, shiftedParent)
//
//                // Recreate parent op because its children locations may be affected.
//                recreateLocations.add(shiftedParent.op.location)
//            } else {
//                assert(newLocation.codeAddress == METHOD_ROOT_ADDRESS)
//                // TODO: add caller states to this if necessary
//                val state = vm.spawnRootState(method)
//                newNode.state = state
//            }
//            reparentNode(shiftedNode, newNode)
//        }
//    }

    private fun reparentNode(child: ExecutionNode, parent: ExecutionNode) {
        reexecuteLocations.add(child.op.location)
        parent.addChild(child)
    }

    private fun recreateAndExecute() {
        if (!recreateOrExecuteAgain) {
            return
        }

        // TODO: this needs to be tested quite a bit. lots has changed!
//        // Was removed from implementation before getting here
//        recreateLocations.removeIf { p: MethodLocation -> p.instruction == null }
//        reexecuteLocations.removeIf { p: MethodLocation -> p.instruction == null }
//        for (location in recreateLocations) {
//            val op = opCreator.create(location)
//            val pile: List<ExecutionNode> = locationToNodePile[location]!!
//
//            // TODO: move side effects out of ops and into nodes or graph
//            // This is a big ugly.
//            if (op is NewInstanceOp || op is InvokeOp) {
//                val node = pile[0]
//                try {
//                    val originalLevel = node.op.sideEffectLevel
//                    var klazz: Class<out Op>
//                    if (op is NewInstanceOp) {
//                        klazz = NewInstanceOp::class.java
//                    } else { // InvokeOp
//                        klazz = InvokeOp::class.java
//                    }
//                    val f = klazz.getDeclaredField("sideEffectLevel")
//                    f.isAccessible = true
//                    f[op] = originalLevel
//                } catch (e: Exception) {
//                    // Ugly.
//                    e.printStackTrace()
//                }
//            }
//            for (node in pile) {
//                // TODO: should I just recreate the node with the new op?
//                node.op = op
//            }
//        }

        // TODO: no idea why I did it this way, comments aren't helpful, be sure and test!
        // Locations with the same address may be added. One is probably being removed. If using a sorted set with an
        // address comparator, it prevents adding multiple locations. This prevents them from executing here.
//        Collections.sort(
//            reexecuteLocations
//        ) { e1: MethodLocation, e2: MethodLocation ->
//            Integer.compare(
//                e1.codeAddress,
//                e2.codeAddress
//            )
//        }
//        val reexecute: Set<MethodLocation> = LinkedHashSet(reexecuteLocations)
//        for (location in reexecute) {
        // TODO: *i think* should execute in ascending order to prevent executing later ops, but this isn't perfect
        // should create a test that must reexecute in a weird order:  op1@0 -> (branch) op2@10 -> op3@2
        for (location in reexecuteLocations.sortedBy { it.codeAddress }) {
            val pile: List<ExecutionNode> = locationToNodePile[location]!!
            pile.forEachIndexed { idx, node ->
                if (idx != TEMPLATE_NODE_INDEX) {
                    node.execute()
                }
            }
        }
        recreateLocations.clear()
        reexecuteLocations.clear()
    }

    private fun rebuildGraph() {
        /*
         This may seem like overkill but by adding or removing a single instruction, multiple new instructions may be added.
         For example, since some ops need to be byte-aligned, removing or adding an instruction may cause padding nops to be inserted.
         */
        val staleLocations: Set<MethodLocation> = locationToNodePile.keys
        val implementationLocations = method.implementation.instructions.map { it.location }
        val addedLocations: MutableSet<MethodLocation> = HashSet(implementationLocations)
        addedLocations.removeAll(staleLocations)
        for (location in addedLocations) {
//            addToNodePile(location)
        }
        val removedLocations: MutableSet<MethodLocation> = HashSet(staleLocations)
        removedLocations.removeAll(implementationLocations)
        removedLocations.forEach(Consumer { location: MethodLocation -> removeFromNodePile(location) })
        val newAddressToLocation = buildAddressToLocation(method.implementation.instructions.map { it.location })
        addressToLocation.clear()
        addressToLocation.putAll(newAddressToLocation)
        recreateAndExecute()
    }

    private fun removeEmptyTryCatchBlocks() {
        /*
         * If every op from a try block is removed, the dex file will fail to save. Maybe dexlib should be smart enough
         * to remove empty blocks itself, but this is an admittedly strange event.
         */
        val iter: ListIterator<BuilderTryBlock> = method.implementation.tryBlocks.listIterator()
        val removeIndexes = LinkedList<Int>()
        while (iter.hasNext()) {
            val index = iter.nextIndex()
            val tryBlock = iter.next()
            // Get location using reflection to avoid null check.
            val start = getLocation(tryBlock.start)
            val end = getLocation(tryBlock.end)
            if (start == null || end == null || start.codeAddress == end.codeAddress) {
                // Empty try block!

                // Went through the trouble of getting indexes ahead of time because otherwise
                // calls to equals might need to be made, and that would inspect properties
                // of the try block, which could cause null pointer exceptions.
                removeIndexes.add(index)

                // I think dexlib correctly, gracefully handles removing orphaned labels
                // if (start != null) {
                // List<Label> remove = new LinkedList<Label>();
                // remove.add(tryBlock.start);
                // remove.add(tryBlock.end);
                // start.getLabels().removeAll(remove);
                // }
            }
        }

        // MutableMethodImplementation#getTryBlocks() returns an immutable collection, but we need to modify it.
        val tryBlocks: ArrayList<BuilderTryBlock> = try {
            val f = method.implementation.javaClass.getDeclaredField("tryBlocks")
            f.isAccessible = true // I DO WHAT I WANT.
            f[method.implementation] as ArrayList<BuilderTryBlock>
        } catch (e: Exception) {
            log.error("Exception getting tryBlocks field", e)
            throw RuntimeException("Unable to retrieve tryBlocks")
        }

        // Remove from the end to avoid re-indexing invalidations
        removeIndexes.sort()
        removeIndexes.reverse()
        for (index in removeIndexes) {
            tryBlocks.removeAt(index)
        }
    }

    private fun getLocation(label: Label): MethodLocation? {
        try {
            val f = ItemWithLocation::class.java.getDeclaredField("location")
            f.isAccessible = true
            return f[label] as MethodLocation
        } catch (e: Exception) {
            log.error("Couldn't get label location.", e)
        }
        return null
    }

    private fun removeFromNodePile(location: MethodLocation) {
        val nodePile: List<ExecutionNode> = locationToNodePile.remove(location)!!
        val locationToChildNodeToRemove: MutableMap<MethodLocation, ExecutionNode> = HashMap()
        for (removedNode in nodePile) {
            val parentNode = removedNode.parent ?: continue
            parentNode.removeChild(removedNode)
            recreateLocations.add(parentNode.op.location)
            // TODO: why is this commented?
            // reexecuteLocations.add(parentNode.getOp().getLocation());
            for (childNode in removedNode.children) {
                val childOp = childNode.op
                val pseudoChild = childOp is FillArrayDataPayloadOp || childOp is SwitchPayloadOp
                if (!pseudoChild) {
                    reparentNode(childNode, parentNode)
                } else { // pseudo child
                    // Implementation was altered such that dexlib removed something, probably nop padding
                    for (grandChildNode in childNode.children) {
                        reparentNode(grandChildNode, parentNode)
                    }
                    locationToChildNodeToRemove[childOp.location] = childNode
                }
            }
        }
        for ((key, value) in locationToChildNodeToRemove) {
            val pile = locationToNodePile[key]!!
            pile.remove(value)
        }
    }

    companion object {
        protected val TEMPLATE_NODE_INDEX = 0
        protected val METHOD_ROOT_ADDRESS = 0

        private val log = LoggerFactory.getLogger(ExecutionGraph2::class.java.simpleName)

        protected fun buildAddressToLocation(locations: Collection<MethodLocation>): MutableMap<Int, MethodLocation> {
            val addressToLocation: MutableMap<Int, MethodLocation> = HashMap(locations.size)
            for (location in locations) {
                addressToLocation[location.codeAddress] = location
            }
            return addressToLocation
        }

        private fun buildTerminatingAddresses(locations: Collection<MethodLocation>): IntArray {
            val addresses: MutableList<Int> = LinkedList()
            for (location in locations) {
                val address = location.codeAddress
                val instruction = location.instruction!!
                if (instruction.opcode == Opcode.RETURN_VOID || instruction.opcode == Opcode.RETURN ||
                    instruction.opcode == Opcode.RETURN_WIDE || instruction.opcode == Opcode.RETURN_OBJECT ||
                    instruction.opcode == Opcode.THROW
                ) {
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

    enum class ExecutionType {
        VIRTUAL, EMULATE, REFLECT
    }
}
