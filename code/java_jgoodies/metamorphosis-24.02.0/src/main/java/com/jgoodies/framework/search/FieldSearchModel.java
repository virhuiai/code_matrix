package com.jgoodies.framework.search;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.bean.Bean;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.framework.util.Background;
import com.jgoodies.search.CompletionApplicationEvent;
import com.jgoodies.search.CompletionApplicationListener;
import com.jgoodies.search.CompletionManager;
import com.jgoodies.search.CompletionProcessEvent;
import com.jgoodies.search.CompletionProcessListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.EventObject;
import javax.swing.text.JTextComponent;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchModel.class */
public class FieldSearchModel<V> extends Bean {
    public static final String PROPERTY_TEXT_VALID = "textValid";
    public static final String PROPERTY_SEARCHING = "searching";
    private static final String KEY_FIND_MODEL = "com.jgoodies.framework.search.FieldSearchModel";
    public static final EscapeAndFocusHandler<?> HANDLER = new EscapeAndFocusHandler<>(null);
    private final ValueModel valueModel;
    private final ValueModel textModel;
    private final CompletionManager manager;
    protected final InvalidTextBehavior invalidTextBehavior;
    protected final PropertyChangeListener valueChangeHandler;
    protected final PropertyChangeListener textChangeHandler;
    private boolean searching;
    private boolean textValid;
    protected PendingEditBehavior pendingEditBehavior;
    protected V oldValue;

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchModel$InvalidTextBehavior.class */
    public enum InvalidTextBehavior {
        PERSIST,
        REVERT,
        NULL
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchModel$PendingEditBehavior.class */
    public enum PendingEditBehavior {
        SEARCH_VALUE,
        UPDATE_TEXT,
        HANDLE_INVALID_TEXT,
        NONE
    }

    public FieldSearchModel(ValueModel valueModel, CompletionManager manager) {
        this(valueModel, manager, InvalidTextBehavior.PERSIST);
    }

    public FieldSearchModel(ValueModel valueModel, CompletionManager manager, InvalidTextBehavior invalidTextBehavior) {
        Preconditions.checkNotNull(valueModel, Messages.MUST_NOT_BE_NULL, "valueModel");
        Preconditions.checkNotNull(manager, Messages.MUST_NOT_BE_NULL, "completion manager");
        Preconditions.checkNotNull(invalidTextBehavior, Messages.MUST_NOT_BE_NULL, "invalidTextBehavior");
        Preconditions.checkArgument(manager.getProcessor() instanceof FieldSearchProcessor, "The CompletionManager's CompletionProcessor must implement the FieldSearchProcessor interface.");
        this.valueModel = valueModel;
        this.manager = manager;
        this.invalidTextBehavior = invalidTextBehavior;
        this.textModel = new ValueHolder();
        this.valueChangeHandler = this::onValueChange;
        this.textChangeHandler = this::onTextChange;
        this.searching = false;
        this.textValid = true;
        this.oldValue = getValue();
        initEventHandling();
    }

    public static <T> FieldSearchModel<T> getFindModel(JTextComponent c) {
        return (FieldSearchModel) c.getClientProperty(KEY_FIND_MODEL);
    }

    public static void setFindModel(JTextComponent c, FieldSearchModel<?> model) {
        c.putClientProperty(KEY_FIND_MODEL, model);
    }

    public CompletionManager getManager() {
        return this.manager;
    }

    public FieldSearchProcessor<V> getProcessor() {
        return (FieldSearchProcessor) getManager().getProcessor();
    }

    public ValueModel getTextModel() {
        return this.textModel;
    }

    public String getText() {
        return (String) this.textModel.getValue();
    }

    public void setText(String newText) {
        this.textModel.setValue(newText);
    }

    public ValueModel getValueModel() {
        return this.valueModel;
    }

    public V getValue() {
        return (V) this.valueModel.getValue();
    }

    public void setValue(V newValue) {
        this.valueModel.setValue(newValue);
    }

