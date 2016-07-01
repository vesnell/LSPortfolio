package vesnell.pl.lsportfolio.database.controller.base;

import android.os.Handler;
import android.os.HandlerThread;

public class ControllerHandler {
    private static final String NAME = "ControllerThread";
    private static final ControllerHandler instance = new ControllerHandler();

    public static ControllerHandler getInstance() {
        return instance;
    }

    private final Handler handler;

    private ControllerHandler() {
        HandlerThread handlerThread = new HandlerThread(NAME);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public void execute(Runnable runnable) {
        handler.post(runnable);
    }
}
