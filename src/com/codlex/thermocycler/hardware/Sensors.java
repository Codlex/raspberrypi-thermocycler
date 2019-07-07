package com.codlex.thermocycler.hardware;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.extern.log4j.Log4j;

/**
 * Main API entry point. Usage: {@link Sensors#getSensors()}.
 *
 * @author Marcus Hirt
 */
@Log4j
class Sensors {
	private static void addDallasSensors(Set<Sensor<Float>> sensors)
			throws IOException {
		File sensorFolder = new File("/sys/bus/w1/devices");
		if (!sensorFolder.exists()) {
			System.err.println(
					"Modules w1-gpio do not seem to be enabled. If you want to use DS18[B|S]20 sensors, please enable them.");
			return;
		}
		if (sensorFolder.list().length == 0) {
			System.err.println(
					"The w1-gpio module seem to be set up, but no DS18[B|S]20 devices were found. If no such devices are connected, this is fine. Otherwise please check to see that w1-therm is loaded, that you have added dtoverlay=w1-gpio to your /boot/config.txt, and that the devices are properly connected.");
			return;
		}
		for (File f : sensorFolder.listFiles()) {
			if (f.getName().startsWith("w1_bus_master")) {
				continue;
			}
			DallasTemperatureSensor sensor = new DallasTemperatureSensor(f);
			sensors.add(sensor);
		}
	}

	public static Optional<Sensor<Float>> getSensorById(final String id) {
		try {
			return getSensors().stream()
					.filter((sensor) -> id.equals(sensor.getID())).findFirst();
		} catch (IOException e) {
			log.error("Sensor not found", e);
			return Optional.empty();
		}
	}

	public static Set<Sensor<Float>> getSensors() throws IOException {
		Set<Sensor<Float>> sensors = new HashSet<Sensor<Float>>();
		addDallasSensors(sensors);
		return sensors;
	}
}
