package com.codlex.thermocycler.hardware;

import java.util.HashMap;
import java.util.Map;

import com.codlex.thermocycler.logic.Settings;
import com.pi4j.io.gpio.Pin;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;

@Log4j
public class VirtualSwitches {
	private static final VirtualSwitches INSTANCE = new VirtualSwitches();

	public static final VirtualSwitches get() {
		return INSTANCE;
	}
	public final BooleanProperty coldBathCooler = new SimpleBooleanProperty();
	public final BooleanProperty coldBathWaterPump = new SimpleBooleanProperty();
	public final BooleanProperty hotBathCirculationWaterPump = new SimpleBooleanProperty();
	public final BooleanProperty hotBathHeater = new SimpleBooleanProperty();
	public final BooleanProperty hotBathWaterPump = new SimpleBooleanProperty();
	public final BooleanProperty translatorPower = new SimpleBooleanProperty();
	public final BooleanProperty translatorPulse = new SimpleBooleanProperty();

	private final BooleanProperty translatorToCold = new SimpleBooleanProperty();

	private final Map<Pin, Switch> switches = new HashMap<>();

	private VirtualSwitches() {
		addSwitch(Settings.get().getColdBathCoolerPin(), this.coldBathCooler, "Cooler");
		addSwitch(Settings.get().getColdBathWaterPump(), this.coldBathWaterPump, "ColdBathWaterPump");
		addSwitch(Settings.get().getHotBathCirculationWaterPump(),
				this.hotBathCirculationWaterPump, "HotBathCircularPump");
		addSwitch(Settings.get().getHotBathHeaterPin(), this.hotBathHeater, "Heater");
		addSwitch(Settings.get().getHotBathWaterPump(), this.hotBathWaterPump, "HotBathWaterPump");
		addSwitch(Settings.get().getTranslatorPowerPin(), this.translatorPower, "TranslatorPower");
		addSwitch(Settings.get().getTranslatorPulsePin(), this.translatorPulse, "TranslatorPulse");
		addSwitch(Settings.get().getTranslatorToColdDirection(), this.translatorToCold, "TranslatorToCold");
	}

	private void addSwitch(Pin pin, BooleanProperty property, String name) {
		this.switches.put(pin, new Switch() {
			@Override
			public void turnOff() {
				if (property.get()) {
					log.debug(name + " turned off.");
				}
				property.set(false);
			}

			@Override
			public void turnOn() {
				if (!property.get()) {
					log.debug(name + " turned on.");
				}
				property.set(true);
			}
		});
	}

	public Switch getSwitch(Pin pin) {
		return this.switches.get(pin);
	}
}
