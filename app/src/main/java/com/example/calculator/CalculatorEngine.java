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
    private static final char OP_POW = '^';      // ^ (power)

    private final StringBuilder expression = new StringBuilder();
    private boolean operatorAdded = false;
    private boolean resultDisplayed = false;
    private boolean hasError = false;
    private boolean radians = true;

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

    /** Append a scientific function call, e.g. "sin(" so an argument can follow. */
    public void inputFunction(String name) {
        if (resultDisplayed || hasError) {
            clear();
        }
        expression.append(name).append('(');
        operatorAdded = false;
    }

    /** Append a mathematical constant (π or e). */
    public void inputConstant(String constant) {
        if (resultDisplayed || hasError) {
            clear();
        }
        expression.append(constant);
        operatorAdded = false;
    }

    /** Append an opening or closing parenthesis. */
    public void inputParenthesis(char paren) {
        if (hasError) {
            clear();
        }
        resultDisplayed = false;
        expression.append(paren);
        operatorAdded = false;
    }

    /** Append a factorial operator (!) to the current value. */
    public void inputFactorial() {
        if (hasError) {
            clear();
        }
        resultDisplayed = false;
        expression.append('!');
        operatorAdded = false;
    }

    /**
     * Apply the reciprocal (1/x) function to the current number. Appends
     * "1÷(" + currentNumber + ")" so it evaluates with correct precedence.
     * Uses the engine's real division operator (÷), not "/".
     */
    public void inputReciprocal() {
        if (hasError) {
            clear();
        }
        resultDisplayed = false;
        String current = currentNumber();
        if (current.isEmpty()) {
            expression.append("1").append(OP_DIV).append("(");
        } else {
            int start = expression.length() - current.length();
            expression.replace(start, expression.length(), "1" + OP_DIV + "(" + current + ")");
        }
        operatorAdded = false;
    }

    /**
     * Negate the current number in place by inserting or removing a leading
     * unary '-' (e.g. "5" -> "-5", "-5" -> "5"). The leading minus is part of
     * the number, so it must be handled explicitly rather than via
     * currentNumber(), which treats '-' as an operator.
     */
    public void toggleSign() {
        if (hasError) {
            clear();
        }
        resultDisplayed = false;
        // Locate the start index of the current number, including a leading
        // unary minus when present. If the scan reaches the start of the
        // expression without hitting an operator/paren, the whole expression
        // is the current number (numStart stays 0).
        int numStart = 0;
        for (int i = expression.length() - 1; i >= 0; i--) {
            char c = expression.charAt(i);
            if (isOperator(c) || c == '(') {
                numStart = i + 1;
                boolean unary = (i == 0)
                        || isOperator(expression.charAt(i - 1))
                        || expression.charAt(i - 1) == '(';
                if (c == OP_SUB && unary) {
                    numStart = i;
                }
                break;
            }
        }
        if (numStart >= expression.length()) {
            return;
        }
        if (expression.charAt(numStart) == '-') {
            expression.deleteCharAt(numStart);
        } else {
            expression.insert(numStart, '-');
        }
        operatorAdded = false;
    }

    /** Toggle between radians and degrees for trigonometric functions. */
    public void toggleAngleMode() {
        radians = !radians;
    }

    /** Whether trigonometric functions use radians (true) or degrees (false). */
    public boolean isRadians() {
        return radians;
    }

    /** Restore the angle mode (used to survive configuration changes). */
    public void setRadians(boolean value) {
        radians = value;
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

    /**
     * Restore a previously persisted expression (used to survive configuration
     * changes such as rotation). Clears all transient flags so the restored
     * expression behaves exactly as if it had been typed.
     */
    public void setExpression(String expr) {
        expression.setLength(0);
        if (expr != null) {
            expression.append(expr);
        }
        operatorAdded = false;
        resultDisplayed = false;
        hasError = false;
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
        return c == OP_ADD || c == OP_SUB || c == OP_MUL || c == OP_DIV || c == OP_POW;
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
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                StringBuilder num = new StringBuilder();
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) {
                    num.append(expr.charAt(i++));
                }
                tokens.add(num.toString());
            } else if (c == 'π' || c == 'e') {
                tokens.add(String.valueOf(c));
                i++;
            } else if (Character.isLetter(c)) {
                StringBuilder name = new StringBuilder();
                while (i < expr.length() && Character.isLetter(expr.charAt(i))) {
                    name.append(expr.charAt(i++));
                }
                tokens.add(name.toString());
            } else {
                tokens.add(String.valueOf(c));
                i++;
            }
        }
        return tokens;
    }

    /**
     * Evaluate a full expression using the shunting-yard algorithm.
     * Supports + - × ÷ ^, parentheses, postfix factorial (!), unary minus,
     * functions (sin, cos, tan, asin, acos, atan, ln, log, sqrt, exp, abs)
     * and constants (π, e).
     */
    private double calculate(String expr) {
        List<String> tokens = tokenize(expr);
        if (tokens.isEmpty()) {
            return 0.0;
        }
        List<String> output = new ArrayList<>();
        Deque<String> ops = new ArrayDeque<>();

        for (int idx = 0; idx < tokens.size(); idx++) {
            String token = tokens.get(idx);
            if (isNumber(token) || isConstant(token)) {
                output.add(token);
            } else if (isFunction(token)) {
                ops.push(token);
            } else if (token.equals("(")) {
                ops.push(token);
            } else if (token.equals(")")) {
                while (!ops.isEmpty() && !ops.peek().equals("(")) {
                    output.add(ops.pop());
                }
                if (ops.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                ops.pop(); // remove "("
                if (!ops.isEmpty() && isFunction(ops.peek())) {
                    output.add(ops.pop());
                }
            } else if (token.equals("!")) {
                output.add(token); // postfix unary
            } else if (isOperator(token)) {
                // Detect unary minus / plus.
                boolean unary = token.equals(String.valueOf(OP_SUB)) || token.equals(String.valueOf(OP_ADD));
                if (unary) {
                    boolean prevIsOp = idx == 0 || isOperator(tokens.get(idx - 1))
                            || tokens.get(idx - 1).equals("(") || tokens.get(idx - 1).equals("!");
                    if (prevIsOp) {
                        output.add("0");
                    }
                }
                while (!ops.isEmpty() && !ops.peek().equals("(")
                        && precedence(ops.peek()) > precedence(token)) {
                    output.add(ops.pop());
                }
                ops.push(token);
            } else {
                throw new IllegalArgumentException("Unknown token: " + token);
            }
        }
        while (!ops.isEmpty()) {
            String op = ops.pop();
            if (op.equals("(") || op.equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(op);
        }
        return evalRpn(output);
    }

    private double evalRpn(List<String> rpn) {
        Deque<Double> stack = new ArrayDeque<>();
        for (String token : rpn) {
            if (isNumber(token)) {
                stack.push(Double.parseDouble(token));
            } else if (isConstant(token)) {
                stack.push(constantValue(token));
            } else if (token.equals("!")) {
                double a = stack.pop();
                stack.push(factorial(a));
            } else if (isFunction(token)) {
                double a = stack.pop();
                stack.push(applyFunction(token, a));
            } else if (isOperator(token)) {
                double b = stack.pop();
                double a = stack.pop();
                stack.push(applyBinary(token, a, b));
            } else {
                throw new IllegalArgumentException("Bad token: " + token);
            }
        }
        if (stack.size() != 1) {
            throw new IllegalArgumentException("Malformed expression");
        }
        return stack.pop();
    }

    private double applyBinary(String op, double a, double b) {
        switch (op.charAt(0)) {
            case OP_ADD:
                return a + b;
            case OP_SUB:
                return a - b;
            case OP_MUL:
                return a * b;
            case OP_DIV:
                if (b == 0.0) {
                    throw new ArithmeticException("Division by zero");
                }
                return a / b;
            case OP_POW:
                return Math.pow(a, b);
            default:
                throw new IllegalStateException("Unknown operator: " + op);
        }
    }

    private double applyFunction(String name, double x) {
        switch (name) {
            case "sin":
                return Math.sin(toRad(x));
            case "cos":
                return Math.cos(toRad(x));
            case "tan":
                return Math.tan(toRad(x));
            case "asin":
                return fromRad(Math.asin(clamp(x, -1, 1)));
            case "acos":
                return fromRad(Math.acos(clamp(x, -1, 1)));
            case "atan":
                return fromRad(Math.atan(x));
            case "ln":
                return Math.log(x);
            case "log":
                return Math.log10(x);
            case "sqrt":
                return Math.sqrt(x);
            case "exp":
                return Math.exp(x);
            case "abs":
                return Math.abs(x);
            default:
                throw new IllegalArgumentException("Unknown function: " + name);
        }
    }

    private double toRad(double x) {
        return radians ? x : Math.toRadians(x);
    }

    private double fromRad(double x) {
        return radians ? x : Math.toDegrees(x);
    }

    private double clamp(double x, double lo, double hi) {
        return Math.max(lo, Math.min(hi, x));
    }

    private double constantValue(String c) {
        if (c.equals("π")) {
            return Math.PI;
        }
        if (c.equals("e")) {
            return Math.E;
        }
        throw new IllegalArgumentException("Unknown constant: " + c);
    }

    private double factorial(double n) {
        if (n < 0 || n != Math.floor(n)) {
            throw new ArithmeticException("Factorial of non-integer/negative");
        }
        double result = 1.0;
        for (int i = 2; i <= (int) n; i++) {
            result *= i;
        }
        return result;
    }

    private boolean isNumber(String token) {
        if (token.isEmpty()) {
            return false;
        }
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isConstant(String token) {
        return token.equals("π") || token.equals("e");
    }

    private boolean isFunction(String token) {
        switch (token) {
            case "sin":
            case "cos":
            case "tan":
            case "asin":
            case "acos":
            case "atan":
            case "ln":
            case "log":
            case "sqrt":
            case "exp":
            case "abs":
                return true;
            default:
                return false;
        }
    }

    private int precedence(String op) {
        if (op.equals("!")) {
            return 4;
        }
        if (op.equals("^")) {
            return 3; // right associative, handled by strict >
        }
        if (op.equals(String.valueOf(OP_MUL)) || op.equals(String.valueOf(OP_DIV))) {
            return 2;
        }
        return 1; // + and -
    }

    private String formatNumber(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            throw new ArithmeticException("Undefined result");
        }
        // Snap values that are extremely close to an integer to that integer
        // (e.g. ln(e) = 0.9999999999999999, sin(0) = ~0).
        double rounded = Math.round(value * 1e12) / 1e12;
        if (Math.abs(rounded - Math.round(rounded)) < 1e-9) {
            return String.valueOf((long) Math.round(rounded));
        }
        // Trim trailing zeros from the decimal representation.
        String text = String.valueOf(rounded);
        if (text.contains(".") && !text.contains("E") && !text.contains("e")) {
            text = text.replaceAll("0*$", "").replaceAll("\\.$", "");
        }
        return text;
    }
}

