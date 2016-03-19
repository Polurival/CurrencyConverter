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

    final int MENU_RESET_ID = 1;
    final int MENU_QUIT_ID = 2;

    EditText etNum1;

    Button btnTRYtoRUB;
    Button btnTRYtoDOL;
    Button btnRUBtoTRY;
    Button btnDOLtoTRY;

    TextView tvResult;

    String oper1 = "";
    String oper2 = "";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // находим элементы
        etNum1 = (EditText) findViewById(R.id.etNum1);

        btnTRYtoRUB = (Button) findViewById(R.id.btnTRYtoRUB);
        btnTRYtoDOL = (Button) findViewById(R.id.btnTRYtoDOL);
        btnRUBtoTRY = (Button) findViewById(R.id.btnRUBtoTRY);
        btnDOLtoTRY = (Button) findViewById(R.id.btnDOLtoTRY);

        tvResult = (TextView) findViewById(R.id.tvResult);

        // прописываем обработчик
        btnTRYtoRUB.setOnClickListener(this);
        btnTRYtoDOL.setOnClickListener(this);
        btnRUBtoTRY.setOnClickListener(this);
        btnDOLtoTRY.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        float num1;
        float result = 0;

        // Проверяем поля на пустоту
        if (TextUtils.isEmpty(etNum1.getText().toString())) {
            return;
        }

        // читаем EditText и заполняем переменные числами
        num1 = Float.parseFloat(etNum1.getText().toString());

        // определяем нажатую кнопку и выполняем соответствующую операцию
        // в oper пишем операцию, потом будем использовать в выводе
        switch (v.getId()) {
            case R.id.btnTRYtoRUB:
                oper1 = "TRY";
                oper2 = "P";
                result = (float) (num1 * 19.68);
                result = RoundResult(result, 0);
                break;
            case R.id.btnTRYtoDOL:
                oper1 = "TRY";
                oper2 = "$";
                result = (float) (num1 * 0.38);
                result = RoundResult(result, 0);
                break;
            case R.id.btnRUBtoTRY:
                oper1 = "P";
                oper2 = "TRY";
                result = (float) (num1 * 0.051);
                result = RoundResult(result, 0);
                break;
            case R.id.btnDOLtoTRY:
                oper1 = "$";
                oper2 = "TRY";
                result = (float) (num1 * 2.66);
                result = RoundResult(result, 0);
                break;
            default:
                break;
        }

        // формируем строку вывода
        tvResult.setText(num1 + " " + oper1 + " = " + result + " " + oper2);

    }

        // создание меню
        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            menu.add(0, MENU_RESET_ID, 0, "Reset");
            menu.add(0, MENU_QUIT_ID, 0, "Quit");
            return super.onCreateOptionsMenu(menu);
        }

        // обработка нажатий на пункты меню
        @Override
        public boolean onOptionsItemSelected (MenuItem item){
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

    // округляем результат


    float RoundResult(float f, int precise) {
        precise = 10 ^ precise;
        f = f * precise;
        int i = Math.round(f);
        return (float) i / precise;
    }


}
