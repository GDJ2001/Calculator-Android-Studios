package com.example.calculator;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final String KEY_EXPRESSION = "expression";
    private static final String KEY_RESULT = "result";
    private static final String KEY_RADIANS = "radians";

    private TextView txtExpression;
    private TextView txtResult;
    private final CalculatorEngine engine = new CalculatorEngine();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtExpression = (TextView) requireView(R.id.txtExpression);
        txtResult = (TextView) requireView(R.id.txtResult);

        boolean isLandscape =
                getResources().getConfiguration().orientation
                        == Configuration.ORIENTATION_LANDSCAPE;

        // Restore persisted state first so subsequent bindings (e.g. RAD/DEG
        // label and color) reflect the retained angle mode.
        if (savedInstanceState != null) {
            engine.setExpression(savedInstanceState.getString(KEY_EXPRESSION, ""));
            String result = savedInstanceState.getString(KEY_RESULT, "");
            engine.setRadians(savedInstanceState.getBoolean(KEY_RADIANS, true));
            txtExpression.setText(engine.getExpression().isEmpty() ? "0" : engine.getExpression());
            txtResult.setText(result);
        }

        // Standard keys exist in BOTH portrait and landscape layouts.
        bindStandardButtons();

        // Scientific keys (power, sin, cos, ...) only exist in the landscape
        // layout, so they are bound only when the device is in landscape.
        if (isLandscape) {
            bindScientificButtons();
        }

        if (savedInstanceState == null) {
            refreshDisplay();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(KEY_EXPRESSION, engine.getExpression());
        outState.putString(KEY_RESULT, txtResult.getText().toString());
        outState.putBoolean(KEY_RADIANS, engine.isRadians());
    }

    /** Bind every standard calculator key. These IDs are present in both layouts. */
    private void bindStandardButtons() {
        int[] numberButtons = {R.id.btn0, R.id.btn00, R.id.btn1, R.id.btn2, R.id.btn3,
                R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};

        for (int buttonId : numberButtons) {
            Button button = (Button) requireView(buttonId);
            button.setOnClickListener(v -> engine.inputDigit(button.getText().toString()));
        }

        bindOperator(R.id.btnAddition, '+');
        bindOperator(R.id.btnSubtraction, '-');
        bindOperator(R.id.btnMultiplication, '\u00D7');
        bindOperator(R.id.btnDivision, '\u00F7');

        requireView(R.id.btnAC).setOnClickListener(v -> {
            engine.clearAll();
            refreshDisplay();
        });

        // Backspace is a FrameLayout, not a Button.
        requireView(R.id.btnClear).setOnClickListener(v -> {
            engine.clearLast();
            refreshDisplay();
        });

        requireView(R.id.btnPercentage).setOnClickListener(v -> {
            engine.percentage();
            refreshDisplay();
        });

        requireView(R.id.btnDecimal).setOnClickListener(v -> {
            engine.inputDecimal();
            refreshDisplay();
        });

        requireView(R.id.btnEqual).setOnClickListener(v -> {
            String result = engine.evaluate();
            txtResult.setText(result);
            txtExpression.setText(engine.getExpression());
        });
    }

    /** Bind scientific keys. Called only in landscape, where they exist. */
    private void bindScientificButtons() {
        bindOperator(R.id.btnPower, '^');

        bindFunction(R.id.btnSin, "sin");
        bindFunction(R.id.btnCos, "cos");
        bindFunction(R.id.btnTan, "tan");
        bindFunction(R.id.btnAsin, "asin");
        bindFunction(R.id.btnAcos, "acos");
        bindFunction(R.id.btnAtan, "atan");
        bindFunction(R.id.btnLn, "ln");
        bindFunction(R.id.btnLog, "log");
        bindFunction(R.id.btnSqrt, "sqrt");

        // x^2 is shorthand for "value ^ 2".
        requireView(R.id.btnSquare).setOnClickListener(v -> {
            engine.inputOperator('^');
            engine.inputDigit("2");
            refreshDisplay();
        });

        bindConstant(R.id.btnPi, "\u03C0"); // π
        bindConstant(R.id.btnE, "e");

        requireView(R.id.btnLparen).setOnClickListener(v -> {
            engine.inputParenthesis('(');
            refreshDisplay();
        });
        requireView(R.id.btnRparen).setOnClickListener(v -> {
            engine.inputParenthesis(')');
            refreshDisplay();
        });
        requireView(R.id.btnParen).setOnClickListener(v -> {
            engine.inputParenthesis('(');
            refreshDisplay();
        });

        requireView(R.id.btnFact).setOnClickListener(v -> {
            engine.inputFactorial();
            refreshDisplay();
        });

        requireView(R.id.btnReciprocal).setOnClickListener(v -> {
            engine.inputReciprocal();
            refreshDisplay();
        });

        requireView(R.id.btnSign).setOnClickListener(v -> {
            engine.toggleSign();
            refreshDisplay();
        });

        // Angle mode toggle (RAD / DEG).
        Button btnRad = (Button) requireView(R.id.btnRad);
        btnRad.setOnClickListener(v -> {
            engine.toggleAngleMode();
            boolean radians = engine.isRadians();
            btnRad.setText(radians ? getString(R.string.rad) : getString(R.string.deg));
            btnRad.setTextColor(radians
                    ? getColor(R.color.profile_key_equal)
                    : getColor(R.color.btn_text_utility));
        });
    }

    private void bindOperator(int buttonId, char operator) {
        requireView(buttonId).setOnClickListener(v -> {
            engine.inputOperator(operator);
            refreshDisplay();
        });
    }

    private void bindFunction(int buttonId, String name) {
        requireView(buttonId).setOnClickListener(v -> {
            engine.inputFunction(name);
            refreshDisplay();
        });
    }

    private void bindConstant(int buttonId, String constant) {
        requireView(buttonId).setOnClickListener(v -> {
            engine.inputConstant(constant);
            refreshDisplay();
        });
    }

    /**
     * Resolve a view, throwing a clear, development-friendly message if it is
     * missing from the active layout. A standard calculator key must never be
     * absent, so this surfaces layout mistakes instead of hiding them.
     */
    private View requireView(int id) {
        View view = findViewById(id);
        if (view == null) {
            String name = getResources().getResourceEntryName(id);
            throw new IllegalStateException(
                    "Missing view @id/" + name + " in the active layout. "
                            + "Every standard button must exist in both res/layout "
                            + "and res/layout-land.");
        }
        return view;
    }

    private void refreshDisplay() {
        String expression = engine.getExpression();
        txtExpression.setText(expression.isEmpty() ? "0" : expression);
        txtResult.setText("");
    }
}
