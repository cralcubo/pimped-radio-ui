package bo.roman.radio.ui.controller;

import java.net.MalformedURLException;
import java.net.URL;

import bo.roman.radio.ui.business.RadioStreamManager;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class StreamInputController {
	private Stage dialogStage;
	
	@FXML
	private TextArea textArea;
	
	@FXML
	private Button okButton;
	
	@FXML
	private void initialize() { }
	
	@FXML
	private void handleOk() {
		if(isInputValid()) {
			RadioStreamManager.setLastStream(textArea.getText());
			dialogStage.close();
		}
	}

	private boolean isInputValid() {
		try {
			return new URL(textArea.getText()) != null;
		} catch (MalformedURLException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.initOwner(dialogStage);
			alert.setTitle("Invalid URI");
			alert.setHeaderText("Please correct invalid field");
			alert.setContentText(e.getMessage());
			alert.showAndWait();
			return false;
		}
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}

}
