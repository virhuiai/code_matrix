package com.jgoodies.common.promise;

import java.util.List;
import java.util.function.Function;

/* loaded from: metamorphosis-24.02.0.jar:com/jgoodies/common/promise/Promises.class */
public final class Promises {
    private Promises() {
    }

    public static <U> Promise<Boolean> anyMatch(List<U> list, Function<? super U, Promise<Boolean>> function) {
        if (list.isEmpty()) {
            return Promise.of(false);
        }
        Object obj = list.get(0);
        List<U> subList = list.subList(1, list.size());
        return function.apply(obj).thenCompose(result -> {
            if (result.booleanValue()) {
                return Promise.of(true);
            }
            return anyMatch(subList, function);
        });
    }

    public static <U> Promise<Boolean> allMatch(List<U> list, Function<? super U, Promise<Boolean>> function) {
        if (list.isEmpty()) {
            return Promise.of(true);
        }
        Object obj = list.get(0);
        List<U> subList = list.subList(1, list.size());
        return function.apply(obj).thenCompose(result -> {
            if (result.booleanValue()) {
                return allMatch(subList, function);
            }
            return Promise.of(false);
        });
    }

    public static <U> Promise<Boolean> noneMatch(List<U> list, Function<? super U, Promise<Boolean>> function) {
        if (list.isEmpty()) {
            return Promise.of(true);
        }
        Object obj = list.get(0);
        List<U> subList = list.subList(1, list.size());
        return function.apply(obj).thenCompose(result -> {
            if (result.booleanValue()) {
                return Promise.of(false);
            }
            return noneMatch(subList, function);
        });
    }
}
