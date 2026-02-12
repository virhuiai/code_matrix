package com.jgoodies.framework.search;

import com.jgoodies.animation.swing.components.AnimatedLabel;
import com.jgoodies.application.Application;
import com.jgoodies.application.ResourceMap;
import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.binder.BinderUtils;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.components.JGTextField;
import com.jgoodies.framework.search.FieldSearchModel;
import com.jgoodies.search.CompletionManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Icon;
import javax.swing.JTextField;
import javax.swing.Timer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchBindings.class */
public final class FieldSearchBindings {
    private static final ResourceMap RESOURCES = Application.getResourceMap(FieldSearchBindings.class);
    private static final Icon[] BUSY_ICONS = BusyIcons.getIcons();

    private FieldSearchBindings() {
    }

    public static void bind(JGTextField field, PresentationModel<?> model, String propertyName, CompletionManager manager) {
        bind(field, model.getComponentModel(propertyName), manager);
        BinderUtils.setValidationMessageKey(field, propertyName);
    }

    public static void bind(JGTextField field, ValueModel valueModel, CompletionManager manager) {
        bind(field, valueModel, manager, FieldSearchModel.InvalidTextBehavior.PERSIST);
    }

    public static void bind(JGTextField field, ValueModel valueModel, CompletionManager manager, FieldSearchModel.InvalidTextBehavior invalidTextBehavior) {
        bind(field, new FieldSearchModel(valueModel, manager, invalidTextBehavior));
    }

    public static void bind(JGTextField field, FieldSearchModel<?> searchModel) {
        Bindings.bind((JTextField) field, searchModel.getTextModel(), false);
        Bindings.addComponentPropertyHandler(field, searchModel.getValueModel());
        FieldSearchModel.setFindModel(field, searchModel);
        field.addFocusListener(FieldSearchModel.HANDLER);
        field.addKeyListener(FieldSearchModel.HANDLER);
        searchModel.getManager().install(field);
        searchModel.addPropertyChangeListener(new Handler(field));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchBindings$Handler.class */
    public static final class Handler implements PropertyChangeListener {
        private final JGTextField field;
        private final Animator animator;

        Handler(JGTextField field) {
            this.field = field;
            this.animator = new Animator(field, FieldSearchBindings.RESOURCES.getInt("FieldSearch.animation.frameRate"), FieldSearchBindings.RESOURCES.getInt("FieldSearch.animation.initialDelay"));
        }

        @Override // java.beans.PropertyChangeListener
        public void propertyChange(PropertyChangeEvent evt) {
            FieldSearchModel<?> model = (FieldSearchModel) evt.getSource();
            String propertyName = evt.getPropertyName();
            boolean z = -1;
            switch (propertyName.hashCode()) {
                case -1046442289:
                    if (propertyName.equals(FieldSearchModel.PROPERTY_TEXT_VALID)) {
                        z = true;
                        break;
                    }
                    break;
                case 1778217274:
                    if (propertyName.equals(FieldSearchModel.PROPERTY_SEARCHING)) {
                        z = false;
                        break;
                    }
                    break;
            }
            switch (z) {
                case AnimatedLabel.CENTER /* 0 */:
                    if (model.isSearching()) {
                        this.animator.start();
                        return;
                    } else {
                        this.animator.stop();
                        return;
                    }
                case true:
                    this.field.setErrorUnderlinePainted(!model.isTextValid());
                    return;
                default:
                    return;
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchBindings$Animator.class */
    private static final class Animator implements ActionListener {
        private final JGTextField field;
        private final Timer timer;
        private int iconIndex = 0;
        private Icon oldIcon;
        private boolean oldIconVisibleAlways;

        Animator(JGTextField field, int frameRate, int initialDelay) {
            this.field = field;
            int delay = 1000 / frameRate;
            this.timer = new Timer(delay, this);
            this.timer.setInitialDelay(initialDelay);
        }

        void start() {
            this.oldIcon = this.field.getIcon();
            this.oldIconVisibleAlways = this.field.isIconVisibleAlways();
            this.field.setIconVisibleAlways(true);
            this.timer.start();
        }

        void stop() {
            this.timer.stop();
            this.field.setIcon(this.oldIcon);
            this.field.setIconVisibleAlways(this.oldIconVisibleAlways);
        }

        public void actionPerformed(ActionEvent e) {
            this.field.setIcon(FieldSearchBindings.BUSY_ICONS[this.iconIndex]);
            this.iconIndex = (this.iconIndex + 1) % FieldSearchBindings.BUSY_ICONS.length;
        }
    }
}
