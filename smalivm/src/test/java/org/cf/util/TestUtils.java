package org.cf.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import org.cf.smalivm.type.LocalInstance;
import org.junit.Before;
import org.junit.Test;

public class TestUtils {

    Random rng;

    @Before
    public void setUp() {
        rng = new Random();
    }

    @Test
    public void getArrayDimensionStringWithNonArrayObjectReturnsEmptyString() {
        String value = "some string";
        String expected = "";

        String actual = Utils.getArrayDimensionString(value);

        assertEquals(expected, actual);
    }

    @Test
    public void getArrayDimensionStringWith1DArrayReturnsExpected() {
        int[] value = new int[5];
        String expected = "[5]";

        String actual = Utils.getArrayDimensionString(value);

        assertEquals(expected, actual);

    }

    @Test
    public void getArrayDimensionStringWith3DArrayReturnsExpected() {
        String[][][] value = new String[5][4][3];
        String expected = "[5][4][3]";

        String actual = Utils.getArrayDimensionString(value);

        assertEquals(expected, actual);
    }

    @Test
    public void buildArrayOf1DPrimitiveClassArrayReturnsExpected() throws ClassNotFoundException {
        boolean useLocalClass = false;
        String arrayType = "[I";
        int length = 1 + rng.nextInt(100);
        int[] expected = new int[length];

        Object actual = Utils.buildArray(arrayType, length, useLocalClass);

        assertArrayEquals(expected, (int[]) actual);
    }

    @Test
    public void buildArrayOf2DPrimitiveClassArrayReturnsExpected() throws ClassNotFoundException {
        boolean useLocalClass = false;
        String arrayType = "[[I";
        int length = 1 + rng.nextInt(100);
        int[][] expected = new int[length][];

        Object actual = Utils.buildArray(arrayType, length, useLocalClass);

        assertArrayEquals(expected, (int[][]) actual);
    }

    @Test
    public void buildArrayOf3DNonLocalClassArrayReturnsExpected() throws ClassNotFoundException {
        boolean useLocalClass = false;
        String arrayType = "[[[Ljava/lang/String;";
        int length = 1 + rng.nextInt(100);
        String[][][] expected = new String[length][][];

        Object actual = Utils.buildArray(arrayType, length, useLocalClass);

        assertArrayEquals(expected, (String[][][]) actual);
    }

    @Test
    public void buildArrayOf3DLocalClassArrayReturnsExpected() throws ClassNotFoundException {
        boolean useLocalClass = true;
        String arrayType = "[[[Lsome/local/ClassyClass;";
        int length = 1 + rng.nextInt(100);
        LocalInstance[][][] expected = new LocalInstance[length][][];
        Object actual = Utils.buildArray(arrayType, length, useLocalClass);

        assertArrayEquals(expected, (LocalInstance[][][]) actual);
    }

    @Test
    public void getDimensionCountOfNonArrayReturnsZero() {
        String typeReference = "I";
        int expected = 0;
        int actual = Utils.getDimensionCount(typeReference);

        assertEquals(expected, actual);
    }

    @Test
    public void getDimensionCountOf1DArrayReturnsOne() {
        String typeReference = "[Lrandom/class;";
        int expected = 1;
        int actual = Utils.getDimensionCount(typeReference);

        assertEquals(expected, actual);
    }

    @Test
    public void getDimensionCountOfRandomRankArrayReturnsExpected() {
        StringBuilder sb = new StringBuilder();
        int expected = 1 + rng.nextInt(100);
        for (int i = 0; i < expected; i++) {
            sb.append('[');
        }
        sb.append("Lsome/Type;");
        String typeReference = sb.toString();
        int actual = Utils.getDimensionCount(typeReference);

        assertEquals(expected, actual);
    }

    @Test
    public void getRegisterSizeWithEmptyListReturnsZero() {
        List<String> typeNames = new LinkedList<String>();
        int expected = 0;
        int actual = Utils.getRegisterSize(typeNames);

        assertEquals(expected, actual);
    }

    @Test
    public void getRegisterSizeWithTwoOneRegisterTypesReturnsTwo() {
        List<String> typeNames = new LinkedList<String>();
        typeNames.add("I"); // 1
        typeNames.add("J"); // 2
        typeNames.add("D"); // 2
        int expected = 5;
        int actual = Utils.getRegisterSize(typeNames);

        assertEquals(expected, actual);
    }

}
