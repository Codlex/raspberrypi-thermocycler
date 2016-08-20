package com.codlex.thermocycler.logic.bath.sensors;

import com.pi4j.io.gpio.Pin;

import lombok.extern.log4j.Log4j;
import to_rewrite.DistanceMonitor;
import to_rewrite.DistanceMonitor.TimeoutException;

@Log4j
public class LevelSensor {

	private int emptyDistance;
	private final DistanceMonitor distanceMonitor;

	public LevelSensor(Pin echoPin, Pin triggerPin, int emptyDistance) {
		this.distanceMonitor = new DistanceMonitor(echoPin, triggerPin);
		this.emptyDistance = emptyDistance;
	}

	public int getPercentageFilled() {
		float distanceFromWater = 0;
		try {
			distanceFromWater = this.distanceMonitor.measureDistance();
		} catch (final TimeoutException e) {
			log.error("Couldn't measure level", e);
		}

		float filledCM = this.emptyDistance - distanceFromWater;
		return (int) ((filledCM / this.emptyDistance) * 100);
	}

}