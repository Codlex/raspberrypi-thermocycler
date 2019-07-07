package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.State;
import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.view.ThermocyclerController;
import com.codlex.thermocycler.view.ThermocyclerScene;

import javafx.animation.PauseTransition;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j;

@Log4j
public class ThermocyclerOverviewController extends ThermocyclerController {

	private static final String temperatureSensorFormat = "%.0f°C / %s";

	private static final String temperatureFormat = "%d°C";

	private static final String timeFormat = "%d:%02d";

	@FXML
	private Label title;

	@FXML
	private Label hotBathTitle;

	@FXML
	private Label coldBathTitle;

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

	@FXML
	private Label estimateTime;

	private State currentState;

	private String okStyle = "-fx-text-fill: green";

	private String notOkStyle = "-fx-text-fill: red";
	private String neutralStyle = "-fx-text-fill: black";
	@Override
	protected boolean backValidation() {
		return this.currentState == State.NotStarted
				|| this.currentState == State.UnexpectedShutdown;
	}

	private void everySeconds() {
		// TODO: make this better
		PauseTransition wait = new PauseTransition(Duration.seconds(1));
		wait.setOnFinished((e) -> {
			if (this.currentState == State.ColdBath
					|| this.currentState == State.HotBath
					|| this.currentState == State.ToColdBathPause
					|| this.currentState == State.ToHotBathPause
					|| this.currentState == State.ToHotBathMiddlePause
					|| this.currentState == State.ToColdBathMiddlePause) {
				// because of immersion time calculation
				updateUI();

			}
			if (this.currentState != State.Finished) {
				wait.playFromStart();
			}
		});
		wait.play();
	}

	@Override
	protected String getBackLabel() {
		if (this.currentState == State.UnexpectedShutdown) {
			return "Start over";
		} else {
			return super.getBackLabel();
		}
	}

	@Override
	protected String getNextLabel() {
		switch (this.currentState) {
			case UnexpectedShutdown :
				return "Continue";
			case NotStarted :
				return "Start";
			case Finished :
				return "Finish";
			default :
				return super.getNextLabel();
		}
	}

	@Override
	protected void onBackClick() {
		if (this.currentState == State.UnexpectedShutdown) {
			this.thermocycler.reset();
			this.gui.setScene(ThermocyclerScene.FillInBaths);
		} else {
			super.onBackClick();
		}
	}

	@Override
	protected void onModelInitialized() {
		this.thermocycler.getColdBath().getCurrentTemperatureProperty()
				.addListener((a) -> {
					updateUI();
				});

		this.thermocycler.getHotBath().getCurrentTemperatureProperty()
				.addListener((a) -> {
					updateUI();
				});

		this.thermocycler.getStateLogic().getStateProperty()
				.addListener((property) -> {
					ObjectProperty<State> stateProperty = (ObjectProperty<State>) property;
					this.currentState = stateProperty.get();
					updateUI();
				});

		this.currentState = this.thermocycler.getStateLogic().getStateProperty()
				.get();

		everySeconds();
	}

	@Override
	protected void onNextClick() {

		switch (this.currentState) {
			case NotStarted :
				this.thermocycler.start();
				break;
			case NotReady :
				log.error(
						"Tried to click next when it should have been disabled.");
				break;
			case ColdBath :
				log.error(
						"Tried to click next when it should have been disabled.");
				break;
			case HotBath :
				log.error(
						"Tried to click next when it should have been disabled.");
				break;
			case Finished :
				this.gui.nextScene();
				break;
			case UnexpectedShutdown :
				this.thermocycler.start();
				break;
		}

		updateUI();
	}

	private void onNotReadyUpdateUI(Bath coldBath, Bath hotBath) {
		StringProperty coldBathTemperatureProperty = this.coldBathTemperature
				.textProperty();
		float currentColdBathTemperature = this.thermocycler.getColdBath()
				.getCurrentTemperatureProperty().get();
		coldBathTemperatureProperty.set(String.format(temperatureSensorFormat,
				currentColdBathTemperature, coldBathTemperatureProperty.get()));
		this.coldBathTemperature.setStyle(
				coldBath.isTemperatureOK() ? this.okStyle : this.notOkStyle);

		StringProperty hotBathTemperatureProperty = this.hotBathTemperature
				.textProperty();
		float currenthotBathTemperature = this.thermocycler.getHotBath()
				.getCurrentTemperatureProperty().get();
		hotBathTemperatureProperty.set(String.format(temperatureSensorFormat,
				currenthotBathTemperature, hotBathTemperatureProperty.get()));
		this.hotBathTemperature.setStyle(
				hotBath.isTemperatureOK() ? this.okStyle : this.notOkStyle);
	}

