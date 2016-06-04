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
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.polurival.cc.model.CBRateUpdaterTask;
import com.github.polurival.cc.model.CustomRateUpdaterMock;
import com.github.polurival.cc.model.RateUpdater;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.db.DBReaderTaskListener;
import com.github.polurival.cc.util.DateUtil;

import java.text.DecimalFormat;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRateFragment extends Fragment
        implements View.OnClickListener, DBReaderTaskListener {

    private RateUpdater rateUpdater = new CustomRateUpdaterMock();

    private SQLiteDatabase db;
    private Cursor SpinnerCursor;
    private SpinnerCursorAdapter cursorAdapter;

    private EditText editCustomCurrency;
    private Spinner customCurrencySpinner;
    private ImageButton btnHint;
    private Button btnSave;
    TextView tvCustomModeHelp;
    private View fragmentView;

    public CustomRateFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_custom_rates, container, false);

        btnHint = (ImageButton) fragmentView.findViewById(R.id.btn_custom_hint);
        btnHint.setOnClickListener(this);
        btnSave = (Button) fragmentView.findViewById(R.id.btn_custom_save);
        btnSave.setOnClickListener(this);

        editCustomCurrency = (EditText) fragmentView.findViewById(R.id.edit_custom_currency);
        customCurrencySpinner = (Spinner) fragmentView.findViewById(R.id.custom_currency_spinner);

        tvCustomModeHelp = (TextView) fragmentView.findViewById(R.id.tv_custom_mode_help);

        return fragmentView;
    }

    @Override
    public void onStart() {
        readSpinnerDataFromDB();
        super.onStart();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_custom_hint:
                showHideHint();
                break;

            case R.id.btn_custom_save:

                //final String currencyName = (String) customCurrencySpinner.getSelectedItem();
                Cursor currencyNameCursor = (Cursor) customCurrencySpinner.getSelectedItem();
                final int currencyNameId = currencyNameCursor.getInt(4);
                String customValue = editCustomCurrency.getText().toString();

                Object[] customNominalAndValue = getCustomNominalAndValue(customValue);
                final int preparedCustomNominal = (int) customNominalAndValue[0];
                String preparedCustomValue = (String) customNominalAndValue[1];

                final ContentValues contentValues = new ContentValues();
                contentValues.put(DBHelper.COLUMN_NAME_CUSTOM_NOMINAL, preparedCustomNominal);
                contentValues.put(DBHelper.COLUMN_NAME_CUSTOM_VALUE, preparedCustomValue);

                Handler handler = new Handler();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            db = DBHelper.getInstance(AppContext.getContext()).getWritableDatabase();
                            db.update(DBHelper.TABLE_NAME,
                                    contentValues,
                                    DBHelper.COLUMN_NAME_NAME_RESOURCE_ID + " = ?",
                                    new String[]{String.valueOf(currencyNameId)});
                        } catch (SQLiteException e) {
                            Toast.makeText(AppContext.getContext(),
                                    AppContext.getContext().getString(R.string.database_error),
                                    Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(AppContext.getContext(),
                                AppContext.getContext().getString(
                                        R.string.database_custom_update_success)
                                        + preparedCustomNominal,
                                Toast.LENGTH_LONG).show();
                        saveDateProperties();
                    }
                });
                break;
        }
    }

    private String prepareCustomValue(double customValue) {

        double roundedValue = Math.round(customValue * 10000.0) / 10000.0;

        String customValueStr = String.format("%.8f", roundedValue).replace(",", ".");
        // Double.toString(roundedValue);

        if (!customValueStr.contains(".")) {
            customValueStr += ".0";
        } else if (customValueStr.substring(customValueStr.indexOf('.')).length() > 5) {
            String[] customValueArr = customValueStr.split("\\.");
            customValueStr = customValueArr[0] + "." + customValueArr[1].substring(0, 4);
        } else if (customValue == 0) {
            customValueStr = "1.0";
        }
        return customValueStr;
    }

    private Object[] getCustomNominalAndValue(String customValue) {
        int nominal = 1;
        //double value = (double) Float.parseFloat(customValue);
        //if (customValue.contains(",")) {
        customValue = customValue.replace(",", ".");
        //}
        double value = Double.valueOf(customValue);
        if (value < 1) {
            int i = 0;
            while (value < 1) {
                value *= 10;
                i++;
            }
            nominal = (int) Math.pow(10, i);
        }

        String customValueStr = prepareCustomValue(value);

        return new Object[]{nominal, customValueStr};
    }

    private void showHideHint() {
        if (tvCustomModeHelp.isShown()) {
            tvCustomModeHelp.setVisibility(View.INVISIBLE);
        } else {
            tvCustomModeHelp.setVisibility(View.VISIBLE);
        }
    }

    //https://github.com/codepath/android_guides/wiki/Populating-a-ListView-with-a-CursorAdapter
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
            TextView currencyName = (TextView) view.findViewById(R.id.spinner_currency_name);
            ImageView flagIcon = (ImageView) view.findViewById(R.id.spinner_flag_icon);

            int currencyNameId = cursor.getInt(4);
            int flagIconId = cursor.getInt(5);

            currencyName.setText(getString(currencyNameId));
            flagIcon.setImageResource(flagIconId);
        }
    }

    public void readSpinnerDataFromDB() {
        DBReaderTask dbReaderTask = new DBReaderTask();
        dbReaderTask.setRateUpdaterListener(null);
        dbReaderTask.setDBReaderTaskListener(this);
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

    public void readEditCurrencyDataFromDB() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                Cursor certainCurrencyCursor = null;
                try {
                    Cursor currencyNameCursor = (Cursor) customCurrencySpinner.getSelectedItem();
                    int currencyNameId = currencyNameCursor.getInt(4);

                    db = DBHelper.getInstance(AppContext.getContext()).getReadableDatabase();
                    certainCurrencyCursor = db.query(DBHelper.TABLE_NAME,
                            new String[]{DBHelper.COLUMN_NAME_CUSTOM_VALUE},
                            DBHelper.COLUMN_NAME_NAME_RESOURCE_ID + " = ?",
                            new String[]{String.valueOf(currencyNameId)},
                            null, null, null, null);
                } catch (SQLiteException e) {
                    Toast.makeText(AppContext.getContext(),
                            AppContext.getContext().getString(R.string.database_error),
                            Toast.LENGTH_LONG).show();
                }
                assert certainCurrencyCursor != null;
                while (certainCurrencyCursor.moveToNext()) {
                    double currencyValue = certainCurrencyCursor.getDouble(0);
                    if (currencyValue < 0.001) {
                        editCustomCurrency.setText(String.format("%.6f", currencyValue));
                    } else {
                        //editCustomCurrency.setText(String.valueOf(certainCurrencyCursor.getDouble(0)));
                        editCustomCurrency.setText(String.format("%.4f", currencyValue));
                    }
                }
                certainCurrencyCursor.close();
            }
        });
    }

    @Override
    public void initCustomSpinner() {
        cursorAdapter =
                new SpinnerCursorAdapter(AppContext.getContext(), SpinnerCursor, 0);
        customCurrencySpinner.setAdapter(cursorAdapter);
        customCurrencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                readEditCurrencyDataFromDB();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void setCursorAndDB(Cursor cursor, SQLiteDatabase db) {
        this.SpinnerCursor = cursor;
        this.db = db;
    }

    @Override
    public void onStop() {
        SpinnerCursor.close();
        db.close();
        super.onStop();
    }

    public void saveDateProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(AppContext.getContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(getString(R.string.saved_custom_up_date_time),
                DateUtil.getUpDateTimeInSeconds(DateUtil.getCurrentDateTime()));

        editor.apply();
    }
}
