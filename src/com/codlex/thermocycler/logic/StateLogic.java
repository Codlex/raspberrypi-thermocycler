package com.codlex.thermocycler.logic;

import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class StateLogic {

	Thermocycler thermocycler;
	State currentState = State.NotStarted;
	long time = 0;
	long immersionStart = 0;
	long hotBathImmersionCount = 0;
	long coldBathImmersionCount = 0;

	@Getter
	ObjectProperty<State> stateProperty = new SimpleObjectProperty<>(
			this.currentState);

	StateLogic(Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
		if (!this.thermocycler.getPersister().lastFinishedSuccessfully()) {
			changeState(State.UnexpectedShutdown);
		} else {
			reset();
		}
	}

	public long calculateImmersionTime() {
		return Math.max(this.time - this.immersionStart
				- Settings.TranslationTimeMillis, 0);
	}

	void changeState(final State state) {
		log.debug("##### [" 
				+ this.currentState 
				+ "->" + state +  "] State(hot="
				+ this.hotBathImmersionCount + ", cold="
				+ this.coldBathImmersionCount + ", total="
				+ this.thermocycler.cycles.get() + ") ####");
		
		this.thermocycler.onStateChange(state);
		this.currentState = state;

		Platform.runLater(() -> {
			this.stateProperty.set(state);
		});

		switch (state) {
			case HotBath :
				this.hotBathImmersionCount++;
				break;
			case ColdBath :
				this.coldBathImmersionCount++;
				break;
			case UnexpectedShutdown :
				log.debug("Thermocycler had unexpected shutdown last time.");
				break;
			case NotStarted :
				log.debug("Ignored last settings reseting thermocycler.");
				break;
			case Finished :
				break;

			default :
				log.error("Unexpected state " + state);
		}
	}

	void doCycle() {
		if (this.currentState == State.ColdBath) {
			changeState(State.HotBath);
		} else {
			changeState(State.ColdBath);
		}

		// reset start time
		this.immersionStart = this.time;
	}

	public int getCurrenCycle() {
		return (int) this.hotBathImmersionCount;
	}

	State getCurrentState() {
		return this.currentState;
	}

	public long getCyclesLeft() {
		return this.thermocycler.cycles.get() - this.hotBathImmersionCount;
	}

	public long getFullTimeLeftMillis() {
		int hotCyclesLeft = (int) (this.thermocycler.cycles.get()
				- this.hotBathImmersionCount);
		long hotTimeLeft = TimeUnit.SECONDS.toMillis(
				hotCyclesLeft * this.thermocycler.getHotBath().time.get());

		int coldCyclesLeft = (int) (this.thermocycler.cycles.get()
				- this.coldBathImmersionCount);
		long coldTimeLeft = TimeUnit.SECONDS.toMillis(
				coldCyclesLeft * this.thermocycler.getColdBath().time.get());

		long translatingTime = 2
				* (hotCyclesLeft * Settings.TranslationTimeMillis);

		return hotTimeLeft + coldTimeLeft + translatingTime
				- this.calculateImmersionTime();
	}

	long getTargetImmersionTime() {
		// ASSERT state in (ColdBath, HotBath)
		if (this.currentState == State.ColdBath) {
			return TimeUnit.SECONDS
					.toMillis(this.thermocycler.getColdBath().time.get());
		} else { // this.currentState == HotBath
			return TimeUnit.SECONDS
					.toMillis(this.thermocycler.getHotBath().time.get());
		}
	}

	boolean isLastCycle() {
		int targetCycles = this.thermocycler.cycles.get();
		boolean hotFinished = this.hotBathImmersionCount >= targetCycles;
		boolean coldFinished = this.coldBathImmersionCount >= targetCycles;
		return hotFinished && coldFinished;
	}

	void processCycling() {
		// ASSERT state in (ColdBath, HotBath)
		long currentImmersionTime = calculateImmersionTime();
		if (currentImmersionTime > getTargetImmersionTime()) {
			if (!isLastCycle()) {
				// reset start time
				this.immersionStart = this.time;

				// invert state
				doCycle();
			} else {
				changeState(State.Finished);
			}
		}
	}

	void processNotReady() {
		boolean hotBathReady = this.thermocycler.getHotBath().isReady();
		boolean coldBathReady = this.thermocycler.getColdBath().isReady();
		if (hotBathReady && coldBathReady) {
			this.immersionStart = this.time;
			changeState(State.HotBath);
		} 
	}

	private void processNotStarted() {
		if (this.thermocycler.isStarted.get()) {
			changeState(State.NotReady);
		}
	}

	public void reset() {
		this.time = 0;
		this.immersionStart = 0;
		changeState(State.NotStarted);
		this.hotBathImmersionCount = 0;
		this.coldBathImmersionCount = 0;
	}

	void update(long delta) {
		this.time += delta;

		switch (this.currentState) {
			case NotStarted :
				processNotStarted();
				break;
			case NotReady :
				if (this.currentState != State.NotReady) {
					log.debug("Thermocycler is warming up.");
				}
				processNotReady();
				break;
			case HotBath :
				processCycling();
				break;
			case ColdBath :
				processCycling();
				break;
			case Finished :
				break;
			case UnexpectedShutdown :
				processNotStarted();
				break;
			default :
				log.error("Unknown state.");
				break;

		}
	}
}