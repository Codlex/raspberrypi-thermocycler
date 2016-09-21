package com.codlex.thermocycler.hardware;

import com.codlex.thermocycler.logic.Settings;
import com.pi4j.io.gpio.Pin;

public class HardwareProvider {
	
	private static final HardwareProvider INSTANCE = new HardwareProvider();
	
	public static HardwareProvider get() {
		return INSTANCE;
	}
	
	public Switch getSwitch(Pin pin, boolean inverse) {
		if (Settings.get().getProduction()) {
			return new SimpleSwitch(pin, inverse);
		} else {
			return VirtualSwitches.get().getSwitch(pin);
		}
	}
	
	public Switch getSwitch(Pin pin) {
		if (Settings.get().getProduction()) {
			return new SimpleSwitch(pin);
		} else {
			return VirtualSwitches.get().getSwitch(pin);
		}
	}
	
	public Sensor<Float> getTemperatureSensor(String sensorId) {
		if (Settings.get().getProduction()) {
			return Sensors.getSensorById(sensorId).get();
		} else {
			return VirtualSensors.get().getTemperatureSensorById(sensorId);
		}
	}
	
	public Sensor<Float> getDistanceSensorForPins(Pin echo, Pin trigger) {
		if (Settings.get().getProduction()) {
			return new DistanceMonitorImpl(echo, trigger);
		} else {
			return VirtualSensors.get().getDistanceSensor(echo, trigger);
		}
	}

}
