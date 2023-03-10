package ru.hukola.threadholmes.gui;

import java.util.List;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Babin Nikolay
 */
public class GuiExecutor extends AbstractExecutorService {
    private static final GuiExecutor instance = new GuiExecutor();

    private GuiExecutor() {

    }

    public static GuiExecutor instance() {
        return instance;
    }

    public void execute(Runnable runnable) {
        if (Utilities.isEventDispatchThread()) {
            runnable.run();
        } else {
            Utilities.invokeLater(runnable);
        }
    }

    @Override
    public void shutdown() {

    }

    @Override
    public List<Runnable> shutdownNow() {
        return null;
    }

    @Override
    public boolean isShutdown() {
        return false;
    }

    @Override
    public boolean isTerminated() {
        return false;
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        return false;
    }
}
