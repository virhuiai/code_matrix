package com.jgoodies.common.swing;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/MnemonicUtils.class */
public final class MnemonicUtils {
    static final char AMPERSAND_MARKER = '&';
    static final char UNDERSCORE_MARKER = '_';
    static MnemonicMarker marker = MnemonicMarker.UNDERSCORE;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/MnemonicUtils$MnemonicMarker.class */
    public enum MnemonicMarker {
        AMPERSAND,
        UNDERSCORE,
        BOTH
    }

    private MnemonicUtils() {
    }

    public static MnemonicMarker getMarker() {
        return marker;
    }

    public static void setMarker(MnemonicMarker newValue) {
        marker = (MnemonicMarker) Preconditions.checkNotNull(newValue, Messages.MUST_NOT_BE_NULL, "mnemonic marker");
    }

    public static void configure(AbstractButton target, String markedText) {
        Preconditions.checkNotNull(target, Messages.MUST_NOT_BE_NULL, "target");
        configure0(target, new MnemonicText(markedText, getMarker(), null));
    }

    public static void configure(Action target, String markedText) {
        Preconditions.checkNotNull(target, Messages.MUST_NOT_BE_NULL, "target");
        configure0(target, new MnemonicText(markedText, getMarker(), null));
    }

    public static void configure(JLabel target, String markedText) {
        Preconditions.checkNotNull(target, Messages.MUST_NOT_BE_NULL, "target");
        configure0(target, new MnemonicText(markedText, getMarker(), null));
    }

    public static void configure(JTabbedPane target, int targetIndex, String markedTitle) {
        Preconditions.checkNotNull(target, Messages.MUST_NOT_BE_NULL, "target");
        configure0(target, targetIndex, new MnemonicText(markedTitle, getMarker(), null));
    }

    public static String plainText(String markedText) {
        return new MnemonicText(markedText, getMarker(), null).text;
    }

    public static boolean containsMarker(String text) {
        return new MnemonicText(text, getMarker(), null).index != -1;
    }

    static int mnemonic(String markedText) {
        return new MnemonicText(markedText, getMarker(), null).key;
    }

    static int mnemonicIndex(String markedText) {
        return new MnemonicText(markedText, getMarker(), null).index;
    }

    private static void configure0(AbstractButton button, MnemonicText mnemonicText) {
        button.setText(mnemonicText.text);
        button.setMnemonic(mnemonicText.key);
        button.setDisplayedMnemonicIndex(mnemonicText.index);
    }

    private static void configure0(Action action, MnemonicText mnemonicText) {
        Integer keyValue = Integer.valueOf(mnemonicText.key);
        Integer indexValue = mnemonicText.index == -1 ? null : Integer.valueOf(mnemonicText.index);
        action.putValue("Name", mnemonicText.text);
        action.putValue("MnemonicKey", keyValue);
        action.putValue("SwingDisplayedMnemonicIndexKey", indexValue);
    }

    private static void configure0(JLabel label, MnemonicText mnemonicText) {
        label.setText(mnemonicText.text);
        label.setDisplayedMnemonic(mnemonicText.key);
        label.setDisplayedMnemonicIndex(mnemonicText.index);
    }

