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
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.RateUpdater;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.RateUpdaterListener;
import com.github.polurival.cc.util.DateUtil;
import com.github.polurival.cc.util.Logger;

import org.joda.time.LocalDateTime;

import java.util.EnumMap;

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

    private EnumMap<CharCode, Currency> currencyMap;
    //private Integer[] countryFlagIds;
    //private String[] currencyCharCodes;

    private PullToRefreshLayout mPullToRefreshLayout;

    private EditText editFromAmount;
    private EditText editToAmount;

    /*private boolean isEditFromAmountChanged;
    private boolean isEditToAmountChanged;
    private boolean isTvDateTimeChanged;
    private boolean isFromSpinnerChanged;
    private boolean isToSpinnerChanged;*/
    private boolean isNeedToReSwapValues;
    private boolean ignoreEditFromAmountChange;
    private boolean ignoreEditToAmountChange;

    private boolean isPropertiesLoaded;

    private Spinner fromSpinner;
    private int fromSpinnerSelectedPos;
    double currencyFromNominal;
    double currencyFromToRubRate;

    private Spinner toSpinner;
    private int toSpinnerSelectedPos;
    double currencyToNominal;
    double currencyToToRubRate;

    //private TextView tvResult;
    private TextView tvDateTime;

    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap) {
        this.currencyMap = currencyMap;
    }

    @Override
    public void setUpDateTime(LocalDateTime upDateTime) {
        this.upDateTime = upDateTime;
    }

    @Override
    public void setPropertiesLoaded(boolean isLoaded) {
        this.isPropertiesLoaded = isLoaded;

        //to invoke convert()
        //tvDateTimeSetText();
        // но из-за этого result = NaN
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
    public void onCreate(Bundle savedInstanceState) {
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

        //tvResult = (TextView) findViewById(R.id.tv_result);
    }

    @Override
    protected void onStart() {
        super.onStart();

        db = DBHelper.getInstance(getApplicationContext()).getReadableDatabase();
        readDataFromDB();

        checkAsyncTaskStatus();

        /*if (DateUtil.compareUpDateWithCurrentDate(upDateTime)) {
            updateRatesFromSource();
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkAsyncTaskStatus();
    }

    private void checkAsyncTaskStatus() {
        if (rateUpdater instanceof CBRateUpdaterTask) {
            if (((CBRateUpdaterTask) rateUpdater).getStatus() != AsyncTask.Status.PENDING) {
                loadRateUpdaterProperties();
            }
        }
    }

    private void updateRatesFromSource() {
        if (rateUpdater instanceof CBRateUpdaterTask) {
            ((CBRateUpdaterTask) rateUpdater).execute();
        } else if (rateUpdater instanceof CustomRateUpdaterMock) {
            //do nothing
        }
    }

    @Override
    public void readDataFromDB() {
        DBReaderTask dbReaderTask = new DBReaderTask();
        dbReaderTask.setRateUpdaterListener(this);
        if (rateUpdater instanceof CBRateUpdaterTask) {
            dbReaderTask.execute(DBHelper.COLUMN_NAME_CB_RF_SOURCE,
                    DBHelper.COLUMN_NAME_NOMINAL,
                    DBHelper.COLUMN_NAME_VALUE);
        } else if (rateUpdater instanceof CustomRateUpdaterMock) {
            dbReaderTask.execute(DBHelper.CUSTOM_SOURCE_MOCK,
                    DBHelper.COLUMN_NAME_CUSTOM_NOMINAL,
                    DBHelper.COLUMN_NAME_CUSTOM_VALUE);
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
                        convert(editFromAmount);
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
                        convert(editToAmount);
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
                if (isPropertiesLoaded) {
                    if (!ignoreEditFromAmountChange) {
                        ignoreEditToAmountChange = true;
                        convert(editFromAmount);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                ignoreEditToAmountChange = false;
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

    public void convert(View v) {
        if (/*null == currencyMap
                ||*/ null == fromSpinner.getSelectedItem()
                || null == toSpinner.getSelectedItem()) {
            return;
        }

        /*if (v.getId() == R.id.edit_from_amount) {
            //fromCursor = (Cursor) fromSpinner.getSelectedItem();
            //toCursor = (Cursor) toSpinner.getSelectedItem();
            //fromCursor = (Cursor) fromSpinner.getItemAtPosition(fromSpinnerSelectedPos);
            //toCursor = (Cursor) toSpinner.getItemAtPosition(toSpinnerSelectedPos);
        } else if (v.getId() == R.id.edit_to_amount) {
            //fromCursor = (Cursor) toSpinner.getSelectedItem();
            //toCursor = (Cursor) fromSpinner.getSelectedItem();
            //fromCursor = (Cursor) toSpinner.getItemAtPosition(toSpinnerSelectedPos);
            //toCursor = (Cursor) fromSpinner.getItemAtPosition(fromSpinnerSelectedPos);
        }*/

        /*String fromCharCode = null;
        String toCharCode = null;

        if (v.getId() == R.id.edit_from_amount) {
            //fromCharCode = (String) fromSpinner.getSelectedItem();
            //toCharCode = (String) toSpinner.getSelectedItem();
            fromCharCode = fromCursor.getString(1);
            toCharCode = toCursor.getString(1);
        } else if (v.getId() == R.id.edit_to_amount) {
            //fromCharCode = (String) toSpinner.getSelectedItem();
            //toCharCode = (String) fromSpinner.getSelectedItem();
            fromCharCode = toCursor.getString(1);
            toCharCode = fromCursor.getString(1);
        }*/

        /*Currency currencyFrom = null;
        Currency currencyTo = null;
        CharCode codeFrom = null;
        CharCode codeTo = null;
        for (CharCode code : CharCode.values()) {
            if (code.getName().equals(fromCharCode)) {
                currencyFrom = currencyMap.get(code);
                codeFrom = code;
            }
            if (code.getName().equals(toCharCode)) {
                currencyTo = currencyMap.get(code);
                codeTo = code;
            }
            if (currencyFrom != null && currencyTo != null) {
                break;
            }
        }*/

        //assert currencyFrom != null;
        //double currencyFromNominal = currencyFrom.getDoubleNominal();
        //double currencyFromToRubRate = currencyFrom.getValue();
        //double currencyFromNominal = (double) fromCursor.getInt(2);
        //double currencyFromToRubRate = fromCursor.getDouble(3);


        //assert currencyTo != null;
        //double currencyToNominal = currencyTo.getNominal();
        //double currencyToToRubRate = currencyTo.getValue();
        //double currencyToNominal = (double) toCursor.getInt(2);
        //double currencyToToRubRate = toCursor.getDouble(3);

        if (isNeedToReSwapValues && (v.getId() != R.id.edit_to_amount)) {
            double tempValFrom = currencyFromToRubRate;
            currencyFromToRubRate = currencyToToRubRate;
            currencyToToRubRate = tempValFrom;

            double tempNomFrom = currencyFromNominal;
            currencyFromNominal = currencyToNominal;
            currencyToNominal = tempNomFrom;

            isNeedToReSwapValues = false;
        }

        if (!isNeedToReSwapValues && (v.getId() == R.id.edit_to_amount)) {
            double tempValFrom = currencyFromToRubRate;
            currencyFromToRubRate = currencyToToRubRate;
            currencyToToRubRate = tempValFrom;

            double tempNomFrom = currencyFromNominal;
            currencyFromNominal = currencyToNominal;
            currencyToNominal = tempNomFrom;

            isNeedToReSwapValues = true;
        }

        double enteredAmountOfMoney = getEnteredAmountOfMoney(v);

        double result = enteredAmountOfMoney *
                (currencyFromToRubRate / currencyToToRubRate) *
                (currencyToNominal / currencyFromNominal);

        /*String text = String.format("%.2f %s\n=\n%.2f %s",
                enteredAmountOfMoney,
                codeFrom.getName(),
                result,
                codeTo.getName());
        setTvResultText(text);*/

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

        //fromCursor.close();
        //toCursor.close();
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

    /*private String[] fillCurrencyArrays() {
        if (currencyMap == null) {
            return new String[]{};
        }

        int len = currencyMap.size();
        String[] currencyNameArray = new String[len];
        countryFlagIds = new Integer[len];
        currencyCharCodes = new String[len];

        int i = 0;
        for (EnumMap.Entry<CharCode, Currency> entry : currencyMap.entrySet()) {
            currencyNameArray[i] = getString(entry.getValue().getNameResourceId());

            currencyCharCodes[i] = entry.getKey().toString();

            int id = entry.getValue().getFlagResourceId();
            if (id != 0) {
                countryFlagIds[i] = id;
            } else {
                countryFlagIds[i] = R.drawable.empty;
            }
            i++;
        }
        return currencyNameArray;
    }*/

    /*private void setTvResultText(String text) {
        tvResult.setText(text);
    }*/

    @Override
    public void initSpinners() {
        SpinnerCursorAdapter cursorAdapter =
                new SpinnerCursorAdapter(getApplicationContext(), cursor, 0);

        fromSpinner = (Spinner) findViewById(R.id.from_spinner);
        //fromSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, fillCurrencyArrays()));
        fromSpinner.setAdapter(cursorAdapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCursor = (Cursor) parent.getItemAtPosition(position);
                currencyFromNominal = (double) fromCursor.getInt(2);
                currencyFromToRubRate = fromCursor.getDouble(3);

                fromSpinnerSelectedPos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toSpinner = (Spinner) findViewById(R.id.to_spinner);
        //toSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, fillCurrencyArrays()));
        toSpinner.setAdapter(cursorAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCursor = (Cursor) parent.getItemAtPosition(position);
                currencyToNominal = (double) toCursor.getInt(2);
                currencyToToRubRate = toCursor.getDouble(3);

                toSpinnerSelectedPos = position;
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

    //http://www.coderzheaven.com/2011/07/18/customizing-a-spinner-in-android/
    /*public class SpinnerApapter extends ArrayAdapter<String> {

        public SpinnerApapter(Context context, int resource, String[] objects) {
            super(context, resource, objects);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.spinner_item, parent, false);


            TextView currencyName = (TextView) row.findViewById(R.id.spinner_currency_name);
            currencyName.setText(fillCurrencyArrays()[position]);

            TextView currencyCharCode =
                    (TextView) row.findViewById(R.id.spinner_currency_char_code);
            currencyCharCode.setText(currencyCharCodes[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.spinner_flag_icon);
            icon.setImageResource(countryFlagIds[position]);

            return row;
        }
    }*/

    private class SpinnerCursorAdapter extends CursorAdapter {

        public SpinnerCursorAdapter(Context context, Cursor cursor, int flags) {
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

        editor.putInt(getString(R.string.saved_from_spinner_pos),
                fromSpinnerSelectedPos);
        editor.putInt(getString(R.string.saved_to_spinner_pos),
                toSpinnerSelectedPos);

        String editFromStr = editFromAmount.getText().toString();
        editor.putString(getString(R.string.saved_from_edit_amount_text),
                editFromAmount.getText().toString());
        String editToStr = editToAmount.getText().toString();
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
        }

        editor.apply();
    }

    private void loadProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String editFromAmountText =
                preferences.getString(getString(R.string.saved_from_edit_amount_text),
                        getString(R.string.saved_edit_amount_text_default));
        editFromAmount.setText("");
        editFromAmount.setText(editFromAmountText);
        String editToAmountText =
                preferences.getString(getString(R.string.saved_to_edit_amount_text),
                        getString(R.string.saved_edit_amount_text_default));
        editToAmount.setText(editToAmountText);

        String savedUpDateTime = null;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            savedUpDateTime = getString(R.string.saved_cb_rf_up_date_time);
        } else if (rateUpdater instanceof CustomRateUpdaterMock) {
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

        fromSpinnerSelectedPos =
                preferences.getInt(getString(R.string.saved_from_spinner_pos), 0);
        fromSpinner.setSelection(fromSpinnerSelectedPos);

        toSpinnerSelectedPos =
                preferences.getInt(getString(R.string.saved_to_spinner_pos), 0);
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
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
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
            Toast.makeText(this, R.string.custom_updating_info, Toast.LENGTH_LONG).show();
            stopRefresh();
        } else {
            loadRateUpdaterProperties();

            updateRatesFromSource();

            saveProperties();

            loadSpinnerProperties();
        }
    }
}
