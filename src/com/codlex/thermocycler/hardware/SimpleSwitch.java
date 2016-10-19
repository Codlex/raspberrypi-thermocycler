package com.codlex.thermocycler.hardware;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

import lombok.extern.log4j.Log4j;

@Log4j
public class SimpleSwitch implements Switch {

	private enum SwitchState {
		On, Off
	};

	static final PinState ON_CURRENT = PinState.HIGH;
	static final PinState OFF_CURRENT = PinState.LOW;

	private SwitchState state = SwitchState.Off;
	private GpioPinDigitalOutput pin;
	private PinState onCurrent;
	private PinState offCurrent;
	private final String name;
	
	SimpleSwitch(Pin pinNumber, String name) {
		this(pinNumber, name, true);
	}

	public SimpleSwitch(Pin pinNumber, String name, boolean inverse) {
		final GpioController gpio = GpioFactory.getInstance();

		if (inverse) {
			this.onCurrent = OFF_CURRENT;
			this.offCurrent = ON_CURRENT;
		} else {
			this.onCurrent = ON_CURRENT;
			this.offCurrent = OFF_CURRENT;
		}

		this.pin = gpio.provisionDigitalOutputPin(pinNumber,
				getClass().getSimpleName(), this.offCurrent);
		
		// default name
		this.name = name;
	}

	@Override
	public void turnOff() {
		if (this.state != SwitchState.Off) {
			this.state = SwitchState.Off;
			this.pin.setState(this.offCurrent);
			log.debug(this.name + " turned off.");
		}
	}

	@Override
	public void turnOn() {
		if (this.state != SwitchState.On) {
			this.state = SwitchState.On;
			this.pin.setState(this.onCurrent);
			log.debug(this.name + " turned on.");
		}
	}
}
