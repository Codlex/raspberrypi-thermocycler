package com.codlex.thermocycler.view;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.fxml.FXML;

public abstract class ThermocyclerController {
	
	protected Thermocycler thermocycler;
	protected ThermocyclerGUI gui;
	
	public final void setModel(Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
		
		if (!isRoot()) {
			onModelInitialized();
			updateUI();
		}
	}

	protected boolean isRoot() {
		return false;
	}

	public final void setGui(ThermocyclerGUI gui) {
		this.gui = gui;
	}
	
	@FXML
	protected void onNextClick() {
		this.gui.nextScene();
	}
	
	@FXML
	protected void onBackClick() {
		this.gui.previousScene();
	}
	
	protected void onModelInitialized() {
		
	}
	
	protected final void updateUI() {
		this.gui.updateUI();
	}
	
	protected void onUpdateUI() {
		
	}
	
	protected boolean validation() {
		return true;
	}
	
	protected boolean backValidation() {
		return true;
	}
	
	protected String getNextLabel() {
		return "Next";
	}
	
	protected String getBackLabel() {
		return "Back";
	}

	public String getTitle() {
		return "Codlex's Thermocycler";
	}
	
}
