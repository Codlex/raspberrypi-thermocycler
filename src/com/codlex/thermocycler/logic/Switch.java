package com.codlex.thermocycler.logic;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

public class Switch {

	private enum SwitchState {
		On, Off
	};

	static final PinState ON_CURRENT = PinState.HIGH;
	static final PinState OFF_CURRENT = PinState.LOW;

	private SwitchState state = SwitchState.Off;
	private GpioPinDigitalOutput pin;
	private PinState onCurrent;
	private PinState offCurrent;

	public Switch(Pin pinNumber) {
		this(pinNumber, true);
	};

	public Switch(Pin pinNumber, boolean inverse) {
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
	};

	protected void off() {

	}

	protected void on() {

	}

	public void turnOff() {
		if (this.state != SwitchState.Off) {
			this.state = SwitchState.Off;
			this.pin.setState(this.offCurrent);
			off();
		}
	}

	public void turnOn() {
		if (this.state != SwitchState.On) {
			this.state = SwitchState.On;
			this.pin.setState(this.onCurrent);
			on();
		}
	}
}
