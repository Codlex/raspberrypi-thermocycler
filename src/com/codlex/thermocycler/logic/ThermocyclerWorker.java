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

	public ThermocyclerWorker(final Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
	}

	private long lastLoopStart;
	
	private long pollDeltaT() {
		long currentMillis = System.currentTimeMillis();
		long deltaT = currentMillis - lastLoopStart;
		this.lastLoopStart = System.currentTimeMillis();
		return deltaT;
	}
	
	@Override
	public void run() {
		log.debug("Thermocycler worker started");
		this.thermocycler.init();
		this.lastLoopStart = System.currentTimeMillis();
		
		while (true) {
			try {
				Stopwatch stopwatch = Stopwatch.createStarted();
				
				this.thermocycler.update(pollDeltaT());
				
				long processingTime = stopwatch.elapsed(TimeUnit.MILLISECONDS);
				if (processingTime > 10) {
					log.error("Processing done in " + processingTime + " ms");
				}
				
			} catch (Exception e) {
				log.error("Exception happened in themrocycler logic: ", e);
			} finally {
				this.thermocycler.performSafetyChecks();
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

}
