package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.view.ThermocyclerController;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class AbstractBathController extends ThermocyclerController {

	@FXML
	private Slider temperatureSlider;
	
	@FXML
	private Label temperatureLabel;
	
	@FXML
	private Button minusOneCelzius;
	
	@FXML
	private Button plusOneCelzius;
	
	@FXML
	private Label timeLabel;
	
	@FXML
	private Slider timeSlider;
	
	@FXML
	private Button minusOneSecond;
	
	@FXML
	private Button plusOneSecond;
	
	private Bath bath;
	
	@Override
	protected void onModelInitialized() {
		this.bath = getBath();
		this.temperatureSlider.valueProperty().bindBidirectional(bath.getTemperatureProperty());
		this.bath.getTemperatureProperty().addListener((newValue) -> {

			IntegerProperty property = (IntegerProperty) newValue;
			Integer value = property.get();
			updateTemperature(value);
			updateUI();
		});
		updateTemperature(this.bath.getTemperatureProperty().get());
		
		this.timeSlider.valueProperty().bindBidirectional(bath.getTimeProperty());
		this.bath.getTimeProperty().addListener((newValue) -> {
			IntegerProperty property = (IntegerProperty) newValue;
			Integer value = property.get();
			updateTime(value);
			updateUI();
		});
		updateTime(this.bath.getTimeProperty().get());
	}

	protected abstract Bath getBath();

	private void updateTemperature(Integer value) {
		this.temperatureLabel.textProperty().set(String.format("%d°C", value));
	}

	private void updateTime(Integer value) {
		this.timeLabel.textProperty().set(String.format("%d:%02d", value / 60, value % 60));
	}
	
	@Override
	protected void onUpdateUI() {
		this.minusOneCelzius.setDisable(this.bath.getTemperatureProperty().get() < (int) this.temperatureSlider.getMin() + 1);
		this.plusOneCelzius.setDisable(this.bath.getTemperatureProperty().get() > (int) this.temperatureSlider.getMax() - 1);

		this.minusOneSecond.setDisable(this.bath.getTimeProperty().get() < (int) this.timeSlider.getMin() + 1);
		this.plusOneSecond.setDisable(this.bath.getTimeProperty().get() > (int) this.timeSlider.getMax() - 1);
		
	}
	
	@FXML
	private void onMinusOneCelziusClick() {
		decrement(this.bath.getTemperatureProperty());
	}
	
	@FXML
	private void onPlusOneCelziusClick() {
		increment(this.bath.getTemperatureProperty());
	}
	
	@FXML
	private void onMinusOneSecondClick() {
		decrement(this.bath.getTimeProperty());
	}

	@FXML
	private void onPlusOneSecondClick() {
		increment(this.bath.getTimeProperty());
	}
	
	private static void increment(IntegerProperty property) {
		property.set(property.get() + 1);
	}
	
	private static void decrement(IntegerProperty property) {
		property.set(property.get() - 1);
	}
}
