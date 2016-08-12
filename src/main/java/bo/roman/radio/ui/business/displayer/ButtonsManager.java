package bo.roman.radio.ui.business.displayer;

import java.util.List;

import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.StationsManager;
import bo.roman.radio.ui.model.StationInformation;
import javafx.scene.control.Button;

public class ButtonsManager implements Initializable {
	private final Button addStation;
	private final StationsManager stationsManager;
	
	public ButtonsManager(Button addStation, StationsManager stationsManager) {
		this.addStation = addStation;
		this.stationsManager = stationsManager;
	}
	
	@Override
	public void initialize() {

	}
	
	public void addStation(StationInformation rsi) throws TunerPersistenceException {
		stationsManager.addStation(rsi);
	}
	
	public void addStation(StationInformation rsi, List<String> catergoryNames) throws TunerPersistenceException {
		stationsManager.addStation(rsi, catergoryNames);
	}
	
	/**
	 * Due to errors connecting with the Database, all the functionality
	 * of the tuner is disabled.
	 */
	public void disableAddstation() {
		//Disable addStation button.
		addStation.setDisable(true);
	}

}
