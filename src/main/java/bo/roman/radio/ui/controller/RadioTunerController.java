package bo.roman.radio.ui.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.CategoryController;
import bo.radio.tuner.StationController;
import bo.radio.tuner.TunerController;
import bo.radio.tuner.business.TunerBusiness;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.model.StationInformation;
import javafx.scene.control.Button;

public class RadioTunerController implements Initializable {
	private final static Logger logger = LoggerFactory.getLogger(RadioTunerController.class);
	
	private static final String DBPROPS_PATH = "src/main/resources/database.properties";
	
	private final StationController stationController;
	private final CategoryController categoryController;
	private final TunerController tunerController;
	
	private final Button addStation;

	public RadioTunerController(Button addStation) {
		this.addStation = addStation;
		
		Properties databaseProperties = new Properties();
		try (FileInputStream fis = new FileInputStream(DBPROPS_PATH)) {
			databaseProperties.load(fis);
			tunerController = new TunerBusiness(databaseProperties);
			stationController = tunerController.getStationControllerInstance();
			categoryController = tunerController.getCategoryControllerInstance();
			
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format(
					"There was an error loading the DB properties. The file %s does not exist.", DBPROPS_PATH), e);
		} catch (IOException e) {
			throw new RuntimeException(String.format("There was an error reading the DB properties.", DBPROPS_PATH), e);
		}
	}

	@Override
	public void initialize() {
		try {
			tunerController.init();
		} catch (TunerPersistenceException e) {
			logger.error("There was an error initializing the Radio Tuner Database. Tuner functionalities disabled.");
			disableTuner();
		}
	}

	public void addStation(StationInformation rsi) {
		Station station = new Station(rsi.getName(), rsi.getUrl());
		try {
			stationController.saveStation(station);
		} catch (TunerPersistenceException e) {
			logger.error("Station could not be saved.", e);
		}
	}
	
	/**
	 * Due to errors connecting with the Database, all the functionality
	 * of the tuner is disabled.
	 */
	private void disableTuner() {
		//Disable addStation button.
		addStation.setDisable(true);
	}

}
