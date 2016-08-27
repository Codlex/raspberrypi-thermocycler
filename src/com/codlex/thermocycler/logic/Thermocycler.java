package com.codlex.thermocycler.logic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.BathFactory;

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
	final IntegerProperty cycles = new SimpleIntegerProperty(1);

	private int start = 0;

	public AtomicBoolean isStarted = new AtomicBoolean(false);

	public Thermocycler() {
		this.stateLogic = new StateLogic(this);
		this.coldBath = BathFactory.createCold();
		this.hotBath = BathFactory.createHot();
		this.translator = new Translator();
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
		// log.debug("ThermocyclerStatus(state=%s, immersion=%lu ms,
		// targetImmersion=%lu ms)",
		// StateToString(this.stateLogic.getCurrentState()),
		// this.stateLogic.calculateImmersionTime(),
		// this.stateLogic.getTargetImmersionTime());
		// this.hotBath.logStatus();
		// this.coldBath.logStatus();
	}

	void reset() {
		// TODO: reset everything for next cycling
	}

	public void start() {
		this.isStarted.set(true);
	}

	void update(long deltaT) {
		if (this.isStarted.get()) {
			log.debug("############################## CYCLE(hot="
					+ this.stateLogic.hotBathImmersionCount + ", cold="
					+ this.stateLogic.coldBathImmersionCount + ", total="
					+ this.cycles + ") ##############################");
			this.stateLogic.update(deltaT);
			this.hotBath.update(deltaT);
			this.coldBath.update(deltaT);
			this.translator.update(this.stateLogic.getCurrentState());
			logStatus();

			if (this.stateLogic.getCurrentState() == State.Finished) {
				this.isStarted.set(false);
				log.debug(
						"############################## CYCLING_FINISHED ##############################");
				reset();
			}
		} else {
			this.hotBath.logStatus();
			this.coldBath.logStatus();
		}
	}

	public void lowerTranslator() {
		
	}
}