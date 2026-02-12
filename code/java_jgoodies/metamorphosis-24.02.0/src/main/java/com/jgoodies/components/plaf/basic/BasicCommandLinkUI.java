package com.jgoodies.components.plaf.basic;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.components.JGCommandLink;
import com.jgoodies.components.internal.ComponentSupport;
import com.jgoodies.components.internal.MnemonicUnderlineSupport;
import com.jgoodies.components.internal.StaticTextArea;
import com.jgoodies.layout.factories.CC;
import com.jgoodies.layout.factories.Paddings;
import com.jgoodies.layout.layout.FormLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.UIResource;
import javax.swing.plaf.basic.BasicButtonListener;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/basic/BasicCommandLinkUI.class */
public class BasicCommandLinkUI extends BasicButtonUI {
    private static Border commandLinkBorder;
    protected JGCommandLink commandLink;
    protected JLabel iconLabel;
    protected JTextComponent textArea;
    protected JTextComponent descriptionArea;
    private Boolean cachedActiveState;
    private Boolean cachedDefaultState;

    public static ComponentUI createUI(JComponent x) {
        return new BasicCommandLinkUI();
    }

    public void installUI(JComponent component) {
        this.commandLink = (JGCommandLink) component;
        super.installUI(component);
        installComponents();
    }

    public void uninstallUI(JComponent c) {
        super.uninstallUI(c);
        uninstallComponents();
        this.commandLink.setLayout(null);
        this.commandLink = null;
    }

    protected void installDefaults(AbstractButton b) {
        super.installDefaults(b);
        if (commandLinkBorder == null) {
            commandLinkBorder = new BorderUIResource.CompoundBorderUIResource(new CommandLinkBorder(), Paddings.createPadding("6dlu, 6dlu, 6dlu, 6dlu", new Object[0]));
        }
        b.setBorder(commandLinkBorder);
    }

    protected void installComponents() {
        this.iconLabel = new JLabel(this.commandLink.isIconVisible() ? getIcon(this.commandLink.isDefaultButton()) : null);
        this.textArea = createTextComponent(this.commandLink.getText());
        this.textArea.setFont(UIManager.getFont("CommandLink.textFont"));
        this.descriptionArea = null;
        if (this.commandLink.getDescription() != null) {
            this.descriptionArea = createTextComponent(this.commandLink.getDescription());
        }
        updateForegroundColors(false);
        updateIcon(this.commandLink.isDefaultButton());
        this.commandLink.setLayout(new FormLayout("pref, 4dlu, fill:[100dlu,default]:grow", "pref, 1dlu, pref:grow"));
        this.commandLink.add(this.iconLabel, CC.xywh(1, 1, 1, 3, "fill, top"));
        this.commandLink.add(this.textArea, CC.xy(3, 1));
        if (this.descriptionArea != null) {
            this.commandLink.add(this.descriptionArea, CC.xy(3, 3, "fill, top"));
        }
    }

    protected void uninstallComponents() {
        this.commandLink.removeAll();
        this.iconLabel = null;
        this.textArea = null;
        this.descriptionArea = null;
        this.cachedActiveState = null;
        this.cachedDefaultState = null;
    }

