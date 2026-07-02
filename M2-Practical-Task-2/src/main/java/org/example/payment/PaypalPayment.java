package org.example.payment;

import org.example.model.PaymentResult;

public class PaypalPayment extends PaymentMethod {
    private final String email;

    public PaypalPayment(String email) {
        super("PayPal");
        this.email = email;
    }

    @Override
    public PaymentResult processPayment(double amount) {
        // a very simple email check: it must contain an "@"
        if (email == null || !email.contains("@")) {
            return new PaymentResult(false, "Invalid PayPal email address.");
        }
        return new PaymentResult(true, "Paid " + amount + " using PayPal (" + email + ")");
    }
}
