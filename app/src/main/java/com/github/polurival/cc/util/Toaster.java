package com.github.polurival.cc.util;

import android.content.Context;
import android.widget.Toast;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;

public class Toaster {

    public static void showToastIfVeryBigNumber(Context context, String resultStr) {
        if (resultStr.length() > 19) {
            Toaster.showToast(context.getString(R.string.number_limit));
        }
    }

    public static void showToast(String text) {
        Toast toast = Toast.makeText(AppContext.getContext(),
                text,
                Toast.LENGTH_LONG);
        toast.show();
    }
}
