package org.example.model;

import org.example.config.AppConfig;

import java.util.ArrayList;
import java.util.List;

public class Order {
    private final String customerName;
    private final List<OrderItem> items;
    private OrderStatus status;
    private Discount discount = new NoDiscount();

    public Order(Builder builder) {
        this.customerName = builder.customerName;
        this.items = builder.items;
        this.status = OrderStatus.NEW;
    }

    public void addItem(OrderItem item){
        if (isPaid()) {
            throw new IllegalStateException("Cannot add items to an order that is already paid.");
        }
        items.add(item);
    }

    // the price of all items, with the discount taken off (before tax)
    public double calculateSubtotal(){
        double total = 0;
        for (OrderItem item : items) {
            total += item.calculateTotal();
        }
        // apply the discount at the end (NoDiscount just returns the same total)
        return discount.apply(total);
    }

    // tax = subtotal * tax rate, where the tax rate comes from the Singleton config
    public double calculateTax(){
        double taxRate = AppConfig.getInstance().getTaxRate();
        return calculateSubtotal() * taxRate;
    }

    // final amount the customer pays = subtotal + tax
    public double calculateTotal(){
        return calculateSubtotal() + calculateTax();
    }

    public void markAsPaid(){
        if (items.isEmpty()) {
            throw new IllegalStateException("Cannot pay an empty order.");
        }
        this.status = OrderStatus.PAID;
    }

    public void applyDiscount(Discount discount){
        this.discount = discount;
    }

    public boolean isPaid(){
        return this.status == OrderStatus.PAID;
    }

    public List<OrderItem> getItems() {
        return items;
    }
    public String getCustomerName() {
        return customerName;
    }
    public OrderStatus getStatus() {
        return status;
    }
    public static Builder builder(){
        return new Builder();
    }
    public static class Builder{
        private String customerName;
        private List<OrderItem> items = new ArrayList<>();
        public Builder customerName(String customerName){
            this.customerName = customerName;
            return this;
        }
        public Builder addItem(OrderItem item){
            this.items.add(item);
            return this;
        }
        public Order build(){
            if (customerName == null || customerName.isBlank()) {
                throw new IllegalArgumentException("Customer name is required.");
            }
            return new Order(this);
        }
    }
}
