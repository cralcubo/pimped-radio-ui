package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Category;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.CategoryCreatorEditorController;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.ResourceFinder;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class CategoryCreatorEditorInitializer implements Initializable {
	private final Logger log = LoggerFactory.getLogger(CategoryCreatorEditorInitializer.class);
	
	private static final String FXML_PATH = "resources/fxml/CategoryCreatorEditor.fxml";
	
	private static CategoryCreatorEditorInitializer creatorInstance;
	private static CategoryCreatorEditorInitializer editorInstance;
	
	private CategoryCreatorEditorController controller;
	
	private final TunerLayoutInitializer rootInitialzer;
	
	private final Stage rootStage;
	private final boolean isCreator;
	private Stage thisStage;
	
	private CategoryCreatorEditorInitializer(TunerLayoutInitializer rootInitialzer, boolean isCreator) {
		this.rootInitialzer = rootInitialzer;
		this.rootStage = rootInitialzer.getStage();
		this.isCreator = isCreator;
	}
	
	public static CategoryCreatorEditorInitializer getCreatorInstance(TunerLayoutInitializer rootInitialzer) {
		if(creatorInstance == null) {
			creatorInstance = new CategoryCreatorEditorInitializer(rootInitialzer, true);
		}
		return creatorInstance;
	}
	
	public static CategoryCreatorEditorInitializer getEditorInstance(TunerLayoutInitializer rootInitialzer) {
		if(editorInstance == null) {
			editorInstance = new CategoryCreatorEditorInitializer(rootInitialzer, false);
		}
		return editorInstance;
	}
	
	@Override
	public void initialize() {
		LoggerUtils.logDebug(log, () -> "Initializing CategoryCreatorEditor View");
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(ResourceFinder.findFileUrl(FXML_PATH));
			HBox hBox = loader.load();
			thisStage = new Stage();
			thisStage.initModality(Modality.WINDOW_MODAL);
			thisStage.initOwner(rootStage);
			
			Scene scene = new Scene(hBox);
			thisStage.setScene(scene);
			
			thisStage.setOnCloseRequest(eh -> controller.clearTextArea());
			
			controller = loader.getController();
			controller.setRootInitializer(rootInitialzer);
			controller.setMode(isCreator);
			controller.setStage(thisStage);
			controller.initializeLabels();
			
		} catch (MalformedURLException e) {
			throw new RuntimeException("There was an error finding the FXML file.", e);
		} catch (IOException e) {
			throw new RuntimeException("There was an error loading the StreamInput view.", e);
		}
	}
	
	public void showAndWait() {
		thisStage.showAndWait();
	}
	
	public void showAndWait(Category c) {
		controller.setCategory(c);
		thisStage.showAndWait();
	}

}
