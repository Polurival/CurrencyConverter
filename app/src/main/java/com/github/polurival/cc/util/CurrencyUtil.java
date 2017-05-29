package com.github.polurival.cc.util;

import com.github.polurival.cc.model.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrencyUtil {

    public static Currency getCurrency(double rate) {
        int nominal = 1;
        if (rate < 1) {
            int j = 0;
            while (rate < 1) {
                rate *= 10;
                j++;
            }
            nominal = (int) Math.pow(10, j);
        }
        return new Currency(nominal, rate);
    }

    /**
     * See <a href='http://stackoverflow.com/a/5323787/5349748'>source</a>
     */
    public static String formatBigDecimal(BigDecimal result, int scale) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

        symbols.setGroupingSeparator(' ');
        formatter.setDecimalFormatSymbols(symbols);

        BigDecimal scaledResult = result.setScale(scale, RoundingMode.HALF_EVEN);
        if (scale == 2) {
            return formatter.format(scaledResult.doubleValue());
        }
        return scaledResult.toPlainString();
    }
}
