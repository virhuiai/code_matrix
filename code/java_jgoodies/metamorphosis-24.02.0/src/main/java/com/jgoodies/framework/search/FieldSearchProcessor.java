package com.jgoodies.framework.search;

import com.jgoodies.search.Completion;
import com.jgoodies.search.CompletionProcessor;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/framework/search/FieldSearchProcessor.class */
public interface FieldSearchProcessor<V> extends CompletionProcessor {
    V searchValue(String str);

    V valueFor(Completion completion);

    String getDisplayString(V v);

    boolean represents(String str, V v);
}
