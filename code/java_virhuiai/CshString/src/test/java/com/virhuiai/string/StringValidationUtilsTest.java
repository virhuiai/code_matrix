package com.virhuiai.string;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class StringValidationUtilsTest {
    private StringValidationUtils utils = Str.Utils;

    @Test
    public void testIsEmpty() {
        assertTrue(utils.isEmpty(null));
        assertTrue(utils.isEmpty(""));
        assertFalse(utils.isEmpty(" "));
        assertFalse(utils.isEmpty("abc"));
    }

    @Test
    public void testIsNotEmpty() {
        assertFalse(utils.isNotEmpty(null));
        assertFalse(utils.isNotEmpty(""));
        assertTrue(utils.isNotEmpty(" "));
        assertTrue(utils.isNotEmpty("abc"));
    }

    @Test
    public void testHasText() {
        assertFalse(utils.hasText(null));
        assertFalse(utils.hasText(""));
        assertFalse(utils.hasText(" "));
        assertTrue(utils.hasText("abc"));
    }

    @Test
    public void testIsBlank() {
        assertTrue(utils.isBlank(null));
        assertTrue(utils.isBlank(""));
        assertTrue(utils.isBlank(" "));
        assertFalse(utils.isBlank("abc"));
    }

    @Test
    public void testIsNotBlank() {
        assertFalse(utils.isNotBlank(null));
        assertFalse(utils.isNotBlank(""));
        assertFalse(utils.isNotBlank(" "));
        assertTrue(utils.isNotBlank("abc"));
    }

    @Test
    public void testIsInteger() {
        assertTrue(utils.isInteger("123"));
        assertTrue(utils.isInteger("-123"));
        assertTrue(utils.isInteger("+123"));
        assertFalse(utils.isInteger("123.45"));
        assertFalse(utils.isInteger("abc"));
    }

    @Test
    public void testIsNumeric() {
        assertTrue(utils.isNumeric("123"));
        assertFalse(utils.isNumeric("123.45"));
        assertFalse(utils.isNumeric("abc"));
        assertFalse(utils.isNumeric(null));
        assertFalse(utils.isNumeric(""));
    }

    @Test
    public void testIsAlpha() {
        assertTrue(utils.isAlpha("abc"));
        assertFalse(utils.isAlpha("abc123"));
        assertFalse(utils.isAlpha(null));
        assertFalse(utils.isAlpha(""));
    }

    @Test
    public void testIsAlphanumeric() {
        assertTrue(utils.isAlphanumeric("abc123"));
        assertFalse(utils.isAlphanumeric("abc123!"));
        assertFalse(utils.isAlphanumeric(null));
        assertFalse(utils.isAlphanumeric(""));
    }

    @Test
    public void testIsLowerCase() {
        assertTrue(utils.isLowerCase("abc"));
        assertFalse(utils.isLowerCase("Abc"));
        assertFalse(utils.isLowerCase(null));
        assertFalse(utils.isLowerCase(""));
    }

    @Test
    public void testIsUpperCase() {
        assertTrue(utils.isUpperCase("ABC"));
        assertFalse(utils.isUpperCase("Abc"));
        assertFalse(utils.isUpperCase(null));
        assertFalse(utils.isUpperCase(""));
    }

    @Test
    public void testStartsWith() {
        assertTrue(utils.startsWith("abc", "a"));
        assertFalse(utils.startsWith("abc", "b"));
        assertFalse(utils.startsWith(null, "a"));
        assertFalse(utils.startsWith("abc", null));
    }

    @Test
    public void testEndsWith() {
        assertTrue(utils.endsWith("abc", "c"));
        assertFalse(utils.endsWith("abc", "b"));
        assertFalse(utils.endsWith(null, "c"));
        assertFalse(utils.endsWith("abc", null));
    }

    @Test
    public void testContains() {
        assertTrue(utils.contains("abc", "b"));
        assertFalse(utils.contains("abc", "d"));
        assertFalse(utils.contains(null, "b"));
        assertFalse(utils.contains("abc", null));
    }
}