package com.codlex.thermocycler.view;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

import lombok.extern.log4j.Log4j;

@Log4j
public class ScreenAlarmClock {
	
	private static final ExecutorService RINGER = Executors.newSingleThreadExecutor();
	private static final long WAKE_UP_TIME = Duration.ofMinutes(1).toMillis();
	
	private AtomicLong lastTouchEventTime = new AtomicLong();

	
	public void onTouchEvent() {
		final long now = System.currentTimeMillis();
		final long diff = now - this.lastTouchEventTime.getAndSet(now);
		
		if (diff > WAKE_UP_TIME) {
			RINGER.submit(() -> {
				log.debug("Waking up screen.");
				final String wakeUpCommand = "/usr/bin/xinit";
				try {
					Runtime.getRuntime().exec(wakeUpCommand);
				} catch (IOException e) {
					log.error("Something went wrong with waking up.", e);
				}
			});
		}
	}
}
