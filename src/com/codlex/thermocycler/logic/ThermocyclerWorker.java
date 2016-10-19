package com.codlex.thermocycler.logic;

import java.util.concurrent.TimeUnit;

import com.google.common.base.Stopwatch;

import lombok.extern.log4j.Log4j;

@Log4j
public class ThermocyclerWorker implements Runnable {

	public static void start(final Thermocycler thermocycler) {
		Thread worker = new Thread(new ThermocyclerWorker(thermocycler));
		worker.start();
	}

	private final Thermocycler thermocycler;

	private long lastLoopStart;

	public ThermocyclerWorker(final Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
	}

	private long pollDeltaT() {
		long currentMillis = System.currentTimeMillis();
		long deltaT = currentMillis - this.lastLoopStart;
		this.lastLoopStart = System.currentTimeMillis();
		return deltaT;
	}

	@Override
	public void run() {

		Stopwatch logicInitialization = Stopwatch.createStarted();
		log.debug("Logic initialization...");
		this.thermocycler.init();
		log.debug("######## Logic initialized in "
				+ logicInitialization.elapsed(TimeUnit.MILLISECONDS) + " ms ########");

		this.lastLoopStart = System.currentTimeMillis();
		while (true) {
			try {
				final Stopwatch stopwatch = Stopwatch.createStarted();

				this.thermocycler.update(pollDeltaT());

				long processingTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
				if (processingTime > 10) {
					log.error("Processing done in " + processingTime + " ms");
				}

				Thread.sleep(500);

			} catch (Exception e) {
				log.error("Exception happened in themrocycler logic: ", e);
			} finally {
				this.thermocycler.performSafetyChecks();
			}
		}
	}

}
