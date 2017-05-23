package com.github.polurival.cc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.EditText;
import android.widget.Spinner;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.updater.CBRateUpdaterTask;
import com.github.polurival.cc.model.updater.RateUpdater;
import com.github.polurival.cc.model.updater.YahooRateUpdaterTask;

import org.joda.time.LocalDateTime;

/**
 * Created by Polurival
 * on 09.10.2016.
 */
public class SharedPreferencesManager {

    //region ===================== MainActivityPreferences =====================

    public static void saveMainActivityProperties(Context context,
                                                  EditText editFromAmount,
                                                  EditText editToAmount,
                                                  RateUpdater rateUpdater) {
        Logger.logD(Logger.getTag(), "saveMainActivityProperties");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.saved_from_edit_amount_text),
                editFromAmount.getText().toString());
        editor.putString(context.getString(R.string.saved_to_edit_amount_text),
                editToAmount.getText().toString());
        editor.putString(context.getString(R.string.saved_rate_updater_class),
                rateUpdater.getClass().getName());

        editor.apply();
    }

    public static void saveMainActivitySpinnersProperties(Context context,
                                                          RateUpdater rateUpdater,
                                                          int fromSpinnerSelectedPos,
                                                          int toSpinnerSelectedPos) {
        Logger.logD(Logger.getTag(), "saveMainActivitySpinnersProperties");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putInt(context.getString(R.string.saved_cb_rf_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(context.getString(R.string.saved_cb_rf_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putInt(context.getString(R.string.saved_yahoo_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(context.getString(R.string.saved_yahoo_to_spinner_pos),
                    toSpinnerSelectedPos);
        } else {
            editor.putInt(context.getString(R.string.saved_custom_from_spinner_pos),
                    fromSpinnerSelectedPos);
            editor.putInt(context.getString(R.string.saved_custom_to_spinner_pos),
                    toSpinnerSelectedPos);
        }

        editor.apply();
    }

    public static void saveMainActivityUpDateTimeProperty(Context context,
                                                          RateUpdater rateUpdater,
                                                          LocalDateTime upDateTime) {
        Logger.logD(Logger.getTag(), "saveMainActivityUpDateTimeProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        if (rateUpdater instanceof CBRateUpdaterTask) {
            editor.putLong(context.getString(R.string.saved_cb_rf_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            editor.putLong(context.getString(R.string.saved_yahoo_up_date_time),
                    DateUtil.getUpDateTimeInSeconds(upDateTime));
        }

        editor.apply();
    }

    public static boolean loadMainActivityIsSetAutoUpdateProperty(Context context) {
        Logger.logD(Logger.getTag(), "loadMainActivityIsSetAutoUpdateProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getBoolean(context.getString(R.string.saved_is_set_auto_update),
                Boolean.valueOf(context.getString(R.string.saved_is_set_auto_update_default))); //todo переместить в bools.xml
    }

    public static void loadMainActivityEditAmountProperties(Context context,
                                                            EditText editFromAmount,
                                                            EditText editToAmount) {
        Logger.logD(Logger.getTag(), "loadMainActivityEditAmountProperties");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String editFromAmountText =
                sp.getString(context.getString(R.string.saved_from_edit_amount_text),
                        context.getString(R.string.saved_edit_amount_text_default));
        editFromAmount.setText(editFromAmountText);
        String editToAmountText =
                sp.getString(context.getString(R.string.saved_to_edit_amount_text),
                        context.getString(R.string.saved_edit_amount_text_default));
        editToAmount.setText(editToAmountText);
    }

    public static LocalDateTime loadMainActivityUpDateTimeProperty(Context context,
                                                                   RateUpdater rateUpdater) {
        Logger.logD(Logger.getTag(), "loadMainActivityUpDateTimeProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String savedUpDateTime;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            savedUpDateTime = context.getString(R.string.saved_cb_rf_up_date_time);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            savedUpDateTime = context.getString(R.string.saved_yahoo_up_date_time);
        } else {
            savedUpDateTime = context.getString(R.string.saved_custom_up_date_time);
        }
        long upDateTimeInSeconds = sp.getLong(savedUpDateTime, DateUtil.getDefaultDateTimeInSeconds());
        return DateUtil.getUpDateTime(upDateTimeInSeconds);
    }

    public static int[] loadMainActivitySpinnersProperties(Context context,
                                                           RateUpdater rateUpdater,
                                                           Spinner fromSpinner,
                                                           Spinner toSpinner) {
        Logger.logD(Logger.getTag(), "loadMainActivitySpinnersProperties");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int fromSpinnerSelectedPos;
        int toSpinnerSelectedPos;
        if (rateUpdater instanceof CBRateUpdaterTask) {
            fromSpinnerSelectedPos = sp.getInt(context.getString(
                    R.string.saved_cb_rf_from_spinner_pos),
                    Constants.DEFAULT_CBRF_USD_POS);
            toSpinnerSelectedPos = sp.getInt(context.getString(
                    R.string.saved_cb_rf_to_spinner_pos),
                    Constants.DEFAULT_CBRF_RUB_POS);
        } else if (rateUpdater instanceof YahooRateUpdaterTask) {
            fromSpinnerSelectedPos = sp.getInt(context.getString(
                    R.string.saved_yahoo_from_spinner_pos),
                    Constants.DEFAULT_YAHOO_USD_POS);
            toSpinnerSelectedPos = sp.getInt(context.getString(
                    R.string.saved_yahoo_to_spinner_pos),
                    Constants.DEFAULT_YAHOO_RUB_POS);
        } else {
            fromSpinnerSelectedPos = sp.getInt(context.getString(
                    R.string.saved_custom_from_spinner_pos),
                    Constants.DEFAULT_CUSTOM_USD_POS);
            toSpinnerSelectedPos = sp.getInt(context.getString(
                    R.string.saved_custom_to_spinner_pos),
                    Constants.DEFAULT_CUSTOM_RUB_POS);
        }

        fromSpinner.setSelection(fromSpinnerSelectedPos);
        toSpinner.setSelection(toSpinnerSelectedPos);

        return new int[]{fromSpinnerSelectedPos, toSpinnerSelectedPos};
    }

    public static String loadRateUpdaterClassNameProperty(Context context) {
        Logger.logD(Logger.getTag(), "loadRateUpdaterClassNameProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(context.getString(R.string.saved_rate_updater_class),
                context.getString(R.string.saved_rate_updater_class_default));
    }

    //endregion

    //region ===================== DataSourceActivityPreferences =====================

    public static void saveDataSourceActivityIsSetAutoUpdateProperty(Context context,
                                                                     boolean isSetAutoUpdate) {
        Logger.logD(Logger.getTag(), "saveDataSourceActivityIsSetAutoUpdateProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(context.getString(R.string.saved_is_set_auto_update), isSetAutoUpdate);

        editor.apply();
    }

    public static void saveDataSourceActivityRateUpdaterNameProperty(Context context,
                                                                     String rateUpdaterClassName) {
        Logger.logD(Logger.getTag(), "saveDataSourceActivityRateUpdaterNameProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.saved_rate_updater_class), rateUpdaterClassName);

        editor.apply();
    }

    public static boolean loadDataSourceActivityIsSetAutoUpdateProperty(Context context) {
        Logger.logD(Logger.getTag(), "loadDataSourceActivityIsSetAutoUpdateProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getBoolean(context.getString(R.string.saved_is_set_auto_update),
                Boolean.valueOf(context.getString(R.string.saved_is_set_auto_update_default)));
    }

    public static String loadDataSourceActivityRateUpdaterNameProperty(Context context) {
        Logger.logD(Logger.getTag(), "loadDataSourceActivityRateUpdaterNameProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(context.getString(R.string.saved_rate_updater_class),
                context.getString(R.string.saved_rate_updater_class_default));
    }

    //endregion


    //region ===================== CurrencySwitchingActivityPreferences =====================

    public static void saveCurrencySwitchingActivityDefaultPositionProperties(Context context) {
        Logger.logD(Logger.getTag(), "saveCurrencySwitchingActivityDefaultPositionProperties");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.saved_cb_rf_from_spinner_pos), 0);
        editor.putInt(context.getString(R.string.saved_cb_rf_to_spinner_pos), 0);

        editor.putInt(context.getString(R.string.saved_yahoo_from_spinner_pos), 0);
        editor.putInt(context.getString(R.string.saved_yahoo_to_spinner_pos), 0);

        editor.putInt(context.getString(R.string.saved_custom_from_spinner_pos), 0);
        editor.putInt(context.getString(R.string.saved_custom_to_spinner_pos), 0);

        editor.putInt(context.getString(R.string.saved_custom_fragment_spinner_pos), 0);

        editor.apply();
    }

    //endregion


    //region ===================== CustomRateFragmentPreferences =====================

    public static void saveCustomRateFragmentCustomDateProperty(Context context) {
        Logger.logD(Logger.getTag(), "saveCustomRateFragmentCustomDateProperty");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putLong(context.getString(R.string.saved_custom_up_date_time),
                DateUtil.getUpDateTimeInSeconds(DateUtil.getCurrentDateTime()));

        editor.apply();
    }

    private void saveCustomRateFragmentCustomSpinnerSelectedPos(Context context,
                                                                Spinner customCurrencySpinner) {
        Logger.logD(Logger.getTag(), "saveCustomRateFragmentCustomSpinnerSelectedPos");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.saved_custom_fragment_spinner_pos),
                customCurrencySpinner.getSelectedItemPosition());

        editor.apply();
    }

    private void loadCustomRateFragmentCustomSpinnerSelectedPos(Context context,
                                                                Spinner customCurrencySpinner) {
        Logger.logD(Logger.getTag(), "loadCustomRateFragmentCustomSpinnerSelectedPos");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int customSpinnerSelectedPos =
                sp.getInt(context.getString(R.string.saved_custom_fragment_spinner_pos), 0);
        customCurrencySpinner.setSelection(customSpinnerSelectedPos);
    }

    //endregion
}
