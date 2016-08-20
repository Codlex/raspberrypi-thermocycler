package com.codlex.thermocycler.logic.bath.cold;

import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.sensors.TemperatureSensor;

import lombok.extern.log4j.Log4j;

@Log4j
public class ColdBath extends Bath {

	Cooler cooler;
	TemperatureSensor antifrizTemperature;

	public ColdBath() {
		super(Settings.ColdBathTemperatureSensor1,
				Settings.ColdBathTemperatureSensor2,
				Settings.ColdBathLevelEchoPin, Settings.ColdBathLevelTriggerPin,
				Settings.ColdBathWaterPump);
		this.cooler = new Cooler(Settings.ColdBathCoolerPin);
		this.antifrizTemperature = new TemperatureSensor(
				Settings.ColdBathTemperatureSensorAntifriz);

		this.temperature = 30;
		this.time = 9;
	}

	@Override
	public void keepTemperature() {
		if (isTemperatureOK()) {
			this.cooler.turnOff();
			return;
		}

		// cooler turned on when antifriz temperature goes over
		// (wanted_temperature - epsOn)
		float epsOn = 1;
		boolean turnOn = this.antifrizTemperature
				.getTemperature() > this.temperature - epsOn;

		if (turnOn) {
			this.cooler.turnOn();
		}

		// cooler turned off when temperature in bath reaches wanted temperature
		// OR antifriz temperature goes below (wanted_temperature - epsOff)
		float epsOff = 4;
		boolean turnOff = this.antifrizTemperature
				.getTemperature() < this.temperature - epsOff;

		if (turnOff) {
			this.cooler.turnOff();
		}
	}

	@Override
	public void logStatus() {
		log.debug("ColdBathStatus(temp1="
				+ this.temperatureSensor1.getTemperature() + ", temp2="
				+ this.temperatureSensor2.getTemperature() + ", tempA="
				+ this.antifrizTemperature.getTemperature() + ", level="
				+ this.level.getPercentageFilled() + ")");
	}

};
