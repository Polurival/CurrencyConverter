package com.github.polurival.cc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.polurival.cc.R;

/**
 * Created by Polurival
 * on 16.06.2016.
 */
public class SpinnerCursorAdapter extends CursorAdapter {

    private Context appContext;

    public SpinnerCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.appContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.spinner_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int flagIconId = cursor.getInt(5);
        viewHolder.flagIcon.setImageResource(flagIconId);

        int currencyNameId = cursor.getInt(4);
        viewHolder.currencyName.setText(appContext.getString(currencyNameId));

        viewHolder.currencyCharCode.setText(cursor.getString(1));
    }

    private static class ViewHolder {
        final ImageView flagIcon;
        final TextView currencyName;
        final TextView currencyCharCode;

        private ViewHolder(View view) {
            flagIcon = (ImageView) view.findViewById(R.id.spinner_flag_icon);
            currencyName = (TextView) view.findViewById(R.id.spinner_currency_name);
            currencyCharCode = (TextView) view.findViewById(R.id.spinner_currency_char_code);
        }
    }
}
