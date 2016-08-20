package com.codlex.thermocycler.logic.bath.hot;

import com.codlex.thermocycler.hardware.OnOffDevice;
import com.codlex.thermocycler.hardware.SimpleSwitch;
import com.pi4j.io.gpio.Pin;

import lombok.extern.log4j.Log4j;

@Log4j
public class Heater extends OnOffDevice {

	public Heater(Pin pin) {
		super(pin);
	}

}
