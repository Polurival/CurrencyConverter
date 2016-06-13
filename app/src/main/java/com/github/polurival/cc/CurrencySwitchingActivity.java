package com.github.polurival.cc;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.polurival.cc.model.db.DBHelper;

public class CurrencySwitchingActivity extends Activity {

    private Cursor listCursor;
    private ListView lvAllCurrencies;

    private CheckBox cbTurnOnOffAllCurrencies;

    private int lvSelectedPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_switching);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        lvAllCurrencies = (ListView) findViewById(R.id.lv_turn_on_off);
        cbTurnOnOffAllCurrencies = (CheckBox) findViewById(R.id.cb_turn_all_on_off);

        if (isAllCurrenciesTurnOn()) {
            cbTurnOnOffAllCurrencies.setChecked(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        readListDataFromDB();
    }

    private void readListDataFromDB() {
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
                    Toast.makeText(getApplicationContext(), R.string.db_reading_error,
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private void initLvAllCurrencies() {
        ListViewCursorAdapter cursorAdapter =
                new ListViewCursorAdapter(getApplicationContext(), listCursor);
        lvAllCurrencies.setAdapter(cursorAdapter);

        lvAllCurrencies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                View rowLayout = getViewByPosition(position, lvAllCurrencies);
                lvSelectedPos = position;

                TextView tvCharCode = (TextView) ((ViewGroup) rowLayout).getChildAt(2);
                String currencyCharCode = tvCharCode.getText().toString();
                CheckBox currencyCondition = (CheckBox) ((ViewGroup) rowLayout).getChildAt(3);

                if (currencyCondition.isChecked()) {
                    saveCurrencyOnOffCondition(currencyCharCode, 0);
                } else {
                    saveCurrencyOnOffCondition(currencyCharCode, 1);
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
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    public void turnOnOffAllCurrencies(View view) {
        if (cbTurnOnOffAllCurrencies.isChecked()) {
            turnOnOffAllListItems(1);
        } else {
            turnOnOffAllListItems(0);
        }
    }

    private boolean isAllCurrenciesTurnOn() {
        for (int i = 0; i < lvAllCurrencies.getCount(); i++) {
            int currencyCondition = ((Cursor) lvAllCurrencies.getItemAtPosition(i)).getInt(4);
            if (currencyCondition == 0) {
                return false;
            }
        }
        return true;
    }

    private void turnOnOffAllListItems(int selector) {
        for (int i = 0; i < lvAllCurrencies.getCount(); i++) {
            String currencyCharCode = ((Cursor) lvAllCurrencies.getItemAtPosition(i)).getString(1);
            int currencyCondition = ((Cursor) lvAllCurrencies.getItemAtPosition(i)).getInt(4);

            if ((selector == 1 && currencyCondition == 0) ||
                    selector == 0 && currencyCondition == 1) {

                if (i == lvAllCurrencies.getCount() - 1) {
                    saveCurrencyOnOffCondition(currencyCharCode, selector);
                } else {
                    saveCurrencyOnOffCondition(currencyCharCode, selector);
                }
            }
        }
    }

    private void saveCurrencyOnOffCondition(
            final String currencyCharCode, int selector) {
        final ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_NAME_SWITCHING, selector);

        final SQLiteDatabase db =
                DBHelper.getInstance(getApplicationContext()).getWritableDatabase();
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    db.update(DBHelper.TABLE_NAME,
                            contentValues,
                            DBHelper.COLUMN_NAME_CHAR_CODE + " = ?",
                            new String[]{currencyCharCode});

                    saveDefaultPositionProperties();

                    readListDataFromDB();

                } catch (SQLiteException e) {
                    Toast.makeText(getApplicationContext(),
                            getApplicationContext().getString(R.string.db_writing_error),
                            Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
    }

    private class ListViewCursorAdapter extends CursorAdapter {

        public ListViewCursorAdapter(Context context, Cursor cursor) {
            super(context, cursor, 0);
        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            return LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false);
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {

            ImageView flagIcon = (ImageView) view.findViewById(R.id.lv_flag_icon);
            int flagIconId = cursor.getInt(3);
            flagIcon.setImageResource(flagIconId);

            TextView currencyName = (TextView) view.findViewById(R.id.lv_currency_name);
            int currencyNameId = cursor.getInt(2);
            currencyName.setText(getString(currencyNameId));

            TextView currencyCharCode =
                    (TextView) view.findViewById(R.id.lv_currency_char_code);
            currencyCharCode.setText(cursor.getString(1));

            CheckBox turnOnOff = (CheckBox) view.findViewById(R.id.cb_currency_turn_on_off);
            if (cursor.getInt(4) == 1) {
                turnOnOff.setChecked(true);
            } else {
                turnOnOff.setChecked(false);
            }
        }
    }

    @Override
    public void onStop() {
        listCursor.close();
        //db.close();

        super.onStop();
    }

    private void saveDefaultPositionProperties() {
        SharedPreferences preferences =
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = preferences.edit();

            editor.putInt(getString(R.string.saved_cb_rf_from_spinner_pos), 0);
            editor.putInt(getString(R.string.saved_cb_rf_to_spinner_pos), 0);

            editor.putInt(getString(R.string.saved_yahoo_from_spinner_pos), 0);
        editor.putInt(getString(R.string.saved_yahoo_to_spinner_pos), 0);

        editor.putInt(getString(R.string.saved_custom_from_spinner_pos), 0);
        editor.putInt(getString(R.string.saved_custom_to_spinner_pos), 0);

        editor.apply();
    }
}
