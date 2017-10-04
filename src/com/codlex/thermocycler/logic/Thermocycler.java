package com.codlex.thermocycler.logic;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.log4j.LogManager;

import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.cold.ColdBath;
import com.codlex.thermocycler.logic.bath.hot.HotBath;
import com.codlex.thermocycler.tracker.Tracker;
import com.google.common.collect.ImmutableSet;

import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class Thermocycler {

	@Getter
	private Bath hotBath;

	@Getter
	private Bath coldBath;

	@Getter
	private StateLogic stateLogic;

	@Getter
	private Translator translator;

	@Getter
	final IntegerProperty cycles = new SimpleIntegerProperty();

	public AtomicBoolean isStarted = new AtomicBoolean(false);

	@Getter
	private ThermocyclerPersister persister = new ThermocyclerPersister();

	public Thermocycler() {
		this.stateLogic = new StateLogic(this);
		this.coldBath = new ColdBath(this);
		this.hotBath = new HotBath(this);
		this.translator = new Translator();

		if (!this.persister.lastFinishedSuccessfully()) {
			boolean succesfullyLoaded = this.persister.load(this);
			if (!succesfullyLoaded) {
				reset();
			}
		} else {
			reset();
		}
	}

	private void clear() {
		log.debug("Clearning thermocycler.");
		this.translator.clear();
		this.hotBath.clear();
		this.coldBath.clear();
	}

	public Date getFinishTime() {
		return new Date(System.currentTimeMillis()
				+ this.stateLogic.getFullTimeLeftMillis());
	}

	public int getTimeLeft() {
		return (int) TimeUnit.MILLISECONDS
				.toSeconds(this.stateLogic.getTargetImmersionTime()
						- this.stateLogic.calculateImmersionTime());
	}

	void init() {
		clear();
		try {
			log.debug("Checking cold bath...");
			Thread.sleep(1500);
			this.translator.goToCold();
			log.debug("Checking hot bath...");
			Thread.sleep(2000);
			this.translator.goToHot();
			Thread.sleep(1500);
			log.debug("Errecting...");
			this.translator.errect(State.HotBath);
		} catch (InterruptedException e1) {
			log.error(e1);
		}
	}

	public boolean lastFinishedSuccessfully() {
		return this.persister.lastFinishedSuccessfully();
	}

	void logStatus() {
		Tracker.track("cyclesLeft", this.stateLogic.getCyclesLeft());
		Tracker.track("timeLeft",
				this.stateLogic.getFullTimeLeftMillis() / 1000);

		log.debug("ThermocyclerStatus(state="
				+ this.stateLogic.getCurrentState() + ", immersion="
				+ this.stateLogic.calculateImmersionTime()
				+ " ms, targetImmersion="
				+ this.stateLogic.getTargetImmersionTime() + " ms)");

		this.hotBath.logStatus();
		this.coldBath.logStatus();
	}

	public void lowerTranslator() {
		this.translator.goToHot();
	}

	public void onStateChange(State state) {
		Set<State> saveStates = ImmutableSet.of(State.NotReady, State.HotBath,
				State.ColdBath);
		if (saveStates.contains(state)) {
			this.persister.save(this);
		}

		Set<State> deleteStates = ImmutableSet.of(State.Finished);
		if (deleteStates.contains(state)) {
			this.persister.delete();
		}

	}

	public void performSafetyChecks() {
		boolean success = this.hotBath.performSafetyChecks();
		success &= this.coldBath.performSafetyChecks();

		if (!success) {
			log.error("Thermocycler safety check failed, shutting down.");
			shutdown();
		}
	}

	public void reset() {
		Platform.runLater(() -> {
			this.cycles.set(1);
		});
		this.stateLogic.reset();
		this.coldBath.reset();
		this.hotBath.reset();

		this.persister.delete();
	}

	public void setCurrentCycle(int lastCycle) {
		this.stateLogic.coldBathImmersionCount = lastCycle;
		this.stateLogic.hotBathImmersionCount = lastCycle;
	}

	private void shutdown() {
		LogManager.shutdown();
		clear();
		System.exit(1);
	}

	public void start() {
		if (validate()) {
			this.isStarted.set(true);
		} else {
			log.error("Starting of thermocycler failed because of bad input.");
			shutdown();
		}
	}

	public void update(long deltaT) {
		if (this.isStarted.get()) {
			this.stateLogic.update(deltaT);
			this.hotBath.update(deltaT);
			this.coldBath.update(deltaT);
			this.translator.update(this.stateLogic.getCurrentState());

			if (this.stateLogic.getCurrentState() == State.Finished) {
				this.translator.errect(State.ColdBath);
				this.isStarted.set(false);
				clear();
				log.debug(
						"############################## CYCLING_FINISHED ##############################");
			}
		}

		// touch to recalculate
		this.hotBath.getCurrentTemperature();
		this.hotBath.getLevelSensor().getPercentageFilled();

		this.coldBath.getCurrentTemperature();
		this.coldBath.getLevelSensor().getPercentageFilled();

		logStatus();

	}

	private boolean validate() {
		boolean isValid = Settings.get().getValidationCyclesRange()
				.contains(this.cycles.get());
		isValid &= this.hotBath.isValid();
		isValid &= this.coldBath.isValid();
		return isValid;
	}
}