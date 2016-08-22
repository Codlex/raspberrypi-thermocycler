package com.codlex.thermocycler.view;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public abstract class ThermocyclerController {
	protected Thermocycler thermocycler;
	protected ThermocyclerGUI gui;
	
	@FXML
	private Button nextButton;
	
	@FXML
	private Button backButton;
	
	public final void setModel(Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
		onModelInitialized();
		updateUI();
	}

	public final void setGui(ThermocyclerGUI gui) {
		this.gui = gui;
	}
	
	@FXML
	protected void onNextClick() {
		System.out.println("NEXT CLICKED");
		this.gui.nextScene();
	}
	
	@FXML
	protected void onBackClick() {
		this.gui.previousScene();
	}
	
	protected void onModelInitialized() {
		
	}
	
	protected final void updateUI() {
		final String VALID_BUTTON_STYLE = "-fx-background-color: green";
		final String INVALID_BUTTON_STYLE = "-fx-background-color: gray";
		boolean isValid = validation();
		this.nextButton.setDisable(!isValid);
		this.nextButton.setStyle(isValid ? VALID_BUTTON_STYLE : INVALID_BUTTON_STYLE);
		onUpdateUI();
	}
	
	protected void onUpdateUI() {
		
	}
	
	protected boolean validation() {
		return true;
	}
}
