package com.codlex.thermocycler.logic.bath;

import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.bath.sensors.LevelSensor;
import com.codlex.thermocycler.logic.bath.sensors.TemperatureSensor;
import com.pi4j.io.gpio.Pin;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class Bath {

	protected TemperatureSensor temperatureSensor1;

	protected TemperatureSensor temperatureSensor2;

	private WaterPump pump;

	protected IntegerProperty temperature = new SimpleIntegerProperty();

	public IntegerProperty time = new SimpleIntegerProperty();

	protected LevelSensor level;

	public Bath(String temperatureSensorIndex1, String temperatureSensorIndex2,
			Pin levelEchoPin, Pin levelTriggerPin, Pin waterPumpPin) {
		this.temperatureSensor1 = new TemperatureSensor(
				temperatureSensorIndex1); // TODO: add addres
		this.temperatureSensor2 = new TemperatureSensor(
				temperatureSensorIndex2); // TODO: add addres
		this.level = new LevelSensor(levelEchoPin, levelTriggerPin,
				Settings.BathDepth);
		this.pump = new WaterPump(waterPumpPin);
	}

	protected float getCurrentTemperature() {
		float sum = this.temperatureSensor1.getTemperature();
		sum += this.temperatureSensor2.getTemperature();
		return sum / 2;
	}
	public boolean isLevelOK() {
		int minimumLevel = Settings.BathMinimumLevel - Settings.LevelEpsilon;
		return this.level.getPercentageFilled() > minimumLevel;
	}
	public boolean isReady() {
		return isTemperatureOK() && isLevelOK();
	}

	public boolean isTemperatureOK() {
		float minTemperature = this.temperature.get()
				- Settings.TemperatureEpsilon;
		float maxTemperature = this.temperature.get()
				+ Settings.TemperatureEpsilon;
		float currentTemperature = getCurrentTemperature();
		return minTemperature <= currentTemperature
				&& currentTemperature <= maxTemperature;
	}

	public void keepLevel() {
		int precetage = this.level.getPercentageFilled();
		if (precetage < Settings.BathMinimumLevel) {
			log.debug("ukljuceno punjenje! " + precetage);
			this.pump.turnOn();
		} else {
			log.debug("iskljuceno punjenje!" + precetage);
			this.pump.turnOff();
		}
	}

	public abstract void keepTemperature();

	public abstract void logStatus();

	public void update(long deltaT) {
		keepTemperature();
		keepLevel();
		logStatus();
	}

	public LevelSensor getLevelSensor() {
		return this.level;
	}

	public IntegerProperty getTemperatureProperty() {
		return this.temperature;
	}

	public IntegerProperty getTimeProperty() {
		return this.time;
	}

}
