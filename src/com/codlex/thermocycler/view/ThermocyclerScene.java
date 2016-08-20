package com.codlex.thermocycler.view;

import java.io.IOException;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import lombok.extern.log4j.Log4j;

@Log4j
public enum ThermocyclerScene {
	FillInBaths;
	
	
	private final FXMLLoader loader;
	
	private ThermocyclerScene() {
		this.loader = new FXMLLoader();
		this.loader.setLocation(ThermocyclerScene.class.getResource("scenes/" + name() + ".fxml"));
	}
	
	
	@SuppressWarnings("unchecked")
	public <T> T getController() {
		return (T) this.loader.getController();
	}
	
	public Pane load(Thermocycler thermocycler) {
		
		try {
			Pane pane = this.loader.load();
			ThermocyclerController controller = this.loader.getController();
			controller.setModel(thermocycler);
			return pane;
		} catch (IOException e) {
			log.debug(e);
			return null;
		}
	}
}
