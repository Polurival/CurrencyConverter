package com.github.polurival.cc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.polurival.cc.adapter.AutoCompleteTVAdapter;
import com.github.polurival.cc.adapter.SpinnerCursorAdapter;
import com.github.polurival.cc.model.updater.CBRateUpdaterTask;
import com.github.polurival.cc.model.updater.CustomRateUpdaterMock;
import com.github.polurival.cc.model.TaskCanceler;
import com.github.polurival.cc.model.updater.YahooRateUpdaterTask;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.updater.RateUpdater;
import com.github.polurival.cc.util.Constants;
import com.github.polurival.cc.util.DateUtil;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.Toaster;

import org.joda.time.LocalDateTime;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

/**
 * Created by Polurival
 * on 24.03.2016.
 */
public class MainActivity extends Activity implements RateUpdaterListener, OnRefreshListener {

    private SQLiteDatabase db;
    private Cursor cursor;
    private Cursor fromCursor;
    private Cursor toCursor;
    private Cursor searchCursor;
    private AutoCompleteTVAdapter autoCompleteTvAdapter;

    private SharedPreferences preferences;

    private ShareActionProvider shareActionProvider;

    private String menuState;

    private Handler taskCancelerHandler;
    private TaskCanceler taskCanceler;

    private RateUpdater rateUpdater;
    private LocalDateTime upDateTime;

    private PullToRefreshLayout mPullToRefreshLayout;

    private EditText editFromAmount;
    private EditText editToAmount;

    private boolean isPropertiesLoaded;
    private boolean isNeedToReSwapValues;
    private boolean ignoreEditFromAmountChange;
    private boolean ignoreEditToAmountChange;
    private boolean isEditTextFormatted;

    private Spinner fromSpinner;
    private int fromSpinnerSelectedPos;
    private String currencyFromCharCode;
    private double currencyFromNominal;
    private double currencyFromToXRate;

    private Spinner toSpinner;
    private int toSpinnerSelectedPos;
    private String currencyToCharCode;
    private double currencyToNominal;
    private double currencyToToXRate;

    private TextView tvLabelForCurrentCurrencies;
    private TextView tvDateTime;

    @Override
    public void setMenuState(String menuState) {
        this.menuState = menuState;
        invalidateOptionsMenu();
    }

    @Override
    public void setCursor(Cursor cursor) {
        this.cursor = cursor;
    }

    @Override
    public void setUpDateTime(LocalDateTime upDateTime) {
        this.upDateTime = upDateTime;
    }

