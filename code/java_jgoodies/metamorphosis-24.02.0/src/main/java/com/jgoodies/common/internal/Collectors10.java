package com.jgoodies.common.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collector;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/internal/Collectors10.class */
public final class Collectors10 {
    private Collectors10() {
    }

    public static <T> Collector<T, List<T>, List<T>> toUnmodifiableList() {
        return toUnmodifiableList(ArrayList::new);
    }

    private static <T, L extends List<T>> Collector<T, L, List<T>> toUnmodifiableList(Supplier<L> listProvider) {
        return Collector.of(listProvider, (v0, v1) -> {
            v0.add(v1);
        }, (left, right) -> {
            left.addAll(right);
            return left;
        }, Collections::unmodifiableList, new Collector.Characteristics[0]);
    }
}
