package bo.roman.radio.ui.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.App;
import bo.roman.radio.ui.business.RadioStreamManager;
import bo.roman.radio.ui.controller.util.NodeFader;
import bo.roman.radio.ui.model.RadioInformation;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class CoverDisplayerController {
	
	private CoverArtController coverArtController;
	private RadioPlayerController radioPlayerController;
	private LabelsController labelsController;
	
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
		radioPlayerController = new RadioPlayerController(play, volume);
		
		List<Initializable> controllers = Arrays.asList(coverArtController, labelsController, radioPlayerController);
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
		String station = RadioStreamManager.getLastStream();
		radioPlayerController.playAction(station);
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
	
	public Rectangle getCoverShader() {
		return coverShader;
	}
	
	public void addObservers(List<Observer<RadioPlayerEntity>> playerEntityObservers, List<Observer<CodecInformation>> codecObservers, List<Observer<ErrorInformation>> errorObservers) {
		radioPlayerController.addObservers(playerEntityObservers, codecObservers, errorObservers);
	}

	public void updateCoverArt(Optional<String> uri) {
		coverArtController.setImage(uri);
	}

	public void updateLabels(Optional<CodecInformation> codecInfo, Optional<RadioInformation> radioInfo) {
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