    public boolean isSearching() {
        return this.searching;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setSearching(boolean b) {
        boolean oldSearching = isSearching();
        this.searching = b;
        firePropertyChange(PROPERTY_SEARCHING, oldSearching, b);
    }

    public boolean isTextValid() {
        return this.textValid;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setTextValid(boolean b) {
        boolean oldTextValid = isTextValid();
        this.textValid = b;
        firePropertyChange(PROPERTY_TEXT_VALID, oldTextValid, b);
    }

    private void initEventHandling() {
        this.valueModel.addValueChangeListener(this.valueChangeHandler);
        this.textModel.addValueChangeListener(this.textChangeHandler);
        CompletionHandler<V> handler = new CompletionHandler<>(this);
        this.manager.addCompletionApplicationListener(handler);
        this.manager.addCompletionProcessListener(handler);
        updateText();
    }

    protected void setValueSilently(V value) {
        this.valueModel.removeValueChangeListener(this.valueChangeHandler);
        try {
            setValue(value);
        } finally {
            this.valueModel.addValueChangeListener(this.valueChangeHandler);
        }
    }

    protected void updateText() {
        this.textModel.removeValueChangeListener(this.textChangeHandler);
        try {
            setText(getProcessor().getDisplayString(getValue()));
            setTextValid(true);
            this.pendingEditBehavior = PendingEditBehavior.NONE;
        } finally {
            this.textModel.addValueChangeListener(this.textChangeHandler);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    public void updateValue() {
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$framework$search$FieldSearchModel$PendingEditBehavior[this.pendingEditBehavior.ordinal()]) {
            case 1:
                return;
            case AnimatedLabel.LEFT /* 2 */:
                if (representsValue(getText(), getValue())) {
                    updateText();
                    return;
                }
                setSearching(true);
                commitSearchValue(Background.get(() -> {
                    return getProcessor().searchValue(getText());
                }));
                setSearching(false);
                return;
            case 3:
                updateText();
                return;
            case AnimatedLabel.RIGHT /* 4 */:
                handleInvalidText();
                return;
            default:
                throw new IllegalStateException("Unknown FocusLostBehavior: " + this.pendingEditBehavior);
        }
    }

    protected void commitValueForCompletion(V value) {
        setValueSilently(value);
        this.oldValue = value;
        this.pendingEditBehavior = PendingEditBehavior.UPDATE_TEXT;
    }

    protected void commitSearchValue(V value) {
        boolean validText = value != null || representsValue(getText(), null);
        if (validText) {
            setValueSilently(value);
            updateText();
        } else {
            handleInvalidText();
        }
    }

    protected void handleInvalidText() {
        switch (AnonymousClass1.$SwitchMap$com$jgoodies$framework$search$FieldSearchModel$InvalidTextBehavior[this.invalidTextBehavior.ordinal()]) {
            case 1:
                revertValue(null);
                return;
            case AnimatedLabel.LEFT /* 2 */:
                setValueSilently(null);
                setTextValid(false);
                return;
            case 3:
                setValue(null);
                return;
            default:
                throw new IllegalStateException("Unknown InvalidTextBehavior: " + this.invalidTextBehavior);
        }
    }

    protected boolean revertValue(JTextComponent c) {
        if (this.oldValue == getValue()) {
            return false;
        }
        setValueSilently(this.oldValue);
        if (representsValue(getText(), this.oldValue)) {
            setTextValid(true);
            return true;
        }
        if (c == null) {
            updateText();
            return true;
        }
        getManager().deactivate();
        try {
            updateText();
            c.selectAll();
            return true;
        } finally {
            getManager().activate(c);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean representsValue(String text, V value) {
        return getProcessor().represents(text, value);
    }

    private void onValueChange(PropertyChangeEvent evt) {
        this.oldValue = getValue();
        updateText();
    }

    private void onTextChange(PropertyChangeEvent evt) {
        setTextValid(true);
        this.pendingEditBehavior = PendingEditBehavior.SEARCH_VALUE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchModel$EscapeAndFocusHandler.class */
    public static final class EscapeAndFocusHandler<T> implements FocusListener, KeyListener {
        private EscapeAndFocusHandler() {
        }

        /* synthetic */ EscapeAndFocusHandler(AnonymousClass1 x0) {
            this();
        }

        public void focusGained(FocusEvent focusEvent) {
            if (!(focusEvent.getOppositeComponent() == null)) {
                FieldSearchModel findModel = getFindModel(focusEvent);
                findModel.oldValue = (T) findModel.getValue();
            }
        }

        public void focusLost(FocusEvent e) {
            FieldSearchModel<T> model = getFindModel(e);
            model.getManager().onFocusLost();
            model.updateValue();
        }

        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == 27) {
                boolean changed = getFindModel(e).revertValue((JTextComponent) e.getSource());
                if (changed) {
                    e.consume();
                }
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void keyTyped(KeyEvent e) {
        }

        private static <T> FieldSearchModel<T> getFindModel(EventObject e) {
            JTextComponent c = (JTextComponent) e.getSource();
            return FieldSearchModel.getFindModel(c);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchModel$CompletionHandler.class */
    public static final class CompletionHandler<V> implements CompletionApplicationListener, CompletionProcessListener {
        private final FieldSearchModel<V> model;

        CompletionHandler(FieldSearchModel<V> model) {
            this.model = model;
        }

        /* JADX WARN: Multi-variable type inference failed */
        public void completionApplied(CompletionApplicationEvent completionApplicationEvent) {
            JTextComponent textComponent = completionApplicationEvent.getTextComponent();
            FieldSearchModel<V> findModel = FieldSearchModel.getFindModel(textComponent);
            Preconditions.checkState(findModel != null, "Missing model for field: %s", textComponent);
            if (this.model == findModel) {
                this.model.setSearching(true);
                Object obj = Background.get(() -> {
                    return this.model.getProcessor().valueFor(completionApplicationEvent.getCompletion());
                });
                this.model.setSearching(false);
                this.model.commitValueForCompletion(obj);
            }
        }

        public boolean searchAllowed(JTextComponent c) {
            FieldSearchModel<V> findModel = FieldSearchModel.getFindModel(c);
            Preconditions.checkState(findModel != null, "Missing model for field: %s", c);
            if (this.model == findModel && !Strings.isBlank(this.model.getText())) {
                boolean valueAlreadyPresented = this.model.representsValue(this.model.getText(), this.model.getValue()) && (this.model.pendingEditBehavior == PendingEditBehavior.UPDATE_TEXT || this.model.pendingEditBehavior == PendingEditBehavior.NONE);
                return !valueAlreadyPresented;
            }
            return true;
        }

        public void completionProcessed(CompletionProcessEvent event) {
            JTextComponent component = event.getTextComponent();
            FieldSearchModel<V> findModel = FieldSearchModel.getFindModel(component);
            Preconditions.checkState(findModel != null, "Missing model for field: %s", component);
            if (this.model == findModel) {
                switch (AnonymousClass1.$SwitchMap$com$jgoodies$search$CompletionProcessEvent$State[event.getState().ordinal()]) {
                    case 1:
                        this.model.setSearching(true);
                        return;
                    case AnimatedLabel.LEFT /* 2 */:
                        this.model.setSearching(false);
                        this.model.pendingEditBehavior = PendingEditBehavior.SEARCH_VALUE;
                        return;
                    case 3:
                        this.model.setSearching(false);
                        if (event.hasCompletions()) {
                            this.model.setTextValid(true);
                            this.model.pendingEditBehavior = PendingEditBehavior.SEARCH_VALUE;
                            return;
                        } else {
                            this.model.setTextValid(false);
                            this.model.setValueSilently(null);
                            this.model.pendingEditBehavior = PendingEditBehavior.HANDLE_INVALID_TEXT;
                            return;
                        }
                    default:
                        throw new IllegalStateException("Unknow completion process state: " + event.getState());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.jgoodies.framework.search.FieldSearchModel$1, reason: invalid class name */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchModel$1.class */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$PendingEditBehavior;
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$InvalidTextBehavior;
        static final /* synthetic */ int[] $SwitchMap$com$jgoodies$search$CompletionProcessEvent$State = new int[CompletionProcessEvent.State.values().length];

        static {
            try {
                $SwitchMap$com$jgoodies$search$CompletionProcessEvent$State[CompletionProcessEvent.State.STARTED.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                $SwitchMap$com$jgoodies$search$CompletionProcessEvent$State[CompletionProcessEvent.State.CANCELLED.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                $SwitchMap$com$jgoodies$search$CompletionProcessEvent$State[CompletionProcessEvent.State.SUCCEEDED.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$InvalidTextBehavior = new int[InvalidTextBehavior.values().length];
            try {
                $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$InvalidTextBehavior[InvalidTextBehavior.REVERT.ordinal()] = 1;
            } catch (NoSuchFieldError e4) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$InvalidTextBehavior[InvalidTextBehavior.PERSIST.ordinal()] = 2;
            } catch (NoSuchFieldError e5) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$InvalidTextBehavior[InvalidTextBehavior.NULL.ordinal()] = 3;
            } catch (NoSuchFieldError e6) {
            }
            $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$PendingEditBehavior = new int[PendingEditBehavior.values().length];
            try {
                $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$PendingEditBehavior[PendingEditBehavior.NONE.ordinal()] = 1;
            } catch (NoSuchFieldError e7) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$PendingEditBehavior[PendingEditBehavior.SEARCH_VALUE.ordinal()] = 2;
            } catch (NoSuchFieldError e8) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$PendingEditBehavior[PendingEditBehavior.UPDATE_TEXT.ordinal()] = 3;
            } catch (NoSuchFieldError e9) {
            }
            try {
                $SwitchMap$com$jgoodies$framework$search$FieldSearchModel$PendingEditBehavior[PendingEditBehavior.HANDLE_INVALID_TEXT.ordinal()] = 4;
            } catch (NoSuchFieldError e10) {
            }
        }
    }
}
