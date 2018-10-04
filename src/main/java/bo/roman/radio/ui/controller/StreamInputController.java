package bo.roman.radio.ui.controller;

import java.net.MalformedURLException;
import java.net.URL;

import bo.radio.tuner.entities.Station;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.RadioDisplayerManager;
import bo.roman.radio.ui.business.RadioPlayerManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
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
		textArea.setOnKeyPressed(e -> {
			if(KeyCode.ENTER.equals(e.getCode())) {
				handleOk();
			}
		});
	}
	
	@FXML
	private void handleOk() {
		if(isInputValid()) {
			RadioPlayerManager rpm = RadioPlayerManager.getInstance();
			Station station = new Station("NO_NAME", textArea.getText());
			
			rpm.stop();
			RadioDisplayerManager.getInstance().reloadUI();
			
			rpm.play(station);
			
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
