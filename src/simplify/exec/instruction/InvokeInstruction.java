package simplify.exec.instruction;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jf.dexlib2.AccessFlags;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.ReferenceInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction35c;
import org.jf.dexlib2.iface.instruction.formats.Instruction3rc;
import org.jf.dexlib2.iface.reference.MethodReference;
import org.jf.dexlib2.util.ReferenceUtil;
import org.jf.dexlib2.writer.builder.BuilderClassDef;
import org.jf.dexlib2.writer.builder.BuilderMethod;

import simplify.Main;
import simplify.SmaliClassUtils;
import simplify.exec.MaxNodeVisitsExceeded;
import simplify.exec.MethodExecutionContext;
import simplify.exec.MethodExecutor;
import simplify.exec.MethodReflector;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

import com.google.common.primitives.Ints;

public class InvokeInstruction {
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static void execute(MethodExecutionContext ectx, Instruction instruction, int index,
                    List<BuilderClassDef> classes) {
        MethodReference methodRef = getMethodReference(instruction);
        List<? extends CharSequence> parameterTypes = methodRef.getParameterTypes();
        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodRef);
        String returnType = methodRef.getReturnType();
        RegisterStore returnRS = new RegisterStore(returnType, new UnknownValue());

        BuilderMethod method = getMethodFromRef(classes, methodRef);
        if (method != null) {
            log.fine("Found " + methodDescriptor + " defined in smali and executing..");

            int registerCount = method.getImplementation().getRegisterCount();
            MethodExecutionContext calledContext = buildCalledContext(ectx, instruction, parameterTypes, registerCount,
                            index);

            MethodExecutor me = new MethodExecutor();
            try {
                // TODO: need to examine exit nodes and see if all return registers agree
                me.execute(classes, method, calledContext);
                // returnRS = calledContext.getReturnRegister();
                log.info("called " + methodDescriptor + " and it returned: " + returnRS);
            } catch (MaxNodeVisitsExceeded e) {
                e.printStackTrace();
            }
        } else if (MethodReflector.isSafeToReflect(methodDescriptor)) {
            // With just a method reference, we can't know the register count. We just need to allocate enough for
            // parameters and instance variable.
            int registerCount = parameterTypes.size();
            if (!instruction.getOpcode().name.startsWith("invoke-static")) {
                registerCount++; // +1 for instance variable
            }
            MethodExecutionContext calledContext = buildCalledContext(ectx, instruction, parameterTypes, registerCount,
                            index);

            if (allArgumentsKnown(calledContext)) {
                // Method not defined in Smali, but all arguments are known and method has been declared safe.
                // Execute it from current class loader.
                MethodReflector.reflect(calledContext, methodRef.getParameterTypes(), methodDescriptor);
                returnRS = ectx.getReturnRegister();
            }
        } else {
            // Any non-final classes passed as non-final parameters could have changed.
            for (int i = 0; i < parameterTypes.size(); i++) {
                String type = parameterTypes.get(i).toString();
                if (!isImmutableClass(classes, type)) {
                    log.finer("r" + i + " (" + type + ") is mutable/unknown and parameter, marking unknown");
                    ectx.addRegister(i, returnType, new UnknownValue(), index);
                } else {
                    log.finer("r" + i + " (" + type + ") is immutable and parameter, retaining value");
                }
            }
        }

