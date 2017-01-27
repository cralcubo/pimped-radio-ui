package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.StreamInputController;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.ResourceFinder;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class StreamInputInitializer implements Initializable {
	private final Logger log = LoggerFactory.getLogger(StreamInputInitializer.class);
	
	private static final String FXML_PATH = "resources/fxml/StreamInput.fxml";
	
	private final Stage rootStage;
	
	private static StreamInputInitializer instance;
	
	private HBox dialogBox;
	private StreamInputController controller;

	private StreamInputInitializer(TunerLayoutInitializer rootInitializer) {
		this.rootStage = rootInitializer.getStage();
	}

	public static StreamInputInitializer getInstance(TunerLayoutInitializer rootInitializer) {
		if (instance == null) {
			instance = new StreamInputInitializer(rootInitializer);
		}
		return instance;
	}

	@Override
	public void initialize() {
		LoggerUtils.logDebug(log, () -> "Initializing StreamInput View");
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(ResourceFinder.findFileUrl(FXML_PATH));
			dialogBox = loader.load();
			
    		controller = loader.getController();
    		controller.setDialogStage(rootStage);
		} catch (MalformedURLException e) {
			throw new RuntimeException("There was an error finding the FXML file.", e);
		} catch (IOException e) {
			throw new RuntimeException("There was an error loading the StreamInput view.", e);
		}
	}
	
	public HBox getDialogBox() {
		return dialogBox;
	}

	public void clearFields() {
		controller.clearFields();
	}
	

}