    protected BasicButtonListener createButtonListener(AbstractButton b) {
        return new CommandLinkListener((JGCommandLink) b);
    }

    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        boolean isActive = model.isRollover() || model.isPressed();
        boolean isDefault = this.commandLink.isDefaultButton();
        if (model.isArmed() && model.isPressed()) {
            paintButtonPressed(g, b);
        } else if (isActive) {
            paintRollover(g, b);
        }
        updateForegroundColors(isActive);
        updateIcon(isDefault);
        if (b.isFocusPainted() && b.hasFocus()) {
            paintFocus(g, b);
        }
        updateMnemonicIndex();
    }

    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        Color metroBackground = new Color(231, 231, 231);
        Color startColor = new Color(238, 238, 238);
        Color endColor = new Color(255, 255, 255);
        paintBackground(g, b, metroBackground, startColor, endColor);
    }

    private static void paintRollover(Graphics g, AbstractButton b) {
        Color metroBackground = new Color(242, 242, 242);
        Color startColor = new Color(255, 255, 255);
        Color endColor = new Color(241, 241, 241);
        paintBackground(g, b, metroBackground, startColor, endColor);
    }

    private static void paintBackground(Graphics g, AbstractButton b, Color metroBackground, Color startColor, Color endColor) {
        int x;
        int y;
        int w;
        int h;
        int width = b.getWidth();
        int height = b.getHeight();
        Insets insets = CommandLinkBorder.INSETS;
        Graphics2D g2 = (Graphics2D) g;
        Rectangle clip = g2.getClipBounds();
        if (clip != null) {
            int x2 = clip.x;
            int y2 = clip.y;
            w = clip.width;
            h = clip.height;
            x = Math.max(x2, insets.left);
            y = Math.max(y2, insets.top);
            int right = width - insets.right;
            if (x + w > right) {
                w = right - x;
            }
            int bottom = height - insets.bottom;
            if (y + h > bottom) {
                h = bottom - y;
            }
        } else {
            x = insets.left;
            y = insets.top;
            w = width - (insets.left + insets.right);
            h = height - (insets.top + insets.bottom);
        }
        g2.setColor(metroBackground);
        g2.fillRect(x, y, w, h);
    }

    private void paintFocus(Graphics g, AbstractButton b) {
        int width = b.getWidth();
        int height = b.getHeight();
        Insets insets = CommandLinkBorder.INSETS;
        int x = insets.left;
        int y = insets.top;
        int w = width - (insets.left + insets.right);
        int h = height - (insets.top + insets.bottom);
        Color focusColor = UIManager.getColor(getPropertyPrefix() + "focus");
        g.setColor(focusColor);
        BasicGraphicsUtils.drawDashedRect(g, x, y, w, h);
    }

    private void updateMnemonicIndex() {
        int mnemonicIndex = this.commandLink.getDisplayedMnemonicIndex();
        if (isMnemonicHidden()) {
            mnemonicIndex = -1;
        }
        MnemonicUnderlineSupport.setMnemonicIndex(this.textArea, mnemonicIndex);
    }

    protected boolean isMnemonicHidden() {
        return false;
    }

    private void updateForegroundColors(boolean isActive) {
        Boolean isActiveValue = Boolean.valueOf(isActive);
        if (isActiveValue == this.cachedActiveState) {
            return;
        }
        this.cachedActiveState = isActiveValue;
        String foregroundKey = isActive ? "CommandLink.activeForeground" : "CommandLink.foreground";
        Color foreground = UIManager.getColor(foregroundKey);
        this.textArea.setForeground(foreground);
        if (this.descriptionArea != null) {
            this.descriptionArea.setForeground(foreground);
        }
    }

    private void updateIcon(boolean isDefault) {
        Boolean isDefaultValue = Boolean.valueOf(isDefault);
        if (isDefaultValue == this.cachedDefaultState) {
            return;
        }
        this.cachedDefaultState = isDefaultValue;
        if (hasStandardIcon()) {
            this.commandLink.setIcon(getStandardIcon(isDefault));
        }
    }

    private boolean hasStandardIcon() {
        return this.commandLink.getIcon() == null || (this.commandLink.getIcon() instanceof UIResource) || this.commandLink.getForceStandardIcon();
    }

    private static Icon getStandardIcon(boolean isDefault) {
        String iconKey = isDefault ? "CommandLink.defaultIcon" : "CommandLink.icon";
        return UIManager.getIcon(iconKey);
    }

    private Icon getIcon(boolean isDefault) {
        if (hasStandardIcon()) {
            return getStandardIcon(isDefault);
        }
        return this.commandLink.getIcon();
    }

    private static JTextComponent createTextComponent(String text) {
        JTextArea area = new StaticTextArea(text);
        ComponentSupport.configureTransparentBackground(area);
        area.setBorder((Border) null);
        area.setMargin(new Insets(0, 0, 0, 0));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setMinimumSize(new Dimension(0, 0));
        return area;
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/basic/BasicCommandLinkUI$CommandLinkListener.class */
    private final class CommandLinkListener extends BasicButtonListener {
        private final JGCommandLink link;

        CommandLinkListener(JGCommandLink link) {
            super(link);
            this.link = link;
        }

        public void propertyChange(PropertyChangeEvent evt) {
            String propertyName = evt.getPropertyName();
            int z = -1;
            switch (propertyName.hashCode()) {
                case -1724546052:
                    if (propertyName.equals("description")) {
                        z = 1;
                        break;
                    }
                    break;
                case -173276743:
                    if (propertyName.equals("iconVisible")) {
                        z = 0;
                        break;
                    }
                    break;
                case 3226745:
                    if (propertyName.equals("icon")) {
                        z = 3;
                        break;
                    }
                    break;
                case 183304033:
                    if (propertyName.equals(JGCommandLink.PROPERTY_FORCE_STANDARD_ICON)) {
                        z = 2;
                        break;
                    }
                    break;
            }
            switch (z) {
                case 0:
                case 1:
                case 2:
                    BasicCommandLinkUI.this.uninstallComponents();
                    BasicCommandLinkUI.this.installComponents();
                    this.link.revalidate();
                    this.link.repaint();
                    return;
                case 3:
                    BasicCommandLinkUI.this.iconLabel.setIcon(this.link.isIconVisible() ? this.link.getIcon() : null);
                    return;
                default:
                    super.propertyChange(evt);
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/plaf/basic/BasicCommandLinkUI$CommandLinkBorder.class */
    public static final class CommandLinkBorder extends AbstractBorder {
        static final Insets INSETS = new Insets(2, 2, 2, 2);

        private CommandLinkBorder() {
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int w, int h) {
            Color outerLine;
            Color light;
            Color lighter;
            Color lightest;
            JButton link = (JButton) c;
            ButtonModel m = link.getModel();
            Color innerLine = Color.WHITE;
            if (m.isArmed()) {
                outerLine = new Color(165, 165, 165);
                light = new Color(178, 178, 178);
                lighter = new Color(198, 198, 198);
                lightest = new Color(204, 204, 204);
            } else if (m.isRollover() || m.isPressed()) {
                outerLine = new Color(125, 162, 206);
                light = new Color(210, 210, 210);
                lighter = new Color(234, 234, 234);
                lightest = new Color(245, 245, 245);
            } else if (link.isDefaultButton()) {
                outerLine = new Color(185, 215, 252);
                light = new Color(170, 238, 255);
                lighter = new Color(198, 244, 255);
                lightest = new Color(235, 251, 255);
            } else {
                return;
            }
            paintBorder(g, x, y, w, h, light, lighter, lightest, outerLine, innerLine);
        }

        private static void paintBorder(Graphics g, int x, int y, int w, int h, Color light, Color lighter, Color lightest, Color outerLine, Color innerLine) {
            g.translate(x, y);
            g.setColor(lighter);
            g.fillRect(1, 0, 1, 1);
            g.fillRect(0, 1, 1, 1);
            g.fillRect(w - 2, 0, 1, 1);
            g.fillRect(w - 1, 1, 1, 1);
            g.fillRect(1, h - 1, 1, 1);
            g.fillRect(0, h - 2, 1, 1);
            g.fillRect(w - 2, h - 1, 1, 1);
            g.fillRect(w - 1, h - 2, 1, 1);
            g.setColor(light);
            g.fillRect(2, 0, 1, 1);
            g.fillRect(1, 1, 1, 1);
            g.fillRect(0, 2, 1, 1);
            g.fillRect(w - 3, 0, 1, 1);
            g.fillRect(w - 2, 1, 1, 1);
            g.fillRect(w - 1, 2, 1, 1);
            g.fillRect(2, h - 1, 1, 1);
            g.fillRect(1, h - 2, 1, 1);
            g.fillRect(0, h - 3, 1, 1);
            g.fillRect(w - 3, h - 1, 1, 1);
            g.fillRect(w - 2, h - 2, 1, 1);
            g.fillRect(w - 1, h - 3, 1, 1);
            g.setColor(outerLine);
            g.fillRect(3, 0, w - 6, 1);
            g.fillRect(3, h - 1, w - 6, 1);
            g.fillRect(0, 3, 1, h - 6);
            g.fillRect(w - 1, 3, 1, h - 6);
            g.setColor(lightest);
            g.fillRect(2, 1, 1, 1);
            g.fillRect(1, 2, 1, 1);
            g.fillRect(w - 3, 1, 1, 1);
            g.fillRect(w - 2, 2, 1, 1);
            g.fillRect(2, h - 2, 1, 1);
            g.fillRect(1, h - 3, 1, 1);
            g.fillRect(w - 3, h - 2, 1, 1);
            g.fillRect(w - 2, h - 3, 1, 1);
            g.setColor(innerLine);
            g.fillRect(3, 1, w - 6, 1);
            g.fillRect(3, h - 2, w - 6, 1);
            g.fillRect(1, 3, 1, h - 6);
            g.fillRect(w - 2, 3, 1, h - 6);
            g.translate(-x, -y);
        }

        public Insets getBorderInsets(Component c) {
            return INSETS;
        }

        public Insets getBorderInsets(Component c, Insets newInsets) {
            newInsets.top = INSETS.top;
            newInsets.left = INSETS.left;
            newInsets.bottom = INSETS.bottom;
            newInsets.right = INSETS.right;
            return newInsets;
        }
    }
}
