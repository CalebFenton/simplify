package org.cf.smalivm.context;

import org.cf.smalivm.SideEffect;
import org.cf.smalivm.VirtualMachine;
import org.cf.smalivm.opcode.Op;
import org.cf.smalivm.type.VirtualField;
import org.cf.smalivm.type.VirtualMethod;
import org.cf.smalivm.type.VirtualType;
import org.jf.dexlib2.builder.MethodLocation;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

public interface ExecutionGraph extends Iterable<ExecutionNode> {
    String toString();

    void addNode(ExecutionNode node);

    int[] getAddresses();

    Set<VirtualType> getAllPossiblyInitializedClasses(int[] addresses);

    int[] getConnectedTerminatingAddresses();

    HeapItem getFieldConsensus(int[] addresses, VirtualField field);

    Set<HeapItem> getFieldItems(int address, VirtualField field);

    SideEffect.Level getHighestClassSideEffectLevel(VirtualType virtualClass);

    SideEffect.Level getHighestMethodSideEffectLevel();

    SideEffect.Level getHighestSideEffectLevel();

    Collection<MethodLocation> getLocations();

    VirtualMethod getMethod();

    int getNodeCount();

    List<ExecutionNode> getNodePile(int address);

    @Nullable
    Op getOp(int address);

    HeapItem getRegisterConsensus(int address, int register);

    @Nullable
    HeapItem getRegisterConsensus(int[] addresses, int register);

    Object getRegisterConsensusValue(int address, int register);

    @Nullable
    Object getRegisterConsensusValue(int[] addresses, int register);

    Set<HeapItem> getRegisterItems(int address, int register);

    ExecutionNode getRoot();

    @Nullable
    ExecutionNode getTemplateNode(int address);

    int[] getTerminatingAddresses();

    List<ExecutionContext> getTerminatingContexts();

    HeapItem getTerminatingFieldConsensus(VirtualField field);

    Map<VirtualField, HeapItem> getTerminatingFieldConsensus(VirtualField[] fields);

    @Nullable
    HeapItem getTerminatingRegisterConsensus(int register);

    @Nullable
    Map<Integer, HeapItem> getTerminatingRegisterConsensus(int[] registers);

    VirtualMachine getVM();

    @Override
    Iterator<ExecutionNode> iterator();

    boolean wasAddressReached(int address);

    String toSmali();

    String toSmali(boolean includeAddress);
}
