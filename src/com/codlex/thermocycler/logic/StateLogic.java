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
	long currentTime = 0;
	long stateStartTime = 0;
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

	public long calculateTimeInState() {
		long translationChange = 0;
		
		if (this.currentState == State.ColdBath || this.currentState == State.HotBath) {
			translationChange = Settings.get().getTranslationTimeMillis(); // time of translation
		}
		
		if (this.currentState == State.ToColdBathPause || this.currentState == State.ToHotBathPause) {
			translationChange = 450; // time of errection
		}
		
		return Math.max(this.currentTime - this.stateStartTime - translationChange, 0);
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
		
		// reset start time
		this.stateStartTime = this.currentTime;
		
		
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
			case ToColdBathPause:
			case ToHotBathPause:
				log.debug("Pausing...");
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

	void doErrect() {
		if (this.currentState == State.ColdBath) {
			changeState(State.ToHotBathPause);
		} else {
			changeState(State.ToColdBathPause);
		}
	}
	
	void doCycle() {
		if (this.currentState == State.ColdBath) {
			changeState(State.HotBath);
		} else {
			changeState(State.ColdBath);
		}
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
				* (hotCyclesLeft * Settings.get().getTranslationTimeMillis());
		
		long pauseTime = Settings.get().getFromColdBathPause() * coldCyclesLeft
				+ Settings.get().getFromHotBathPause() * hotCyclesLeft;
		
		return hotTimeLeft + coldTimeLeft + translatingTime + pauseTime
				- this.calculateTimeInState();
	}

	long getTargetTimeInState() {
		// ASSERT state in (ColdBath, HotBath)
		if (this.currentState == State.ColdBath) {
			return TimeUnit.SECONDS
					.toMillis(this.thermocycler.getColdBath().time.get());
		} else if (this.currentState == State.HotBath) { // this.currentState == HotBath
			return TimeUnit.SECONDS
					.toMillis(this.thermocycler.getHotBath().time.get());
		} else if (this.currentState == State.ToHotBathPause || this.currentState == State.ToColdBathPause) {
			return getPauseTime();
		} 
		
//		log.error("This state doesn't have time associated with it: " + this.currentState); 
		return 0;
	}

	boolean isLastCycle() {
		int targetCycles = this.thermocycler.cycles.get();
		boolean hotFinished = this.hotBathImmersionCount >= targetCycles;
		boolean coldFinished = this.coldBathImmersionCount >= targetCycles;
		return hotFinished && coldFinished;
	}

	void processPauseTo(State to) {
		// ASSERT state in (ColdBath, HotBath)
		long currentStateTime = calculateTimeInState();
		if (currentStateTime > getPauseTime()) {
			if (!isLastCycle()) {
				// reset start time
				this.stateStartTime = this.currentTime;
				
				if (this.currentState == State.ToColdBathPause) {
					changeState(State.ColdBath);
				} else {
					changeState(State.HotBath);
				}
				
			} else {
				changeState(State.Finished);
			}
		}
	}
	
	private long getPauseTime() {
		if (this.currentState == State.HotBath || this.currentState == State.ToColdBathPause) {
			return Settings.get().getFromHotBathPause();
		} else if (this.currentState == State.ColdBath || this.currentState == State.ToHotBathPause) {
			return Settings.get().getFromColdBathPause();
		}
		
		throw new RuntimeException("Getting pause time when not in valid state: " + this.currentState);
	}
	

	void processCycling() {
		// ASSERT state in (ColdBath, HotBath)
		long currentImmersionTime = calculateTimeInState();
		if (currentImmersionTime > getTargetTimeInState()) {
			if (!isLastCycle()) {
				// invert state
				if (getPauseTime() != 0) {
					doErrect();
				} else {
					doCycle();
				}
				
			} else {
				changeState(State.Finished);
			}
		}
	}

	void processNotReady() {
		boolean hotBathReady = this.thermocycler.getHotBath().isReady();
		boolean coldBathReady = this.thermocycler.getColdBath().isReady();
		if (hotBathReady && coldBathReady) {
			this.stateStartTime = this.currentTime;
			changeState(State.HotBath);
		} 
	}

	private void processNotStarted() {
		if (this.thermocycler.isStarted.get()) {
			changeState(State.NotReady);
		}
	}

	public void reset() {
		this.currentTime = 0;
		this.stateStartTime = 0;
		changeState(State.NotStarted);
		this.hotBathImmersionCount = 0;
		this.coldBathImmersionCount = 0;
	}

	void update(long delta) {
		this.currentTime += delta;

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
			case ToColdBathPause:
				processPauseTo(State.ColdBath);
				break;
			case ToHotBathPause:
				processPauseTo(State.HotBath);
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