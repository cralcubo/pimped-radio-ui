package bo.roman.radio.ui;

import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import bo.roman.radio.ui.model.AlertMessage;
import bo.roman.radio.ui.view.initializers.AlertInitializer;
import bo.roman.radio.ui.view.initializers.RadioPlayerInitializer;
import bo.roman.radio.ui.view.initializers.TunerLayoutInitializer;
import javafx.application.Application;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
	private static final String LOGO_URI = "src/main/resources/logo/pimped-radio-glossy.jpeg";

	private RadioPlayerInitializer radioPlayerInitializer;
	private AlertInitializer alertInitializer;
	private TunerLayoutInitializer tunerInitializer;
	
	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("restriction")
	@Override
	public void start(Stage primaryStage) {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);
		com.apple.eawt.Application.getApplication().setDockIconImage(new ImageIcon(LOGO_URI).getImage());

		// Instantiate all the UI initializers
		radioPlayerInitializer = RadioPlayerInitializer.getInstance(primaryStage, this);
		alertInitializer = AlertInitializer.getInstance(primaryStage);
		tunerInitializer = TunerLayoutInitializer.getInstance(primaryStage);
		
		List<Initializable> initializers = Arrays.asList(radioPlayerInitializer, tunerInitializer, alertInitializer);
		initializers.forEach(Initializable::initialize);

		// Show Radio Player UI
		radioPlayerInitializer.show();
	}

	public void showInputStreamDialog() {
		tunerInitializer.showAndWait();
	}

	public void triggerAlert(AlertType alertType, AlertMessage alertMessage) {
		alertInitializer.triggerAlert(alertType, alertMessage);
	}
}
