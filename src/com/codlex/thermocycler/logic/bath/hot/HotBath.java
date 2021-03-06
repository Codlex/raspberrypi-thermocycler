package com.codlex.thermocycler.logic.bath.hot;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.Switch;
import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.Thermocycler;
import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.sensors.TemperatureSensor;
import com.codlex.thermocycler.tracker.Tracker;

import javafx.application.Platform;
import lombok.extern.log4j.Log4j;

@Log4j
public class HotBath extends Bath {

	private final Heater heater;
	private final Switch circulationWaterPump;
	private final TemperatureSensor temperatureSensor3;

	public HotBath(Thermocycler thermocycler) {
		super(thermocycler, Settings.HotBathTemperatureSensor1,
				Settings.HotBathTemperatureSensor2,
				Settings.get().getHotBathLevelEchoPin(), Settings.get().getHotBathLevelTriggerPin(),
				Settings.get().getHotBathWaterPump());
		this.heater = new Heater(Settings.get().getHotBathHeaterPin());
		this.time.set(1);
		this.circulationWaterPump = HardwareProvider.get()
				.getSwitch(Settings.get().getHotBathCirculationWaterPump(), "HotBathCirculationWaterPump");
		this.temperatureSensor3 = new TemperatureSensor(
				Settings.HotBathTemperatureSensor3);
	}

	@Override
	public void clear() {
		log.debug("Clearning HotBath.");
		super.clear();
		this.heater.turnOff();
		this.circulationWaterPump.turnOff();
	}

	@Override
	public boolean isValid() {
		boolean isValid = super.isValid();
		isValid &= Settings.get().getValidationHotTemperatureRange()
				.contains(this.temperature.get());
		return isValid;
	}

	@Override
	public void keepTemperature() {
		
		if (Settings.get().getCirculationPumpOn()) {
			this.circulationWaterPump.turnOn();
		} else {
			this.circulationWaterPump.turnOff();
		}
		
		if (getCurrentTemperature() < this.temperature.get()) {
			this.heater.turnOn();
		} else {
			this.heater.turnOff();
		}
	}

	@Override
	public void logStatus() {
		Tracker.track("hot_bath.temperature1",
				this.temperatureSensor1.getTemperature());
		Tracker.track("hot_bath.temperature2",
				this.temperatureSensor2.getTemperature());
		Tracker.track("hot_bath.temperature3",
				this.temperatureSensor3.getTemperature());
		Tracker.track("hot_bath.percentageFilled",
				this.level.getPercentageFilled());
		Tracker.track("hot_bath.average", getCurrentTemperature());

		log.debug("HotBathStatus(temp1="
				+ this.temperatureSensor1.getTemperature() + ", temp2="
				+ this.temperatureSensor2.getTemperature() + ", level="
				+ this.level.getPercentageFilled() + ")");
	}

	@Override
	public boolean performSafetyChecks() {
		boolean isOk = super.performSafetyChecks();
		if (this.temperatureSensor3
				.getTemperature() > Settings.get().getSafetyHotBathTemperatureMax()) {
			log.error(
					"Safety check failed: maximum safe temperature exceeded, t = "
							+ this.temperatureSensor3.getTemperature());
			isOk &= false;
		}

		return isOk;
	}
	
	@Override
	public synchronized float getCurrentTemperature() {
		float sum = this.temperatureSensor1.getTemperature();
		float averageTemp = sum;

		Platform.runLater(() -> {
			this.currentTemperatureProperty.setValue(averageTemp);
		});

		return averageTemp;
	}
}