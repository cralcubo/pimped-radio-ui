package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Category;
import bo.radio.tuner.entities.Station;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TunerLayoutInitializer implements Initializable {
	private final Logger logger = LoggerFactory.getLogger(TunerLayoutInitializer.class);

	private static final String FXML_PATH = "src/main/resources/fxml/TunerLayout.fxml";

	private static TunerLayoutInitializer instance;

	private final Stage primaryStage;

	private Stage thisStage;
	private StreamInputInitializer streamInputInitializer;
	private StationsOverviewInitializer stationsOverviewInitializer;
	private CategoryCreatorEditorInitializer categoryCreatorInitializer;
	private CategoryCreatorEditorInitializer categoryEditorInitializer;
	private StationEditorInitializer stationEditorInitializer;

	public TunerLayoutInitializer(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	public static TunerLayoutInitializer getInstance(Stage primaryStage) {
		if (instance == null) {
			instance = new TunerLayoutInitializer(primaryStage);
		}
		return instance;
	}

	@Override
	public void initialize() {
		LoggerUtils.logDebug(logger, () -> "Initializing TunerLayoutInitializer View");
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(Paths.get(FXML_PATH).toUri().toURL());
			BorderPane tunerPane = loader.load();

			thisStage = new Stage();
			thisStage.setTitle("Radio Tuner");
			thisStage.initModality(Modality.WINDOW_MODAL);
			thisStage.initOwner(primaryStage);

			Scene scene = new Scene(tunerPane);
			thisStage.setScene(scene);

			// Initialize the components in the Tuner
			initializeInputStreamView(tunerPane);
			initializeStationsOverview(tunerPane);
			initializeStationEditor();
			initializeCategoryCreator();
			initializeCategoryEditor();

		} catch (MalformedURLException e) {
			throw new RuntimeException("There was an error finding the FXML file.", e);
		} catch (IOException e) {
			throw new RuntimeException("There was an error loading the TunerLayout view.");
		}
	}

	private void initializeStationEditor() {
		stationEditorInitializer = StationEditorInitializer.getInstance(this);
		stationEditorInitializer.initialize();
		
	}

	private void initializeCategoryCreator() {
		categoryCreatorInitializer = CategoryCreatorEditorInitializer.getCreatorInstance(this);
		categoryCreatorInitializer.initialize();
	}
	
	private void initializeCategoryEditor() {
		categoryEditorInitializer = CategoryCreatorEditorInitializer.getEditorInstance(this);
		categoryEditorInitializer.initialize();
	}

	private void initializeStationsOverview(BorderPane tunerPane) {
		stationsOverviewInitializer = StationsOverviewInitializer.getInstance(this);
		stationsOverviewInitializer.initialize();
		// Set it in the body
		tunerPane.setCenter(stationsOverviewInitializer.getStationsPane());
	}

	private void initializeInputStreamView(BorderPane tunerPane) {
		streamInputInitializer = StreamInputInitializer.getInstance(this);
		streamInputInitializer.initialize();
		// Set it in the Top
		tunerPane.setTop(streamInputInitializer.getDialogBox());
	}

	public void showAndWait() {
		streamInputInitializer.clearFields();
		thisStage.showAndWait();
	}
	
	public void showAddCategory() {
		categoryCreatorInitializer.showAndWait();
	}
	
	public void showEditCategory(Category c) {
		categoryEditorInitializer.showAndWait(c);
	}
	
	public void showEditStation(Station s) {
		stationEditorInitializer.showAndWait(s);
	}

	public void loadStationsOverview() {
		stationsOverviewInitializer.loadStations();
	}

	public Stage getStage() {
		return thisStage;
	}

	public StationEditorInitializer getStationEditorInitializer() {
		return stationEditorInitializer;
	}

}
