package bo.roman.radio.ui.controller.displayer;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.radio.tuner.exceptions.TunerPersistenceException;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.App;
import bo.roman.radio.ui.business.RadioStationInfoManager;
import bo.roman.radio.ui.controller.tuner.RadioTunerController;
import bo.roman.radio.ui.controller.util.NodeFader;
import bo.roman.radio.ui.model.AlertMessage;
import bo.roman.radio.ui.model.RadioPlayerInformation;
import bo.roman.radio.ui.model.StationInformation;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class RadioDisplayerController {
	private final static Logger logger = LoggerFactory.getLogger(RadioDisplayerController.class);
	
	private CoverArtController coverArtController;
	private RadioPlayerController radioPlayerController;
	private LabelsController labelsController;
	private RadioTunerController tunerController;
	
	private static final double MAXOPACITY_INFO = 1.0;
	private static final double MINOPACITY_INFO = 0.0;
	
	private final NodeFader fader = new NodeFader(200);
	
	private App mainApp;
	
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
	private Button addStation;
	@FXML
	private ToggleButton play;
	@FXML
	private Slider volume;
	
	
	@FXML
	private Label mainLabel;
	@FXML
	private Label subLabel;
	@FXML
	private Label extraLabel;
	@FXML
	private Label codecLabel;
	
	@FXML
	private void initialize() {
		coverArtController = new CoverArtController(coverViewer, coverShader);
		labelsController = new LabelsController(mainLabel, subLabel, extraLabel, codecLabel);
		radioPlayerController = new RadioPlayerController(volume);
		tunerController = new RadioTunerController(addStation);
		
		List<Initializable> controllers = Arrays.asList(coverArtController, labelsController, radioPlayerController, tunerController);
		controllers.forEach(Initializable::initialize);
	}
	
	@FXML
	private void onMouseEntered() {
		coverArtController.shadeIt(fader);
		// Make Player Controls and Song Info appear 
		fader.fadeNode(MINOPACITY_INFO, MAXOPACITY_INFO, controlsPane);
	}
	
	@FXML
	private void onMouseExited() {
		coverArtController.clearIt(fader);
		// Make Player Controls and Song Info disappear
		fader.fadeNode(MAXOPACITY_INFO, MINOPACITY_INFO, controlsPane);
	}
	
	@FXML
	private void volumeAction() {
		radioPlayerController.changeVolume();
	}
	
	@FXML
	private void playButtonAction() {
		if (play.isSelected()) {
			Optional<StationInformation> currentStation = RadioStationInfoManager.getCurrentStationPlaying();
			Optional<StationInformation> lastStationPlayed = RadioStationInfoManager.getLastStationPlaying();
			if(!currentStation.isPresent() && !lastStationPlayed.isPresent()) {
				logger.warn("There are no Radio Stations to be played.");
				mainApp.triggerAlert(AlertType.INFORMATION, new AlertMessage.Builder()
																			.title("Play Radio Station")
																			.header("There were no Radio Stations played yet.")
																			.message("Load a Radio Station to play something.")
																			.build());
			}
			
			final StationInformation si = currentStation.orElseGet(() -> lastStationPlayed.get());
			radioPlayerController.play(si.getStreamUrl());
		} else {
			radioPlayerController.stop();
			coverArtController.initialize();
			labelsController.initialize();
		}
	}
	
	@FXML
	private void loadStreamAction() {
		if(mainApp != null) {
			mainApp.showInputStreamDialog();
		}
	}
	
	@FXML
	private void closeAction() {
		radioPlayerController.close();
		System.exit(1);
	}
	
	@FXML
	private void addAction() {
		LoggerUtils.logDebug(logger, () -> "Adding a new Radio Station");
		Optional<StationInformation> currentStation = RadioStationInfoManager.getCompleteCurrentStationPlaying();
		if(currentStation.isPresent()) {
			try {
				tunerController.addStation(currentStation.get());
			} catch (TunerPersistenceException e) {
				logger.error("Station could not be saved.", e);
				mainApp.triggerAlert(AlertType.ERROR, new AlertMessage.Builder()
																			.title("Add Radio Station")
																			.header("Error saving a Radio Station.")
																			.message(e.getMessage())
																			.build());
			}
		}else {
			logger.error("A Radio Station was tried to be added but the operation failed.");
			mainApp.triggerAlert(AlertType.INFORMATION, new AlertMessage.Builder()
																		.title("Add Radio Station")
																		.header("Not enough information to save a Radio Station")
																		.message("A Radio Station was tried to be added but the operation failed.")
																		.build());
		}
		
	}

	public Rectangle getCoverShader() {
		return coverShader;
	}
	
	public void addObservers(List<Observer<RadioPlayerEntity>> playerEntityObservers, List<Observer<CodecInformation>> codecObservers, List<Observer<ErrorInformation>> errorObservers) {
		radioPlayerController.addObservers(playerEntityObservers, codecObservers, errorObservers);
	}

	public void updateCoverArt(Optional<String> uri) {
		coverArtController.setImage(uri);
	}

	public void updateLabels(Optional<CodecInformation> codecInfo, Optional<RadioPlayerInformation> radioInfo) {
		labelsController.updateLabels(codecInfo, radioInfo);
	}
	
	public void reportError(ErrorInformation e) {
		radioPlayerController.stop();
		labelsController.reportError(e);
	}
	
	public void setMainApp(App mainApp) {
		this.mainApp = mainApp;
	}
	
}
