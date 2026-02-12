package com.jgoodies.sandbox.accessibility;

import com.jgoodies.binding.value.ComponentModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.dialogs.basics.accessibility.AccessibilityUtils;
import com.jgoodies.dialogs.basics.accessibility.ControlList;
import java.awt.AWTEvent;
import java.awt.Component;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import javax.swing.AbstractButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/accessibility/AccessibilityChecker.class */
public final class AccessibilityChecker {
    private AccessibilityChecker() {
    }

    public static void install() {
        Toolkit.getDefaultToolkit().addAWTEventListener(new CheckHandler(), 1L);
    }

    public static void installIfEnabledBySystemProperty() {
        String property = System.getProperty("AccessibilityChecker");
        if (ComponentModel.PROPERTY_ENABLED.equalsIgnoreCase(property) || "on".equalsIgnoreCase(property)) {
            install();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void checkAndReport(Container parent) {
        Preconditions.checkNotNull(parent, Messages.MUST_NOT_BE_NULL, "parent");
        List<Component> mnemonicComponents = AccessibilityUtils.visitAndCollectComponents(parent, new MnemonicFilter());
        reportMnemonicDuplicates(findDuplicates(mnemonicComponents));
        List<Component> inputComponents = AccessibilityUtils.visitAndCollectComponents(parent, new AccessibilityUtils.DefaultInputElementFilter().and(new NotExcludedFilter()));
        reportAccessibleInfoProblems(inputComponents);
    }

    private static void reportAccessibleInfoProblems(List<Component> inputElements) {
        Map<String, List<Component>> map = new HashMap<>();
        for (Component c : inputElements) {
            String info = AccessibilityUtils.getAccessibleInfo(c);
            if (Strings.isBlank(info)) {
                System.out.println("Missing accessible info: " + print(c));
            } else {
                List<Component> components = map.get(info);
                if (components == null) {
                    components = new ArrayList<>();
                    map.put(info, components);
                }
                components.add(c);
            }
        }
        reportInfoDuplicates(map);
    }

    private static void reportInfoDuplicates(Map<String, List<Component>> infoMap) {
        infoMap.forEach((accessibleInfo, components) -> {
            if (components.size() > 1) {
                System.out.println("Doppelte Accessible Info '" + accessibleInfo + "':");
                Iterator it = components.iterator();
                while (it.hasNext()) {
                    AbstractButton abstractButton = (Component) it.next();
                    if (abstractButton instanceof JLabel) {
                        System.out.println("  JLabel: \"" + print((JLabel) abstractButton) + "\" " + abstractButton);
                    } else if (abstractButton instanceof AbstractButton) {
                        System.out.println("  Button: \"" + print(abstractButton) + "\" " + abstractButton);
                    }
                }
            }
        });
    }

    private static Map<Integer, List<Component>> findDuplicates(List<Component> mnemonicComponents) {
        Map<Integer, List<Component>> mnemonicMap = new HashMap<>();
        Iterator<Component> it = mnemonicComponents.iterator();
        while (it.hasNext()) {
            JLabel jLabel = (Component) it.next();
            int mnemonic = 0;
            if (jLabel instanceof JLabel) {
                mnemonic = jLabel.getDisplayedMnemonic();
            } else if (jLabel instanceof AbstractButton) {
                mnemonic = ((AbstractButton) jLabel).getMnemonic();
            }
            if (mnemonic != 0) {
                Integer key = Integer.valueOf(mnemonic);
                List<Component> components = mnemonicMap.get(key);
                if (components == null) {
                    components = new ArrayList<>();
                    mnemonicMap.put(key, components);
                }
                components.add(jLabel);
            }
        }
        return mnemonicMap;
    }

    private static void reportMnemonicDuplicates(Map<Integer, List<Component>> mnemonicMap) {
        mnemonicMap.forEach((mnemonic, components) -> {
            if (components.size() > 1) {
                System.out.println("Mehrfach vergebener Mnemonic '" + ((char) mnemonic.intValue()) + "':");
                Iterator it = components.iterator();
                while (it.hasNext()) {
                    AbstractButton abstractButton = (Component) it.next();
                    if (abstractButton instanceof JLabel) {
                        System.out.println("  JLabel: \"" + print((JLabel) abstractButton) + "\" " + abstractButton);
                    } else if (abstractButton instanceof AbstractButton) {
                        System.out.println("  Button: \"" + print(abstractButton) + "\" " + abstractButton);
                    }
                }
            }
        });
    }

    private static String print(Component c) {
        if (c instanceof JTextComponent) {
            return print((JTextComponent) c);
        }
        if (c instanceof JLabel) {
            return print((JLabel) c);
        }
        if (c instanceof AbstractButton) {
            return print((AbstractButton) c);
        }
        return c.toString();
    }

    private static String print(JTextComponent c) {
        return "\"" + c.getText() + "\" " + c.toString();
    }

    private static String print(JLabel label) {
        return label.getText();
    }

    private static String print(AbstractButton button) {
        if (Strings.isNotBlank(button.getText())) {
            return button.getText();
        }
        if (Strings.isNotBlank(button.getToolTipText())) {
            return button.getToolTipText();
        }
        if (Strings.isNotBlank(button.getActionCommand())) {
            return button.getActionCommand();
        }
        return button.toString();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/accessibility/AccessibilityChecker$MnemonicFilter.class */
    public static final class MnemonicFilter implements Predicate<Component> {
        private MnemonicFilter() {
        }

        @Override // java.util.function.Predicate
        public boolean test(Component c) {
            return (c instanceof JLabel) || (c instanceof AbstractButton);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/accessibility/AccessibilityChecker$NotExcludedFilter.class */
    public static final class NotExcludedFilter implements Predicate<Component> {
        private NotExcludedFilter() {
        }

        @Override // java.util.function.Predicate
        public boolean test(Component c) {
            return !ControlList.isExcluded(c);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/accessibility/AccessibilityChecker$CheckHandler.class */
    public static final class CheckHandler implements AWTEventListener {
        private CheckHandler() {
        }

        public void eventDispatched(AWTEvent event) {
            JRootPane rootPane;
            if (event instanceof ComponentEvent) {
                ComponentEvent ce = (ComponentEvent) event;
                if (ce.getID() == 102 && (ce.getComponent() instanceof JPanel) && (rootPane = SwingUtilities.getRootPane(ce.getComponent())) != null) {
                    AccessibilityChecker.checkAndReport(rootPane);
                }
            }
        }
    }
}
