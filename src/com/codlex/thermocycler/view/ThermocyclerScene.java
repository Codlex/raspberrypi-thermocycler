package com.codlex.thermocycler.view;

import java.io.IOException;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import lombok.extern.log4j.Log4j;

@Log4j
public enum ThermocyclerScene {
	FillInBaths, 
	FillRefillTanks, 
	PutSpecimen,
	HotBathConfiguration,
	ColdBathConfiguration,
	CyclesConfiguration,
	ThermocyclerOverview,
//	CycingInProgress
	;
	
	private final FXMLLoader loader;
	
	private ThermocyclerScene() {
		this.loader = new FXMLLoader();
		this.loader.setLocation(ThermocyclerScene.class.getResource("scenes/" + name() + ".fxml"));
	}
	
	
	public ThermocyclerScene previousScene() {
		if (!hasPreviousScene()) {
			throw new RuntimeException(name() + " doesn't have previous scene!");
		}
		
		return values()[ordinal() - 1];
	}
	
	private boolean hasPreviousScene() {
		return ordinal() != 0;
	}

	public ThermocyclerScene nextScene() {
		if (!hasNextScene()) {
			throw new RuntimeException(name() + " doesn't have next scene!");
		}
		return values()[ordinal() + 1];
	}
	
	public boolean hasNextScene() {
		return ordinal() !=  values().length - 1;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getController() {
		return (T) this.loader.getController();
	}
	
	public Pane load(Thermocycler thermocycler, ThermocyclerGUI gui) {
		try {
			Pane pane = this.loader.load();
			ThermocyclerController controller = this.loader.getController();
			controller.setModel(thermocycler);
			controller.setGui(gui);
			return pane;
		} catch (IOException e) {
			log.debug(e);
			return null;
		}
	}
}
