package bo.roman.radio.ui.controller.tuner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.CategoryController;
import bo.radio.tuner.StationController;
import bo.radio.tuner.TunerController;
import bo.radio.tuner.business.TunerBusiness;
import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.controller.displayer.Initializable;
import bo.roman.radio.ui.model.StationInformation;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.scene.control.Button;

public class RadioTunerController implements Initializable {
	private final static Logger logger = LoggerFactory.getLogger(RadioTunerController.class);
	
	private static final String DBPROPS_PATH = "src/main/resources/database.properties";

	private static final String DEAFAULTCATEGORY_NAME = "General";
	
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
			LoggerUtils.logDebug(logger, () -> "Initializing Tuner Database.");
			tunerController.init();
		} catch (TunerPersistenceException e) {
			logger.error("There was an error initializing the Radio Tuner Database. Tuner functionalities disabled.");
			disableTuner();
		}
	}
	
	public void addStation(StationInformation rsi) throws TunerPersistenceException {
		LoggerUtils.logDebug(logger,
				() -> "No cateory names found for saving the RadioStation, setting a default category name.");
		addStation(rsi, Arrays.asList(DEAFAULTCATEGORY_NAME));
	}

	public void addStation(StationInformation rsi, List<String> catergoryNames) throws TunerPersistenceException {
		if(catergoryNames == null || catergoryNames.isEmpty()) {
			throw new IllegalArgumentException("To save a radio station, it should have a category.");
		}
		
		logger.info("Adding new Radio Station: {} with categories: {}", rsi, catergoryNames);
		
		Station station = new Station(rsi.getName(), rsi.getStreamUrl());
		station.setBitRate(rsi.getBitRate());
		station.setCodec(rsi.getCodec());
		station.setSampleRate(rsi.getSampleRate());

		for (String n : catergoryNames) {
			Category category = new Category(n);
			category.getStations().add(station);
			station.getCategories().add(category);
		}
		Station savedStation = stationController.saveStation(station);
		logger.info("Station [{}] saved.", savedStation.getName());
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
