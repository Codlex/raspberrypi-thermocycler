package com.codlex.thermocycler.view;

import com.codlex.thermocycler.hardware.VirtualSensors;
import com.codlex.thermocycler.hardware.VirtualSwitches;
import com.codlex.thermocycler.logic.Thermocycler;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.FloatProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.log4j.Log4j;

@Log4j
public class TestController {

	@FXML
	private Slider hotBathTemperature1Slider;

	@FXML
	private TextField hotBathTemperature1Field;

	@FXML
	private Slider hotBathTemperature2Slider;

	@FXML
	private TextField hotBathTemperature2Field;

	@FXML
	private ToggleButton hotBathHeater;

	@FXML
	private Slider coldBathTemperature1Slider;
	
	@FXML
	private TextField coldBathTemperature1Field;
	
	@FXML
	private Slider coldBathTemperature2Slider;
	
	@FXML
	private TextField coldBathTemperature2Field;

	@FXML
	private Slider coldBathTemperatureAntifrizSlider;

	@FXML
	private TextField coldBathTemperatureAntifrizField;

	@FXML
	private ToggleButton coldBathCooler;

	private Thermocycler thermocycler;

	@FXML
	private TextField cycles;

	public void bind() {		
		bind(VirtualSensors.get().hotBathTemperature1, this.hotBathTemperature1Slider, this.hotBathTemperature1Field);
		bind(VirtualSensors.get().hotBathTemperature2, this.hotBathTemperature2Slider, this.hotBathTemperature2Field);
		bind(VirtualSwitches.get().hotBathHeater, this.hotBathHeater);
		
		bind(VirtualSensors.get().coldBathTemperature1, this.coldBathTemperature1Slider, this.coldBathTemperature1Field);
		bind(VirtualSensors.get().coldBathTemperature2, this.coldBathTemperature2Slider, this.coldBathTemperature2Field);
		bind(VirtualSensors.get().coldBathTemperatureAntifriz, this.coldBathTemperatureAntifrizSlider, this.coldBathTemperatureAntifrizField);
		bind(VirtualSwitches.get().coldBathCooler, this.coldBathCooler);
	}

	private void bind(BooleanProperty sensor, ToggleButton button) {
		button.selectedProperty().bindBidirectional(sensor);
	}

	private static void bind(FloatProperty sensor, Slider slider, TextField field) {
		slider.valueProperty().bindBidirectional(sensor);
		field.textProperty().bindBidirectional(sensor, new NumberStringConverter());
	}

	public void setModel(Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
	}

	@FXML
	private void start() {
		log.debug("start clicked");
		this.thermocycler.start();
	}
}
