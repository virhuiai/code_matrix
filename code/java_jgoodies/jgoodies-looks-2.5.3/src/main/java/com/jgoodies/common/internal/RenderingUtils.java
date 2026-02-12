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

import javax.swing.*;
import java.awt.*;

/**
 * Provides frequently used rendering utilities.
 *
 * @author Karsten Lentzsch
 */
public class RenderingUtils {
    
    /**
     * Private constructor to prevent instantiation.
     */
    private RenderingUtils() {
        // Override default constructor; prevents instantiation.
    }
    
    /**
     * Sets the foreground and background color for the given component.
     *
     * @param c the component to configure
     * @param foreground the foreground color
     * @param background the background color
     */
    public static void setColors(JComponent c, java.awt.Color foreground, java.awt.Color background) {
        if (foreground != null) {
            c.setForeground(foreground);
        }
        if (background != null) {
            c.setBackground(background);
        }
    }
    
    /**
     * Sets the font for the given component.
     *
     * @param c the component to configure
     * @param font the font to set
     */
    public static void setFont(JComponent c, java.awt.Font font) {
        if (font != null) {
            c.setFont(font);
        }
    }
    
    /**
     * Sets the enabled state for the given component.
     *
     * @param c the component to configure
     * @param enabled the enabled state
     */
    public static void setEnabled(JComponent c, boolean enabled) {
        c.setEnabled(enabled);
    }
    
    /**
     * Sets the opaque state for the given component.
     *
     * @param c the component to configure
     * @param opaque the opaque state
     */
    public static void setOpaque(JComponent c, boolean opaque) {
        c.setOpaque(opaque);
    }
    
    /**
     * Draws a string with an underline at the specified character position.
     *
     * @param c the component
     * @param g the graphics context
     * @param text the text to draw
     * @param underlinedIndex the index of the character to underline
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public static void drawStringUnderlineCharAt(JComponent c, Graphics g, String text, 
            int underlinedIndex, int x, int y) {
        if (text == null || text.length() == 0) {
            return;
        }
        
        g.drawString(text, x, y);
        
        if (underlinedIndex >= 0 && underlinedIndex < text.length()) {
            FontMetrics fm = g.getFontMetrics();
            int underlineX = x + fm.stringWidth(text.substring(0, underlinedIndex));
            int underlineY = y;
            int charWidth = fm.charWidth(text.charAt(underlinedIndex));
            
            g.drawLine(underlineX, underlineY + 1, underlineX + charWidth, underlineY + 1);
        }
    }
    
    /**
     * Draws a string with an underline at the specified character position.
     *
     * @param menuItem the menu item
     * @param g the graphics context
     * @param text the text to draw
     * @param underlinedIndex the index of the character to underline
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public static void drawStringUnderlineCharAt(javax.swing.JMenuItem menuItem, Graphics g, String text, 
            int underlinedIndex, int x, int y) {
        drawStringUnderlineCharAt((JComponent) menuItem, g, text, underlinedIndex, x, y);
    }
}