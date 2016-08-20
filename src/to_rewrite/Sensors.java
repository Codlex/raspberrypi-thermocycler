/*
 *  Copyright (C) 2013 Marcus Hirt
 *                     www.hirt.se
 *
 * This software is free:
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESSED OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright (C) Marcus Hirt, 2013
 */
package to_rewrite;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.Getter;
import lombok.extern.log4j.Log4j;

/**
 * Main API entry point. Usage: {@link Sensors#getSensors()}.
 *
 * @author Marcus Hirt
 */
@Log4j
public class Sensors {
	private static void addDallasSensors(Set<Sensor> sensors)
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
			DallasSensorDS18B20 sensor = new DallasSensorDS18B20(f);
			sensors.add(sensor);
		}
	}

	public static Optional<Sensor> getSensorById(final String id) {
		try {
			return getSensors().stream()
					.filter((sensor) -> id.equals(sensor.getID())).findFirst();
		} catch (IOException e) {
			log.error("Sensor not found", e);
			return Optional.empty();
		}
	}

	public static Set<Sensor> getSensors() throws IOException {
		Set<Sensor> sensors = new HashSet<Sensor>();
		addDallasSensors(sensors);
		return sensors;
	}

	/**
	 * Simple example, printing the number of sensors found, and then the
	 * identity and value of each sensor, sleeping a second between each sensor
	 * reading.
	 *
	 * @param args
	 *            not used.
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		Set<Sensor> sensors = Sensors.getSensors();
		System.out.println(String.format("Found %d sensors!", sensors.size()));
		while (true) {
			for (Sensor sensor : sensors) {
				System.out.println(String.format("%s(%s):%3.2f%s",
						sensor.getPhysicalQuantity(), sensor.getID(),
						sensor.getValue(), sensor.getUnitString()));
				Thread.sleep(1000);
			}
			System.out.println("");
			System.out.flush();
		}
	}

	@Getter
	int test;
}
