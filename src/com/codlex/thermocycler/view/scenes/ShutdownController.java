package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.view.ThermocyclerController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShutdownController extends ThermocyclerController {	
	
	
	@FXML
	private Label message;
	
	@Override
	protected void onModelInitialized() {
		this.nextButton.setText("Shutdown");
	}
	
	@Override
	protected void onNextClick() {
		this.thermocycler.lowerTranslator();
		this.message.setText("You may now shutdown Thermocycler.");
		System.exit(0);
	}
}
