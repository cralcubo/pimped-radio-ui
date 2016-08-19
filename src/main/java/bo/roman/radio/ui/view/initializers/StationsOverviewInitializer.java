package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.StationsOverviewController;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

public class StationsOverviewInitializer implements Initializable {
	private final Logger logger = LoggerFactory.getLogger(StationsOverviewInitializer.class);
	
	private final static String FXML_PATH = "src/main/resources/fxml/StationsOverview.fxml";
	
	private final TunerLayoutInitializer rootInitializer;
	
	private static  StationsOverviewInitializer instance;
	
	private AnchorPane stationsPane;
	
	
	private StationsOverviewController controller;
	

	public StationsOverviewInitializer(TunerLayoutInitializer rootInitializer) {
		this.rootInitializer = rootInitializer;
	}

	public static StationsOverviewInitializer getInstance(TunerLayoutInitializer rootInitializer) {
		if(instance == null) {
			instance = new StationsOverviewInitializer(rootInitializer);
		}
		return instance;
	}

	@Override
	public void initialize() {
		LoggerUtils.logDebug(logger, () -> "Initializing StatationsOverview View");
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(Paths.get(FXML_PATH).toUri().toURL());
			stationsPane = loader.load();
			
			controller = loader.getController();
			controller.setRootInitializer(rootInitializer);
			
		} catch (MalformedURLException e) {
			throw new RuntimeException("There was an error finding the FXML file.", e);
		} catch (IOException e) {
			throw new RuntimeException("There was an error loading the StatationsOverview view.", e);
		}
	}
	
	public AnchorPane getStationsPane() {
		return stationsPane;
	}

	public void loadStations() {
		controller.loadStations();
	}
}
