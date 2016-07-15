package com.github.polurival.cc;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Spinner;

import com.github.polurival.cc.util.Logger;

/**
 * Created by Polurival
 * on 10.07.2016.
 */
public class SpinnerSelectionDialog extends DialogFragment {

    private Spinner fromSpinner;
    private Spinner toSpinner;
    private int searchedCharCodeSpinnerPos;

    public void setFromSpinner(Spinner fromSpinner) {
        this.fromSpinner = fromSpinner;
    }

    public void setToSpinner(Spinner toSpinner) {
        this.toSpinner = toSpinner;
    }

    public void setSearchedCharCodeSpinnerPos(int searchedCharCodeSpinnerPos) {
        this.searchedCharCodeSpinnerPos = searchedCharCodeSpinnerPos;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Logger.logD(Logger.getTag(), "onActivityCreated");

        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Logger.logD(Logger.getTag(), "onCreateDialog");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_list_selection_dialog);

        builder.setNeutralButton(R.string.convert_from, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                fromSpinner.setSelection(searchedCharCodeSpinnerPos);
            }
        });
        builder.setPositiveButton(R.string.convert_to, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toSpinner.setSelection(searchedCharCodeSpinnerPos);
            }
        });

        return builder.create();
    }
}
