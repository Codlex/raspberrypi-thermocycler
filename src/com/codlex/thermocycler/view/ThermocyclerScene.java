package com.codlex.thermocycler.view;

import java.io.IOException;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import lombok.extern.log4j.Log4j;

@Log4j
public enum ThermocyclerScene {
	MockSensors, FillInBaths, FillRefillTanks, PutSpecimen, HotBathConfiguration, ColdBathConfiguration, CyclesConfiguration, ThermocyclerOverview, Shutdown;

	private final FXMLLoader loader;
	private Pane pane;
	private ThermocyclerController controller;

	private ThermocyclerScene() {
		this.loader = new FXMLLoader();
		this.loader.setLocation(ThermocyclerScene.class
				.getResource("scenes/" + name() + ".fxml"));
		try {
			this.pane = this.loader.load();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.controller = this.loader.getController();
	}

	@SuppressWarnings("unchecked")
	public <T extends ThermocyclerController> T getController() {
		return (T) this.loader.getController();
	}

	public boolean hasNextScene() {
		return ordinal() != values().length - 1;
	}

	private boolean hasPreviousScene() {
		return ordinal() != 0;
	}

	public Pane load(Thermocycler thermocycler, ThermocyclerGUI gui) {
		this.controller.setGui(gui);
		this.controller.setModel(thermocycler);
		return this.pane;
	}

	public ThermocyclerScene nextScene() {
		if (!hasNextScene()) {
			throw new RuntimeException(name() + " doesn't have next scene!");
		}
		return values()[ordinal() + 1];
	}

	public ThermocyclerScene previousScene() {
		if (!hasPreviousScene()) {
			throw new RuntimeException(
					name() + " doesn't have previous scene!");
		}

		return values()[ordinal() - 1];
	}
}
