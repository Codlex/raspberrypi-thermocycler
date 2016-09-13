package com.codlex.thermocycler.hardware;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import com.codlex.thermocycler.logic.Settings;
import com.pi4j.io.gpio.Pin;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.util.Pair;

public class VirtualSensors {

	private static final VirtualSensors INSTANCE = new VirtualSensors();

	public static VirtualSensors get() {
		return INSTANCE;
	}
	
	public FloatProperty coldBathTemperature1 = new SimpleFloatProperty(20);
	public FloatProperty coldBathTemperature2 = new SimpleFloatProperty(20);
	public FloatProperty coldBathTemperatureAntifriz = new SimpleFloatProperty(
			20);

	public FloatProperty coldBathDistance = new SimpleFloatProperty(3);
	public FloatProperty hotBathTemperature1 = new SimpleFloatProperty(20);
	public FloatProperty hotBathTemperature2 = new SimpleFloatProperty(20);
	public FloatProperty hotBathTemperatureSafety = new SimpleFloatProperty(
			20);

	public FloatProperty hotBathDistance = new SimpleFloatProperty(3);

	private final Map<String, Sensor<Float>> temperatureSensors = new HashMap<>();

	private final Map<Pair<Pin, Pin>, Sensor<Float>> distanceMonitors = new HashMap<>();

	private VirtualSensors() {

		addTemperatureSensor(Settings.ColdBathTemperatureSensor1,
				this.coldBathTemperature1);
		addTemperatureSensor(Settings.ColdBathTemperatureSensor2,
				this.coldBathTemperature2);
		addTemperatureSensor(Settings.ColdBathTemperatureSensorAntifriz,
				this.coldBathTemperatureAntifriz);

		addTemperatureSensor(Settings.HotBathTemperatureSensor1,
				this.hotBathTemperature1);
		addTemperatureSensor(Settings.HotBathTemperatureSensor2,
				this.hotBathTemperature2);
		addTemperatureSensor(Settings.HotBathTemperatureSensor3,
				this.hotBathTemperatureSafety);

		addDistanceMonitor(Settings.ColdBathLevelEchoPin,
				Settings.ColdBathLevelTriggerPin, this.coldBathDistance);
		addDistanceMonitor(Settings.HotBathLevelEchoPin,
				Settings.HotBathLevelTriggerPin, this.hotBathDistance);

	}

	private void addDistanceMonitor(Pin echo, Pin trigger,
			FloatProperty distance) {
		final Pair<Pin, Pin> id = new Pair<>(echo, trigger);
		this.distanceMonitors.put(id, new RefreshedSensor<Float>(Duration.ofMillis(Settings.DistanceRefreshMillis)) {

			@Override
			public String getID() {
				return id.toString();
			}

			@Override
			protected Float recalculateValue() throws Exception {
				// simulate duration of measuring
				// Thread.sleep(ThreadLocalRandom.current().nextLong(1500));
				return distance.get();
			}
			
			@Override
			protected Float getDefaultValue() {
				return 0f;
			}
			
		});
	}

	private void addTemperatureSensor(final String id,
			final FloatProperty property) {

		this.temperatureSensors.put(id, new RefreshedSensor<Float>(Duration.ofMillis(Settings.TemperatureRefreshMillis)) {

			@Override
			public String getID() {
				return id;
			}

			@Override
			protected Float recalculateValue() throws Exception {
				// simulate duration of measuring
				// Thread.sleep(ThreadLocalRandom.current().nextLong(1500));
				return property.get();
			}

			@Override
			protected Float getDefaultValue() {
				return 20f;
			}
		});

	}

	public Sensor<Float> getDistanceSensor(Pin echo, Pin trigger) {
		return this.distanceMonitors.get(new Pair<Pin, Pin>(echo, trigger));
	}

	public Sensor<Float> getTemperatureSensorById(String id) {
		return this.temperatureSensors.get(id);
	}

}
