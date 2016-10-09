package com.github.polurival.cc;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.polurival.cc.util.Logger;

public class DataSourceActivity extends Activity implements SearcherFragment.Listener {

    private SharedPreferences preferences;

    private String rateUpdaterClassName;

    private CustomRateFragment customRateFragment;
    private LinearLayout customRateFragmentLayout;
    private LinearLayout searcherFragmentLayout;
    private CheckBox cbAutoUpdate;
    private View divider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.logD(Logger.getTag(), "onCreate");

        setContentView(R.layout.activity_data_source);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        rateUpdaterClassName = loadRateUpdaterNameProperty();

        customRateFragmentLayout = (LinearLayout) findViewById(R.id.custom_rates_fragment);
        searcherFragmentLayout =
                (LinearLayout) findViewById(R.id.searcher_custom_rates_fragment_layout);

        cbAutoUpdate = ((CheckBox) findViewById(R.id.cb_auto_update));
        cbAutoUpdate.setChecked(loadIsSetAutoUpdateProperty());

        divider = findViewById(R.id.divider);

        initSourceSpinner();
    }

    public void setAutoUpdate(View view) {
        Logger.logD(Logger.getTag(), "setAutoUpdate");

        saveIsSetAutoUpdateProperty(((CheckBox) view).isChecked());
    }

    private void initSourceSpinner() {
        Logger.logD(Logger.getTag(), "initSourceSpinner");

        Spinner sourceSpinner = (Spinner) findViewById(R.id.source_spinner);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this,
                R.layout.mode_spinner_item,
                getResources().getStringArray(R.array.data_source_array));
        sourceSpinner.setAdapter(sourceAdapter);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 2) {
                    rateUpdaterClassName = getString(R.string.custom_rate_updater_class);

                    divider.setVisibility(View.VISIBLE);
                    searcherFragmentLayout.setVisibility(View.VISIBLE);
                    customRateFragmentLayout.setVisibility(View.VISIBLE);
                    cbAutoUpdate.setEnabled(false);
                } else {
                    divider.setVisibility(View.GONE);
                    customRateFragmentLayout.setVisibility(View.GONE);
                    searcherFragmentLayout.setVisibility(View.GONE);
                    cbAutoUpdate.setEnabled(true);

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

        setNewSearcherFragment();
    }

    private boolean loadIsSetAutoUpdateProperty() {
        Logger.logD(Logger.getTag(), "loadIsSetAutoUpdateProperty");

        return preferences.getBoolean(getString(R.string.saved_is_set_auto_update),
                Boolean.valueOf(getString(R.string.saved_is_set_auto_update_default)));
    }

    private void saveIsSetAutoUpdateProperty(boolean isSetAutoUpdate) {
        Logger.logD(Logger.getTag(), "saveIsSetAutoUpdateProperty");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(getString(R.string.saved_is_set_auto_update), isSetAutoUpdate);

        editor.apply();
    }

    private void saveRateUpdaterNameProperty() {
        Logger.logD(Logger.getTag(), "saveRateUpdaterNameProperty");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getString(R.string.saved_rate_updater_class), rateUpdaterClassName);

        editor.apply();
        Logger.logD("Successful saving rateUpdaterClassName = " + rateUpdaterClassName);
    }

    private String loadRateUpdaterNameProperty() {
        Logger.logD(Logger.getTag(), "loadRateUpdaterNameProperty");

        return preferences.getString(getString(R.string.saved_rate_updater_class),
                getString(R.string.saved_rate_updater_class_default));
    }

    private void setNewSearcherFragment() {
        Logger.logD(Logger.getTag(), "setNewSearcherFragment");

        customRateFragment = (CustomRateFragment)
                getFragmentManager().findFragmentById(R.id.custom_rates_fragment);
        Spinner customCurrencySpinner = customRateFragment.getCustomCurrencySpinner();

        SearcherFragment searcherFragment = new SearcherFragment();
        searcherFragment.setCustomSpinner(customCurrencySpinner);

        FragmentTransaction transaction
                = getFragmentManager().beginTransaction();
        transaction.replace(R.id.custom_searcher_fragment_container, searcherFragment);
        transaction.commit();
    }

    @Override
    public Cursor getCursor() {
        return customRateFragment.getSpinnerCursor();
    }

    @Override
    public String getRateUpdaterClassName() {
        return rateUpdaterClassName;
    }
}
