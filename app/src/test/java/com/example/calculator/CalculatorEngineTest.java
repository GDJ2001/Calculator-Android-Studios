package com.example.calculator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class CalculatorEngineTest {

    @Test
    public void addition() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("2");
        engine.inputOperator('+');
        engine.inputDigit("3");
        assertEquals("5", engine.evaluate());
    }

    @Test
    public void subtraction() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("9");
        engine.inputOperator('-');
        engine.inputDigit("4");
        assertEquals("5", engine.evaluate());
    }

    @Test
    public void multiplication() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("6");
        engine.inputOperator('×');
        engine.inputDigit("7");
        assertEquals("42", engine.evaluate());
    }

    @Test
    public void division() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("8");
        engine.inputOperator('÷');
        engine.inputDigit("2");
        assertEquals("4", engine.evaluate());
    }

    @Test
    public void operatorPrecedence() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("2");
        engine.inputOperator('+');
        engine.inputDigit("3");
        engine.inputOperator('×');
        engine.inputDigit("4");
        assertEquals("14", engine.evaluate());
    }

    @Test
    public void divisionByZeroReturnsError() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("5");
        engine.inputOperator('÷');
        engine.inputDigit("0");
        assertEquals("Error", engine.evaluate());
    }

    @Test
    public void decimalInNumber() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("1");
        engine.inputDecimal();
        engine.inputDigit("5");
        engine.inputOperator('+');
        engine.inputDigit("2");
        engine.inputDecimal();
        engine.inputDigit("5");
        assertEquals("4", engine.evaluate());
    }

    @Test
    public void decimalAfterOperator() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("1");
        engine.inputOperator('+');
        engine.inputDecimal();
        engine.inputDigit("5");
        assertEquals("1.5", engine.evaluate());
    }

    @Test
    public void percentageOfCurrentNumber() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("5");
        engine.inputDigit("0");
        engine.percentage();
        assertEquals("0.5", engine.getExpression());
    }

    @Test
    public void percentageThenAdd() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("5");
        engine.inputDigit("0");
        engine.percentage();
        engine.inputOperator('+');
        engine.inputDigit("1");
        engine.inputDigit("0");
        assertEquals("10.5", engine.evaluate());
    }

    @Test
    public void clearLastRemovesOperatorFlag() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("5");
        engine.inputOperator('+');
        engine.clearLast();
        engine.inputOperator('+');
        engine.inputDigit("3");
        assertEquals("8", engine.evaluate());
    }

    @Test
    public void backspaceDigit() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("1");
        engine.inputDigit("2");
        engine.inputDigit("3");
        engine.clearLast();
        assertEquals("12", engine.getExpression());
    }

    @Test
    public void clearAllResets() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("9");
        engine.inputOperator('+');
        engine.clearAll();
        assertEquals("", engine.getExpression());
    }

    @Test
    public void chainedCalculationAfterResult() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("2");
        engine.inputOperator('+');
        engine.inputDigit("3");
        engine.evaluate();
        engine.inputOperator('×');
        engine.inputDigit("4");
        assertEquals("20", engine.evaluate());
    }

    @Test
    public void secondOperatorReplacesTrailingOne() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("2");
        engine.inputOperator('+');
        engine.inputOperator('×');
        engine.inputDigit("3");
        assertEquals("6", engine.evaluate());
    }

    @Test
    public void powerOperator() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("2");
        engine.inputOperator('^');
        engine.inputDigit("10");
        assertEquals("1024", engine.evaluate());
    }

    @Test
    public void parenthesesChangePrecedence() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputParenthesis('(');
        engine.inputDigit("2");
        engine.inputOperator('+');
        engine.inputDigit("3");
        engine.inputParenthesis(')');
        engine.inputOperator('×');
        engine.inputDigit("4");
        assertEquals("20", engine.evaluate());
    }

    @Test
    public void factorial() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("5");
        engine.inputFactorial();
        assertEquals("120", engine.evaluate());
    }

    @Test
    public void squareRoot() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputFunction("sqrt");
        engine.inputDigit("16");
        engine.inputParenthesis(')');
        assertEquals("4", engine.evaluate());
    }

    @Test
    public void sineOfZero() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputFunction("sin");
        engine.inputDigit("0");
        engine.inputParenthesis(')');
        assertEquals("0", engine.evaluate());
    }

    @Test
    public void sineOfNinetyDegrees() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.toggleAngleMode(); // switch to degrees
        engine.inputFunction("sin");
        engine.inputDigit("90");
        engine.inputParenthesis(')');
        assertEquals("1", engine.evaluate());
    }

    @Test
    public void piConstant() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputConstant("π");
        assertEquals("3.14159265359", engine.evaluate());
    }

    @Test
    public void naturalLogOfE() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputFunction("ln");
        engine.inputConstant("e");
        engine.inputParenthesis(')');
        assertEquals("1", engine.evaluate());
    }

    @Test
    public void divisionByZeroStillErrors() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("1");
        engine.inputOperator('÷');
        engine.inputDigit("0");
        assertEquals("Error", engine.evaluate());
    }

    @Test
    public void reciprocal() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("4");
        engine.inputReciprocal();
        assertEquals("0.25", engine.evaluate());
    }

    @Test
    public void toggleSignPositive() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("5");
        engine.toggleSign();
        assertEquals("-5", engine.getExpression());
    }

    @Test
    public void toggleSignNegative() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("5");
        engine.toggleSign();
        engine.toggleSign();
        assertEquals("5", engine.getExpression());
    }

    @Test
    public void reciprocalWorksInExpression() {
        CalculatorEngine engine = new CalculatorEngine();
        engine.inputDigit("2");
        engine.inputOperator('+');
        engine.inputDigit("4");
        engine.inputReciprocal();
        assertEquals("2.25", engine.evaluate());
    }}

