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
	
	final File backup = new File("ThermocyclerState.bin");

	public boolean lastFinishedSuccessfully() {
		return !this.backup.exists();
	}

	public void save(Thermocycler thermocycler) {
		StringBuilder builder = new StringBuilder();
				
		builder.append(thermocycler.getHotBath().getTemperatureProperty().get()).append(SEPARATOR); // hot bath temperature
		builder.append(thermocycler.getHotBath().getTimeProperty().get()).append(SEPARATOR); // hot bath time
		
		builder.append(thermocycler.getColdBath().getTemperatureProperty().get()).append(SEPARATOR); // cold bath temperature
		builder.append(thermocycler.getColdBath().getTimeProperty().get()).append(SEPARATOR); // cold bath time

		builder.append(thermocycler.cycles.get()).append(SEPARATOR); // total cycles


		int lastCycle = thermocycler.getStateLogic().getCurrenCycle();
		builder.append(lastCycle).append(SEPARATOR); // last cycle
		
		try {
			Files.write(builder.toString(), this.backup, Charsets.UTF_8);
		} catch (IOException e) {
			log.error("Couldn't persist state of thermocycler: ", e);
		}

	}
	
	public void load(Thermocycler thermocycler) {
		try {
			List<String> parameters = Files.readLines(this.backup, Charsets.UTF_8);
			
			int i = 0;
			
			thermocycler.getHotBath().getTemperatureProperty().set(Integer.parseInt(parameters.get(i)));
			i++;
			
			thermocycler.getHotBath().getTimeProperty().set(Integer.parseInt(parameters.get(i)));
			i++;
			
			thermocycler.getColdBath().getTemperatureProperty().set(Integer.parseInt(parameters.get(i)));
			i++;
			
			thermocycler.getColdBath().getTimeProperty().set(Integer.parseInt(parameters.get(i)));
			i++;
			
			thermocycler.getCycles().set(Integer.parseInt(parameters.get(i)));
			i++;
			
			thermocycler.setCurrentCycle(Integer.parseInt(parameters.get(i)));

			
		} catch (IOException e) {
			log.debug("Couldn't load state of thermocycler: ", e);
			return;
		}
	}
	
	public void delete() {
		if (!this.backup.exists()) {
			return;
		}
		
		if(!this.backup.delete()) {
			throw new RuntimeException("Can't delete persistance file.");
		}
	}
	
	
	
}
