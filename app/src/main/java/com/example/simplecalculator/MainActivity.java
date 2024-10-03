package com.example.simplecalculator;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tvExpresson, tvResult;
    MaterialButton btnC, btnOpenBrackets, btnCloseBrackets, btnDivide, btn7, btn8, btn9, btnMultiply, btn4, btn5, btn6, btnSubtract, btn1, btn2, btn3, btnAddition, btn0, btnDot, btnEqual, btnAC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvResult = findViewById(R.id.tvResult);
        tvExpresson = findViewById(R.id.tvExpresson);

       initButton(btn0, R.id.btn0);
       initButton(btn1, R.id.btn1);
       initButton(btn2, R.id.btn2);
       initButton(btn3, R.id.btn3);
       initButton(btn4, R.id.btn4);
       initButton(btn5, R.id.btn5);
       initButton(btn6, R.id.btn6);
       initButton(btn7, R.id.btn7);
       initButton(btn8, R.id.btn8);
       initButton(btn9, R.id.btn9);
       initButton(btnDot, R.id.btnDot);
       initButton(btnEqual, R.id.btnEqual);
       initButton(btnAddition, R.id.btnAddition);
       initButton(btnSubtract, R.id.btnSubtract);
       initButton(btnMultiply, R.id.btnMultiply);
       initButton(btnDivide, R.id.btnDivide);
       initButton(btnC, R.id.btnC);
       initButton(btnAC, R.id.btnAC);
       initButton(btnOpenBrackets, R.id.btnOpenBrackets);
       initButton(btnCloseBrackets, R.id.btnCloseBrackets);



    }
    void initButton(MaterialButton button, int id) {
        button = findViewById(id);
        button.setOnClickListener(this::onClick);
    }
    @Override
    public void onClick(View v) {
        MaterialButton button = (MaterialButton) v;
        String btnText = button.getText().toString();
        String data = tvExpresson.getText().toString();

        // Если "AC", сбрасываем ввод и результат на "0"
        if (btnText.equals("AC")) {
            tvExpresson.setText("0");
            tvResult.setText("0");
            return;
        }

        // Если "C", удаляем последний символ из ввода
        if (btnText.equals("C")) {
            if (data.length() != 0) {
                data = data.substring(0, data.length() - 1);
                tvExpresson.setText(data);
            }
            // Если после удаления выражение пустое, устанавливаем "0"
            if (data.isEmpty()) {
                tvExpresson.setText("0");
            }
            return;
        }

        // Если нажата кнопка "=", показываем результат
        if (btnText.equals("=")) {
            tvExpresson.setText(tvResult.getText());
            return;
        }

        // Проверка на добавление оператора умножения перед открывающей скобкой
        if (btnText.equals("(") && !data.isEmpty() && Character.isDigit(data.charAt(data.length() - 1))) {
            data += "*";  // Добавляем оператор умножения перед открывающей скобкой
        }

        // Ограничение повторения операторов (+, -, *, /)
        if ("+-*/.".contains(btnText)) {
            // Если последний символ оператор, заменяем его
            if (!data.isEmpty() && "+-*/.".contains(data.substring(data.length() - 1))) {
                data = data.substring(0, data.length() - 1); // Удаляем последний оператор
            }
        }

        // Запрет на двойные точки
        if (btnText.equals(".") && data.contains(".")) {
            return;
        }

        // Запрет закрывающей скобки, если нет открывающей
        int openBrackets = data.length() - data.replace("(", "").length();
        int closeBrackets = data.length() - data.replace(")", "").length();
        if (btnText.equals(")") && closeBrackets >= openBrackets) {
            return;
        }

        // Замена нуля на цифру, если ноль стоит первым
        if (data.equals("0") && !".".equals(btnText)) {
            data = "";
        }

        // Добавляем символ к выражению
        data += btnText;
        tvExpresson.setText(data);

        // Вычисляем результат выражения
        String finalResult = evaluateExpression(data);
        if (!finalResult.equals("Error")) {
            tvResult.setText(finalResult);
        }

        Log.i("result", finalResult);
    }

    private String evaluateExpression(String expression) {
        // Создаем контекст Rhino
        Context rhino = Context.enter();
        // Устанавливаем версию JavaScript, которую будем использовать (по умолчанию актуальная)
        rhino.setOptimizationLevel(-1);
        // Без оптимизации для мобильных устройств
        try {
            // Создаем скриптовый объект Rhino
            Scriptable scope = rhino.initStandardObjects();
            // Выполняем выражение JavaScript
            String result = rhino.evaluateString(scope, expression, "JavaScript", 1, null).toString();
            // Приводим результат к числу и возвращаем его

            DecimalFormat decimalFormat = new DecimalFormat("#.###");

            return decimalFormat.format(Double.parseDouble(result));
        }
        catch (Exception e){
            return "Error";
        }
        finally {
            // Выход из контекста Rhino, освобождаем ресурсы
            Context.exit();
        }
    }
}

