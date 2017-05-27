package com.github.polurival.cc.model.dto;

public class SpinnersPositions {

    private int fromSpinnerSelectedPos;
    private int toSpinnerSelectedPos;

    public SpinnersPositions(int fromSpinnerSelectedPos, int toSpinnerSelectedPos) {
        this.fromSpinnerSelectedPos = fromSpinnerSelectedPos;
        this.toSpinnerSelectedPos = toSpinnerSelectedPos;
    }

    public int getFromSpinnerSelectedPos() {
        return fromSpinnerSelectedPos;
    }

    public int getToSpinnerSelectedPos() {
        return toSpinnerSelectedPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SpinnersPositions that = (SpinnersPositions) o;

        if (fromSpinnerSelectedPos != that.fromSpinnerSelectedPos) return false;
        return toSpinnerSelectedPos == that.toSpinnerSelectedPos;

    }

    @Override
    public int hashCode() {
        int result = fromSpinnerSelectedPos;
        result = 31 * result + toSpinnerSelectedPos;
        return result;
    }
}
