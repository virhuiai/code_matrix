package com.jgoodies.components.internal;

import java.awt.event.ActionEvent;
import java.util.function.Consumer;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/JGTextComponent2.class */
public interface JGTextComponent2 extends JGTextComponent {
    public static final String PROPERTY_ICON = "icon";
    public static final String PROPERTY_PRESSED_ICON = "pressedIcon";
    public static final String PROPERTY_ROLLOVER_ICON = "rolloverIcon";
    public static final String PROPERTY_ICON_ACTION = "iconAction";
    public static final String PROPERTY_LINK_ACTION = "linkAction";
    public static final String PROPERTY_BUTTON_PAINTED_ALWAYS = "buttonPaintedAlways";
    public static final String PROPERTY_ICON_VISIBLE_ALWAYS = "iconVisibleAlways";
    public static final String PROPERTY_ERROR_UNDERLINE_PAINTED = "errorUnderlinePainted";

    Icon getIcon();

    void setIcon(Icon icon);

    Icon getPressedIcon();

    void setPressedIcon(Icon icon);

    Icon getRolloverIcon();

    void setRolloverIcon(Icon icon);

    Action getIconAction();

    void setIconAction(Action action);

    void setIconAction(Consumer<ActionEvent> consumer);

    Action getLinkAction();

    void setLinkAction(Action action);

    void setLinkAction(Consumer<ActionEvent> consumer);

    boolean isButtonPaintedAlways();

    void setButtonPaintedAlways(boolean z);

    boolean isIconVisibleAlways();

    void setIconVisibleAlways(boolean z);

    JPopupMenu getMenu();

    void setMenu(JPopupMenu jPopupMenu);

    Icon getMenuIcon();

    void setMenuIcon(Icon icon);

    boolean isErrorUnderlinePainted();

    void setErrorUnderlinePainted(boolean z);
}
