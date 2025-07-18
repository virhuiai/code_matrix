package com.virhuiai.log.string;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

/**
 * ：处理带循环引用检测的对象到字符串转换。 测试
 */
public class ToStringnCycleDetectionTest extends TestCase {

    private ToStringnCycleDetection utils;

    @Before
    public void setUp() {
        utils = new ToStringnCycleDetection() {};
        // Redirect System.err to capture printStackTrace output
    }

    // --- Tests for Cycle Detection ---
    @Test
    public void testToStringWithCycleDetection_Null() {
        assertEquals("null", utils.toStringWithCycleDetection(null, new IdentityHashMap<Object, Boolean>()));
    }

    @Test
    public void testToStringWithCycleDetection_NoCycle() {
        ToStringTest.SimpleObject obj = new ToStringTest.SimpleObject();
        String expected = "com.virhuiai.string.ToStringTest$SimpleObject{name=Test; value=123; active=true}";
        assertEquals(expected, utils.toStringWithCycleDetection(obj, new IdentityHashMap<Object, Boolean>()));
    }

    @Test
    public void testToStringWithCycleDetection_SelfReferencingObject() {
        ToStringTest.SelfReferencingObject obj = new ToStringTest.SelfReferencingObject();
        // The 'self' field should be detected as a cycle
        String expected = "com.virhuiai.string.ToStringTest$SelfReferencingObject{name=Self; self=[Cyclic Reference]}";
        assertEquals(expected, utils.toStringWithCycleDetection(obj, new IdentityHashMap<Object, Boolean>()));
    }

    @Test
    public void testToStringWithCycleDetection_TwoWayReference() {
        ToStringTest.TwoWayReferenceA a = new ToStringTest.TwoWayReferenceA();
        ToStringTest.TwoWayReferenceB b = new ToStringTest.TwoWayReferenceB();
        a.b = b;
        b.a = a;

        String expectedA = "com.virhuiai.string.ToStringTest$TwoWayReferenceA{name=A; b=com.virhuiai.string.ToStringTest$TwoWayReferenceB{name=B; a=[Cyclic Reference]}}";
        assertEquals(expectedA, utils.toStringWithCycleDetection(a, new IdentityHashMap<Object, Boolean>()));

        // Also test starting from B
        String expectedB = "com.virhuiai.string.ToStringTest$TwoWayReferenceB{name=B; a=com.virhuiai.string.ToStringTest$TwoWayReferenceA{name=A; b=[Cyclic Reference]}}";
        assertEquals(expectedB, utils.toStringWithCycleDetection(b, new IdentityHashMap<Object, Boolean>()));
    }

    @Test
    public void testToStringWithCycleDetection_ListWithSelfReference() {
        // todo just here
        List<Object> list = new ArrayList<>();
        list.add("Item1");
        list.add(list); // Self-referencing list
        list.add("Item2");

        String expectedList = "java.util.ArrayList{Item1; [Cyclic Reference]; Item2}";
        assertEquals(expectedList, utils.toStringWithCycleDetection(list, new IdentityHashMap<Object, Boolean>()));// todo now
    }

    @Test
    public void testToStringWithCycleDetection_MapWithSelfReference() {
        // todo just here
        Map<String, Object> map = new HashMap<>();
        map.put("key1", "value1");
        map.put("self", map); // Self-referencing map
        map.put("key2", "value2");
        //java.util.HashMap{key1=value1; key2=value2; self=[Cyclic Reference]}
        String result = utils.toStringWithCycleDetection(map, new IdentityHashMap<Object, Boolean>());
        assertTrue(result.startsWith("java.util.HashMap{"));
        assertTrue(result.contains("key1=value1"));
        assertTrue(result.contains("self=[Cyclic Reference]"));
        assertTrue(result.contains("key2=value2"));
        assertTrue(result.endsWith("}"));
    }

    // Test for a deeper cycle (Node A -> Node B -> Node C -> Node A)
    @Test
    public void testToStringWithCycleDetection_DeeperCycle() {
        ToStringTest.Node nodeA = new ToStringTest.Node("A");
        ToStringTest.Node nodeB = new ToStringTest.Node("B");
        ToStringTest.Node nodeC = new ToStringTest.Node("C");

        nodeA.next = nodeB;
        nodeB.next = nodeC;
        nodeC.next = nodeA; // Cycle A->B->C->A

        String expected = "com.virhuiai.string.ToStringTest$Node{id=A; next=com.virhuiai.string.ToStringTest$Node{id=B; next=com.virhuiai.string.ToStringTest$Node{id=C; next=[Cyclic Reference]}}}";
        assertEquals(expected, utils.toStringWithCycleDetection(nodeA, new IdentityHashMap<Object, Boolean>()));
    }

}