package com.jgoodies.components.util;

import com.jgoodies.common.base.SystemUtils;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/OSXComponentProperties.class */
public final class OSXComponentProperties {
    private OSXComponentProperties() {
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/OSXComponentProperties$SizeVariant.class */
    public enum SizeVariant {
        REGULAR("regular"),
        SMALL("small"),
        MINI("mini");

        public static final String KEY = "JComponent.sizeVariant";
        private final String value;

        public String getValue() {
            return this.value;
        }

        SizeVariant(String value) {
            this.value = value;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/OSXComponentProperties$ButtonType.class */
    public enum ButtonType {
        HELP("help"),
        ROUND_RECT("roundRect"),
        TEXTURED("textured");

        public static final String KEY = "JButton.buttonType";
        private final String value;

        public String getValue() {
            return this.value;
        }

        ButtonType(String value) {
            this.value = value;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/OSXComponentProperties$SegmentedButtonType.class */
    public enum SegmentedButtonType {
        ROUND_RECT("segmentedRoundRect"),
        TEXTURED("segmentedTextured");

        public static final String KEY = "JButton.buttonType";
        private final String value;

        public String getValue() {
            return this.value;
        }

        SegmentedButtonType(String value) {
            this.value = value;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/util/OSXComponentProperties$SegmentPosition.class */
    public enum SegmentPosition {
        FIRST("first"),
        MIDDLE("middle"),
        LAST("last"),
        ONLY("only");

        public static final String KEY = "JButton.segmentPosition";
        private final String value;

        public String getValue() {
            return this.value;
        }

        SegmentPosition(String value) {
            this.value = value;
        }
    }

    public static void setSizeVariant(JComponent component, SizeVariant variant) {
        component.putClientProperty(SizeVariant.KEY, variant.getValue());
    }

    public static void setButtonType(JButton button, ButtonType type) {
        button.putClientProperty("JButton.buttonType", type.getValue());
    }

    public static void setHelpButton(JButton button) {
        if (!SystemUtils.IS_OS_MAC) {
            return;
        }
        setButtonType(button, ButtonType.HELP);
        button.setText((String) null);
        button.setIcon((Icon) null);
    }

    public static void setSegmentedTypeAndPosition(AbstractButton button, SegmentedButtonType type, SegmentPosition position) {
        button.putClientProperty("JButton.buttonType", type.getValue());
        button.putClientProperty(SegmentPosition.KEY, position.getValue());
    }

    public static void setSegmentedTypeAndPosition(AbstractButton button, SegmentedButtonType type, int index, int segmentLength) {
        SegmentPosition position;
        if (segmentLength == 1) {
            position = SegmentPosition.ONLY;
        } else if (index == 0) {
            position = SegmentPosition.FIRST;
        } else if (index == segmentLength - 1) {
            position = SegmentPosition.LAST;
        } else {
            position = SegmentPosition.MIDDLE;
        }
        setSegmentedTypeAndPosition(button, type, position);
    }
}
