package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.view.ThermocyclerController;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import lombok.extern.log4j.Log4j;
@Log4j
public class FillInBathsController extends ThermocyclerController {

	@FXML
	private Label hotBathLevelLabel;

	@FXML
	private Label coldBathLevelLabel;

	@FXML
	private ProgressBar hotBathLevelProgressBar;

	@FXML
	private ProgressBar coldBathLevelProgressBar;

	@FXML
	private Button nextButton;

	@Override
	public void onModelInitialized() {
		bind(this.thermocycler.getHotBath(), this.hotBathLevelLabel,
				this.hotBathLevelProgressBar);
		bind(this.thermocycler.getColdBath(), this.coldBathLevelLabel,
				this.coldBathLevelProgressBar);
	}

	private void bind(final Bath bath, Label label, ProgressBar progressBar) {
		bath.getLevelSensor().getProperty().addListener((newValue) -> {
			IntegerProperty property = (IntegerProperty) newValue;
			Integer value = property.getValue();
			label.textProperty().set(value.toString() + "%");
			progressBar.progressProperty().set(value / 100.0);
			updateUI();
		});
	}

	private static final String INVALID_PROGRESS_STYLE = "-fx-accent : red";
	private static final String VALID_PROGRESS_STYLE = "-fx-accent : green";

	@Override
	protected void onUpdateUI() {
		updateBathUI(this.thermocycler.getHotBath(),
				this.hotBathLevelProgressBar);
		updateBathUI(this.thermocycler.getColdBath(),
				this.coldBathLevelProgressBar);
	}

	@Override
	protected boolean validation() {
		boolean isValid = this.thermocycler.getHotBath().isLevelOK();
		isValid &= this.thermocycler.getColdBath().isLevelOK();
		return isValid;
	}

	private void updateBathUI(Bath bath, ProgressBar progress) {
		if (bath.isLevelOK()) {
			progress.setStyle(VALID_PROGRESS_STYLE);
		} else {
			progress.setStyle(INVALID_PROGRESS_STYLE);
		}
	}
	
	@Override
	protected boolean backValidation() {
		return false;
	}
	
}
