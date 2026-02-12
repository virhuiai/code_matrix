package com.jgoodies.application;

import com.jgoodies.application.internal.Exceptions;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.MnemonicUtils;
import java.util.MissingResourceException;
import javax.swing.Icon;
import javax.swing.KeyStroke;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/Actions.class */
public final class Actions {
    public static final String ACCESSIBLE_NAME_KEY = "AccessibleNameKey";
    public static final String ACCESSIBLE_DESCRIPTION_KEY = "AccessibleDescriptionKey";
    private static final String TEXT = ".Action.text";
    private static final String MNEMONIC = ".Action.mnemonic";
    private static final String ACCELERATOR = ".Action.accelerator";
    private static final String COMMAND = ".Action.command";
    private static final String ICON = ".Action.icon";
    private static final String SMALL_ICON = ".Action.smallIcon";
    private static final String LARGE_ICON = ".Action.largeIcon";
    private static final String SHORT_DESCRIPTION = ".Action.shortDescription";
    private static final String LONG_DESCRIPTION = ".Action.longDescription";
    private static final String ACCESSIBLE_NAME = ".Action.accessibleName";
    private static final String ACCESSIBLE_DESCRIPTION = ".Action.accessibleDescription";
    private static final Character NO_MNEMONIC = '0';

    private Actions() {
    }

    public static void configure(javax.swing.Action action, ResourceMap map, String prefix) {
        configure(action, map, prefix, null);
    }

    public static void configure(javax.swing.Action action, ResourceMap map, String prefix, String configurationExceptionMessageDetails) {
        Preconditions.checkNotNull(action, Messages.MUST_NOT_BE_NULL, "Action");
        String markedText = getString(map, prefix + TEXT);
        String command = getString(map, prefix + COMMAND);
        String shortDescription = getString(map, prefix + SHORT_DESCRIPTION);
        String longDescription = getString(map, prefix + LONG_DESCRIPTION);
        String accessibleName = getString(map, prefix + ACCESSIBLE_NAME);
        String accessibleDescription = getString(map, prefix + ACCESSIBLE_DESCRIPTION);
        Icon smallIcon = getIcon(map, prefix + SMALL_ICON);
        Icon largeIcon = getIcon(map, prefix + LARGE_ICON);
        if (smallIcon == null) {
            smallIcon = getIcon(map, prefix + ICON);
        }
        KeyStroke accelerator = (KeyStroke) getObject(map, prefix + ACCELERATOR, KeyStroke.class, null);
        Character mnemonicChar = (Character) getObject(map, prefix + MNEMONIC, Character.class, NO_MNEMONIC);
        Integer mnemonic = mnemonicChar == null ? null : Integer.valueOf(Character.toUpperCase(mnemonicChar.charValue()));
        if (markedText == null) {
            checkConfiguration((smallIcon == null && largeIcon == null) ? false : true, map, prefix, configurationExceptionMessageDetails, "You must provide either the action text or action icon.\ntext key=%1$s\nicon key=%2$s");
        } else {
            checkConfiguration(Strings.isNotBlank(markedText), map, prefix, configurationExceptionMessageDetails, "The action text must not be empty or whitespace.\ntext key=%1$s");
        }
        MnemonicUtils.configure(action, markedText);
        if (mnemonicChar != NO_MNEMONIC) {
            configureMnemonicAndIndexFromMnemonic(action, mnemonic);
        }
        action.putValue("AcceleratorKey", accelerator);
        action.putValue("ShortDescription", shortDescription);
        action.putValue("LongDescription", longDescription);
        action.putValue("SmallIcon", smallIcon);
        action.putValue("SwingLargeIconKey", largeIcon);
        action.putValue("ActionCommandKey", command);
        action.putValue(ACCESSIBLE_NAME_KEY, accessibleName);
        action.putValue(ACCESSIBLE_DESCRIPTION_KEY, accessibleDescription);
    }

    private static void configureMnemonicAndIndexFromMnemonic(javax.swing.Action action, Integer mnemonic) {
        if (mnemonic == null) {
            action.putValue("MnemonicKey", (Object) null);
            action.putValue("SwingDisplayedMnemonicIndexKey", (Object) null);
            return;
        }
        action.putValue("MnemonicKey", mnemonic);
        String text = (String) action.getValue("Name");
        if (text == null) {
            return;
        }
        int computedMnemonicIndex = text.indexOf(mnemonic.intValue());
        if (computedMnemonicIndex == -1) {
            computedMnemonicIndex = text.indexOf(Character.toLowerCase(mnemonic.intValue()));
        }
        action.putValue("SwingDisplayedMnemonicIndexKey", Integer.valueOf(computedMnemonicIndex));
    }

    private static <T> T getObject(ResourceMap resourceMap, String str, Class<T> cls, T t) {
        try {
            return (T) resourceMap.getObject(str, cls);
        } catch (MissingResourceException e) {
            return t;
        }
    }

    private static String getString(ResourceMap map, String key) {
        return (String) getObject(map, key, String.class, null);
    }

    private static Icon getIcon(ResourceMap map, String key) {
        return (Icon) getObject(map, key, Icon.class, null);
    }

    private static void checkConfiguration(boolean expression, ResourceMap map, String keyPrefix, String configurationExceptionMessageDetails, String rawMessageFormat) {
        if (!expression) {
            String messageFormat = configurationExceptionMessageDetails != null ? rawMessageFormat + configurationExceptionMessageDetails : rawMessageFormat + "\nbundle  =%3$s";
            String message = String.format(messageFormat, keyPrefix + TEXT, keyPrefix + SMALL_ICON, map.getBaseName());
            throw new Exceptions.ActionConfigurationException(message);
        }
    }
}
