package com.github.polurival.cc;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.polurival.cc.model.CharCode;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.util.DateUtil;

/**
 * Created by Polurival
 * on 07.06.2016.
 */
public class CustomRateFragment extends Fragment implements View.OnClickListener {

    private Context appContext;

    private Cursor spinnerCursor;

    private EditText editCustomCurrency;
    private Spinner customCurrencySpinner;
    private TextView tvCharCode;
    private TextView tvCustomCurrencyNominal;
    private TextView tvCustomModeHelp;

    public CustomRateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_custom_rates, container, false);

        ImageButton btnHint = (ImageButton) fragmentView.findViewById(R.id.btn_custom_hint);
        btnHint.setOnClickListener(this);
        ImageButton btnSave = (ImageButton) fragmentView.findViewById(R.id.btn_custom_save);
        btnSave.setOnClickListener(this);

        editCustomCurrency = (EditText) fragmentView.findViewById(R.id.edit_custom_currency);
        customCurrencySpinner = (Spinner) fragmentView.findViewById(R.id.custom_currency_spinner);

        tvCharCode = (TextView) fragmentView.findViewById(R.id.tv_char_code);
        tvCustomCurrencyNominal =
                (TextView) fragmentView.findViewById(R.id.tv_custom_currency_nominal);
        tvCustomModeHelp = (TextView) fragmentView.findViewById(R.id.tv_custom_mode_help);

        return fragmentView;
    }

    @Override
    public void onResume() {
        super.onResume();

        appContext = AppContext.getContext();
        readSpinnerDataFromDB();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_custom_hint:
                showHideHint();
                break;

            case R.id.btn_custom_save:
                saveCurrencyCustomValueAndCustomNominal();
                break;
        }
    }

    private void saveCurrencyCustomValueAndCustomNominal() {
        if (null == customCurrencySpinner || customCurrencySpinner.getCount() == 0) {
            Toast.makeText(appContext,
                    appContext.getString(R.string.all_currencies_disabled),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Cursor currencyNameCursor = (Cursor) customCurrencySpinner.getSelectedItem();
        final int currencyNameId = currencyNameCursor.getInt(4);

        String customValue = editCustomCurrency.getText().toString();
        customValue = customValue.replace(",", ".");
        if ("".equals(customValue) || (Double.valueOf(customValue) == 0)) {
            Toast.makeText(appContext,
                    appContext.getString(R.string.db_custom_update_invalid_value),
                    Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Object[] customNominalAndValue = getCustomNominalAndValue(customValue);
        final int preparedCustomNominal = (int) customNominalAndValue[0];
        String preparedCustomValue = (String) customNominalAndValue[1];

        final ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NAME_CUSTOM_NOMINAL, preparedCustomNominal);
        contentValues.put(DBHelper.COLUMN_NAME_CUSTOM_RATE, preparedCustomValue);

        final SQLiteDatabase db = DBHelper.getInstance(appContext).getWritableDatabase();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    db.update(DBHelper.TABLE_NAME,
                            contentValues,
                            DBHelper.COLUMN_NAME_NAME_RESOURCE_ID + " = ?",
                            new String[]{String.valueOf(currencyNameId)});

                    Toast.makeText(appContext, appContext.getString(
                                    R.string.db_custom_update_success) + preparedCustomNominal,
                            Toast.LENGTH_SHORT)
                            .show();

                    saveCustomDateProperties();
                    saveCustomSpinnerSelectedPos();
                    readSpinnerDataFromDB();

                } catch (SQLiteException e) {
                    Toast.makeText(appContext, appContext.getString(R.string.db_writing_error),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private Object[] getCustomNominalAndValue(String customRate) {
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

        String customRateStr = String.format("%.4f", rate).replace(",", ".");

        return new Object[]{nominal, customRateStr};
    }

    private void showHideHint() {
        if (tvCustomModeHelp.isShown()) {
            tvCustomModeHelp.setVisibility(View.INVISIBLE);
        } else {
            tvCustomModeHelp.setVisibility(View.VISIBLE);
        }
    }

    /**
     * See <a href="https://github.com/codepath/android_guides/wiki/Populating-a-ListView-with-a-CursorAdapter">source</a>
     */
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

    private void readSpinnerDataFromDB() {
        final SQLiteDatabase db = DBHelper.getInstance(appContext).getWritableDatabase();
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
                                    DBHelper.COLUMN_NAME_NAME_RESOURCE_ID,
                                    DBHelper.COLUMN_NAME_FLAG_RESOURCE_ID},
                            DBHelper.COLUMN_NAME_SWITCHING + " = 1 AND " +
                                    DBHelper.COLUMN_NAME_CHAR_CODE + " != ?",
                            new String[]{CharCode.USD.toString()}, null, null, null);

                    initCustomSpinner();
                    initCurrencyDataFromCustomSpinnerCursor();

                } catch (SQLiteException e) {
                    Toast.makeText(appContext, appContext.getString(R.string.db_reading_error),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void initCurrencyDataFromCustomSpinnerCursor() {
        if (null != customCurrencySpinner && customCurrencySpinner.getCount() != 0) {
            double currencyRate = ((Cursor) customCurrencySpinner.getSelectedItem()).getDouble(3);
            setEditCustomCurrencyText(currencyRate);

            String charCode = ((Cursor) customCurrencySpinner.getSelectedItem()).getString(1);
            tvCharCode.setText(charCode);

            int currencyNominal = ((Cursor) customCurrencySpinner.getSelectedItem()).getInt(2);
            tvCustomCurrencyNominal.setText(String.format("%s%s",
                    appContext.getString(R.string.custom_currency_nominal), currencyNominal));
        } else {
            Toast.makeText(appContext,
                    appContext.getString(R.string.all_currencies_disabled),
                    Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void setEditCustomCurrencyText(double currencyRate) {
        editCustomCurrency.setText(String.format("%.4f", currencyRate).replace(",", "."));
    }

    private void initCustomSpinner() {
        SpinnerCursorAdapter cursorAdapter =
                new SpinnerCursorAdapter(appContext, spinnerCursor);
        customCurrencySpinner.setAdapter(cursorAdapter);
        loadCustomSpinnerSelectedPos();

        customCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                initCurrencyDataFromCustomSpinnerCursor();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    @Override
    public void onStop() {
        spinnerCursor.close();

        super.onStop();
    }

    private void saveCustomDateProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(getString(R.string.saved_custom_up_date_time),
                DateUtil.getUpDateTimeInSeconds(DateUtil.getCurrentDateTime()));

        editor.apply();
    }

    private void saveCustomSpinnerSelectedPos() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(appContext);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(getString(R.string.saved_custom_spinner_pos),
                customCurrencySpinner.getSelectedItemPosition());

        editor.apply();
    }

    private void loadCustomSpinnerSelectedPos() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(appContext);

        int customSpinnerSelectedPos =
                preferences.getInt(getString(R.string.saved_custom_spinner_pos), 0);
        customCurrencySpinner.setSelection(customSpinnerSelectedPos);
    }
}
