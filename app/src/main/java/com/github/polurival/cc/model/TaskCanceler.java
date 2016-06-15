package com.github.polurival.cc.model;

import android.os.AsyncTask;

/**
 * Created by Polurival
 * on 15.06.2016.
 *
 * <p>See <a href="http://stackoverflow.com/questions/17315372/cancel-asynctask-after-some-time">source</a></p>
 */
public class TaskCanceler implements Runnable {

    private AsyncTask task;

    public TaskCanceler(AsyncTask task) {
        this.task = task;
    }

    @Override
    public void run() {
        if (task.getStatus() == AsyncTask.Status.RUNNING) {
            task.cancel(true);
        }
    }
}
