package com.polurival.cuco.strategies;

import android.graphics.drawable.Drawable;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public class Valute {
    private String nominal;
    private String valuteToRubRate;
    private Drawable flag;

    public Valute(String nominal, String value) {
        this.nominal = nominal;
        this.valuteToRubRate = value;
    }

    public String getNominal() {
        return nominal;
    }

    public String getValuteToRubRate() {
        return valuteToRubRate;
    }

    public Drawable getFlag() {
        return flag;
    }
}
