package com.github.polurival.cc.model.dto;

import android.text.TextUtils;

import com.github.polurival.cc.util.Logger;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

public class CurrenciesRelations {

    private static final int DEFAULT_SCALE = 10;

    private CurrencyForCalculation currencyFrom;
    private CurrencyForCalculation currencyTo;

    public CurrenciesRelations() {
        currencyFrom = new CurrencyForCalculation();
        currencyTo = new CurrencyForCalculation();
    }

    public boolean isEmpty() {
        return currencyFrom.isEmpty() || currencyTo.isEmpty();
    }

    public void swapCurrenciesValues() {
        Logger.logD(Logger.getTag(), "swapCurrenciesValues");

        currencyFrom.setRate(currencyFrom.getRate().add(currencyTo.getRate()));
        currencyTo.setRate(currencyFrom.getRate().subtract(currencyTo.getRate()));
        currencyFrom.setRate(currencyFrom.getRate().subtract(currencyTo.getRate()));

        currencyFrom.setNominal(currencyFrom.getNominal().add(currencyTo.getNominal()));
        currencyTo.setNominal(currencyFrom.getNominal().subtract(currencyTo.getNominal()));
        currencyFrom.setNominal(currencyFrom.getNominal().subtract(currencyTo.getNominal()));
    }

    public BigDecimal getEnteredAmountOfMoney(String amountOfMoneyWithGaps) {
        Logger.logD(Logger.getTag(), "getEnteredAmountOfMoney = " + amountOfMoneyWithGaps);

        if (TextUtils.isEmpty(amountOfMoneyWithGaps)) {
            return BigDecimal.ZERO;
        }
        String amountOfMoney = amountOfMoneyWithGaps.replaceAll(" ", "");
        return new BigDecimal(amountOfMoney);
    }

    /**
     * See <a href='http://stackoverflow.com/a/5323787/5349748'>source</a>
     */
    public String formatBigDecimal(BigDecimal result, int scale) {
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

    public BigDecimal calculateConversionResultForCB(BigDecimal enteredAmountOfMoney) {
        return currencyFrom.getRate()
                .divide(currencyTo.getRate(), DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                .multiply(currencyTo.getNominal())
                .divide(currencyFrom.getNominal(), DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                .multiply(enteredAmountOfMoney);
    }

    public BigDecimal calculateConversionResultForYahooOrCustom(BigDecimal enteredAmountOfMoney) {
        return currencyTo.getRate()
                .divide(currencyFrom.getRate(), DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                .multiply(currencyFrom.getNominal())
                .divide(currencyTo.getNominal(), DEFAULT_SCALE, RoundingMode.HALF_EVEN)
                .multiply(enteredAmountOfMoney);
    }

    public CurrencyForCalculation getCurrencyFrom() {
        return currencyFrom;
    }

    public CurrencyForCalculation getCurrencyTo() {
        return currencyTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CurrenciesRelations that = (CurrenciesRelations) o;

        if (currencyFrom != null ? !currencyFrom.equals(that.currencyFrom) : that.currencyFrom != null) {
            return false;
        }
        return currencyTo != null ? currencyTo.equals(that.currencyTo) : that.currencyTo == null;

    }

    @Override
    public int hashCode() {
        int result = currencyFrom != null ? currencyFrom.hashCode() : 0;
        result = 31 * result + (currencyTo != null ? currencyTo.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CurrenciesRelations{" +
                "currencyFrom=" + currencyFrom +
                ", currencyTo=" + currencyTo +
                '}';
    }
}
