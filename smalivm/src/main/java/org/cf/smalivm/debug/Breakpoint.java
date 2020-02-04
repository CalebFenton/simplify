package org.cf.smalivm.debug;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Breakpoint implements Comparable<Breakpoint> {

  private final String methodSignature;
  private final int instructionIndex;

  Breakpoint(String methodSignature, int instructionIndex) {
    this.methodSignature = methodSignature;
    this.instructionIndex = instructionIndex;
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(43, 941)
        .append(getMethodSignature())
        .append(getInstructionIndex())
        .hashCode();
  }

  public String getMethodSignature() {
    return methodSignature;
  }

  public int getInstructionIndex() {
    return instructionIndex;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }
    Breakpoint rhs = (Breakpoint) obj;

    return new EqualsBuilder()
        .append(getMethodSignature(), rhs.getMethodSignature())
        .append(getInstructionIndex(), rhs.getInstructionIndex())
        .isEquals();
  }

  public String toString() {
    return getMethodSignature() + " : " + getInstructionIndex();
  }

  @Override
  public int compareTo(Breakpoint rhs) {
    if (getMethodSignature().equals(rhs.getMethodSignature())) {
      return rhs.getInstructionIndex() - getInstructionIndex();
    }
    else {
      return getMethodSignature().compareTo(rhs.getMethodSignature());
    }
  }

}
