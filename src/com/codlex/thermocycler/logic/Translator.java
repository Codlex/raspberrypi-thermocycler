package com.codlex.thermocycler.logic;

import com.codlex.thermocycler.hardware.HardwareProvider;
import com.codlex.thermocycler.hardware.Switch;

public class Translator {

	Switch power = HardwareProvider.get().getSwitch(Settings.get().getTranslatorPowerPin(), "TranslatorPower",
			false);
	Switch toCold = HardwareProvider.get()
			.getSwitch(Settings.get().getTranslatorToColdDirection(), "TranslatorToColdSwitch");
	Switch pulse = HardwareProvider.get()
			.getSwitch(Settings.get().getTranslatorPulsePin(), "TranslatorPulse");

	State currentState = State.NotReady;

	public Translator() {
		this.power.turnOn();
	}

	void changeState(State state) {
		this.currentState = state;

		switch (this.currentState) {
			case ColdBath :
				goToCold();
				break;
			case HotBath :
				goToHot();
				break;
		}
	}

	public void clear() {
		this.power.turnOff();
		this.toCold.turnOff();
		this.pulse.turnOff();
	}

	public void errect(State from) {
		this.power.turnOn();
		if (State.HotBath.equals(from)) {
			this.toCold.turnOn();
		} else {
			this.toCold.turnOff();
		}
		this.pulse.turnOn();
		try {
			Thread.sleep(750);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.pulse.turnOff();
		this.power.turnOff();

	}

	void goToCold() {
		this.power.turnOn();
		this.toCold.turnOn();
		this.pulse.turnOn();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.pulse.turnOff();
	}

	void goToHot() {
		this.power.turnOn();
		this.toCold.turnOff();
		this.pulse.turnOn();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.pulse.turnOff();
	}

	void update(State state) {

		// not_ready . hot_bath
		// hot_bath . cold_bath
		// cold_bath . hot_bath
		// cold_bath . finished
		if (state != this.currentState) {
			changeState(state);
		}
	}
}
