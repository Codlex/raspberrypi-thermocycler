package com.codlex.thermocycler.logic.bath.sensors;

import java.io.IOException;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.Sensor;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class TemperatureSensor {
	
	private final Sensor<Float> sensor;

	public TemperatureSensor(String sensorAddress) {
		this.sensor = HardwareProvider.get().getTemperatureSensor(sensorAddress);
	}

	public float getTemperature() {
		return this.sensor.getValue().floatValue();
	}
}
