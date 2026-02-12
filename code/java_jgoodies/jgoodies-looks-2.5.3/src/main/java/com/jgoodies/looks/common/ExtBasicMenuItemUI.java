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

package com.jgoodies.looks.common;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicMenuItemUI;
import java.awt.*;

/**
 * Extended BasicMenuItemUI that provides common functionality
 * for menu item UI implementations.
 *
 * @author Karsten Lentzsch
 */
public class ExtBasicMenuItemUI extends BasicMenuItemUI {
    
    /**
     * Creates a new instance of ExtBasicMenuItemUI.
     */
    public ExtBasicMenuItemUI() {
        super();
    }
    
    /**
     * Factory method to create a new UI delegate.
     *
     * @param c the component to create the UI for
     * @return the UI delegate
     */
    public static ComponentUI createUI(JComponent c) {
        return new ExtBasicMenuItemUI();
    }
    
    /**
     * Indicates whether the icon border should be enabled.
     *
     * @return true if icon border is enabled
     */
    protected boolean iconBorderEnabled() {
        return true;
    }
    
    /**
     * Paints the menu item.
     *
     * @param g the graphics context
     * @param c the component to paint
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        super.paint(g, c);
    }
    
    /**
     * Installs the UI defaults.
     *
     * @param menuItem the menu item
     */
    @Override
    protected void installDefaults() {
        super.installDefaults();
    }
    
    /**
     * Uninstalls the UI defaults.
     *
     * @param menuItem the menu item
     */
    @Override
    protected void uninstallDefaults() {
        super.uninstallDefaults();
    }
}