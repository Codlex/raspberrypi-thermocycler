package com.codlex.thermocycler.hardware;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import com.codlex.thermocycler.logic.Settings;


public class DallasSensorDS18B20 extends RefreshedSensor<Float> {
	
	private static File deriveValueFile(File sensorFile) {
		return new File(sensorFile, "w1_slave");
	}
	
	private final File sensorFile;

	private final File valueFile;

	DallasSensorDS18B20(File sensorFile) {
		super(Duration.ofMillis(Settings.TemperatureRefreshMillis));
		this.sensorFile = sensorFile;
		this.valueFile = deriveValueFile(sensorFile);
	}

	@Override
	public String getID() {
		return this.sensorFile.getName();
	}

	@Override
	public String toString() {
		return String.format("Sensor ID: %s, Temperature: %2.3fC", getID(),
				getValue());
	}

	@Override
	protected Float recalculateValue() throws FileNotFoundException, IOException {
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
	protected Float getDefaultValue() {
		return 20.0f;
	}
}
