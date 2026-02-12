package com.jgoodies.framework.completion;

import com.jgoodies.application.Application;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.components.JGSearchField;
import com.jgoodies.search.Completion;
import com.jgoodies.search.CompletionApplicationEvent;
import com.jgoodies.search.CompletionManager;
import com.jgoodies.search.CompletionProcessor;
import com.jgoodies.search.CompletionPublisher;
import com.jgoodies.search.CompletionState;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/completion/Suggest.class */
public final class Suggest {
    public static final int DEFAULT_CAPACITY = 100;
    private static final int DEFAULT_AUTO_ACTIVATION_MIN_SIZE = 1;
    private final Preferences parent;
    private final MostRecentlyUsedList<String> mruList;
    private final CompletionProcessor processor;
    private Preferences prefs;
    private int autoActivationMinSize;
    private Predicate<String> textFilter;
    private Function<String, String> textConverter;
    private Function<String, String> displayStringConverter;

    static /* synthetic */ Preferences access$200() {
        return getPreferencesRoot();
    }

    private Suggest(Preferences parent) {
        this.autoActivationMinSize = DEFAULT_AUTO_ACTIVATION_MIN_SIZE;
        this.parent = parent;
        this.mruList = new MostRecentlyUsedList<>(100);
        this.processor = new SuggestCompletionProcessor();
    }

    public static Suggest install(JGSearchField searchField, String prefsId) {
        return new Builder().preferencesId(prefsId).install(searchField).build();
    }

    public static void clearAll() {
        try {
            getPreferencesRoot().clear();
        } catch (BackingStoreException ex) {
            Logger.getLogger(Suggest.class.getName()).log(Level.WARNING, "Could not clear the preferences root for suggestions.", (Throwable) ex);
        }
    }

    public void clear() {
        try {
            this.prefs.clear();
        } catch (BackingStoreException ex) {
            Logger.getLogger(Suggest.class.getName()).log(Level.WARNING, "Could not clear the preferences root for suggestions.", (Throwable) ex);
        }
        this.mruList.clear();
    }

    public boolean isEmpty() {
        return this.mruList.isEmpty();
    }

    public void setTextFilter(Predicate<String> filter) {
        this.textFilter = filter;
    }

    public void setAutoActivationMinimumSize(int size) {
        Preconditions.checkArgument(this.autoActivationMinSize > 0, "The auto activation minimum size must be greater than 0.");
        this.autoActivationMinSize = size;
    }

    public void setHistoryCapacity(int capacity) {
        Preconditions.checkArgument(capacity > DEFAULT_AUTO_ACTIVATION_MIN_SIZE, "The history capacity must be greater than 1.");
        ((MostRecentlyUsedList) this.mruList).capacity = capacity;
    }

    public void setPreferencesId(String prefsId) {
        Preconditions.checkNotBlank(prefsId, Messages.MUST_NOT_BE_BLANK, "preferences id");
        this.prefs = this.parent.node(prefsId);
        restoreList();
    }

    public void setTextConverter(Function<String, String> converter) {
        this.textConverter = converter;
    }

    public void setDisplayStringConverter(Function<String, String> converter) {
        this.displayStringConverter = converter;
    }

    public void addText(String suggestion) {
        if (Strings.isBlank(suggestion) || !testText(suggestion)) {
            return;
        }
        this.mruList.add(convertText(suggestion.trim()));
        storeList();
    }

    public void install(JGSearchField searchField) {
        Preconditions.checkNotNull(searchField, Messages.MUST_NOT_BE_NULL, "search field");
        CompletionManager manager = createCompletionManager();
        manager.install(searchField);
        manager.addCompletionApplicationListener(this::onCompletionApplied);
        searchField.addPropertyChangeListener(JGSearchField.PROPERTY_SEARCH_TEXT, this::onSearchTextChanged);
    }

    private void onSearchTextChanged(PropertyChangeEvent evt) {
        String searchText = (String) evt.getNewValue();
        addText(searchText);
        JGSearchField searchField = (JGSearchField) evt.getSource();
        if (searchField.getSearchMode() == JGSearchField.SearchMode.REGULAR) {
            CompletionManager manager = CompletionManager.getManager(searchField);
            manager.reactivate();
        }
    }

    private void onCompletionApplied(CompletionApplicationEvent event) {
        JGSearchField searchField = event.getTextComponent();
        if (searchField.getSearchMode() == JGSearchField.SearchMode.REGULAR) {
            searchField.search();
        }
    }

    private boolean testText(String text) {
        return this.textFilter == null || this.textFilter.test(text);
    }

