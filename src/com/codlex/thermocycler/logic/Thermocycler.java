package com.codlex.thermocycler.logic;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

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
			this.persister.load(this);
		} else {
			reset();
		}
	}

	public int getTimeLeft() {
		return (int) TimeUnit.MILLISECONDS
				.toSeconds(this.stateLogic.getTargetImmersionTime()
						- this.stateLogic.calculateImmersionTime());
	}

	void init() {
		clear();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		this.translator.goToCold();
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.translator.goToHot();
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.translator.errect(State.HotBath);
	}

	void logStatus() {
		Tracker.track("cyclesLeft", this.stateLogic.getCyclesLeft());
		Tracker.track("timeLeft", this.stateLogic.getFullTimeLeftMillis() / 1000);

		// log.debug("ThermocyclerStatus(state=%s, immersion=%lu ms,
		// targetImmersion=%lu ms)",
		// StateToString(this.stateLogic.getCurrentState()),
		// this.stateLogic.calculateImmersionTime(),
		// this.stateLogic.getTargetImmersionTime());
		this.hotBath.logStatus();
		this.coldBath.logStatus();
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

	public void start() {
		if (validate()) {
			this.isStarted.set(true);
		} else {
			log.error("Starting of thermocycler failed because of bad input.");
			shutdown();
		}
	}

	private boolean validate() {
		boolean isValid = Settings.ValidationCyclesRange
				.contains(this.cycles.get());
		isValid &= this.hotBath.isValid();
		isValid &= this.coldBath.isValid();
		return isValid;
	}

	public void update(long deltaT) {
		if (this.isStarted.get()) {
			log.debug("############################## CYCLE(hot="
					+ this.stateLogic.hotBathImmersionCount + ", cold="
					+ this.stateLogic.coldBathImmersionCount + ", total="
					+ this.cycles + ") ##############################");
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

		logStatus();

	}

	public void lowerTranslator() {
		this.translator.goToHot();
	}

	public boolean lastFinishedSuccessfully() {
		return this.persister.lastFinishedSuccessfully();
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

	public void setCurrentCycle(int lastCycle) {
		this.stateLogic.coldBathImmersionCount = lastCycle;
		this.stateLogic.hotBathImmersionCount = lastCycle;
	}

	public Date getFinishTime() {
		return new Date(System.currentTimeMillis()
				+ this.stateLogic.getFullTimeLeftMillis());
	}

	public void performSafetyChecks() {
		boolean success = this.hotBath.performSafetyChecks();
		success &= this.coldBath.performSafetyChecks();

		if (!success) {
			log.error("Thermocycler safety check failed, shutting down.");
			shutdown();
		}
	}

	private void shutdown() {
		clear();
		System.exit(1);
	}

	private void clear() {
		this.translator.clear();
		this.hotBath.clear();
		this.coldBath.clear();
	}
}