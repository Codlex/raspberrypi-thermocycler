package com.codlex.thermocycler.logic.bath.sensors;

import java.io.IOException;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.Sensor;

import lombok.extern.log4j.Log4j;

@Log4j
public class TemperatureSensor {

	private final Sensor sensor;

	public TemperatureSensor(String sensorAddress) {
		this.sensor = HardwareProvider.get().getTemperatureSensor(sensorAddress);
	}

	public float getTemperature() {
		try {
			return this.sensor.getValue().floatValue();
		} catch (IOException e) {
			log.error("Couldn't read temperature", e);
			return -255;
		}
	}
}
