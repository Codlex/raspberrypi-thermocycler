package com.codlex.thermocycler.logic;

import java.util.concurrent.TimeUnit;

import lombok.extern.log4j.Log4j;

@Log4j
public class StateLogic {

	static String StateToString(State state) {
		switch (state) {
			case NotReady :
				return "NotReady";
			case HotBath :
				return "HotBath";
			case ColdBath :
				return "ColdBath";
			case Finished :
				return "Finished";
			default :
				return "StateNameNotFound!";
		}
	}
	Thermocycler thermocycler;
	State currentState = State.NotReady;
	long time = 0;

	long immersionStart = 0;

	long hotBathImmersionCount = 0;

	long coldBathImmersionCount = 0;

	StateLogic(Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
	}

	long calculateImmersionTime() {
		return this.time - this.immersionStart;
	}

	void changeState(State state) {
		log.debug("STATE_CHANGE [" + this.currentState.toString() + "] . ["
				+ state.toString() + "]");

		this.currentState = state;
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
			return TimeUnit.SECONDS.toMillis(this.thermocycler.coldBath.time);
		} else { // this.currentState == HotBath
			return TimeUnit.SECONDS.toMillis(this.thermocycler.hotBath.time);
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
		boolean hotBathReady = this.thermocycler.hotBath.isReady();
		boolean coldBathReady = this.thermocycler.coldBath.isReady();
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
			case NotReady :
				processNotReady();
				break;
			case HotBath :
				processCycling();
				break;
			case ColdBath :
				processCycling();
				break;

		}
	}

}