package com.jgoodies.application;

import com.jgoodies.common.base.Preconditions;
import com.jgoodies.common.base.Strings;
import com.jgoodies.common.internal.BuilderSupport;
import com.jgoodies.common.internal.Messages;
import java.awt.Component;
import java.util.EventObject;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/TaskBuilder.class */
public final class TaskBuilder<T, V> {
    private javax.swing.Action actionToBlock;
    private String title;
    private String description;
    private String message;
    private Consumer<Task<T, V>> initialization;
    private long delayMillis;
    private Function<BuiltTask.TaskPublisher<V>, T> backgroundFunction;
    private Consumer<List<V>> processor;
    private BiConsumer<BuiltTask.TaskContext, T> succeededBiConsumer;
    private Consumer<BuiltTask.TaskContext> cancelledConsumer;
    private BiConsumer<BuiltTask.TaskContext, Throwable> failedBiConsumer;
    private Consumer<BuiltTask.TaskContext> finishedConsumer;
    private Runnable finishedRunnable;
    private final BuilderSupport support = new BuilderSupport();
    private BlockingScope blockingScope = BlockingScope.APPLICATION;
    private boolean progressIndeterminate = true;

    public TaskBuilder<T, V> block(BlockingScope scope) {
        this.support.checkNotCalledTwice("block");
        this.blockingScope = (BlockingScope) Preconditions.checkNotNull(scope, Messages.MUST_NOT_BE_NULL, "blocking scope");
        return this;
    }

    public TaskBuilder<T, V> blockApplication() {
        return block(BlockingScope.APPLICATION);
    }

    public TaskBuilder<T, V> blockNothing() {
        return block(BlockingScope.NONE);
    }

    public TaskBuilder<T, V> blockWindow() {
        return block(BlockingScope.WINDOW);
    }

    public TaskBuilder<T, V> block(javax.swing.Action actionToBlock) {
        this.actionToBlock = (javax.swing.Action) Preconditions.checkNotNull(actionToBlock, Messages.MUST_NOT_BE_NULL, "action to be blocked");
        return block(BlockingScope.ACTION);
    }

    public TaskBuilder<T, V> progressIndeterminate(boolean value) {
        this.support.checkNotCalledTwice("progressDeterminate or #progressIndeterminate");
        this.progressIndeterminate = value;
        return this;
    }

    public TaskBuilder<T, V> progressDeterminate() {
        this.support.checkNotCalledTwice("progressDeterminate or #progressIndeterminate");
        this.progressIndeterminate = false;
        return this;
    }

    public TaskBuilder<T, V> title(String str, Object... args) {
        this.support.checkNotCalledTwice(Task.PROPERTY_TITLE);
        this.title = Strings.get(str, args);
        return this;
    }

    public TaskBuilder<T, V> description(String str, Object... args) {
        this.support.checkNotCalledTwice("description");
        this.description = Strings.get(str, args);
        return this;
    }

    public TaskBuilder<T, V> message(String str, Object... args) {
        this.message = Strings.get(str, args);
        return this;
    }

    public TaskBuilder<T, V> onInit(Consumer<Task<T, V>> value) {
        this.support.checkNotCalledTwice("onInit");
        this.initialization = value;
        return this;
    }

    public TaskBuilder<T, V> sleep(long delayMillis) {
        this.delayMillis = delayMillis;
        return this;
    }

    public TaskBuilder<T, V> inBackgroundDo(Function<BuiltTask.TaskPublisher<V>, T> value) {
        this.support.checkNotCalledTwice("inBackgroundDo");
        this.backgroundFunction = value;
        return this;
    }

    public TaskBuilder<T, V> inBackgroundDo(Supplier<T> value) {
        return inBackgroundDo(publisher -> {
            return value.get();
        });
    }

    public TaskBuilder<T, V> processor(Consumer<List<V>> value) {
        this.support.checkNotCalledTwice("processor");
        this.processor = value;
        return this;
    }

    public TaskBuilder<T, V> onSucceeded(BiConsumer<BuiltTask.TaskContext, T> value) {
        this.support.checkNotCalledTwice("onSucceeded");
        this.succeededBiConsumer = value;
        return this;
    }

    public TaskBuilder<T, V> onSucceeded(Consumer<T> value) {
        return onSucceeded((context, result) -> {
            value.accept(result);
        });
    }

    public TaskBuilder<T, V> onCancelled(Consumer<BuiltTask.TaskContext> value) {
        this.support.checkNotCalledTwice("onCancelled");
        this.cancelledConsumer = value;
        return this;
    }

    public TaskBuilder<T, V> onCancelled(Runnable value) {
        return onCancelled(context -> {
            value.run();
        });
    }

