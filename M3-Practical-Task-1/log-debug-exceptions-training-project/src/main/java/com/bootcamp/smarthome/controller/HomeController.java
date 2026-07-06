package com.bootcamp.smarthome.controller;

import com.bootcamp.smarthome.device.Device;
import com.bootcamp.smarthome.exception.HomeAutomationException; // Task 2
import org.slf4j.Logger;              // Task 3
import org.slf4j.LoggerFactory;       // Task 3

/**
 * Central hub that manages all registered smart devices.
 *
 * Devices are stored in a fixed-size array (maximum {@value #MAX_DEVICES}).
 * The controller routes commands to devices by their ID.
 */
public class HomeController {

    // Task 3: SLF4J logger for this class
    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    public static final int MAX_DEVICES = 8;

    private final Device[] devices = new Device[MAX_DEVICES];
    private int deviceCount = 0;

    // -------------------------------------------------------------------------
    // Device registration
    // -------------------------------------------------------------------------

    /**
     * Registers a new device with the controller.
     * The controller accepts at most {@value #MAX_DEVICES} devices.
     *
     * @param device the device to register
     * @throws IllegalStateException if the device limit has been reached
     */
    public void addDevice(Device device) {
        if (deviceCount >= MAX_DEVICES) {
            throw new IllegalStateException(
                    "Cannot add device '" + device.getDeviceId() +
                    "': controller is at maximum capacity (" + MAX_DEVICES + ").");
        }
        devices[deviceCount] = device;
        deviceCount++;
        System.out.println("Device registered: " + device);
    }

    // -------------------------------------------------------------------------
    // Device lookup
    // -------------------------------------------------------------------------

    /**
     * Finds a registered device by its ID.
     *
     * Returns {@code null} when no matching device is found.
     */
    public Device findDevice(String deviceId) {
        for (int i = 0; i < deviceCount; i++) {   // Task 4 (bug 2): was <= which ran past the array
            if (devices[i] != null && devices[i].getDeviceId().equals(deviceId)) {
                return devices[i];
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------
    // Command routing
    // -------------------------------------------------------------------------

    /**
     * Parses {@code fullCommand}, resolves the target device, and delegates
     * execution to {@link Device#executeCommand(String)}.
     *
     * Full command format: {@code "DEVICE_ID ACTION [VALUE]"}
     * Example: {@code "LIGHT_01 SET_BRIGHTNESS 75"}
     *
     * @param fullCommand the full command string
     */
    public void sendCommand(String fullCommand) throws HomeAutomationException {
        String deviceId = CommandParser.extractDeviceId(fullCommand);

        // Task 2: wrap the whole body in try-catch-finally
        try {
            String command = CommandParser.extractCommand(fullCommand);

            // Task 3: DEBUG - a command was received
            logger.debug("Command received for device {}: {}", deviceId, fullCommand);

            Device device = findDevice(deviceId);

            if (device == null) {
                System.out.println("Device not found: " + deviceId);
                return;
            }

            if (!device.isOnline()) {
                // Task 3: WARN - device offline, command skipped
                logger.warn("Device '{}' is offline — command skipped.", deviceId);
                return;
            }

            device.executeCommand(command);

            // Task 3: INFO - command executed successfully
            logger.info("Command executed successfully for device {}", deviceId);
        } catch (HomeAutomationException e) {
            // Task 3: ERROR - an exception was caught during processing
            logger.error("Command '{}' failed for device '{}'", fullCommand, deviceId, e);
            // Task 2: rethrow with more context, original kept as cause
            throw new HomeAutomationException(
                    "Command '" + fullCommand + "' failed for device '" + deviceId + "'", e);
        } finally {
            System.out.println("Command processing ended for device " + deviceId);
        }
    }

    // -------------------------------------------------------------------------
    // Utility
    // -------------------------------------------------------------------------

    /** Prints the status of every registered device. */
    public void printAllDevices() {
        System.out.println("=== Registered Devices (" + deviceCount + "/" + MAX_DEVICES + ") ===");
        for (int i = 0; i < deviceCount; i++) {
            System.out.println("  " + devices[i]);
        }
    }

    public int getDeviceCount() {
        return deviceCount;
    }
}
