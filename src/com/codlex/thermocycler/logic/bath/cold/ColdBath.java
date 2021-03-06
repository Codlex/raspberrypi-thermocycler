package com.codlex.thermocycler.logic.bath.cold;

import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.Thermocycler;
import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.sensors.TemperatureSensor;
import com.codlex.thermocycler.tracker.Tracker;

import lombok.extern.log4j.Log4j;

@Log4j
public class ColdBath extends Bath {

	private Cooler cooler;
	private TemperatureSensor antifrizTemperature;

	public ColdBath(Thermocycler thermocycler) {
		super(thermocycler, Settings.ColdBathTemperatureSensor1,
				Settings.ColdBathTemperatureSensor2,
				Settings.get().getColdBathLevelEchoPin(), Settings.get().getColdBathLevelTriggerPin(),
				Settings.get().getColdBathWaterPump());
		this.cooler = new Cooler(Settings.get().getColdBathCoolerPin());
		this.antifrizTemperature = new TemperatureSensor(
				Settings.ColdBathTemperatureSensorAntifriz);

		this.time.set(1);
	}

	@Override
	public void clear() {
		log.debug("Clearning ColdBath.");
		super.clear();
		this.cooler.turnOff();
	}

	@Override
	public boolean isValid() {
		boolean isValid = super.isValid();
		isValid &= Settings.get().getValidationColdTemperatureRange()
				.contains(this.temperature.get());
		return isValid;
	}

	@Override
	public void keepTemperature() {
		
		// cooler turned on when antifriz temperature goes over
		// (wanted_temperature - epsOn)
		float epsOn = Settings.get().getColdBathEpsOn();
		boolean turnOn = this.antifrizTemperature
				.getTemperature() > this.temperature.get() - epsOn 
				|| getCurrentTemperature() > this.temperature.get() - epsOn;
		if (turnOn) {
			this.cooler.turnOn();
		}

		// cooler turned off when temperature in bath reaches wanted temperature
		// OR antifriz temperature goes below (wanted_temperature - epsOff)
		float epsOff = Settings.get().getColdBathEpsOff();
		boolean turnOff = this.antifrizTemperature
				.getTemperature() < this.temperature.get() - epsOff;

		if (turnOff) {
			this.cooler.turnOff();
		}
		
//		if (isTemperatureOK()) {
//			this.cooler.turnOff();
//			return;
//		}

	}

	@Override
	public void logStatus() {
		Tracker.track("cold_bath.temperature1",
				this.temperatureSensor1.getTemperature());
		Tracker.track("cold_bath.temperature2",
				this.temperatureSensor2.getTemperature());
		Tracker.track("cold_bath.antifrizTemperature",
				this.antifrizTemperature.getTemperature());
		Tracker.track("cold_bath.percentageFilled",
				this.level.getPercentageFilled());
		Tracker.track("cold_bath.average", getCurrentTemperature());

		log.debug("ColdBathStatus(temp1="
				+ this.temperatureSensor1.getTemperature() + ", temp2="
				+ this.temperatureSensor2.getTemperature() + ", tempA="
				+ this.antifrizTemperature.getTemperature() + ", level="
				+ this.level.getPercentageFilled() + ")");
	}

};
