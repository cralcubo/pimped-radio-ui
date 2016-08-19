package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.CategoryCreatorController;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoryCreatorInitializer implements Initializable {
	private final Logger log = LoggerFactory.getLogger(CategoryCreatorInitializer.class);
	
	private static final String FXML_PATH = "src/main/resources/fxml/CategoryCreator.fxml";
	
	private static CategoryCreatorInitializer instance;
	
	private CategoryCreatorController controller;
	
	private final TunerLayoutInitializer rootInitialzer;
	
	private final Stage rootStage;
	private Stage thisStage;
	
	private CategoryCreatorInitializer(TunerLayoutInitializer rootInitialzer) {
		this.rootInitialzer = rootInitialzer;
		this.rootStage = rootInitialzer.getStage();
	}
	
	public static CategoryCreatorInitializer getInstance(TunerLayoutInitializer rootInitialzer) {
		if(instance == null) {
			instance = new CategoryCreatorInitializer(rootInitialzer);
		}
		return instance;
	}
	
	@Override
	public void initialize() {
		LoggerUtils.logDebug(log, () -> "Initializing CategoryCreator View");
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(Paths.get(FXML_PATH).toUri().toURL());
			HBox hBox = loader.load();
			controller = loader.getController();
			controller.setRootInitializer(rootInitialzer);
			
			thisStage = new Stage();
			thisStage.setTitle("Category Creator");
			thisStage.initModality(Modality.WINDOW_MODAL);
			thisStage.initOwner(rootStage);
			
			Scene scene = new Scene(hBox);
			thisStage.setScene(scene);
			
			thisStage.setOnCloseRequest(eh -> controller.clearTextArea());
			
		} catch (MalformedURLException e) {
			throw new RuntimeException("There was an error finding the FXML file.", e);
		} catch (IOException e) {
			throw new RuntimeException("There was an error loading the StreamInput view.", e);
		}
	}

	public void showAndWait() {
		thisStage.showAndWait();
	}

}
