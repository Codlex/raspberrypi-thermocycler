package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.view.ThermocyclerController;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class ShutdownController extends ThermocyclerController {	
	
	
	@FXML
	private Label message;
	private boolean alreadyClicked;
	
	@Override
	protected String getNextLabel() {
		return "Shutdown";
	}
	
	@Override
	protected void onNextClick() {
		if (this.alreadyClicked) {
			System.exit(0);
		}
		this.thermocycler.lowerTranslator();
		this.message.setText("You may now shutdown Thermocycler.");
		this.alreadyClicked = true;
	}
	
	@Override
	protected boolean backValidation() {
		return false;
	}

	@Override
	protected boolean validation() {
		return false;
	}
	
	@Override
	protected String getBackLabel() {
		return "";
	}
}
