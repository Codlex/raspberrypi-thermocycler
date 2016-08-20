package com.codlex.thermocycler.logic.bath.sensors;

import java.io.IOException;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.Sensor;
import com.pi4j.io.gpio.Pin;

import lombok.extern.log4j.Log4j;

@Log4j
public class LevelSensor {

	private int emptyDistance;
	private final Sensor distanceMonitor;

	public LevelSensor(Pin echoPin, Pin triggerPin, int emptyDistance) {
		this.distanceMonitor = HardwareProvider.get()
				.getDistanceSensorForPins(echoPin, triggerPin);
		this.emptyDistance = emptyDistance;
	}

	public int getPercentageFilled() {
		float distanceFromWater = 0;
		try {
			distanceFromWater = this.distanceMonitor.getValue().floatValue();
		} catch (IOException e) {
			log.error("Couldn't measure level", e);
		}

		float filledCM = this.emptyDistance - distanceFromWater;
		return (int) ((filledCM / this.emptyDistance) * 100);
	}

}