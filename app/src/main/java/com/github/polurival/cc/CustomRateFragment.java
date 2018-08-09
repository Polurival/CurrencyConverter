package com.github.polurival.cc;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.polurival.cc.adapter.SpinnerCursorAdapter;
import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CustomRateFragment extends Fragment implements View.OnClickListener {

    private Cursor spinnerCursor;
    private SpinnerCursorAdapter cursorAdapter;

    private EditText editCustomCurrency;
    private Spinner customCurrencySpinner;
    private TextView tvCharCode;
    private TextView tvCustomCurrencyNominal;
    private TextView tvCustomModeHelp;

    public CustomRateFragment() {
    }

    public Cursor getSpinnerCursor() {
        return spinnerCursor;
    }

    public Spinner getCustomCurrencySpinner() {
        return customCurrencySpinner;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.logD(Logger.getTag(), "onCreateView");

        View fragmentView = inflater.inflate(R.layout.fragment_custom_rates, container, false);

        ImageButton btnHint = fragmentView.findViewById(R.id.btn_custom_hint);
        btnHint.setOnClickListener(this);
        ImageButton btnSave =  fragmentView.findViewById(R.id.btn_custom_save);
        btnSave.setOnClickListener(this);

        editCustomCurrency =  fragmentView.findViewById(R.id.edit_custom_currency);
        customCurrencySpinner =  fragmentView.findViewById(R.id.custom_currency_spinner);

        tvCharCode =  fragmentView.findViewById(R.id.tv_char_code);
        tvCustomCurrencyNominal = fragmentView.findViewById(R.id.tv_custom_currency_nominal);
        tvCustomModeHelp = fragmentView.findViewById(R.id.tv_custom_mode_help);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.logD(Logger.getTag(), "onStart");

        readSpinnerDataFromDB();
    }

    @Override
    public void onStop() {
        Logger.logD(Logger.getTag(), "onStop");

        spinnerCursor.close();

        super.onStop();
    }

    @Override
    public void onClick(View v) {
        Logger.logD(Logger.getTag(), "onClick");

        switch (v.getId()) {
            case R.id.btn_custom_hint:
                showHideHint(v);
                break;

            case R.id.btn_custom_save:
                saveCurrencyCustomValueAndCustomNominal();
                break;
        }
    }

    private void showHideHint(View view) {
        Logger.logD(Logger.getTag(), "showHideHint");

        if (tvCustomModeHelp.isShown()) {
            tvCustomModeHelp.setVisibility(View.GONE);
        } else {
            tvCustomModeHelp.setVisibility(View.VISIBLE);
            final InputMethodManager inputMethodManager =
                    (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void saveCurrencyCustomValueAndCustomNominal() {
        Logger.logD(Logger.getTag(), "saveCurrencyCustomValueAndCustomNominal");

        if (null == customCurrencySpinner || customCurrencySpinner.getCount() == 0) {
            Toaster.showToast(getActivity().getString(R.string.all_currencies_disabled));
            return;
        }

        Cursor currencyCharCodeCursor = (Cursor) customCurrencySpinner.getSelectedItem();
        final String currencyCharCode = currencyCharCodeCursor.getString(1);

        String customRate = editCustomCurrency.getText().toString();
        customRate = customRate.replace(",", ".");
        if ("".equals(customRate) || (Double.valueOf(customRate) == 0)) {
            Toaster.showToast(getActivity().getString(R.string.db_custom_update_invalid_value));
            return;
        }

        Object[] customNominalAndRate = getCustomNominalAndRate(customRate);
        final int preparedCustomNominal = (int) customNominalAndRate[0];
        String preparedCustomRate = (String) customNominalAndRate[1];

        final ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NAME_CUSTOM_NOMINAL, preparedCustomNominal);
        contentValues.put(DBHelper.COLUMN_NAME_CUSTOM_RATE, preparedCustomRate);

        final SQLiteDatabase db = DBHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                db.beginTransaction();
                try {
                    db.update(DBHelper.TABLE_NAME,
                            contentValues,
                            DBHelper.COLUMN_NAME_CHAR_CODE + " = ?",
                            new String[]{currencyCharCode});
                    db.setTransactionSuccessful();

                    Toaster.showToast(getActivity().getString(R.string.db_custom_update_success) + preparedCustomNominal);

                    AppPreferences.saveCustomRateUpDateTime(getActivity());
                    saveCustomSpinnerSelectedPos();
                    readSpinnerDataFromDB();

                } catch (SQLiteException e) {
                    Toaster.showToast(getActivity().getString(R.string.db_writing_error));
                } finally {
                    db.endTransaction();
                }
            }
        });
    }

    /**
     * todo использовать Currency или еще что-то вместо Object[]
     */
    private Object[] getCustomNominalAndRate(String customRate) {
        Logger.logD(Logger.getTag(), "getCustomNominalAndRate");

        int nominal = 1;
        double rate = Double.valueOf(customRate);
        if (rate < 1) {
            int i = 0;
            while (rate < 1) {
                rate *= 10;
                i++;
            }
            nominal = (int) Math.pow(10, i);
        }

        return new Object[]{nominal, getFormattedCustomCurrencyText(rate)};
    }

    private void readSpinnerDataFromDB() {
        Logger.logD(Logger.getTag(), "readSpinnerDataFromDB");

        final SQLiteDatabase db = DBHelper.getInstance(getActivity().getApplicationContext()).getWritableDatabase();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    spinnerCursor = db.query(DBHelper.TABLE_NAME,
                            new String[]{DBHelper.COLUMN_NAME_ID,
                                    DBHelper.COLUMN_NAME_CHAR_CODE,
                                    DBHelper.COLUMN_NAME_CUSTOM_NOMINAL,
                                    DBHelper.COLUMN_NAME_CUSTOM_RATE,
                                    DBHelper.COLUMN_NAME_CURRENCY_NAME,
                                    DBHelper.COLUMN_NAME_FLAG_ID},
                            DBHelper.COLUMN_NAME_SWITCHING + " = 1 AND " +
                                    DBHelper.COLUMN_NAME_CHAR_CODE + " != ?",
                            new String[]{CharCode.USD.toString()}, null, null, null);

                    initCustomSpinner();
                    initCurrencyDataFromCustomSpinnerCursor();

                } catch (SQLiteException e) {
                    Toaster.showToast(getActivity().getString(R.string.db_reading_error));
                }
            }
        });
    }

    private void initCustomSpinner() {
        Logger.logD(Logger.getTag(), "initCustomSpinner");

        if (cursorAdapter == null) {
            cursorAdapter = new SpinnerCursorAdapter(getActivity(), spinnerCursor);
            customCurrencySpinner.setAdapter(cursorAdapter);
            customCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    initCurrencyDataFromCustomSpinnerCursor();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            cursorAdapter.changeCursor(spinnerCursor);
            cursorAdapter.notifyDataSetChanged();
        }
        loadCustomSpinnerSelectedPos();
    }

    private void initCurrencyDataFromCustomSpinnerCursor() {
        Logger.logD(Logger.getTag(), "initCurrencyDataFromCustomSpinnerCursor");

        if (null != customCurrencySpinner && customCurrencySpinner.getCount() != 0) {
            Cursor currencyCursor = (Cursor) customCurrencySpinner.getSelectedItem();

            double currencyRate = currencyCursor.getDouble(3);
            editCustomCurrency.setText(getFormattedCustomCurrencyText(currencyRate));

            String charCode = currencyCursor.getString(1);
            tvCharCode.setText(charCode);

            int currencyNominal = currencyCursor.getInt(2);
            tvCustomCurrencyNominal.setText(String.format("%s %s",
                    getActivity().getString(R.string.custom_currency_nominal), currencyNominal));
        } else {
            Toaster.showToast(getActivity().getString(R.string.all_currencies_disabled));
        }
    }

    private String getFormattedCustomCurrencyText(double currencyRate) {
        Logger.logD(Logger.getTag(), "getFormattedCustomCurrencyText");

        BigDecimal currencyRateIndependentOfLocale =
                BigDecimal.valueOf(currencyRate).setScale(6, RoundingMode.HALF_EVEN);

        return currencyRateIndependentOfLocale.toPlainString();
    }

    private void saveCustomSpinnerSelectedPos() {
        Logger.logD(Logger.getTag(), "saveCustomSpinnerSelectedPos");

        AppPreferences.saveCustomRateFragmentCustomSpinnerSelectedPosition(getActivity(),
                customCurrencySpinner.getSelectedItemPosition());
    }

    private void loadCustomSpinnerSelectedPos() {
        Logger.logD(Logger.getTag(), "loadCustomSpinnerSelectedPos");

        final int customSpinnerSelectedPosition =
                AppPreferences.loadCustomRateFragmentCustomSpinnerSelectedPosition(getActivity());
        customCurrencySpinner.setSelection(customSpinnerSelectedPosition);
    }
}
