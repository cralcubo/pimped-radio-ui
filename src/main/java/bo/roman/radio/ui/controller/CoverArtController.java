package bo.roman.radio.ui.controller;

import java.nio.file.Paths;

import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class CoverArtController implements Initializable {
	private static final String LOGO_URI = "src/main/resources/logo/pimped-radio-glossy.jpeg";
	private static final int BLUR_RATIO = 10;
	
	private ImageView coverViewer;
	private Rectangle coverShader;
	private final Reflection reflection;
	
	public CoverArtController(ImageView coverViewer, Rectangle coverShader) {
		this.coverViewer = coverViewer;
		this.coverShader = coverShader;
		reflection = new Reflection();
	}

	@Override
	public void initialize() {
		String logoPath = Paths.get(LOGO_URI).toUri().toString();
		coverViewer.setImage(new Image(logoPath));
	}
	
	public void shadeIt(NodeFader fader) {
		// Blur the Picture that has the Cover Art
		GaussianBlur blur = new GaussianBlur();
		blur.setRadius(BLUR_RATIO);
		blur.setInput(reflection);
		coverViewer.setEffect(blur);
		
		// Apply a layer that will shade the blurred Cover Art
		fader.fadeNode(0, 0.5, coverShader);
	}
	
	public void clearIt(NodeFader fader) {
		// Remove Blur Effect
		coverViewer.setEffect(reflection);
		
		// Make shader transparent
		fader.fadeNode(0.5, 0, coverShader);
	}

}
