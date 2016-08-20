package com.codlex.thermocycler.view;

import java.io.IOException;

import com.codlex.thermocycler.logic.Thermocycler;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ThermocyclerGUI extends Application {

	public static void main(String[] args) {
		launch(new String[0]);
	}

	private Stage primaryStage;

	private AnchorPane rootLayout;

	private Thermocycler thermocycler;

	/**
	 * Returns the main stage.
	 *
	 * @return
	 */
	public Stage getPrimaryStage() {
		return this.primaryStage;
	}

	// /**
	// * Shows the person overview inside the root layout.
	// */
	// public void showPersonOverview() {
	// try {
	// // Load person overview.
	// FXMLLoader loader = new FXMLLoader();
	// loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
	// AnchorPane personOverview = (AnchorPane) loader.load();
	//
	// // Set person overview into the center of root layout.
	// rootLayout.setCenter(personOverview);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(
					ThermocyclerGUI.class.getResource("TestScene.fxml"));
			this.rootLayout = (AnchorPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(this.rootLayout);
			this.primaryStage.setScene(scene);
			this.primaryStage.show();

			TestController controller = loader.getController();
			controller.setModel(this.thermocycler);
			controller.bind();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void start(Stage primaryStage) {
		this.thermocycler = new Thermocycler();
		// ThermocyclerWorker.start(thermocycler);
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Thermocycler");
		// this.primaryStage.setFullScreen(true);
		initRootLayout();

		// showPersonOverview();
	}

}
