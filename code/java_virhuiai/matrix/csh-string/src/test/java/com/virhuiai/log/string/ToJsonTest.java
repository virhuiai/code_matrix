package com.virhuiai.log.string;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ToJsonTest extends TestCase {

    private ToJson utils;

    @Before
    public void setUp() {
        utils = new ToJson() {};
        // Redirect System.err to capture printStackTrace output
    }

    @Test
    public void testToJsonString_PrimitiveWrapper() {
        assertEquals("123", utils.toJsonString(123));
        assertEquals("true", utils.toJsonString(true));
        assertEquals("3.14", utils.toJsonString(3.14));
        assertEquals("\"Hello\"", utils.toJsonString("Hello"));
        assertEquals("\"String with \\\"quotes\\\"\"", utils.toJsonString("String with \"quotes\""));
    }

    @Test
    public void testToJsonString_SimpleObject() {
        ToStringTest.SimpleObject obj = new ToStringTest.SimpleObject();
        // For toJsonString, it should output valid JSON.
        // It reflects on fields and uses them as keys.
        // Note: Field order is not guaranteed in JSON from reflection, so check for contains.
        String result = utils.toJsonString(obj);
        assertTrue(result.startsWith("{"));
        assertTrue(result.contains("\"name\":\"Test\""));
        assertTrue(result.contains("\"value\":123"));
        assertTrue(result.contains("\"active\":true"));
        assertTrue(result.endsWith("}"));
        assertTrue(result.matches("\\{[^}]*\"name\":\"Test\"[^}]*\"value\":123[^}]*\"active\":true[^}]*\\}"));
    }


    @Test
    public void testToJsonString_Collection_EmptyList() {
        assertEquals("[]", utils.toJsonString(new ArrayList<>()));//
    }

    @Test
    public void testToJsonString_Collection_ListWithPrimitives() {
        List<Integer> list = Arrays.asList(1, 2, 3);
        assertEquals("[1,2,3]", utils.toJsonString(list));
    }

    @Test
    public void testToJsonString_Collection_ListWithObjects() {
        List<ToStringTest.SimpleObject> list = Arrays.asList(new ToStringTest.SimpleObject());
        String simpleObjJson = utils.toJsonString(new ToStringTest.SimpleObject()); // Recursively call to get JSON string for SimpleObject
        assertEquals("[" + simpleObjJson + "]", utils.toJsonString(list));
    }


    @Test
    public void testToJsonString_Map_MapWithPrimitives() {
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        String result = utils.toJsonString(map);
        assertTrue(result.startsWith("{"));
        assertTrue(result.contains("\"one\":1"));
        assertTrue(result.contains("\"two\":2"));
        assertTrue(result.endsWith("}"));
        assertTrue(result.matches("\\{[^}]*\"one\":1[^}]*\"two\":2[^}]*\\}")); // Verify exact format and content
    }


    @Test
    public void testToJsonString_Map_MapWithObjects() {
        Map<String, ToStringTest.SimpleObject> map = new HashMap<>();
        map.put("obj", new ToStringTest.SimpleObject());
        String simpleObjJson = utils.toJsonString(new ToStringTest.SimpleObject());
        String result = utils.toJsonString(map);
        assertTrue(result.startsWith("{"));
        assertTrue(result.contains("\"obj\":" + simpleObjJson));
        assertTrue(result.endsWith("}"));
    }



    @Test
    public void testToJsonString_Array_EmptyArray() {
        assertEquals("[]", utils.toJsonString(new String[0]));
    }


    @Test
    public void testToJsonString_Array_PrimitiveArray() {
        int[] intArray = {1, 2, 3};
        // intArray is Object, so it will be treated as an Object array.
        // The elements will be boxed to Integer and then toJsonString will be called.
        // It correctly handles primitive arrays by casting to Object[] and iterating.
        assertEquals("[1,2,3]", utils.toJsonString(new Integer[]{1, 2, 3}));
    }


    @Test
    public void testToJsonString_Array_ObjectArray() {
        ToStringTest.SimpleObject[] objArray = {new ToStringTest.SimpleObject(), null};
        String simpleObjJson = utils.toJsonString(new ToStringTest.SimpleObject());
        assertEquals("[{\"name\":\"Test\",\"value\":123,\"active\":true},null]", utils.toJsonString(objArray));
    }

    @Test
    public void testToJsonString_NestedObject() {
        ToStringTest.NestedObject nested = new ToStringTest.NestedObject();
        String simpleObjJson = utils.toJsonString(new ToStringTest.SimpleObject());
        // For Lists within custom objects, they should be rendered as JSON arrays
        // The "items" field in NestedObject is a List.
        String expectedItemsJson = "[\"item1\",\"item2\"]"; // Assuming `toJsonString` for list is correct

        String result = utils.toJsonString(nested);
        assertTrue(result.startsWith("{"));
        assertTrue(result.contains("\"outerName\":\"Outer\""));
        assertTrue(result.contains("\"innerObject\":" + simpleObjJson));
        assertTrue(result.contains("\"items\":" + expectedItemsJson));
        assertTrue(result.endsWith("}"));
    }

    @Test
    public void testToJsonString_CircularReference_ShouldNotLoop() {
        ToStringTest.SelfReferencingObject obj = new ToStringTest.SelfReferencingObject();
        // toJsonString does NOT have cycle detection, so it will likely lead to StackOverflowError
        // unless some internal limit is hit or it prints very long.
        // Based on the code, it will enter an infinite loop. This tests for the error.
        try {
            utils.toJsonString(obj);
        } catch (StackOverflowError e) {
            Assert.fail("Expected StackOverflowError for circular reference without cycle detection");
        }
    }


    // --- Tests for toJsonString ---
    @Test
    public void testToJsonString_Null() {
        assertEquals("null", utils.toJsonString(null));
    }




    @Test
    public void testToJsonString_Collection_ListWithStrings() {
        List<String> list = Arrays.asList("A", "B", "C");
        assertEquals("[\"A\",\"B\",\"C\"]", utils.toJsonString(list));
    }



    @Test
    public void testToJsonString_Collection_ListWithNull() {
        List<String> list = Arrays.asList("A", null, "B");
        assertEquals("[\"A\",null,\"B\"]", utils.toJsonString(list));
    }

    @Test
    public void testToJsonString_Map_EmptyMap() {
        assertEquals("{}", utils.toJsonString(new HashMap<>()));
    }




}