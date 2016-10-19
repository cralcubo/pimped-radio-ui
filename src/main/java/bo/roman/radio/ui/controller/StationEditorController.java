package bo.roman.radio.ui.controller;

import java.util.stream.Collectors;

import org.controlsfx.control.CheckComboBox;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.business.tuner.CategoryManager;
import bo.roman.radio.ui.business.tuner.StationManager;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class StationEditorController {

	private final Logger log = LoggerFactory.getLogger(StationEditorController.class);

	@FXML
	TextArea textArea;
	@FXML
	Button saveButton;
	@FXML
	CheckComboBox<String> comboBox;

	private Station station;

	private final StationManager stationManager;
	private final CategoryManager categoryManager;
	
	private Stage thisStage;

	public StationEditorController() {
		this.stationManager = StationManager.getInstance();
		this.categoryManager = CategoryManager.getInstance();
	}
	
	@FXML
	private void initialize() {
		try {
			comboBox.getItems().addAll(categoryManager.getAllCategories().stream()
																		 .map(Category::getName)
																		 .collect(Collectors.toList()));
		} catch (TunerPersistenceException e) {
			log.error("Error loading all the categories in the ComboBox.", e);
		}
	}

	@FXML
	public void saveAction() {
		LoggerUtils.logDebug(log, () -> "Updating Station: " + station);
		if (!textArea.getText().equals(station.getName())) {
			try {
				for(String cn : comboBox.getCheckModel().getCheckedItems()) {
					categoryManager.findCategoryByName(cn)
								   .ifPresent(c -> station.getCategories().add(c));
				}
				station.setName(textArea.getText());
				stationManager.updateStation(station);
				thisStage.close();
			} catch (TunerPersistenceException e) {
				log.error("There was an error updating a station.", e);
			}
		}
	}

	public void loadStation(Station station) {
		this.station = station;
		// We set the Name of the station in the text area
		textArea.setText(station.getName());
		// We set as checked the categories of this station in the combobox
		station.getCategories().stream()
							  .map(Category::getName)
							  .forEach(cn -> comboBox.getCheckModel().check(cn));
	}

	public void clearTextArea() {
		textArea.clear();
	}
	
	public void setStage(Stage stage) {
		this.thisStage = stage;
	}

}
