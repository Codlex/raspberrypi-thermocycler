package com.codlex.thermocycler.view;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.fxml.FXML;
import lombok.extern.log4j.Log4j;

@Log4j
public abstract class ThermocyclerController {

	protected Thermocycler thermocycler;
	protected ThermocyclerGUI gui;

	protected boolean backValidation() {
		return true;
	}

	protected String getBackLabel() {
		return "Back";
	}

	protected String getNextLabel() {
		return "Next";
	}

	public String getTitle() {
		return "Codlex's Thermocycler";
	}

	protected boolean isRoot() {
		return false;
	}

	@FXML
	protected void onBackClick() {
		this.gui.previousScene();
	}

	protected void onModelInitialized() {

	}

	@FXML
	protected void onNextClick() {
		this.gui.nextScene();
	}

	protected void onUpdateUI() {

	}

	public final void setGui(ThermocyclerGUI gui) {
		this.gui = gui;
	}

	public final void setModel(Thermocycler thermocycler) {
		if (!isRoot()) {
			if (this.thermocycler == null) {
				this.thermocycler = thermocycler;
				onModelInitialized();
			}

			updateUI();
		}
	}

	protected final void updateUI() {
		this.gui.updateUI();
	}

	protected boolean validation() {
		return true;
	}

}
