package com.github.polurival.cc;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.github.polurival.cc.adapter.ListViewCursorAdapter;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Constants;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

public class CurrencySwitchingActivity extends Activity implements SearcherFragment.Listener {

    private String rateUpdaterClassName;

    private Cursor listCursor;
    private ListViewCursorAdapter cursorAdapter;

    private ListView lvAllCurrencies;
    private CheckBox cbTurnOnOffAllCurrencies;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.logD(Logger.getTag(), "onCreate");

        setContentView(R.layout.activity_currency_switching);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        lvAllCurrencies = (ListView) findViewById(R.id.lv_turn_on_off);
        cbTurnOnOffAllCurrencies = (CheckBox) findViewById(R.id.cb_turn_all_on_off);

        if (isAllCurrenciesTurnOn()) {
            cbTurnOnOffAllCurrencies.setChecked(true);
        }

        Intent intent = getIntent();
        rateUpdaterClassName = intent.getStringExtra(Constants.RATE_UPDATER_CLASS_NAME);
    }

    @Override
    public void onResume() {
        super.onResume();
        Logger.logD(Logger.getTag(), "onResume");

        readListDataFromDB();
        setNewSearcherFragment();
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
                                    DBHelper.COLUMN_NAME_CURRENCY_NAME,
                                    DBHelper.COLUMN_NAME_FLAG_ID,
                                    DBHelper.COLUMN_NAME_SWITCHING},
                            getWhereClause(), null, null, null, null);

                    initLvAllCurrencies();

                } catch (SQLiteException e) {
                    Toaster.showBottomToast(getString(R.string.db_reading_error));
                }
            }
        });
    }

    private void initLvAllCurrencies() {
        Logger.logD(Logger.getTag(), "initLvAllCurrencies");

        if (cursorAdapter == null) {

            cursorAdapter = new ListViewCursorAdapter(getApplicationContext(), listCursor);
            lvAllCurrencies.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    View rowLayout = getViewByPosition(position, lvAllCurrencies);

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

            lvAllCurrencies.setAdapter(cursorAdapter);
        } else {
            cursorAdapter.changeCursor(listCursor);
            cursorAdapter.notifyDataSetChanged();
        }


        if (isAllCurrenciesTurnOn()) {
            cbTurnOnOffAllCurrencies.setChecked(true);
        } else {
            cbTurnOnOffAllCurrencies.setChecked(false);
        }
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

    private void setNewSearcherFragment() {
        Logger.logD(Logger.getTag(), "setNewSearcherFragment");

        SearcherFragment searcherFragment = new SearcherFragment();
        searcherFragment.setSwitchingListView(lvAllCurrencies);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.switching_searcher_fragment_container, searcherFragment);
        transaction.commit();
    }

    public void turnOnOffAllCurrencies(View view) {
        Logger.logD(Logger.getTag(), "turnOnOffAllCurrencies");

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
                db.beginTransaction();
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
                                getWhereClause(),
                                null);
                    }

                    db.setTransactionSuccessful();

                    AppPreferences.resetSpinnersPositionsToZero(CurrencySwitchingActivity.this);

                    readListDataFromDB();

                } catch (SQLiteException e) {
                    Toaster.showBottomToast(getString(R.string.db_writing_error));
                } finally {
                    db.endTransaction();
                }
            }
        });
    }

    private String getWhereClause() {
        String where;
        if (rateUpdaterClassName.equals(getString(R.string.cb_rf_rate_updater_class))) {
            where = DBHelper.COLUMN_NAME_CB_RF_SOURCE + " = 1";
        } else if(rateUpdaterClassName.equals(getString(R.string.yahoo_rate_updater_class))) {
            where = DBHelper.COLUMN_NAME_YAHOO_SOURCE + " = 1";
        } else{
            where = null;
        }
        return where;
    }

    @Override
    public Cursor getCursor() {
        return listCursor;
    }
}
