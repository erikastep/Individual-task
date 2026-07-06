package org.example.model;

import java.util.ArrayList;
import java.util.List;

public class OrderHistory {
    private final List<Order> completedOrders = new ArrayList<>();

    public void add(Order order){
        completedOrders.add(order);
    }

    public List<Order> getCompletedOrders(){
        return completedOrders;
    }
}
