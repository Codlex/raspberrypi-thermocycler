package com.codlex.thermocycler.logic;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.BathFactory;
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
		this.coldBath = BathFactory.createCold();
		this.hotBath = BathFactory.createHot();
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
		this.translator.errect();
	}

	void logStatus() {
//		 log.debug("ThermocyclerStatus(state=%s, immersion=%lu ms, targetImmersion=%lu ms)",
//		 StateToString(this.stateLogic.getCurrentState()),
//		 this.stateLogic.calculateImmersionTime(),
//		 this.stateLogic.getTargetImmersionTime());
		 this.hotBath.logStatus();
		 this.coldBath.logStatus();
	}

	public void reset() {
		Platform.runLater(()->{
			this.cycles.set(1);
		});
		this.stateLogic.reset();
		this.coldBath.reset();
		this.hotBath.reset();
		
		this.persister.delete();
	}

	public void start() {
		this.isStarted.set(true);
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
				this.isStarted.set(false);
				log.debug(
						"############################## CYCLING_FINISHED ##############################");
			}
		}
		
		logStatus();

	}

	public void lowerTranslator() {
		
	}

	public boolean lastFinishedSuccessfully() {
		return this.persister.lastFinishedSuccessfully();
	}
	
	public void onStateChange(State state) {
		Set<State> saveStates = ImmutableSet.of(State.NotReady, State.HotBath, State.ColdBath);
		if (saveStates.contains(state)) {
			this.persister.save(this);
		} 
		
		Set<State> deleteStates = ImmutableSet.of(State.Finished);
		if (deleteStates.contains(state)){
			this.persister.delete();
		}
		
	}

	public void setCurrentCycle(int lastCycle) {
		this.stateLogic.coldBathImmersionCount = lastCycle;
		this.stateLogic.hotBathImmersionCount = lastCycle;
	}
}