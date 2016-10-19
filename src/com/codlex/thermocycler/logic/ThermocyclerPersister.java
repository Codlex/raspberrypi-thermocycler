package com.codlex.thermocycler.logic;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

import lombok.extern.log4j.Log4j;

@Log4j
public class ThermocyclerPersister {

	private static final Object SEPARATOR = "\n";

	final File temp = new File("ThermocyclerState.bin.temp");
	final File finalFile = new File("ThermocyclerState.bin");

	public void delete() {
		if (!this.finalFile.exists()) {
			return;
		}

		if (!this.finalFile.delete()) {
			throw new RuntimeException("Can't delete persistance file.");
		}
	}

	public boolean lastFinishedSuccessfully() {
		return !this.finalFile.exists();
	}

	public boolean load(Thermocycler thermocycler) {
		try {
			List<String> parameters = Files.readLines(this.finalFile,
					Charsets.UTF_8);

			int i = 0;

			thermocycler.getHotBath().getTemperatureProperty()
					.set(Integer.parseInt(parameters.get(i)));
			i++;

			thermocycler.getHotBath().getTimeProperty()
					.set(Integer.parseInt(parameters.get(i)));
			i++;

			thermocycler.getColdBath().getTemperatureProperty()
					.set(Integer.parseInt(parameters.get(i)));
			i++;

			thermocycler.getColdBath().getTimeProperty()
					.set(Integer.parseInt(parameters.get(i)));
			i++;

			thermocycler.getCycles().set(Integer.parseInt(parameters.get(i)));
			i++;

			thermocycler.setCurrentCycle(Integer.parseInt(parameters.get(i)));

		} catch (Exception e) {
			log.error("Couldn't load state of thermocycler: ", e);
			return false;
		}

		return true;
	}

	public void save(Thermocycler thermocycler) {
		StringBuilder builder = new StringBuilder();

		builder.append(thermocycler.getHotBath().getTemperatureProperty().get())
				.append(SEPARATOR); // hot bath temperature
		builder.append(thermocycler.getHotBath().getTimeProperty().get())
				.append(SEPARATOR); // hot bath time

		builder.append(
				thermocycler.getColdBath().getTemperatureProperty().get())
				.append(SEPARATOR); // cold bath temperature
		builder.append(thermocycler.getColdBath().getTimeProperty().get())
				.append(SEPARATOR); // cold bath time

		builder.append(thermocycler.cycles.get()).append(SEPARATOR); // total
																		// cycles

		int lastCycle = thermocycler.getStateLogic().getCurrenCycle();
		builder.append(lastCycle).append(SEPARATOR); // last cycle

		try {
			Files.write(builder.toString(), this.temp, Charsets.UTF_8);
			Files.move(this.temp, this.finalFile);
		} catch (IOException e) {
			log.error("Couldn't persist state of thermocycler: ", e);
		}

	}

}
