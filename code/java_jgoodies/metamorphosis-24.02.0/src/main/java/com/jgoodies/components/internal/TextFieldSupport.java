package com.jgoodies.components.internal;

import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.base.SystemUtils;
import com.jgoodies.common.jsdl.action.ConsumerAction;
import com.jgoodies.components.util.ComponentUtils;
import com.jgoodies.components.util.CompoundIcon;
import com.jgoodies.components.util.NullIcon;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.function.Consumer;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.ToolTipManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.UIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/TextFieldSupport.class */
public final class TextFieldSupport {
    public static final String PROPERTY_ICON = "icon";
    public static final String PROPERTY_ROLLOVER_ICON = "rolloverIcon";
    public static final String PROPERTY_PRESSED_ICON = "pressedIcon";
    public static final String PROPERTY_BUTTON_PAINTED_ALWAYS = "buttonPaintedAlways";
    public static final String PROPERTY_ICON_VISIBLE_ALWAYS = "iconVisibleAlways";
    public static final String PROPERTY_MENU_ICON = "menuIcon";
    public static final String PROPERTY_ICON_ACTION = "iconAction";
    public static final String PROPERTY_LINK_ACTION = "linkAction";
    public static final String PROPERTY_MENU = "menu";
    public static final String PROPERTY_SELECT_ON_FOCUS_GAIN_ENABLED = "JGoodies.selectAllOnFocusGain";
    private static final String KEY_EVENT_HANDLER = "TextFieldSupport.eventHandler";
    private static final String KEY_ROLLOVER_STATE_TEXT_BOX = "TextFieldSupport.rolloverStateTextBox";
    private static final String KEY_ROLLOVER_STATE_TEXT = "TextFieldSupport.rolloverStateText";
    private static final String KEY_ROLLOVER_STATE_ICON = "TextFieldSupport.rolloverStateIcon";
    private static final String KEY_ROLLOVER_STATE_MENU = "TextFieldSupport.rolloverStateMenu";
    private static final String KEY_PRESSED = "TextFieldSupport.pressed";
    private static final String KEY_CONTROL_DOWN = "TextFieldSupport.controlDown";
    private static Action showPopupAction;
    private static final Border ICON_BORDER = new IconBorder();
    private static final EventHandler EVENT_HANDLER = new EventHandler();

    private TextFieldSupport() {
    }

    public static void updateUI(JTextComponent c) {
        boolean installed = c.getClientProperty(KEY_EVENT_HANDLER) != null;
        if (!installed) {
            c.addFocusListener(EVENT_HANDLER);
            c.addMouseListener(EVENT_HANDLER);
            c.addMouseMotionListener(EVENT_HANDLER);
            c.addKeyListener(EVENT_HANDLER);
            c.putClientProperty(KEY_EVENT_HANDLER, EVENT_HANDLER);
            updateBorder(c);
        } else if (c.getBorder() instanceof UIResource) {
            updateBorder(c);
        }
        TextFieldIcons.ensureValidImages();
    }

    private static void updateBorder(JTextComponent c) {
        Border border = c.getBorder();
        Border iconBorder = ICON_BORDER;
        c.setBorder(border instanceof UIResource ? new BorderUIResource.CompoundBorderUIResource(border, iconBorder) : new CompoundBorder(border, iconBorder));
    }

    public static Icon getIcon(JTextComponent c) {
        return (Icon) c.getClientProperty("icon");
    }

    public static void setIcon(JTextComponent c, Icon newIcon) {
        Icon oldIcon = getIcon(c);
        if (oldIcon == newIcon) {
            return;
        }
        c.putClientProperty("icon", newIcon);
        c.revalidate();
        c.repaint();
    }

    public static Icon getPressedIcon(JTextComponent c) {
        return (Icon) c.getClientProperty("pressedIcon");
    }

    public static void setPressedIcon(JTextComponent c, Icon newIcon) {
        Icon oldIcon = getPressedIcon(c);
        if (oldIcon == newIcon) {
            return;
        }
        c.putClientProperty("pressedIcon", newIcon);
        c.repaint();
    }

