package org.example.model;

public class NoDiscount extends Discount {
    public NoDiscount() {
        super("NONE");
    }
    @Override
    public double apply(double originalAmount) {
        // "no discount" means give back the same amount unchanged
        return originalAmount;
    }
}
