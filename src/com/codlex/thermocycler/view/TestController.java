package com.codlex.thermocycler.view;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;
import lombok.extern.log4j.Log4j;

@Log4j
public class TestController {

	private Thermocycler thermocycler;

	@FXML
	private TextField cycles;

	public void bind() {
		this.cycles.textProperty().bindBidirectional(
				this.thermocycler.getCycles(), new NumberStringConverter());
	}

	public void setModel(Thermocycler thermocycler) {
		this.thermocycler = thermocycler;
	}

	@FXML
	private void start() {
		log.debug("start clicked");
		this.thermocycler.start();
	}
}
