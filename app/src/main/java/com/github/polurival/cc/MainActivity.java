package com.github.polurival.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.polurival.cc.model.CBRateUpdaterTask;
import com.github.polurival.cc.model.CustomRateUpdaterMock;
import com.github.polurival.cc.model.YahooRateUpdaterTask;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.RateUpdater;
import com.github.polurival.cc.model.RateUpdaterListener;
import com.github.polurival.cc.util.DateUtil;
import com.github.polurival.cc.util.Logger;

import org.joda.time.LocalDateTime;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Polurival
 * on 24.03.2016.
 */
public class MainActivity extends Activity implements RateUpdaterListener, OnRefreshListener {

    private SQLiteDatabase db;
    private Cursor cursor;
    private Cursor fromCursor;
    private Cursor toCursor;

    private RateUpdater rateUpdater;
    private LocalDateTime upDateTime;

    private PullToRefreshLayout mPullToRefreshLayout;

    private EditText editFromAmount;
    private EditText editToAmount;

    private boolean isPropertiesLoaded;
    private boolean isNeedToReSwapValues;
    private boolean ignoreEditFromAmountChange;
    private boolean ignoreEditToAmountChange;

    private Spinner fromSpinner;
    private int fromSpinnerSelectedPos;
    double currencyFromNominal;
    double currencyFromToXRate;

    private Spinner toSpinner;
    private int toSpinnerSelectedPos;
    double currencyToNominal;
    double currencyToToXRate;

    private TextView tvDateTime;

    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void setUpDateTime(LocalDateTime upDateTime) {
        this.upDateTime = upDateTime;
    }

    @Override
    public void setPropertiesLoaded(boolean isLoaded) {
        this.isPropertiesLoaded = isLoaded;
    }

    @Override
    public RateUpdater getRateUpdater() {
        return rateUpdater;
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ignoreEditFromAmountChange = false;
        ignoreEditToAmountChange = false;
        isNeedToReSwapValues = false;
        isPropertiesLoaded = false;

        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        initEditAmount();

        loadRateUpdaterProperties();
        loadProperties();
    }

    @Override
    protected void onStart() {
        super.onStart();

        db = DBHelper.getInstance(getApplicationContext()).getReadableDatabase();
        readDataFromDB();

        checkAsyncTaskStatusAndSetNewInstance();

        //update on Application Start
        if (DateUtil.compareUpDateWithCurrentDate(upDateTime)) {
            updateRatesFromSource();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAsyncTaskStatusAndSetNewInstance();
    }

    private void checkAsyncTaskStatusAndSetNewInstance() {
        if (rateUpdater instanceof AsyncTask) {
            if (((AsyncTask) rateUpdater).getStatus() != AsyncTask.Status.PENDING) {
                loadRateUpdaterProperties();
            }
        }
    }

    private void updateRatesFromSource() {
        if (rateUpdater instanceof CBRateUpdaterTask) {
            ((CBRateUpdaterTask) rateUpdater).execute();
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            ((YahooRateUpdaterTask) rateUpdater).execute();
        }
    }

    @Override
    public void readDataFromDB() {
        DBReaderTask dbReaderTask = new DBReaderTask();
        dbReaderTask.setRateUpdaterListener(this);
        if (rateUpdater instanceof CBRateUpdaterTask) {
            dbReaderTask.execute(DBHelper.COLUMN_NAME_CB_RF_SOURCE,
                    DBHelper.COLUMN_NAME_CB_RF_NOMINAL,
                    DBHelper.COLUMN_NAME_CB_RF_RATE);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            dbReaderTask.execute(DBHelper.COLUMN_NAME_YAHOO_SOURCE,
                    DBHelper.COLUMN_NAME_YAHOO_NOMINAL,
                    DBHelper.COLUMN_NAME_YAHOO_RATE);
        } else if (rateUpdater instanceof CustomRateUpdaterMock) {
            dbReaderTask.execute(DBHelper.CUSTOM_SOURCE_MOCK,
                    DBHelper.COLUMN_NAME_CUSTOM_NOMINAL,
                    DBHelper.COLUMN_NAME_CUSTOM_RATE);
        }
    }

    @Override
    public void stopRefresh() {
        mPullToRefreshLayout.setRefreshComplete();
    }

    private void initEditAmount() {
        editFromAmount = (EditText) findViewById(R.id.edit_from_amount);
        editFromAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && isPropertiesLoaded) {
                    if (!ignoreEditFromAmountChange) {
                        ignoreEditToAmountChange = true;
                        convertAndSetResult(editFromAmount);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ignoreEditToAmountChange = false;
                if ("".equals(s.toString())) {
                    editToAmount.getText().clear();
                }
            }
        });

