package vesnell.pl.lsportfolio.database.controller.base;

import android.util.Log;

public abstract class ControllerRunnable implements Runnable {
    @Override
    public final void run() {
        try {
            runController();
        } catch (Exception e) {
            Log.w(getClass().getName(), "ControllerRunnable exception", e);
        }
    }

    protected abstract void runController();
}
