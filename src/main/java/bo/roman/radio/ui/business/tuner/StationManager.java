package bo.roman.radio.ui.business.tuner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.CategoryDaoController;
import bo.radio.tuner.StationDaoController;
import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.utilities.LoggerUtils;

public class StationManager {
	
private final static Logger logger = LoggerFactory.getLogger(StationManager.class);

	private static final String DEAFAULTCATEGORY_NAME = "General";
	
	private static StationManager instance;
	
	private final StationDaoController stationDao;
	private final CategoryDaoController categoryDao;

	private StationManager() {
		stationDao = TunerManager.getInstance().getStationDaoInstance();
		categoryDao = TunerManager.getInstance().getCategoryDaoInstance();
	}
	
	public static StationManager getInstance() {
		if(instance == null) {
			instance = new StationManager();
		}
		
		return instance;
	}
	
	public Station addStation(Station s) throws TunerPersistenceException {
		LoggerUtils.logDebug(logger,
				() -> "No category names found for saving the RadioStation, setting a default category name.");
		return addStation(s, Arrays.asList(DEAFAULTCATEGORY_NAME));
	}

	public Station addStation(Station s, List<String> catergoryNames) throws TunerPersistenceException {
		if(catergoryNames == null || catergoryNames.isEmpty()) {
			throw new IllegalArgumentException("To save a radio station, it should have a category.");
		}
		
		logger.info("Adding new Radio Station: {} with categories: {}", s, catergoryNames);

		for (String n : catergoryNames) {
			Category category = new Category(n);
			category.getStations().add(s);
			category = categoryDao.createCategory(category);
			
			s.getCategories().add(category);
		}
		Station savedStation = stationDao.saveStation(s);
		logger.info("Station [{}] saved.", savedStation.getName());
		
		return savedStation;
	}

	public Optional<Station> findStation(Station station) throws TunerPersistenceException {
		return stationDao.findStation(station);
	}

	public Optional<Station> findStationByName(String name) throws TunerPersistenceException {
		return stationDao.findStationByName(name);
	}

}
