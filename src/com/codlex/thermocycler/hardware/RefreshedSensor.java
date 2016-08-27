package com.codlex.thermocycler.hardware;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.log4j.Log4j;

/**
 * Refreshed sensor refreshes itself periodically on it's own Thread not to block main execution of logic.
 */
@Log4j
abstract class RefreshedSensor<Value extends Number> implements Sensor<Value> {
	
	private final ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
	private final AtomicReference<Value> valueContainer = new AtomicReference<>();
	private final Duration refreshInterval;
	private ScheduledFuture<?> measuringTask;
	
	protected RefreshedSensor(final Duration refreshInterval) {
		this.refreshInterval = refreshInterval;
	}
	
	public final void startMeasuring() {
		this.measuringTask = this.worker.scheduleAtFixedRate(this::refreshValue, 0, refreshInterval.toMillis(), TimeUnit.MILLISECONDS);
	}
	
	public final void stopMeasuring() {
		if (this.measuringTask == null) {
			throw new RuntimeException("Tried to stop measuring but didn't start.");
		}
		
		boolean success = this.measuringTask.cancel(false);
		
		if(!success) {
			log.error("Failed to cancel task of RefreshedSensor.");
		}
	}
	
	@Override
	public final Value getValue() {
		return this.valueContainer.get();
	}
	
	protected abstract Value recalculateValue() throws Exception;
	
	private final void refreshValue() {
		try {
			this.valueContainer.set(recalculateValue());
		} catch (Exception e) {
			log.error(getClass().getName() + " failed to recalculate value: ", e);
		}
	}
}
