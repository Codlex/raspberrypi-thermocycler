package com.codlex.thermocycler.logic;

public class Translator {

	Switch power = new Switch(Settings.TranslatorPowerPin, false);
	Switch toCold = new Switch(Settings.TranslatorToColdDirection);
	Switch pulse = new Switch(Settings.TranslatorPulsePin);

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

	void erect() {
		// assume that state is HotBath
		this.power.turnOn();
		this.toCold.turnOn();
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
