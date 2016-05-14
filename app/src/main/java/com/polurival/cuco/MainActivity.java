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

import com.polurival.cuco.strategies.CBRateUpdater;
import com.polurival.cuco.strategies.RateUpdater;
import com.polurival.cuco.strategies.Valute;
import com.polurival.cuco.strategies.ValuteCharCode;
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

    private EnumMap<ValuteCharCode, Valute> valuteMap;
    private Integer[] countryFlagIds;

    private EditText editAmount;

    private Spinner fromSpinner;
    private Spinner toSpinner;

    private TextView tvResult;
    private TextView tvDateTime;

    public void setValuteMap(EnumMap<ValuteCharCode, Valute> valuteMap) {
        this.valuteMap = valuteMap;
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
        int fromSpinnerSelectedItemPos = fromSpinner.getSelectedItemPosition();
        fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
        toSpinner.setSelection(fromSpinnerSelectedItemPos);
    }

    public void convert(View v) {
        if (valuteMap == null
                || fromSpinner.getSelectedItem() == null
                || toSpinner.getSelectedItem() == null) {
            return;
        }
        String fromCharCode = (String) fromSpinner.getSelectedItem();
        String toCharCode = (String) toSpinner.getSelectedItem();
        Valute valuteFrom = null;
        Valute valuteTo = null;
        ValuteCharCode codeFrom = null;
        ValuteCharCode codeTo = null;
        for (ValuteCharCode code : ValuteCharCode.values()) {
            if (code.getName().equals(fromCharCode)) {
                valuteFrom = valuteMap.get(code);
                codeFrom = code;
            }
            if (code.getName().equals(toCharCode)) {
                valuteTo = valuteMap.get(code);
                codeTo = code;
            }
            if (valuteFrom != null && valuteTo != null) {
                break;
            }
        }

        assert valuteFrom != null;
        double valuteFromNominal = Double.valueOf(valuteFrom.getNominal());
        double valuteFromToRubRate = Double.valueOf(valuteFrom.getValuteToRubRate());

        assert valuteTo != null;
        double valuteToNominal = Integer.valueOf(valuteTo.getNominal());
        double valuteToToRubRate = Double.valueOf(valuteTo.getValuteToRubRate());

        double inputedAmountOfMoney = getInputedAmountOfMoney();
        double result = inputedAmountOfMoney *
                (valuteFromToRubRate / valuteToToRubRate) *
                (valuteToNominal / valuteFromNominal);

        String text = String.format("%.2f %s\n=\n%.2f %s",
                inputedAmountOfMoney,
                codeFrom.getName(),
                result,
                codeTo.getName());
        setTvResultText(text);
    }

    private double getInputedAmountOfMoney() {
        if (TextUtils.isEmpty(editAmount.getText().toString())) {
            return 1d;
        }
        return (double) Float.parseFloat(editAmount.getText().toString());
    }

    private String[] getFilledValuteArray() {
        if (valuteMap == null) {
            return new String[]{};
        }

        int len = valuteMap.size();
        String[] valuteNameArray = new String[len];
        countryFlagIds = new Integer[len];

        int i = 0;
        for (ValuteCharCode code : valuteMap.keySet()) {
            valuteNameArray[i] = code.getName();
            int id = getDrawable(this, code.toString().toLowerCase());
            if (id != 0) {
                countryFlagIds[i] = getDrawable(this, code.toString().toLowerCase());
            } else {
                countryFlagIds[i] = R.drawable.empty;
            }
            i++;
        }
        return valuteNameArray;
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
                getFilledValuteArray());
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        //fromSpinner.setAdapter(adapter);
        fromSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, getFilledValuteArray()));
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
        toSpinner.setAdapter(new SpinnerApapter(this, R.layout.spinner_item, getFilledValuteArray()));
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

    private void initCountryFlagIds(ArrayList<Integer> countryFlagIds) {
        //countryFlagIds.add(R.drawable.rub);
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
            label.setText(getFilledValuteArray()[position]);

            ImageView icon = (ImageView) row.findViewById(R.id.spinnerFlagIcon);
            icon.setImageResource(countryFlagIds[position]);

            return row;
        }
    }
}