    public static Icon getRolloverIcon(JTextComponent c) {
        return (Icon) c.getClientProperty("rolloverIcon");
    }

    public static void setRolloverIcon(JTextComponent c, Icon newIcon) {
        Icon oldIcon = getRolloverIcon(c);
        if (oldIcon == newIcon) {
            return;
        }
        c.putClientProperty("rolloverIcon", newIcon);
        c.repaint();
    }

    public static boolean isButtonPaintedAlways(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty("buttonPaintedAlways"));
    }

    public static void setButtonPaintedAlways(JTextComponent c, boolean newValue) {
        boolean oldValue = isButtonPaintedAlways(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty("buttonPaintedAlways", Boolean.valueOf(newValue));
        c.repaint();
    }

    public static boolean isIconVisibleAlways(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty("iconVisibleAlways"));
    }

    public static void setIconVisibleAlways(JTextComponent c, boolean newValue) {
        boolean oldValue = isIconVisibleAlways(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty("iconVisibleAlways", Boolean.valueOf(newValue));
        c.repaint();
    }

    public static Action getIconAction(JTextComponent c) {
        return (Action) c.getClientProperty("iconAction");
    }

    public static void setIconAction(JTextComponent c, Action newAction) {
        Action oldAction = getIconAction(c);
        if (oldAction == newAction) {
            return;
        }
        if (oldAction != null) {
            ComponentUtils.unregisterKeyboardAction(c, oldAction);
        }
        if (newAction != null) {
            ComponentUtils.registerKeyboardAction(c, newAction);
            ToolTipManager.sharedInstance().registerComponent(c);
        }
        c.putClientProperty("iconAction", newAction);
        c.revalidate();
        c.repaint();
    }

    public static void setIconAction(JTextComponent c, Consumer<ActionEvent> consumer) {
        setIconAction(c, (Action) new ConsumerAction("unused", consumer));
    }

    public static Action getLinkAction(JTextComponent c) {
        return (Action) c.getClientProperty("linkAction");
    }

    public static void setLinkAction(JTextComponent c, Action newAction) {
        Action oldAction = getLinkAction(c);
        if (oldAction == newAction) {
            return;
        }
        if (oldAction != null) {
            ComponentUtils.unregisterKeyboardAction(c, oldAction);
        }
        if (newAction != null) {
            ComponentUtils.registerKeyboardAction(c, newAction);
        }
        c.putClientProperty("linkAction", newAction);
        c.repaint();
    }

    public static void setLinkAction(JTextComponent c, Consumer<ActionEvent> consumer) {
        setLinkAction(c, (Action) new ConsumerAction("unused", consumer));
    }

    public static Icon getMenuIcon(JTextComponent c) {
        return (Icon) c.getClientProperty(PROPERTY_MENU_ICON);
    }

    public static void setMenuIcon(JTextComponent c, Icon newIcon) {
        Icon oldIcon = getIcon(c);
        if (oldIcon == newIcon) {
            return;
        }
        c.putClientProperty(PROPERTY_MENU_ICON, newIcon);
        c.revalidate();
        c.repaint();
    }

    public static JPopupMenu getMenu(JTextComponent c) {
        return (JPopupMenu) c.getClientProperty(PROPERTY_MENU);
    }

    public static void setMenu(JTextComponent c, JPopupMenu newMenu) {
        JPopupMenu oldMenu = getMenu(c);
        if (oldMenu == newMenu) {
            return;
        }
        if (oldMenu != null) {
            oldMenu.removePropertyChangeListener(ComponentModel.PROPERTY_VISIBLE, EVENT_HANDLER);
            ComponentUtils.unregisterKeyboardAction(c, getShowPopupAction());
        }
        if (newMenu != null) {
            newMenu.addPropertyChangeListener(ComponentModel.PROPERTY_VISIBLE, EVENT_HANDLER);
            ComponentUtils.registerKeyboardAction(c, getShowPopupAction());
        }
        c.putClientProperty(PROPERTY_MENU, newMenu);
        c.revalidate();
        c.repaint();
    }

    public static Boolean getSelectOnFocusGainEnabled(JTextComponent c) {
        return (Boolean) c.getClientProperty("JGoodies.selectAllOnFocusGain");
    }

    public static void setSelectOnFocusGainEnabled(JTextComponent component, Boolean b) {
        component.putClientProperty("JGoodies.selectAllOnFocusGain", b);
    }

    public static String getToolTipText(JTextComponent c, MouseEvent e, String superToolTipText) {
        Action iconAction = getIconAction(c);
        if (iconAction == null || !isOverIcon(c, e.getX(), e.getY())) {
            return superToolTipText;
        }
        String toolTip = (String) iconAction.getValue("ShortDescription");
        if (Strings.isNotBlank(toolTip)) {
            return toolTip;
        }
        return (String) iconAction.getValue("Name");
    }

    public static Dimension getPreferredSize(JTextComponent c, Dimension original) {
        Icon menuIcon;
        Icon actionIcon = getActionBaseIcon(c);
        if (actionIcon != null && !isIconPainted(c)) {
            original.width += actionIcon.getIconWidth();
        }
        if (hasMenu(c) && !isMenuIconPainted(c) && (menuIcon = getCurrentMenuIcon(c, -1)) != null) {
            original.width += menuIcon.getIconWidth();
        }
        return original;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Icon getActionBaseIcon(JTextComponent c) {
        Icon icon = getIcon(c);
        if (icon != null) {
            return icon;
        }
        Action a = getIconAction(c);
        if (a == null) {
            return null;
        }
        return (Icon) a.getValue("SmallIcon");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Icon getCurrentActionIcon(JTextComponent c, int height) {
        Icon baseIcon = getActionBaseIcon(c);
        if (baseIcon == null) {
            return null;
        }
        boolean clickable = isIconClickable(c);
        boolean rollover = isRolloverStateIcon(c);
        boolean pressed = isPressed(c) && rollover && clickable;
        boolean active = isButtonPaintedAlways(c) && clickable;
        int width = baseIcon.getIconWidth();
        if (pressed) {
            Icon icon = getPressedIcon(c);
            return icon != null ? icon : new CompoundIcon(TextFieldIcons.getButtonIconPressed(width, height), baseIcon, CompoundIcon.Anchor.CENTER);
        }
        if (rollover && clickable) {
            Icon icon2 = getRolloverIcon(c);
            return icon2 != null ? icon2 : new CompoundIcon(TextFieldIcons.getButtonIconRollover(width, height), baseIcon, CompoundIcon.Anchor.CENTER);
        }
        if (active) {
            return new CompoundIcon(TextFieldIcons.getButtonIcon(width, height), baseIcon, CompoundIcon.Anchor.CENTER);
        }
        return new CompoundIcon(new NullIcon(width, height), baseIcon, CompoundIcon.Anchor.CENTER);
    }

    private static Icon getMenuBaseIcon(JTextComponent c) {
        Icon customIcon = getMenuIcon(c);
        return customIcon != null ? customIcon : TextFieldIcons.getPopupIcon();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static Icon getCurrentMenuIcon(JTextComponent c, int height) {
        Icon baseIcon = getMenuBaseIcon(c);
        if (baseIcon == null) {
            return null;
        }
        int width = baseIcon.getIconWidth();
        Icon background = getMenuBackgroundIcon(c, width, height);
        return new CompoundIcon(background, baseIcon, CompoundIcon.Anchor.CENTER);
    }

    private static Icon getMenuBackgroundIcon(JTextComponent c, int width, int height) {
        boolean popupVisible = isPopupMenuVisible(c);
        boolean rollover = isRolloverStateMenu(c) || popupVisible;
        boolean pressed = (isPressed(c) && rollover) || popupVisible;
        if (pressed) {
            return TextFieldIcons.getButtonIconPressed(width, height);
        }
        if (rollover) {
            return TextFieldIcons.getButtonIconRollover(width, height);
        }
        if (isButtonPaintedAlways(c) || (isRolloverStateIcon(c) && isIconClickable(c))) {
            return TextFieldIcons.getButtonIcon(width, height);
        }
        return new NullIcon(width, height);
    }

    private static boolean isRolloverStateTextBox(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(KEY_ROLLOVER_STATE_TEXT_BOX));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setRolloverStateTextBox(JTextComponent c, boolean newValue) {
        boolean oldValue = isRolloverStateTextBox(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(KEY_ROLLOVER_STATE_TEXT_BOX, Boolean.valueOf(newValue));
        if (newValue && !LinkUnderlineSupport.isLinkPainted(c)) {
            changeCursor(c, 2);
        }
        c.repaint();
    }

    private static boolean isRolloverStateText(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(KEY_ROLLOVER_STATE_TEXT));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setRolloverStateText(JTextComponent c, boolean newValue) {
        boolean oldValue = isRolloverStateText(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(KEY_ROLLOVER_STATE_TEXT, Boolean.valueOf(newValue));
        updateLink(c);
    }

    private static boolean isRolloverStateIcon(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(KEY_ROLLOVER_STATE_ICON));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setRolloverStateIcon(JTextComponent c, boolean newValue) {
        boolean oldValue = isRolloverStateIcon(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(KEY_ROLLOVER_STATE_ICON, Boolean.valueOf(newValue));
        if (newValue) {
            changeCursor(c, 0);
        }
        c.repaint();
    }

    private static boolean isRolloverStateMenu(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(KEY_ROLLOVER_STATE_MENU));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setRolloverStateMenu(JTextComponent c, boolean newValue) {
        boolean oldValue = isRolloverStateMenu(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(KEY_ROLLOVER_STATE_MENU, Boolean.valueOf(newValue));
        if (newValue) {
            changeCursor(c, 0);
        }
        c.repaint();
    }

    private static boolean isPressed(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(KEY_PRESSED));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setPressed(JTextComponent c, boolean newValue) {
        boolean oldValue = isPressed(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(KEY_PRESSED, Boolean.valueOf(newValue));
        c.repaint();
    }

    private static boolean isIconClickable(JTextComponent c) {
        Action iconAction = getIconAction(c);
        return iconAction != null && iconAction.isEnabled();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isIconPainted(JTextComponent c) {
        if (isIconVisibleAlways(c)) {
            return true;
        }
        if (!c.isEnabled() || !c.isEditable()) {
            return false;
        }
        if (c.isFocusOwner() || isPopupMenuVisible(c)) {
            return true;
        }
        Action action = getIconAction(c);
        return action != null && action.isEnabled() && (isRolloverStateTextBox(c) || isRolloverStateIcon(c) || isRolloverStateMenu(c));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isMenuIconPainted(JTextComponent c) {
        return hasMenu(c) && (isIconPainted(c) || isRolloverStateTextBox(c) || isRolloverStateIcon(c) || isRolloverStateMenu(c));
    }

    private static boolean isPopupMenuVisible(JTextComponent c) {
        JPopupMenu menu = getMenu(c);
        return menu != null && menu.isVisible();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean hasMenu(JTextComponent c) {
        return getMenu(c) != null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isOverIcon(JTextComponent c, int x, int y) {
        return isIconPainted(c) && getIconBounds(c).contains(x, y);
    }

    private static Rectangle getIconBounds(JTextComponent c) {
        Icon actionIcon = getCurrentActionIcon(c, -1);
        if (actionIcon == null) {
            return new Rectangle();
        }
        boolean isLTR = c.getComponentOrientation().isLeftToRight();
        boolean hasMenu = hasMenu(c);
        Icon menuIcon = getCurrentMenuIcon(c, -1);
        int iconShift = hasMenu ? menuIcon.getIconWidth() : 0;
        Insets margin = c.getMargin();
        Insets insets = c.getInsets();
        int marginLeft = margin != null ? margin.left : 0;
        int marginRight = margin != null ? margin.right : 0;
        int left = insets.left - marginLeft;
        int right = insets.right - marginRight;
        int x = isLTR ? c.getWidth() - right : iconShift;
        int width = (isLTR ? right : left) - iconShift;
        int height = c.getHeight();
        Rectangle r = new Rectangle(x, 0, width, height);
        return r;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isOverMenu(JTextComponent c, int x, int y) {
        return hasMenu(c) && getMenuBounds(c).contains(x, y);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isOverText(JTextComponent c, InputEvent e) {
        Point p;
        if (e instanceof MouseEvent) {
            p = ((MouseEvent) e).getPoint();
        } else {
            PointerInfo info = MouseInfo.getPointerInfo();
            if (info == null) {
                return false;
            }
            p = info.getLocation();
            SwingUtilities.convertPointFromScreen(p, c);
        }
        return isOverText(c, p.x, p.y);
    }

    private static boolean isOverText(JTextComponent c, int x, int y) {
        int offs1 = c.getText().length();
        Insets insets = c.getInsets();
        try {
            Rectangle modelToView = c.getUI().getRootView(c).modelToView(0, Position.Bias.Forward, offs1, Position.Bias.Backward, new Rectangle());
            Rectangle b = modelToView instanceof Rectangle ? modelToView : modelToView.getBounds();
            Rectangle textBounds = new Rectangle(insets.left, insets.top, b.width, b.height);
            return textBounds.contains(x, y);
        } catch (BadLocationException e) {
            return false;
        }
    }

    private static Rectangle getMenuBounds(JTextComponent c) {
        if (!hasMenu(c)) {
            return new Rectangle();
        }
        boolean isLTR = c.getComponentOrientation().isLeftToRight();
        Insets margin = c.getMargin();
        Insets insets = c.getInsets();
        int marginLeft = margin != null ? margin.left : 0;
        int marginRight = margin != null ? margin.right : 0;
        int left = insets.left - marginLeft;
        int right = insets.right - marginRight;
        int actionIconWidth = (!isIconPainted(c) || getActionBaseIcon(c) == null) ? 0 : getCurrentActionIcon(c, -1).getIconWidth();
        int x = isLTR ? (c.getWidth() - right) + actionIconWidth : 0;
        int width = (isLTR ? right : left) - actionIconWidth;
        int height = c.getHeight();
        Rectangle r = new Rectangle(x, 0, width, height);
        return r;
    }

    private static void changeCursor(JTextComponent c, int type) {
        c.setCursor(Cursor.getPredefinedCursor(type));
    }

    private static boolean isControlDown(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(KEY_CONTROL_DOWN));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setControlDown(JTextComponent c, boolean newValue) {
        boolean oldValue = isControlDown(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty(KEY_CONTROL_DOWN, Boolean.valueOf(newValue));
        updateLink(c);
    }

    private static void updateLink(JTextComponent c) {
        Action linkAction = getLinkAction(c);
        boolean linkPainted = isControlDown(c) && isRolloverStateText(c) && linkAction != null && linkAction.isEnabled();
        LinkUnderlineSupport.setLinkPainted(c, linkPainted);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isControlDown(InputEvent e) {
        return SystemUtils.IS_OS_MAC ? (e.getModifiersEx() & 256) != 0 : (e.getModifiersEx() & 128) != 0;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/TextFieldSupport$IconBorder.class */
    private static final class IconBorder extends AbstractBorder {
        private static final long serialVersionUID = 7449629122643189364L;

        private IconBorder() {
        }

        public Insets getBorderInsets(Component c) {
            return getBorderInsets(c, new Insets(0, 0, 0, 0));
        }

        public Insets getBorderInsets(Component c, Insets insets) {
            insets.top = 0;
            insets.right = 0;
            insets.left = 0;
            insets.bottom = 0;
            JTextComponent field = (JTextComponent) c;
            boolean isLTR = isLeftToRight(field);
            Icon actionIcon = TextFieldSupport.getActionBaseIcon(field);
            if (actionIcon != null && TextFieldSupport.isIconPainted(field)) {
                int iconWidth = actionIcon.getIconWidth();
                Insets margin = field.getMargin();
                if (isLTR) {
                    insets.right = iconWidth;
                } else {
                    insets.left = iconWidth + (margin != null ? margin.left : 0);
                }
            }
            if (TextFieldSupport.isMenuIconPainted(field)) {
                Icon menuIcon = TextFieldSupport.getCurrentMenuIcon(field, -1);
                if (isLTR) {
                    insets.right += menuIcon.getIconWidth();
                } else {
                    insets.left += menuIcon.getIconWidth();
                }
            }
            return insets;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Icon menuIcon;
            int iconShift;
            JTextComponent field = (JTextComponent) c;
            Insets margin = field.getMargin();
            int marginTop = margin != null ? margin.top : 0;
            int marginBottom = margin != null ? margin.bottom : 0;
            int marginRight = margin != null ? margin.right : 0;
            int iconHeight = h + marginTop + marginBottom;
            boolean isLTR = isLeftToRight(field);
            boolean hasMenu = TextFieldSupport.hasMenu(field);
            Icon actionIcon = TextFieldSupport.getCurrentActionIcon(field, iconHeight);
            if (hasMenu) {
                menuIcon = TextFieldSupport.getCurrentMenuIcon(field, iconHeight);
                iconShift = menuIcon.getIconWidth();
            } else {
                menuIcon = null;
                iconShift = 0;
            }
            if (actionIcon != null && TextFieldSupport.isIconPainted(field)) {
                int iconX = x + (isLTR ? (w - actionIcon.getIconWidth()) - iconShift : iconShift) + marginRight;
                int iconY = y + ((h - actionIcon.getIconHeight()) / 2);
                actionIcon.paintIcon(c, g, iconX, iconY);
            }
            if (TextFieldSupport.isMenuIconPainted(field)) {
                int menuX = ((x + w) - menuIcon.getIconWidth()) + marginRight;
                int menuY = y - marginTop;
                menuIcon.paintIcon(c, g, menuX, menuY);
            }
        }

        private static boolean isLeftToRight(JTextComponent field) {
            return field.getComponentOrientation().isLeftToRight();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void showPopupMenu(JTextComponent c) {
        int x;
        JPopupMenu popupMenu = getMenu(c);
        ComponentOrientation co = c.getComponentOrientation();
        if (co != popupMenu.getComponentOrientation()) {
            popupMenu.applyComponentOrientation(co);
        }
        popupMenu.pack();
        if (co.isLeftToRight()) {
            x = c.getWidth() - popupMenu.getPreferredSize().width;
        } else {
            x = 0;
        }
        popupMenu.show(c, x, c.getHeight() - 1);
    }

    private static Action getShowPopupAction() {
        if (showPopupAction == null) {
            showPopupAction = new ShowPopupAction();
        }
        return showPopupAction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/TextFieldSupport$ShowPopupAction.class */
    public static final class ShowPopupAction extends AbstractAction {
        private ShowPopupAction() {
            super((String) null);
            putValue("AcceleratorKey", KeyStroke.getKeyStroke("alt DOWN"));
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent c = (JTextComponent) e.getSource();
            TextFieldSupport.showPopupMenu(c);
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/TextFieldSupport$EventHandler.class */
    private static final class EventHandler implements FocusListener, KeyListener, MouseListener, MouseMotionListener, PropertyChangeListener {
        private EventHandler() {
        }

        public void focusGained(FocusEvent e) {
            field(e).repaint();
        }

        public void focusLost(FocusEvent e) {
            field(e).repaint();
        }

        public void keyTyped(KeyEvent e) {
            JTextComponent c = field(e);
            TextFieldSupport.setRolloverStateText(c, TextFieldSupport.isOverText(c, e));
        }

        public void keyPressed(KeyEvent e) {
            TextFieldSupport.setControlDown(field(e), TextFieldSupport.isControlDown((InputEvent) e));
        }

        public void keyReleased(KeyEvent e) {
            TextFieldSupport.setControlDown(field(e), TextFieldSupport.isControlDown((InputEvent) e));
        }

        public void mousePressed(MouseEvent e) {
            JTextComponent field = field(e);
            TextFieldSupport.setControlDown(field, TextFieldSupport.isControlDown((InputEvent) e));
            if (SwingUtilities.isLeftMouseButton(e)) {
                TextFieldSupport.setPressed(field, true);
                if (TextFieldSupport.isOverMenu(field, e.getX(), e.getY())) {
                    TextFieldSupport.showPopupMenu(field);
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            JTextComponent field = field(e);
            TextFieldSupport.setControlDown(field, TextFieldSupport.isControlDown((InputEvent) e));
            if (SwingUtilities.isLeftMouseButton(e)) {
                TextFieldSupport.setPressed(field, false);
            }
        }

        public void mouseClicked(MouseEvent e) {
            JTextComponent field = field(e);
            TextFieldSupport.setControlDown(field, TextFieldSupport.isControlDown((InputEvent) e));
            if (!SwingUtilities.isLeftMouseButton(e)) {
                return;
            }
            Action iconAction = TextFieldSupport.getIconAction(field);
            if (iconAction != null && iconAction.isEnabled() && TextFieldSupport.isOverIcon(field, e.getX(), e.getY())) {
                performAction(field, iconAction);
                return;
            }
            Action linkAction = TextFieldSupport.getLinkAction(field);
            if (linkAction != null && linkAction.isEnabled() && LinkUnderlineSupport.isLinkPainted(field)) {
                performAction(field, linkAction);
            }
        }

        public void mouseEntered(MouseEvent e) {
            setRolloverState(e);
            TextFieldSupport.setControlDown(field(e), TextFieldSupport.isControlDown((InputEvent) e));
        }

        public void mouseExited(MouseEvent e) {
            JTextComponent field = field(e);
            TextFieldSupport.setRolloverStateTextBox(field, false);
            TextFieldSupport.setRolloverStateText(field, false);
            TextFieldSupport.setRolloverStateIcon(field, false);
            TextFieldSupport.setRolloverStateMenu(field, false);
            TextFieldSupport.setControlDown(field(e), false);
        }

        public void mouseMoved(MouseEvent e) {
            setRolloverState(e);
            TextFieldSupport.setControlDown(field(e), TextFieldSupport.isControlDown((InputEvent) e));
        }

        public void mouseDragged(MouseEvent e) {
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent e) {
            JPopupMenu menu = (JPopupMenu) e.getSource();
            if (!menu.isVisible() && menu.getInvoker() != null) {
                menu.getInvoker().repaint();
            }
        }

        private static void setRolloverState(MouseEvent e) {
            boolean overTextBox;
            boolean overText;
            boolean overIcon;
            boolean overMenu;
            JTextComponent c = field(e);
            if (!TextFieldSupport.isOverIcon(c, e.getX(), e.getY())) {
                if (TextFieldSupport.isOverMenu(c, e.getX(), e.getY())) {
                    overTextBox = false;
                    overText = false;
                    overIcon = false;
                    overMenu = true;
                } else {
                    overTextBox = true;
                    overText = TextFieldSupport.isOverText(c, e);
                    overIcon = false;
                    overMenu = false;
                }
            } else {
                overTextBox = false;
                overText = false;
                overIcon = true;
                overMenu = false;
            }
            TextFieldSupport.setRolloverStateTextBox(c, overTextBox);
            TextFieldSupport.setRolloverStateText(c, overText);
            TextFieldSupport.setRolloverStateIcon(c, overIcon);
            TextFieldSupport.setRolloverStateMenu(c, overMenu);
        }

        private static void performAction(JTextComponent c, Action action) {
            action.actionPerformed(new ActionEvent(c, 1001, (String) action.getValue("ActionCommandKey")));
        }

        private static JTextComponent field(ComponentEvent e) {
            return e.getComponent();
        }
    }
}
