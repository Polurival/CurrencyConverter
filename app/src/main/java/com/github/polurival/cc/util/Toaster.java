package com.github.polurival.cc.util;

import android.view.Gravity;
import android.widget.Toast;

import com.github.polurival.cc.AppContext;

/**
 * Created by Polurival
 * on 29.06.2016.
 */
public class Toaster {

    public static void showCenterToast(String text) {
        Toast toast = Toast.makeText(AppContext.getContext(),
                text,
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }
}
