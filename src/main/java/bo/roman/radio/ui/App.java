package bo.roman.radio.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.controller.CoverDisplayerController;
import bo.roman.radio.ui.controller.StreamInputController;
import bo.roman.radio.ui.controller.events.UpdateCoverEvent;
import bo.roman.radio.ui.controller.events.UpdateLabelsEvent;
import bo.roman.radio.ui.controller.observers.CodecObeserver;
import bo.roman.radio.ui.controller.observers.CoverArtObserver;
import bo.roman.radio.ui.controller.observers.RadioInfoObserver;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
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
			controller.setMainApp(this);
			
			// Clip the root pane
			Rectangle rectangle = controller.getCoverShader();
			clipIt(rootLayout, rectangle);

			// Make root pane draggable
			draggable(rootLayout);
			
			Observer<RadioPlayerEntity> printer = (rpe) -> {
				System.out.println("****************************");
				System.out.println("****************************");
				rpe.getRadio().ifPresent(r -> System.out.println(r));
				rpe.getSong().ifPresent(s -> System.out.println(s));
				rpe.getAlbum().ifPresent(a -> System.out.println(a));
				System.out.println("****************************");
				System.out.println("****************************");
			};
			
			// Add Event Handlers
			rootLayout.addEventHandler(UpdateCoverEvent.UPDATE_IMAGE, event -> controller.updateCoverArt(event.getImageUrl()));
			rootLayout.addEventHandler(UpdateLabelsEvent.UPDATE_LABELS, event -> controller.updateLabels(event.getCodecInfo(), event.getRadioInfo()));
			
			// Add Observers
			List<Observer<RadioPlayerEntity>> playerEntityObservers = Arrays.asList(printer, new CoverArtObserver(rootLayout), new RadioInfoObserver(rootLayout));
			List<Observer<CodecInformation>> codecObservers = Arrays.asList(new CodecObeserver(rootLayout));
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
	
	public void showInputStreamDialog() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("view/StreamInput.fxml"));
		try {
			HBox dialogBox = loader.load();
			
			Stage stage = new Stage();
			stage.setTitle("Set Radio Stream");
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
    		Scene scene = new Scene(dialogBox);
    		stage.setScene(scene);
    		
    		StreamInputController controller = loader.getController();
    		controller.setDialogStage(stage);
    		
    		stage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
