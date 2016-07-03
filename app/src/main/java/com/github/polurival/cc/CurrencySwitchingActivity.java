package com.github.polurival.cc;

import android.app.Activity;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.github.polurival.cc.adapter.ListViewCursorAdapter;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.util.Constants;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

public class CurrencySwitchingActivity extends Activity {

    private Cursor listCursor;
    private ListView lvAllCurrencies;

    private CheckBox cbTurnOnOffAllCurrencies;

    private int lvSelectedPos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.logD(Logger.getTag(), "onCreate");

        setContentView(R.layout.activity_currency_switching);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        lvSelectedPos = 0;

        lvAllCurrencies = (ListView) findViewById(R.id.lv_turn_on_off);
        cbTurnOnOffAllCurrencies = (CheckBox) findViewById(R.id.cb_turn_all_on_off);

        if (isAllCurrenciesTurnOn()) {
            cbTurnOnOffAllCurrencies.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.logD(Logger.getTag(), "onResume");

        readListDataFromDB();
    }

    @Override
    public void onStop() {
        Logger.logD(Logger.getTag(), "onStop");

        listCursor.close();

        super.onStop();
    }

    private void readListDataFromDB() {
        Logger.logD(Logger.getTag(), "readListDataFromDB");

        final SQLiteDatabase db =
                DBHelper.getInstance(getApplicationContext()).getWritableDatabase();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    listCursor = db.query(DBHelper.TABLE_NAME,
                            new String[]{DBHelper.COLUMN_NAME_ID,
                                    DBHelper.COLUMN_NAME_CHAR_CODE,
                                    DBHelper.COLUMN_NAME_NAME_RESOURCE_ID,
                                    DBHelper.COLUMN_NAME_FLAG_RESOURCE_ID,
                                    DBHelper.COLUMN_NAME_SWITCHING},
                            null, null, null, null, null);

                    initLvAllCurrencies();

                } catch (SQLiteException e) {
                    Toaster.showCenterToast(getString(R.string.db_reading_error));
                }
            }
        });
    }

    private void initLvAllCurrencies() {
        Logger.logD(Logger.getTag(), "initLvAllCurrencies");

        ListViewCursorAdapter cursorAdapter =
                new ListViewCursorAdapter(getApplicationContext(), listCursor);
        lvAllCurrencies.setAdapter(cursorAdapter);

        lvAllCurrencies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View rowLayout = getViewByPosition(position, lvAllCurrencies);

                lvSelectedPos = lvAllCurrencies.getFirstVisiblePosition();

                TextView tvCharCode = (TextView) ((ViewGroup) rowLayout).getChildAt(2);
                String currencyCharCode = tvCharCode.getText().toString();
                CheckBox currencyCondition = (CheckBox) ((ViewGroup) rowLayout).getChildAt(3);

                if (currencyCondition.isChecked()) {
                    saveCurrencyOnOffCondition(currencyCharCode, 0, Constants.SINGLE);
                } else {
                    saveCurrencyOnOffCondition(currencyCharCode, 1, Constants.SINGLE);
                }
            }
        });

        if (isAllCurrenciesTurnOn()) {
            cbTurnOnOffAllCurrencies.setChecked(true);
        } else {
            cbTurnOnOffAllCurrencies.setChecked(false);
        }

        lvAllCurrencies.setSelection(lvSelectedPos);
    }

    /**
     * See  <a href="http://stackoverflow.com/questions/24811536/android-listview-get-item-view-by-position">source</a>
     */
    private View getViewByPosition(int pos, ListView listView) {
        Logger.logD(Logger.getTag(), "getViewByPosition");

        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void turnOnOffAllCurrencies(View view) {
        Logger.logD(Logger.getTag(), "turnOnOffAllCurrencies");

        lvSelectedPos = 0;

        if (cbTurnOnOffAllCurrencies.isChecked()) {
            saveCurrencyOnOffCondition(null, 1, Constants.MULTIPLE);
        } else {
            saveCurrencyOnOffCondition(null, 0, Constants.MULTIPLE);
        }
    }

    private boolean isAllCurrenciesTurnOn() {
        Logger.logD(Logger.getTag(), "isAllCurrenciesTurnOn");

        for (int i = 0; i < lvAllCurrencies.getCount(); i++) {
            int currencyCondition = ((Cursor) lvAllCurrencies.getItemAtPosition(i)).getInt(4);
            if (currencyCondition == 0) {
                return false;
            }
        }
        return true;
    }

    private void saveCurrencyOnOffCondition(
            final String currencyCharCode, int selector, final String mode) {
        Logger.logD(Logger.getTag(), "saveCurrencyOnOffCondition");

        final ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NAME_SWITCHING, selector);

        final SQLiteDatabase db =
                DBHelper.getInstance(getApplicationContext()).getWritableDatabase();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Constants.SINGLE.equals(mode)) {
                        if (null == currencyCharCode) {
                            Logger.logD(Logger.getTag(), "SINGLE Error: currencyCharCode = null");
                        }
                        db.update(DBHelper.TABLE_NAME,
                                contentValues,
                                DBHelper.COLUMN_NAME_CHAR_CODE + " = ?",
                                new String[]{currencyCharCode});
                    } else {
                        db.update(DBHelper.TABLE_NAME,
                                contentValues,
                                null,
                                null);
                    }

                    saveDefaultPositionProperties();

                    readListDataFromDB();

                } catch (SQLiteException e) {
                    Toaster.showCenterToast(getString(R.string.db_writing_error));
                }
            }
        });
    }

    private void saveDefaultPositionProperties() {
        Logger.logD(Logger.getTag(), "saveDefaultPositionProperties");

        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(getString(R.string.saved_cb_rf_from_spinner_pos), 0);
        editor.putInt(getString(R.string.saved_cb_rf_to_spinner_pos), 0);

        editor.putInt(getString(R.string.saved_yahoo_from_spinner_pos), 0);
        editor.putInt(getString(R.string.saved_yahoo_to_spinner_pos), 0);

        editor.putInt(getString(R.string.saved_custom_from_spinner_pos), 0);
        editor.putInt(getString(R.string.saved_custom_to_spinner_pos), 0);

        editor.putInt(getString(R.string.saved_custom_fragment_spinner_pos), 0);

        editor.apply();
    }
}