        editToAmount = (EditText) findViewById(R.id.edit_to_amount);
        editToAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && isPropertiesLoaded) {
                    if (!ignoreEditToAmountChange) {
                        ignoreEditFromAmountChange = true;
                        convertAndSetResult(editToAmount);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ignoreEditFromAmountChange = false;
                if ("".equals(s.toString())) {
                    editFromAmount.getText().clear();
                }
            }
        });
    }

    @Override
    public void initTvDateTime() {
        tvDateTime = (TextView) findViewById(R.id.tv_date_time);
        tvDateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                fromSpinner.setSelection(fromSpinner.getSelectedItemPosition());
                toSpinner.setSelection(toSpinner.getSelectedItemPosition());
            }
        });
        tvDateTimeSetText();
    }

    private void tvDateTimeSetText() {
        tvDateTime.setText(String.format("%s%s",
                rateUpdater.getDescription(), DateUtil.getUpDateTimeStr(upDateTime)));
    }

    public void swapFromTo(View v) {
        if (fromSpinner != null && toSpinner != null) {
            int fromSpinnerSelectedItemPos = fromSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
            toSpinner.setSelection(fromSpinnerSelectedItemPos);
        }
    }

    public void convertAndSetResult(View v) {
        if (null == fromSpinner.getSelectedItem() || null == toSpinner.getSelectedItem()) {
            return;
        }

        if (isNeedToReSwapValues && (v.getId() != R.id.edit_to_amount)) {
            double tempValFrom = currencyFromToXRate;
            currencyFromToXRate = currencyToToXRate;
            currencyToToXRate = tempValFrom;

            double tempNomFrom = currencyFromNominal;
            currencyFromNominal = currencyToNominal;
            currencyToNominal = tempNomFrom;

            isNeedToReSwapValues = false;
        }

        if (!isNeedToReSwapValues && (v.getId() == R.id.edit_to_amount)) {
            double tempValFrom = currencyFromToXRate;
            currencyFromToXRate = currencyToToXRate;
            currencyToToXRate = tempValFrom;

            double tempNomFrom = currencyFromNominal;
            currencyFromNominal = currencyToNominal;
            currencyToNominal = tempNomFrom;

            isNeedToReSwapValues = true;
        }

        double enteredAmountOfMoney = getEnteredAmountOfMoney(v);

        double result;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            result = enteredAmountOfMoney *
                    (currencyFromToXRate / currencyToToXRate) *
                    (currencyToNominal / currencyFromNominal);
        } else {
            result = enteredAmountOfMoney *
                    (currencyToToXRate / currencyFromToXRate) *
                    (currencyFromNominal / currencyToNominal);
        }


        if (v.getId() == R.id.edit_from_amount) {
            if ("".equals(editFromAmount.getText().toString())) {
                editToAmount.setText("");
            } else {
                editToAmount.setText(String.format("%.2f", result).replace(",", "."));
            }
        } else if (v.getId() == R.id.edit_to_amount) {
            if ("".equals(editToAmount.getText().toString())) {
                editFromAmount.setText("");
            } else {
                editFromAmount.setText(String.format("%.2f", result).replace(",", "."));
            }
        }
    }

    private double getEnteredAmountOfMoney(View v) {
        if (v.getId() == R.id.edit_from_amount) {
            if (TextUtils.isEmpty(editFromAmount.getText().toString())) {
                return 0d;
            }
            return (double) Float.parseFloat(editFromAmount.getText().toString().replace(",", "."));
        } else {
            if (TextUtils.isEmpty(editToAmount.getText().toString())) {
                return 0d;
            }
            return (double) Float.parseFloat(editToAmount.getText().toString().replace(",", "."));
        }
    }

    @Override
    public void initSpinners() {
        SpinnerCursorAdapter cursorAdapter =
                new SpinnerCursorAdapter(getApplicationContext(), cursor);

        fromSpinner = (Spinner) findViewById(R.id.from_spinner);
        fromSpinner.setAdapter(cursorAdapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCursor = (Cursor) parent.getItemAtPosition(position);
                currencyFromNominal = (double) fromCursor.getInt(2);
                currencyFromToXRate = fromCursor.getDouble(3);

                fromSpinnerSelectedPos = position;

                editFromAmount.setText(editFromAmount.getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toSpinner = (Spinner) findViewById(R.id.to_spinner);
        toSpinner.setAdapter(cursorAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCursor = (Cursor) parent.getItemAtPosition(position);
                currencyToNominal = (double) toCursor.getInt(2);
                currencyToToXRate = toCursor.getDouble(3);

                toSpinnerSelectedPos = position;

                editFromAmount.setText(editFromAmount.getText());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onRefreshStarted(View view) {
        updateRates();
    }

    private class SpinnerCursorAdapter extends CursorAdapter {

        public SpinnerCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ImageView flagIcon = (ImageView) view.findViewById(R.id.spinner_flag_icon);
            int flagIconId = cursor.getInt(5);
            flagIcon.setImageResource(flagIconId);

            TextView currencyName = (TextView) view.findViewById(R.id.spinner_currency_name);
            int currencyNameId = cursor.getInt(4);
            currencyName.setText(getString(currencyNameId));

            TextView currencyCharCode =
                    (TextView) view.findViewById(R.id.spinner_currency_char_code);
            currencyCharCode.setText(cursor.getString(1));
        }
    }

    @Override
    protected void onStop() {
        saveProperties();

        fromCursor.close();
        toCursor.close();
        cursor.close();
        db.close();

        super.onStop();
    }

    private void saveProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putInt(getString(R.string.saved_cb_rf_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(getString(R.string.saved_cb_rf_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putInt(getString(R.string.saved_yahoo_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(getString(R.string.saved_yahoo_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else {
            editor.putInt(getString(R.string.saved_custom_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(getString(R.string.saved_custom_to_spinner_pos),
                    toSpinnerSelectedPos);
        }

        editor.putString(getString(R.string.saved_from_edit_amount_text),
                editFromAmount.getText().toString());
        editor.putString(getString(R.string.saved_to_edit_amount_text),
                editToAmount.getText().toString());

        editor.putString(getString(R.string.saved_rate_updater_class),
                rateUpdater.getClass().getName());

        editor.apply();
    }

    @Override
    public void saveDateProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putLong(getString(R.string.saved_cb_rf_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putLong(getString(R.string.saved_yahoo_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        }

        editor.apply();
    }

    private void loadProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String editFromAmountText =
                preferences.getString(getString(R.string.saved_from_edit_amount_text),
                        getString(R.string.saved_edit_amount_text_default));
        editFromAmount.setText(""); //todo check without it and delete in case of useless
        editFromAmount.setText(editFromAmountText);
        String editToAmountText =
                preferences.getString(getString(R.string.saved_to_edit_amount_text),
                        getString(R.string.saved_edit_amount_text_default));
        editToAmount.setText(editToAmountText);

        String savedUpDateTime;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            savedUpDateTime = getString(R.string.saved_cb_rf_up_date_time);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            savedUpDateTime = getString(R.string.saved_yahoo_up_date_time);
        } else {
            savedUpDateTime = getString(R.string.saved_custom_up_date_time);
        }
        long upDateTimeInSeconds =
                preferences.getLong(savedUpDateTime, DateUtil.getDefaultDateTimeInSeconds());
        upDateTime = DateUtil.getUpDateTime(upDateTimeInSeconds);
    }

    private void loadRateUpdaterProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String rateUpdaterName =
                preferences.getString(getString(R.string.saved_rate_updater_class),
                        getString(R.string.saved_rate_updater_class_default));
        Logger.logD("rateUpdater className = " + rateUpdaterName);
        try {
            rateUpdater
                    = (RateUpdater) Class.forName(rateUpdaterName).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rateUpdater.setRateUpdaterListener(this);
    }

    @Override
    public void loadSpinnerProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (rateUpdater instanceof CBRateUpdaterTask) {
            fromSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_cb_rf_from_spinner_pos), 30);
            toSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_cb_rf_to_spinner_pos), 23);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            fromSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_yahoo_from_spinner_pos), 143);
            toSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_yahoo_to_spinner_pos), 116);
        } else {
            fromSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_custom_from_spinner_pos), 143);
            toSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_custom_to_spinner_pos), 116);
        }

        fromSpinner.setSelection(fromSpinnerSelectedPos);
        toSpinner.setSelection(toSpinnerSelectedPos);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.data_source_action:
                Intent dataSourceIntent = new Intent(this, DataSourceActivity.class);
                startActivity(dataSourceIntent);
                return true;

            case R.id.currency_switching:
                Intent currencySwitchingIntent = new Intent(this, CurrencySwitchingActivity.class);
                startActivity(currencySwitchingIntent);
                return true;

            case R.id.update_rates_action:
                updateRates();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateRates() {
        if (rateUpdater instanceof CustomRateUpdaterMock) {
            Toast.makeText(this, R.string.custom_updating_info, Toast.LENGTH_SHORT).show();
            stopRefresh();
        } else {
            loadRateUpdaterProperties();

            updateRatesFromSource();

            saveProperties();

            loadSpinnerProperties();
        }
    }
}
