package com.codlex.thermocycler.logic.bath.hot;

import com.codlex.thermocycler.logic.Switch;
import com.pi4j.io.gpio.Pin;

import lombok.extern.log4j.Log4j;

@Log4j
public class Heater extends Switch {

	public Heater(Pin pin) {
		super(pin);
	}

	@Override
	protected void off() {
		log.debug("Heater is turned off!");
	}

	@Override
	protected void on() {
		log.debug("Heater is turned on!");
	}

}
