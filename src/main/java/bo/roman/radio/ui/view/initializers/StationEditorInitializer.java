package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Station;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.StationEditorController;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class StationEditorInitializer implements Initializable{
	private final static Logger log = LoggerFactory.getLogger(StationEditorInitializer.class);
	
	private static final String FXML_PATH = "src/main/resources/fxml/StationEditor.fxml";

	private StationEditorController controller;
	
	private final Stage rootStage;
	
	private static StationEditorInitializer instance;
	
	private Stage thisStage;
	
	private StationEditorInitializer(TunerLayoutInitializer rootInitialzer) {
		this.rootStage = rootInitialzer.getStage();
	}
	
	public static StationEditorInitializer getInstance(TunerLayoutInitializer rootInitialzer) {
		if(instance == null){
			instance = new StationEditorInitializer(rootInitialzer);
		}
		return instance;
	}

	@Override
	public void initialize() {
		LoggerUtils.logDebug(log, () -> "Initializing StationEditor View");
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(Paths.get(FXML_PATH).toUri().toURL());
			HBox hBox = loader.load();
			controller = loader.getController();
			
			thisStage = new Stage();
			thisStage.setTitle("Station Editor");
			thisStage.initModality(Modality.WINDOW_MODAL);
			thisStage.initOwner(rootStage);
			
			Scene scene = new Scene(hBox);
			thisStage.setScene(scene);
			
			thisStage.setOnCloseRequest(eh -> controller.clearTextArea());
			
			controller.setStage(thisStage);
		} catch (MalformedURLException e) {
			throw new RuntimeException("There was an error finding the FXML file.", e);
		} catch (IOException e) {
			throw new RuntimeException("There was an error loading the StreamInput view.", e);
		}
		
	}
	
	public void showAndWait(Station station) {
		controller.loadEditor(station);
		thisStage.showAndWait();
	}

}
