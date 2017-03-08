package com.codlex.thermocycler.hardware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;

import com.codlex.thermocycler.logic.Settings;

public class DallasTemperatureSensor extends RefreshedSensor<Float> {

	private static File deriveValueFile(File sensorFile) {
		return new File(sensorFile, "w1_slave");
	}

	private final File sensorFile;

	private final File valueFile;

	DallasTemperatureSensor(File sensorFile) {
		super(Duration.ofMillis(Settings.get().getTemperatureRefreshMillis()));
		this.sensorFile = sensorFile;
		this.valueFile = deriveValueFile(sensorFile);
	}

	@Override
	protected Float getDefaultValue() {
		return 20.0f;
	}

	@Override
	public String getID() {
		return this.sensorFile.getName();
	}

	@Override
	protected Float recalculateValue()
			throws FileNotFoundException, IOException {
		try (BufferedReader reader = new BufferedReader(
				new FileReader(this.valueFile))) {
			String tmp = reader.readLine();
			int index = -1;
			while (tmp != null) {
				index = tmp.indexOf("t=");
				if (index >= 0) {
					break;
				}
				tmp = reader.readLine();
			}

			if (index < 0) {
				throw new IOException("Could not read sensor " + getID());
			}

			return Integer.parseInt(tmp.substring(index + 2)) / 1000f;
		}
	}

	@Override
	public String toString() {
		return String.format("Sensor ID: %s, Temperature: %2.3fC", getID(),
				getValue());
	}
}
