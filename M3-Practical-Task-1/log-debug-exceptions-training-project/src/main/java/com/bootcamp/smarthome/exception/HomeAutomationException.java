package com.bootcamp.smarthome.exception;

// Task 1: base checked exception for the whole hierarchy
public class HomeAutomationException extends Exception {

    public HomeAutomationException(String message) {
        super(message);
    }

    public HomeAutomationException(String message, Throwable cause) {
        super(message, cause);
    }
}
