package com.codlex.thermocycler.logic.bath;

import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.Thermocycler;
import com.codlex.thermocycler.logic.bath.sensors.LevelSensor;
import com.codlex.thermocycler.logic.bath.sensors.TemperatureSensor;
import com.pi4j.io.gpio.Pin;

import javafx.application.Platform;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class Bath {

	@Getter
	private FloatProperty currentTemperatureProperty = new SimpleFloatProperty();
	protected TemperatureSensor temperatureSensor1;
	protected TemperatureSensor temperatureSensor2;

	private WaterPump pump;
	protected LevelSensor level;

	protected IntegerProperty temperature = new SimpleIntegerProperty();
	public IntegerProperty time = new SimpleIntegerProperty();
	private final Thermocycler thermocycler;

	public Bath(Thermocycler thermocycler, String temperatureSensorIndex1,
			String temperatureSensorIndex2, Pin levelEchoPin,
			Pin levelTriggerPin, Pin waterPumpPin) {
		this.thermocycler = thermocycler;
		this.temperatureSensor1 = new TemperatureSensor(
				temperatureSensorIndex1);
		this.temperatureSensor2 = new TemperatureSensor(
				temperatureSensorIndex2);
		this.level = new LevelSensor(levelEchoPin, levelTriggerPin,
				Settings.BathDepth);
		this.pump = new WaterPump(waterPumpPin);
	}

	public void clear() {
		this.pump.turnOff();
	}

	public synchronized float getCurrentTemperature() {
		float sum = this.temperatureSensor1.getTemperature();
		sum += this.temperatureSensor2.getTemperature();
		float averageTemp = sum / 2;

		Platform.runLater(() -> {
			this.currentTemperatureProperty.setValue(averageTemp);
		});

		return averageTemp;
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

	public boolean isValid() {
		return this.isLevelOK()
				&& Settings.ValidationTimeRange.contains(this.time.get());
	}

	public void keepLevel() {
		int precetage = this.level.getPercentageFilled();
		if (precetage < Settings.BathMinimumLevel) {
			this.pump.turnOn();
		} else {
			this.pump.turnOff();
		}
	}

	public abstract void keepTemperature();

	public abstract void logStatus();

	private boolean performLevelSafetyChecks() {
		boolean isOk = this.level
				.getPercentageFilled() > Settings.SafetyLevelMin;
		isOk &= this.level.getPercentageFilled() < Settings.SafetyLevelMax;
		if (!isOk) {
			log.error("Safety check failed: level of liquid dangerous!");
		}
		return isOk;
	}

	public boolean performSafetyChecks() {
		boolean isOk = true;
		if (this.thermocycler.isStarted.get()) {
			isOk &= performLevelSafetyChecks();
		}
		return isOk;
	}

	public void reset() {
		Platform.runLater(() -> {
			this.temperature.set(20);
			this.time.set(10);
		});

	}

	public void update(long deltaT) {
		// TODO: fix this, this is to initialize values
		getCurrentTemperature();
		keepTemperature();
		keepLevel();
	}
}