    public TaskBuilder<T, V> onFailed(BiConsumer<BuiltTask.TaskContext, Throwable> value) {
        this.support.checkNotCalledTwice("onFailed");
        this.failedBiConsumer = value;
        return this;
    }

    public TaskBuilder<T, V> onFailed(Consumer<Throwable> value) {
        return onFailed((context, t) -> {
            value.accept(t);
        });
    }

    public TaskBuilder<T, V> onFinished(Consumer<BuiltTask.TaskContext> value) {
        this.support.checkNotCalledTwice("onFinished");
        this.finishedConsumer = value;
        return this;
    }

    public TaskBuilder<T, V> onFinished(Runnable value) {
        this.finishedRunnable = value;
        return this;
    }

    public Task<T, V> build() {
        Preconditions.checkArgument(this.backgroundFunction != null, "The background operation must be specified.");
        Task<T, V> task = new BuiltTask<>(this);
        if (this.actionToBlock != null) {
            task.setAction(this.actionToBlock);
        }
        return task;
    }

    public void execute(EventObject evt) {
        Application.execute(evt, (Task<?, ?>) build());
    }

    /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/TaskBuilder$BuiltTask.class */
    public static final class BuiltTask<T, V> extends Task<T, V> {
        private final TaskBuilder<T, V> builder;
        private final TaskPublisher<V> publisher;
        private final TaskContext context;

        BuiltTask(TaskBuilder<T, V> builder) {
            super(((TaskBuilder) builder).blockingScope);
            this.builder = builder;
            this.publisher = new TaskPublisher<>(this);
            this.context = new TaskContext(this);
            setTitle(((TaskBuilder) builder).title);
            setDescription(((TaskBuilder) builder).description);
            setProgressIndeterminate(((TaskBuilder) builder).progressIndeterminate);
            if (((TaskBuilder) builder).initialization != null) {
                ((TaskBuilder) builder).initialization.accept(this);
            }
        }

        protected T doInBackground() throws Exception {
            if (((TaskBuilder) this.builder).message != null) {
                setMessage(((TaskBuilder) this.builder).message);
            }
            if (((TaskBuilder) this.builder).delayMillis != -1) {
                try {
                    Thread.sleep(((TaskBuilder) this.builder).delayMillis);
                } catch (InterruptedException e) {
                }
            }
            return (T) ((TaskBuilder) this.builder).backgroundFunction.apply(this.publisher);
        }

        protected void process(List<V> chunks) {
            if (((TaskBuilder) this.builder).processor != null) {
                ((TaskBuilder) this.builder).processor.accept(chunks);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.jgoodies.application.Task
        public void succeeded(T result) {
            if (((TaskBuilder) this.builder).succeededBiConsumer != null) {
                ((TaskBuilder) this.builder).succeededBiConsumer.accept(this.context, result);
            } else {
                super.succeeded(result);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.jgoodies.application.Task
        public void cancelled() {
            if (((TaskBuilder) this.builder).cancelledConsumer != null) {
                ((TaskBuilder) this.builder).cancelledConsumer.accept(this.context);
            } else {
                super.cancelled();
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.jgoodies.application.Task
        public void failed(Throwable cause) {
            if (((TaskBuilder) this.builder).failedBiConsumer != null) {
                ((TaskBuilder) this.builder).failedBiConsumer.accept(this.context, cause);
            } else {
                super.failed(cause);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // com.jgoodies.application.Task
        public void finished() {
            if (((TaskBuilder) this.builder).finishedConsumer != null) {
                ((TaskBuilder) this.builder).finishedConsumer.accept(this.context);
            } else if (((TaskBuilder) this.builder).finishedRunnable != null) {
                ((TaskBuilder) this.builder).finishedRunnable.run();
            } else {
                super.finished();
            }
        }

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/TaskBuilder$BuiltTask$TaskPublisher.class */
        public static final class TaskPublisher<V> {
            private final BuiltTask<?, V> task;

            TaskPublisher(BuiltTask<?, V> task) {
                this.task = task;
            }

            public void publish(V... chunks) {
                this.task.publish(chunks);
            }

            public void setMessage(String str, Object... args) {
                this.task.setMessage(Strings.get(str, args));
            }

            public void setProgress(int value) {
                this.task.setProgress(value);
            }

            public void setProgressIndeterminate(boolean b) {
                this.task.setProgressIndeterminate(b);
            }
        }

        /* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/application/TaskBuilder$BuiltTask$TaskContext.class */
        public static final class TaskContext {
            private final BuiltTask<?, ?> task;

            TaskContext(BuiltTask<?, ?> task) {
                this.task = task;
            }

            public Component getComponent() {
                return this.task.getComponent();
            }

            public EventObject getEventObject() {
                return this.task.getEventObject();
            }

            public void fail(Throwable cause) {
                this.task.failed(cause);
            }
        }
    }
}
