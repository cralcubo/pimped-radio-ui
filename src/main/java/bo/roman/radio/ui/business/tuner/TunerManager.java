package bo.roman.radio.ui.business.tuner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.CategoryDaoController;
import bo.radio.tuner.StationDaoController;
import bo.radio.tuner.TunerDaoController;
import bo.radio.tuner.business.TunerBusiness;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.utilities.LoggerUtils;

public class TunerManager {
	private final Logger logger = LoggerFactory.getLogger(TunerManager.class);
	
	private static final String DBPROPS_PATH = "src/main/resources/database.properties";
	private static TunerDaoController tunerDao;
	
	private static TunerManager instance;
	
	private TunerManager() {
		Properties databaseProperties = new Properties();
		try (FileInputStream fis = new FileInputStream(DBPROPS_PATH)) {
			databaseProperties.load(fis);
			tunerDao = new TunerBusiness(databaseProperties);
		} catch (FileNotFoundException e) {
			throw new RuntimeException(String.format(
					"There was an error loading the DB properties. The file %s does not exist.", DBPROPS_PATH), e);
		} catch (IOException e) {
			throw new RuntimeException(String.format("There was an error reading the DB properties.", DBPROPS_PATH), e);
		}
	}
	
	public static TunerManager getInstance() {
		if (instance == null) {
			instance = new TunerManager();
		}
		return instance;
	}
	
	public StationDaoController getStationDaoInstance() {
		return tunerDao.getStationControllerInstance();
	}
	
	public CategoryDaoController getCategoryDaoInstance() {
		return tunerDao.getCategoryControllerInstance();
	}
	
	public void initializeTunerDatabase() throws TunerPersistenceException {
		LoggerUtils.logDebug(logger, () -> "Initializing Tuner Database.");
		tunerDao.init();
	}

}
