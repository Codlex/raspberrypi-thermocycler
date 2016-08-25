package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.view.ThermocyclerController;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ThermocyclerOverviewController extends ThermocyclerController {
	
	@FXML
	private Label title;
	
	@FXML
	private Label hotBathTemperature;
	
	@FXML
	private Label coldBathTemperature;
	
	@FXML
	private Label hotBathTime;
	
	@FXML
	private Label coldBathTime;
	
	@FXML
	private Label cycles;

	private boolean isWarmup;
	
	private static final String temperatureSensorFormat = "%.0f°C / %s";

	private static final String temperatureFormat = "%d°C";
	
	private static final String timeFormat = "%d:%02d";
	
	@Override
	protected void onModelInitialized() {
		this.thermocycler.getColdBath().getCurrentTemperatureProperty().addListener((a) -> {
			System.out.println("executed listener");

			updateUI();
		});
		
		this.thermocycler.getHotBath().getCurrentTemperatureProperty().addListener((a) -> {
			System.out.println("executed listener");

			updateUI();
		});
	}
	
	private String okStyle = "-fx-text-fill: green";
	private String notOkStyle = "-fx-text-fill: red";

	@Override
	protected void onUpdateUI() {
		Bath hotBath = this.thermocycler.getHotBath();
		this.hotBathTemperature.textProperty().set(String.format(temperatureFormat, hotBath.getTemperatureProperty().get()));
		int hotTime = hotBath.getTimeProperty().get();
		this.hotBathTime.textProperty().set(String.format(timeFormat, hotTime / 60, hotTime % 60));
		
		Bath coldBath = this.thermocycler.getColdBath();
		this.coldBathTemperature.textProperty().set(String.format(temperatureFormat, coldBath.getTemperatureProperty().get()));
		int coldTime = coldBath.getTimeProperty().get();
		this.coldBathTime.textProperty().set(String.format(timeFormat, coldTime / 60, coldTime % 60));
		
		this.cycles.textProperty().set(Integer.toString(this.thermocycler.getCycles().get()));
		
		if (this.isWarmup) {
			System.out.println("executed warmup");
			StringProperty coldBathTemperatureProperty = this.coldBathTemperature.textProperty();
			float currentColdBathTemperature = this.thermocycler.getColdBath().getCurrentTemperatureProperty().get();
			coldBathTemperatureProperty.set(String.format(temperatureSensorFormat, currentColdBathTemperature, coldBathTemperatureProperty.get()));
			this.coldBathTemperature.setStyle(coldBath.isTemperatureOK() ? okStyle : notOkStyle);
			
			StringProperty hotBathTemperatureProperty = this.hotBathTemperature.textProperty();
			float currenthotBathTemperature = this.thermocycler.getHotBath().getCurrentTemperatureProperty().get();
			hotBathTemperatureProperty.set(String.format(temperatureSensorFormat, currenthotBathTemperature, hotBathTemperatureProperty.get()));
			this.hotBathTemperature.setStyle(hotBath.isTemperatureOK() ? okStyle : notOkStyle);

		}
	}
	
	@Override
	protected void onNextClick() {
		this.title.setText("Thermocycler is warming up...");
		this.nextButton.setVisible(false);
		this.isWarmup = true;
		
		updateUI();
		
		this.thermocycler.start();
	}
}
