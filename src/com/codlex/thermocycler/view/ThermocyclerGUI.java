package com.codlex.thermocycler.view;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import com.codlex.thermocycler.logic.Settings;
import com.codlex.thermocycler.logic.Thermocycler;
import com.codlex.thermocycler.logic.ThermocyclerWorker;

import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.TouchEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import lombok.Getter;
import lombok.extern.log4j.Log4j;

@Log4j
public class ThermocyclerGUI extends Application {

	public static void main(String[] args) {		
		PropertyConfigurator.configure("log4j.properties");
		log.debug("################ Thermocycler is starting ################");
		launch(new String[0]);
	}

	private Stage primaryStage;

	private BorderPane rootLayout;

	private Thermocycler thermocycler;

	@Getter
	private ThermocyclerScene currentScene = ThermocyclerScene.FillInBaths;

	private ThermocyclerController controller;

	private ScreenAlarmClock screenAlarmClock = new ScreenAlarmClock();

	/**
	 * Returns the main stage.
	 *
	 * @return
	 */
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(
					ThermocyclerGUI.class.getResource("RootScene.fxml"));
			this.rootLayout = loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(this.rootLayout);
			this.primaryStage.setFullScreen(Settings.get().getFullScreen());
			this.primaryStage.setScene(scene);
			this.primaryStage.show();

			this.primaryStage.addEventFilter(TouchEvent.ANY,
					new EventHandler<Event>() {
						@Override
						public void handle(Event event) {
							ThermocyclerGUI.this.screenAlarmClock
									.onTouchEvent();
						};
					});

			this.controller = loader.getController();
			this.controller.setGui(this);
			this.controller.setModel(this.thermocycler);

			if (this.thermocycler.lastFinishedSuccessfully()) {
				setScene(ThermocyclerScene.FillInBaths);
			} else {
				setScene(ThermocyclerScene.ThermocyclerOverview);
			}

			// this.rootLayout.setRight(ThermocyclerScene.MockSensors.load(this.thermocycler,
			// this));

		} catch (IOException e) {
			log.error(e);
		}
	}

	public void nextScene() {
		setScene(this.currentScene.nextScene());
	}

	public void previousScene() {
		setScene(this.currentScene.previousScene());
	}

	public void setScene(ThermocyclerScene scene) {
		this.currentScene = scene;
		Pane pane = scene.load(this.thermocycler, this);
		if (pane == null) {
			throw new RuntimeException(scene.name() + " couldn't be loaded!");
		}
		this.rootLayout.setCenter(pane);
	}

	@Override
	public void start(Stage primaryStage) {
		this.thermocycler = new Thermocycler();
		ThermocyclerWorker.start(this.thermocycler);

		log.debug("Initializing GUI start");
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Thermocycler");
		initRootLayout();
		log.debug("Initializing GUI end");
	}

	public void updateUI() {
		this.controller.onUpdateUI();

	}

}
