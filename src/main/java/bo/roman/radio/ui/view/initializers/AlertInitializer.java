package bo.roman.radio.ui.view.initializers;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.model.AlertMessage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

public class AlertInitializer implements Initializable {

	private static AlertInitializer instance;
	
	private final Stage stage;

	private AlertInitializer(Stage stage) {
		this.stage = stage;
	}
	
	public static AlertInitializer getInstance(Stage stage) {
		if (instance == null) {
			instance = new AlertInitializer(stage);
		}
		return instance;
	}

	@Override
	public void initialize() {
	}
	
	public void triggerAlert(AlertType alertType, AlertMessage alertMessage) {
		Alert alert = new Alert(alertType);
		alert.initOwner(stage);
		alert.setTitle(alertMessage.getTitle());
		alert.setHeaderText(alertMessage.getHeader());
		alert.setContentText(alertMessage.getMessage());
		
		alert.showAndWait();
	}


}
