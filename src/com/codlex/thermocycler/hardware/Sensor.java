package com.codlex.thermocycler.hardware;

public interface Sensor<Value extends Number> {

	String getID();

	Value getValue();

	void startMeasuring();
}
