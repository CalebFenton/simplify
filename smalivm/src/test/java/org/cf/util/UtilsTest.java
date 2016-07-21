package org.cf.util;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import org.jf.dexlib2.writer.builder.BuilderTypeList;
import org.jf.dexlib2.writer.builder.BuilderTypeReference;
import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UtilsTest {

    private Random rng;

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
        String arrayType = "[I";
        int length = 1 + rng.nextInt(100);
        int[] expected = new int[length];

        Object actual = Utils.buildArray(arrayType, length);

        assertArrayEquals(expected, (int[]) actual);
    }

    @Test
    public void buildArrayOf2DPrimitiveClassArrayReturnsExpected() throws ClassNotFoundException {
        String arrayType = "[[I";
        int length = 1 + rng.nextInt(100);
        int[][] expected = new int[length][];

        Object actual = Utils.buildArray(arrayType, length);

        assertArrayEquals(expected, (int[][]) actual);
    }

    @Test
    public void buildArrayOf3DNonLocalClassArrayReturnsExpected() throws ClassNotFoundException {
        String arrayType = "[[[Ljava/lang/String;";
        int length = 1 + rng.nextInt(100);
        String[][][] expected = new String[length][][];

        Object actual = Utils.buildArray(arrayType, length);

        assertArrayEquals(expected, (String[][][]) actual);
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

    @Test
    public void shiftIntegerMapKeysShiftsUpAsExpected() {
        TIntObjectMap<String> intToObject = new TIntObjectHashMap<String>();
        intToObject.put(1, "one");
        intToObject.put(2, "two");
        intToObject.put(3, "three");

        Utils.shiftIntegerMapKeys(1, 2, intToObject);
        assertEquals(3, intToObject.size());
        assertEquals("one", intToObject.get(1));
        assertEquals("two", intToObject.get(4));
        assertEquals("three", intToObject.get(5));
    }

    @Test
    public void shiftIntegerMapKeysRemovesWhenShiftingDown() {
        TIntObjectMap<String> intToObject = new TIntObjectHashMap<String>();
        intToObject.put(1, "one");
        intToObject.put(2, "two");
        intToObject.put(3, "three");

        Utils.shiftIntegerMapKeys(1, -1, intToObject);
        assertEquals(2, intToObject.size());
        assertEquals("two", intToObject.get(1));
        assertEquals("three", intToObject.get(2));
    }

    @Test
    public void builderTypeListToStringListReturnsExpected() {
        List<BuilderTypeReference> types = new LinkedList<BuilderTypeReference>();
        BuilderTypeReference typeRef1 = mock(BuilderTypeReference.class);
        BuilderTypeReference typeRef2 = mock(BuilderTypeReference.class);
        when(typeRef1.getType()).thenReturn("typeRef1");
        when(typeRef2.getType()).thenReturn("typeRef2");
        types.add(typeRef1);
        types.add(typeRef2);

        BuilderTypeList typeList = new BuilderTypeList(types);
        List<String> typeNames = Utils.builderTypeListToTypeNames(typeList);

        assertEquals(2, typeNames.size());
        assertEquals("typeRef1", typeNames.get(0));
        assertEquals("typeRef2", typeNames.get(1));
    }

    @Test
    public void getRegisterSizeWithBuilderTypeListReturnsExpectedValue() {
        List<BuilderTypeReference> types = new LinkedList<BuilderTypeReference>();
        BuilderTypeReference typeRef1 = mock(BuilderTypeReference.class);
        BuilderTypeReference typeRef2 = mock(BuilderTypeReference.class);
        when(typeRef1.getType()).thenReturn("I");
        when(typeRef2.getType()).thenReturn("J");
        types.add(typeRef1);
        types.add(typeRef2);

        BuilderTypeList typeList = new BuilderTypeList(types);
        int registerSize = Utils.getRegisterSize(typeList);

        assertEquals(3, registerSize);
    }

    @Test
    public void castToPrimitiveWithBooleanReturnsExpectedValues() {
        assertEquals(true, Utils.castToPrimitive(Boolean.TRUE, "Z"));
        assertEquals(false, Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Boolean;"));

        assertEquals((byte) 1, Utils.castToPrimitive(Boolean.TRUE, "B"));
        assertEquals((byte) 0, Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Byte;"));

        assertEquals(1, Utils.castToPrimitive(Boolean.TRUE, "I"));
        assertEquals(0, Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Integer;"));

        assertEquals((short) 1, Utils.castToPrimitive(Boolean.TRUE, "S"));
        assertEquals((short) 0, Utils.castToPrimitive(Boolean.FALSE, "Ljava/lang/Short;"));
    }

    @Test
    public void castToPrimitiveWithCharacterReturnsExpectedValues() {
        char value1 = 'a';
        char value2 = 'b';

        assertEquals(value1, Utils.castToPrimitive(value1, "C"));
        assertEquals(value2, Utils.castToPrimitive(Character.valueOf(value2), "Ljava/lang/Character;"));

        assertEquals(true, Utils.castToPrimitive((char) 1, "Z"));
        assertEquals(false, Utils.castToPrimitive((char) 0, "Ljava/lang/Boolean;"));

        assertEquals((byte) value1, Utils.castToPrimitive(value1, "B"));
        assertEquals((byte) value2, Utils.castToPrimitive(value2, "Ljava/lang/Byte;"));

        assertEquals((int) value1, Utils.castToPrimitive(value1, "I"));
        assertEquals((int) value2, Utils.castToPrimitive(value2, "Ljava/lang/Integer;"));

        assertEquals((short) value1, Utils.castToPrimitive(value1, "S"));
        assertEquals((short) value2, Utils.castToPrimitive(value2, "Ljava/lang/Short;"));
    }

    @Test
    public void castToPrimitiveWithNumberReturnsExpectedValues() {
        int value1 = 0;
        int value2 = 1;

        assertEquals((byte) value1, Utils.castToPrimitive(Byte.valueOf((byte) value1), "B"));
        assertEquals((byte) value2, Utils.castToPrimitive(Byte.valueOf((byte) value2), "Ljava/lang/Byte;"));

        assertEquals((double) value1, Utils.castToPrimitive(Double.valueOf(value1), "D"));
        assertEquals((double) value2, Utils.castToPrimitive(Double.valueOf(value2), "Ljava/lang/Double;"));

        assertEquals((float) value1, Utils.castToPrimitive(Float.valueOf(value1), "F"));
        assertEquals((float) value2, Utils.castToPrimitive(Float.valueOf(value2), "Ljava/lang/Float;"));

        assertEquals(value1, Utils.castToPrimitive(value1, "I"));
        assertEquals(value2, Utils.castToPrimitive(value2, "Ljava/lang/Integer;"));

        assertEquals((long) value1, Utils.castToPrimitive(Long.valueOf(value1), "J"));
        assertEquals((long) value2, Utils.castToPrimitive(Long.valueOf(value2), "Ljava/lang/Long;"));

        assertEquals((short) value1, Utils.castToPrimitive(Short.valueOf((short) value1), "S"));
        assertEquals((short) value2, Utils.castToPrimitive(Short.valueOf((short) value2), "Ljava/lang/Short;"));

        assertEquals((char) value1, Utils.castToPrimitive(value1, "C"));
        assertEquals((char) value2, Utils.castToPrimitive(value2, "Ljava/lang/Character;"));

        assertEquals(true, Utils.castToPrimitive(1, "Z"));
        assertEquals(false, Utils.castToPrimitive(0, "Ljava/lang/Boolean;"));
    }

}
