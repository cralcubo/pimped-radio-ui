package bo.roman.radio.ui;

import java.util.Arrays;
import java.util.List;

import bo.roman.radio.ui.model.AlertMessage;
import bo.roman.radio.ui.view.initializers.AlertInitializer;
import bo.roman.radio.ui.view.initializers.RadioPlayerInitializer;
import bo.roman.radio.ui.view.initializers.TunerLayoutInitializer;
import javafx.application.Application;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
	private RadioPlayerInitializer radioPlayerInitializer;
	private AlertInitializer alertInitializer;
	private TunerLayoutInitializer tunerInitializer;
	
	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);

		// Instantiate all the UI initializers
		radioPlayerInitializer = RadioPlayerInitializer.getInstance(primaryStage, this);
		alertInitializer = AlertInitializer.getInstance(primaryStage);
		tunerInitializer = TunerLayoutInitializer.getInstance(primaryStage);
		
		List<Initializable> initializers = Arrays.asList(radioPlayerInitializer, tunerInitializer, alertInitializer);
		initializers.forEach(Initializable::initialize);

		// Show Radio Player UI
		radioPlayerInitializer.show();
	}

	public void showTuner() {
		tunerInitializer.showAndWait();
	}

	public void triggerAlert(AlertType alertType, AlertMessage alertMessage) {
		alertInitializer.triggerAlert(alertType, alertMessage);
	}

	public TunerLayoutInitializer getTunerLayoutInitializer() {
		return tunerInitializer;
	}
}
