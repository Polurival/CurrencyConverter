package com.polurival.cuco;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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

import com.polurival.cuco.model.CBRateUpdater;
import com.polurival.cuco.model.RateUpdater;
import com.polurival.cuco.model.Currency;
import com.polurival.cuco.model.CurrencyCharCode;
import com.polurival.cuco.util.DateUtil;

import java.util.ArrayList;
import java.util.EnumMap;

/**
 * Created by Polurival
 * on 24.03.2016.
 */
public class MainActivity extends Activity {

    private static MainActivity mainActivity;
    private RateUpdater rateUpdater;

    private EnumMap<CurrencyCharCode, Currency> currencyMap;
    private Integer[] countryFlagIds;

    private EditText editAmount;

    private Spinner fromSpinner;
    private Spinner toSpinner;


    private TextView tvResult;
    private TextView tvDateTime;

    public void setCurrencyMap(EnumMap<CurrencyCharCode, Currency> currencyMap) {
        this.currencyMap = currencyMap;
    }

    public RateUpdater getRateUpdater() {
        return rateUpdater;
    }

    public static MainActivity getInstance() {
        return mainActivity;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        if (mainActivity == null) {
            mainActivity = this;
        }

        initEditAmount();

        tvResult = (TextView) findViewById(R.id.tvResult);

        rateUpdater = new CBRateUpdater();
        if (rateUpdater instanceof CBRateUpdater) {
            ((CBRateUpdater) rateUpdater).execute();
        }


        tvDateTime = (TextView) findViewById(R.id.tvDateTime);
        tvDateTime.setText(DateUtil.getCurrentDateTime());
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
        CurrencyCharCode codeFrom = null;
        CurrencyCharCode codeTo = null;
        for (CurrencyCharCode code : CurrencyCharCode.values()) {
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
        double currencyFromNominal = Double.valueOf(currencyFrom.getNominal());
        double currencyFromToRubRate = Double.valueOf(currencyFrom.getCurrencyToRubRate());

        assert currencyTo != null;
        double currencyToNominal = Integer.valueOf(currencyTo.getNominal());
        double currencyToToRubRate = Double.valueOf(currencyTo.getCurrencyToRubRate());

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

    private String[] getFilledCurrencyArray() {
        if (currencyMap == null) {
            return new String[]{};
        }

        int len = currencyMap.size();
        String[] currencyNameArray = new String[len];
        countryFlagIds = new Integer[len];

        int i = 0;
        for (CurrencyCharCode code : currencyMap.keySet()) {
            currencyNameArray[i] = code.getName();

            String codeInLowerCase = code.toString().toLowerCase();
            if ("try".equals(codeInLowerCase)) {
                codeInLowerCase += '_';
            }

            int id = getDrawable(this, codeInLowerCase);
            if (id != 0) {
                countryFlagIds[i] = id;
            } else {
                countryFlagIds[i] = R.drawable.empty;
            }
            i++;
        }
        return currencyNameArray;
    }

    public int getDrawable(Context context, String name) {
        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    private void setTvResultText(String text) {
        tvResult.setText(text);
    }

    public void initSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.spinner_item,
                getFilledCurrencyArray());
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        //fromSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, getFilledCurrencyArray()));
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
        toSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, getFilledCurrencyArray()));
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
            label.setText(getFilledCurrencyArray()[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.spinnerFlagIcon);
            icon.setImageResource(countryFlagIds[position]);

            return row;
        }
    }
}
