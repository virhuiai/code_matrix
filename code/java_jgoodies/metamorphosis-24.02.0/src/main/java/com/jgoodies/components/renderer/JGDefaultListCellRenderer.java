package com.jgoodies.components.renderer;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import com.jgoodies.common.swing.ScreenScaling;
import java.awt.Component;
import java.util.function.BiConsumer;
import java.util.function.Function;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/renderer/JGDefaultListCellRenderer.class */
public class JGDefaultListCellRenderer<V> implements ListCellRenderer<V> {
    private final DefaultListCellRenderer delegate;
    private final RenderContext<V> context;
    private final BiConsumer<V, RenderContext<V>> valueSetter;

    public JGDefaultListCellRenderer() {
        this(createDefaultValueSetter());
    }

    public JGDefaultListCellRenderer(Function<V, String> displayStringFunction) {
        this((value, context) -> {
            context.setText((String) displayStringFunction.apply(value), new Object[0]);
        });
    }

    public JGDefaultListCellRenderer(BiConsumer<V, RenderContext<V>> valueSetter) {
        this.delegate = createDelegate();
        this.context = new RenderContext<>(this);
        this.valueSetter = (BiConsumer) Preconditions.checkNotNull(valueSetter, Messages.MUST_NOT_BE_NULL, "value setter");
    }

    private static DefaultListCellRenderer createDelegate() {
        DefaultListCellRenderer renderer = new DefaultListCellRenderer();
        renderer.setIconTextGap(ScreenScaling.toPhysical(renderer.getIconTextGap()));
        return renderer;
    }

    private static <V> BiConsumer<V, RenderContext<V>> createDefaultValueSetter() {
        return (value, context) -> {
            if (value instanceof Icon) {
                context.setIcon((Icon) value);
                context.setText("", new Object[0]);
            } else {
                context.setIcon(null);
                context.setText(value.toString(), new Object[0]);
            }
        };
    }

    public Component getListCellRendererComponent(JList<? extends V> list, V value, int index, boolean isSelected, boolean cellHasFocus) {
        this.delegate.getListCellRendererComponent(list, (Object) null, index, isSelected, cellHasFocus);
        if (value != null) {
            setValue(value);
        }
        return this.delegate;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setValue(V value) {
        this.valueSetter.accept(value, this.context);
    }

    protected final void setIcon(Icon icon) {
        this.delegate.setIcon(icon);
    }

    protected final void setText(String text, Object... args) {
        this.delegate.setText(Strings.get(text, args));
    }

    protected final void setToolTipText(String text, Object... args) {
        this.delegate.setToolTipText(Strings.get(text, args));
    }

    protected final void setHorizontalAlignment(int alignment) {
        this.delegate.setHorizontalAlignment(alignment);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/components/renderer/JGDefaultListCellRenderer$RenderContext.class */
    public static final class RenderContext<E> {
        private final JGDefaultListCellRenderer<E> renderer;

        RenderContext(JGDefaultListCellRenderer<E> renderer) {
            this.renderer = renderer;
        }

        public void setIcon(Icon icon) {
            this.renderer.setIcon(icon);
        }

        public void setText(String text, Object... args) {
            this.renderer.setText(text, args);
        }

        public void setToolTipText(String text, Object... args) {
            this.renderer.setToolTipText(text, args);
        }

        public void setHorizontalAlignment(int alignment) {
            this.renderer.setHorizontalAlignment(alignment);
        }
    }
}
