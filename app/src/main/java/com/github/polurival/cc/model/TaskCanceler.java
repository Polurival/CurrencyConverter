package com.github.polurival.cc.model;

import android.os.AsyncTask;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.RateUpdaterListener;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

/**
 * Created by Polurival
 * on 15.06.2016.
 * <p/>
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
        Logger.logD(Logger.getTag(), "taskCanceler.run()");

        AsyncTask.Status taskStatus = task.getStatus();
        boolean isCanceled = rateUpdaterListener.isCanceledByUser();

        if (!taskStatus.equals(AsyncTask.Status.FINISHED) && !isCanceled) {
            rateUpdaterListener.stopRefresh();
            rateUpdaterListener.setMenuState(null);

            task.cancel(true);
            rateUpdaterListener.hideCancelBtn();

            rateUpdaterListener.checkAsyncTaskStatusAndSetNewInstance();
            rateUpdaterListener.readDataFromDB();

            Toaster.showCenterToast(AppContext.getContext().getString(R.string.update_error));
        }
    }
}
