package com.example.calculator;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Pure, UI-independent calculator logic.
 *
 * <p>Handles digit/operator/decimal input, backspace, percentage and evaluation
 * with correct operator precedence (× and ÷ before + and -). The class keeps no
 * Android dependencies so it can be unit tested on the JVM.
 */
public class CalculatorEngine {

    private static final char OP_ADD = '+';
    private static final char OP_SUB = '-';
    private static final char OP_MUL = '\u00D7'; // ×
    private static final char OP_DIV = '\u00F7'; // ÷

    private final StringBuilder expression = new StringBuilder();
    private boolean operatorAdded = false;
    private boolean resultDisplayed = false;
    private boolean hasError = false;

    /** Append a digit (or "00") to the expression. */
    public void inputDigit(String digit) {
        if (resultDisplayed || hasError) {
            clear();
        }
        expression.append(digit);
    }

    /** Append an operator, but only when it is valid to do so. */
    public void inputOperator(char operator) {
        if (hasError) {
            clear();
        }
        // After a valid result, keep the result and continue the calculation.
        resultDisplayed = false;

        if (expression.length() == 0) {
            // An expression cannot start with an operator.
            return;
        }
        if (endsWithOperator()) {
            // Replace the trailing operator (e.g. 2 + 3 × -> 2 + 3 ×).
            expression.setCharAt(expression.length() - 1, operator);
            operatorAdded = true;
        } else if (expression.charAt(expression.length() - 1) != '.') {
            expression.append(operator);
            operatorAdded = true;
        }
    }

    /** Insert a decimal point in the current number if it does not already have one. */
    public void inputDecimal() {
        if (resultDisplayed || hasError) {
            clear();
        }
        String current = currentNumber();
        if (!current.contains(".")) {
            if (expression.length() == 0 || endsWithOperator()) {
                expression.append('0');
            }
            expression.append('.');
            operatorAdded = false;
        }
    }

    /** Remove the last character, keeping internal flags consistent. */
    public void clearLast() {
        if (expression.length() > 0) {
            char removed = expression.charAt(expression.length() - 1);
            expression.deleteCharAt(expression.length() - 1);
            if (isOperator(removed)) {
                operatorAdded = false;
            }
        }
        hasError = false;
        resultDisplayed = false;
    }

    /** Reset the calculator completely. */
    public void clearAll() {
        clear();
    }

    /**
     * Convert the current number in the expression to its percentage value
     * (divide by 100) and replace it in place.
     */
    public void percentage() {
        if (hasError) {
            clear();
        }
        resultDisplayed = false;
        String current = currentNumber();
        if (!current.isEmpty()) {
            double value = Double.parseDouble(current);
            double percentage = value / 100.0;
            int start = expression.length() - current.length();
            expression.replace(start, expression.length(), formatNumber(percentage));
            operatorAdded = false;
        }
    }

    /**
     * Evaluate the current expression.
     *
     * @return the formatted result, or "Error" if the expression is invalid
     *         or divides by zero.
     */
    public String evaluate() {
        if (expression.length() == 0) {
            return "";
        }
        // Drop a trailing operator so "5+3=" works like "5+3".
        while (endsWithOperator()) {
            expression.deleteCharAt(expression.length() - 1);
            operatorAdded = false;
        }
        if (expression.length() == 0) {
            return "";
        }
        try {
            double result = calculate(expression.toString());
            String formatted = formatNumber(result);
            expression.setLength(0);
            expression.append(formatted);
            operatorAdded = false;
            resultDisplayed = true;
            hasError = false;
            return formatted;
        } catch (ArithmeticException e) {
            expression.setLength(0);
            resultDisplayed = true;
            hasError = true;
            return "Error";
        } catch (Exception e) {
            expression.setLength(0);
            resultDisplayed = true;
            hasError = true;
            return "Error";
        }
    }

    /** The expression currently being built (or the last result). */
    public String getExpression() {
        return expression.toString();
    }

    private void clear() {
        expression.setLength(0);
        operatorAdded = false;
        resultDisplayed = false;
        hasError = false;
    }

    private boolean endsWithOperator() {
        if (expression.length() == 0) {
            return false;
        }
        return isOperator(expression.charAt(expression.length() - 1));
    }

    private boolean isOperator(char c) {
        return c == OP_ADD || c == OP_SUB || c == OP_MUL || c == OP_DIV;
    }

    private boolean isOperator(String token) {
        return token.length() == 1 && isOperator(token.charAt(0));
    }

    /** The number currently being entered (text after the last operator). */
    private String currentNumber() {
        int lastOp = -1;
        for (int i = expression.length() - 1; i >= 0; i--) {
            if (isOperator(expression.charAt(i))) {
                lastOp = i;
                break;
            }
        }
        return expression.substring(lastOp + 1);
    }

    private List<String> tokenize(String expr) {
        List<String> tokens = new ArrayList<>();
        StringBuilder number = new StringBuilder();
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (isOperator(c)) {
                if (number.length() > 0) {
                    tokens.add(number.toString());
                    number.setLength(0);
                }
                tokens.add(String.valueOf(c));
            } else {
                number.append(c);
            }
        }
        if (number.length() > 0) {
            tokens.add(number.toString());
        }
        return tokens;
    }

    /** Evaluate an expression respecting operator precedence (× ÷ before + -). */
    private double calculate(String expr) {
        List<String> tokens = tokenize(expr);
        if (tokens.isEmpty()) {
            return 0.0;
        }
        Deque<Double> values = new ArrayDeque<>();
        Deque<String> ops = new ArrayDeque<>();

        for (String token : tokens) {
            if (isOperator(token)) {
                while (!ops.isEmpty() && precedence(ops.peek()) >= precedence(token)) {
                    applyTop(values, ops);
                }
                ops.push(token);
            } else {
                values.push(Double.parseDouble(token));
            }
        }
        while (!ops.isEmpty()) {
            applyTop(values, ops);
        }
        return values.pop();
    }

    private int precedence(String op) {
        if (op.equals(String.valueOf(OP_MUL)) || op.equals(String.valueOf(OP_DIV))) {
            return 2;
        }
        return 1; // + and -
    }

    private void applyTop(Deque<Double> values, Deque<String> ops) {
        String op = ops.pop();
        double b = values.pop();
        double a = values.pop();
        switch (op.charAt(0)) {
            case OP_ADD:
                values.push(a + b);
                break;
            case OP_SUB:
                values.push(a - b);
                break;
            case OP_MUL:
                values.push(a * b);
                break;
            case OP_DIV:
                if (b == 0.0) {
                    throw new ArithmeticException("Division by zero");
                }
                values.push(a / b);
                break;
            default:
                throw new IllegalStateException("Unknown operator: " + op);
        }
    }

    private String formatNumber(double value) {
        double rounded = Math.round(value * 1e10) / 1e10;
        if (rounded == (long) rounded) {
            return String.valueOf((long) rounded);
        }
        return String.valueOf(rounded);
    }
}

