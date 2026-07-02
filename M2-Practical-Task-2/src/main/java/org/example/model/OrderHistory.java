package org.example.model;

import java.util.ArrayList;
import java.util.List;

// Keeps every completed (paid) order in a list while the app is running.
// This is "in memory" - the list is forgotten when you close the program.
public class OrderHistory {
    private final List<Order> completedOrders = new ArrayList<>();

    public void add(Order order){
        completedOrders.add(order);
    }

    public List<Order> getCompletedOrders(){
        return completedOrders;
    }
}
