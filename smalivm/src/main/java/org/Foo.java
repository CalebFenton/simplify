package org.cf;

public interface Foo {

    int bar = 3; // => java.lang.ClassFormatError: Illegal field modifiers in class Foo: 0x1A
}
