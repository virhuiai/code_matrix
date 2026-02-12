package com.jgoodies.components;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.Listeners;
import com.jgoodies.components.internal.TextFieldIcons;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.EventObject;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGMenuButton.class */
public class JGMenuButton extends JGButton {
    private final Icon popupIcon;
    private JPopupMenu menu;

    public JGMenuButton(Action action, JPopupMenu menu) {
        super(action);
        setMenu(menu);
        this.popupIcon = getPopupIcon();
        initEventHandling();
    }

    public JGMenuButton(Icon icon, JPopupMenu menu) {
        this(null, icon, menu);
    }

    public JGMenuButton(String text, JPopupMenu menu) {
        this(text, null, menu);
    }

    public JGMenuButton(String text, Icon icon, JPopupMenu menu) {
        super(text, icon);
        setMenu(menu);
        this.popupIcon = getPopupIcon();
        initEventHandling();
    }

    private void initEventHandling() {
        addActionListener(this::onButtonPerformed);
        addKeyListener(Listeners.keyPressed(this::onKeyPressed, "alt"));
    }

    public final JPopupMenu getMenu() {
        return this.menu;
    }

    public final void setMenu(JPopupMenu popupMenu) {
        this.menu = (JPopupMenu) Preconditions.checkNotNull(popupMenu, Messages.MUST_NOT_BE_NULL, "popup menu");
    }

    private void onButtonPerformed(ActionEvent evt) {
        showMenu(evt);
    }

    private void onKeyPressed(KeyEvent evt) {
        if (!evt.isConsumed() && evt.getKeyCode() == 40) {
            evt.consume();
            showMenu(evt);
        }
    }

    private static void showMenu(EventObject evt) {
        JGMenuButton b = (JGMenuButton) evt.getSource();
        if (!b.isShowing()) {
            return;
        }
        b.getMenu().show(b, 0, b.getHeight() + 1);
    }

    public void setMargin(Insets margin) {
        super.setMargin(new Insets(margin.top, margin.left, margin.bottom, margin.right + getPopupAreaWidth()));
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintPopupIcon(g);
    }

    private void paintPopupIcon(Graphics g) {
        Insets insets = getInsets();
        int x = getPopupAreaX() + ((getPopupAreaWidth() - this.popupIcon.getIconWidth()) / 2);
        int y = insets.top + 1 + ((((getHeight() - insets.top) - insets.bottom) - this.popupIcon.getIconHeight()) / 2);
        this.popupIcon.paintIcon(this, g, x, y);
    }

    protected int getPopupAreaWidth() {
        return JGSplitButton.POPUP_AREA_WIDTH;
    }

    private int getPopupAreaX() {
        Insets insets = getInsets();
        Insets margin = getMargin();
        return ((getWidth() - (insets.right - margin.right)) - getPopupAreaWidth()) + getPopupPlatformOffsetX();
    }

    protected int getPopupPlatformOffsetX() {
        return (SystemUtils.IS_OS_MAC && getIcon() == null) ? 11 : 0;
    }

    protected Icon getPopupIcon() {
        return TextFieldIcons.getPopupIcon();
    }
}
