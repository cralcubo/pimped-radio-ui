package bo.roman.radio.ui.controller;

import java.util.Arrays;
import java.util.List;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

public class CoverDisplayerController {
	
	private final NodeFader fader = new NodeFader(200);
	private CoverArtController coverArtController;
	private RadioPlayerController radioPlayerController;

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
	private void initialize() {
		coverArtController = new CoverArtController(coverViewer, coverShader);
		List<Initializable> controllers = Arrays.asList(coverArtController);
		controllers.forEach(Initializable::initialize);
	}
	
	@FXML
	private void onMouseEntered() {
		coverArtController.shadeIt(fader);
		// Make Player Controls and Song Info appear 
		fader.fadeNode(0.0, 1.0, controlsPane);
	}
	
	@FXML
	private void onMouseExited() {
		coverArtController.clearIt(fader);
		// Make Player Controls and Song Info disappear
		fader.fadeNode(1.0, 0.0, controlsPane);
	}
	
	public Rectangle getCoverShader() {
		return coverShader;
	}
	
	public void addObservers(List<Observer<RadioPlayerEntity>> playerEntityObservers, List<Observer<CodecInformation>> codecObservers) {
		radioPlayerController.addObservers(playerEntityObservers, codecObservers);
	}

	public void updateCoverArt(String uri) {
		coverArtController.setImage(uri);
	}

	public void updateLabels(CodecInformation codecInfo, RadioPlayerEntity radioInfo) {
		
	}
	
}
