/*
 * Copyright (c) 2001-2013 JGoodies Software GmbH. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Software GmbH nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.jgoodies.common.base;

/**
 * Provides frequently used system properties and utility methods
 * for system-related operations.
 *
 * @author Karsten Lentzsch
 */
public class SystemUtils {
    
    // System Properties
    public static final String OS_NAME = getSystemProperty("os.name", "unknown");
    public static final String OS_VERSION = getSystemProperty("os.version", "unknown");
    public static final String JAVA_VERSION = getSystemProperty("java.version", "unknown");
    public static final String JAVA_VENDOR = getSystemProperty("java.vendor", "unknown");
    public static final String JAVA_VM_VERSION = getSystemProperty("java.vm.version", "unknown");
    
    // OS Detection Constants
    public static final boolean IS_OS_WINDOWS = startsWithIgnoreCase(OS_NAME, "windows");
    public static final boolean IS_OS_MAC = startsWithIgnoreCase(OS_NAME, "mac os x");
    public static final boolean IS_OS_LINUX = startsWithIgnoreCase(OS_NAME, "linux");
    public static final boolean IS_OS_UNIX = IS_OS_MAC || IS_OS_LINUX;
    
    // Windows Specific Versions
    public static final boolean IS_OS_WINDOWS_95 = IS_OS_WINDOWS && startsWith(OS_VERSION, "4.0");
    public static final boolean IS_OS_WINDOWS_98 = IS_OS_WINDOWS && startsWith(OS_VERSION, "4.1");
    public static final boolean IS_OS_WINDOWS_ME = IS_OS_WINDOWS && startsWith(OS_VERSION, "4.9");
    public static final boolean IS_OS_WINDOWS_NT = IS_OS_WINDOWS && startsWith(OS_NAME, "windows nt");
    public static final boolean IS_OS_WINDOWS_2000 = IS_OS_WINDOWS && startsWith(OS_VERSION, "5.0");
    public static final boolean IS_OS_WINDOWS_XP = IS_OS_WINDOWS && startsWith(OS_VERSION, "5.1");
    public static final boolean IS_OS_WINDOWS_VISTA = IS_OS_WINDOWS && startsWith(OS_VERSION, "6.0");
    public static final boolean IS_OS_WINDOWS_7 = IS_OS_WINDOWS && startsWith(OS_VERSION, "6.1");
    public static final boolean IS_OS_WINDOWS_8 = IS_OS_WINDOWS && startsWith(OS_VERSION, "6.2");
    public static final boolean IS_OS_WINDOWS_10 = IS_OS_WINDOWS && startsWith(OS_VERSION, "10.0");
    
    // Windows Version Groups
    public static final boolean IS_OS_WINDOWS_6_OR_LATER = IS_OS_WINDOWS_VISTA || IS_OS_WINDOWS_7 || 
                                                          IS_OS_WINDOWS_8 || IS_OS_WINDOWS_10;
    
    // Display Resolution Detection
    public static final boolean IS_LOW_RESOLUTION = isLowResolution();
    
    // Look and Feel Related
    public static final boolean IS_LAF_WINDOWS_XP_ENABLED = isWindowsXPStyleEnabled();
    
    // Utility constants
    public static final Object MUST_NOT_BE_NULL = new Object();
    public static final Object[] EMPTY_ARRAY = new Object[0];
    
    /**
     * Private constructor to prevent instantiation.
     */
    protected SystemUtils() {
        // Override default constructor; prevents instantiation.
    }
    
    /**
     * Checks if a string starts with the specified prefix.
     *
     * @param str    the string to check
     * @param prefix the prefix to look for
     * @return true if the string starts with the prefix
     */
    public static boolean startsWith(String str, String prefix) {
        return str != null && str.startsWith(prefix);
    }
    
    /**
     * Checks if a string starts with the specified prefix, ignoring case.
     *
     * @param str    the string to check
     * @param prefix the prefix to look for
     * @return true if the string starts with the prefix (case insensitive)
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        return str != null && str.toLowerCase().startsWith(prefix.toLowerCase());
    }
    
    /**
     * Tries to look up the System property for the given key.
     * In untrusted environments this may throw a SecurityException.
     * In this case we catch the exception and answer the default value.
     *
     * @param key          the name of the system property
     * @param defaultValue the default value if no property exists
     * @return the system property's String value, or the defaultValue
     *     if there's no such value, or a SecurityException has been caught
     */
    public static String getSystemProperty(String key, String defaultValue) {
        try {
            return System.getProperty(key, defaultValue);
        } catch (SecurityException e) {
            return defaultValue;
        }
    }
    
    /**
     * Tries to look up the System property for the given key.
     * In untrusted environments this may throw a SecurityException.
     * In this case we catch the exception and answer null.
     *
     * @param key the name of the system property
     * @return the system property's String value, or null if there's
     *     no such value, or a SecurityException has been caught
     */
    public static String getSystemProperty(String key) {
        try {
            return System.getProperty(key);
        } catch (SecurityException e) {
            return null;
        }
    }
    
    /**
     * Detects if the system has low resolution (typically used for older systems
     * or systems with lower DPI settings).
     *
     * @return true if the system is considered low resolution
     */
    private static boolean isLowResolution() {
        try {
            // Simple heuristic: assume low resolution for older Windows versions
            // and systems that are likely to have lower DPI
            return IS_OS_WINDOWS_95 || IS_OS_WINDOWS_98 || IS_OS_WINDOWS_ME || 
                   IS_OS_WINDOWS_2000 || IS_OS_WINDOWS_XP;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Checks if Windows XP style is enabled for the look and feel.
     * This is typically true for Windows XP and later versions.
     *
     * @return true if Windows XP style should be enabled
     */
    private static boolean isWindowsXPStyleEnabled() {
        try {
            // Enable XP style for Windows XP and later
            return IS_OS_WINDOWS_XP || IS_OS_WINDOWS_VISTA || IS_OS_WINDOWS_7 || 
                   IS_OS_WINDOWS_8 || IS_OS_WINDOWS_10;
        } catch (Exception e) {
            return false;
        }
    }
}