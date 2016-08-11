package bo.roman.radio.ui;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.swing.ImageIcon;

import bo.roman.radio.player.listener.Observer;
import bo.roman.radio.player.model.CodecInformation;
import bo.roman.radio.player.model.ErrorInformation;
import bo.roman.radio.player.model.RadioPlayerEntity;
import bo.roman.radio.ui.controller.displayer.RadioDisplayerController;
import bo.roman.radio.ui.controller.events.ReportErrorEvent;
import bo.roman.radio.ui.controller.events.UpdateCoverEvent;
import bo.roman.radio.ui.controller.events.UpdateLabelsEvent;
import bo.roman.radio.ui.controller.observers.CodecObeserver;
import bo.roman.radio.ui.controller.observers.CoverArtObserver;
import bo.roman.radio.ui.controller.observers.ErrorObserver;
import bo.roman.radio.ui.controller.observers.RadioInfoObserver;
import bo.roman.radio.ui.controller.observers.RadioStationInfoManagerObserver;
import bo.roman.radio.ui.controller.tuner.StreamInputController;
import bo.roman.radio.ui.model.AlertMessage;
import bo.roman.radio.ui.model.StationInformation;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class App extends Application {
	private static final String LOGO_URI = "src/main/resources/logo/pimped-radio-glossy.jpeg";

	private Stage primaryStage;
	private StackPane rootLayout;

	private double xOffset = 0;
	private double yOffset = 0;

	public static void main(String[] args) {
		launch(args);
	}

	@SuppressWarnings("restriction")
	@Override
	public void start(Stage primaryStage) throws Exception {
		primaryStage.initStyle(StageStyle.TRANSPARENT);
		primaryStage.setResizable(false);
		primaryStage.setAlwaysOnTop(true);
		com.apple.eawt.Application.getApplication().setDockIconImage(new ImageIcon(LOGO_URI).getImage());
		this.primaryStage = primaryStage;
		initRootLayout();
	}

	private void initRootLayout() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(App.class.getResource("view/RadioDisplayer.fxml"));
		try {
			rootLayout = loader.load();
			RadioDisplayerController controller = loader.getController();
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
			
			// Add Observers
			RadioStationInfoManagerObserver stationInfoObserver = new RadioStationInfoManagerObserver(new StationInformation());
			List<Observer<RadioPlayerEntity>> playerEntityObservers = Arrays.asList(printer, new CoverArtObserver(rootLayout), new RadioInfoObserver(rootLayout), stationInfoObserver.createRadioInfoObserver());
			List<Observer<CodecInformation>> codecObservers = Arrays.asList(new CodecObeserver(rootLayout), stationInfoObserver.createCodecInfoObserver());
			List<Observer<ErrorInformation>> errorObservers = Arrays.asList(new ErrorObserver(rootLayout));
			controller.addObservers(playerEntityObservers, codecObservers, errorObservers);
			
			// Add Event Handlers
			rootLayout.addEventHandler(UpdateCoverEvent.UPDATE_IMAGE, event -> controller.updateCoverArt(event.getImageUrl()));
			rootLayout.addEventHandler(UpdateLabelsEvent.UPDATE_LABELS, event -> controller.updateLabels(event.getCodecInfo(), event.getRadioInfo()));
			rootLayout.addEventHandler(ReportErrorEvent.REPORT_ERROR, event -> controller.reportError(event.getErrorInformation()));

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

	public void triggerAlert(AlertType alertType, AlertMessage alertMessage) {
		Alert alert = new Alert(alertType);
		alert.initOwner(primaryStage);
		alert.setTitle(alertMessage.getTitle());
		alert.setHeaderText(alertMessage.getHeader());
		alert.setContentText(alertMessage.getMessage());
		
		alert.showAndWait();
		
	}
}
