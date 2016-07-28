package org.cf.smalivm;

import org.cf.smalivm.context.ExecutionGraph;
import org.cf.smalivm.context.HeapItem;
import org.cf.smalivm.type.VirtualClass;
import org.cf.smalivm.type.VirtualField;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class StaticFieldAccessorTest {

    private static final String CLASS_NAME = "Lstatic_field_accessor_test;";

    @Test
    public void canGetFieldsWhileInitializing() {
        VirtualMachine vm = VMTester.spawnVM();
        ExecutionGraph graph = VMTester.execute(vm, CLASS_NAME, "stubMethod()V");

        VirtualClass virtualClass = vm.getClassManager().getVirtualClass(CLASS_NAME);
        VirtualField alwaysZeroField = virtualClass.getField("alwaysZero");
        HeapItem alwaysZeroItem = graph.getTerminatingFieldConsensus(alwaysZeroField);
        assertEquals(0, alwaysZeroItem.getValue());

        VirtualField testInt1Field = virtualClass.getField("testInt1");
        HeapItem testInt1Item = graph.getTerminatingFieldConsensus(testInt1Field);
        assertEquals(0, testInt1Item.getValue());

        VirtualField testInt2Field = virtualClass.getField("testInt2");
        HeapItem testInt2Item = graph.getTerminatingFieldConsensus(testInt2Field);
        assertEquals(1, testInt2Item.getValue());
    }
}
