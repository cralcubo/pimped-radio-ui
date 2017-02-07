package bo.roman.radio.ui.business.tuner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.StationDaoApi;
import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.utilities.LoggerUtils;

public class StationManager {
	
private final static Logger logger = LoggerFactory.getLogger(StationManager.class);

	private static StationManager instance;
	private final CategoryManager categoryManager;
	private final TunerManager tunerManager;
	
	private final StationDaoApi stationDao;


	private StationManager() {
		tunerManager = TunerManager.getInstance();
		categoryManager = CategoryManager.getInstance();
		
		stationDao = tunerManager.getStationDaoInstance();
	}
	
	public static StationManager getInstance() {
		if(instance == null) {
			instance = new StationManager();
		}
		
		return instance;
	}

	public Station createStation(Station s) throws TunerPersistenceException {
		logger.info("Adding new Radio Station: {}", s);
		List<Category> toCheckCategories = new ArrayList<>(s.getCategories());
		s.getCategories().clear();
		for (Category c : toCheckCategories) {
			s.getCategories().add(categoryManager.createCategory(c));
		}
		Station savedStation = stationDao.saveStation(s);
		logger.info("Station [{}] saved.", savedStation.getName());
		// Update the Tuner Table
		tunerManager.addStation(savedStation);
		return savedStation;
	}

	public Optional<Station> findStation(Station station) throws TunerPersistenceException {
		return stationDao.findStation(station);
	}

	public Optional<Station> findStationByName(String name) throws TunerPersistenceException {
		return stationDao.findStationByName(name);
	}

	public void deleteStation(Station s) throws TunerPersistenceException {
		LoggerUtils.logDebug(logger, () -> "Deleting station: " + s);
		Station toRemove = findStation(s).orElse(s);
		stationDao.removeStation(toRemove);
		tunerManager.removeStation(toRemove);
	}

	public void updateStation(Station station) throws TunerPersistenceException {
		tunerManager.updateStation(stationDao.findStation(station).orElseGet(() -> station), station);
		stationDao.updateStation(station);
	}
	
	public Optional<Station> getLastPlayedStation() throws TunerPersistenceException {
		LoggerUtils.logDebug(logger, () -> "Retrieving the last played Station." );
		return stationDao.getLastPlayedStation();
	}
	
	public void saveLastPlayedStation(Station station) throws TunerPersistenceException {
		LoggerUtils.logDebug(logger, () -> "Saving the last played Station." );
		stationDao.saveLastPlayedStation(station);
	}
	

}
