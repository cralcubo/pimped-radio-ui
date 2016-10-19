package bo.roman.radio.ui.controller;

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
import javafx.util.StringConverter;

public class StationEditorController {

	private final Logger log = LoggerFactory.getLogger(StationEditorController.class);

	@FXML
	TextArea textArea;
	@FXML
	Button saveButton;
	@FXML
	CheckComboBox<Category> comboBox;

	private Station station;

	private final StationManager stationManager;
	private final CategoryManager categoryManager;
	
	private Stage thisStage;

	public StationEditorController() {
		this.stationManager = StationManager.getInstance();
		this.categoryManager = CategoryManager.getInstance();
	}
	
	private void loadCombobox() {
		comboBox.getItems().clear();
		try {
			comboBox.getItems().addAll(categoryManager.getAllCategories());
			comboBox.setConverter(new StringConverter<Category>() {
				
				@Override
				public String toString(Category c) {
					return c.getName();
				}
				
				@Override
				public Category fromString(String name) {
					try {
						return categoryManager.findCategoryByName(name).orElseGet(() -> new Category(name));
					} catch (TunerPersistenceException e) {
						log.error("Error retrieving a category from DB.");
						return null;
					}
				}
			});
			comboBox.getCheckModel().clearChecks();
		} catch (TunerPersistenceException e1) {
			log.error("Error loading all the categories in the ComboBox.", e1);
		}
	}

	@FXML
	public void saveAction() {
		LoggerUtils.logDebug(log, () -> "Updating Station: " + station);
		try {
			station.getCategories().clear();
			for(Category c : comboBox.getCheckModel().getCheckedItems()) {
				station.getCategories().add(c);
			}
			station.setName(textArea.getText());
			stationManager.updateStation(station);
			thisStage.close();
		} catch (TunerPersistenceException e) {
			log.error("There was an error updating a station.", e);
		}
	}

	public void loadEditor(Station station) {
		try {
			stationManager.findStation(station).ifPresent(s -> {
				this.station = s;
				// Load items in the combobox
				loadCombobox();
				// We set the Name of the station in the text area
				textArea.setText(s.getName());
				// We set as checked the categories of this station in the combobox
				s.getCategories().forEach(c -> comboBox.getCheckModel().check(c));
			});
			
		} catch (TunerPersistenceException e) {
			log.error("There was an error finding the requested station to update.");
		}
	}

	public void clearTextArea() {
		textArea.clear();
	}
	
	public void setStage(Stage stage) {
		this.thisStage = stage;
	}

}
