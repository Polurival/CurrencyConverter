package com.github.polurival.cc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.polurival.cc.AppContext;
import com.github.polurival.cc.R;
import com.github.polurival.cc.model.updater.CBRateUpdaterTask;
import com.github.polurival.cc.model.updater.RateUpdater;
import com.github.polurival.cc.model.updater.YahooRateUpdaterTask;

import org.joda.time.LocalDateTime;

/**
 * Created by Polurival
 * on 09.10.2016.
 */
public class PrefManager {

    private static Context ctx = AppContext.getContext();
    private static SharedPreferences preferences =
            PreferenceManager.getDefaultSharedPreferences(AppContext.getContext());

    /*
     * MainActivityPreferences
     */

    public static void saveMainActivityProperties(EditText editFromAmount,
                                                  EditText editToAmount,
                                                  RateUpdater rateUpdater) {
        Logger.logD(Logger.getTag(), "saveMainActivityProperties");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(ctx.getString(R.string.saved_from_edit_amount_text),
                editFromAmount.getText().toString());
        editor.putString(ctx.getString(R.string.saved_to_edit_amount_text),
                editToAmount.getText().toString());
        editor.putString(ctx.getString(R.string.saved_rate_updater_class),
                rateUpdater.getClass().getName());

        editor.apply();
    }

