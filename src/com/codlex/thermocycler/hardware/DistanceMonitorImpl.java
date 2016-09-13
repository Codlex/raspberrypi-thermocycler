package com.codlex.thermocycler.hardware;

import java.time.Duration;

import com.codlex.thermocycler.logic.Settings;

/**
 * Class to monitor distance measured by an HC-SR04 distance sensor on a
 * Raspberry Pi.
 *
 * The main method assumes the trig pin is connected to the pin # 7 and the echo
 * pin is connected to pin # 11.  Output of the program are comma separated lines
 * where the first value is the number of milliseconds since unix epoch, and the
 * second value is the measured distance in centimeters.
 */

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;

import javafx.util.Pair;
import lombok.core.Main;
import lombok.extern.log4j.Log4j;

@Log4j
public class DistanceMonitorImpl extends RefreshedSensor<Float> {

	/**
	 * Exception thrown when timeout occurs
	 */
	public static class TimeoutException extends Exception {

		private final String reason;

		public TimeoutException(String reason) {
			this.reason = reason;
		}

		@Override
		public String toString() {
			return this.reason;
		}
	}

	private final static float SOUND_SPEED = 343.2f; // speed of sound in m/s
	private final static int TRIG_DURATION_IN_MICROS = 10; // trigger duration
															// of 10 micro s

	private final static int WAIT_DURATION_IN_MILLIS = 60; // wait 60 milli s

	private final static int TIMEOUT = 2100;

	private final static GpioController gpio = GpioFactory.getInstance();

	private final GpioPinDigitalInput echoPin;

	private final GpioPinDigitalOutput trigPin;

	DistanceMonitorImpl(Pin echoPin, Pin trigPin) {
		super(Duration.ofSeconds(Settings.DistanceRefreshSeconds));
		this.echoPin = gpio.provisionDigitalInputPin(echoPin);
		this.trigPin = gpio.provisionDigitalOutputPin(trigPin);
		this.trigPin.low();
	}

	@Override
	public String getID() {
		// TODO: this is dummy implementations
		return new Pair(this.echoPin.getPin(), this.trigPin.getPin())
				.toString();
	}

	/**
	 * @return the duration of the signal in micro seconds
	 * @throws DistanceMonitorImpl.TimeoutException
	 *             if no low appears in time
	 */
	private long measureSignal() throws TimeoutException {
		int countdown = TIMEOUT;
		long start = System.nanoTime();
		while (this.echoPin.isHigh() && countdown > 0) {
			countdown--;
		}
		long end = System.nanoTime();

		if (countdown <= 0) {
			throw new TimeoutException("Timeout waiting for signal end");
		}

		return (long) Math.ceil((end - start) / 1000.0); // Return micro seconds
	}

	@Override
	public Float recalculateValue() throws TimeoutException {
		this.triggerSensor();
		this.waitForSignal();
		long duration = this.measureSignal();
		float seconds = (float) (1e-6 * duration);
		float meters = (float) ((seconds * SOUND_SPEED) / 2.0);
		return meters * 100;
	}

	/**
	 * Put a high on the trig pin for TRIG_DURATION_IN_MICROS
	 */
	private void triggerSensor() {
		try {
			this.trigPin.high();
			Thread.sleep(0, TRIG_DURATION_IN_MICROS * 1000);
			this.trigPin.low();
		} catch (InterruptedException ex) {
			System.err.println("Interrupt during trigger");

		}
	}

	/**
	 * Wait for a high on the echo pin
	 *
	 * @throws DistanceMonitorImpl.TimeoutException
	 *             if no high appears in time
	 */
	private void waitForSignal() throws TimeoutException {
		int countdown = TIMEOUT;
		while (this.echoPin.isLow() && countdown > 0) {
			countdown--;
		}

		if (countdown <= 0) {
			throw new TimeoutException("Timeout waiting for signal start");
		}
	}

	@Override
	protected Float getDefaultValue() {
		return 0.0f;
	}

}