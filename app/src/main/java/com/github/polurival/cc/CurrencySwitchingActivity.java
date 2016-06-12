package com.github.polurival.cc;

import android.app.Activity;
import android.os.Bundle;

public class CurrencySwitchingActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_switching);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
