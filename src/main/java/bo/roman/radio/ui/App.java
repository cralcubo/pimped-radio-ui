package bo.roman.radio.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.controller.CoverDisplayerController;
import bo.roman.radio.ui.controller.observers.AlbumObserver;
import bo.roman.radio.ui.controller.observers.CodecObeserver;
import bo.roman.radio.ui.controller.observers.CoverArtObserver;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {

	private Stage primaryStage;
	private StackPane rootLayout;

	private double xOffset = 0;
	private double yOffset = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);
		this.primaryStage = primaryStage;
		initRootLayout();
	}

	private void initRootLayout() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("view/CoverDisplayer.fxml"));
		try {
			rootLayout = loader.load();
			CoverDisplayerController controller = loader.getController();
			
			// Clip the root pane
			Rectangle rectangle = controller.getCoverShader();
			clipIt(rootLayout, rectangle);

			// Make root pane draggable
			draggable(rootLayout);

			// Add Observers
			List<Observer<RadioPlayerEntity>> playerEntityObservers = Arrays.asList(new CoverArtObserver(rootLayout), new AlbumObserver());
			List<Observer<CodecInformation>> codecObservers = Arrays.asList(new CodecObeserver());
			
			controller.addObservers(playerEntityObservers, codecObservers);

			Scene scene = new Scene(rootLayout);
			scene.setFill(Color.TRANSPARENT);
			primaryStage.setScene(scene);

			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
