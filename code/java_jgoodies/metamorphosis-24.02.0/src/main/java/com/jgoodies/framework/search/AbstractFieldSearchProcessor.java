package com.jgoodies.framework.search;

import com.jgoodies.common.base.Strings;
import com.jgoodies.search.Completion;
import com.jgoodies.search.CompletionPublisher;
import com.jgoodies.search.CompletionState;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/AbstractFieldSearchProcessor.class */
public abstract class AbstractFieldSearchProcessor<V> implements FieldSearchProcessor<V> {
    public V poorMansSearchValue(String content) {
        if (Strings.isBlank(content)) {
            return null;
        }
        DelegatePublisher publisher = new DelegatePublisher();
        boolean searchPossible = search(content, content.length(), publisher, publisher);
        Completion firstCompletion = publisher.firstCompletion;
        if (!searchPossible || firstCompletion == null) {
            return null;
        }
        return valueFor(firstCompletion);
    }

    @Override // com.jgoodies.framework.search.FieldSearchProcessor
    public boolean represents(String text, V value) {
        String trimmedText = text == null ? "" : text.trim();
        String displayString = getDisplayString(value);
        String trimmedDisplayString = displayString == null ? "" : displayString.trim();
        return trimmedText.equalsIgnoreCase(trimmedDisplayString);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/AbstractFieldSearchProcessor$DelegatePublisher.class */
    protected static final class DelegatePublisher implements CompletionPublisher, CompletionState {
        Completion firstCompletion = null;

        protected DelegatePublisher() {
        }

        public void publish(Completion... chunks) {
            if (this.firstCompletion == null && chunks.length > 0) {
                this.firstCompletion = chunks[0];
            }
        }

        public boolean isCancelled() {
            return this.firstCompletion != null;
        }
    }
}
