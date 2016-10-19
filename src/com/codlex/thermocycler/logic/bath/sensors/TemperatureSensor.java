package com.codlex.thermocycler.logic.bath.sensors;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.Sensor;

import lombok.extern.log4j.Log4j;

@Log4j
public class TemperatureSensor {

	private final Sensor<Float> sensor;

	public TemperatureSensor(String sensorAddress) {
		this.sensor = HardwareProvider.get()
				.getTemperatureSensor(sensorAddress);
		this.sensor.startMeasuring();
	}

	public float getTemperature() {
		return this.sensor.getValue();
	}
}
