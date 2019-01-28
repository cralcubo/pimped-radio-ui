package bo.roman.radio.ui.controller;

import static java.util.Arrays.asList;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.entities.Station;
import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.ui.App;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.AddEditButtonManager;
import bo.roman.radio.ui.business.RadioDisplayerManager;
import bo.roman.radio.ui.business.RadioPlayerManager;
import bo.roman.radio.ui.business.StationPlayingManager;
import bo.roman.radio.ui.business.displayer.CoverArtManager;
import bo.roman.radio.ui.business.displayer.DockInfoManager;
import bo.roman.radio.ui.business.displayer.LabelsManager;
import bo.roman.radio.ui.business.displayer.SubInfoLabelsManager;
import bo.roman.radio.ui.business.observers.CodecObeserver;
import bo.roman.radio.ui.business.observers.PimpedRadioObserver;
import bo.roman.radio.ui.business.tuner.TunerManager;
import bo.roman.radio.ui.model.AlertMessage;
import bo.roman.radio.ui.view.initializers.TunerLayoutInitializer;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

public class RadioDisplayerController {
	private final static Logger logger = LoggerFactory.getLogger(RadioDisplayerController.class);

	private CoverArtManager coverArtManager;
	private DockInfoManager dockManager;
	private RadioPlayerManager radioPlayerManager;
	private LabelsManager labelsManager;
	private AddEditButtonManager addEditButtonManager;
	private RadioDisplayerManager displayerManager;
	private SubInfoLabelsManager subInfoLabelsManager;

	private App mainApp;

	@FXML
	private StackPane mainPane;

	@FXML
	private ImageView coverViewer;
	@FXML
	private Rectangle coverShader;
	@FXML
	private GridPane controlsPane;

	@FXML
	private Button close;
	@FXML
	private Button load;
	@FXML
	private ToggleButton addEditStation;
	@FXML
	private ToggleButton play;
	@FXML
	private Slider volume;
	@FXML
	private RadioButton pinInfo;
	
	/*
	 * Main info labels
	 */
	@FXML
	private Label mainLabel;
	@FXML
	private Label subLabel;
	@FXML
	private Label extraLabel;
	@FXML
	private Label codecLabel;
	
	/*
	 * Sub info labels
	 */
	@FXML
	private Label artistLabel;
	@FXML
	private Label songLabel;
	@FXML
	private Label radioLabel;
	

	@FXML
	private void initialize() {
		dockManager = DockInfoManager.getInstance();
		coverArtManager = CoverArtManager.getInstance(coverViewer, coverShader);
		labelsManager = LabelsManager.getInstance(mainLabel, subLabel, extraLabel, codecLabel);
		radioPlayerManager = RadioPlayerManager.getInstance(volume, play);
		subInfoLabelsManager = SubInfoLabelsManager.getInstance(artistLabel, songLabel, radioLabel);
		
		// Add observers to the radioPlayerManager
		radioPlayerManager.setCodecInfoObserver(new CodecObeserver(labelsManager));
		radioPlayerManager.setCoverObserver(new PimpedRadioObserver<CoverArtManager>(coverArtManager));
		radioPlayerManager.setLabelsObserver(new PimpedRadioObserver<LabelsManager>(labelsManager));
		radioPlayerManager.setDockObserver(new PimpedRadioObserver<DockInfoManager>(dockManager));
		radioPlayerManager.setSubInfoObserver(new PimpedRadioObserver<SubInfoLabelsManager>(subInfoLabelsManager));
		
		addEditButtonManager = AddEditButtonManager.getInstance(addEditStation);
		displayerManager = RadioDisplayerManager.getInstance(controlsPane);

		List<Initializable> controllers = asList(coverArtManager, //
				labelsManager, //
				radioPlayerManager, //
				addEditButtonManager, //
				displayerManager, //
				dockManager,
				labelsManager);
		controllers.forEach(Initializable::initialize);

		// Initialize Tuner Database
		try {
			TunerManager.getInstance().initializeTunerDatabase();
		} catch (TunerPersistenceException e) {
			logger.error("There was an error initializing the Tuner Database.");
			addEditButtonManager.disableButton();
		}
	}

	@FXML
	private void onMouseEntered() {
		pinUnselectedAction(RadioDisplayerManager::displayPlayerInformation);
	}

	@FXML
	private void onMouseExited() {
		pinUnselectedAction(RadioDisplayerManager::clearPlayerInformation);
	}

	private void pinUnselectedAction(Consumer<RadioDisplayerManager> action) {
		if (pinInfo.isSelected()) {
			displayerManager.unblurPlayerInformation();
		} else {
			action.accept(displayerManager);
		}
	}

	@FXML
	private void volumeAction() {
		radioPlayerManager.changeVolume();
	}

	@FXML
	private void playButtonAction() {
		if (play.isSelected()) {
			final Station si = StationPlayingManager.getStationToPlay();
			radioPlayerManager.play(si);
			
			StationPlayingManager.setCurrentStationPlaying(si);
			// Enable Add Station button
			addEditButtonManager.enableAdd(si);
		} else {
			radioPlayerManager.stop();
			displayerManager.reloadUI();
		}
	}

	@FXML
	private void loadTunerAction() {
		if (mainApp != null) {
			mainApp.showTuner();
		}
	}

	@FXML
	private void closeAction() {
		// Save last station played
		StationPlayingManager.saveLastStationPlayed();
		// Close the music player
		radioPlayerManager.close();
		// Close the program
		System.exit(1);
	}

	@FXML
	private void addEditAction() {
		Station currentStation = StationPlayingManager.getCompleteCurrentStationPlaying();
		if (currentStation != null) {
			try {
				TunerLayoutInitializer tli = mainApp.getTunerLayoutInitializer();
				addEditButtonManager.addEditStation(currentStation, tli.getStationEditorInitializer());
			} catch (TunerPersistenceException e) {
				logger.error("Station could not be edit/saved.", e);
				mainApp.triggerAlert(AlertType.ERROR, new AlertMessage.Builder()//
						.title("Add Radio Station")//
						.header("Error saving a Radio Station.")//
						.message(e.getMessage())//
						.build());
			}
		} else {
			logger.error("A Radio Station was tried to be editted/added but the operation failed.");
			mainApp.triggerAlert(AlertType.INFORMATION, new AlertMessage.Builder()//
					.title("Add Radio Station")//
					.header("Not enough information to save a Radio Station")//
					.message("A Radio Station was tried to be added but the operation failed.")//
					.build());
		}

	}

	public Rectangle getCoverShader() {
		return coverShader;
	}

	public void setMainApp(App mainApp) {
		this.mainApp = mainApp;
	}

}
