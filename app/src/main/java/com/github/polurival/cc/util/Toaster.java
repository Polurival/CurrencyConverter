package com.github.polurival.cc.util;

import android.widget.Toast;

import com.github.polurival.cc.AppContext;

public class Toaster {

    public static void showBottomToast(String text) {
        Toast toast = Toast.makeText(AppContext.getContext(),
                text,
                Toast.LENGTH_SHORT);
        toast.show();
    }
}
