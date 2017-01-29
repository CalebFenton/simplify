package org.cf.smalivm.opcode;

import gnu.trove.map.TIntObjectMap;

import org.cf.smalivm.VirtualMachine;
import org.jf.dexlib2.builder.MethodLocation;

public interface OpFactory {

    Op create(MethodLocation location, TIntObjectMap<MethodLocation> addressToLocation, VirtualMachine vm);
}
