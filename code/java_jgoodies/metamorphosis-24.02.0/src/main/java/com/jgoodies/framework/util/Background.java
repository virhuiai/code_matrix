package com.jgoodies.framework.util;

import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/Background.class */
public final class Background {
    private Background() {
    }

    public static <T> void accept(Consumer<T> consumer, T argument) {
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();
        BackgroundConsumer<T> background = new BackgroundConsumer<>(secondaryLoop, consumer, argument);
        Thread thread = new Thread(background, "BackgroundConsumer -" + consumer.getClass().getSimpleName());
        thread.start();
        secondaryLoop.enter();
        if (((BackgroundConsumer) background).exception == null) {
        } else {
            throw ((BackgroundConsumer) background).exception;
        }
    }

    public static <T, R> R apply(Function<T, R> function, T t) {
        SecondaryLoop createSecondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
        BackgroundFunction backgroundFunction = new BackgroundFunction(createSecondaryLoop, function, t);
        new Thread(backgroundFunction, "BackgroundFunction -" + function.getClass().getSimpleName()).start();
        createSecondaryLoop.enter();
        if (backgroundFunction.exception != null) {
            throw backgroundFunction.exception;
        }
        return (R) backgroundFunction.result;
    }

    public static <R> R call(Callable<R> callable) throws Exception {
        SecondaryLoop createSecondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
        BackgroundCallable backgroundCallable = new BackgroundCallable(createSecondaryLoop, callable);
        new Thread(backgroundCallable, "BackgroundCallable: " + callable.getClass().getSimpleName()).start();
        createSecondaryLoop.enter();
        if (backgroundCallable.exception != null) {
            throw backgroundCallable.exception;
        }
        return (R) backgroundCallable.result;
    }

    public static <T> boolean test(Predicate<T> predicate, T argument) {
        EventQueue eventQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        SecondaryLoop secondaryLoop = eventQueue.createSecondaryLoop();
        BackgroundPredicate<T> background = new BackgroundPredicate<>(secondaryLoop, predicate, argument);
        Thread thread = new Thread(background, "BackgroundPredicate -" + predicate.getClass().getSimpleName());
        thread.start();
        secondaryLoop.enter();
        if (((BackgroundPredicate) background).exception != null) {
            throw ((BackgroundPredicate) background).exception;
        }
        return ((BackgroundPredicate) background).result;
    }

    public static <R> R get(Supplier<R> supplier) {
        SecondaryLoop createSecondaryLoop = Toolkit.getDefaultToolkit().getSystemEventQueue().createSecondaryLoop();
        BackgroundSupplier backgroundSupplier = new BackgroundSupplier(createSecondaryLoop, supplier);
        new Thread(backgroundSupplier, "BackgroundSupplier -" + supplier.getClass().getSimpleName()).start();
        createSecondaryLoop.enter();
        if (backgroundSupplier.exception != null) {
            throw backgroundSupplier.exception;
        }
        return (R) backgroundSupplier.result;
    }

    public static <T> Consumer<T> toBackground(Consumer<T> consumer) {
        return t -> {
            accept(consumer, t);
        };
    }

    public static <T, R> Function<T, R> toBackground(Function<T, R> function) {
        return t -> {
            return apply(function, t);
        };
    }

    public static <R> Callable<R> toBackground(Callable<R> callable) {
        return () -> {
            return call(callable);
        };
    }

    public static <T> Predicate<T> toBackground(Predicate<T> predicate) {
        return t -> {
            return test(predicate, t);
        };
    }

    public static <R> Supplier<R> toBackground(Supplier<R> supplier) {
        return () -> {
            return get(supplier);
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/Background$BackgroundCallable.class */
    public static final class BackgroundCallable<R> implements Runnable {
        private final SecondaryLoop loop;
        private final Callable<R> delegate;
        private R result;
        private Exception exception;

        private BackgroundCallable(SecondaryLoop loop, Callable<R> delegate) {
            this.loop = loop;
            this.delegate = delegate;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.result = this.delegate.call();
            } catch (Exception ex) {
                this.exception = ex;
                this.result = null;
            } finally {
                this.loop.exit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/Background$BackgroundConsumer.class */
    public static final class BackgroundConsumer<T> implements Runnable {
        private final SecondaryLoop loop;
        private final Consumer<T> delegate;
        private final T argument;
        private RuntimeException exception;

        private BackgroundConsumer(SecondaryLoop loop, Consumer<T> delegate, T argument) {
            this.loop = loop;
            this.delegate = delegate;
            this.argument = argument;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.delegate.accept(this.argument);
            } catch (RuntimeException ex) {
                this.exception = ex;
            } finally {
                this.loop.exit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/Background$BackgroundFunction.class */
    public static final class BackgroundFunction<T, R> implements Runnable {
        private final SecondaryLoop loop;
        private final Function<T, R> delegate;
        private final T argument;
        private R result;
        private RuntimeException exception;

        private BackgroundFunction(SecondaryLoop loop, Function<T, R> delegate, T argument) {
            this.loop = loop;
            this.delegate = delegate;
            this.argument = argument;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.result = this.delegate.apply(this.argument);
            } catch (RuntimeException ex) {
                this.exception = ex;
                this.result = null;
            } finally {
                this.loop.exit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/Background$BackgroundPredicate.class */
    public static final class BackgroundPredicate<T> implements Runnable {
        private final SecondaryLoop loop;
        private final Predicate<T> delegate;
        private final T argument;
        private boolean result;
        private RuntimeException exception;

        private BackgroundPredicate(SecondaryLoop loop, Predicate<T> delegate, T argument) {
            this.loop = loop;
            this.delegate = delegate;
            this.argument = argument;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.result = this.delegate.test(this.argument);
            } catch (RuntimeException ex) {
                this.exception = ex;
            } finally {
                this.loop.exit();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/util/Background$BackgroundSupplier.class */
    public static final class BackgroundSupplier<R> implements Runnable {
        private final SecondaryLoop loop;
        private final Supplier<R> delegate;
        private R result;
        private RuntimeException exception;

        private BackgroundSupplier(SecondaryLoop loop, Supplier<R> delegate) {
            this.loop = loop;
            this.delegate = delegate;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                this.result = this.delegate.get();
            } catch (RuntimeException ex) {
                this.exception = ex;
                this.result = null;
            } finally {
                this.loop.exit();
            }
        }
    }
}
