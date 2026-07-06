package com.bootcamp.smarthome.exception;

// Task 1: base checked exception for the whole hierarchy
public class HomeAutomationException extends Exception {

    public HomeAutomationException(String message) {
        super(message);
    }

    // needed later so we can wrap the original error as the "cause"
    public HomeAutomationException(String message, Throwable cause) {
        super(message, cause);
    }
}
