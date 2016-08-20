package com.codlex.thermocycler.hardware;

import com.pi4j.io.gpio.Pin;

public class HardwareProvider {
	
	private static final boolean PRODUCTION = false;
	
	private static final HardwareProvider INSTANCE = new HardwareProvider();
	
	public static HardwareProvider get() {
		return INSTANCE;
	}
	
	public Switch getSwitch(Pin pin, boolean inverse) {
		if (PRODUCTION) {
			return new SimpleSwitch(pin, inverse);
		} else {
			return VirtualSwitches.get().getSwitch(pin);
		}
	}
	
	public Switch getSwitch(Pin pin) {
		if (PRODUCTION) {
			return new SimpleSwitch(pin);
		} else {
			return VirtualSwitches.get().getSwitch(pin);
		}
	}
	
	public Sensor getTemperatureSensor(String sensorId) {
		if (PRODUCTION) {
			return Sensors.getSensorById(sensorId).get();
		} else {
			return VirtualSensors.get().getTemperatureSensorById(sensorId);
		}
	}
	
	public Sensor getDistanceSensorForPins(Pin echo, Pin trigger) {
		if (PRODUCTION) {
			return new DistanceMonitorImpl(echo, trigger);
		} else {
			return VirtualSensors.get().getDistanceSensor(echo, trigger);
		}
	}

}