    public static void saveMainActivitySpinnersProperties(RateUpdater rateUpdater,
                                                          int fromSpinnerSelectedPos,
                                                          int toSpinnerSelectedPos) {
        Logger.logD(Logger.getTag(), "saveMainActivitySpinnersProperties");

        SharedPreferences.Editor editor = preferences.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putInt(ctx.getString(R.string.saved_cb_rf_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(ctx.getString(R.string.saved_cb_rf_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putInt(ctx.getString(R.string.saved_yahoo_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(ctx.getString(R.string.saved_yahoo_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else {
            editor.putInt(ctx.getString(R.string.saved_custom_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(ctx.getString(R.string.saved_custom_to_spinner_pos),
                    toSpinnerSelectedPos);
        }

        editor.apply();
    }

    public static void saveMainActivityUpDateTimeProperty(RateUpdater rateUpdater,
                                                          LocalDateTime upDateTime) {
        Logger.logD(Logger.getTag(), "saveMainActivityUpDateTimeProperty");

        SharedPreferences.Editor editor = preferences.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putLong(ctx.getString(R.string.saved_cb_rf_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putLong(ctx.getString(R.string.saved_yahoo_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        }

        editor.apply();
    }

    public static boolean loadMainActivityIsSetAutoUpdateProperty() {
        Logger.logD(Logger.getTag(), "loadMainActivityIsSetAutoUpdateProperty");

        return preferences.getBoolean(ctx.getString(R.string.saved_is_set_auto_update),
                Boolean.valueOf(ctx.getString(R.string.saved_is_set_auto_update_default)));
    }

    public static void loadMainActivityEditAmountProperties(EditText editFromAmount,
                                                            EditText editToAmount) {
        Logger.logD(Logger.getTag(), "loadMainActivityEditAmountProperties");

        String editFromAmountText =
                preferences.getString(ctx.getString(R.string.saved_from_edit_amount_text),
                        ctx.getString(R.string.saved_edit_amount_text_default));
        editFromAmount.setText(editFromAmountText);
        String editToAmountText =
                preferences.getString(ctx.getString(R.string.saved_to_edit_amount_text),
                        ctx.getString(R.string.saved_edit_amount_text_default));
        editToAmount.setText(editToAmountText);
    }

    public static LocalDateTime loadMainActivityUpDateTimeProperty(RateUpdater rateUpdater) {
        Logger.logD(Logger.getTag(), "loadMainActivityUpDateTimeProperty");

        String savedUpDateTime;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            savedUpDateTime = ctx.getString(R.string.saved_cb_rf_up_date_time);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            savedUpDateTime = ctx.getString(R.string.saved_yahoo_up_date_time);
        } else {
            savedUpDateTime = ctx.getString(R.string.saved_custom_up_date_time);
        }
        long upDateTimeInSeconds =
                preferences.getLong(savedUpDateTime, DateUtil.getDefaultDateTimeInSeconds());
        return DateUtil.getUpDateTime(upDateTimeInSeconds);
    }

    public static int[] loadMainActivitySpinnersProperties(RateUpdater rateUpdater,
                                                           Spinner fromSpinner, Spinner toSpinner) {
        Logger.logD(Logger.getTag(), "loadMainActivitySpinnersProperties");

        int fromSpinnerSelectedPos;
        int toSpinnerSelectedPos;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            fromSpinnerSelectedPos = preferences.getInt(ctx.getString(
                    R.string.saved_cb_rf_from_spinner_pos),
                    Constants.DEFAULT_CBRF_USD_POS);
            toSpinnerSelectedPos = preferences.getInt(ctx.getString(
                    R.string.saved_cb_rf_to_spinner_pos),
                    Constants.DEFAULT_CBRF_RUB_POS);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            fromSpinnerSelectedPos = preferences.getInt(ctx.getString(
                    R.string.saved_yahoo_from_spinner_pos),
                    Constants.DEFAULT_YAHOO_USD_POS);
            toSpinnerSelectedPos = preferences.getInt(ctx.getString(
                    R.string.saved_yahoo_to_spinner_pos),
                    Constants.DEFAULT_YAHOO_RUB_POS);
        } else {
            fromSpinnerSelectedPos = preferences.getInt(ctx.getString(
                    R.string.saved_custom_from_spinner_pos),
                    Constants.DEFAULT_CUSTOM_USD_POS);
            toSpinnerSelectedPos = preferences.getInt(ctx.getString(
                    R.string.saved_custom_to_spinner_pos),
                    Constants.DEFAULT_CUSTOM_RUB_POS);
        }

        fromSpinner.setSelection(fromSpinnerSelectedPos);
        toSpinner.setSelection(toSpinnerSelectedPos);

        return new int[]{fromSpinnerSelectedPos, toSpinnerSelectedPos};
    }

    public static String loadRateUpdaterClassNameProperty() {
        Logger.logD(Logger.getTag(), "loadRateUpdaterClassNameProperty");

        return preferences.getString(ctx.getString(R.string.saved_rate_updater_class),
                ctx.getString(R.string.saved_rate_updater_class_default));
    }


    /*
     * DataSourceActivityPreferences
     */

    public static void saveDataSourceActivityIsSetAutoUpdateProperty(boolean isSetAutoUpdate) {
        Logger.logD(Logger.getTag(), "saveDataSourceActivityIsSetAutoUpdateProperty");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putBoolean(ctx.getString(R.string.saved_is_set_auto_update), isSetAutoUpdate);

        editor.apply();
    }

    public static void saveDataSourceActivityRateUpdaterNameProperty(String rateUpdaterClassName) {
        Logger.logD(Logger.getTag(), "saveDataSourceActivityRateUpdaterNameProperty");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(ctx.getString(R.string.saved_rate_updater_class), rateUpdaterClassName);

        editor.apply();
    }

    public static boolean loadDataSourceActivityIsSetAutoUpdateProperty() {
        Logger.logD(Logger.getTag(), "loadDataSourceActivityIsSetAutoUpdateProperty");

        return preferences.getBoolean(ctx.getString(R.string.saved_is_set_auto_update),
                Boolean.valueOf(ctx.getString(R.string.saved_is_set_auto_update_default)));
    }

    public static String loadDataSourceActivityRateUpdaterNameProperty() {
        Logger.logD(Logger.getTag(), "loadDataSourceActivityRateUpdaterNameProperty");

        return preferences.getString(ctx.getString(R.string.saved_rate_updater_class),
                ctx.getString(R.string.saved_rate_updater_class_default));
    }


    /*
     * CurrencySwitchingActivityPreferences
     */

    public static void saveCurrencySwitchingActivityDefaultPositionProperties() {
        Logger.logD(Logger.getTag(), "saveCurrencySwitchingActivityDefaultPositionProperties");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(ctx.getString(R.string.saved_cb_rf_from_spinner_pos), 0);
        editor.putInt(ctx.getString(R.string.saved_cb_rf_to_spinner_pos), 0);

        editor.putInt(ctx.getString(R.string.saved_yahoo_from_spinner_pos), 0);
        editor.putInt(ctx.getString(R.string.saved_yahoo_to_spinner_pos), 0);

        editor.putInt(ctx.getString(R.string.saved_custom_from_spinner_pos), 0);
        editor.putInt(ctx.getString(R.string.saved_custom_to_spinner_pos), 0);

        editor.putInt(ctx.getString(R.string.saved_custom_fragment_spinner_pos), 0);

        editor.apply();
    }


    /*
     * CustomRateFragmentPreferences
     */

    public static void saveCustomRateFragmentCustomDateProperty() {
        Logger.logD(Logger.getTag(), "saveCustomRateFragmentCustomDateProperty");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putLong(ctx.getString(R.string.saved_custom_up_date_time),
                DateUtil.getUpDateTimeInSeconds(DateUtil.getCurrentDateTime()));

        editor.apply();
    }

    private void saveCustomRateFragmentCustomSpinnerSelectedPos(Spinner customCurrencySpinner) {
        Logger.logD(Logger.getTag(), "saveCustomRateFragmentCustomSpinnerSelectedPos");

        SharedPreferences.Editor editor = preferences.edit();

        editor.putInt(ctx.getString(R.string.saved_custom_fragment_spinner_pos),
                customCurrencySpinner.getSelectedItemPosition());

        editor.apply();
    }

    private void loadCustomRateFragmentCustomSpinnerSelectedPos(Spinner customCurrencySpinner) {
        Logger.logD(Logger.getTag(), "loadCustomRateFragmentCustomSpinnerSelectedPos");

        int customSpinnerSelectedPos =
                preferences.getInt(ctx.getString(R.string.saved_custom_fragment_spinner_pos), 0);
        customCurrencySpinner.setSelection(customSpinnerSelectedPos);
    }
}
