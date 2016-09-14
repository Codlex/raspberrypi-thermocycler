package com.codlex.thermocycler.logic.bath.hot;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.SimpleSwitch;
import com.codlex.thermocycler.hardware.Switch;
import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.sensors.TemperatureSensor;

import lombok.extern.log4j.Log4j;

@Log4j
public class HotBath extends Bath {

	private final Heater heater;
	private final Switch circulationWaterPump;
	private final TemperatureSensor temperatureSensor3;
	
	public HotBath() {
		super(Settings.HotBathTemperatureSensor1,
				Settings.HotBathTemperatureSensor2,
				Settings.HotBathLevelEchoPin, Settings.HotBathLevelTriggerPin,
				Settings.HotBathWaterPump);
		this.heater = new Heater(Settings.HotBathHeaterPin);
		this.temperature.set(20);
		this.time.set(1);
		this.circulationWaterPump = HardwareProvider.get().getSwitch(Settings.HotBathCirculationWaterPump);
		this.circulationWaterPump.turnOn();
		this.temperatureSensor3 = new TemperatureSensor(Settings.HotBathTemperatureSensor3); 
	}

	@Override
	public void keepTemperature() {
		// TOOD: check this
		if (getCurrentTemperature() < this.temperature.get()) {
			this.heater.turnOn();
		} else {
			this.heater.turnOff();
		}
	}

	@Override
	public void logStatus() {
		log.debug("HotBathStatus(temp1="
				+ this.temperatureSensor1.getTemperature() + ", temp2="
				+ this.temperatureSensor2.getTemperature() + ", level="
				+ this.level.getPercentageFilled() + ")");
	}
	
	
	@Override
	public boolean performSafetyChecks() {
		if (this.temperatureSensor3.getTemperature() > Settings.HotBathSafetyTemperatureMax) {
			log.error("Safety check failed: maximum safe temperature exceeded, t = " + this.temperatureSensor3.getTemperature());
			return false;
		}
		
		return true;
	}
}