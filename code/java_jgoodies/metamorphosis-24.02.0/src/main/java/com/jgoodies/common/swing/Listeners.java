package com.jgoodies.common.swing;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.Messages;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Consumer;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners.class */
public final class Listeners {
    private static final int ALL_DOWN_MODIFIERS_MASK = 16320;

    private Listeners() {
    }

    public static MouseListener contextMenu(Consumer<MouseEvent> handler) {
        return contextMenu(handler, null);
    }

    public static MouseListener contextMenu(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaContextMenuListener(handler, modifiers);
    }

    public static DocumentListener document(Consumer<DocumentEvent> handler) {
        return new LambdaDocumentListener(handler, LambdaDocumentListener.Type.ALL);
    }

    public static FocusListener focusGained(Consumer<FocusEvent> handler) {
        return new LambdaFocusListener(handler, LambdaFocusListener.Type.FOCUS_GAINED);
    }

    public static FocusListener focusLost(Consumer<FocusEvent> handler) {
        return new LambdaFocusListener(handler, LambdaFocusListener.Type.FOCUS_LOST);
    }

    public static HierarchyListener hierarchyShown(Consumer<HierarchyEvent> handler) {
        return new LambdaHierarchyShowingListener(handler);
    }

    public static HyperlinkListener hyperlinkActivated(Consumer<HyperlinkEvent> handler) {
        return new LambdaHyperlinkActivationListener(handler);
    }

    public static KeyListener keyPressed(Consumer<KeyEvent> handler) {
        return keyPressed(handler, null);
    }

    public static KeyListener keyPressed(Consumer<KeyEvent> handler, String modifiers) {
        return new LambdaKeyListener(handler, LambdaKeyListener.Type.KEY_PRESSED, modifiers);
    }

    public static KeyListener keyTyped(Consumer<KeyEvent> handler) {
        return keyTyped(handler, null);
    }

    public static KeyListener keyTyped(Consumer<KeyEvent> handler, String modifiers) {
        return new LambdaKeyListener(handler, LambdaKeyListener.Type.KEY_TYPED, modifiers);
    }

    public static KeyListener keyReleased(Consumer<KeyEvent> handler) {
        return keyReleased(handler, null);
    }

    public static KeyListener keyReleased(Consumer<KeyEvent> handler, String modifiers) {
        return new LambdaKeyListener(handler, LambdaKeyListener.Type.KEY_RELEASED, modifiers);
    }

    public static ListDataListener listData(Consumer<ListDataEvent> handler) {
        return new LambdaListDataListener(handler, LambdaListDataListener.Type.ALL);
    }

    public static ListDataListener listIntervalAdded(Consumer<ListDataEvent> handler) {
        return new LambdaListDataListener(handler, LambdaListDataListener.Type.INTERVAL_ADDED);
    }

    public static ListDataListener listIntervalRemoved(Consumer<ListDataEvent> handler) {
        return new LambdaListDataListener(handler, LambdaListDataListener.Type.INTERVAL_REMOVED);
    }

    public static ListDataListener listContentsChanged(Consumer<ListDataEvent> handler) {
        return new LambdaListDataListener(handler, LambdaListDataListener.Type.CONTENTS_CHANGED);
    }

    public static MouseListener mouseClicked(Consumer<MouseEvent> handler) {
        return mouseClicked(handler, null);
    }

