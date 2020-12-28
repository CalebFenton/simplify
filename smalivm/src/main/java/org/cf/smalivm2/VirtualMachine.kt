package org.cf.smalivm2
import com.rits.cloning.Cloner
import com.rits.cloning.ObjenesisInstantiationStrategy
import org.agrona.collections.Int2ObjectHashMap
import org.cf.smalivm.opcode.Op
import org.cf.util.ClassNameUtils
import java.util.*

data class Value(val value: Any, val type: String, val id: ByteArray) {
    companion object {
        private val cloner = Cloner(ObjenesisInstantiationStrategy())

        fun wrap(value: Any): Value {
            return wrap(value, ClassNameUtils.toInternal(value.javaClass))
        }

        fun wrap(value: Any, type: String): Value {
            val id = ByteArray(4)
            Random().nextBytes(id)
            return Value(value, type, id)
        }

        private fun clone(other: Value): Value {
            return Value(cloner.deepClone(other.value), other.type, other.id)
        }
    }

    fun hasSameID(other: Value): Boolean {
        return id.contentEquals(other.id)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value

        if (value != other.value) return false
        if (type != other.type) return false
        if (!id.contentEquals(other.id)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = value.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + id.contentHashCode()
        return result
    }
}

class ExecutionGraph() {

}

open class ExecutionNode() {
    lateinit var parent: ExecutionNode;
    lateinit var children: Array<ExecutionNode>;

    //TODO: build custom map class which can have 1 as default size (this one is 8)
    val registers = Int2ObjectHashMap<Value>()
    val classValues = HashMap<String, Value>()
    val initializedClasses = HashSet<String>()

    // 0 - registers, 1 - class state

    fun execute(): Array<ExecutionNode> {
        return arrayOf()
    }

    fun initializeClass(classSignature: String) {

    }

    fun isClassInitialized(classSignature: String): Boolean {

    }
}

open class ClassReferencingNode: ExecutionNode() {
    open fun getClassSignaturesToLoad(): Array<String> {
        return arrayOf()
    }
}

class InvokeNode() : ClassReferencingNode() {
    fun resume(): Array<ExecutionNode> {
        // TODO: throw not implemented
        return arrayOf()
    }
}

class EntrypointNode(val methodReference: String) : ClassReferencingNode() {
    override fun getClassSignaturesToLoad(): Array<String> {
        // return super class and this class
        // other nodes will only return sget, invoke, const-class, etc. references
        return arrayOf()
    }
}

class VirtualMachine(val debugMode: Boolean) {
    fun execute(methodSignature: String): ExecutionGraph {
        val graph = ExecutionGraph()

        // TODO: get first node of method
        val node = EntrypointNode(methodSignature)
        val nodes = Stack<ExecutionNode>()
        nodes.add(node)

        execute(nodes, graph)
        return graph
    }

    fun execute(nodes: Stack<ExecutionNode>, graph: ExecutionGraph?) {
        if (!this.debugMode) {
            while (nodes.isNotEmpty()) {
                step(nodes, graph)
            }
        }
    }

    private fun callMethod(methodSignature: String, graph: ExecutionGraph?) {

    }
    private fun initializeClassesIfNecessary(callerNode: ClassReferencingNode, callerGraph: ExecutionGraph?) {
        for (classReference in callerNode.getClassSignaturesToLoad()) {
            if (callerNode.isClassInitialized(classReference)) {
                continue
            }
            val initSignature = "$classReference-><clinit>()V"
            val calleeGraph = if (debugMode) null else ExecutionGraph()
            val calleeNodes = Stack<ExecutionNode>()
            val calleeEntryNode = EntrypointNode(initSignature)
            calleeNodes.push(calleeEntryNode)
            execute(calleeNodes, calleeGraph)

            if (debugMode) {
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
    fun step(nodes: Stack<ExecutionNode>, graph: ExecutionGraph?) {
        val node = nodes.peek()

        if (node is ClassReferencingNode) {
            initializeClassesIfNecessary(node, nodes, graph)
        }

        // get class references in node
        // check if class refs are initialized
        // if not, create a node for <clinit> of that class, push to buffer, return

        val children = node.execute()
        for (child in children) {
            if (child is EntrypointNode) {
                val calleeGraph = if (this.debugMode) null else ExecutionGraph()
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

    fun findClassReferences(op: Op) {

    }
}


object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        println("hey");
    }
}
