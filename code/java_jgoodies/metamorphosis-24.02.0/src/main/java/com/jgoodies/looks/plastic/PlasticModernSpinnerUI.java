package com.jgoodies.looks.plastic;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicArrowButton;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/PlasticModernSpinnerUI.class */
public class PlasticModernSpinnerUI extends PlasticSpinnerUI {
    public static ComponentUI createUI(JComponent b) {
        return new PlasticModernSpinnerUI();
    }

    @Override // com.jgoodies.looks.plastic.PlasticSpinnerUI
    protected JButton createArrowButton0(int direction) {
        return new BasicArrowButton(direction);
    }
}
