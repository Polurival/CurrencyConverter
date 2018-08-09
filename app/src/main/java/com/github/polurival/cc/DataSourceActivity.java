package com.github.polurival.cc;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Logger;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DataSourceActivity extends Activity implements SearcherFragment.Listener {

    private static final int CB_SPINNER_POSITION = 0;
    private static final int YAHOO_SPINNER_POSITION = -1; // Yahoo doesn't work
    private static final int MY_CURRENCY_SPINNER_POSITION = 1;
    private static final int CUSTOM_SPINNER_POSITION = 2;

    private String rateUpdaterClassName;

    private CustomRateFragment customRateFragment;
    private LinearLayout customRateFragmentLayout;
    private LinearLayout searcherFragmentLayout;
    private CheckBox cbAutoUpdate;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.logD(Logger.getTag(), "onCreate");

        setContentView(R.layout.activity_data_source);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        rateUpdaterClassName = AppPreferences.loadRateUpdaterClassName(this);

        customRateFragmentLayout = findViewById(R.id.custom_rates_fragment);
        searcherFragmentLayout = findViewById(R.id.searcher_custom_rates_fragment_layout);

        cbAutoUpdate = findViewById(R.id.cb_auto_update);
        cbAutoUpdate.setChecked(AppPreferences.loadIsSetAutoUpdate(this));

        initSourceSpinner();
    }

    public void setAutoUpdate(View view) {
        Logger.logD(Logger.getTag(), "setAutoUpdate");

        AppPreferences.saveIsSetAutoUpdate(this, cbAutoUpdate.isChecked());
    }

    private void initSourceSpinner() {
        Logger.logD(Logger.getTag(), "initSourceSpinner");

        Spinner sourceSpinner = findViewById(R.id.source_spinner);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this,
                R.layout.mode_spinner_item,
                getResources().getStringArray(R.array.data_source_array));
        sourceSpinner.setAdapter(sourceAdapter);

        sourceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == CUSTOM_SPINNER_POSITION) {
                    rateUpdaterClassName = getString(R.string.custom_rate_updater_class);

                    searcherFragmentLayout.setVisibility(View.VISIBLE);
                    customRateFragmentLayout.setVisibility(View.VISIBLE);
                    cbAutoUpdate.setEnabled(false);
                } else {
                    customRateFragmentLayout.setVisibility(View.GONE);
                    searcherFragmentLayout.setVisibility(View.GONE);
                    cbAutoUpdate.setEnabled(true);

                    if (position == CB_SPINNER_POSITION) {
                        rateUpdaterClassName = getString(R.string.cb_rf_rate_updater_class);
                    } else if (position == YAHOO_SPINNER_POSITION) {
                        rateUpdaterClassName = getString(R.string.yahoo_rate_updater_class);
                    } else if (position == MY_CURRENCY_SPINNER_POSITION) {
                        rateUpdaterClassName = getString(R.string.my_currency_net_rate_updater_class);
                    }
                }

                AppPreferences.saveRateUpdaterClassName(DataSourceActivity.this, rateUpdaterClassName);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (rateUpdaterClassName.equals(getString(R.string.cb_rf_rate_updater_class))) {
            sourceSpinner.setSelection(CB_SPINNER_POSITION);
        } else if (rateUpdaterClassName.equals(getString(R.string.yahoo_rate_updater_class))) {
            sourceSpinner.setSelection(YAHOO_SPINNER_POSITION);
        } else if (rateUpdaterClassName.equals(getString(R.string.my_currency_net_rate_updater_class))) {
            sourceSpinner.setSelection(MY_CURRENCY_SPINNER_POSITION);
        } else if (rateUpdaterClassName.equals(getString(R.string.custom_rate_updater_class))) {
            sourceSpinner.setSelection(CUSTOM_SPINNER_POSITION);
        }

        setNewSearcherFragment();
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

    public Cursor getCommonCursor() {
        return customRateFragment.getSpinnerCursor();
    }
}
