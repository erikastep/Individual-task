package com.bootcamp.smarthome.exception;

// Task 1: checked exception, extends the base
public class DeviceOfflineException extends HomeAutomationException {

    public DeviceOfflineException(String message) {
        super(message);
    }
}
