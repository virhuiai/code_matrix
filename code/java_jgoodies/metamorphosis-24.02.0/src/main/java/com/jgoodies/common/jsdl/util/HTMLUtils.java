package com.jgoodies.common.jsdl.util;

import com.jgoodies.common.base.Preconditions;
import java.awt.Font;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/jsdl/util/HTMLUtils.class */
public class HTMLUtils {
    private static Font cachedTextFont;
    private static String cachedDefaultRule;

    protected HTMLUtils() {
    }

    public static void addDefaultStyleSheetRuleToSharedStyleSheet() {
        addRuleToSharedStyleSheet(getDefaultRule());
    }

    public static void addRuleToSharedStyleSheet(String rule) {
        new HTMLEditorKit().getStyleSheet().addRule(rule);
    }

    public static void addDefaultStyleSheetRule(JEditorPane editorPane) {
        addStyleSheetRule(editorPane, getDefaultRule());
    }

    public static void addDefaultStyleSheetRule(JEditorPane editorPane, Font textFont) {
        addStyleSheetRule(editorPane, createDefaultRule(textFont));
    }

    public static void addStyleSheetRule(JEditorPane editorPane, String rule) {
        HTMLDocument document = editorPane.getDocument();
        Preconditions.checkArgument(document instanceof HTMLDocument, "The editor pane must hold an HTMLDocument.");
        document.getStyleSheet().addRule(rule);
    }

    public static Font getDefaultTextFont() {
        return UIManager.getFont("Label.font");
    }

    private static String getDefaultRule() {
        Font textFont = getDefaultTextFont();
        if (textFont != cachedTextFont) {
            cachedDefaultRule = createDefaultRule(textFont);
            cachedTextFont = textFont;
        }
        return cachedDefaultRule;
    }

    private static String createDefaultRule(Font font) {
        String rule = "body, p, td, a, li { font-family: " + font.getName() + "; font-size: " + font.getSize() + "pt;  }a                  { color: #0066CC; }";
        return rule;
    }
}
