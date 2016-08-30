package com.codlex.thermocycler.hardware;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.google.common.base.Stopwatch;

import lombok.extern.log4j.Log4j;

/**
 * Refreshed sensor refreshes itself periodically on it's own Thread not to
 * block main execution of logic.
 */
@Log4j
abstract class RefreshedSensor<Value extends Number> implements Sensor<Value> {

	private final ScheduledExecutorService worker = Executors
			.newSingleThreadScheduledExecutor();
	private final AtomicReference<Value> valueContainer = new AtomicReference<>();
	private final Duration refreshInterval;
	private ScheduledFuture<?> measuringTask;

	protected RefreshedSensor(final Duration refreshInterval) {
		this.refreshInterval = refreshInterval;
	}

	@Override
	public final Value getValue() {
		if (this.valueContainer.get() == null) {
			log.error("Not initialized sensor.");
			return getDefaultValue();
		}
		return this.valueContainer.get();
	}

	protected abstract Value getDefaultValue();

	protected abstract Value recalculateValue() throws Exception;

	private final void refreshValue() {
		try {
			Stopwatch stopwatch = Stopwatch.createStarted();
			this.valueContainer.set(recalculateValue());
			log.debug(getClass().getSimpleName() + " took "
					+ stopwatch.elapsed(TimeUnit.MILLISECONDS)
					+ " to measure value.");

		} catch (Exception e) {
			log.error(getClass().getSimpleName() + " failed to recalculate value: ",
					e);
		}
	}

	@Override
	public final void startMeasuring() {
		log.debug(getClass().getSimpleName()
				+ " started measuring periodically every "
				+ this.refreshInterval.toMillis() + " ms");
		this.measuringTask = this.worker.scheduleAtFixedRate(this::refreshValue,
				0, this.refreshInterval.toMillis(), TimeUnit.MILLISECONDS);
	}

	public final void stopMeasuring() {
		if (this.measuringTask == null) {
			throw new RuntimeException(
					"Tried to stop measuring but didn't start.");
		}

		boolean success = this.measuringTask.cancel(false);

		if (!success) {
			log.error("Failed to cancel task of RefreshedSensor.");
		}
	}
}
