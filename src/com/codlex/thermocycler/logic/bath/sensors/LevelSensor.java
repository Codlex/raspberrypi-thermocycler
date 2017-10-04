package com.codlex.thermocycler.logic.bath.sensors;

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
	private final Sensor<Float> distanceMonitor;

	private IntegerProperty property = new SimpleIntegerProperty();
	private DoubleProperty doubleProperty = new SimpleDoubleProperty();
	private boolean isFirstTime = true;

	public LevelSensor(Pin echoPin, Pin triggerPin, int emptyDistance) {
		this.distanceMonitor = HardwareProvider.get()
				.getDistanceSensorForPins(echoPin, triggerPin);
		this.distanceMonitor.startMeasuring();
		this.emptyDistance = emptyDistance;
	}

	public DoubleProperty getDoubleProperty() {
		getPercentageFilled(); // dummy way to refresh
		return this.doubleProperty;
	}

	public int getPercentageFilled() {
		float distanceFromWater = this.distanceMonitor.getValue().floatValue();

		float filledCM = this.emptyDistance - distanceFromWater;
		int integerValue = (int) ((filledCM / this.emptyDistance) * 100);

		int diff = this.isFirstTime ? 0 : Math.abs(this.property.get() - integerValue);
		
		if (0 <= integerValue && integerValue <= 100 && diff < 10) {
			Platform.runLater(() -> {
				this.property.set(integerValue);
				this.doubleProperty.set(integerValue / 100.0);
			});
		} else {
			log.error("Ignoring value: " + integerValue);
		}
		
		this.isFirstTime  = false;
		
		return this.property.get();
	}

	public IntegerProperty getProperty() {
		getPercentageFilled(); // dummy way to refresh
		return this.property;
	}

}