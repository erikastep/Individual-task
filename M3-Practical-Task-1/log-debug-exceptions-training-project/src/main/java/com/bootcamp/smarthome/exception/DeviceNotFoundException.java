package com.bootcamp.smarthome.exception;

// Task 1: unchecked exception (extends RuntimeException, not the base)
public class DeviceNotFoundException extends RuntimeException {

    public DeviceNotFoundException(String message) {
        super(message);
    }
}
