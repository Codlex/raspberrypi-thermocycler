package com.codlex.thermocycler.logic.bath.sensors;

import java.io.IOException;

import lombok.extern.log4j.Log4j;
import to_rewrite.Sensor;
import to_rewrite.Sensors;

@Log4j
public class TemperatureSensor {

	private final Sensor sensor;

	public TemperatureSensor(String sensorAddress) {
		this.sensor = Sensors.getSensorById(sensorAddress).get();
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
