package com.jgoodies.components.internal;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.swing.internal.RenderingUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.Objects;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/PromptSupport.class */
public final class PromptSupport {
    public static final String PROPERTY_PROMPT = "prompt";
    public static final String PROPERTY_PROMPT_STYLE = "promptStyle";
    public static final String PROPERTY_PROMPT_VISIBLE_WHEN_FOCUSED = "promptVisibleWhenFocused";
    public static final String PROPERTY_PROMPT_HORIZONTAL_ALIGNMENT = "promptHorizontalAlignment";
    private static final String PROPERTY_LAST_FOCUS_GAIN_WAS_ACTIVATION = "lastFocusGainWasActivation";
    private static FocusListener focusHandler = null;
    private static Rectangle paintIconR = new Rectangle();
    private static Rectangle paintTextR = new Rectangle();
    private static Rectangle paintViewR = new Rectangle();
    private static Insets paintViewInsets = new Insets(0, 0, 0, 0);

    private PromptSupport() {
    }

    public static String getPrompt(JTextComponent c) {
        return (String) c.getClientProperty("prompt");
    }

    public static void setPrompt(JTextComponent c, String newPrompt) {
        String oldPrompt = getPrompt(c);
        if (Objects.equals(oldPrompt, newPrompt)) {
            return;
        }
        if (Strings.isNotBlank(newPrompt)) {
            c.addFocusListener(getFocusHandler());
        } else {
            c.removeFocusListener(getFocusHandler());
        }
        c.putClientProperty("prompt", newPrompt);
        c.repaint();
    }

    public static int getPromptStyle(JTextComponent c) {
        Integer style = (Integer) c.getClientProperty("promptStyle");
        if (style != null) {
            return style.intValue();
        }
        return 2;
    }

    public static void setPromptStyle(JTextComponent c, int newStyle) {
        Preconditions.checkArgument(newStyle == 2 || newStyle == 0, "The style must be one of: Font.ITALIC, FONT.PLAIN");
        int oldStyle = getPromptStyle(c);
        if (oldStyle == newStyle) {
            return;
        }
        c.putClientProperty("promptStyle", Integer.valueOf(newStyle));
        c.repaint();
    }

    public static boolean isPromptVisibleWhenFocused(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty("promptVisibleWhenFocused"));
    }

    public static void setPromptVisibleWhenFocused(JTextComponent c, boolean newValue) {
        boolean oldValue = isPromptVisibleWhenFocused(c);
        if (oldValue == newValue) {
            return;
        }
        c.putClientProperty("promptVisibleWhenFocused", Boolean.valueOf(newValue));
        c.repaint();
    }

    public static boolean isPromptPainted(JTextComponent c) {
        return c.isVisible() && (!c.isFocusOwner() || isPromptVisibleWhenFocused(c) || getLastFocusGainWasActivation(c)) && c.getDocument().getLength() == 0 && Strings.isNotBlank(getPrompt(c));
    }

    public static void paintPrompt(JTextComponent c, Graphics g) {
        if (!isPromptPainted(c)) {
            return;
        }
        Graphics2D g2 = (Graphics2D) g;
        Font font = c.getFont().deriveFont(getPromptStyle(c));
        FontMetrics fm = g2.getFontMetrics(font);
        Insets insets = c.getInsets(paintViewInsets);
        paintViewR.x = insets.left;
        paintViewR.y = insets.top;
        paintViewR.width = c.getWidth() - (insets.left + insets.right);
        paintViewR.height = c.getHeight() - (insets.top + insets.bottom);
        Rectangle rectangle = paintIconR;
        Rectangle rectangle2 = paintIconR;
        Rectangle rectangle3 = paintIconR;
        paintIconR.height = 0;
        rectangle3.width = 0;
        rectangle2.y = 0;
        rectangle.x = 0;
        Rectangle rectangle4 = paintTextR;
        Rectangle rectangle5 = paintTextR;
        Rectangle rectangle6 = paintTextR;
        paintTextR.height = 0;
        rectangle6.width = 0;
        rectangle5.y = 0;
        rectangle4.x = 0;
        String prompt = getPrompt(c);
        String clippedText = layoutPrompt(c, prompt, fm, paintViewR, paintTextR);
        Color color = UIManager.getColor("textInactiveText");
        int x = paintTextR.x;
        int y = paintTextR.y + fm.getAscent();
        drawString(c, g2, clippedText, x, y, color, font);
    }

    private static String layoutPrompt(JTextComponent c, String prompt, FontMetrics fontMetrics, Rectangle viewR, Rectangle textR) {
        int horizontalAlignment = getPromptHorizontalAlignment(c);
        return SwingUtilities.layoutCompoundLabel(c, fontMetrics, prompt, (Icon) null, 0, horizontalAlignment, 0, 10, viewR, paintIconR, textR, 0);
    }

    private static void drawString(JComponent c, Graphics2D g2, String text, int x, int y, Color color, Font font) {
        Font oldFont = g2.getFont();
        Color oldColor = g2.getColor();
        g2.setFont(font);
        g2.setColor(color);
        RenderingUtils.drawString(c, g2, text, x, y);
        g2.setColor(oldColor);
        g2.setFont(oldFont);
    }

    private static boolean getLastFocusGainWasActivation(JTextComponent c) {
        return Boolean.TRUE.equals(c.getClientProperty(PROPERTY_LAST_FOCUS_GAIN_WAS_ACTIVATION));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void setLastFocusGainWasActivation(JTextComponent c, boolean b) {
        c.putClientProperty(PROPERTY_LAST_FOCUS_GAIN_WAS_ACTIVATION, Boolean.valueOf(b));
    }

    private static int getPromptHorizontalAlignment(JTextComponent c) {
        Object value = c.getClientProperty(PROPERTY_PROMPT_HORIZONTAL_ALIGNMENT);
        if (value instanceof Integer) {
            return ((Integer) value).intValue();
        }
        if (c instanceof JTextField) {
            return ((JTextField) c).getHorizontalAlignment();
        }
        return 10;
    }

    private static FocusListener getFocusHandler() {
        if (focusHandler == null) {
            focusHandler = new FocusHandler();
        }
        return focusHandler;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/PromptSupport$FocusHandler.class */
    public static final class FocusHandler implements FocusListener {
        private FocusHandler() {
        }

        public void focusGained(FocusEvent evt) {
            JTextComponent c = (JTextComponent) evt.getSource();
            boolean activation = evt.getOppositeComponent() == null;
            PromptSupport.setLastFocusGainWasActivation(c, activation);
            c.repaint();
        }

        public void focusLost(FocusEvent evt) {
            JTextComponent c = (JTextComponent) evt.getSource();
            c.repaint();
        }
    }
}
