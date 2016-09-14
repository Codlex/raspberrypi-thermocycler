package com.codlex.thermocycler.logic;

import com.google.common.collect.Range;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

public class Settings {
	
	public static final boolean Production = true;
	public static final boolean FullScreen = true;
	public static final long TranslationTimeMillis = 1500;
	
	public static int DistanceRefreshMillis = 1000;
	public static int TemperatureRefreshMillis = 1000;
	
	/**
	 * Bath.
	 */
	public static int BathMinimumLevel = 60;
	public static int BathDepth = 20;
	public static int LevelEpsilon = 1;
	public static int TemperatureEpsilon = 1;

	/**
	 * Hot Bath.
	 */
	public static Pin HotBathLevelEchoPin = RaspiPin.GPIO_01;
	public static Pin HotBathLevelTriggerPin = RaspiPin.GPIO_00;
	public static Pin HotBathHeaterPin = RaspiPin.GPIO_29;
	public static Pin HotBathWaterPump = RaspiPin.GPIO_27;
	public static Pin HotBathCirculationWaterPump = RaspiPin.GPIO_25;
	


	// indices for one wire

	/**
	 * ColdBath.
	 */
	public static Pin ColdBathLevelEchoPin = RaspiPin.GPIO_04;
	public static Pin ColdBathLevelTriggerPin = RaspiPin.GPIO_03;
	public static Pin ColdBathCoolerPin = RaspiPin.GPIO_21;
	public static Pin ColdBathWaterPump = RaspiPin.GPIO_23;

	/**
	 * Translator
	 */
	public static Pin TranslatorPowerPin = RaspiPin.GPIO_28;
	public static Pin TranslatorPulsePin = RaspiPin.GPIO_22;
	public static Pin TranslatorToColdDirection = RaspiPin.GPIO_24;

	/**
	 * Logger.
	 */
	public static int SDSelectChip = 53;
	public static String LogFile = "debug2.log";
	public static boolean LogToFile = true;
	public static boolean LogToSerial = true;

	public static String HotBathTemperatureSensor1 = "28-000006cc1f5e";
	public static String HotBathTemperatureSensor2 = "28-0215030e8cff";
	public static String HotBathTemperatureSensor3 = "28-031504074bff";
	public static String ColdBathTemperatureSensor1 = "28-021502e596ff";
	public static String ColdBathTemperatureSensor2 = "28-02150310a9ff";
	public static String ColdBathTemperatureSensorAntifriz = "28-031635f7afff";
	
	/**
	 * Validation
	 */
	public static Range<Integer> ValidationCyclesRange = Range.closed(1, 999999);
	public static Range<Integer> ValidationHotTemperatureRange = Range.closed(20, 100);
	public static Range<Integer> ValidationColdTemperatureRange = Range.closed(-6, 20);
	public static Range<Integer> ValidationTimeRange = Range.closed(1, 300);
	
	/**
	 * Safety
	 */
	public static final float SafetyHotBathTemperatureMax = 120;
	public static final int SafetyLevelMin = 30;
	public static final int SafetyLevelMax = 95;
	
}
