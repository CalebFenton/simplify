package org.cf.smalivm.context;

import org.cf.util.ClassNameUtils;

public class StackTraceElement {

    private String definingClass;
    private String methodName;
    private String fileName;
    private int lineNumber;

    public StackTraceElement(String methodDescriptor, String fileName, int lineNumber) {
        String[] parts = methodDescriptor.split("->");
        definingClass = ClassNameUtils.internalToBinary(parts[0]);
        methodName = parts[1].split("\\(")[0];
        this.fileName = fileName;
        this.lineNumber = lineNumber;
    }

    public String getDeclaringClass() {
        return definingClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getFileName() {
        return fileName;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        // E.g. java.lang.Thread.getStackTrace(Thread.java:1589)
        StringBuilder sb = new StringBuilder(getDeclaringClass());
        sb.append('.').append(getMethodName()).append('(').append(getFileName()).append(':').append(getLineNumber())
                        .append(')');

        return sb.toString();
    }

}
