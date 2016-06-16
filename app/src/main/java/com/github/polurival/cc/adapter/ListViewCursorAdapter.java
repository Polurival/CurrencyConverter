package com.github.polurival.cc.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.polurival.cc.R;

/**
 * Created by Polurival
 * on 16.06.2016.
 *
 * <p>See <a href="http://stackoverflow.com/questions/4567969/viewholder-pattern-correctly-implemented-in-custom-cursoradapter/36955882#36955882">source</a></p>
 */
public class ListViewCursorAdapter extends CursorAdapter {

    private Context appContext;

    public ListViewCursorAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
        this.appContext = context;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();

        int flagIconId = cursor.getInt(3);
        viewHolder.flagIcon.setImageResource(flagIconId);

        int currencyNameId = cursor.getInt(2);
        viewHolder.currencyName.setText(appContext.getString(currencyNameId));

        viewHolder.currencyCharCode.setText(cursor.getString(1));

        if (cursor.getInt(4) == 1) {
            viewHolder.turnOnOff.setChecked(true);
        } else {
            viewHolder.turnOnOff.setChecked(false);
        }
    }

    /** Cache of the children views for a list item. */
    private static class ViewHolder {
        final ImageView flagIcon;
        final TextView currencyName;
        final TextView currencyCharCode;
        final CheckBox turnOnOff;

        private ViewHolder(View view) {
            flagIcon = (ImageView) view.findViewById(R.id.lv_flag_icon);
            currencyName = (TextView) view.findViewById(R.id.lv_currency_name);
            currencyCharCode = (TextView) view.findViewById(R.id.lv_currency_char_code);
            turnOnOff = (CheckBox) view.findViewById(R.id.cb_currency_turn_on_off);
        }
    }
}
