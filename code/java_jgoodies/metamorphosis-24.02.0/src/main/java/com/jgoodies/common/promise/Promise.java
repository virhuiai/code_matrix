package com.jgoodies.common.promise;

import com.jgoodies.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/promise/Promise.class */
public interface Promise<T> {

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/promise/Promise$State.class */
    public enum State {
        PENDING,
        FULFILLED,
        REJECTED
    }

    State getState();

    T get();

    Promise<T> resolve(T t);

    <U> Promise<U> thenApply(Function<? super T, ? extends U> function);

    Promise<Void> thenAccept(Consumer<? super T> consumer);

    Promise<Void> thenRun(Runnable runnable);

    <U> Promise<U> thenCompose(Function<? super T, ? extends Promise<U>> function);

    default boolean isPending() {
        return getState() == State.PENDING;
    }

    default boolean isFulfilled() {
        return getState() == State.FULFILLED;
    }

    static <U> Promise<U> pending() {
        return new DefaultPromise();
    }

    static <U> Promise<U> of(U value) {
        return new DefaultPromise(value);
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/promise/Promise$DefaultPromise.class */
    public static final class DefaultPromise<T> implements Promise<T> {
        private final List<PromiseWithResolution<T>> chain = new ArrayList();
        private State state = State.PENDING;
        private T result;

        DefaultPromise() {
        }

        DefaultPromise(T value) {
            resolve(value);
        }

        @Override // com.jgoodies.common.promise.Promise
        public State getState() {
            return this.state;
        }

        @Override // com.jgoodies.common.promise.Promise
        public T get() {
            Preconditions.checkState(isFulfilled(), "This Promise must be fulfilled to provide a result.");
            return this.result;
        }

        @Override // com.jgoodies.common.promise.Promise
        public Promise<T> resolve(T value) {
            Preconditions.checkState(isPending(), "Cannot resolve, because this Promise has been resolved before: " + this);
            this.state = State.FULFILLED;
            this.result = value;
            handleSuccess(value);
            return this;
        }

        @Override // com.jgoodies.common.promise.Promise
        public <U> Promise<U> thenApply(Function<? super T, ? extends U> fn) {
            DefaultPromise<U> promise = new DefaultPromise<>();
            add(promise, (promise2, value) -> {
                promise2.resolve(fn.apply(value));
            });
            if (isFulfilled()) {
                handleSuccess(this.result);
            }
            return promise;
        }

        @Override // com.jgoodies.common.promise.Promise
        public Promise<Void> thenAccept(Consumer<? super T> action) {
            return thenApply(result -> {
                action.accept(result);
                return null;
            });
        }

        @Override // com.jgoodies.common.promise.Promise
        public Promise<Void> thenRun(Runnable action) {
            return thenAccept(result -> {
                action.run();
            });
        }

        @Override // com.jgoodies.common.promise.Promise
        public <U> Promise<U> thenCompose(Function<? super T, ? extends Promise<U>> fn) {
            DefaultPromise<U> promise = new DefaultPromise<>();
            add(promise, (next, value) -> {
                Promise promise2 = (Promise) fn.apply(value);
                next.getClass();
                promise2.thenAccept(next::resolve);
            });
            if (isFulfilled()) {
                handleSuccess(this.result);
            }
            return promise;
        }

        public String toString() {
            return String.format("DefaultPromise@%s [state=%s, result=%s, chainCount=%s]", Integer.toHexString(hashCode()), getState(), this.result, Integer.valueOf(this.chain.size()));
        }

        private void add(Promise<?> promise, BiConsumer<Promise, ? super T> successHandler) {
            this.chain.add(new PromiseWithResolution<>(promise, successHandler));
        }

        private void handleSuccess(T value) {
            for (PromiseWithResolution<T> promiseWithResolution : this.chain) {
                if (promiseWithResolution.isPending()) {
                    promiseWithResolution.handleSuccess(value);
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/promise/Promise$DefaultPromise$PromiseWithResolution.class */
        public static final class PromiseWithResolution<T> {
            private final Promise<?> promise;
            private final BiConsumer<Promise, ? super T> successHandler;

            PromiseWithResolution(Promise<?> promise, BiConsumer<Promise, ? super T> successHandler) {
                this.promise = promise;
                this.successHandler = successHandler;
            }

            boolean isPending() {
                return this.promise.isPending();
            }

            void handleSuccess(T value) {
                this.successHandler.accept(this.promise, value);
            }
        }
    }
}
