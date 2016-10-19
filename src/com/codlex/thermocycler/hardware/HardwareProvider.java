package com.codlex.thermocycler.hardware;

import com.codlex.thermocycler.logic.Settings;
import com.pi4j.io.gpio.Pin;

public class HardwareProvider {

	private static final HardwareProvider INSTANCE = new HardwareProvider();

	public static HardwareProvider get() {
		return INSTANCE;
	}

	public Sensor<Float> getDistanceSensorForPins(Pin echo, Pin trigger) {
		if (Settings.get().getProduction()) {
			return new DistanceSensor(echo, trigger);
		} else {
			return VirtualSensors.get().getDistanceSensor(echo, trigger);
		}
	}

	public Switch getSwitch(Pin pin, String name) {
		if (Settings.get().getProduction()) {
			return new SimpleSwitch(pin, name);
		} else {
			return VirtualSwitches.get().getSwitch(pin);
		}
	}

	public Switch getSwitch(Pin pin, String name, boolean inverse) {
		if (Settings.get().getProduction()) {
			return new SimpleSwitch(pin, name, inverse);
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

}
