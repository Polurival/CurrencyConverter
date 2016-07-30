package com.github.polurival.cc;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.LinearLayout;

/**
 * Created by Polurival
 * on 30.07.2016.
 */
public class CheckableLinearLayout extends LinearLayout implements Checkable {
    private CheckBox mCheckBox;

    public CheckableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        int childCount = getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View v = getChildAt(i);
            if (v instanceof CheckBox) {
                mCheckBox = (CheckBox) v;
            }
        }
    }

    @Override
    public void setChecked(boolean checked) {
        if (null != mCheckBox) {
            mCheckBox.setChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        return mCheckBox != null && mCheckBox.isChecked();
    }

    @Override
    public void toggle() {
        if (null != mCheckBox) {
            mCheckBox.toggle();
        }
    }
}
