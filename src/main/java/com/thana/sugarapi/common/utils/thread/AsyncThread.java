package com.thana.sugarapi.common.utils.thread;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AsyncThread implements ThreadFactory {

    private static final ExecutorService POOL = Executors.newFixedThreadPool(150, new AsyncThread());

    @Override
    public Thread newThread(@NotNull Runnable runnable) {
        return new Thread(runnable);
    }

    public static void runAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable, POOL);
    }
}
