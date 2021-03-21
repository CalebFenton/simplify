package org.cf.smalivm2

import org.cf.smalivm.configuration.Configuration
import org.cf.smalivm.context.*
import org.cf.smalivm.dex.SmaliClassLoader
import org.cf.smalivm.dex.SmaliParser
import org.cf.smalivm.opcode.Op
import org.cf.smalivm.type.*
import org.cf.util.Utils
import org.jf.dexlib2.iface.instruction.*
import java.io.File
import java.util.*
import kotlin.collections.HashMap


class VirtualMachine2 private constructor(
    val maxAddressVisits: Int = 500,
    val maxCallDepth: Int = 20,
    val maxMethodVisits: Int = 1_000_000,
    val maxExecutionTime: Int = 5 * 60,
) {
    lateinit var classManager: ClassManager
    lateinit var classLoader: SmaliClassLoader
    val templates: MutableMap<VirtualMethod, ExecutionGraph2> = HashMap()
    val configuration = Configuration.instance()
    var interactive = false
    //    val staticFieldAccessor = StaticFieldAccessor(this)
    //    val exceptionFactory = ExceptionFactory(this)

    companion object Factory {
        const val DEFAULT_MAX_ADDRESS_VISITS = 500
        const val DEFAULT_MAX_CALL_DEPTH = 20
        const val DEFAULT_MAX_METHOD_VISTS = 1_000_000
        const val DEFAULT_MAX_EXECUTION_TIME = 5 * 60
        const val DEFAULT_OUTPUT_API_LEVEL = SmaliParser.DEX_API_LEVEL

        fun build(
            inputPath: String,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
            outputAPILevel: Int = DEFAULT_OUTPUT_API_LEVEL
        ): VirtualMachine2 {
            return build(File(inputPath), maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime, outputAPILevel)
        }

        fun build(
            inputPath: File,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
            outputAPILevel: Int = DEFAULT_OUTPUT_API_LEVEL
        ): VirtualMachine2 {
            return build(ClassManager2(inputPath, outputAPILevel), maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime)
        }

        fun build(
            classManager: ClassManager,
            maxAddressVisits: Int = DEFAULT_MAX_ADDRESS_VISITS,
            maxCallDepth: Int = DEFAULT_MAX_CALL_DEPTH,
            maxMethodVisits: Int = DEFAULT_MAX_METHOD_VISTS,
            maxExecutionTime: Int = DEFAULT_MAX_EXECUTION_TIME,
        ): VirtualMachine2 {
            val vm = VirtualMachine2(maxAddressVisits, maxCallDepth, maxMethodVisits, maxExecutionTime)
            vm.classManager = classManager
            vm.classLoader = SmaliClassLoader(vm.classManager)
            return vm
        }
    }

    fun execute(className: String, methodDescriptor: String): ExecutionGraphImpl {
        return execute("$className->$methodDescriptor")
    }

    fun execute(methodSignature: String): ExecutionGraphImpl {
        val method = classManager.getMethod(methodSignature) ?: throw IllegalArgumentException("Method signature not found: $methodSignature")

        return execute(method)
    }

    fun execute(method: VirtualMethod): ExecutionGraphImpl {
        val graph = ExecutionGraph2(method, this.classManager, this.classLoader)
        graph.getTemplateNode()

        // TODO: get first node of method
        val node = ExecutionNode(methodSignature)
        val nodes = Stack<Stack<ExecutionNode>>()
        val methodNodes = Stack<ExecutionNode>()
        methodNodes.add(node)
        nodes.add(methodNodes)

        execute(nodes, graph)
        return graph
        return execute(state)
    }

    fun execute(context: ExecutionContext?): ExecutionGraphImpl {
        return execute(context, null, null)
    }

    fun execute(calleeContext: ExecutionContext?, callerContext: ExecutionContext?, parameterRegisters: IntArray?): ExecutionGraphImpl {
        TODO("Not yet implemented")
    }

    fun execute(nodes: Stack<Stack<ExecutionNode>>, graph: ExecutionGraph2) {
        while (!this.interactive && nodes.isNotEmpty()) {
            step(nodes, graph)
        }
    }

    private fun callMethod(methodSignature: String, graph: ExecutionGraph?) {

    }

    private fun initializeClassesIfNecessary(callerNode: org.cf.smalivm2.ClassReferencingNode, callerGraph: org.cf.smalivm2.ExecutionGraph?) {
        for (classReference in callerNode.getClassSignaturesToLoad()) {
            if (callerNode.isClassInitialized(classReference)) {
                continue
            }
            val initSignature = "$classReference-><clinit>()V"
            val calleeGraph = if (interactive) null else org.cf.smalivm2.ExecutionGraph()
            val calleeNodes = Stack<org.cf.smalivm2.ExecutionNode>()
            val calleeEntryNode = EntrypointNode(initSignature)
            calleeNodes.push(calleeEntryNode)
            execute(calleeNodes, calleeGraph)

            if (interactive) {
                /*
                set caller node children to include calleeNode
                set
                 */
            } else {

                // merge consensus of calleeGraph with current state
                // delete calleeGraph
                // make sure no references to these nodes exist!
                // set calleeEntryNode parent to null
                // reset node children and

            }
        }
    }

    fun step(nodes: Stack<Stack<ExecutionNode>>, graph: ExecutionGraph2) {
        val methodNodes = nodes.peek()
        val node = methodNodes.peek()

        if (node is EntrypointNode) {
            if (!node.isClassInitialized(node.method.className)) {
                val callNodes = Stack<ExecutionNode>()
                val callMethod = classManager.getMethod("${node.method.className}-><init>()V")
                // graph get root node, set parent to node
                val callNode = node.spawnChild()
            }
        }
        if (node.parent == null) {
            // TODO: init this class, it's the root
            initializeClassesIfNecessary(node, nodes, graph)
        }

        // get class references in node
        // check if class refs are initialized
        // if not, create a node for <clinit> of that class, push to buffer, return

        val children = node.execute()
        for (child in children) {
            if (child is EntrypointNode) {
                val calleeGraph = if (this.interactive) null else org.cf.smalivm2.ExecutionGraph()
                execute(methodReference, nodes, calleeGraph)
                node.resume()
            } else {
                nodes.add(child)
            }

        }
        methodNodes.addAll(children);
        if (methodNodes.isEmpty()) {
            // Finished executing all nodes for this method
            methodNodes.removeAt(0)
        }

        graph?.let {
            // add node to graph
        }
    }

//    fun startDebug(calleeContext: ExecutionContext?, callerContext: ExecutionContext?): MethodExecutor {
//        TODO("Not yet implemented")
//    }
//
//    fun finishDebug(methodExecutor: MethodExecutor?, callerContext: ExecutionContext?, parameterRegisters: IntArray?): ExecutionGraphImpl {
//        TODO("Not yet implemented")
//    }


//    override fun getStaticFieldAccessor(): StaticFieldAccessor {
//        TODO("Not yet implemented")
//    }

    fun isSafe(virtualClass: VirtualType): Boolean {
        return configuration.isSafe(virtualClass.toString())
    }

    fun spawnExecutionGraph(className: String, methodDescriptor: String): ExecutionGraph2 {
        val method = classManager.getVirtualClass(className).getMethod(methodDescriptor)!!
        return spawnExecutionGraph(method)
    }

    fun spawnExecutionGraph(method: VirtualMethod): ExecutionGraph2 {
        if (!templates.containsKey(method)) {
            updateTemplateGraph(method)
        }
        return ExecutionGraph2(templates[method]!!)
    }

    fun spawnEntrypointState(methodSignature: String): ExecutionState {
        val method = classManager.getMethod(methodSignature)
        return spawnEntrypointState(method)
    }

    fun spawnEntrypointState(className: String, methodDescriptor: String): ExecutionState {
        val klazz = classManager.getVirtualClass(className)
        val method = klazz.getMethod(methodDescriptor) ?: throw RuntimeException("Method signature not found: $className->$methodDescriptor")
        return spawnEntrypointState(method)
    }

    fun spawnEntrypointState(method: VirtualMethod, callerContext: ExecutionContext? = null, callerAddress: Int? = null): ExecutionState {
        if (!method.hasImplementation()) {
            // Native or abstract methods have no implementation. Shouldn't be executing them.
            throw IllegalArgumentException("No implementation for $method");
        }

        val registerCount = method.registerCount
        val parameterTypeNames = method.parameterTypeNames
        val parameterSize = Utils.getRegisterSize(parameterTypeNames)
        val firstParameterRegister = registerCount - parameterSize
        val fieldCount = method.definingClass.fields.size
        val state = ExecutionState(registerCount + fieldCount, 1)

        val currentRegister = firstParameterRegister
        for (typeName in method.parameterTypeNames) {
            val value = if (currentRegister == firstParameterRegister && !method.isStatic && method.name == "<init>") {
                val instance = UninitializedInstance(method.definingClass)
                Value.wrap(instance, typeName)
            } else {
                Value.wrap(UnknownValue(), typeName)
            }
            state.
        }
        for (VirtualField field : virtualClass.getFields()) {
            Object value = field . getInitialValue ();
            String type = field . getType ();
            cState.pokeField(field, new HeapItem (value, type));
        }

//        VirtualMethod method = context.getMethod();
//        int registerCount = method.getRegisterCount();
//        List<String> parameterTypes = method.getParameterTypeNames();
//        int parameterSize = Utils.getRegisterSize(parameterTypes);
        MethodState mState = new MethodState(context, registerCount, parameterTypes.size(), parameterSize);
        int firstParameter = mState . getParameterStart ();
        int parameterRegister = firstParameter;

        for (String type : parameterTypes) {
            HeapItem item;
            if (parameterRegister == firstParameter && !method.isStatic() && method.getName().equals("<init>")) {
                UninitializedInstance instance = new UninitializedInstance(method.getDefiningClass());
                item = new HeapItem (instance, type);
            } else {
                item = HeapItem.newUnknown(type);
            }
            mState.assignParameter(parameterRegister, item);
            parameterRegister += Utils.getRegisterSize(type);
        }

        return mState;
//        ExecutionContext spawnedContext = new ExecutionContext(this, method);
//        ClassState templateClassState = TemplateStateFactory.forClass(spawnedContext, method.getDefiningClass());
//        spawnedContext.setClassState(templateClassState);
//
//        MethodState templateMethodState = TemplateStateFactory.forMethod(spawnedContext);
//        spawnedContext.setMethodState(templateMethodState);
//
//        if (callerContext != null) {
//            spawnedContext.registerCaller(callerContext, callerAddress);
//        }

    }

    /*
    public static MethodState forMethod(ExecutionContext context) {
        VirtualMethod method = context.getMethod();
        int registerCount = method.getRegisterCount();
        List<String> parameterTypes = method.getParameterTypeNames();
        int parameterSize = Utils.getRegisterSize(parameterTypes);
        MethodState mState = new MethodState(context, registerCount, parameterTypes.size(), parameterSize);
        int firstParameter = mState.getParameterStart();
        int parameterRegister = firstParameter;

        for (String type : parameterTypes) {
            HeapItem item;
            if (parameterRegister == firstParameter && !method.isStatic() && method.getName().equals("<init>")) {
                UninitializedInstance instance = new UninitializedInstance(method.getDefiningClass());
                item = new HeapItem(instance, type);
            } else {
                item = HeapItem.newUnknown(type);
            }
            mState.assignParameter(parameterRegister, item);
            parameterRegister += Utils.getRegisterSize(type);
        }

        return mState;
    }

    public static ClassState forClass(ExecutionContext context, VirtualType virtualClass) {
        ClassState cState = new ClassState(virtualClass, context);
        for (VirtualField field : virtualClass.getFields()) {
            Object value = field.getInitialValue();
            String type = field.getType();
            cState.pokeField(field, new HeapItem(value, type));
        }

        return cState;
    }

     */
    fun spawnEntrypointNode() {

    }

    fun updateTemplateGraph(method: VirtualMethod) {
        val graph = ExecutionGraph2(method, this.classManager, this.classLoader)
        templates[method] = graph
    }

    fun findClassReferences(op: Op) {

    }


    object Main {
        @JvmStatic
        fun main(args: Array<String>) {
            println("hey");
        }
    }

}
