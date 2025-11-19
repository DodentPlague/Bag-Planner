package tech.allydoes;

import java.security.InvalidParameterException;

/*
 * Very basic fixed point implementation
 * 
 * We use this instead of the builtin floating-point types (float/double)
 * because floating-point variables will have rounding errors
 * 
 * As floating point values move further away from 0, the amount of values
 * that they can represent decreases. To prevent this problem, we use this
 * custom FixedPoint class that restricts the input to 2 decimal digits
 * 
 * See https://0.30000000000000004.com/
 */
public class FixedPoint {
    private int integer;
    private int decimal;

    public FixedPoint(int integer, int decimal) {
        this.integer = integer;
        this.decimal = decimal;
    }

    public int getInteger() {
        return this.integer;
    }

    public int getDecimal() {
        return this.decimal;
    }

    public void setInteger(int newInteger) {
        this.integer = newInteger;
    }

    public void setDecimal(int newDecimal) {
        if (newDecimal >= 100) {
            throw new InvalidParameterException("Function called with cent value greater than or equal to 100");
        }

        this.decimal = newDecimal;
    }

    public FixedPoint add(FixedPoint other) {
        int integer = this.integer;
        int decimal = this.decimal;

        decimal += (other.integer * 100) + other.decimal;
        
        integer += decimal / 100;
        decimal = decimal % 100;

        return new FixedPoint(integer, decimal);
    }

    public FixedPoint subtract(FixedPoint other) {
        int integer = this.integer;
        int decimal = this.decimal;

        int subtractedDecimals = decimal - other.decimal;

        if (subtractedDecimals < 0) {
            integer -= 1;
        }
        integer -= other.integer;

        decimal = 100 - Math.abs(subtractedDecimals % 100);
        if (decimal == 100) {
            decimal = 0;
        }

        return new FixedPoint(integer, decimal);
    }
}
