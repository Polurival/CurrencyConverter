package com.polurival.cuco;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.polurival.cuco.strategies.CBRateUpdater;
import com.polurival.cuco.strategies.Valute;
import com.polurival.cuco.strategies.ValuteCharCode;

import java.util.EnumMap;

public class MainActivity extends Activity implements OnClickListener {

    private CBRateUpdater rateUpdater;
    private EnumMap<ValuteCharCode, Valute> valuteMap;

    private static final double RUB_TO_IRR_RATE = 438.8;
    private static final double USD_TO_IRR_RATE = 30250;

    private final int MENU_RESET_ID = 1;
    private final int MENU_QUIT_ID = 2;

    private Button btnIRRtoRUB;
    private Button btnIRRtoUSD;
    private Button btnRUBtoIRR;
    private Button btnUSDtoIRR;
    private Button btnUpdateRate;

    private EditText etNum1;

    private Spinner fromSpinner;
    private Spinner toSpinner;
    private ArrayAdapter<String> adapter;

    private TextView tvResult;

    private String oper1 = "";
    private String oper2 = "";

    public void setValuteMap(EnumMap<ValuteCharCode, Valute> valuteMap) {
        this.valuteMap = valuteMap;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        btnIRRtoRUB = (Button) findViewById(R.id.btnIRRtoRUB);
        btnIRRtoUSD = (Button) findViewById(R.id.btnIRRtoUSD);
        btnRUBtoIRR = (Button) findViewById(R.id.btnRUBtoIRR);
        btnUSDtoIRR = (Button) findViewById(R.id.btnUSDtoIRR);
        btnUpdateRate = (Button) findViewById(R.id.btnUpdateRate);

        etNum1 = (EditText) findViewById(R.id.etNum1);

        tvResult = (TextView) findViewById(R.id.tvResult);

        btnIRRtoRUB.setOnClickListener(this);
        btnIRRtoUSD.setOnClickListener(this);
        btnRUBtoIRR.setOnClickListener(this);
        btnUSDtoIRR.setOnClickListener(this);

        rateUpdater = new CBRateUpdater(this);
        rateUpdater.execute();

        initSpinners();
    }

    @Override
    public void onClick(View v) {
        double result = 0;
        double inputedAmountOfMoney = getInputedAmountOfMoney();

        switch (v.getId()) {
            case R.id.btnIRRtoRUB:
                oper1 = "IRR";
                oper2 = "P";
                result = inputedAmountOfMoney / RUB_TO_IRR_RATE;
                break;
            case R.id.btnIRRtoUSD:
                oper1 = "IRR";
                oper2 = "$";
                result = inputedAmountOfMoney / USD_TO_IRR_RATE;
                break;
            case R.id.btnRUBtoIRR:
                oper1 = "P";
                oper2 = "IRR";
                result = inputedAmountOfMoney * RUB_TO_IRR_RATE;
                break;
            case R.id.btnUSDtoIRR:
                oper1 = "$";
                oper2 = "IRR";
                result = inputedAmountOfMoney * USD_TO_IRR_RATE;
                break;
            default:
                break;
        }
        String text = String.format("%.2f %s = %.2f %s", inputedAmountOfMoney, oper1, result, oper2);
        setTvResultText(text);
    }

    public void showUsdToRubRate(View v) {
        if (valuteMap != null) {
            String text = String.format("%s %s = %s %s",
                    valuteMap.get(ValuteCharCode.RUB).getValuteToRubRate(),
                    ValuteCharCode.USD.getName(),
                    valuteMap.get(ValuteCharCode.USD).getValuteToRubRate(),
                    ValuteCharCode.RUB.getName());
            setTvResultText(text);
        }
    }

    public void swapFromTo(View v) {
        int fromSpinnerSelectedItemPos = fromSpinner.getSelectedItemPosition();
        fromSpinner.setSelection(toSpinner.getSelectedItemPosition());
        toSpinner.setSelection(fromSpinnerSelectedItemPos);
    }

    public void transfer(View v) {
        if (valuteMap != null) {
            String fromCharCode = (String) fromSpinner.getSelectedItem();
            String toCharCode = (String) toSpinner.getSelectedItem();
            Valute valuteFrom = null;
            Valute valuteTo = null;
            ValuteCharCode codeFrom = null;
            ValuteCharCode codeTo = null;
            for (ValuteCharCode code : ValuteCharCode.values()) {
                if (code.getName().equals(fromCharCode)) {
                    valuteFrom = valuteMap.get(code);
                    codeFrom = code;
                }
                if (code.getName().equals(toCharCode)) {
                    valuteTo = valuteMap.get(code);
                    codeTo = code;
                }
                if (valuteFrom != null && valuteTo != null) {
                    break;
                }
            }

            assert valuteFrom != null;
            double valuteFromNominal = Double.valueOf(valuteFrom.getNominal());
            double valuteFromToRubRate = Double.valueOf(valuteFrom.getValuteToRubRate());

            assert valuteTo != null;
            double valuteToNominal = Integer.valueOf(valuteTo.getNominal());
            double valuteToToRubRate = Double.valueOf(valuteTo.getValuteToRubRate());

            double inputedAmountOfMoney = getInputedAmountOfMoney();
            double result = inputedAmountOfMoney *
                    (valuteFromToRubRate / valuteToToRubRate) *
                    (valuteToNominal / valuteFromNominal);


            String text = String.format("%.2f %s = %.2f %s",
                    inputedAmountOfMoney,
                    codeFrom.getName(),
                    result,
                    codeTo.getName());
            setTvResultText(text);
        }
    }

    private double getInputedAmountOfMoney() {
        if (TextUtils.isEmpty(etNum1.getText().toString())) {
            return 1d;
        }
        return (double) Float.parseFloat(etNum1.getText().toString());
    }

    private String[] getFilledValuteArray() {
        int len = ValuteCharCode.values().length;
        String[] str = new String[len];
        int i = 0;
        for (ValuteCharCode code : ValuteCharCode.values()) {
            str[i] = code.getName();
            i++;
        }
        return str;
    }

    private void setTvResultText(String text) {
        tvResult.setText(text);
        tvResult.setVisibility(View.VISIBLE);
    }

    private void initSpinners() {
        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                getFilledValuteArray());
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        fromSpinner.setAdapter(adapter);
        fromSpinner.setSelection(0);

        toSpinner = (Spinner) findViewById(R.id.toSpinner);
        toSpinner.setAdapter(adapter);
        toSpinner.setSelection(0);
    }

    // создание меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_RESET_ID, 0, "Reset");
        menu.add(0, MENU_QUIT_ID, 0, "Quit");
        return super.onCreateOptionsMenu(menu);
    }

    // обработка нажатий на пункты меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
            case MENU_RESET_ID:
                // очищаем поля
                etNum1.setText("");
                tvResult.setVisibility(View.INVISIBLE);
                break;
            case MENU_QUIT_ID:
                // выход из приложения
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
