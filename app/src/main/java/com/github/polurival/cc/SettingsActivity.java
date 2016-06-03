package com.github.polurival.cc;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.polurival.cc.util.Logger;

public class SettingsActivity extends Activity {

    private String rateUpdaterClassName;
    private CustomRateFragment customRateFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        rateUpdaterClassName = loadRateUpdaterNameProperty();

        customRateFragment = new CustomRateFragment();
        initSourceSpinner();
    }

    private void initSourceSpinner() {
        Spinner sourceSpinner = (Spinner) findViewById(R.id.source_spinner);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.data_source_array));
        sourceSpinner.setAdapter(sourceAdapter);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                FragmentTransaction ft = getFragmentManager().beginTransaction();

                if (position == 1) {
                    if (!customRateFragment.isAdded()) {
                        ft.add(R.id.custom_rates_fragment_container, customRateFragment);
                        Logger.logD("fragment was added");
                    }
                    if (customRateFragment.isHidden()) {
                        ft.show(customRateFragment);
                        Logger.logD("fragment was shown");
                    }
                    rateUpdaterClassName = getString(R.string.custom_rate_updater_class);


                } else {
                    if (customRateFragment.isVisible()) {
                        ft.hide(customRateFragment);
                        Logger.logD("fragment was hidden");
                    }
                    if (position == 0) {
                        rateUpdaterClassName = getString(R.string.cb_rf_rate_updater_class);
                    }
                }
                saveRateUpdaterNameProperty();

                ft.addToBackStack(null);
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                ft.commit();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (rateUpdaterClassName.equals(getString(R.string.cb_rf_rate_updater_class))) {
            sourceSpinner.setSelection(0);
        } else if (rateUpdaterClassName.equals(getString(R.string.custom_rate_updater_class))) {
            sourceSpinner.setSelection(1);
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