    private static void configure0(JTabbedPane pane, int index, MnemonicText mnemonicText) {
        pane.setTitleAt(index, mnemonicText.text);
        pane.setMnemonicAt(index, mnemonicText.key);
        pane.setDisplayedMnemonicIndexAt(index, mnemonicText.index);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/MnemonicUtils$MnemonicText.class */
    public static final class MnemonicText {
        String text;
        int key;
        int index;

        /* synthetic */ MnemonicText(String x0, MnemonicMarker x1, AnonymousClass1 x2) {
            this(x0, x1);
        }

        private MnemonicText(String markedText, MnemonicMarker marker) {
            switch (AnonymousClass1.$SwitchMap$com$jgoodies$common$swing$MnemonicUtils$MnemonicMarker[marker.ordinal()]) {
                case 1:
                    compute(markedText, '&');
                    return;
                case AnimatedLabel.LEFT /* 2 */:
                    compute(markedText, '_');
                    return;
                case 3:
                    if (markedText == null) {
                        compute(markedText, '_');
                        return;
                    }
                    int i = markedText.indexOf(MnemonicUtils.UNDERSCORE_MARKER);
                    if (i != -1) {
                        compute(markedText, '_');
                        return;
                    } else {
                        compute(markedText, '&');
                        return;
                    }
                default:
                    throw new IllegalStateException("Unknown marker setup");
            }
        }

        private void compute(String markedText, char marker) {
            if (markedText != null && markedText.length() > 1) {
                int indexOf = markedText.indexOf(marker);
                int i = indexOf;
                if (indexOf != -1) {
                    boolean html = Strings.startsWithIgnoreCase(markedText, "<html>");
                    StringBuilder builder = new StringBuilder();
                    int begin = 0;
                    int quotedMarkers = 0;
                    int markerIndex = -1;
                    boolean marked = false;
                    char markedChar = 0;
                    CharacterIterator sci = new StringCharacterIterator(markedText);
                    do {
                        builder.append(markedText.substring(begin, i));
                        char current = sci.setIndex(i);
                        char next = sci.next();
                        if (html) {
                            int entityEnd = indexOfEntityEnd(markedText, i);
                            if (entityEnd == -1) {
                                marked = true;
                                builder.append("<u>").append(next).append("</u>");
                                begin = i + 2;
                                markedChar = next;
                            } else {
                                builder.append(markedText.substring(i, entityEnd));
                                begin = entityEnd;
                            }
                        } else if (next == marker) {
                            builder.append(next);
                            begin = i + 2;
                            quotedMarkers++;
                        } else if (Character.isWhitespace(next)) {
                            builder.append(current).append(next);
                            begin = i + 2;
                        } else if (next == 65535) {
                            builder.append(current);
                            begin = i + 2;
                        } else {
                            builder.append(next);
                            begin = i + 2;
                            markerIndex = i - quotedMarkers;
                            marked = true;
                            markedChar = next;
                        }
                        i = markedText.indexOf(marker, begin);
                        if (i == -1) {
                            break;
                        }
                    } while (!marked);
                    if (begin < markedText.length()) {
                        builder.append(markedText.substring(begin));
                    }
                    this.text = builder.toString();
                    this.index = markerIndex;
                    if (marked) {
                        this.key = mnemonicKey(markedChar);
                        return;
                    } else {
                        this.key = 0;
                        return;
                    }
                }
            }
            this.text = markedText;
            this.key = 0;
            this.index = -1;
        }

        private static int indexOfEntityEnd(String htmlText, int start) {
            char c;
            CharacterIterator sci = new StringCharacterIterator(htmlText, start);
            do {
                c = sci.next();
                if (c == ';') {
                    return sci.getIndex();
                }
                if (!Character.isLetterOrDigit(c)) {
                    return -1;
                }
            } while (c != 65535);
            return -1;
        }

        private static int mnemonicKey(char c) {
            int vk = c;
            if (vk >= 97 && vk <= 122) {
                vk -= 32;
            }
            return vk;
        }
    }

    /* renamed from: com.jgoodies.common.swing.MnemonicUtils$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/MnemonicUtils$1.class */
    static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$common$swing$MnemonicUtils$MnemonicMarker = new int[MnemonicMarker.values().length];

        static {
            try {
                $SwitchMap$com$jgoodies$common$swing$MnemonicUtils$MnemonicMarker[MnemonicMarker.AMPERSAND.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$jgoodies$common$swing$MnemonicUtils$MnemonicMarker[MnemonicMarker.UNDERSCORE.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$jgoodies$common$swing$MnemonicUtils$MnemonicMarker[MnemonicMarker.BOTH.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
        }
    }
}
