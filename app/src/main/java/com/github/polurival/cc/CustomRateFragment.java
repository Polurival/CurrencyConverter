package com.github.polurival.cc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.polurival.cc.model.CBRateUpdaterTask;
import com.github.polurival.cc.model.CustomRateUpdaterMock;
import com.github.polurival.cc.model.RateUpdater;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.db.DBReaderTaskListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomRateFragment extends Fragment
        implements View.OnClickListener, DBReaderTaskListener {

    private RateUpdater rateUpdater = new CustomRateUpdaterMock();

    private SQLiteDatabase db;
    private Cursor cursor;

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
        readDataFromDB();
        super.onStart();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_custom_hint:
                showHideHint();
                break;
            case R.id.btn_custom_save:
                //customCurrencySpinner.getSelectedItem();
                //DBHelper.saveCurrencyRate(code, editCustomCurrency.getText());
                break;
        }
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

    public void readDataFromDB() {
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

    @Override
    public void initCustomSpinner() {
        SpinnerCursorAdapter cursorAdapter =
                new SpinnerCursorAdapter(AppContext.getContext(), cursor, 0);
        customCurrencySpinner.setAdapter(cursorAdapter);
    }

    @Override
    public void setCursorAndDB(Cursor cursor, SQLiteDatabase db) {
        this.cursor = cursor;
        this.db = db;
    }


    @Override
    public void onStop() {
        cursor.close();
        db.close();
        super.onStop();
    }
}
