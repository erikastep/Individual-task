package org.example.menu;

import org.example.config.AppConfig;
import org.example.model.Order;
import org.example.model.OrderHistory;
import org.example.model.OrderItem;
import org.example.model.PaymentResult;
import org.example.payment.PaymentMethod;
import org.example.payment.PaymentMethodFactory;
import org.example.payment.PaymentProcessor;

import java.util.Scanner;

public class ConsoleMenu {
    private final Scanner scanner = new Scanner(System.in);
    private final PaymentProcessor paymentProcessor = new PaymentProcessor();
    private final OrderHistory orderHistory = new OrderHistory();

    private Order currentOrder;

    public void start(){
        AppConfig config = AppConfig.getInstance();
        System.out.println("Welcome to " + config.getApplicationName());

        boolean running = true;
        while(running){
            printMenu();

            int option = readInt();

            switch (option){
                case 1 -> createOrder();
                case 2 -> addItem();
                case 3 -> viewOrder();
                case 4 -> payOrder();
                case 5 -> viewCompletedOrders();
                case 0 -> running = false;
                default -> System.out.println("Invalid option");
            }
        }
    }

    private void createOrder(){
        System.out.println("Customer name:");
        String customerName = scanner.nextLine();

        currentOrder = Order.builder()
                .customerName(customerName)
                .build();
        System.out.println("Order created for " + customerName);
    }

    private void addItem(){
        if (currentOrder == null) {
            System.out.println("Please create an order first.");
            return;
        }

        System.out.println("Item name:");
        String itemName = scanner.nextLine();

        System.out.println("Price:");
        double price = readDouble();

        System.out.println("Quantity:");
        int quantity = readInt();

        currentOrder.addItem(new OrderItem(itemName, price, quantity));
        System.out.println("Item added to order");
    }

    private void viewOrder(){
        if (currentOrder == null) {
            System.out.println("Please create an order first.");
            return;
        }

        System.out.println("Customer: " + currentOrder.getCustomerName());
        System.out.println("Status: " +  currentOrder.getStatus());
        System.out.println("Items:");

        for (OrderItem item : currentOrder.getItems()){
            System.out.println("- " + item);
        }

        System.out.println("Subtotal: " + currentOrder.calculateSubtotal());
        System.out.println("Tax: " + currentOrder.calculateTax());
        System.out.println("Total: " + currentOrder.calculateTotal());
    }

    private void payOrder(){
        if (currentOrder == null) {
            System.out.println("Please create an order first.");
            return;
        }

        System.out.println("""
                Select payment method:
                1. Credit Card
                2. PayPal
                3. Gift Card
                """);
        int option = readInt();

        PaymentMethod paymentMethod;
        switch (option){
            case 1 -> paymentMethod = createCreditCardPayment();
            case 2 -> paymentMethod = createPaypalPayment();
            case 3 -> paymentMethod = createGiftCardPayment();
            default -> {
                System.out.println("Invalid payment method");
                return;
            }
        }

        PaymentResult result = paymentProcessor.process(currentOrder, paymentMethod);
        System.out.println(result.getMessage());

        if (result.isSuccessful()) {
            orderHistory.add(currentOrder);
        }
    }

    private void viewCompletedOrders(){
        if (orderHistory.getCompletedOrders().isEmpty()) {
            System.out.println("No completed orders yet.");
            return;
        }

        System.out.println("Completed orders:");
        for (Order order : orderHistory.getCompletedOrders()) {
            System.out.println("- " + order.getCustomerName() + " paid " + order.calculateTotal());
        }
    }

    private PaymentMethod createCreditCardPayment(){
        System.out.println("Card number:");
        String cardNumber =  scanner.nextLine();

        System.out.println("Card holder name:");
        String cardHolderName =  scanner.nextLine();

        return PaymentMethodFactory.createCreditCardPayment(cardNumber,cardHolderName);
    }

    private  PaymentMethod createPaypalPayment(){
        System.out.println("PayPal email:");
        String email = scanner.nextLine();

        return PaymentMethodFactory.createPaypalPayment(email);
    }

    private PaymentMethod createGiftCardPayment(){
        System.out.println("Gift card code:");
        String code = scanner.nextLine();

        System.out.println("Gift card balance:");
        double balance = readDouble();

        return PaymentMethodFactory.createGiftCardPayment(code, balance);
    }

    private int readInt(){
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private double readDouble(){
        while (true) {
            try {
                return Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void printMenu(){
        System.out.println("""
                1. Create order
                2. Add item to order
                3. View order
                4. Pay order
                5. View completed orders
                0. Exit
                """);
    }
}
