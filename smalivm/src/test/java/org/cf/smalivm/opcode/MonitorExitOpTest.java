package org.cf.smalivm.opcode;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.cf.smalivm.VMTester;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.ExecutionNode;
import org.junit.jupiter.api.Test;

public class MonitorExitOpTest {
    private static final String CLASS_NAME = "Lmonitor_exit_test;";

    @Test
    public void monitorEnterHasExpectedTostring() {
        String methodDescriptor = "monitorExit()V";
        VirtualMachine vm = VMTester.spawnVM();
        ExecutionGraph graph = vm.spawnInstructionGraph(CLASS_NAME, methodDescriptor);
        ExecutionNode node = graph.getTemplateNode(1);
        assertEquals("monitor-exit r0", node.getOp().toString());
    }
}