    @Override
    public void setPropertiesLoaded(boolean isLoaded) {
        this.isPropertiesLoaded = isLoaded;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.logD(Logger.getTag(), "onCreate");

        setContentView(R.layout.activity_main);
        tvLabelForCurrentCurrencies = (TextView) findViewById(R.id.tv_label_for_current_currencies);

        db = DBHelper.getInstance(getApplicationContext()).getReadableDatabase();
        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        ignoreEditFromAmountChange = false;
        ignoreEditToAmountChange = false;
        isNeedToReSwapValues = false;
        isPropertiesLoaded = false;

        isEditTextFormatted = false;

        mPullToRefreshLayout = (PullToRefreshLayout) findViewById(R.id.ptr_layout);
        ActionBarPullToRefresh.from(this)
                .allChildrenArePullable()
                .listener(this)
                .setup(mPullToRefreshLayout);

        initEditAmount();
        loadEditAmountProperties();

        checkScreenSizeAndSetSoftInputMode();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Logger.logD(Logger.getTag(), "onStart");

        loadRateUpdaterProperties();
        loadUpDateTimeProperty();

        initSearchAdapter();
        initSearchFilter();

        if (loadIsSetAutoUpdateProperty()) {
            if (DateUtil.compareUpDateWithCurrentDate(upDateTime)) {
                readDataFromDB();
                if (!(rateUpdater instanceof CustomRateUpdaterMock)) {
                    mPullToRefreshLayout.setRefreshing(true);
                }
                updateRatesFromSource();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.logD(Logger.getTag(), "onResume");

        readDataFromDB();
        checkAsyncTaskStatusAndSetNewInstance();
    }

    @Override
    protected void onPause() {
        Logger.logD(Logger.getTag(), "onPause");

        cancelAsyncTask();

        super.onPause();
    }

    @Override
    protected void onStop() {
        Logger.logD(Logger.getTag(), "onStop");

        saveProperties();

        if (null != taskCanceler && null != taskCancelerHandler) {
            taskCancelerHandler.removeCallbacks(taskCanceler);
        }

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Logger.logD(Logger.getTag(), "onDestroy");

        if (null != fromCursor) fromCursor.close();
        if (null != toCursor) toCursor.close();
        if (null != cursor) cursor.close();
        if (null != searchCursor) searchCursor.close();

        if (null != db) db.close();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Logger.logD(Logger.getTag(), "onBackPressed");

        cancelAsyncTask();

        super.onBackPressed();
    }

    @Override
    protected void onUserLeaveHint() {
        Logger.logD(Logger.getTag(), "onUserLeaveHint");

        cancelAsyncTask();
        setMenuState(null);

        super.onUserLeaveHint();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Logger.logD(Logger.getTag(), "onCreateOptionsMenu");

        getMenuInflater().inflate(R.menu.menu_main, menu);

        if (Constants.MENU_HIDE.equals(menuState) &&
                !(rateUpdater instanceof CustomRateUpdaterMock)) {
            for (int i = 0; i < menu.size(); i++) {
                menu.getItem(i).setVisible(false);
            }
        }

        MenuItem shareAction = menu.findItem(R.id.share_action);
        shareActionProvider = (ShareActionProvider) shareAction.getActionProvider();

        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Show menu icons
     * See <a href="http://stackoverflow.com/a/22668665/5349748">source</a>
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (featureId == Window.FEATURE_ACTION_BAR && menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Logger.logD("onMenuOpened error");
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Logger.logD(Logger.getTag(), "onOptionsItemSelected " + item.getTitle().toString());

        switch (item.getItemId()) {

            case R.id.data_source_action:
                cancelAsyncTask();

                Intent dataSourceIntent = new Intent(this, DataSourceActivity.class);
                startActivity(dataSourceIntent);
                return true;

            case R.id.currency_switching_action:
                cancelAsyncTask();

                Intent currencySwitchingIntent = new Intent(this, CurrencySwitchingActivity.class);
                currencySwitchingIntent.putExtra(
                        Constants.RATE_UPDATER_CLASS_NAME, rateUpdater.getClass().getName());
                startActivity(currencySwitchingIntent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void checkScreenSizeAndSetSoftInputMode() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    @Override
    public void checkAsyncTaskStatusAndSetNewInstance() {
        Logger.logD(Logger.getTag(), "checkAsyncTaskStatusAndSetNewInstance()");

        if (rateUpdater instanceof AsyncTask) {
            if (((AsyncTask) rateUpdater).getStatus() != AsyncTask.Status.PENDING) {
                loadRateUpdaterProperties();
            }
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        Logger.logD(Logger.getTag(), "onRefreshStarted");

        if (rateUpdater instanceof CustomRateUpdaterMock) {
            Toaster.showCenterToast(getString(R.string.custom_updating_info));
            stopRefresh();
        } else {
            checkAsyncTaskStatusAndSetNewInstance();
            updateRatesFromSource();
        }
    }

    @Override
    public void stopRefresh() {
        Logger.logD(Logger.getTag(), "stopRefresh");

        if (mPullToRefreshLayout.isRefreshing()) {
            mPullToRefreshLayout.setRefreshComplete();
        }
    }

    /**
     * See <a href="http://stackoverflow.com/a/24788257/5349748">Source</a>
     */
    private void updateRatesFromSource() {
        Logger.logD(Logger.getTag(), "updateRatesFromSource");

        taskCancelerHandler.postDelayed(taskCanceler, 15 * 1000);

        if (rateUpdater instanceof CBRateUpdaterTask) {
            ((CBRateUpdaterTask) rateUpdater).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            ((YahooRateUpdaterTask) rateUpdater).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }

        hideMenuWhileUpdating();
    }

    private void hideMenuWhileUpdating() {
        Logger.logD(Logger.getTag(), "hideMenuWhileUpdating");

        menuState = Constants.MENU_HIDE;
        invalidateOptionsMenu();
    }

    @Override
    public void readDataFromDB() {
        Logger.logD(Logger.getTag(), "readDataFromDB");

        DBReaderTask dbReaderTask = new DBReaderTask();
        dbReaderTask.setRateUpdaterListener(this);

        if (rateUpdater instanceof CBRateUpdaterTask) {
            dbReaderTask.execute(DBHelper.COLUMN_NAME_CB_RF_SOURCE,
                    DBHelper.COLUMN_NAME_CB_RF_NOMINAL,
                    DBHelper.COLUMN_NAME_CB_RF_RATE);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            dbReaderTask.execute(DBHelper.COLUMN_NAME_YAHOO_SOURCE,
                    DBHelper.COLUMN_NAME_YAHOO_NOMINAL,
                    DBHelper.COLUMN_NAME_YAHOO_RATE);
        } else if (rateUpdater instanceof CustomRateUpdaterMock) {
            dbReaderTask.execute(DBHelper.CUSTOM_SOURCE_MOCK,
                    DBHelper.COLUMN_NAME_CUSTOM_NOMINAL,
                    DBHelper.COLUMN_NAME_CUSTOM_RATE);
        }
    }

    private void initSearchAdapter() {
        searchCursor = DBHelper.getSearchCursor("", rateUpdater.getClass().getName());
        autoCompleteTvAdapter = new AutoCompleteTVAdapter(
                getApplicationContext(), searchCursor, rateUpdater.getClass().getName());
    }

    private void initSearchFilter() {
        Logger.logD(Logger.getTag(), "initSearchFilter");

        final AutoCompleteTextView currencySearcher =
                (AutoCompleteTextView) findViewById(R.id.tv_auto_complete);
        currencySearcher.setAdapter(autoCompleteTvAdapter);
        currencySearcher.setThreshold(1);
        currencySearcher.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.logD(Logger.getTag(), "currencySearcher.onItemClick");

                currencySearcher.setText("");

                Cursor searchedCurrency = (Cursor) parent.getItemAtPosition(position);
                String searchedCharCode = searchedCurrency.getString(1);

                int searchedCharCodeSpinnerPos = 0;
                for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                    String cursorCurrentCharCode = cursor.getString(1);
                    if (searchedCharCode.equals(cursorCurrentCharCode)) {
                        searchedCharCodeSpinnerPos = cursor.getPosition();
                    }
                }

                SpinnerSelectionDialog fragmentDialog = new SpinnerSelectionDialog();
                fragmentDialog.setFromSpinner(fromSpinner);
                fragmentDialog.setToSpinner(toSpinner);
                fragmentDialog.setSearchedCharCodeSpinnerPos(searchedCharCodeSpinnerPos);
                fragmentDialog.show(getFragmentManager(), "list selection");
            }
        });
    }

    @Override
    public void initSpinners() {
        Logger.logD(Logger.getTag(), "initSpinners");

        SpinnerCursorAdapter cursorAdapter =
                new SpinnerCursorAdapter(getApplicationContext(), cursor);

        fromSpinner = (Spinner) findViewById(R.id.from_spinner);
        fromSpinner.setAdapter(cursorAdapter);
        fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fromCursor = (Cursor) parent.getItemAtPosition(position);

                currencyFromCharCode = fromCursor.getString(1);
                currencyFromNominal = (double) fromCursor.getInt(2);
                currencyFromToXRate = fromCursor.getDouble(3);

                fromSpinnerSelectedPos = position;

                editFromAmount.setText(editFromAmount.getText());

                saveSpinnersProperties();

                tvLabelForCurrentCurrencies.setText(composeTextForLabel());
                syncShareActionData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        toSpinner = (Spinner) findViewById(R.id.to_spinner);
        toSpinner.setAdapter(cursorAdapter);
        toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                toCursor = (Cursor) parent.getItemAtPosition(position);

                currencyToCharCode = toCursor.getString(1);
                currencyToNominal = (double) toCursor.getInt(2);
                currencyToToXRate = toCursor.getDouble(3);

                toSpinnerSelectedPos = position;

                editFromAmount.setText(editFromAmount.getText());

                saveSpinnersProperties();

                tvLabelForCurrentCurrencies.setText(composeTextForLabel());
                syncShareActionData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        if (fromSpinner.getCount() == 0) {
            Toaster.showCenterToast(getString(R.string.all_currencies_disabled));
        }
    }

    private void initEditAmount() {
        Logger.logD(Logger.getTag(), "initEditAmount");

        editFromAmount = (EditText) findViewById(R.id.edit_from_amount);
        editFromAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && isPropertiesLoaded) {
                    if (!ignoreEditFromAmountChange) {
                        ignoreEditToAmountChange = true;
                        convertAndSetResult(editFromAmount);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ignoreEditToAmountChange = false;

                String s = editable.toString();
                if ("".equals(s)) {
                    editToAmount.getText().clear();
                }

                syncShareActionData();

                if (null == editFromAmount) return;
                if (s.isEmpty()) return;

                String[] sParts = getPartsOfEditAmountText(s);

                editFromAmount.removeTextChangedListener(this);

                String formatted = formatAndSetEditAmountText(editFromAmount, s, sParts);
                editFromAmount.setSelection(formatted.length());

                editFromAmount.addTextChangedListener(this);
            }
        });

        editToAmount = (EditText) findViewById(R.id.edit_to_amount);
        editToAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() != 0 && isPropertiesLoaded) {
                    if (!ignoreEditToAmountChange) {
                        ignoreEditFromAmountChange = true;
                        convertAndSetResult(editToAmount);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ignoreEditFromAmountChange = false;

                String s = editable.toString();
                if ("".equals(s)) {
                    editFromAmount.getText().clear();
                }

                syncShareActionData();

                if (null == editToAmount) return;
                if (s.isEmpty()) return;

                String[] sParts = getPartsOfEditAmountText(s);

                editToAmount.removeTextChangedListener(this);

                String formatted = formatAndSetEditAmountText(editToAmount, s, sParts);
                editToAmount.setSelection(formatted.length());

                editToAmount.addTextChangedListener(this);
            }
        });
    }

    private String[] getPartsOfEditAmountText(String s) {
        String[] sParts = null;
        if (s.contains(".")) {
            sParts = new String[2];
            sParts[0] = s.substring(0, s.indexOf('.'));
            sParts[1] = s.substring(s.indexOf('.'));
        }
        return sParts;
    }

    private String formatAndSetEditAmountText(EditText editText, String s, String[] sParts) {
        String formatted;
        if (null == sParts) {
            formatted = formatBigDecimal(prepareBigDecimal(s), 2);
        } else {
            formatted = formatBigDecimal(prepareBigDecimal(sParts[0]), 2) + sParts[1];
        }
        editText.setText(formatted);
        return formatted;
    }

    @NonNull
    private BigDecimal prepareBigDecimal(CharSequence s) {
        String plainEditAmountText = s.toString().replaceAll(" ", "");
        isEditTextFormatted = true;
        return new BigDecimal(plainEditAmountText);
    }

    @Override
    public void initTvDateTime() {
        Logger.logD(Logger.getTag(), "initTvDateTime");

        tvDateTime = (TextView) findViewById(R.id.tv_date_time);
        tvDateTime.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                fromSpinner.setSelection(fromSpinner.getSelectedItemPosition());
                toSpinner.setSelection(toSpinner.getSelectedItemPosition());
            }
        });
        tvDateTimeSetText();
    }

    private void tvDateTimeSetText() {
        Logger.logD(Logger.getTag(), "tvDateTimeSetText");

        tvDateTime.setText(String.format("%s%s",
                rateUpdater.getDescription(), DateUtil.getUpDateTimeStr(upDateTime)));
    }

    private void convertAndSetResult(View v) {
        Logger.logD(Logger.getTag(), "convertAndSetResult " + v.toString());

        if (cancelConvertingIfNothingToConvert()) {
            Toaster.showCenterToast(getString(R.string.all_currencies_disabled));
            return;
        }

        checkNeedToSwapValues(v);

        BigDecimal amount = getEnteredAmountOfMoney(v);
        BigDecimal result = calculateResult(amount);

        String resultStr = formatBigDecimal(result, 2);

        if (v.getId() == R.id.edit_from_amount) {
            if ("".equals(editFromAmount.getText().toString())) {
                editToAmount.setText("");
            } else {
                editToAmount.setText(resultStr);
            }
        } else if (v.getId() == R.id.edit_to_amount) {
            if ("".equals(editToAmount.getText().toString())) {
                editFromAmount.setText("");
            } else {
                editFromAmount.setText(resultStr);
            }
        }
    }

    private String convertForLabel(View v) {
        Logger.logD(Logger.getTag(), "convertForLabel");

        if (cancelConvertingIfNothingToConvert()) return null;

        checkNeedToSwapValues(v);

        BigDecimal result = calculateResult(BigDecimal.ONE);

        int scale;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            scale = 4;
        } else {
            scale = 6;
        }

        return formatBigDecimal(result, scale);
    }

    private boolean cancelConvertingIfNothingToConvert() {
        return null == fromSpinner.getSelectedItem() || null == toSpinner.getSelectedItem();
    }

    /**
     * See <a href='http://stackoverflow.com/a/5323787/5349748'>source</a>
     */
    private String formatBigDecimal(BigDecimal result, int scale) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);

        return formatter.format(result.setScale(scale, RoundingMode.HALF_EVEN).doubleValue());
    }

    private void checkNeedToSwapValues(View v) {
        if (isNeedToReSwapValues && (v.getId() != R.id.edit_to_amount)) {
            reSwapEditAmountsValues();
            isNeedToReSwapValues = false;
        }

        if (!isNeedToReSwapValues && (v.getId() == R.id.edit_to_amount)) {
            reSwapEditAmountsValues();
            isNeedToReSwapValues = true;
        }
    }

    private void reSwapEditAmountsValues() {
        Logger.logD(Logger.getTag(), "reSwapEditAmountsValues");

        double tempValFrom = currencyFromToXRate;
        currencyFromToXRate = currencyToToXRate;
        currencyToToXRate = tempValFrom;

        double tempNomFrom = currencyFromNominal;
        currencyFromNominal = currencyToNominal;
        currencyToNominal = tempNomFrom;
    }

    public void swapFromTo(View v) {
        Logger.logD(Logger.getTag(), "swapFromTo");

        if (fromSpinner != null && toSpinner != null) {
            int fromSpinnerSelectedItemPos = fromSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
            toSpinner.setSelection(fromSpinnerSelectedItemPos);

            if (isNeedToReSwapValues) {
                reSwapEditAmountsValues();
                isNeedToReSwapValues = false;
            }
        }
    }

    private BigDecimal getEnteredAmountOfMoney(View v) {
        Logger.logD(Logger.getTag(), "getEnteredAmountOfMoney " + v.toString());

        if (v.getId() == R.id.edit_from_amount) {
            String editFromText = editFromAmount.getText().toString().replaceAll(" ", "");
            if (TextUtils.isEmpty(editFromText)) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(editFromText);
        } else {
            String editToText = editToAmount.getText().toString().replaceAll(" ", "");
            if (TextUtils.isEmpty(editToText)) {
                return BigDecimal.ZERO;
            }
            return new BigDecimal(editToText);
        }
    }

    private BigDecimal calculateResult(BigDecimal enteredAmountOfMoney) {
        if (enteredAmountOfMoney.equals(BigDecimal.ZERO)
                || currencyFromToXRate == 0 || currencyToToXRate == 0
                || currencyToNominal == 0 || currencyFromNominal == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal fromXRate = BigDecimal.valueOf(currencyFromToXRate);
        BigDecimal toXRate = BigDecimal.valueOf(currencyToToXRate);
        BigDecimal toNominal = BigDecimal.valueOf(currencyToNominal);
        BigDecimal fromNominal = BigDecimal.valueOf(currencyFromNominal);
        BigDecimal result;

        if (rateUpdater instanceof CBRateUpdaterTask) {
            result = fromXRate
                    .divide(toXRate, 10, RoundingMode.HALF_EVEN)
                    .multiply(toNominal)
                    .divide(fromNominal, 10, RoundingMode.HALF_EVEN)
                    .multiply(enteredAmountOfMoney);
        } else {
            result = toXRate
                    .divide(fromXRate, 10, RoundingMode.HALF_EVEN)
                    .multiply(fromNominal)
                    .divide(toNominal, 10, RoundingMode.HALF_EVEN)
                    .multiply(enteredAmountOfMoney);

        }
        return result;
    }

    private void saveProperties() {
        Logger.logD(Logger.getTag(), "saveProperties");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(getString(R.string.saved_from_edit_amount_text),
                editFromAmount.getText().toString());
        editor.putString(getString(R.string.saved_to_edit_amount_text),
                editToAmount.getText().toString());

        editor.putString(getString(R.string.saved_rate_updater_class),
                rateUpdater.getClass().getName());

        editor.apply();

        saveSpinnersProperties();
    }

    private void saveSpinnersProperties() {
        SharedPreferences.Editor editor = preferences.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putInt(getString(R.string.saved_cb_rf_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(getString(R.string.saved_cb_rf_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putInt(getString(R.string.saved_yahoo_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(getString(R.string.saved_yahoo_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else {
            editor.putInt(getString(R.string.saved_custom_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(getString(R.string.saved_custom_to_spinner_pos),
                    toSpinnerSelectedPos);
        }

        editor.apply();
    }

    @Override
    public void saveUpDateTimeProperty() {
        Logger.logD(Logger.getTag(), "saveUpDateTimeProperty");

        SharedPreferences.Editor editor = preferences.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putLong(getString(R.string.saved_cb_rf_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putLong(getString(R.string.saved_yahoo_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        }

        editor.apply();
    }

    private boolean loadIsSetAutoUpdateProperty() {
        Logger.logD(Logger.getTag(), "loadIsSetAutoUpdateProperty");

        return preferences.getBoolean(getString(R.string.saved_is_set_auto_update),
                Boolean.valueOf(getString(R.string.saved_is_set_auto_update_default)));
    }

    private void loadEditAmountProperties() {
        Logger.logD(Logger.getTag(), "loadEditAmountProperties");

        String editFromAmountText =
                preferences.getString(getString(R.string.saved_from_edit_amount_text),
                        getString(R.string.saved_edit_amount_text_default));
        editFromAmount.setText(editFromAmountText);
        String editToAmountText =
                preferences.getString(getString(R.string.saved_to_edit_amount_text),
                        getString(R.string.saved_edit_amount_text_default));
        editToAmount.setText(editToAmountText);

    }

    private void loadUpDateTimeProperty() {
        Logger.logD(Logger.getTag(), "loadUpDateTimeProperty");

        String savedUpDateTime;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            savedUpDateTime = getString(R.string.saved_cb_rf_up_date_time);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            savedUpDateTime = getString(R.string.saved_yahoo_up_date_time);
        } else {
            savedUpDateTime = getString(R.string.saved_custom_up_date_time);
        }
        long upDateTimeInSeconds =
                preferences.getLong(savedUpDateTime, DateUtil.getDefaultDateTimeInSeconds());
        upDateTime = DateUtil.getUpDateTime(upDateTimeInSeconds);
    }

    private void loadRateUpdaterProperties() {
        Logger.logD(Logger.getTag(), "loadRateUpdaterProperties");

        String rateUpdaterName =
                preferences.getString(getString(R.string.saved_rate_updater_class),
                        getString(R.string.saved_rate_updater_class_default));
        Logger.logD("rateUpdater className = " + rateUpdaterName);
        try {
            rateUpdater
                    = (RateUpdater) Class.forName(rateUpdaterName).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        rateUpdater.setRateUpdaterListener(this);

        taskCancelerHandler = new Handler();
        taskCanceler = new TaskCanceler((AsyncTask) rateUpdater, this);
    }

    @Override
    public void loadSpinnersProperties() {
        Logger.logD(Logger.getTag(), "loadSpinnersProperties");

        if (rateUpdater instanceof CBRateUpdaterTask) {
            fromSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_cb_rf_from_spinner_pos), 30);
            toSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_cb_rf_to_spinner_pos), 23);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            fromSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_yahoo_from_spinner_pos), 144);
            toSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_yahoo_to_spinner_pos), 117);
        } else {
            fromSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_custom_from_spinner_pos), 144);
            toSpinnerSelectedPos =
                    preferences.getInt(getString(R.string.saved_custom_to_spinner_pos), 117);
        }

        fromSpinner.setSelection(fromSpinnerSelectedPos);
        toSpinner.setSelection(toSpinnerSelectedPos);
    }

    private void syncShareActionData() {
        Logger.logD(Logger.getTag(), "syncShareActionData");

        if (isPropertiesLoaded) {
            setShareIntent(composeTextForShare());
        }
    }

    private void setShareIntent(String text) {
        Logger.logD(Logger.getTag(), "setShareIntent " + text);
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        shareActionProvider.setShareIntent(intent);
    }

    private String composeTextForShare() {
        Logger.logD(Logger.getTag(), "composeTextForShare");

        String fromCurrencyValue = editFromAmount.getText().toString();
        String toCurrencyValue;

        if (fromCurrencyValue.length() == 0) {
            fromCurrencyValue = "1";
            toCurrencyValue = convertForLabel(editFromAmount);
        } else {
            toCurrencyValue = editToAmount.getText().toString();
        }

        return getFormattedText(fromCurrencyValue, toCurrencyValue);
    }

    private String composeTextForLabel() {
        Logger.logD(Logger.getTag(), "composeTextForLabel");

        String fromCurrencyValue = "1";
        String toCurrencyValue = convertForLabel(editFromAmount);

        return getFormattedText(fromCurrencyValue, toCurrencyValue);
    }

    private String getFormattedText(String fromCurrencyValue, String toCurrencyValue) {
        if (null == currencyFromCharCode || null == currencyToCharCode) {
            return "";
        }
        return String.format("%s %s = %s %s",
                fromCurrencyValue, currencyFromCharCode,
                toCurrencyValue, currencyToCharCode);
    }

    private void cancelAsyncTask() {
        Logger.logD(Logger.getTag(), "cancelAsyncTask");

        stopRefresh();

        AsyncTask task = (AsyncTask) rateUpdater;
        if (task.getStatus() != AsyncTask.Status.PENDING) {
            task.cancel(true);
        }
    }
}
