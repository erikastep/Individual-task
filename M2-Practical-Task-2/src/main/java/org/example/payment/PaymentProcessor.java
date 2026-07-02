package org.example.payment;

import org.example.model.Order;
import org.example.model.PaymentResult;

public class PaymentProcessor {
    public PaymentResult process(Order order, PaymentMethod paymentMethod){
        if (order.isPaid()) {
            return new PaymentResult(false, "This order is already paid.");
        }
        if (order.getItems().isEmpty()) {
            return new PaymentResult(false, "Cannot pay an empty order. Add items first.");
        }

        PaymentResult result = paymentMethod.pay(order.calculateTotal());

        if(result.isSuccessful()){
            order.markAsPaid();
        }

        return result;
    }
}
