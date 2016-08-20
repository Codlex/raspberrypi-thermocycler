package com.codlex.thermocycler.logic.bath.cold;

import com.codlex.thermocycler.logic.Switch;
import com.pi4j.io.gpio.Pin;

import lombok.extern.log4j.Log4j;

@Log4j
public class Cooler extends Switch {

	public Cooler(Pin pin) {
		super(pin);
	}

	@Override
	protected void off() {
		log.debug("Cooler is turned off.");
	}

	@Override
	protected void on() {
		log.debug("Cooler is turned on.");
	}

}
