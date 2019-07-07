package com.codlex.thermocycler.hardware;

import com.pi4j.io.gpio.Pin;

import lombok.Getter;

public class OnOffDevice {

	@Getter
	private Switch controller;

	public OnOffDevice(Pin pin, String name) {
		this.controller = HardwareProvider.get().getSwitch(pin, name);
	}

	public void turnOff() {
		this.controller.turnOff();
	}

	public void turnOn() {
		this.controller.turnOn();
	}
}
