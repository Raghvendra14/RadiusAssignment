package com.example.android.radiusassignment.utils;

import android.os.Process;
import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class to execute tasks on background thread
 */
public class TaskExecutor {

    private static TaskExecutor sInstance;

    private static final int KEEP_ALIVE = 60;

    /**
     * tweak these if it doesn't work well
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = CPU_COUNT + 2;
    private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 3 + 1;

    private static final BlockingQueue<Runnable> sPoolWorkQueue = new LinkedBlockingDeque<>(2048);

    private static final ThreadFactory sThreadFactory;

    static {
        sThreadFactory = new ThreadFactory() {
            private final AtomicInteger mCount = new AtomicInteger(1);

            public Thread newThread(@NonNull Runnable r) {
                return new BackgroundThread(r, "Background Task # " + mCount.getAndIncrement());
            }
        };
    }

    public static final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE, KEEP_ALIVE, TimeUnit.SECONDS, sPoolWorkQueue, sThreadFactory);

    private TaskExecutor() {
        // start a core thread right now in order avoid the overhead later on
        threadPoolExecutor.prestartCoreThread();
    }

    public static TaskExecutor getInstance() {
        if (sInstance == null) {
            synchronized (TaskExecutor.class) {
                if (sInstance == null) {
                    sInstance = new TaskExecutor();
                }
            }
        }
        return sInstance;
    }

    // executes the given task
    public void executeTask(Runnable task) {
        if (task != null) {
            try {
                threadPoolExecutor.execute(task);
            } catch (Exception e) {
                PrettyLogger.e(e.toString());
            }
        }
    }

    public Future<?> submitTask(Callable<?> task) {
        if (task != null) {
            try {
                return threadPoolExecutor.submit(task);
            } catch (Exception e) {
                PrettyLogger.e(e.toString());
            }
        }
        return null;
    }

    static class BackgroundThread extends Thread {

        public BackgroundThread(Runnable runnable, String threadName) {
            super(runnable, threadName);
        }

        @Override
        public void run() {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            super.run();
        }
    }
}