	@Override
	protected void onUpdateUI() {
		Bath hotBath = this.thermocycler.getHotBath();
		this.hotBathTemperature.textProperty().set(String.format(
				temperatureFormat, hotBath.getTemperatureProperty().get()));
		int hotTime = hotBath.getTimeProperty().get();
		this.hotBathTime.textProperty()
				.set(String.format(timeFormat, hotTime / 60, hotTime % 60));

		Bath coldBath = this.thermocycler.getColdBath();
		this.coldBathTemperature.textProperty().set(String.format(
				temperatureFormat, coldBath.getTemperatureProperty().get()));
		int coldTime = coldBath.getTimeProperty().get();
		this.coldBathTime.textProperty()
				.set(String.format(timeFormat, coldTime / 60, coldTime % 60));
		this.cycles.textProperty()
				.set(Integer.toString(this.thermocycler.getCycles().get()));
		int minutes = this.thermocycler.getTimeLeft() / 60;
		int seconds = this.thermocycler.getTimeLeft() % 60;

		this.estimateTime.textProperty()
				.set(this.thermocycler.getFinishTime().toString());

		switch (this.currentState) {
			case NotStarted :
				this.title.setText("Thermocycler overview");
				break;
			case NotReady :
				this.title.setText("Warming up...");
				onNotReadyUpdateUI(coldBath, hotBath);
				break;
			case ColdBath :
				this.title.setText("Cooling specimen...");
				this.coldBathTitle.setStyle(this.okStyle);
				this.hotBathTitle.setStyle(this.neutralStyle);
				this.coldBathTime
						.setText(String.format(timeFormat, minutes, seconds));
				this.cycles.setText(
						this.thermocycler.getStateLogic().getCurrenCycle()
								+ " / " + this.cycles.getText());
				break;
				
			case ToHotBathPause:
				this.coldBathTitle.setStyle(this.neutralStyle);
				this.hotBathTitle.setStyle(this.neutralStyle);
				this.title.setText("Pausing for: " + String.format(timeFormat, minutes, seconds));
				break;
				
			case ToColdBathPause:
				this.coldBathTitle.setStyle(this.neutralStyle);
				this.hotBathTitle.setStyle(this.neutralStyle);
				this.title.setText("Pausing for: " + String.format(timeFormat, minutes, seconds));
				break;
				
			case ToHotBathMiddlePause:
				this.coldBathTitle.setStyle(this.neutralStyle);
				this.hotBathTitle.setStyle(this.neutralStyle);
				this.title.setText("Pausing (m) for: " + String.format(timeFormat, minutes, seconds));
				break;
				
			case ToColdBathMiddlePause:
				this.coldBathTitle.setStyle(this.neutralStyle);
				this.hotBathTitle.setStyle(this.neutralStyle);
				this.title.setText("Pausing (m) for: " + String.format(timeFormat, minutes, seconds));
				break;
				
			case HotBath :
				this.title.setText("Heating specimen...");
				this.coldBathTitle.setStyle(this.neutralStyle);
				this.hotBathTitle.setStyle(this.okStyle);
				this.hotBathTime
						.setText(String.format(timeFormat, minutes, seconds));
				this.cycles.setText(
						this.thermocycler.getStateLogic().getCurrenCycle()
								+ " / " + this.cycles.getText());
				break;
			
			case Finished :
				this.title.setText("Cycling completed click finish.");
				break;
			case UnexpectedShutdown :
				this.title.setText(
						"Shutdown unepectedly do you wish to continue?");
				this.cycles.setText(
						this.thermocycler.getStateLogic().getCurrenCycle()
								+ " / " + this.cycles.getText());
				break;
		}

	}

	@Override
	protected boolean validation() {
		switch (this.currentState) {
			case NotStarted :
				return true;
			case Finished :
				return true;
			case UnexpectedShutdown :
				return true;
			default :
				return false;
		}
	}
}
