package com.polurival.cuco;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener {

    private static final double RUB_TO_IRR_RATE = 438.8;
    private static final double USD_TO_IRR_RATE = 30250;

    private final int MENU_RESET_ID = 1;
    private final int MENU_QUIT_ID = 2;

    private EditText etNum1;

    private Button btnIRRtoRUB;
    private Button btnIRRtoUSD;
    private Button btnRUBtoIRR;
    private Button btnUSDtoIRR;
    private Button btnUpdateRate;

    private TextView tvResult;

    private String oper1 = "";
    private String oper2 = "";

    public TextView getTvResult() {
        return tvResult;
    }

    public void setTvResult(TextView tvResult) {
        this.tvResult = tvResult;
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // находим элементы
        etNum1 = (EditText) findViewById(R.id.etNum1);

        btnIRRtoRUB = (Button) findViewById(R.id.btnIRRtoRUB);
        btnIRRtoUSD = (Button) findViewById(R.id.btnIRRtoUSD);
        btnRUBtoIRR = (Button) findViewById(R.id.btnRUBtoIRR);
        btnUSDtoIRR = (Button) findViewById(R.id.btnUSDtoIRR);
        btnUpdateRate = (Button) findViewById(R.id.btnUpdateRate);

        tvResult = (TextView) findViewById(R.id.tvResult);

        // прописываем обработчик
        btnIRRtoRUB.setOnClickListener(this);
        btnIRRtoUSD.setOnClickListener(this);
        btnRUBtoIRR.setOnClickListener(this);
        btnUSDtoIRR.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        double result = 0;

        // Проверяем поля на пустоту
        if (TextUtils.isEmpty(etNum1.getText().toString())) {
            return;
        }

        // читаем EditText и заполняем переменные числами
        double inputedAmountOfMoney = Float.parseFloat(etNum1.getText().toString());

        // определяем нажатую кнопку и выполняем соответствующую операцию
        // в oper пишем операцию, потом будем использовать в выводе
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

        // формируем строку вывода
        tvResult.setText(String.format("%.1f %s = %.1f %s", inputedAmountOfMoney, oper1, result, oper2));
        tvResult.setVisibility(View.VISIBLE);
    }

    public void updateRate(View v) {
        RateUpdater rateUpdater = new RateUpdater(this);
        rateUpdater.execute();
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
                tvResult.setText("");
                break;
            case MENU_QUIT_ID:
                // выход из приложения
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
