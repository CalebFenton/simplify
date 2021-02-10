package org.cf.smalivm.type;

import java.util.Collection;
import java.util.Set;
import org.jf.dexlib2.iface.reference.TypeReference;
import org.jf.dexlib2.writer.builder.DexBuilder;

public interface ClassManager {

  Set<String> getClassNames();

  DexBuilder getDexBuilder();

  Set<String> getFrameworkClassNames();

  Collection<VirtualClass> getLoadedClasses();

  VirtualMethod getMethod(String methodSignature);

  VirtualMethod getMethod(String className, String methodDescriptor);

  Set<String> getNonFrameworkClassNames();

  VirtualClass getVirtualClass(Class<?> klazz);

  VirtualClass getVirtualClass(String className);

  VirtualType getVirtualType(TypeReference typeReference);

  VirtualType getVirtualType(Class<?> typeClass);

  VirtualType getVirtualType(String typeSignature);

  boolean isFrameworkClass(VirtualType virtualClass);

  boolean isSafeFrameworkClass(VirtualType virtualClass);

  DexBuilder getFrameworkDexBuilder();
}
