package com.codlex.thermocycler.view;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;

import com.codlex.thermocycler.logic.Thermocycler;
import com.codlex.thermocycler.logic.ThermocyclerWorker;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ThermocyclerGUI extends Application {

	public static void main(String[] args) {
		BasicConfigurator.configure();
		launch(new String[0]);
	}

	private Stage primaryStage;

	private Pane rootLayout;

	private Thermocycler thermocycler;

	private ThermocyclerScene currentScene;

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
			this.primaryStage.setFullScreen(true);
			this.primaryStage.setScene(scene);
			this.primaryStage.show();
			
			
			TestController controller = loader.getController();
			controller.setModel(this.thermocycler);
			controller.bind();
			
			setScene(ThermocyclerScene.FillInBaths);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void setScene(ThermocyclerScene scene) {
		this.currentScene = scene;
		Pane pane = scene.load(this.thermocycler, this);
		if (pane == null) {
			throw new RuntimeException(scene.name() + " couldn't be loaded!");
		}
		
		this.rootLayout.getChildren().set(0, pane);
		
		AnchorPane.setTopAnchor(pane,0.0);
		AnchorPane.setBottomAnchor(pane,0.0);
		AnchorPane.setLeftAnchor(pane,0.0);
		AnchorPane.setRightAnchor(pane, 300.0);
	}
	
	public void nextScene() {
		setScene(this.currentScene.nextScene());
	}

	@Override
	public void start(Stage primaryStage) {
		
		this.thermocycler = new Thermocycler();
		ThermocyclerWorker.start(thermocycler);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Thermocycler");
		// this.primaryStage.setFullScreen(true);
		initRootLayout();

		// showPersonOverview();
	}

	public void previousScene() {
		setScene(this.currentScene.previousScene());
	}

}
