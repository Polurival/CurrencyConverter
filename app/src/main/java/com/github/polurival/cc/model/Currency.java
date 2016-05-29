package com.github.polurival.cc.model;

/**
 * Created by Polurival
 * on 10.04.2016.
 */
public class Currency {
    private int nominal;
    private double value;
    private int nameResourceId;
    private int flagResourceId;

    public Currency(int nominal, double value) {
        this.nominal = nominal;
        this.value = value;
    }

    public Currency(int nominal, double value, int nameResourceId, int flagResourceId) {
        this.nominal = nominal;
        this.value = value;
        this.nameResourceId = nameResourceId;
        this.flagResourceId = flagResourceId;
    }

    public int getNominal() {
        return nominal;
    }

    public double getDoubleNominal() {
        return (double) nominal;
    }

    public double getValue() {
        return value;
    }

    public int getNameResourceId() {
        return nameResourceId;
    }

    public int getFlagResourceId() {
        return flagResourceId;
    }
}
