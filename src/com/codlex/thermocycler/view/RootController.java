package com.codlex.thermocycler.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import lombok.extern.log4j.Log4j;

@Log4j
public class RootController extends ThermocyclerController {

	@FXML
	protected Button nextButton;

	@FXML
	protected Button backButton;

	private ThermocyclerController getController() {
		return this.gui.getCurrentScene().getController();
	}

	@Override
	protected boolean isRoot() {
		return true;
	}

	@Override
	protected void onBackClick() {
		getController().onBackClick();
	}

	@Override
	protected void onNextClick() {
		getController().onNextClick();
	}

	@Override
	protected void onUpdateUI() {
		final String VALID_BUTTON_STYLE = "-fx-background-color: green; -fx-text-fill: white";
		final String INVALID_BUTTON_STYLE = "-fx-background-color: gray; -fx-text-fill: white";

		final ThermocyclerController controller = getController();

		boolean isNextValid = controller.validation();
		this.nextButton.setStyle(
				isNextValid ? VALID_BUTTON_STYLE : INVALID_BUTTON_STYLE);
		this.nextButton.setDisable(!isNextValid);
		this.nextButton.setText(controller.getNextLabel());

		boolean isBackValid = controller.backValidation();
		this.backButton.setStyle(
				isNextValid ? VALID_BUTTON_STYLE : INVALID_BUTTON_STYLE);
		this.backButton.setDisable(!isBackValid);
		this.backButton.setText(controller.getBackLabel());

		controller.onUpdateUI();

	}
}
