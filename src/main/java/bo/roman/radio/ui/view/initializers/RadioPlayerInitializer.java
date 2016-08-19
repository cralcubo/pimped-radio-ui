package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.App;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.business.events.ReportErrorEvent;
import bo.roman.radio.ui.business.events.UpdateCoverEvent;
import bo.roman.radio.ui.business.events.UpdateLabelsEvent;
import bo.roman.radio.ui.business.observers.CodecObeserver;
import bo.roman.radio.ui.business.observers.CoverArtObserver;
import bo.roman.radio.ui.business.observers.ErrorObserver;
import bo.roman.radio.ui.business.observers.RadioInfoObserver;
import bo.roman.radio.ui.business.observers.RadioStationInfoManagerObserver;
import bo.roman.radio.ui.controller.RadioDisplayerController;
import bo.roman.radio.utilities.LoggerUtils;
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

	private static final String FXML_PATH = "src/main/resources/fxml/RadioPlayerUI.fxml";

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
			loader.setLocation(Paths.get(FXML_PATH).toUri().toURL());
			StackPane radioPlayerUI = loader.load();
			RadioDisplayerController controller = loader.getController();
			controller.setMainApp(mainApp);

			// Clip the root pane
			Rectangle rectangle = controller.getCoverShader();
			clipIt(radioPlayerUI, rectangle);

			// Make root pane draggable
			draggable(radioPlayerUI);

			// Create observers and initialize event handlers
			initializeHandlers(radioPlayerUI, controller);

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

	private void initializeHandlers(StackPane radioPlayerUI, RadioDisplayerController controller) {
		Observer<RadioPlayerEntity> printer = (rpe) -> {
			System.out.println("****************************");
			System.out.println("****************************");
			rpe.getRadio().ifPresent(r -> System.out.println(r));
			rpe.getSong().ifPresent(s -> System.out.println(s));
			rpe.getAlbum().ifPresent(a -> System.out.println(a));
			System.out.println("****************************");
			System.out.println("****************************");
		};

		// Add Observers
		List<Observer<RadioPlayerEntity>> playerEntityObservers = Arrays.asList(printer, new CoverArtObserver(radioPlayerUI), new RadioInfoObserver(radioPlayerUI), RadioStationInfoManagerObserver.createRadioInfoObserver());
		List<Observer<CodecInformation>> codecObservers = Arrays.asList(new CodecObeserver(radioPlayerUI), RadioStationInfoManagerObserver.createCodecInfoObserver());
		List<Observer<ErrorInformation>> errorObservers = Arrays.asList(new ErrorObserver(radioPlayerUI));
		controller.addObservers(playerEntityObservers, codecObservers, errorObservers);

		// Add Event Handlers
		radioPlayerUI.addEventHandler(UpdateCoverEvent.UPDATE_IMAGE,
				event -> controller.updateCoverArt(event.getImageUrl()));
		radioPlayerUI.addEventHandler(UpdateLabelsEvent.UPDATE_LABELS,
				event -> controller.updateLabels(event.getCodecInfo(), event.getRadioInfo()));
		radioPlayerUI.addEventHandler(ReportErrorEvent.REPORT_ERROR,
				event -> controller.reportError(event.getErrorInformation()));
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
