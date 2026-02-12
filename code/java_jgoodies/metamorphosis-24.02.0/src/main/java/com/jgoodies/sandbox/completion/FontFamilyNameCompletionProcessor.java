package com.jgoodies.sandbox.completion;

import com.jgoodies.common.base.Strings;
import com.jgoodies.search.Completion;
import com.jgoodies.search.CompletionProcessor;
import com.jgoodies.search.CompletionPublisher;
import com.jgoodies.search.CompletionState;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/sandbox/completion/FontFamilyNameCompletionProcessor.class */
public final class FontFamilyNameCompletionProcessor implements CompletionProcessor {
    private static final String EXAMPLE_STRING = "The quick brown fox jumps over the lazy dog\nTHE QUICK BROWN FOX JUMPS OVER THE LAZY DOG\n1234567890!@#$%^&*()-=\\`_+|~,./<>?;':\"[]{}";
    private final Locale locale;
    private String[] fontFamilyNames;

    public FontFamilyNameCompletionProcessor() {
        this(Locale.getDefault());
    }

    public FontFamilyNameCompletionProcessor(Locale locale) {
        this.locale = locale;
    }

    public void prepare() {
        getFontFamilyNames();
    }

    public boolean isAutoActivatable(String content, int caretPosition) {
        if (Strings.isBlank(content)) {
            return false;
        }
        List<Completion> completions = search(content);
        return 1 <= completions.size() && completions.size() <= 10;
    }

    public boolean search(String content, int caretPosition, CompletionPublisher publisher, CompletionState state) {
        publisher.publish(search(content));
        return true;
    }

    private List<Completion> search(String content) {
        String trimmedContent = content.trim();
        List<Completion> completions = new ArrayList<>();
        for (String fontName : getFontFamilyNames()) {
            if (matchesExact(fontName, trimmedContent)) {
                completions.add(new Completion.Builder().replacementText(fontName, new Object[0]).additionalInfo("%1$s 11pt Plain\n%2$s", new Object[]{fontName, EXAMPLE_STRING}).build());
            }
        }
        return completions;
    }

    private String[] getFontFamilyNames() {
        if (this.fontFamilyNames == null) {
            this.fontFamilyNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames(this.locale);
        }
        return this.fontFamilyNames;
    }

    private static boolean matchesExact(String str, String content) {
        return Strings.startsWithIgnoreCase(str, content) && str.length() != content.length();
    }
}
