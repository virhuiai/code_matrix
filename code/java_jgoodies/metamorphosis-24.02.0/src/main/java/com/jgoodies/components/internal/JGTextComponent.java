package com.jgoodies.components.internal;

import com.jgoodies.common.base.Strings;
import javax.swing.Action;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/internal/JGTextComponent.class */
public interface JGTextComponent {
    public static final String PROPERTY_PROMPT = "prompt";
    public static final String PROPERTY_PROMPT_STYLE = "promptStyle";
    public static final String PROPERTY_PROMPT_VISIBLE_WHEN_FOCUSED = "promptVisibleWhenFocused";

    String getPrompt();

    void setPrompt(String str);

    int getPromptStyle();

    void setPromptStyle(int i);

    boolean isPromptVisibleWhenFocused();

    void setPromptVisibleWhenFocused(boolean z);

    boolean isJGFocusTraversable();

    void setJGFocusTraversable(Boolean bool);

    Boolean getSelectOnFocusGainEnabled();

    void setSelectOnFocusGainEnabled(Boolean bool);

    void setKeyboardAction(Action action);

    default void setPrompt(String str, Object... args) {
        setPrompt(Strings.get(str, args));
    }
}
