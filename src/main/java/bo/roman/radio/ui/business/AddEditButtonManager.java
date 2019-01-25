package bo.roman.radio.ui.business;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.tuner.StationManager;
import bo.roman.radio.ui.view.initializers.StationEditorInitializer;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.scene.control.ToggleButton;

public class AddEditButtonManager implements Initializable {
	private final Logger logger = LoggerFactory.getLogger(AddEditButtonManager.class);
	
	private final ToggleButton addStation;
	private final StationManager stationsManager;
	private static AddEditButtonManager instance;
	
	
	private AddEditButtonManager(ToggleButton addStation) {
		this.addStation = addStation;
		this.stationsManager = StationManager.getInstance();
	}
	
	public static AddEditButtonManager getInstance() {
		return getInstance(null);
	}
	
	public static AddEditButtonManager getInstance(ToggleButton addStation) {
		if(instance == null) {
			if(addStation == null) {
				throw new IllegalStateException("To get an instance of a new AddEditButtonManager, a ToggleButton must be provided.");
			}
			instance = new AddEditButtonManager(addStation);
		}
		return instance;
	}
	
	@Override
	public void initialize() {
		// Disable the button because there is nothing to be saved yet
		disableButton();
	}
	
	public void addEditStation(Station stationToAddEdit, StationEditorInitializer stationEditorInitializer) throws TunerPersistenceException {
		if (addStation.isSelected()) {
			LoggerUtils.logDebug(logger, () -> "Adding a new Radio Station: " + stationToAddEdit);
			// Set the general category
			stationToAddEdit.getCategories().add(new Category("General"));
			stationsManager.createStation(stationToAddEdit);
		} else {
			LoggerUtils.logDebug(logger, () -> "Editing Radio Station: " + stationToAddEdit);
			enableEdit();
			// Trigger Station Editor
			stationEditorInitializer.showAndWait(stationToAddEdit);
		}
	}
	
	public void enableAdd(Station stationToAddEdit) {
		// Check if there will be an add or an edit action
		try {
			// Check if the station was already saved in DB to enable the Add or Edit actions
			Optional<Station> stationFound = stationsManager.findStation(stationToAddEdit);
			// Selected -> Edit
			// Unselected -> Add
			addStation.setDisable(false);
			addStation.setSelected(stationFound.isPresent());
		} catch (TunerPersistenceException e) {
			// There was an error requesting info from the database
			// log this information and keep the add button disabled.
			logger.error(
					"It is not possible to request information from the Database. Add station button is disabled.", e);
			disableButton();
		}
	}
	
	public void enableEdit() {
		addStation.setDisable(false);
		addStation.setSelected(true);
	}
	
	/**
	 * Due to errors connecting with the Database, all the functionality
	 * of the tuner is disabled.
	 */
	public void disableButton() {
		//Disable addStation button.
		addStation.setDisable(true);
	}
}
