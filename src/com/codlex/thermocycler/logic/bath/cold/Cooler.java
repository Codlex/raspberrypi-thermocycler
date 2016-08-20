package com.codlex.thermocycler.logic.bath.cold;

import com.codlex.thermocycler.hardware.OnOffDevice;
import com.codlex.thermocycler.hardware.SimpleSwitch;
import com.pi4j.io.gpio.Pin;

import lombok.extern.log4j.Log4j;

@Log4j
public class Cooler extends OnOffDevice {
	
	public Cooler(Pin pin) {
		super(pin);
	}
}
