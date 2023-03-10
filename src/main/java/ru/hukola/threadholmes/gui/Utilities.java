package ru.hukola.threadholmes.gui;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * @author Babin Nikolay
 */
public class Utilities {
    private static final ExecutorService exec = Executors.newSingleThreadExecutor(new ThreadFactoryImpl());
    private static volatile Thread thread;

    private static class ThreadFactoryImpl implements ThreadFactory{

        @Override
        public Thread newThread(Runnable r) {
            thread = new Thread(r);
            return thread;
        }
    }

    public static boolean isEventDispatchThread() {
        return Thread.currentThread() == thread;
    }

    public static void invokeLater(Runnable task) {
        exec.execute(task);
    }
}
