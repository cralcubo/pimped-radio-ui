package bo.roman.radio.ui.controller;

import java.nio.file.Paths;
import java.util.Optional;

import bo.roman.radio.ui.controller.util.NodeFader;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class CoverArtController implements Initializable {
	private static final String LOGO_URI = "src/main/resources/logo/pimped-radio-glossy.jpeg";
	private static final int BLUR_RATIO = 10;
	
	private static final double MAXOPACITY_SHADER = 0.7;
	private static final double MINOPACITY_SHADER = 0.0;
	
	private final ImageView coverViewer;
	private final Rectangle coverShader;
	private final Reflection reflection;
	private final String defaultLogoUri;
	
	
	public CoverArtController(ImageView coverViewer, Rectangle coverShader) {
		this.coverViewer = coverViewer;
		this.coverShader = coverShader;
		reflection = new Reflection();
		defaultLogoUri = Paths.get(LOGO_URI).toUri().toString();
	}

	@Override
	public void initialize() {
		coverViewer.setImage(new Image(defaultLogoUri));
	}
	
	public void shadeIt(NodeFader fader) {
		// Blur the Picture that has the Cover Art
		GaussianBlur blur = new GaussianBlur();
		blur.setRadius(BLUR_RATIO);
		blur.setInput(reflection);
		coverViewer.setEffect(blur);
		
		// Apply a layer that will shade the blurred Cover Art
		fader.fadeNode(MINOPACITY_SHADER, MAXOPACITY_SHADER, coverShader);
	}
	
	public void clearIt(NodeFader fader) {
		// Remove Blur Effect
		coverViewer.setEffect(reflection);
		
		// Make shader transparent
		fader.fadeNode(MAXOPACITY_SHADER, MINOPACITY_SHADER, coverShader);
	}

	public void setImage(Optional<String> uri) {
		coverViewer.setImage(new Image(uri.orElse(defaultLogoUri)));
	}

}
