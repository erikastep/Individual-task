package com.bootcamp.smarthome.exception;

// Task 1: checked exception, extends the base
public class InvalidCommandException extends HomeAutomationException {

    public InvalidCommandException(String message) {
        super(message);
    }
}
