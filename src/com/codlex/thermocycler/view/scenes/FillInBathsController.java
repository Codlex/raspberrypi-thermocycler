package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.Thermocycler;
import com.codlex.thermocycler.logic.bath.sensors.LevelSensor;
import com.codlex.thermocycler.view.ThermocyclerController;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.util.converter.NumberStringConverter;

public class FillInBathsController implements ThermocyclerController {
	
	@FXML
	private Label hotBathLevelLabel;
	
	@FXML
	private Label coldBathLevelLabel;
	
	@FXML
	private ProgressBar hotBathLevelProgressBar;
	
	@FXML
	private ProgressBar coldBathLevelProgressBar;
	
	
	public void setModel(Thermocycler thermocycler) {
		LevelSensor hotBathLevel  = thermocycler.getHotBath().getLevelSensor();
		LevelSensor coldBathLevel  = thermocycler.getColdBath().getLevelSensor();
		
		bind(hotBathLevel, this.hotBathLevelLabel, this.hotBathLevelProgressBar);
		bind(coldBathLevel, this.coldBathLevelLabel, this.coldBathLevelProgressBar);
	}

	private static void bind(LevelSensor sensor, Label label, ProgressBar progressBar) {
		label.textProperty().bindBidirectional(sensor.getProperty(), new NumberStringConverter());
		progressBar.progressProperty().bindBidirectional(sensor.getDoubleProperty());
	}

}
