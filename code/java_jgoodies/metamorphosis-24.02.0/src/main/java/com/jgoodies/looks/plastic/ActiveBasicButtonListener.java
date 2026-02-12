package com.jgoodies.looks.plastic;

import java.awt.event.MouseEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.plaf.basic.BasicButtonListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/looks/plastic/ActiveBasicButtonListener.class */
final class ActiveBasicButtonListener extends BasicButtonListener {
    private boolean mouseOver;

    /* JADX INFO: Access modifiers changed from: package-private */
    public ActiveBasicButtonListener(AbstractButton b) {
        super(b);
        this.mouseOver = false;
    }

    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        AbstractButton button = (AbstractButton) e.getSource();
        ButtonModel model = button.getModel();
        this.mouseOver = true;
        model.setRollover(true);
    }

    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        AbstractButton button = (AbstractButton) e.getSource();
        ButtonModel model = button.getModel();
        this.mouseOver = false;
        model.setRollover(false);
    }

    public void mouseReleased(MouseEvent e) {
        super.mouseReleased(e);
        AbstractButton button = (AbstractButton) e.getSource();
        button.getModel().setRollover(this.mouseOver);
    }
}
