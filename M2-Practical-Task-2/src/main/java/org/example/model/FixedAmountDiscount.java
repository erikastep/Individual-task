package org.example.model;

public class FixedAmountDiscount extends Discount {
    private final double amount;

    public FixedAmountDiscount(String code, double amount) {
        super(code);
        this.amount = amount;
    }

    @Override
    public double apply(double originalAmount) {
        // take money off, but never let the total go below 0
        return Math.max(0, originalAmount - amount);
    }
}
