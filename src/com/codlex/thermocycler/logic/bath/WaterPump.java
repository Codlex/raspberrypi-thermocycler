package com.codlex.thermocycler.logic.bath;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.OnOffDevice;
import com.codlex.thermocycler.hardware.Switch;
import com.pi4j.io.gpio.Pin;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class WaterPump extends OnOffDevice {
	
	public WaterPump(Pin pin) {
		super(pin);
	}
}