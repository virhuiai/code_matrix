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

package com.jgoodies.common.internal;

/**
 * Provides frequently used message formatting utilities.
 *
 * @author Karsten Lentzsch
 */
public class Messages {
    
    /**
     * Private constructor to prevent instantiation.
     */
    private Messages() {
        // Override default constructor; prevents instantiation.
    }
    
    /**
     * Formats a message with the given arguments.
     *
     * @param pattern the message pattern
     * @param args the arguments to insert into the pattern
     * @return the formatted message
     */
    public static String format(String pattern, Object... args) {
        if (pattern == null) {
            return null;
        }
        if (args == null || args.length == 0) {
            return pattern;
        }
        
        StringBuilder result = new StringBuilder(pattern.length() + 50);
        int index = 0;
        int patternIndex = 0;
        
        while (index < args.length) {
            int placeholderStart = pattern.indexOf("{" + index + "}", patternIndex);
            if (placeholderStart == -1) {
                break;
            }
            
            result.append(pattern.substring(patternIndex, placeholderStart));
            result.append(args[index]);
            patternIndex = placeholderStart + 3 + String.valueOf(index).length();
            index++;
        }
        
        result.append(pattern.substring(patternIndex));
        return result.toString();
    }
    
    /**
     * Gets a message for the given key from the resource bundle.
     *
     * @param key the message key
     * @return the message string
     */
    public static String getString(String key) {
        // In a real implementation, this would load from a resource bundle
        // For now, we'll just return the key as the message
        return key;
    }
}