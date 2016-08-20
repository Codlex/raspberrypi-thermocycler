package com.codlex.thermocycler.logic;

import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class RaspiPinsExtened extends RaspiPin {
	public static Pin pin = createDigitalPin(40, "GPIO 40");
}
