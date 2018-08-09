package com.github.polurival.cc;

import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Spinner;

import com.github.polurival.cc.adapter.AutoCompleteTVAdapter;
import com.github.polurival.cc.util.AppPreferences;
import com.github.polurival.cc.util.Logger;

/**
 * <p>See <a href="http://www.vogella.com/tutorials/AndroidFragments/article.html">Source</a></p>
 */
public class SearcherFragment extends Fragment {

    private SearcherFragment.Listener listener;

    private Spinner fromSpinner;
    private Spinner toSpinner;
    private Spinner customSpinner;
    private ListView switchingListView;

    private AutoCompleteTextView currencySearcher;

    public SearcherFragment() {
    }

    public void setFromSpinner(Spinner fromSpinner) {
        this.fromSpinner = fromSpinner;
    }

    public void setToSpinner(Spinner toSpinner) {
        this.toSpinner = toSpinner;
    }

    public void setCustomSpinner(Spinner customSpinner) {
        this.customSpinner = customSpinner;
    }

    public void setSwitchingListView(ListView switchingListView) {
        this.switchingListView = switchingListView;
    }

    public interface Listener {
        Cursor getCommonCursor();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Logger.logD(Logger.getTag(), "onAttach");

        if (context instanceof SearcherFragment.Listener) {
            listener = (SearcherFragment.Listener) context;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Logger.logD(Logger.getTag(), "onCreateView()");

        View fragmentView = inflater.inflate(R.layout.fragment_searcher_layout, container, false);
        currencySearcher = fragmentView.findViewById(R.id.tv_auto_complete);

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        Logger.logD(Logger.getTag(), "onStart");

        initCurrencySearcher();
    }

    private void initCurrencySearcher() {
        Logger.logD(Logger.getTag(), "initSearchAdapter");

        AutoCompleteTVAdapter autoCompleteTvAdapter;
        if (listener instanceof CurrencySwitchingActivity) {
            autoCompleteTvAdapter = initCursorAdapter(false);
        } else {
            autoCompleteTvAdapter = initCursorAdapter(true);
        }

        currencySearcher.setAdapter(autoCompleteTvAdapter);
        currencySearcher.setThreshold(1);
        currencySearcher.setOnItemClickListener(searcherClickListener);
    }

    @NonNull
    private AutoCompleteTVAdapter initCursorAdapter(boolean withSwitching) {
        String rateUpdaterClassName = AppPreferences.loadRateUpdaterClassName(getActivity());
        return new AutoCompleteTVAdapter(
                getActivity().getApplicationContext(), rateUpdaterClassName, withSwitching);
    }

    private final AdapterView.OnItemClickListener searcherClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Logger.logD(Logger.getTag(), "currencySearcher.onItemClick");

                    currencySearcher.setText("");

                    Cursor searchedCurrency = (Cursor) parent.getItemAtPosition(position);
                    String searchedCharCode = searchedCurrency.getString(1);

                    Cursor cursor = listener.getCommonCursor();
                    int searchedCharCodePos = 0;
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        String cursorCurrentCharCode = cursor.getString(1);
                        if (searchedCharCode.equals(cursorCurrentCharCode)) {
                            searchedCharCodePos = cursor.getPosition();
                        }
                    }

                    if (listener instanceof MainActivity) {
                        SpinnerSelectionDialog fragmentDialog = new SpinnerSelectionDialog();
                        fragmentDialog.setFromSpinner(fromSpinner);
                        fragmentDialog.setToSpinner(toSpinner);
                        fragmentDialog.setSearchedCharCodeSpinnerPos(searchedCharCodePos);
                        fragmentDialog.show(getFragmentManager(), "list selection");

                    } else if (listener instanceof CurrencySwitchingActivity) {
                        switchingListView.setSelection(searchedCharCodePos);

                    } else if (listener instanceof DataSourceActivity) {
                        customSpinner.setSelection(searchedCharCodePos);
                    }
                }
            };
}
