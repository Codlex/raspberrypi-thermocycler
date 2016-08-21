package com.codlex.thermocycler.logic.bath.sensors;

import java.io.IOException;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.Sensor;
import com.pi4j.io.gpio.Pin;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.log4j.Log4j;

@Log4j
public class LevelSensor {

	private int emptyDistance;
	private final Sensor distanceMonitor;

	private IntegerProperty property = new SimpleIntegerProperty();
	private DoubleProperty doubleProperty = new SimpleDoubleProperty();
	
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
		int integerValue = (int) ((filledCM / this.emptyDistance) * 100);
		
		Platform.runLater(() -> {
			this.property.set(integerValue);
			this.doubleProperty.set(integerValue / 100.0);
		});
		
		return property.get();
	}

	public IntegerProperty getProperty() {
		getPercentageFilled(); // dummy way to refresh
		return this.property;
	}
	
	
	public DoubleProperty getDoubleProperty() {
		getPercentageFilled(); // dummy way to refresh
		return this.doubleProperty;
	}

}