package com.codlex.thermocycler.logic;

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

	@Override
	public void run() {
		log.debug("Thermocycler worker started");
		this.thermocycler.init();
		long lastLoopStart = System.currentTimeMillis();
		while (true) {
			long currentMillis = System.currentTimeMillis();
			long deltaT = currentMillis - lastLoopStart;
			lastLoopStart = System.currentTimeMillis();

			this.thermocycler.update(deltaT);

			if (deltaT > 100) {
				// TODO: fix this it measures whole 500 ms
				log.debug("Processing done in " + (deltaT  - 500) + " ms");
			}

			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				log.error(e);
			}
		}
	}

}
