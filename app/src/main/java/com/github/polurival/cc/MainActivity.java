package com.github.polurival.cc;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ShareActionProvider;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.polurival.cc.adapter.SpinnerCursorAdapter;
import com.github.polurival.cc.model.TaskCanceler;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.model.db.DBReaderTask;
import com.github.polurival.cc.model.dto.SpinnersPositions;
import com.github.polurival.cc.model.updater.CBRateUpdaterTask;
import com.github.polurival.cc.model.updater.CustomRateUpdaterMock;
import com.github.polurival.cc.model.updater.RateUpdater;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.CurrencyUtil;
import com.github.polurival.cc.util.DateUtil;
import com.github.polurival.cc.util.InternetChecker;
import com.github.polurival.cc.util.Logger;
import com.github.polurival.cc.util.SimpleTextWatcher;
import com.github.polurival.cc.util.Toaster;

import org.joda.time.LocalDateTime;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;
import uk.co.senab.actionbarpulltorefresh.library.ActionBarPullToRefresh;
import uk.co.senab.actionbarpulltorefresh.library.PullToRefreshLayout;
import uk.co.senab.actionbarpulltorefresh.library.listeners.OnRefreshListener;

public class MainActivity extends Activity implements RateUpdaterListener, OnRefreshListener,
        SearcherFragment.Listener {

    private static final int DEFAULT_SCALE = 10;

    /**
     * Hide menu while updating from source
     */
    private static final String MENU_HIDE = "menuHide";

    private SQLiteDatabase db;
    private Cursor cursor;
    private SpinnerCursorAdapter cursorAdapter;
    private Cursor fromCursor;
    private Cursor toCursor;

    private ShareActionProvider shareActionProvider;

    private String menuState;

    private Handler taskCancelerHandler;
    private TaskCanceler taskCanceler;

    private String rateUpdaterClassName;
    private RateUpdater rateUpdater;
    private LocalDateTime upDateTime;

    private PullToRefreshLayout mPullToRefreshLayout;

    private EditText fromAmountEditText;
    private EditText toAmountEditText;

    private boolean isPropertiesLoaded;
    private boolean isNeedToReSwapValues;
    private boolean ignoreEditFromAmountChange;
    private boolean ignoreEditToAmountChange;

    private Spinner fromSpinner;
    private int fromSpinnerSelectedPos;
    private String currencyFromCharCode;
    private BigDecimal currencyFromNominal;
    private BigDecimal currencyFromToXRate;

    private Spinner toSpinner;
    private int toSpinnerSelectedPos;
    private String currencyToCharCode;
    private BigDecimal currencyToNominal;
    private BigDecimal currencyToToXRate;

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

        db = DBHelper.getInstance(getApplicationContext()).getReadableDatabase();

        fromSpinner = (Spinner) findViewById(R.id.from_spinner);
        toSpinner = (Spinner) findViewById(R.id.to_spinner);
        tvLabelForCurrentCurrencies = (TextView) findViewById(R.id.tv_label_for_current_currencies);

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

        // TODO: 29.05.2017 попробовать перенести все это в onCreate

        loadRateUpdaterClassName();
        setRateUpdaterAndTaskCanceler();

        setNewSearcherFragment();

        loadUpDateTime();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Logger.logD(Logger.getTag(), "onResume");

        AppContext.activityResumed();

        readDataFromDB();
        if (AppPreferences.loadIsSetAutoUpdate(this)) {
            if (DateUtil.isUpDateTimeLessThenCurrentDateTime(upDateTime)) {
                updateRatesFromSource();
            }
        } else {
            checkAsyncTaskStatusAndSetNewInstance();
        }
    }

    @Override
    protected void onPause() {
        Logger.logD(Logger.getTag(), "onPause");

        AppContext.activityPaused();

        cancelAsyncTask();

        super.onPause();
    }

    @Override
    protected void onStop() {
        Logger.logD(Logger.getTag(), "onStop");

        AppPreferences.saveMainActivityProperties(this,
                fromAmountEditText.getText().toString(),
                toAmountEditText.getText().toString(),
                rateUpdater.getClass().getName());
        rateUpdater.saveSelectedCurrencySpinnersPositions(this, fromSpinnerSelectedPos, toSpinnerSelectedPos);

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Logger.logD(Logger.getTag(), "onDestroy");

        if (null != fromCursor) fromCursor.close();
        if (null != toCursor) toCursor.close();
        if (null != cursor) cursor.close();

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

        if (MENU_HIDE.equals(menuState) &&
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
                    Logger.logD(Logger.getTag(), "onMenuOpened error");
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

    private void setNewSearcherFragment() {
        Logger.logD(Logger.getTag(), "setNewSearcherFragment");

        SearcherFragment searcherFragment = new SearcherFragment();
        searcherFragment.setFromSpinner(fromSpinner);
        searcherFragment.setToSpinner(toSpinner);

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.searcher_fragment_container, searcherFragment);
        transaction.commit();
    }

    @Override
    public void checkAsyncTaskStatusAndSetNewInstance() {
        Logger.logD(Logger.getTag(), "checkAsyncTaskStatusAndSetNewInstance()");

        if (((AsyncTask) rateUpdater).getStatus() != AsyncTask.Status.PENDING) {
            loadRateUpdaterClassName();
            setRateUpdaterAndTaskCanceler();
        }
    }

    @Override
    public void onRefreshStarted(View view) {
        Logger.logD(Logger.getTag(), "onRefreshStarted");

        checkAsyncTaskStatusAndSetNewInstance();
        updateRatesFromSource();
        if (rateUpdater instanceof CustomRateUpdaterMock) {
            Toaster.showToast(getString(R.string.custom_updating_info));
        }
    }

    @Override
    public void stopRefresh() {
        Logger.logD(Logger.getTag(), "stopRefresh");

        if (mPullToRefreshLayout.isRefreshing()) {
            mPullToRefreshLayout.setRefreshComplete();
        }
    }

    private void updateRatesFromSource() {
        Logger.logD(Logger.getTag(), "updateRatesFromSource");

        if (InternetChecker.isOnline(getApplicationContext())) {
            mPullToRefreshLayout.setRefreshing(true);
            taskCancelerHandler.postDelayed(taskCanceler, 15 * 1000);
            rateUpdater.execute();
            hideMenuWhileUpdating();
        } else {
            stopRefresh();
            if (!(rateUpdater instanceof CustomRateUpdaterMock)) {
                Toaster.showToast(getString(R.string.unavailable_network));
            }
        }
    }

    private void hideMenuWhileUpdating() {
        Logger.logD(Logger.getTag(), "hideMenuWhileUpdating");

        menuState = MENU_HIDE;
        invalidateOptionsMenu();
    }

    @Override
    public void readDataFromDB() {
        Logger.logD(Logger.getTag(), "readDataFromDB");

        final DBReaderTask dbReaderTask = new DBReaderTask();
        dbReaderTask.setRateUpdaterListener(this);

        rateUpdater.readDataFromDB(dbReaderTask);
    }

    @Override
    public void initSpinners() {
        Logger.logD(Logger.getTag(), "initSpinners");

        if (cursorAdapter == null) {
            cursorAdapter = new SpinnerCursorAdapter(getApplicationContext(), cursor);

            fromSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    fromCursor = (Cursor) parent.getItemAtPosition(position);

                    currencyFromCharCode = fromCursor.getString(1);
                    currencyFromNominal = BigDecimal.valueOf(fromCursor.getInt(2));
                    currencyFromToXRate = BigDecimal.valueOf(fromCursor.getDouble(3));

                    fromSpinnerSelectedPos = position;

                    fromAmountEditText.setText(fromAmountEditText.getText());

                    rateUpdater.saveSelectedCurrencySpinnersPositions(MainActivity.this, fromSpinnerSelectedPos, toSpinnerSelectedPos);

                    tvLabelForCurrentCurrencies.setText(composeTextForLabel());
                    syncShareActionData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
            toSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    toCursor = (Cursor) parent.getItemAtPosition(position);

                    currencyToCharCode = toCursor.getString(1);
                    currencyToNominal = BigDecimal.valueOf(toCursor.getInt(2));
                    currencyToToXRate = BigDecimal.valueOf(toCursor.getDouble(3));

                    toSpinnerSelectedPos = position;

                    fromAmountEditText.setText(fromAmountEditText.getText());

                    rateUpdater.saveSelectedCurrencySpinnersPositions(MainActivity.this, fromSpinnerSelectedPos, toSpinnerSelectedPos);

                    tvLabelForCurrentCurrencies.setText(composeTextForLabel());
                    syncShareActionData();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
        } else {
            cursorAdapter.changeCursor(cursor);
            cursorAdapter.notifyDataSetChanged();
        }

        fromSpinner.setAdapter(cursorAdapter);
        toSpinner.setAdapter(cursorAdapter);

        if (fromSpinner.getCount() == 0) {
            Toaster.showToast(getString(R.string.all_currencies_disabled));
        }
    }

    private void initEditAmount() {
        Logger.logD(Logger.getTag(), "initEditAmount");

        SpannableString enterAmountHint = getScaledSpannableString();

        fromAmountEditText = (EditText) findViewById(R.id.edit_from_amount);
        fromAmountEditText.requestFocus();
        fromAmountEditText.setHint(enterAmountHint);
        fromAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence fromAmount, int start, int before, int count) {
                if (fromAmount.length() != 0 && isPropertiesLoaded) {
                    if (!ignoreEditFromAmountChange) {
                        ignoreEditToAmountChange = true;
                        convertAndSetResultOnFromAmountChanged(); // TODO: 29.05.2017 отключить здесь собственный слушатель fromAmountEditText
                    }
                }
            }

            /**
             * See <a href="http://stackoverflow.com/a/24621325/5349748">Source</a>
             */
            @Override
            public void afterTextChanged(Editable editable) {
                ignoreEditToAmountChange = false;

                String fromAmount = editable.toString();
                if ("".equals(fromAmount)) {
                    toAmountEditText.getText().clear();
                }

                syncShareActionData();

                if (fromAmountEditText == null || fromAmount.isEmpty()) {
                    return;
                }

                String[] sParts = getPartsOfEditAmountText(fromAmount);

                fromAmountEditText.removeTextChangedListener(this);

                formatAndSetEditAmountText(fromAmountEditText, fromAmount, sParts);

                fromAmountEditText.addTextChangedListener(this);
            }
        });

        toAmountEditText = (EditText) findViewById(R.id.edit_to_amount);
        toAmountEditText.setHint(enterAmountHint);
        toAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence toAmount, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence toAmount, int start, int before, int count) {
                if (toAmount.length() != 0 && isPropertiesLoaded) {
                    if (!ignoreEditToAmountChange) {
                        ignoreEditFromAmountChange = true;
                        convertAndSetResultOnToAmountChanged(); // TODO: 29.05.2017 отключить здесь собственный слушатель toAmountEditText
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                ignoreEditFromAmountChange = false;

                String toAmount = editable.toString();
                if ("".equals(toAmount)) {
                    fromAmountEditText.getText().clear();
                }

                syncShareActionData();

                if (toAmountEditText == null || toAmount.isEmpty()) {
                    return;
                }

                String[] sParts = getPartsOfEditAmountText(toAmount);

                toAmountEditText.removeTextChangedListener(this);

                formatAndSetEditAmountText(toAmountEditText, toAmount, sParts);

                toAmountEditText.addTextChangedListener(this);
            }
        });
    }

    /**
     * See <a href="http://stackoverflow.com/a/19925406/5349748">Source</a>
     */
    @NonNull
    private SpannableString getScaledSpannableString() {
        String hint = getString(R.string.enter_amount_hint);
        SpannableString span = new SpannableString(hint);
        span.setSpan(
                new RelativeSizeSpan(0.8f), 0, hint.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
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

    private void formatAndSetEditAmountText(EditText editText, String s, String[] sParts) {
        String formatted;
        if (null == sParts) {
            formatted = CurrencyUtil.formatBigDecimal(prepareBigDecimal(s), 2);
        } else {
            formatted = CurrencyUtil.formatBigDecimal(prepareBigDecimal(sParts[0]), 2) + sParts[1];
        }
        editText.setText(formatted);
        if (formatted.length() > 19) {
            editText.setSelection(formatted.length() - 1);
            Toaster.showToast(getString(R.string.number_limit));
        } else {
            editText.setSelection(formatted.length());
        }
    }

    @NonNull
    private BigDecimal prepareBigDecimal(CharSequence s) {
        String plainEditAmountText = s.toString().replaceAll(" ", "");
        return new BigDecimal(plainEditAmountText);
    }

    @Override
    public void initTvDateTime() {
        Logger.logD(Logger.getTag(), "initTvDateTime");

        if (tvDateTime == null) {
            tvDateTime = (TextView) findViewById(R.id.tv_date_time);
            tvDateTime.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    fromSpinner.setSelection(fromSpinner.getSelectedItemPosition());
                    toSpinner.setSelection(toSpinner.getSelectedItemPosition());
                }
            });
        }
        tvDateTimeSetText();
    }

    private void tvDateTimeSetText() {
        Logger.logD(Logger.getTag(), "tvDateTimeSetText");

        tvDateTime.setText(String.format("%s%s",
                rateUpdater.getDescription(), DateUtil.getUpDateTimeStr(upDateTime)));
    }

    private void convertAndSetResultOnFromAmountChanged() {
        Logger.logD(Logger.getTag(), "convertAndSetResultOnFromAmountChanged");

        if (cancelConvertingIfNothingToConvert()) {
            Toaster.showToast(getString(R.string.all_currencies_disabled));
            return;
        }

        if (isNeedToReSwapValues) {
            isNeedToReSwapValues = false;
            reSwapEditAmountsValues();
        }

        String enteredAmount = fromAmountEditText.getText().toString();

        if ("".equals(enteredAmount)) {
            toAmountEditText.setText("");
        } else {
            String resultStr = getResultAmountForEditText(enteredAmount);
            toAmountEditText.setText(resultStr);
        }
    }

    private void convertAndSetResultOnToAmountChanged() {
        Logger.logD(Logger.getTag(), "convertAndSetResultOnToAmountChanged");

        if (cancelConvertingIfNothingToConvert()) {
            Toaster.showToast(getString(R.string.all_currencies_disabled));
            return;
        }

        if (!isNeedToReSwapValues) {
            isNeedToReSwapValues = true;
            reSwapEditAmountsValues();
        }

        String enteredAmount = toAmountEditText.getText().toString();

        if ("".equals(enteredAmount)) {
            fromAmountEditText.setText("");
        } else {
            String resultStr = getResultAmountForEditText(enteredAmount);
            fromAmountEditText.setText(resultStr);
        }
    }

    private String getResultAmountForEditText(String enteredAmount) {
        BigDecimal amount = getEnteredAmountOfMoney(enteredAmount);
        BigDecimal result = calculateResult(amount);

        String resultStr = CurrencyUtil.formatBigDecimal(result, 2);
        showToastIfVeryBigNumber(resultStr);
        return resultStr;
    }

    private void showToastIfVeryBigNumber(String resultStr) {
        if (resultStr.length() > 19) {
            Toaster.showToast(getString(R.string.number_limit));
        }
    }

    @Nullable
    private String convertForLabel() {
        Logger.logD(Logger.getTag(), "convertForLabel");

        if (cancelConvertingIfNothingToConvert()) {
            return null;
        }

        BigDecimal result = calculateResult(BigDecimal.ONE);
        int scale = rateUpdater.getDecimalScale();

        return CurrencyUtil.formatBigDecimal(result, scale);
    }

    private boolean cancelConvertingIfNothingToConvert() {
        return fromSpinner.getSelectedItem() == null || toSpinner.getSelectedItem() == null;
    }

    private void reSwapEditAmountsValues() {
        Logger.logD(Logger.getTag(), "reSwapEditAmountsValues");

        //rate
        currencyFromToXRate = currencyFromToXRate.add(currencyToToXRate);
        currencyToToXRate = currencyFromToXRate.subtract(currencyToToXRate);
        currencyFromToXRate = currencyFromToXRate.subtract(currencyToToXRate);

        //nominal
        currencyFromNominal = currencyFromNominal.add(currencyToNominal);
        currencyToNominal = currencyFromNominal.subtract(currencyToNominal);
        currencyFromNominal = currencyFromNominal.subtract(currencyToNominal);
    }

    public void swapFromTo(View v) {
        Logger.logD(Logger.getTag(), "swapFromTo");

        if (fromSpinner != null && toSpinner != null) {
            int fromSpinnerSelectedItemPos = fromSpinner.getSelectedItemPosition();
            fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
            toSpinner.setSelection(fromSpinnerSelectedItemPos);

            reSwapEditAmountsValues();
        }
    }

    private BigDecimal getEnteredAmountOfMoney(String amountOfMoneyWithGaps) {
        Logger.logD(Logger.getTag(), "getEnteredAmountOfMoney = " + amountOfMoneyWithGaps);

        if (TextUtils.isEmpty(amountOfMoneyWithGaps)) {
            return BigDecimal.ZERO;
        }
        String amountOfMoney = amountOfMoneyWithGaps.replaceAll(" ", "");
        return new BigDecimal(amountOfMoney);
    }

    private BigDecimal calculateResult(BigDecimal enteredAmountOfMoney) {
        if (enteredAmountOfMoney == null || enteredAmountOfMoney.equals(BigDecimal.ZERO) ||
                currencyFromToXRate == null || currencyFromToXRate.equals(BigDecimal.ZERO) ||
                currencyToToXRate == null || currencyToToXRate.equals(BigDecimal.ZERO) ||
                currencyToNominal == null || currencyToNominal.equals(BigDecimal.ZERO) ||
                currencyFromNominal == null || currencyFromNominal.equals(BigDecimal.ZERO)) {
            return BigDecimal.ZERO;
        }

        BigDecimal result;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            result = currencyFromToXRate
                    .divide(currencyToToXRate, DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                    .multiply(currencyToNominal)
                    .divide(currencyFromNominal, DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                    .multiply(enteredAmountOfMoney);
        } else {
            result = currencyToToXRate
                    .divide(currencyFromToXRate, DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                    .multiply(currencyFromNominal)
                    .divide(currencyToNominal, DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                    .multiply(enteredAmountOfMoney);

        }
        return result;
    }

    @Override
    public void saveUpDateTimeProperty() {
        Logger.logD(Logger.getTag(), "saveUpDateTimeProperty");

        rateUpdater.saveUpDateTime(this, upDateTime);
    }

    private void loadEditAmountProperties() {
        Logger.logD(Logger.getTag(), "loadEditAmountProperties");

        final String editFromAmount = AppPreferences.loadMainActivityEditFromAmount(this);
        fromAmountEditText.setText(editFromAmount);

        final String editToAmount = AppPreferences.loadMainActivityEditToAmount(this);
        toAmountEditText.setText(editToAmount);
    }

    private void loadUpDateTime() {
        Logger.logD(Logger.getTag(), "loadUpDateTime");

        upDateTime = rateUpdater.loadUpDateTime(this);
    }

    @Override
    public void loadSpinnersProperties() {
        Logger.logD(Logger.getTag(), "loadSpinnersProperties");

        final SpinnersPositions spinnersPositions = rateUpdater.loadSpinnersPositions(this);

        fromSpinner.setSelection(spinnersPositions.getFromSpinnerSelectedPos());
        toSpinner.setSelection(spinnersPositions.getToSpinnerSelectedPos());
    }

    private void loadRateUpdaterClassName() {
        Logger.logD(Logger.getTag(), "loadRateUpdaterClassName");

        rateUpdaterClassName = AppPreferences.loadRateUpdaterClassName(this);
    }

    private void setRateUpdaterAndTaskCanceler() {
        try {
            rateUpdater = (RateUpdater)
                    Class.forName(rateUpdaterClassName).getConstructor().newInstance();
        } catch (Exception e) {
            Logger.logE(Logger.getTag(), "can't instantiate instance of  " + rateUpdaterClassName);
            e.printStackTrace();
        }
        rateUpdater.setRateUpdaterListener(this);

        taskCancelerHandler = new Handler();
        taskCanceler = new TaskCanceler((AsyncTask) rateUpdater, this);
    }

    private void syncShareActionData() {
        Logger.logD(Logger.getTag(), "syncShareActionData");

        if (isPropertiesLoaded) {
            setShareIntent(composeTextForShare());
        }
    }

    private void setShareIntent(String text) {
        Logger.logD(Logger.getTag(), "setShareIntent " + text);
        if (text == null || shareActionProvider == null) {
            return;
        }

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
        intent.putExtra(Intent.EXTRA_TEXT, text);

        shareActionProvider.setShareIntent(
                Intent.createChooser(intent, getResources().getString(R.string.send_to)));

    }

    private String composeTextForShare() {
        Logger.logD(Logger.getTag(), "composeTextForShare");

        String fromCurrencyValue = fromAmountEditText.getText().toString();
        String toCurrencyValue;

        if (TextUtils.isEmpty(fromCurrencyValue)) {
            fromCurrencyValue = "1";
            toCurrencyValue = convertForLabel();
        } else {
            toCurrencyValue = toAmountEditText.getText().toString();
        }

        return getFormattedText(fromCurrencyValue, toCurrencyValue);
    }

    private String composeTextForLabel() {
        Logger.logD(Logger.getTag(), "composeTextForLabel");

        String fromCurrencyValue = "1";
        String toCurrencyValue = convertForLabel();

        return getFormattedText(fromCurrencyValue, toCurrencyValue);
    }

    private String getFormattedText(String fromCurrencyValue, String toCurrencyValue) {
        if (currencyFromCharCode == null || currencyToCharCode == null) {
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

    @Override
    public Cursor getCursor() {
        return cursor;
    }
}
