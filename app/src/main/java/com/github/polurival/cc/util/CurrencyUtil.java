package com.github.polurival.cc.util;

import com.github.polurival.cc.model.Currency;

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
}
