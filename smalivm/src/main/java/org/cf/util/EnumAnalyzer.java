package org.cf.util;

import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.cf.smalivm.opcode.ConstOp;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.opcode.SPutOp;
import org.cf.smalivm.type.ClassManager;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualMethod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumAnalyzer {

    private static final String METHOD_DESCRIPTOR = "<clinit>()V";

    private final Map<String, String> originalNameToObfuscatedName;
    private final VirtualMachine vm;

    public EnumAnalyzer(VirtualMachine vm) {
        this.vm = vm;
        originalNameToObfuscatedName = new HashMap<>();
    }

    public void analyze(Class<? extends Enum> enumClass) {
        /*
         * If an enum is obfuscated, say with Proguard, the field names change. This is a problem
         * because it's not possible to instantiate Enums using reflection. Instead, you must use
         * Enum.valueOf(Class, String) where the String is the Enum name and also must be the name
         * of the *field*. If the field name changes, the actual string used in the enum's <clinit>
         * to initialize the field doesn't change. So we end up getting the valueOf() something
         * that doesn't exist. To deal with this, we "analyze" the enum to figure out which original
         * string maps to which field name. Then, when the code asks for the original, we look up
         * the mapped, obfuscated field name and use that instead.
         */
        ClassManager classManager = vm.getClassManager();
        VirtualClass virtualClass = classManager.getVirtualClass(enumClass);
        VirtualMethod method = virtualClass.getMethod(METHOD_DESCRIPTOR);

        ExecutionGraph graph = vm.spawnInstructionGraph(method);

        List<VirtualField> fields = new ArrayList<>(virtualClass.getFields());
        boolean lookingForField = false;
        String name = null;
        for (int address : graph.getAddresses()) {
            // Would be smarter, though significantly more work, to analyze semantics and know for
            // sure which literal was put into which field.
            ExecutionNode node = graph.getTemplateNode(address);
            Op op = node.getOp();
            if (op instanceof ConstOp) {
                ConstOp cop = (ConstOp) op;
                if (cop.getConstantType() == ConstOp.ConstantType.STRING) {
                    name = (String) cop.getLiteral();
                    lookingForField = true;
                }
            } else if(op instanceof SPutOp && lookingForField) {
                SPutOp sput = (SPutOp) op;
                String obfuscatedName = sput.getFieldReference().getName();
                originalNameToObfuscatedName.put(name, obfuscatedName);
                lookingForField = false;
            }
        }
    }

    public String getObfuscatedName(String name) {
        return originalNameToObfuscatedName.get(name);
    }
}
