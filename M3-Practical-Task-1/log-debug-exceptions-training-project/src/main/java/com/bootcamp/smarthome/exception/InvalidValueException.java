package com.bootcamp.smarthome.exception;

// Task 1: checked exception with the required 3-argument constructor
public class InvalidValueException extends HomeAutomationException {

    public InvalidValueException(String field, Object value, String constraint) {
        super("Invalid value for " + field + ": " + value + " (" + constraint + ")");
    }
}
