package com.codlex.thermocycler.logic;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.google.common.collect.Range;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiPin;

import lombok.extern.log4j.Log4j;

@Log4j
public class Settings {

	private static final Settings INSTANCE = new Settings();

	public boolean getTrackingOn() {
		return getValue(Boolean.class);
	}
	
	public long getTranslationTimeMillis() {
		return getValue(Long.class);
	}

	public int getDistanceRefreshMillis() {
		return getValue(Integer.class);
	}

	public int getTemperatureRefreshMillis() {
		return getValue(Integer.class);
	}

	/**
	 * Bath.
	 */
	public int getBathMinimumLevel() {
		return getValue(Integer.class);
	}

	public int getBathDepth() {
		return getValue(Integer.class);
	}

	public int getLevelEpsilon() {
		return getValue(Integer.class);
	}

	public int getTemperatureEpsilon() {
		return getValue(Integer.class);
	}

	/**
	 * Hot Bath.
	 */

	public Pin getHotBathLevelEchoPin() {
		return getValue(Pin.class);
	}

	public Pin getHotBathLevelTriggerPin() {
		return getValue(Pin.class);
	}

	public Pin getHotBathHeaterPin() {
		return getValue(Pin.class);
	}

	public Pin getHotBathWaterPump() {
		return getValue(Pin.class);
	}

	public Pin getHotBathCirculationWaterPump() {
		return getValue(Pin.class);
	}
	
	public long getFromHotBathPause() {
		return getValue(Long.class);
	}

	/**
	 * ColdBath.
	 */
	public Pin getColdBathLevelEchoPin() {
		return getValue(Pin.class);
	}

	public Pin getColdBathLevelTriggerPin() {
		return getValue(Pin.class);
	}

	public Pin getColdBathCoolerPin() {
		return getValue(Pin.class);
	}

	public Pin getColdBathWaterPump() {
		return getValue(Pin.class);
	}
	
	public long getFromColdBathPause() {
		return getValue(Long.class);
	}

	/**
	 * Translator
	 */
	public Pin getTranslatorPowerPin() {
		return getValue(Pin.class);
	}

	public Pin getTranslatorPulsePin() {
		return getValue(Pin.class);
	}

	public Pin getTranslatorToColdDirection() {
		return getValue(Pin.class);
	}

	public static String HotBathTemperatureSensor1 = "28-0417838278ff";
	public static String HotBathTemperatureSensor2 = "28-051790ae46ff";
	public static String HotBathTemperatureSensor3 = "28-0417838287ff";
	public static String ColdBathTemperatureSensor1 = "28-04178380a8ff";
	public static String ColdBathTemperatureSensor2 = "28-0517907c6dff";
	public static String ColdBathTemperatureSensorAntifriz = "28-0417837701ff";

	/**
	 * Validation
	 */
	@SuppressWarnings("unchecked")
	public Range<Integer> getValidationCyclesRange() {
		return getValue(Range.class);
	}

	@SuppressWarnings("unchecked")
	public Range<Integer> getValidationHotTemperatureRange() {
		return getValue(Range.class);
	}

	@SuppressWarnings("unchecked")
	public Range<Integer> getValidationColdTemperatureRange() {
		return getValue(Range.class);
	}

	@SuppressWarnings("unchecked")
	public Range<Integer> getValidationTimeRange() {
		return getValue(Range.class);
	}

	/**
	 * Safety
	 */

	public boolean getSafety() {
		return getValue(Boolean.class);
	}

	public float getSafetyHotBathTemperatureMax() {
		return getValue(Float.class);
	}

	public int getSafetyLevelMin() {
		return getValue(Integer.class);
	}

	public int getSafetyLevelMax() {
		return getValue(Integer.class);
	}

	public static final Settings get() {
		return INSTANCE;
	}

	public final Properties properties = new Properties();

	private Settings() {
		try {
			this.properties.load(new FileInputStream("settings.properties"));
		} catch (IOException e) {
			log.error(e);
		}
	}

	public boolean getFullScreen() {
		return getValue(Boolean.class);
	}

	private String getMethodName() {
		final int depth = 3;
		return Thread.currentThread().getStackTrace()[depth].getMethodName().replace("get", ""); // delete
																									// get
																									// part
	}

	public boolean getProduction() {
		return getValue(Boolean.class);
	}

	/**
	 * This method takes name of calling method and by it gets value from
	 * properties file.
	 */
	private <T> T getValue(Class<T> expectedClass) {
		final String key = getMethodName();
		final String textValue = this.properties.getProperty(key);

		// casting
		switch (expectedClass.getSimpleName()) {
		case "Integer":
			return expectedClass.cast(Integer.parseInt(textValue));
		case "Boolean":
			return expectedClass.cast(Boolean.parseBoolean(textValue));
		case "Float":
			return expectedClass.cast(Float.parseFloat(textValue));
		case "Long":
			return expectedClass.cast(Long.parseLong(textValue));
		case "Pin":
			return expectedClass.cast(RaspiPin.getPinByAddress(Integer.parseInt(textValue)));
		case "Range":
			String[] splitted = textValue.split(",");
			return expectedClass.cast(Range.closed(Integer.parseInt(splitted[0].trim()), Integer.parseInt(splitted[1].trim())));
		}

		return null;
	}

	public float getColdBathEpsOn() {
		return getValue(Float.class);
	}

	public float getColdBathEpsOff() {
		return getValue(Float.class);
	}

	public boolean getCirculationPumpOn() {
		return getValue(Boolean.class);
	}

	public boolean getKeepLevelOn() {
		return getValue(Boolean.class);
	}

	public long getMiddlePausePulseDurationFromHot() {
		return getValue(Long.class);
	}

	public long getMiddlePausePulseDurationFromCold() {
		return getValue(Long.class);
	}

	public long getFromColdBathMiddlePause() {
		return getValue(Long.class);
	}
	
	public long getFromHotBathMiddlePause() {
		return getValue(Long.class);
	}


}
