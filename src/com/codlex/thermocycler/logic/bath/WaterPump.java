package com.codlex.thermocycler.logic.bath;

import com.codlex.thermocycler.logic.Switch;
import com.pi4j.io.gpio.Pin;

import lombok.extern.log4j.Log4j;

@Log4j
public class WaterPump extends Switch {

	public WaterPump(Pin pin) {
		super(pin);
	}

	@Override
	public void off() {
		log.debug("Pump is turned off.");
	}

	@Override
	public void on() {
		log.debug("Pump is turned on.");
	}
}