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
import simplify.exec.MethodExecutionContext;
import simplify.exec.MethodReflector;
import simplify.exec.RegisterStore;
import simplify.exec.UnknownValue;

import com.google.common.primitives.Ints;

public class InvokeInstruction {
    private static final Logger log = Logger.getLogger(Main.class.getSimpleName());

    public static void execute(MethodExecutionContext ectx, Instruction instruction, int index,
                    List<BuilderClassDef> classes) {
        // Build called method context even before we know it's emulated because we'll
        // need to know types later to mark them as unknown if it's not emulated.
        MethodReference methodRef = getMethodReference(instruction);
        MethodExecutionContext calledContext = buildCalledMethodContext(ectx, instruction, methodRef, index);

        boolean hasUnknownArgument = false;
        for (int i = 0; i < calledContext.getRegisterCount(); i++) {
            if (calledContext.peekRegister(i).getValue() instanceof UnknownValue) {
                hasUnknownArgument = true;
                break;
            }
        }

        String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodRef);
        String returnType = methodRef.getReturnType();

        BuilderMethod method = getMethodFromRef(classes, methodRef);
        if (method != null) {
            // TODO: execute method with MethodExecutor
            log.fine("Found " + methodDescriptor + " but holding off on executing it.");
        } else if (MethodReflector.isSafeToReflect(methodDescriptor) && !hasUnknownArgument) {
            // Method not defined, but all arguments are known and method has been declared safe.
            MethodReflector.reflect(calledContext, methodRef.getParameterTypes(), methodDescriptor);

            if (!returnType.equals("V")) {
                ectx.setResultRegister(calledContext.getReturnRegister());
            }
        } else {
            // If the method isn't emulated or implemented in target, we don't know the return value.
            if (!returnType.equals("V")) {
                ectx.setResultRegister(new RegisterStore(returnType, new UnknownValue()));
            }

            // Any non-final classes passed as non-final parameters could have changed.
            int paramStart = calledContext.getParameterStart();
            for (int i = paramStart; i < calledContext.getRegisterCount(); i++) {
                RegisterStore rs = calledContext.getRegister(i, 0);
                if (!isImmutableClass(classes, rs.getType())) {
                    log.finer("r" + i + " (" + rs.getType() + ") is mutable/unknown and parameter, marking unknown");
                    ectx.addRegister(i, returnType, new UnknownValue(), index);
                } else {
                    log.finer("r" + i + " (" + rs.getType() + ") is immutable and parameter, retaining value");
                }
            }

        }

        // String methodDescriptor = ReferenceUtil.getMethodDescriptor(methodRef);
        // if (MethodEmulator.canEmulate(methodDescriptor)) {
        // log.info("Emulating " + methodDescriptor + " @" + index);
        // // log.info(ectx.toString());
        //
        // MethodEmulator.emulate(calledMethodContext, methodDescriptor);
        //
        // if (!methodRef.getReturnType().equals("V")) {
        // // Non-void method should have a return type.
        // ectx.setResultRegister(calledMethodContext.getReturnRegister());
        // }
        // } else {
    }

    private static int[] getInvokeRegisters(Instruction instruction, List<? extends CharSequence> typesList,
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

    private static MethodExecutionContext buildCalledMethodContext(MethodExecutionContext ectx,
                    Instruction instruction, MethodReference method, int index) {
        boolean isStatic = instruction.getOpcode().name.startsWith("invoke-static");
        int[] invokeRegisters = getInvokeRegisters(instruction, method.getParameterTypes(), isStatic);
        MethodExecutionContext calledMethodContext = new MethodExecutionContext(invokeRegisters.length,
                        invokeRegisters.length, ectx.getRemaingCallDepth() - 1);

        int startRegister = 0;
        if (!isStatic) {
            // Pass a reference to ectx's register store, not a clone, because it may be modified.
            RegisterStore rs = ectx.getRegister(invokeRegisters[0], index);
            calledMethodContext.addParameterRegister(0, rs);

            // Make the next loop to skip this parameter because type info won't be in the
            // method parameters, it's explicit in the instance type.
            startRegister++;
        }

        List<? extends CharSequence> parameterTypes = method.getParameterTypes();
        for (int i = startRegister; i < invokeRegisters.length; i++) {
            Object value = ectx.getRegisterValue(invokeRegisters[i], index);
            String type = parameterTypes.get(i - startRegister).toString();
            calledMethodContext.addParameterRegister(i, type, value);
        }

        return calledMethodContext;
    }

    private static MethodReference getMethodReference(Instruction instruction) {
        return (MethodReference) ((ReferenceInstruction) instruction).getReference();
    }

    private static BuilderMethod getMethodFromDescriptor(List<BuilderMethod> methods, String methodDescriptor) {
        for (BuilderMethod method : methods) {
            String md = ReferenceUtil.getMethodDescriptor(method);
            if (md.equals(methodDescriptor)) {
                return method;
            }
        }

        return null;
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
        if (type.length() == 1) {
            // Primitive type, e.g. I, Z, B, etc.
            return true;
        }

        // Look in dex classes first because they'd overload framework classes.
        BuilderClassDef classDef = getClassFromType(classes, type);
        if (classDef != null) {
            return (classDef.getAccessFlags() & AccessFlags.FINAL.getValue()) != 0;
        }

        String javaName = type.substring(1, type.length() - 1).replaceAll("/", ".");
        try {
            Class clazz = Class.forName(javaName);

            return (clazz.getModifiers() & Modifier.FINAL) != 0;
        } catch (ClassNotFoundException e) {
        }

        return false;
    }

    private static boolean isClassDefined(List<BuilderClassDef> classDefs, String type) {
        BuilderClassDef classDef = getClassFromType(classDefs, type);

        if (classDef == null) {
            return false;
        }

        return true;
    }

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