    private String convertText(String text) {
        return this.textConverter == null ? text : this.textConverter.apply(text);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String convertDisplayString(String text) {
        return this.displayStringConverter == null ? text : this.displayStringConverter.apply(text);
    }

    private CompletionManager createCompletionManager() {
        CompletionManager manager = new CompletionManager(this.processor);
        manager.setAutoCompletionEnabled(false);
        return manager;
    }

    private void storeList() {
        int size = this.mruList.size();
        this.prefs.putInt("size", size);
        int i = 0;
        Iterator<String> it = this.mruList.iterator();
        while (it.hasNext()) {
            String text = it.next();
            Preferences preferences = this.prefs;
            int i2 = i;
            i += DEFAULT_AUTO_ACTIVATION_MIN_SIZE;
            preferences.put(Integer.toString(i2), text);
        }
    }

    private void restoreList() {
        int size = this.prefs.getInt("size", 0);
        for (int i = 0; i < size; i += DEFAULT_AUTO_ACTIVATION_MIN_SIZE) {
            String text = this.prefs.get(Integer.toString(i), "");
            if (Strings.isNotBlank(text)) {
                this.mruList.append(text);
            }
        }
    }

    private static Preferences getPreferencesRoot() {
        return Application.getInstance().getContext().getUserPreferences(Suggest.class);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/completion/Suggest$Builder.class */
    public static final class Builder {
        private final Suggest target = new Suggest(Suggest.access$200());

        public Builder preferencesId(String id) {
            this.target.setPreferencesId(id);
            return this;
        }

        public Builder autoActivationMinimumSize(int size) {
            this.target.setAutoActivationMinimumSize(size);
            return this;
        }

        public Builder historyCapacity(int capacity) {
            this.target.setHistoryCapacity(capacity);
            return this;
        }

        public Builder textFilter(Predicate<String> filter) {
            this.target.setTextFilter(filter);
            return this;
        }

        public Builder textSizeFilter(int minimumTextLength) {
            Preconditions.checkArgument(minimumTextLength > Suggest.DEFAULT_AUTO_ACTIVATION_MIN_SIZE, "The minimum text length must be greater than 1.");
            this.target.setTextFilter(str -> {
                return str.length() >= minimumTextLength;
            });
            return this;
        }

        public Builder textConverter(Function<String, String> converter) {
            this.target.setTextConverter(converter);
            return this;
        }

        public Builder displayStringConverter(Function<String, String> converter) {
            this.target.setDisplayStringConverter(converter);
            return this;
        }

        public Builder add(String... suggestions) {
            Preconditions.checkNotNullOrEmpty(suggestions, Messages.MUST_NOT_BE_NULL_OR_EMPTY, "suggestions to add");
            int length = suggestions.length;
            for (int i = 0; i < length; i += Suggest.DEFAULT_AUTO_ACTIVATION_MIN_SIZE) {
                String text = suggestions[i];
                this.target.addText(text);
            }
            return this;
        }

        public Builder install(JGSearchField searchField) {
            build().install(searchField);
            return this;
        }

        public Suggest build() {
            if (this.target.prefs == null) {
                throw new IllegalStateException("You must set a preferences id.");
            }
            return this.target;
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/completion/Suggest$SuggestCompletionProcessor.class */
    private final class SuggestCompletionProcessor implements CompletionProcessor {
        private SuggestCompletionProcessor() {
        }

        public boolean isAutoActivatable(String content, int caretPosition) {
            String trimmedContent = content.trim();
            int length = trimmedContent.length();
            return Suggest.this.autoActivationMinSize <= length;
        }

        public boolean search(String content, int caretPosition, CompletionPublisher publisher, CompletionState state) {
            if (!isAutoActivatable(content, caretPosition)) {
                return false;
            }
            String trimmedContent = content.trim();
            Iterator it = Suggest.this.mruList.iterator();
            while (it.hasNext()) {
                String aText = (String) it.next();
                if (Strings.startsWithIgnoreCase(aText, trimmedContent) && !aText.equalsIgnoreCase(trimmedContent)) {
                    new Completion.Builder().replacementText(aText, new Object[0]).displayString(Suggest.this.convertDisplayString(aText), new Object[0]).publish(publisher);
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/completion/Suggest$MostRecentlyUsedList.class */
    public static final class MostRecentlyUsedList<E> implements Iterable<E> {
        private final List<E> list;
        private int capacity;

        MostRecentlyUsedList(int capacity) {
            this.capacity = capacity;
            this.list = new ArrayList(capacity);
        }

        void add(E e) {
            int index = this.list.indexOf(e);
            if (index != -1) {
                this.list.remove(index);
            }
            this.list.add(0, e);
            if (this.list.size() > this.capacity) {
                this.list.remove(this.capacity);
            }
        }

        void append(E e) {
            this.list.add(e);
        }

        int size() {
            return this.list.size();
        }

        boolean isEmpty() {
            return this.list.isEmpty();
        }

        void clear() {
            this.list.clear();
        }

        @Override // java.lang.Iterable
        public Iterator<E> iterator() {
            List<E> copy = new ArrayList<>(this.list);
            return copy.iterator();
        }
    }
}
