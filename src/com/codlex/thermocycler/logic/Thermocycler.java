package com.codlex.thermocycler.logic;

import java.util.concurrent.atomic.AtomicBoolean;

import com.codlex.thermocycler.logic.bath.Bath;
import com.codlex.thermocycler.logic.bath.BathFactory;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class Thermocycler {

	static int COLD_TEMPERATURE = 1;
	static int COLD_TIME = 2;

	static int HOT_TEMPERATURE = 3;
	static int HOT_TIME = 4;
	static int CYCLES = 5;
	static int START = 6;
	static int STARTED = 7;
	private StateLogic stateLogic;
	private Translator translator;

	Bath coldBath;
	Bath hotBath;

	@Getter
	final IntegerProperty cycles = new SimpleIntegerProperty(100);

	int start = 0;

	public AtomicBoolean isStarted = new AtomicBoolean(false);

	public Thermocycler() {
		 this.stateLogic = new StateLogic(this);
		 this.coldBath = BathFactory.createCold();
		 this.hotBath = BathFactory.createHot();
		 this.translator = new Translator();
	}

	void back() {
		if (this.isStarted.get()) {
			log.error("Thermocycler started, can't go back now.");
			return;
		}

		// TODO: implement
		log.debug("back");
	}

	void confirm() {
		if (this.isStarted.get()) {
			log.error("Thermocycler started, can't go confirm now.");
			return;
		}
		// TODO: implement
	}

	void deleteDigit() {
		// TODO: implement
	}

	void enterDigit(int digit) {
		// TODO: implement
	}

	void exit() {
		log.debug("exit");
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
		this.translator.erect();
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
}