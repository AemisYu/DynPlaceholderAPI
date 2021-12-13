package me.veir1.dynplaceholderapi.bukkit.util;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;

public final class CallbackUtil {
    public static BiFunction<String, Queue<CompletableFuture<?>>, Queue<CompletableFuture<?>>> computeValue(final CompletableFuture<?> queueValue) {
        return (key, value) -> {
            if (value == null) value = new ArrayDeque<>();
            value.add(queueValue);
            return value;
        };
    }
}
