package vesnell.pl.lsportfolio.database.controller.base;

import android.util.Log;

public abstract class CallbackRunnable implements Runnable {
    @Override
    public final void run() {
        try {
            runCallback();
        } catch (Exception e) {
            Log.w(getClass().getName(), "CallbackRunnable exception", e);
        }
    }

    protected abstract void runCallback();
}
