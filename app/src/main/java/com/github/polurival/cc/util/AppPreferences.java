package com.github.polurival.cc.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.dto.SpinnersPositions;

import org.joda.time.LocalDateTime;

public class AppPreferences {

    //region ===================== MainActivityPreferences =====================

    public static void saveMainActivityProperties(Context context,
                                                  String editFromAmount,
                                                  String editToAmount,
                                                  String rateUpdaterClassName) {
        Logger.logD(Logger.getTag(), "saveMainActivityProperties");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.saved_from_edit_amount_text), editFromAmount);
        editor.putString(context.getString(R.string.saved_to_edit_amount_text), editToAmount);
        editor.putString(context.getString(R.string.saved_rate_updater_class),
                rateUpdaterClassName);

        editor.apply();
    }

    public static void saveMainActivityCBRateUpdaterSpinnersPositions(Context context,
                                                                      int fromSpinnerSelectedPos,
                                                                      int toSpinnerSelectedPos) {
        Logger.logD(Logger.getTag(), "saveMainActivityCBRateUpdaterSpinnersPositions");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.saved_cb_rf_from_spinner_pos),
                fromSpinnerSelectedPos);
        editor.putInt(context.getString(R.string.saved_cb_rf_to_spinner_pos),
                toSpinnerSelectedPos);

        editor.apply();
    }

    public static void saveMainActivityYahooRateUpdaterSpinnersPositions(Context context,
                                                                         int fromSpinnerSelectedPos,
                                                                         int toSpinnerSelectedPos) {
        Logger.logD(Logger.getTag(), "saveMainActivityYahooRateUpdaterSpinnersPositions");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.saved_yahoo_from_spinner_pos),
                fromSpinnerSelectedPos);
        editor.putInt(context.getString(R.string.saved_yahoo_to_spinner_pos),
                toSpinnerSelectedPos);

        editor.apply();
    }

    public static void saveMainActivityCustomRateUpdaterSpinnersPositions(Context context,
                                                                          int fromSpinnerSelectedPos,
                                                                          int toSpinnerSelectedPos) {
        Logger.logD(Logger.getTag(), "saveMainActivityCustomRateUpdaterSpinnersPositions");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.saved_custom_from_spinner_pos),
                fromSpinnerSelectedPos);
        editor.putInt(context.getString(R.string.saved_custom_to_spinner_pos),
                toSpinnerSelectedPos);

        editor.apply();
    }

    public static void saveCBRateUpdaterUpDateTime(Context context,
                                                   LocalDateTime upDateTime) {
        Logger.logD(Logger.getTag(), "saveCBRateUpdaterUpDateTime");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putLong(context.getString(R.string.saved_cb_rf_up_date_time),
                DateUtil.getUpDateTimeInSeconds(upDateTime));

        editor.apply();
    }

    public static void saveYahooRateUpdaterUpDateTime(Context context,
                                                      LocalDateTime upDateTime) {
        Logger.logD(Logger.getTag(), "saveYahooRateUpdaterUpDateTime");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putLong(context.getString(R.string.saved_yahoo_up_date_time),
                DateUtil.getUpDateTimeInSeconds(upDateTime));

        editor.apply();
    }

    public static boolean loadIsSetAutoUpdate(Context context) {
        Logger.logD(Logger.getTag(), "loadIsSetAutoUpdate");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getBoolean(context.getString(R.string.saved_is_set_auto_update),
                Boolean.valueOf(context.getString(R.string.saved_is_set_auto_update_default))); //todo переместить в bools.xml
    }

    public static String loadMainActivityEditFromAmount(Context context) {
        Logger.logD(Logger.getTag(), "loadMainActivityEditFromAmount");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(context.getString(R.string.saved_from_edit_amount_text),
                context.getString(R.string.saved_edit_amount_text_default));
    }

    public static String loadMainActivityEditToAmount(Context context) {
        Logger.logD(Logger.getTag(), "loadMainActivityEditToAmount");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(context.getString(R.string.saved_to_edit_amount_text),
                context.getString(R.string.saved_edit_amount_text_default));
    }

    public static LocalDateTime loadCBRateUpdaterUpDateTime(Context context) {
        Logger.logD(Logger.getTag(), "loadCBRateUpdaterUpDateTime");

        return loadRateUpdaterUpDateTime(context, context.getString(R.string.saved_cb_rf_up_date_time));
    }

    public static LocalDateTime loadYahooRateUpdaterUpDateTime(Context context) {
        Logger.logD(Logger.getTag(), "loadYahooRateUpdaterUpDateTime");

        return loadRateUpdaterUpDateTime(context, context.getString(R.string.saved_yahoo_up_date_time));
    }

    public static LocalDateTime loadCustomRateUpdaterUpDateTime(Context context) {
        Logger.logD(Logger.getTag(), "loadCustomRateUpdaterUpDateTime");

        return loadRateUpdaterUpDateTime(context, context.getString(R.string.saved_custom_up_date_time));
    }

    private static LocalDateTime loadRateUpdaterUpDateTime(Context context, String rateUpdaterUpDateTimeProperty) {
        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        final long upDateTimeInSeconds = sp.getLong(rateUpdaterUpDateTimeProperty,
                DateUtil.getDefaultDateTimeInSeconds());

        return DateUtil.getUpDateTime(upDateTimeInSeconds);
    }

    public static SpinnersPositions loadMainActivityCBRateUpdaterSpinnersPositions(Context context) {
        Logger.logD(Logger.getTag(), "loadMainActivityCBRateUpdaterSpinnersPositions");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int fromSpinnerSelectedPos = sp.getInt(context.getString(
                R.string.saved_cb_rf_from_spinner_pos),
                Constants.DEFAULT_CBRF_USD_POS);
        int toSpinnerSelectedPos = sp.getInt(context.getString(
                R.string.saved_cb_rf_to_spinner_pos),
                Constants.DEFAULT_CBRF_RUB_POS);

        return new SpinnersPositions(fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    public static SpinnersPositions loadMainActivityYahooRateUpdaterSpinnersPositions(Context context) {
        Logger.logD(Logger.getTag(), "loadMainActivityYahooRateUpdaterSpinnersPositions");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int fromSpinnerSelectedPos = sp.getInt(context.getString(
                R.string.saved_yahoo_from_spinner_pos),
                Constants.DEFAULT_YAHOO_USD_POS);
        int toSpinnerSelectedPos = sp.getInt(context.getString(
                R.string.saved_yahoo_to_spinner_pos),
                Constants.DEFAULT_YAHOO_RUB_POS);

        return new SpinnersPositions(fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    public static SpinnersPositions loadMainActivityCustomRateUpdaterSpinnersPositions(Context context) {
        Logger.logD(Logger.getTag(), "loadMainActivityCustomRateUpdaterSpinnersPositions");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        int fromSpinnerSelectedPos = sp.getInt(context.getString(
                R.string.saved_custom_from_spinner_pos),
                Constants.DEFAULT_CUSTOM_USD_POS);
        int toSpinnerSelectedPos = sp.getInt(context.getString(
                R.string.saved_custom_to_spinner_pos),
                Constants.DEFAULT_CUSTOM_RUB_POS);

        return new SpinnersPositions(fromSpinnerSelectedPos, toSpinnerSelectedPos);
    }

    public static String loadRateUpdaterClassName(Context context) {
        Logger.logD(Logger.getTag(), "loadRateUpdaterClassName");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getString(context.getString(R.string.saved_rate_updater_class),
                context.getString(R.string.saved_rate_updater_class_default));
    }

    //endregion

    //region ===================== DataSourceActivityPreferences =====================

    public static void saveIsSetAutoUpdate(Context context,
                                           boolean isSetAutoUpdate) {
        Logger.logD(Logger.getTag(), "saveIsSetAutoUpdate");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putBoolean(context.getString(R.string.saved_is_set_auto_update), isSetAutoUpdate);

        editor.apply();
    }

    public static void saveRateUpdaterClassName(Context context,
                                                String rateUpdaterClassName) {
        Logger.logD(Logger.getTag(), "saveRateUpdaterClassName");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.saved_rate_updater_class), rateUpdaterClassName);

        editor.apply();
    }

    //endregion


    //region ===================== CurrencySwitchingActivityPreferences =====================

    public static void resetSpinnersPositionsToZero(Context context) {
        Logger.logD(Logger.getTag(), "resetSpinnersPositionsToZero");

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

    public static void saveCustomRateUpDateTime(Context context) {
        Logger.logD(Logger.getTag(), "saveCustomRateUpDateTime");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putLong(context.getString(R.string.saved_custom_up_date_time),
                DateUtil.getUpDateTimeInSeconds(DateUtil.getCurrentDateTime()));

        editor.apply();
    }

    public static void saveCustomRateFragmentCustomSpinnerSelectedPosition(Context context,
                                                                           int customCurrencySpinnerPosition) {
        Logger.logD(Logger.getTag(), "saveCustomRateFragmentCustomSpinnerSelectedPosition");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = sp.edit();

        editor.putInt(context.getString(R.string.saved_custom_fragment_spinner_pos),
                customCurrencySpinnerPosition);

        editor.apply();
    }

    public static int loadCustomRateFragmentCustomSpinnerSelectedPosition(Context context) {
        Logger.logD(Logger.getTag(), "loadCustomRateFragmentCustomSpinnerSelectedPosition");

        final SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        return sp.getInt(context.getString(R.string.saved_custom_fragment_spinner_pos), 0);
    }

    //endregion
}
