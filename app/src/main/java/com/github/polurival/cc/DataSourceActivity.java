package com.github.polurival.cc;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.polurival.cc.util.Logger;

public class DataSourceActivity extends Activity {

    private String rateUpdaterClassName;
    private LinearLayout customRateFragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_source);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        rateUpdaterClassName = loadRateUpdaterNameProperty();

        customRateFragmentLayout = (LinearLayout) findViewById(R.id.custom_rates_fragment);

        initSourceSpinner();
    }

    private void initSourceSpinner() {
        Spinner sourceSpinner = (Spinner) findViewById(R.id.source_spinner);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this,
                R.layout.mode_spinner_item,
                getResources().getStringArray(R.array.data_source_array));
        sourceSpinner.setAdapter(sourceAdapter);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 2) {
                    customRateFragmentLayout.setVisibility(View.VISIBLE);
                    rateUpdaterClassName = getString(R.string.custom_rate_updater_class);
                } else {
                    customRateFragmentLayout.setVisibility(View.GONE);

                    if (position == 0) {
                        rateUpdaterClassName = getString(R.string.cb_rf_rate_updater_class);
                    } else if (position == 1) {
                        rateUpdaterClassName = getString(R.string.yahoo_rate_updater_class);
                    }
                }
                saveRateUpdaterNameProperty();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (rateUpdaterClassName.equals(getString(R.string.cb_rf_rate_updater_class))) {
            sourceSpinner.setSelection(0);
        } else if (rateUpdaterClassName.equals(getString(R.string.yahoo_rate_updater_class))) {
            sourceSpinner.setSelection(1);
        } else if (rateUpdaterClassName.equals(getString(R.string.custom_rate_updater_class))) {
            sourceSpinner.setSelection(2);
        }
    }

    private void saveRateUpdaterNameProperty() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getString(R.string.saved_rate_updater_class),
                rateUpdaterClassName);

        editor.apply();
        Logger.logD("Successful saving rateUpdaterClassName = " + rateUpdaterClassName);
    }

    private String loadRateUpdaterNameProperty() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        return preferences.getString(getString(R.string.saved_rate_updater_class),
                getString(R.string.saved_rate_updater_class_default));
    }
}
