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
			case ToHotBathPause:
				errectFrom(State.ColdBath);
				break;
			case ToColdBathPause:
				errectFrom(State.HotBath);
				break;
			case ToHotBathMiddlePause:
				errectToMiddleFrom(State.ColdBath);
				break;
			case ToColdBathMiddlePause:
				errectToMiddleFrom(State.HotBath);
				break;
		}
	}

	public void clear() {
		this.power.turnOff();
		this.toCold.turnOff();
		this.pulse.turnOff();
	}

	public void errectToMiddleFrom(State from) {
		this.power.turnOn();
		if (State.HotBath.equals(from)) {
			this.toCold.turnOn();
		} else {
			this.toCold.turnOff();
		}
		this.pulse.turnOn();
		try {
			if (State.HotBath.equals(from)) {
				Thread.sleep(Settings.get().getMiddlePausePulseDurationFromHot());
			} else {
				Thread.sleep(Settings.get().getMiddlePausePulseDurationFromCold());
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.pulse.turnOff();
		this.power.turnOff();
	}
	
	public void errectFrom(State from) {
		this.power.turnOn();
		if (State.HotBath.equals(from)) {
			this.toCold.turnOn();
		} else {
			this.toCold.turnOff();
		}
		this.pulse.turnOn();
		try {
			Thread.sleep(450);
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
			Thread.sleep(1000);
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
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.pulse.turnOff();
	}

	void update(State state) {

		// not_ready . hot_bath
		// hot_bath . cold_bath if there is pause, it will go to to_cold_bath_pause
		// cold_bath . hot_bath if there is pause, it will go to to_hot_bath_pause
		// cold_bath . finished
		if (state != this.currentState) {
			changeState(state);
		}
	}
}
