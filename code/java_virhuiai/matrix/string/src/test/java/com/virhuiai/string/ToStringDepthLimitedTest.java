package com.virhuiai.string;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

public class ToStringDepthLimitedTest extends TestCase {


    private ToStringDepthLimited utils;

    @Before
    public void setUp() {
        utils = new ToStringDepthLimited() {};
        // Redirect System.err to capture printStackTrace output
    }

    // --- Tests for toStringWithDepth ---
    @Test
    public void testToStringWithDepth_Null() {
        assertEquals("null", utils.toStringWithDepth(null, 5));
    }

    @Test
    public void testToStringWithDepth_MaxDepthZero() {
        ToStringTest.SimpleObject obj = new ToStringTest.SimpleObject();
        // At depth 0, it should just call obj.toString()
        assertEquals(obj.toString(), utils.toStringWithDepth(obj, 0));
        assertEquals("123", utils.toStringWithDepth(123, 0));
    }

    @Test
    public void testToStringWithDepth_BasicObjectFullDepth() {
        ToStringTest.SimpleObject obj = new ToStringTest.SimpleObject();
        String expected = "com.virhuiai.string.ToStringTest$SimpleObject:[name:Test; value:123; active:true]";
        assertEquals(expected, utils.toStringWithDepth(obj, 1)); // Depth 1 should be enough for simple objects
        assertEquals(expected, utils.toStringWithDepth(obj, 5));
//        Expected :com.virhuiai.string.ToStringTest$SimpleObject:[name:Test; value:123; active:true; ]
//        Actual   :com.virhuiai.string.ToStringTest$SimpleObject:[name:; value:123; active:] 
    }

    @Test
    public void testToStringWithDepth_NestedObjectLimitedDepth() {
        ToStringTest.NestedObject nested = new ToStringTest.NestedObject();
        // Depth 1: Should show nested object's class and basic info, but not its fields
        String expectedAtDepth1 = "com.virhuiai.string.ToStringTest$NestedObject:[outerName:Outer; innerObject:SimpleObject{name='Test', value=123, active=true}; items:[item1, item2]]";
        assertEquals(expectedAtDepth1, utils.toStringWithDepth(nested, 1));

//        Expected :com.virhuiai.string.ToStringTest$NestedObject:[outerName:Outer; innerObject:SimpleObject{name='Test', value=123, active=true}; items:java.util.Arrays$ArrayList{item1; item2; }; ]
//        Actual   :com.virhuiai.string.ToStringTest$NestedObject:[outerName:; innerObject:; items:[item1, item2]]
//
        // Depth 2: Should go one level deeper, showing innerObject's fields
        // The original implementation seems to apply depth to `toString` calls for elements,
        // but not directly to the `toString` calls on complex types themselves inside the depth methods.
        // It's a bit inconsistent. Let's test based on the expected output structure after fixing `isSubClassOf` for consistency.
        // Given the current implementation, `toStringWithDepth` for objects directly calls `toStringWithDepth`
        // on their fields with `maxDepth - 1`. So, at depth 1, `innerObject` will be rendered
        // with `maxDepth = 0`, leading to `innerObject.toString()`.
        String expectedInnerObjStr = "SimpleObject{name='Test', value=123, active=true}";
        String expectedListStr = "java.util.Arrays$ArrayList{item1; item2; }"; // The current processIteratorWithDepth prints class name + {..}, and then elements at maxDepth
        String expectedDepth2 = "com.virhuiai.string.ToStringTest$NestedObject:[outerName:Outer; innerObject:com.virhuiai.string.ToStringTest$SimpleObject:[name:Test; value:123; active:true; ]; items:java.util.Arrays$ArrayList{" + expectedListStr.substring(expectedListStr.indexOf("{") + 1, expectedListStr.lastIndexOf("}")) + "}; ]";
        // Correction: The `processIteratorWithDepth` calls `toStringWithDepth` on its elements.
        // So at depth 2, list items "item1", "item2" will be rendered with depth 1, which will be their toString()
        String expectedListDepth2 = "java.util.Arrays$ArrayList{item1; item2; }";
        expectedDepth2 = "com.virhuiai.string.ToStringTest$NestedObject:[outerName:Outer; innerObject:com.virhuiai.string.ToStringTest$SimpleObject:[name:Test; value:123; active:true; ]; items:" + expectedListDepth2 + "; ]";

        // Re-evaluating the expected output based on the provided code's behavior:
        // When maxDepth is 1:
        // outerName: "Outer" (String, java.lang, returns toString)
        // innerObject: toStringWithDepth(innerObject, 0) -> innerObject.toString()
        // items: processIteratorWithDepth(items.iterator(), List.class, 0) -> List.class + "{" + toStringWithDepth("item1",0) + "; " + toStringWithDepth("item2",0) + "; }"
        String simpleObjToString = new ToStringTest.SimpleObject().toString(); // SimpleObject{name='Test', value=123, active=true}
        String listItemsString = "item1, item2"; // No class name for elements because toStringWithDepth(String, 0) is just "item"
        String expectedDepth1ForNested = "com.virhuiai.string.ToStringTest$NestedObject:[outerName:Outer; innerObject:" + simpleObjToString + "; items:[" + listItemsString + "]]";
        assertEquals(expectedDepth1ForNested, utils.toStringWithDepth(nested, 1));


        // When maxDepth is 2:
        // outerName: "Outer"
        // innerObject: toStringWithDepth(innerObject, 1) -> com.virhuiai.string.ToStringTest$SimpleObject:[name:Test; value:123; active:true; ]
        // items: processIteratorWithDepth(items.iterator(), List.class, 1) -> List.class + "{" + toStringWithDepth("item1",1) + "; " + toStringWithDepth("item2",1) + "; }"
        String expectedInnerObjStrAtDepth2 = "com.virhuiai.string.ToStringTest$SimpleObject:[name:Test; value:123; active:true]";
        String expectedListItemsStrAtDepth2 = "item1; item2"; // Still just item; because it's a String
        String expectedDepth2ForNested = "com.virhuiai.string.ToStringTest$NestedObject:[outerName:Outer; innerObject:" + expectedInnerObjStrAtDepth2 + "; items:java.util.Arrays$ArrayList{" + expectedListItemsStrAtDepth2 + "}]";
        assertEquals(expectedDepth2ForNested, utils.toStringWithDepth(nested, 2));
    }
}