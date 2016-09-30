package bo.roman.radio.ui.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.business.tuner.StationManager;
import bo.roman.radio.ui.view.initializers.TunerLayoutInitializer;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;

public class StationEditorController {

	private final Logger log = LoggerFactory.getLogger(StationEditorController.class);

	@FXML
	TextArea textArea;
	@FXML
	Button saveButton;
	@FXML
	ComboBox comboBox;

	private Station station;

	private final StationManager stationManager;

	private TunerLayoutInitializer rootInitializer;

	public StationEditorController() {
		this.stationManager = StationManager.getInstance();
	}

	@FXML
	public void saveAction() {
		LoggerUtils.logDebug(log, () -> "Updating Station: " + station);
		if (!textArea.getText().equals(station.getName())) {
			try {
				station.setName(textArea.getText());
				stationManager.updateStation(station);
				// Reload Tuner Displayer
				rootInitializer.loadStationsOverview();
			} catch (TunerPersistenceException e) {
				log.error("There was an error updating a station.", e);
			}
		}
	}

	public void setStation(Station station) {
		this.station = station;
		// We set the Name of the station in the text area
		textArea.setText(station.getName());
	}

	public void clearTextArea() {
		textArea.clear();
	}

	public void setRootInitializer(TunerLayoutInitializer rootInitialzer) {
		this.rootInitializer = rootInitialzer;
		
	}

}
