package com.example.calculator;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtExpression;
    private TextView txtResult;
    private final CalculatorEngine engine = new CalculatorEngine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtExpression = findViewById(R.id.txtExpression);
        txtResult = findViewById(R.id.txtResult);

        int[] numberButtons = {R.id.btn0, R.id.btn00, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        for (int buttonId : numberButtons) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(v -> engine.inputDigit(button.getText().toString()));
        }

        bindOperator(R.id.btnAddition, '+');
        bindOperator(R.id.btnSubtraction, '-');
        bindOperator(R.id.btnMultiplication, '\u00D7');
        bindOperator(R.id.btnDivision, '\u00F7');

        findViewById(R.id.btnEqual).setOnClickListener(v -> {
            String result = engine.evaluate();
            txtResult.setText(result);
            txtExpression.setText(engine.getExpression());
        });

        findViewById(R.id.btnAC).setOnClickListener(v -> {
            engine.clearAll();
            refreshDisplay();
        });

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            engine.clearLast();
            refreshDisplay();
        });

        findViewById(R.id.btnPercentage).setOnClickListener(v -> {
            engine.percentage();
            refreshDisplay();
        });

        findViewById(R.id.btnDecimal).setOnClickListener(v -> {
            engine.inputDecimal();
            refreshDisplay();
        });

        refreshDisplay();
    }

    private void bindOperator(int buttonId, char operator) {
        findViewById(buttonId).setOnClickListener(v -> {
            engine.inputOperator(operator);
            refreshDisplay();
        });
    }

    private void refreshDisplay() {
        txtExpression.setText(engine.getExpression());
        txtResult.setText("");
    }
}
