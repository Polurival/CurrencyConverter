package com.github.polurival.cc.model.dto;

import java.math.BigDecimal;

public class CurrencyForCalculation {

    private String charCode;
    private BigDecimal nominal;
    private BigDecimal rate;

    public boolean isEmpty() {
        return getNominal() == null || getNominal().equals(BigDecimal.ZERO)
                || getRate() == null || getRate().equals(BigDecimal.ZERO);
    }

    public String getCharCode() {
        return charCode;
    }

    public void setCharCode(String charCode) {
        this.charCode = charCode;
    }

    public BigDecimal getNominal() {
        return nominal;
    }

    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CurrencyForCalculation that = (CurrencyForCalculation) o;

        if (charCode != null ? !charCode.equals(that.charCode) : that.charCode != null) {
            return false;
        }
        if (nominal != null ? !nominal.equals(that.nominal) : that.nominal != null) {
            return false;
        }
        return rate != null ? rate.equals(that.rate) : that.rate == null;

    }

    @Override
    public int hashCode() {
        int result = charCode != null ? charCode.hashCode() : 0;
        result = 31 * result + (nominal != null ? nominal.hashCode() : 0);
        result = 31 * result + (rate != null ? rate.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CurrencyForCalculation{" +
                "charCode='" + charCode + '\'' +
                ", nominal=" + nominal +
                ", rate=" + rate +
                '}';
    }
}
