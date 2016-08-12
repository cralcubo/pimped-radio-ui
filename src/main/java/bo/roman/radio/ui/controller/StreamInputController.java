package bo.roman.radio.ui.controller;

import java.net.MalformedURLException;
import java.net.URL;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.RadioStationInfoManager;
import bo.roman.radio.ui.model.StationInformation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class StreamInputController implements Initializable {
	private Stage dialogStage;
	
	@FXML
	private TextArea textArea;
	
	@FXML
	private Button okButton;
	
	@FXML
	public void initialize() { 
		textArea.clear();
	}
	
	@FXML
	private void handleOk() {
		if(isInputValid()) {
			RadioStationInfoManager.setCurrentStationPlaying(new StationInformation("NO_NAME", textArea.getText()));
			dialogStage.close();
		} else {
			textArea.setText(String.format("Invalid URI[%s]. Plase set a valid URI", textArea.getText()));
		}
	}
	
	public void setDialogStage(Stage dialogStage) {
		this.dialogStage = dialogStage;
	}
	
	public void clearFields() {
		initialize();
	}
	
	private boolean isInputValid() {
		try {
			return new URL(textArea.getText()) != null;
		} catch (MalformedURLException e) {
			return false;
		}
	}

}
