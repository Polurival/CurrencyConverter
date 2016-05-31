package com.github.polurival.cc;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        assert getActionBar() != null;
        getActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner sourceSpinner = (Spinner) findViewById(R.id.source_spinner);
        ArrayAdapter<String> sourceAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.data_source_array));
        sourceSpinner.setAdapter(sourceAdapter);
    }
}
