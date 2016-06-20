package com.github.polurival.cc.model;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.RateUpdaterListener;

/**
 * Created by Polurival
 * on 15.06.2016.
 *
 * <p>See <a href="http://stackoverflow.com/questions/17315372/cancel-asynctask-after-some-time">source</a></p>
 */
public class TaskCanceler implements Runnable {

    private AsyncTask task;
    private RateUpdaterListener rateUpdaterListener;

    public TaskCanceler(AsyncTask task, RateUpdaterListener rateUpdaterListener) {
        this.task = task;
        this.rateUpdaterListener = rateUpdaterListener;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTask.Status.RUNNING) {
            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setMenuState(null);
            task.cancel(true);

            Context appContext = AppContext.getContext();
            Toast.makeText(appContext, appContext.getString(R.string.update_error),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }
}
