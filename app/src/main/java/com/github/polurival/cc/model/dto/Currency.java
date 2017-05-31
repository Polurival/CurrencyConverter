package com.github.polurival.cc.model.dto;

public final class Currency {
    private String charCode;
    private int nominal;
    private double rate;
    private int switching;

    public Currency(int nominal, double rate) {
        this.nominal = nominal;
        this.rate = rate;
    }

    public Currency(String charCode, int nominal, double rate, int switching) {
        this.charCode = charCode;
        this.nominal = nominal;
        this.rate = rate;
        this.switching = switching;
    }

    public int getNominal() {
        return nominal;
    }

    public double getRate() {
        return rate;
    }

    public int getSwitching() {
        return switching;
    }

    public String getCharCode() {
        return charCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Currency currency = (Currency) o;

        if (nominal != currency.nominal) {
            return false;
        }
        if (Double.compare(currency.rate, rate) != 0) {
            return false;
        }
        if (switching != currency.switching) {
            return false;
        }
        return charCode != null ? charCode.equals(currency.charCode) : currency.charCode == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = charCode != null ? charCode.hashCode() : 0;
        result = 31 * result + nominal;
        temp = Double.doubleToLongBits(rate);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + switching;
        return result;
    }

    @Override
    public String toString() {
        return "Currency{" +
                "charCode='" + charCode + '\'' +
                ", nominal=" + nominal +
                ", rate=" + rate +
                ", switching=" + switching +
                '}';
    }
}
