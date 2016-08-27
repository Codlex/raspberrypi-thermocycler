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
	
	@Getter
	ObjectProperty<State> stateProperty = new SimpleObjectProperty<>(this.currentState);

	long immersionStart = 0;

	long hotBathImmersionCount = 0;

	long coldBathImmersionCount = 0;

	StateLogic(Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
	}

	public long calculateImmersionTime() {
		return this.time - this.immersionStart;
	}

	void changeState(final State state) {
		log.debug("STATE_CHANGE [" + this.currentState.toString() + "] . ["
				+ state.toString() + "]");

		this.currentState = state;
		
		Platform.runLater(()->{
			this.stateProperty.set(state);
		});
		
		switch (state) {
			case HotBath :
				this.hotBathImmersionCount++;
				break;
			case ColdBath :
				this.coldBathImmersionCount++;
				break;
			// do nothing for the rest
			default :
				log.error("EXPECTED HOT OR COLD BATH ONLY");
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

	State getCurrentState() {
		return this.currentState;
	}
	
	long getTargetImmersionTime() {
		// ASSERT state in (ColdBath, HotBath)
		if (this.currentState == State.ColdBath) {
			return TimeUnit.SECONDS.toMillis(this.thermocycler.getColdBath().time.get());
		} else { // this.currentState == HotBath
			return TimeUnit.SECONDS.toMillis(this.thermocycler.getHotBath().time.get());
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
		} else {
			log.debug("Thermocycler is not ready yet.");
		}
	}

	void update(long delta) {
		this.time += delta;

		switch (this.currentState) {
			case NotStarted:
				processNotStarted();
				break;
			case NotReady :
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
			default :
				log.error("Unknown state.");
				break;

		}
	}

	private void processNotStarted() {
		if (this.thermocycler.isStarted.get()) {
			changeState(State.NotReady);
		}
	}
	
	public int getCurrenCycle() {
		return (int) this.hotBathImmersionCount - 1;
	}
}