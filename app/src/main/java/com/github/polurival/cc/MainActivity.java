package com.github.polurival.cc;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.polurival.cc.model.CBRateUpdater;
import com.github.polurival.cc.model.DBHelper;
import com.github.polurival.cc.model.DBReaderTask;
import com.github.polurival.cc.model.RateUpdater;
import com.github.polurival.cc.model.Currency;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.RateUpdaterListener;
import com.github.polurival.cc.util.DateUtil;

import org.joda.time.LocalDateTime;

import java.util.Calendar;
import java.util.EnumMap;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by Polurival
 * on 24.03.2016.
 */
public class MainActivity extends Activity implements RateUpdaterListener {

    private RateUpdater rateUpdater;
    private LocalDateTime upDateTime;

    private EnumMap<CharCode, Currency> currencyMap;
    private Integer[] countryFlagIds;

    private EditText editAmount;

    private Spinner fromSpinner;
    private Spinner toSpinner;


    private TextView tvResult;
    private TextView tvDateTime;

    @Override
    public void setCurrencyMap(EnumMap<CharCode, Currency> currencyMap) {
        this.currencyMap = currencyMap;
    }

    public void setUpDateTime(LocalDateTime upDateTime) {
        this.upDateTime = upDateTime;
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

        initEditAmount();

        loadProperties();

        tvResult = (TextView) findViewById(R.id.tvResult);
        tvDateTime = (TextView) findViewById(R.id.tvDateTime);

        if (DateUtil.compareUpDateWithCurrentDate(upDateTime)) {
            rateUpdater.setRateUpdaterListener(this);
            if (rateUpdater instanceof CBRateUpdater) {
                ((CBRateUpdater) rateUpdater).execute();
            }
        }

        tvDateTimeSetText();

        DBReaderTask dbReaderTask = new DBReaderTask();
        dbReaderTask.setRateUpdaterListener(this);
        if (rateUpdater instanceof CBRateUpdater) {
            dbReaderTask.execute(DBHelper.COLUMN_NAME_CB_RF_SOURCE, DBHelper.COLUMN_NAME_VALUE);
        }
    }

    @Override
    public void tvDateTimeSetText() {
        tvDateTime.setText(String.format("%s \n %s",
                rateUpdater.getDescription(), DateUtil.getUpDateTimeStr(upDateTime)));
    }

    private void initEditAmount() {
        editAmount = (EditText) findViewById(R.id.editAmount);
        editAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0) {
                    convert(editAmount);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public void swapFromTo(View v) {
        if (fromSpinner != null && toSpinner != null) {
            int fromSpinnerSelectedItemPos = fromSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
            toSpinner.setSelection(fromSpinnerSelectedItemPos);
        }
    }

    public void convert(View v) {
        if (currencyMap == null
                || fromSpinner.getSelectedItem() == null
                || toSpinner.getSelectedItem() == null) {
            return;
        }
        String fromCharCode = (String) fromSpinner.getSelectedItem();
        String toCharCode = (String) toSpinner.getSelectedItem();
        Currency currencyFrom = null;
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
        }

        assert currencyFrom != null;
        double currencyFromNominal = currencyFrom.getDoubleNominal();
        double currencyFromToRubRate = currencyFrom.getValue();

        assert currencyTo != null;
        int currencyToNominal = currencyTo.getNominal();
        double currencyToToRubRate = currencyTo.getValue();

        double enteredAmountOfMoney = getEnteredAmountOfMoney();
        double result = enteredAmountOfMoney *
                (currencyFromToRubRate / currencyToToRubRate) *
                (currencyToNominal / currencyFromNominal);

        String text = String.format("%.2f %s\n=\n%.2f %s",
                enteredAmountOfMoney,
                codeFrom.getName(),
                result,
                codeTo.getName());
        setTvResultText(text);
    }

    private double getEnteredAmountOfMoney() {
        if (TextUtils.isEmpty(editAmount.getText().toString())) {
            return 1d;
        }
        return (double) Float.parseFloat(editAmount.getText().toString());
    }

    private String[] fillCurrencyArrays() {
        if (currencyMap == null) {
            return new String[]{};
        }

        int len = currencyMap.size();
        String[] currencyNameArray = new String[len];
        countryFlagIds = new Integer[len];

        int i = 0;
        for (EnumMap.Entry<CharCode, Currency> entry : currencyMap.entrySet()) {
            currencyNameArray[i] = getString(entry.getValue().getNameResourceId());

            int id = entry.getValue().getFlagResourceId();
            if (id != 0) {
                countryFlagIds[i] = id;
            } else {
                countryFlagIds[i] = R.drawable.empty;
            }
            i++;
        }
        return currencyNameArray;
    }

    private void setTvResultText(String text) {
        tvResult.setText(text);
    }

    @Override
    public void initSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                fillCurrencyArrays());
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        //fromSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, fillCurrencyArrays()));
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert(fromSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toSpinner = (Spinner) findViewById(R.id.toSpinner);
        toSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, fillCurrencyArrays()));
        //toSpinner.setAdapter(adapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                convert(toSpinner);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    //http://www.coderzheaven.com/2011/07/18/customizing-a-spinner-in-android/
    public class SpinnerApapter extends ArrayAdapter<String> {

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

            TextView label = (TextView) row.findViewById(R.id.spinnerValuteName);
            label.setText(fillCurrencyArrays()[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.spinnerFlagIcon);
            icon.setImageResource(countryFlagIds[position]);

            return row;
        }
    }

    @Override
    protected void onStop() {
        saveProperties();
        super.onStop();
    }

    private void saveProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(getString(R.string.saved_from_spinner_pos),
                fromSpinner.getSelectedItemPosition());
        editor.putInt(getString(R.string.saved_to_spinner_pos),
                toSpinner.getSelectedItemPosition());
        editor.putString(getString(R.string.saved_edit_amount_text),
                editAmount.getText().toString());
        editor.putString(getString(R.string.saved_rate_updater_class),
                rateUpdater.getClass().getName());

        editor.apply();
    }

    @Override
    public void saveDateProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        if (rateUpdater instanceof CBRateUpdater) {
            editor.putString(getString(R.string.saved_cb_rf_up_date_time),
                    DateUtil.getUpDateTimeStr(upDateTime));
        }

        editor.apply();
    }

    private void loadProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String editAmountText =
                preferences.getString(getString(R.string.saved_edit_amount_text),
                        getString(R.string.saved_edit_amount_text_default));
        editAmount.setText(editAmountText);

        String rateUpdaterName =
                preferences.getString(getString(R.string.saved_rate_updater_class),
                        getString(R.string.saved_rate_updater_class_default));
        try {
            rateUpdater
                    = (RateUpdater) Class.forName(rateUpdaterName).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String formattedUpDateTime = null;
        if (rateUpdater instanceof CBRateUpdater) {
            formattedUpDateTime =
                    preferences.getString(getString(R.string.saved_cb_rf_up_date_time),
                            DateUtil.getDefaultDateTimeStr());
        }
        upDateTime = DateUtil.getUpDateTime(formattedUpDateTime);
    }

    @Override
    public void loadSpinnerProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        int fromSpinnerSelectedPos =
                preferences.getInt(getString(R.string.saved_from_spinner_pos), 0);
        fromSpinner.setSelection(fromSpinnerSelectedPos);

        int toSpinnerSelectedPos =
                preferences.getInt(getString(R.string.saved_to_spinner_pos), 0);
        toSpinner.setSelection(toSpinnerSelectedPos);
    }
}