        if (!returnType.equals("V")) {
            ectx.setResultRegister(returnRS);
        }
    }

    private static int[] getParameterRegistersIndicies(Instruction instruction, List<? extends CharSequence> typesList,
                    boolean isStatic) {
        int[] result;
        if (instruction.getOpcode().name.endsWith("range")) {
            Instruction3rc instr = (Instruction3rc) instruction;
            result = new int[instr.getRegisterCount()];

            int start = instr.getStartRegister();
            for (int i = 0; i < result.length; i++) {
                result[i] = start + i;
            }
        } else {
            Instruction35c instr = (Instruction35c) instruction;
            result = new int[instr.getRegisterCount()];

            switch (result.length) {
            case 5:
                result[4] = instr.getRegisterG();
            case 4:
                result[3] = instr.getRegisterF();
            case 3:
                result[2] = instr.getRegisterE();
            case 2:
                result[1] = instr.getRegisterD();
            case 1:
                result[0] = instr.getRegisterC();
            }
        }

        List<String> types = new ArrayList<String>(typesList.size());
        if (!isStatic) {
            // Non static method's first parameter is an instance reference
            types.add("this");
        }
        for (CharSequence cs : typesList) {
            types.add(cs.toString());
        }

        List<Integer> registerList = new ArrayList<Integer>();
        for (int i = 0; i < result.length; i++) {
            registerList.add(result[i]);

            String type = types.get(i);
            if (type.equals("J") || type.equals("D")) {
                // Long and Double types take up two registers, skip the next one
                i++;
            }
        }

        result = Ints.toArray(registerList);

        return result;
    }

    private static MethodExecutionContext buildCalledContext(MethodExecutionContext callerContext,
                    Instruction instruction, List<? extends CharSequence> parameterTypes, int registerCount, int index) {
        boolean isStatic = instruction.getOpcode().name.startsWith("invoke-static");
        int[] paramRegisters = getParameterRegistersIndicies(instruction, parameterTypes, isStatic);

        int remaingCallDepth = callerContext.getRemaingCallDepth() - 1;
        MethodExecutionContext calledContext = new MethodExecutionContext(registerCount, paramRegisters.length,
                        remaingCallDepth);

        int startOffset = 0;
        if (!isStatic) {
            // Pass a reference to ectx's register store, not a clone, because it may be modified.
            RegisterStore rs = callerContext.getRegister(paramRegisters[0], index);
            calledContext.addParameterRegister(0, rs);

            startOffset = 1;
        }

        for (int i = startOffset; i < paramRegisters.length; i++) {
            Object value = callerContext.getRegisterValue(paramRegisters[i], index);
            String type = parameterTypes.get(i - startOffset).toString();
            calledContext.addParameterRegister(i, type, value);
        }

        return calledContext;
    }

    private static MethodReference getMethodReference(Instruction instruction) {
        return (MethodReference) ((ReferenceInstruction) instruction).getReference();
    }

    private static BuilderClassDef getClassFromType(List<BuilderClassDef> classes, String type) {
        for (BuilderClassDef classDef : classes) {
            if (classDef.getType().equals(type)) {
                return classDef;
            }
        }

        return null;
    }

    private static boolean isImmutableClass(List<BuilderClassDef> classes, String type) {
        if (SmaliClassUtils.isPrimitiveType(type)) {
            return true;
        }

        // Look in Smali classes first because they'd overload framework classes.
        BuilderClassDef classDef = getClassFromType(classes, type);
        if (classDef != null) {
            return (classDef.getAccessFlags() & AccessFlags.FINAL.getValue()) != 0;
        }

        String javaName = type.substring(1, type.length() - 1).replaceAll("/", ".");
        try {
            Class<?> clazz = Class.forName(javaName);

            return (clazz.getModifiers() & Modifier.FINAL) != 0;
        } catch (ClassNotFoundException e) {
        }

        return false;
    }

    private static boolean allArgumentsKnown(MethodExecutionContext ectx) {
        for (int i = 0; i < ectx.getRegisterCount(); i++) {
            if (ectx.peekRegister(i).getValue() instanceof UnknownValue) {
                return false;
            }
        }

        return true;
    }

    // private static boolean isClassDefined(List<BuilderClassDef> classDefs, String type) {
    // BuilderClassDef classDef = getClassFromType(classDefs, type);
    //
    // if (classDef == null) {
    // return false;
    // }
    //
    // return true;
    // }

    private static BuilderMethod getMethodFromRef(List<BuilderClassDef> classDefs, MethodReference methodRef) {
        String target = ReferenceUtil.getMethodDescriptor(methodRef);
        for (BuilderClassDef classDef : classDefs) {
            for (BuilderMethod method : classDef.getMethods()) {
                String descriptor = ReferenceUtil.getMethodDescriptor(method);
                if (target.equals(descriptor)) {
                    return method;
                }
            }
        }

        return null;
    }
}
