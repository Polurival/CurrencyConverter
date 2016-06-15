package com.github.polurival.cc.model.db;

/**
 * Created by Polurival
 * on 14.06.2016.
 *
 * <p>For listening {@link android.app.Activity#onBackPressed()}</p>
 */
public interface OnBackPressedListener {

    /**
     * Cancel AsyncTask after pressing Back button
     */
    void notifyBackPressed();

}
