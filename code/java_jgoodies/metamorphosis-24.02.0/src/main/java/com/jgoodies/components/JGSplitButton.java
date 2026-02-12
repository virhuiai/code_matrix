package com.jgoodies.components;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.Listeners;
import com.jgoodies.common.swing.ScreenScaling;
import com.jgoodies.components.internal.TextFieldIcons;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EventObject;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSplitButton.class */
public class JGSplitButton extends JGButton {
    static final int POPUP_AREA_WIDTH = ScreenScaling.toPhysical(19);
    private final Icon popupIcon;
    private MouseListener mousePressedListener;
    private MouseListener mouseReleasedListener;
    private JPopupMenu menu;
    private boolean mousePressedInPopupArea;

    public JGSplitButton(Action action, JPopupMenu menu) {
        super(action);
        this.mousePressedInPopupArea = false;
        setMenu(menu);
        this.popupIcon = getPopupIcon();
        initEventHandling();
    }

    public JGSplitButton(Icon icon, JPopupMenu menu) {
        this(null, icon, menu);
    }

    public JGSplitButton(String text, JPopupMenu menu) {
        this(text, null, menu);
    }

    public JGSplitButton(String text, Icon icon, JPopupMenu menu) {
        super(text, icon);
        this.mousePressedInPopupArea = false;
        setMenu(menu);
        this.popupIcon = getPopupIcon();
        initEventHandling();
    }

    private void initEventHandling() {
        this.mousePressedListener = Listeners.mousePressed(this::onMousePressed);
        this.mouseReleasedListener = Listeners.mouseReleased(this::onMouseReleased);
        addKeyListener(Listeners.keyPressed(this::onKeyPressed));
        addMouseListener(this.mousePressedListener);
        addMouseListener(this.mouseReleasedListener);
    }

    public final JPopupMenu getMenu() {
        return this.menu;
    }

    public final void setMenu(JPopupMenu popupMenu) {
        this.menu = (JPopupMenu) Preconditions.checkNotNull(popupMenu, Messages.MUST_NOT_BE_NULL, "popup menu");
    }

    private void onMousePressed(MouseEvent evt) {
        this.mousePressedInPopupArea = evt.getX() > getPopupAreaX();
    }

    private void onMouseReleased(MouseEvent evt) {
        this.mousePressedInPopupArea = false;
    }

    private void onKeyPressed(KeyEvent evt) {
        if (evt.getKeyCode() == 40 && evt.isAltDown()) {
            evt.consume();
            showMenu(evt);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showMenu(EventObject e) {
        JGSplitButton b = (JGSplitButton) e.getSource();
        if (!b.isShowing()) {
            return;
        }
        b.getMenu().show(b, 0, b.getHeight() + 1);
    }

    public final void addActionListener(ActionListener delegate) {
        if (delegate == null) {
            return;
        }
        super.addActionListener(new SplitActionListener(delegate));
    }

    public final void removeActionListener(ActionListener delegate) {
        if (delegate == null) {
            return;
        }
        for (ActionListener listener : getActionListeners()) {
            SplitActionListener splitListener = (SplitActionListener) listener;
            if (splitListener.delegate == delegate) {
                super.removeActionListener(listener);
                return;
            }
        }
    }

    public void setMargin(Insets margin) {
        int marginRight = margin.right + getPopupAreaWidth();
        super.setMargin(new Insets(margin.top, margin.left, margin.bottom, marginRight));
    }

    protected final void paintComponent(Graphics g) {
        super.paintComponent(g);
        paintPopupIcon(g);
        if (isSeparatorPaintedAlways() || ((getModel().isPressed() && getModel().isArmed()) || getModel().isRollover())) {
            paintSeparator(g);
        }
    }

    private void paintPopupIcon(Graphics g) {
        Insets insets = getInsets();
        int x = getPopupAreaX() + ((getPopupAreaWidth() - this.popupIcon.getIconWidth()) / 2);
        int y = insets.top + 1 + ((((getHeight() - insets.top) - insets.bottom) - this.popupIcon.getIconHeight()) / 2);
        this.popupIcon.paintIcon(this, g, x, y);
    }

    private void paintSeparator(Graphics g) {
        Insets insets = getInsets();
        Insets margin = getMargin();
        int x = getPopupAreaX();
        int y = (insets.top - margin.top) + 1;
        int sHeight = (getHeight() - (((insets.top - margin.top) + insets.bottom) - margin.bottom)) - 2;
        if (isFocusOwner() && isFocusPainted()) {
            y++;
            sHeight -= 2;
        }
        g.setColor(UIManager.getColor("controlShadow"));
        g.fillRect(x, y, 1, sHeight);
    }

    protected int getPopupAreaWidth() {
        return POPUP_AREA_WIDTH;
    }

    private int getPopupAreaX() {
        Insets insets = getInsets();
        Insets margin = getMargin();
        return ((getWidth() - (insets.right - margin.right)) - getPopupAreaWidth()) + getPopupPlatformOffsetX();
    }

    protected int getPopupPlatformOffsetX() {
        return (SystemUtils.IS_OS_MAC && getIcon() == null) ? 11 : 0;
    }

    protected boolean isSeparatorPaintedAlways() {
        return true;
    }

    protected Icon getPopupIcon() {
        return TextFieldIcons.getPopupIcon();
    }

    public void updateUI() {
        removeMouseListener(this.mousePressedListener);
        removeMouseListener(this.mouseReleasedListener);
        super.updateUI();
        addMouseListener(this.mousePressedListener);
        addMouseListener(this.mouseReleasedListener);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/JGSplitButton$SplitActionListener.class */
    private static final class SplitActionListener implements ActionListener {
        private final JGSplitButton button;
        private final ActionListener delegate;

        private SplitActionListener(JGSplitButton button, ActionListener delegate) {
            this.button = (JGSplitButton) Preconditions.checkNotNull(button, Messages.MUST_NOT_BE_NULL, "button");
            this.delegate = (ActionListener) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "listener");
        }

        public void actionPerformed(ActionEvent evt) {
            if (this.button.mousePressedInPopupArea) {
                JGSplitButton.showMenu(evt);
            } else {
                this.delegate.actionPerformed(evt);
            }
        }
    }
}
