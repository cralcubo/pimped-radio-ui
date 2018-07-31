package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.cover.model.Codec;
import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.listener.RadioInformationFinder;
import bo.roman.radio.player.listener.ReactiveMediaEventListener;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.MediaPlayerInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.App;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.events.DockInfoEvent;
import bo.roman.radio.ui.business.events.ReportErrorEvent;
import bo.roman.radio.ui.business.events.UpdateCoverEvent;
import bo.roman.radio.ui.business.events.UpdateLabelsEvent;
import bo.roman.radio.ui.business.observers.CodecObeserver;
import bo.roman.radio.ui.business.observers.CoverArtObserver;
import bo.roman.radio.ui.business.observers.DockInfoObserver;
import bo.roman.radio.ui.business.observers.ErrorObserver;
import bo.roman.radio.ui.business.observers.RadioInfoObserver;
import bo.roman.radio.ui.business.observers.RadioStationInfoManagerObserver;
import bo.roman.radio.ui.controller.RadioDisplayerController;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.ResourceFinder;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class RadioPlayerInitializer implements Initializable {
	private final static Logger logger = LoggerFactory.getLogger(RadioPlayerInitializer.class);

	private static final String FXML_PATH = "resources/fxml/RadioPlayerUI.fxml";

	private final Stage primaryStage;
	private final App mainApp;

	private double xOffset = 0;
	private double yOffset = 0;

	private static RadioPlayerInitializer instance;

	private RadioPlayerInitializer(Stage primaryStage, App runningApp) {
		this.primaryStage = primaryStage;
		this.mainApp = runningApp;
	}

	public static RadioPlayerInitializer getInstance(Stage primaryStage, App runningApp) {
		if (instance == null) {
			instance = new RadioPlayerInitializer(primaryStage, runningApp);
		}
		return instance;
	}

	@Override
	public void initialize() {
		LoggerUtils.logDebug(logger, () -> "Initializing the Radio Player View");
		FXMLLoader loader = new FXMLLoader();
		try {
			loader.setLocation(ResourceFinder.findFileUrl(FXML_PATH));
			StackPane radioPlayerUI = loader.load();
			RadioDisplayerController controller = loader.getController();
			controller.setMainApp(mainApp);

			// Clip the root pane
			Rectangle rectangle = controller.getCoverShader();
			clipIt(radioPlayerUI, rectangle);

			// Make root pane draggable
			draggable(radioPlayerUI);

			// Create observers and initialize event handlers
//			initializeHandlers(radioPlayerUI, controller);
			initializeObservers(controller);

			Scene scene = new Scene(radioPlayerUI);
			scene.setFill(Color.TRANSPARENT);
			primaryStage.setScene(scene);

		} catch (MalformedURLException e) {
			throw new RuntimeException("There was an error finding the FXML file.", e);
		} catch (IOException e) {
			throw new RuntimeException("There was an error loading the UI from the Radio Player", e);
		}
	}

	public void show() {
		primaryStage.show();
	}
	
	private void initializeObservers(RadioDisplayerController controller) {
		logger.info("Initializing reactive observers");
		ReactiveMediaEventListener mediaListener = new ReactiveMediaEventListener();
		Observable<MediaPlayerInformation> mpio = Observable.create(mediaListener);
		
		ConnectableObservable<MediaPlayerInformation> connectable = mpio.publish();
		
		RadioInformationFinder rif = new RadioInformationFinder();
		connectable.map(mpi -> rif.find(mpi.getoRadioName(), mpi.getoSong()))
		.subscribe(rpe -> {
			logger.info("Now Reactively Playing:");
			rpe.getRadio().ifPresent(r -> logger.info(r.toString()));
			rpe.getSong().ifPresent(s -> logger.info(s.toString()));
			rpe.getAlbum().ifPresent(a -> logger.info(a.toString()));
		});
		
		connectable.map(MediaPlayerInformation::getoCodec)
				.map(o -> o.orElseGet(() -> new Codec.Builder().channels(8).bitRate(192f).build()))
				.subscribe(c -> logger.info("Reactive Codec: " + c.toString()));
		
		connectable.connect();
		controller.addEventAdapter(mediaListener);
	}

	private void initializeHandlers(StackPane radioPlayerUI, RadioDisplayerController controller) {
		Observer<RadioPlayerEntity> logInfo = (rpe) -> {
			logger.info("Now Playing:");
			rpe.getRadio().ifPresent(r -> logger.info(r.toString()));
			rpe.getSong().ifPresent(s -> logger.info(s.toString()));
			rpe.getAlbum().ifPresent(a -> logger.info(a.toString()));
		};

		// Add Observers
		List<Observer<RadioPlayerEntity>> playerEntityObservers = Arrays.asList(new RadioInfoObserver(radioPlayerUI), 
																			    new CoverArtObserver(radioPlayerUI), 
																			    RadioStationInfoManagerObserver.createRadioInfoObserver(), 
																			    new DockInfoObserver(radioPlayerUI), 
																			    logInfo);
		List<Observer<CodecInformation>> codecObservers = Arrays.asList(new CodecObeserver(radioPlayerUI), 
																		RadioStationInfoManagerObserver.createCodecInfoObserver());
		List<Observer<ErrorInformation>> errorObservers = Arrays.asList(new ErrorObserver(radioPlayerUI));
		controller.addObservers(playerEntityObservers, codecObservers, errorObservers);

		// Add Event Handlers
		radioPlayerUI.addEventHandler(UpdateCoverEvent.UPDATE_IMAGE,
				event -> controller.updateCoverArt(event.getImageUrl()));
		radioPlayerUI.addEventHandler(UpdateLabelsEvent.UPDATE_LABELS,
				event -> controller.updateLabels(event.getCodecInfo(), event.getRadioInfo()));
		radioPlayerUI.addEventHandler(ReportErrorEvent.REPORT_ERROR,
				event -> controller.reportError(event.getErrorInformation()));
		radioPlayerUI.addEventHandler(DockInfoEvent.UPDATE_DOCK, e -> controller.updateDockInfo(e.getRadio()));
	}

	private void draggable(StackPane coverDisplayer) {
		coverDisplayer.setOnMousePressed(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				xOffset = event.getSceneX();
				yOffset = event.getSceneY();
			}
		});
		coverDisplayer.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				primaryStage.setX(event.getScreenX() - xOffset);
				primaryStage.setY(event.getScreenY() - yOffset);
			}
		});

	}

	private void clipIt(StackPane coverDisplayer, Rectangle rectangle) {
		Rectangle clip = new Rectangle(rectangle.getWidth(), rectangle.getHeight());
		clip.setArcWidth(rectangle.getArcWidth());
		clip.setArcHeight(rectangle.getArcHeight());

		coverDisplayer.setClip(clip);
	}
}
