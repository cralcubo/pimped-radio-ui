package bo.roman.radio.ui.view.initializers;

import java.io.IOException;
import java.net.MalformedURLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bo.roman.radio.ui.App;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.RadioDisplayerController;
import bo.roman.radio.utilities.LoggerUtils;
import bo.roman.radio.utilities.ResourceFinder;
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
