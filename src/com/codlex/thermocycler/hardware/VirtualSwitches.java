package com.codlex.thermocycler.hardware;

import java.util.HashMap;
import java.util.Map;

import com.codlex.thermocycler.logic.Settings;
import com.pi4j.io.gpio.Pin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class VirtualSwitches {
	private static final VirtualSwitches INSTANCE = new VirtualSwitches();

	public static final VirtualSwitches get() {
		return INSTANCE;
	}
	private final BooleanProperty codlBathCooler = new SimpleBooleanProperty();
	private final BooleanProperty coldBathWaterPump = new SimpleBooleanProperty();
	private final BooleanProperty hotBathCirculationWaterPump = new SimpleBooleanProperty();
	private final BooleanProperty hotBathHeater = new SimpleBooleanProperty();
	private final BooleanProperty hotBathWaterPump = new SimpleBooleanProperty();
	private final BooleanProperty translatorPower = new SimpleBooleanProperty();
	private final BooleanProperty translatorPulse = new SimpleBooleanProperty();

	private final BooleanProperty translatorToCold = new SimpleBooleanProperty();

	private final Map<Pin, Switch> switches = new HashMap<>();

	private VirtualSwitches() {
		addSwitch(Settings.ColdBathCoolerPin, this.codlBathCooler);
		addSwitch(Settings.ColdBathWaterPump, this.coldBathWaterPump);
		addSwitch(Settings.HotBathCirculationWaterPump,
				this.hotBathCirculationWaterPump);
		addSwitch(Settings.HotBathHeaterPin, this.hotBathHeater);
		addSwitch(Settings.HotBathWaterPump, this.hotBathWaterPump);
		addSwitch(Settings.TranslatorPowerPin, this.translatorPower);
		addSwitch(Settings.TranslatorPulsePin, this.translatorPulse);
		addSwitch(Settings.TranslatorToColdDirection, this.translatorToCold);
	}

	private void addSwitch(Pin pin, BooleanProperty property) {
		this.switches.put(pin, new Switch() {

			@Override
			public void turnOff() {
				property.set(false);
			}

			@Override
			public void turnOn() {
				property.set(true);
			}
		});
	}
	
	
	public Switch getSwitch(Pin pin) {
		return this.switches.get(pin);
	}
}
