package com.codlex.thermocycler.view.scenes;

import java.util.function.Consumer;

import com.codlex.thermocycler.view.ThermocyclerController;
import com.google.common.collect.ImmutableList;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class CyclesConfiguration extends ThermocyclerController {

	@FXML
	private Button increment1;

	@FXML
	private Button increment10;

	@FXML
	private Button increment100;

	@FXML
	private Button increment1000;

	@FXML
	private Button increment10000;

	@FXML
	private Button increment100000;

	@FXML
	private Button decrement1;

	@FXML
	private Button decrement10;

	@FXML
	private Button decrement100;

	@FXML
	private Button decrement1000;

	@FXML
	private Button decrement10000;

	@FXML
	private Button decrement100000;

	@FXML
	private Label digit1;

	@FXML
	private Label digit10;

	@FXML
	private Label digit100;

	@FXML
	private Label digit1000;

	@FXML
	private Label digit10000;

	@FXML
	private Label digit100000;

	private ImmutableList<Button> increments;

	private ImmutableList<Button> decrements;

	private ImmutableList<Label> digits;

	private void bind(ImmutableList<Button> buttons, Consumer<Integer> action) {
		for (int i = 0; i < buttons.size(); i++) {
			final int value = (int) Math.pow(10, i);
			final Button button = buttons.get(i);
			button.addEventHandler(MouseEvent.MOUSE_CLICKED, (a) -> {
				action.accept(value);
			});
		}
	}

	private void bind(ImmutableList<Label> digits) {
		for (int i = 0; i < digits.size(); i++) {
			this.thermocycler.getCycles().addListener((newValue) -> {
				updateUI();
			});
		}
	}

	private void decrementCounter(int value) {
		IntegerProperty property = this.thermocycler.getCycles();
		property.set(property.get() - value);
	}

	private void incrementCounter(int value) {
		IntegerProperty property = this.thermocycler.getCycles();
		property.set(property.get() + value);
	}

	@Override
	protected void onModelInitialized() {
		this.increments = ImmutableList.of(this.increment1, this.increment10,
				this.increment100, this.increment1000, this.increment10000,
				this.increment100000);
		this.decrements = ImmutableList.of(this.decrement1, this.decrement10,
				this.decrement100, this.decrement1000, this.decrement10000,
				this.decrement100000);
		this.digits = ImmutableList.of(this.digit1, this.digit10, this.digit100,
				this.digit1000, this.digit10000, this.digit100000);

		bind(this.increments, this::incrementCounter);
		bind(this.decrements, this::decrementCounter);
		bind(this.digits);

	}

	@Override
	protected void onUpdateUI() {

		updateDigits();

		int cycles = this.thermocycler.getCycles().get();

		for (int i = 0; i < this.decrements.size(); i++) {
			final Button button = this.decrements.get(i);
			final int value = (int) Math.pow(10, i);
			button.setDisable(cycles < value);
		}

		for (int i = 0; i < this.increments.size(); i++) {
			final Button button = this.increments.get(i);
			final int value = (int) Math.pow(10, i);
			button.setDisable(cycles + value > 999999);
		}

	}

	private void updateDigits() {
		int newValue = this.thermocycler.getCycles().get();
		for (int i = 0; i < this.digits.size(); i++) {
			final int digitValue = (int) Math.pow(10, i);
			final Label digit = this.digits.get(i);
			digit.textProperty()
					.set(Integer.toString((newValue / digitValue) % 10));
		}
	}

	@Override
	protected boolean validation() {
		return this.thermocycler.getCycles().get() != 0;
	}
}
