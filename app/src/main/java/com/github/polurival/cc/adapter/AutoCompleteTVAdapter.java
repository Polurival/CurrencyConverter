package com.github.polurival.cc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.github.polurival.cc.R;
import com.github.polurival.cc.model.db.DBHelper;
import com.github.polurival.cc.util.ResourcesLoader;

/**
 * <p>See <a href='http://hello-android.blogspot.ru/2011/06/using-autocompletetextview-with-sqlite.html'>source</a></p>
 */
public class AutoCompleteTVAdapter extends CursorAdapter {

    private String rateUpdaterClassName;
    private boolean withSwitching;

    private AutoCompleteTVAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public AutoCompleteTVAdapter(Context context, String rateUpdaterClassName,
                                 boolean withSwitching) {
        this(context, DBHelper.getSearchCursor("", rateUpdaterClassName, withSwitching));
        this.rateUpdaterClassName = rateUpdaterClassName;
        this.withSwitching = withSwitching;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view =
                LayoutInflater.from(context).inflate(R.layout.auto_complete_tv_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        viewHolder.currencyCharCode.setText(cursor.getString(1));

        int currencyNameId = ResourcesLoader.getResId(cursor.getString(2), R.string.class);
        viewHolder.currencyName.setText(currencyNameId);
    }

    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
            return getFilterQueryProvider().runQuery(constraint);
        }

        String args = "";
        if (constraint != null) {
            args = constraint.toString();
        }

        return DBHelper.getSearchCursor(args, rateUpdaterClassName, withSwitching);
    }

    private static class ViewHolder {
        final TextView currencyCharCode;
        final TextView currencyName;

        private ViewHolder(View view) {
            currencyCharCode = view.findViewById(R.id.autoCompleteTvCharCode);
            currencyName = view.findViewById(R.id.autoCompleteTvCurrencyName);
        }
    }
}
