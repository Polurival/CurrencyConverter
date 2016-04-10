package com.polurival.cuco.strategies;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public class Valute {
    private String nominal;
    private String valuteToRubRate;

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
}
