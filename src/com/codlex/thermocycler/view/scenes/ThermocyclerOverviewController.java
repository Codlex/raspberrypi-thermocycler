package com.codlex.thermocycler.view.scenes;

import com.codlex.thermocycler.logic.State;
import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.view.ThermocyclerController;
import com.codlex.thermocycler.view.ThermocyclerScene;

import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.util.Duration;
import lombok.extern.log4j.Log4j;

@Log4j
public class ThermocyclerOverviewController extends ThermocyclerController {
	
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

	
	private static final String temperatureSensorFormat = "%.0f°C / %s";

	private static final String temperatureFormat = "%d°C";
	
	private static final String timeFormat = "%d:%02d";
	
	private State currentState;
	
	@Override
	protected void onModelInitialized() {
		this.thermocycler.getColdBath().getCurrentTemperatureProperty().addListener((a) -> {
			updateUI();
		});
		
		this.thermocycler.getHotBath().getCurrentTemperatureProperty().addListener((a) -> {
			updateUI();
		});
		
		this.thermocycler.getStateLogic().getStateProperty().addListener((property) -> {
			ObjectProperty<State> stateProperty = (ObjectProperty<State>)property;
			this.currentState = stateProperty.get();
			updateUI();
		});
		
		this.currentState = this.thermocycler.getStateLogic().getStateProperty().get();
		
		everySeconds();
	}
	
	private String okStyle = "-fx-text-fill: green";
	private String notOkStyle = "-fx-text-fill: red";
	private String neutralStyle = "-fx-text-fill: black";

	@Override
	protected void onUpdateUI() {
		log.debug("Update UI");
		Bath hotBath = this.thermocycler.getHotBath();
		this.hotBathTemperature.textProperty().set(String.format(temperatureFormat, hotBath.getTemperatureProperty().get()));
		int hotTime = hotBath.getTimeProperty().get();
		this.hotBathTime.textProperty().set(String.format(timeFormat, hotTime / 60, hotTime % 60));
		
		Bath coldBath = this.thermocycler.getColdBath();
		this.coldBathTemperature.textProperty().set(String.format(temperatureFormat, coldBath.getTemperatureProperty().get()));
		int coldTime = coldBath.getTimeProperty().get();
		this.coldBathTime.textProperty().set(String.format(timeFormat, coldTime / 60, coldTime % 60));
		this.cycles.textProperty().set(Integer.toString(this.thermocycler.getCycles().get()));
		int minutes = this.thermocycler.getTimeLeft() / 60;
		int seconds = this.thermocycler.getTimeLeft() % 60;
		switch (this.currentState) {
			case NotStarted:
				break;
			case NotReady:
				this.title.setText("Thermocycler is warming up...");
				onNotReadyUpdateUI(coldBath, hotBath);
				break;
			case ColdBath:
				this.title.setText("Thermocycler is cooling specimen...");
				this.coldBathTitle.setStyle(okStyle);
				this.hotBathTitle.setStyle(neutralStyle);
				this.coldBathTime.setText(String.format(timeFormat, minutes, seconds));
				this.cycles.setText(this.thermocycler.getStateLogic().getCurrenCycle() + " / " + this.cycles.getText());
				break;
			case HotBath:
				this.title.setText("Thermocycler is heating specimen...");
				this.coldBathTitle.setStyle(neutralStyle);
				this.hotBathTitle.setStyle(okStyle);
				this.hotBathTime.setText(String.format(timeFormat, minutes, seconds));
				this.cycles.setText(this.thermocycler.getStateLogic().getCurrenCycle() + " / " + this.cycles.getText());
				break;
			case Finished:
				this.title.setText("Cycling completed click finish.");
				break;
		}
		
	}
	
	@Override
	protected String getNextLabel() {
		switch (this.currentState) {
			case NotStarted:
				return "Start";
			case Finished:
				return "Finish";
			default:
				return super.getNextLabel();
		}
	}
	
	@Override
	protected boolean validation() {
		switch (this.currentState) {
			case NotStarted:
				return true;
			case Finished:
				return true;
			default:
				return false;
		}
	}
	
	private void onNotReadyUpdateUI(Bath coldBath, Bath hotBath) {		
		StringProperty coldBathTemperatureProperty = this.coldBathTemperature.textProperty();
		float currentColdBathTemperature = this.thermocycler.getColdBath().getCurrentTemperatureProperty().get();
		coldBathTemperatureProperty.set(String.format(temperatureSensorFormat, currentColdBathTemperature, coldBathTemperatureProperty.get()));
		this.coldBathTemperature.setStyle(coldBath.isTemperatureOK() ? okStyle : notOkStyle);
		
		StringProperty hotBathTemperatureProperty = this.hotBathTemperature.textProperty();
		float currenthotBathTemperature = this.thermocycler.getHotBath().getCurrentTemperatureProperty().get();
		hotBathTemperatureProperty.set(String.format(temperatureSensorFormat, currenthotBathTemperature, hotBathTemperatureProperty.get()));
		this.hotBathTemperature.setStyle(hotBath.isTemperatureOK() ? okStyle : notOkStyle);
	}

	@Override
	protected void onNextClick() {
		
		switch (this.currentState) {
			case NotStarted:
				this.thermocycler.start();
				break;
			case NotReady:
				log.error("Tried to click next when it should have been disabled.");
				break;
			case ColdBath:
				log.error("Tried to click next when it should have been disabled.");
				break;
			case HotBath:
				log.error("Tried to click next when it should have been disabled.");
				break;
			case Finished:
				this.gui.nextScene();
				this.thermocycler.getTranslator().errect();
				break;
		}
		
		updateUI();
	}
	
	private void everySeconds() {
		// TODO: make this better
		PauseTransition wait = new PauseTransition(Duration.seconds(1));
	    wait.setOnFinished((e) -> {
	    	if (this.currentState == State.ColdBath 
	    			|| this.currentState == State.HotBath) {
	    		// because of immersion time calculation
	    		updateUI();
		        
	    	}
	    	if (this.currentState != State.Finished) {
	    		wait.playFromStart();
	    	}
	    });
	    wait.play();
	}
}
