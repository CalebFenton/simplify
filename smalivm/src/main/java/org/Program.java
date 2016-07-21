package org;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.VirtualMachineFactory;
import org.cf.smalivm.context.ExecutionContext;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.context.MethodState;
import org.cf.smalivm.type.VirtualMethod;

public class Program {

    public static void main(String[] args) throws Exception {

        VirtualMachineFactory vmFactory = new VirtualMachineFactory(); // this is Java, so factory
        String dexOrSmali = "teddygh-smali/ffffff";
        VirtualMachine vm = vmFactory.build(dexOrSmali);
        String methodSignature = "Lffffff/uuuaaa;->bТТ0422Т0422ТТ(Ljava/lang/String;CC)Ljava/lang/String;";
        VirtualMethod method = vm.getClassManager().getMethod(methodSignature);
        ExecutionContext ectx = vm.spawnRootContext(method);
        MethodState mState = ectx.getMethodState();
        mState.assignParameter(mState.getParameterStart() + 0,
                "\u0339\u0344\u02fe\u0343\u033b\u0349\u02fe\u0331\u033e\u0349\u0347\u0338\u0335\u0342\u0335",
                "Ljava/lang/String;");
        mState.assignParameter(mState.getParameterStart() + 1, 0xf0, "C");
        mState.assignParameter(mState.getParameterStart() + 2, 0x2, "C");
        //mState.assignParameter(mState.getParameterStart() + 3, 0x3, "C");
        ExecutionGraph graph = vm.execute(methodSignature, ectx);
        HeapItem item = graph.getTerminatingRegisterConsensus(MethodState.ReturnRegister);
        System.out.println("With context, returns: " + item);


        // all arrays are of type Object
        // arrays of N dimension are of type Object N-1 dimension
        // all arrays are of their exact same type

        Class<?> klazz = Class.forName("[B");
        System.out.println(klazz);
        //System.out.println(o instanceof int);
    }

}