    public static MouseListener mouseClicked(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_CLICKED, modifiers);
    }

    public static MouseListener mouseDoubleClicked(Consumer<MouseEvent> handler) {
        return mouseDoubleClicked(handler, null);
    }

    public static MouseListener mouseDoubleClicked(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_DOUBLE_CLICKED, modifiers);
    }

    public static MouseMotionListener mouseDragged(Consumer<MouseEvent> handler) {
        return new LambdaMouseMotionListener(handler, LambdaMouseMotionListener.Type.MOUSE_DRAGGED, null);
    }

    public static MouseMotionListener mouseDragged(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseMotionListener(handler, LambdaMouseMotionListener.Type.MOUSE_DRAGGED, modifiers);
    }

    public static MouseListener mouseEntered(Consumer<MouseEvent> handler) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_ENTERED, null);
    }

    public static MouseListener mouseEntered(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_ENTERED, modifiers);
    }

    public static MouseListener mouseExited(Consumer<MouseEvent> handler) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_EXITED, null);
    }

    public static MouseListener mouseExited(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_EXITED, modifiers);
    }

    public static MouseMotionListener mouseMoved(Consumer<MouseEvent> handler) {
        return new LambdaMouseMotionListener(handler, LambdaMouseMotionListener.Type.MOUSE_MOVED, null);
    }

    public static MouseMotionListener mouseMoved(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseMotionListener(handler, LambdaMouseMotionListener.Type.MOUSE_MOVED, modifiers);
    }

    public static MouseListener mousePressed(Consumer<MouseEvent> handler) {
        return mousePressed(handler, null);
    }

    public static MouseListener mousePressed(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_PRESSED, modifiers);
    }

    public static MouseListener mouseReleased(Consumer<MouseEvent> handler) {
        return mouseReleased(handler, null);
    }

    public static MouseListener mouseReleased(Consumer<MouseEvent> handler, String modifiers) {
        return new LambdaMouseListener(handler, LambdaMouseListener.Type.MOUSE_RELEASED, modifiers);
    }

    public static ListSelectionListener nonAdjustingListSelection(ListSelectionListener delegate) {
        return new NonAdjustingListSelectionListener(delegate);
    }

    public static WindowListener windowClosed(Consumer<WindowEvent> handler) {
        return new LambdaWindowListener(handler, LambdaWindowListener.Type.CLOSED);
    }

    public static WindowListener windowClosing(Consumer<WindowEvent> handler) {
        return new LambdaWindowListener(handler, LambdaWindowListener.Type.CLOSING);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$AbstractLambdaListener.class */
    public static abstract class AbstractLambdaListener<E> {
        private final Consumer<E> handler;

        protected AbstractLambdaListener(Consumer<E> handler) {
            this.handler = (Consumer) Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "handler");
        }

        protected final void handleEvent(E event) {
            this.handler.accept(event);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaContextMenuListener.class */
    public static final class LambdaContextMenuListener extends AbstractLambdaListener<MouseEvent> implements MouseListener {
        private final int modifiers;

        LambdaContextMenuListener(Consumer<MouseEvent> handler, String modifiersString) {
            super(handler);
            this.modifiers = Listeners.modifiers(modifiersString);
        }

        public void mousePressed(MouseEvent evt) {
            popupMenuIfTriggered(evt);
        }

        public void mouseReleased(MouseEvent evt) {
            popupMenuIfTriggered(evt);
        }

        public void mouseClicked(MouseEvent evt) {
        }

        public void mouseEntered(MouseEvent evt) {
        }

        public void mouseExited(MouseEvent evt) {
        }

        protected void popupMenuIfTriggered(MouseEvent evt) {
            if (evt.isPopupTrigger() && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaDocumentListener.class */
    private static final class LambdaDocumentListener extends AbstractLambdaListener<DocumentEvent> implements DocumentListener {
        private final Type type;

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaDocumentListener$Type.class */
        private enum Type {
            ALL,
            INSERTED,
            REMOVED,
            CHANGED
        }

        LambdaDocumentListener(Consumer<DocumentEvent> handler, Type type) {
            super(handler);
            this.type = type;
        }

        public void insertUpdate(DocumentEvent evt) {
            if (this.type == Type.ALL || this.type == Type.INSERTED) {
                handleEvent(evt);
            }
        }

        public void removeUpdate(DocumentEvent evt) {
            if (this.type == Type.ALL || this.type == Type.REMOVED) {
                handleEvent(evt);
            }
        }

        public void changedUpdate(DocumentEvent evt) {
            if (this.type == Type.ALL || this.type == Type.CHANGED) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaFocusListener.class */
    private static final class LambdaFocusListener extends AbstractLambdaListener<FocusEvent> implements FocusListener {
        private final Type type;

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaFocusListener$Type.class */
        private enum Type {
            FOCUS_GAINED,
            FOCUS_LOST
        }

        LambdaFocusListener(Consumer<FocusEvent> handler, Type type) {
            super(handler);
            this.type = type;
        }

        public void focusGained(FocusEvent evt) {
            if (this.type == Type.FOCUS_GAINED) {
                handleEvent(evt);
            }
        }

        public void focusLost(FocusEvent evt) {
            if (this.type == Type.FOCUS_LOST) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaHierarchyShowingListener.class */
    private static final class LambdaHierarchyShowingListener extends AbstractLambdaListener<HierarchyEvent> implements HierarchyListener {
        LambdaHierarchyShowingListener(Consumer<HierarchyEvent> handler) {
            super(handler);
        }

        public void hierarchyChanged(HierarchyEvent evt) {
            if (evt.getID() == 1400 && (evt.getChangeFlags() & 4) != 0 && evt.getChanged().isShowing()) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaHyperlinkActivationListener.class */
    private static final class LambdaHyperlinkActivationListener implements HyperlinkListener {
        private final Consumer<HyperlinkEvent> handler;

        LambdaHyperlinkActivationListener(Consumer<HyperlinkEvent> handler) {
            this.handler = (Consumer) Preconditions.checkNotNull(handler, Messages.MUST_NOT_BE_NULL, "hyper link handler");
        }

        public void hyperlinkUpdate(HyperlinkEvent evt) {
            if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                this.handler.accept(evt);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaKeyListener.class */
    public static final class LambdaKeyListener extends AbstractLambdaListener<KeyEvent> implements KeyListener {
        private final Type type;
        private final int modifiers;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaKeyListener$Type.class */
        public enum Type {
            KEY_PRESSED,
            KEY_TYPED,
            KEY_RELEASED
        }

        LambdaKeyListener(Consumer<KeyEvent> handler, Type type, String modifiersString) {
            super(handler);
            this.type = type;
            this.modifiers = Listeners.modifiers(modifiersString);
        }

        public void keyPressed(KeyEvent evt) {
            if (!evt.isConsumed() && this.type == Type.KEY_PRESSED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }

        public void keyTyped(KeyEvent evt) {
            if (!evt.isConsumed() && this.type == Type.KEY_TYPED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }

        public void keyReleased(KeyEvent evt) {
            if (!evt.isConsumed() && this.type == Type.KEY_RELEASED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaListDataListener.class */
    private static final class LambdaListDataListener extends AbstractLambdaListener<ListDataEvent> implements ListDataListener {
        private final Type type;

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaListDataListener$Type.class */
        private enum Type {
            ALL,
            INTERVAL_ADDED,
            INTERVAL_REMOVED,
            CONTENTS_CHANGED
        }

        LambdaListDataListener(Consumer<ListDataEvent> handler, Type type) {
            super(handler);
            this.type = type;
        }

        public void intervalAdded(ListDataEvent evt) {
            if (this.type == Type.ALL || this.type == Type.INTERVAL_ADDED) {
                handleEvent(evt);
            }
        }

        public void intervalRemoved(ListDataEvent evt) {
            if (this.type == Type.ALL || this.type == Type.INTERVAL_REMOVED) {
                handleEvent(evt);
            }
        }

        public void contentsChanged(ListDataEvent evt) {
            if (this.type == Type.ALL || this.type == Type.CONTENTS_CHANGED) {
                handleEvent(evt);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaMouseListener.class */
    public static final class LambdaMouseListener extends AbstractLambdaListener<MouseEvent> implements MouseListener {
        private final Type type;
        private final int modifiers;

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaMouseListener$Type.class */
        public enum Type {
            MOUSE_CLICKED,
            MOUSE_DOUBLE_CLICKED,
            MOUSE_PRESSED,
            MOUSE_RELEASED,
            MOUSE_ENTERED,
            MOUSE_EXITED
        }

        LambdaMouseListener(Consumer<MouseEvent> handler, Type type, String modifiersString) {
            super(handler);
            this.type = type;
            this.modifiers = Listeners.modifiers(modifiersString);
        }

        public void mouseClicked(MouseEvent evt) {
            if (evt.isConsumed()) {
                return;
            }
            if (this.type == Type.MOUSE_CLICKED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            } else if (this.type == Type.MOUSE_DOUBLE_CLICKED && SwingUtilities.isLeftMouseButton(evt) && evt.getClickCount() == 2 && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }

        public void mousePressed(MouseEvent evt) {
            if (!evt.isConsumed() && this.type == Type.MOUSE_PRESSED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }

        public void mouseReleased(MouseEvent evt) {
            if (!evt.isConsumed() && this.type == Type.MOUSE_RELEASED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }

        public void mouseEntered(MouseEvent evt) {
            if (!evt.isConsumed() && this.type == Type.MOUSE_ENTERED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }

        public void mouseExited(MouseEvent evt) {
            if (!evt.isConsumed() && this.type == Type.MOUSE_EXITED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaMouseMotionListener.class */
    private static final class LambdaMouseMotionListener extends AbstractLambdaListener<MouseEvent> implements MouseMotionListener {
        private final Type type;
        private final int modifiers;

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaMouseMotionListener$Type.class */
        private enum Type {
            MOUSE_DRAGGED,
            MOUSE_MOVED
        }

        LambdaMouseMotionListener(Consumer<MouseEvent> handler, Type type, String modifiersString) {
            super(handler);
            this.type = type;
            this.modifiers = Listeners.modifiers(modifiersString);
        }

        public void mouseDragged(MouseEvent evt) {
            if (!evt.isConsumed() && this.type == Type.MOUSE_DRAGGED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }

        public void mouseMoved(MouseEvent evt) {
            if (!evt.isConsumed() && this.type == Type.MOUSE_MOVED && Listeners.matches(evt, this.modifiers)) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaWindowListener.class */
    private static final class LambdaWindowListener extends AbstractLambdaListener<WindowEvent> implements WindowListener {
        private final Type type;

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$LambdaWindowListener$Type.class */
        private enum Type {
            OPENED,
            CLOSING,
            CLOSED,
            ICONIFIED,
            DEICONIFIED,
            ACTIVATED,
            DEACTIVATED
        }

        LambdaWindowListener(Consumer<WindowEvent> handler, Type type) {
            super(handler);
            this.type = type;
        }

        public void windowOpened(WindowEvent evt) {
            if (this.type == Type.OPENED) {
                handleEvent(evt);
            }
        }

        public void windowClosing(WindowEvent evt) {
            if (this.type == Type.CLOSING) {
                handleEvent(evt);
            }
        }

        public void windowClosed(WindowEvent evt) {
            if (this.type == Type.CLOSED) {
                handleEvent(evt);
            }
        }

        public void windowIconified(WindowEvent evt) {
            if (this.type == Type.ICONIFIED) {
                handleEvent(evt);
            }
        }

        public void windowDeiconified(WindowEvent evt) {
            if (this.type == Type.DEICONIFIED) {
                handleEvent(evt);
            }
        }

        public void windowActivated(WindowEvent evt) {
            if (this.type == Type.ACTIVATED) {
                handleEvent(evt);
            }
        }

        public void windowDeactivated(WindowEvent evt) {
            if (this.type == Type.DEACTIVATED) {
                handleEvent(evt);
            }
        }
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/swing/Listeners$NonAdjustingListSelectionListener.class */
    private static final class NonAdjustingListSelectionListener implements ListSelectionListener {
        private final ListSelectionListener delegate;

        NonAdjustingListSelectionListener(ListSelectionListener delegate) {
            this.delegate = (ListSelectionListener) Preconditions.checkNotNull(delegate, Messages.MUST_NOT_BE_NULL, "list selection listener");
        }

        public void valueChanged(ListSelectionEvent evt) {
            if (!evt.getValueIsAdjusting()) {
                this.delegate.valueChanged(evt);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int modifiers(String modifiersText) {
        if (modifiersText == null) {
            return 0;
        }
        KeyStroke keyStroke = KeyStroke.getKeyStroke(modifiersText + " K");
        if (keyStroke == null) {
            throw new IllegalArgumentException(Strings.get("Invalid modifiers text \"%s\".\nThe syntax is: (shift | control | ctrl | meta | alt | altGraph)*\nExamples: \"ctrl\", \"shift ctrl\", \"control alt\".", modifiersText));
        }
        return keyStroke.getModifiers() & ALL_DOWN_MODIFIERS_MASK;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean matches(InputEvent evt, int modifiers) {
        return (evt.getModifiersEx() & modifiers) == modifiers;
    }
}
