package com.codlex.thermocycler.logic.bath;

import java.util.concurrent.atomic.AtomicBoolean;

import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.Thermocycler;
import com.codlex.thermocycler.logic.bath.sensors.LevelSensor;
import com.codlex.thermocycler.logic.bath.sensors.TemperatureSensor;
import com.pi4j.io.gpio.Pin;

import javafx.application.Platform;
import javafx.beans.Observable;
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
	private AtomicBoolean logicOn = new AtomicBoolean(true);

	public Bath(Thermocycler thermocycler, String temperatureSensorIndex1, String temperatureSensorIndex2,
			Pin levelEchoPin, Pin levelTriggerPin, Pin waterPumpPin) {
		this.thermocycler = thermocycler;
		this.temperatureSensor1 = new TemperatureSensor(temperatureSensorIndex1);
		this.temperatureSensor2 = new TemperatureSensor(temperatureSensorIndex2);
		this.level = new LevelSensor(levelEchoPin, levelTriggerPin, Settings.get().getBathDepth());
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
		if (!Settings.get().getKeepLevelOn()) {
			return true;
		}
		
		int minimumLevel = Settings.get().getBathMinimumLevel() - Settings.get().getLevelEpsilon();
		return this.level.getPercentageFilled() > minimumLevel;
	}

	public boolean isReady() {
		return isTemperatureOK() && isLevelOK();
	}

	public boolean isTemperatureOK() {
		if (!this.logicOn.get()) {
			return true;
		}
		
		float minTemperature = this.temperature.get() - Settings.get().getTemperatureEpsilon();
		float maxTemperature = this.temperature.get() + Settings.get().getTemperatureEpsilon();
		float currentTemperature = getCurrentTemperature();
		return minTemperature <= currentTemperature && currentTemperature <= maxTemperature;
	}

	public boolean isValid() {
		return this.isLevelOK() && Settings.get().getValidationTimeRange().contains(this.time.get());
	}

	public void keepLevel() {
		if (!Settings.get().getKeepLevelOn()) {
			return;
		}
		
		int precetage = this.level.getPercentageFilled();
		if (precetage < Settings.get().getBathMinimumLevel()) {
			this.pump.turnOn();
		} else {
			this.pump.turnOff();
		}
	}

	public abstract void keepTemperature();

	public abstract void logStatus();

	private boolean performLevelSafetyChecks() {
		
		int level = this.level.getPercentageFilled();
		int minLevel = Settings.get().getSafetyLevelMin();
		int maxLevel = Settings.get().getSafetyLevelMax();

		boolean isOk = level > minLevel;
		isOk &= level < maxLevel;

		if (!isOk) {
			log.error(getClass().getSimpleName() + " safety check failed: level of liquid dangerous! " + minLevel
					+ " < " + level + " < " + maxLevel);
		}
		return isOk;
	}

	public boolean performSafetyChecks() {
		boolean isOk = true;
		if (this.thermocycler.isStarted.get()) {
			if (Settings.get().getKeepLevelOn()) {
				isOk &= performLevelSafetyChecks();
			}
		}
		return isOk;
	}

	public void reset() {
		Platform.runLater(() -> {
			this.temperature.set(getInitialTemperature());
			this.time.set(10);
		});
	}
	
	private int getInitialTemperature() {
		return (int) getCurrentTemperature();
	}

	public void update(long deltaT) {
		// TODO: fix this, this is to initialize values
		getCurrentTemperature();
		if (this.logicOn.get()) {
			keepTemperature();
		}
		keepLevel();
	}

	public void setLogicOn(boolean newValue) {
		this.logicOn.set(newValue);
	}
}
