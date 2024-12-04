package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    TextView txt1, txt2;
    StringBuilder inputBuffer = new StringBuilder();
    StringBuilder calculationBuffer = new StringBuilder();
    boolean isOperatorAdded = false;
    boolean isResultDisplayed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);

        int[] numberButtons = {R.id.btn0, R.id.btn00, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        for (int buttonId : numberButtons) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button clickedButton = (Button) v;
                    String buttonText = clickedButton.getText().toString();
                    if (isResultDisplayed) {
                        clearAll();
                        isResultDisplayed = false;
                    }
                    appendInput(buttonText);
                }
            });
        }

        Button btnEqual = findViewById(R.id.btnEqual);
        btnEqual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                evaluateExpression();
            }
        });

        Button btnAC = findViewById(R.id.btnAC);
        btnAC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearAll();
            }
        });

        Button btnClear = findViewById(R.id.btnClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearLastInput();
            }
        });

        int[] operatorButtons = {R.id.btnAddition, R.id.btnSubtraction, R.id.btnMultiplication, R.id.btnDivision};

        for (int buttonId : operatorButtons) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Button clickedButton = (Button) v;
                    String buttonText = clickedButton.getText().toString();
                    if (!isOperatorAdded) {
                        if (inputBuffer.length() > 0 && !inputBuffer.toString().endsWith(".")) {
                            appendInput(buttonText);
                            isOperatorAdded = true;
                        } else {
                            Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
        }

        Button btnPercentage = findViewById(R.id.btnPercentage);
        btnPercentage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyPercentage();
            }
        });

        Button btnDecimal = findViewById(R.id.btnDecimal);
        btnDecimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appendDecimal();
            }
        });
    }

    private void appendInput(String input) {
        inputBuffer.append(input);
        calculationBuffer.append(input);
        txt1.setText(inputBuffer.toString());
    }

    private void evaluateExpression() {
        String expression = calculationBuffer.toString();
        if (!expression.isEmpty()) {
            try {
                double result = calculate(expression);
                txt2.setText(String.valueOf(result));
                inputBuffer.setLength(0);
                inputBuffer.append(result);
                isOperatorAdded = false;
                calculationBuffer.setLength(0);
                calculationBuffer.append(result);
                isResultDisplayed = true;
            } catch (ArithmeticException e) {
                txt2.setText("Error: Division by zero");
            } catch (Exception e) {
                txt2.setText("Error: Invalid expression");
            }
        }
    }

    private double calculate(String expression) {

        String[] operands = expression.split("[+\\-×÷]");
        String[] operators = expression.split("\\d+\\.?\\d*");


        double result = Double.parseDouble(operands[0]);
        for (int i = 1; i < operands.length; i++) {
            double operand = Double.parseDouble(operands[i]);
            String operator = operators[i];
            switch (operator) {
                case "+":
                    result += operand;
                    break;
                case "-":
                    result -= operand;
                    break;
                case "×":
                    result *= operand;
                    break;
                case "÷":
                    if (operand == 0) {
                        throw new ArithmeticException();
                    }
                    result /= operand;
                    break;
            }
        }
        return result;
    }

    private void clearAll() {
        txt1.setText("");
        txt2.setText("");
        inputBuffer.setLength(0);
        calculationBuffer.setLength(0);
        isOperatorAdded = false;
        isResultDisplayed = false;
    }

    private void clearLastInput() {
        if (inputBuffer.length() > 0) {
            inputBuffer.deleteCharAt(inputBuffer.length() - 1);
            calculationBuffer.deleteCharAt(calculationBuffer.length() - 1);
            txt1.setText(inputBuffer.toString());
        }
    }

    private void applyPercentage() {
        if (inputBuffer.length() > 0 && !isOperatorAdded) {
            double currentValue = Double.parseDouble(inputBuffer.toString());
            double percentageValue = currentValue * 0.01;
            txt1.setText(String.valueOf(percentageValue));
            inputBuffer.setLength(0);
            inputBuffer.append(percentageValue);
            isResultDisplayed = false;
        }
    }

    private void appendDecimal() {
        if (!inputBuffer.toString().contains(".")) {
            appendInput(".");
        }
    }
}
