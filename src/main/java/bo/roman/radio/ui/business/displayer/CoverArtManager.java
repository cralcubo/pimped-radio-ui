package bo.roman.radio.ui.business.displayer;

import java.util.Optional;

import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.util.NodeFader;
import bo.roman.radio.utilities.ResourceFinder;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class CoverArtManager implements Initializable {
	private static final int BLUR_RATIO = 30;
	private static final String LOGO_URI = "resources/logo/pimped-radio-glossy.jpeg";
	private static final Image LOGO_IMAGE = new Image(ResourceFinder.findFileUri(LOGO_URI).toASCIIString());
	
	private static final double MAXOPACITY_SHADER = 0.4;
	private static final double MINOPACITY_SHADER = 0.0;
	
	private final ImageView coverViewer;
	private final Rectangle coverShader;
	private final Reflection reflection;
	
	private static CoverArtManager instance;
	
	private CoverArtManager(ImageView coverViewer, Rectangle coverShader) {
		this.coverViewer = coverViewer;
		this.coverShader = coverShader;
		reflection = new Reflection();
	}
	
	public static CoverArtManager getInstance() {
		return getInstance(null, null);
	}
	public static CoverArtManager getInstance(ImageView coverViewer, Rectangle coverShader) {
		if(instance == null) {
			if(coverViewer == null || coverShader == null) {
				throw new IllegalStateException("A new instance of CoverArtManager was tried to be created, but no ImageView and Rectangle provided.");
			}
			instance = new CoverArtManager(coverViewer, coverShader);
		}
		
		return instance;
	}

	@Override
	public void initialize() {
		coverViewer.setEffect(reflection);
		coverViewer.setImage(LOGO_IMAGE);
	}
	
	public void shadeIt(NodeFader fader) {
		// Blur the Picture that has the Cover Art
		GaussianBlur blur = new GaussianBlur();
		blur.setRadius(BLUR_RATIO);
		blur.setInput(reflection);
		coverViewer.setEffect(blur);
		
		if(fader != null) {
			// Apply a layer that will shade the blurred Cover Art
			fader.fadeNode(MINOPACITY_SHADER, MAXOPACITY_SHADER, coverShader);
		}
	}
	
	public void clearIt(NodeFader fader) {
		// Remove Blur Effect
		coverViewer.setEffect(reflection);
		
		if(fader != null) {
			// Make shader transparent
			fader.fadeNode(MAXOPACITY_SHADER, MINOPACITY_SHADER, coverShader);
		}
	}
	
	public void shadeUnblurIt(NodeFader fader) {
		// Remove Blur Effect
		coverViewer.setEffect(reflection);
		
		if(fader != null) {
			// Apply a layer that will shade the blurred Cover Art
			fader.fadeNode(MINOPACITY_SHADER, MAXOPACITY_SHADER, coverShader);
		}
	}

	public void setImage(Optional<String> uri) {
		Optional<Image> oImage = uri.map(s -> new Image(s));
		coverViewer.setImage(oImage.orElseGet(() -> LOGO_IMAGE));
	}

}
