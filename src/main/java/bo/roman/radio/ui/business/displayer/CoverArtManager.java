package bo.roman.radio.ui.business.displayer;

import static bo.roman.radio.ui.model.PlayerImageInformation.DEFAULTLOGO_URL;

import java.net.URI;
import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.CoverArt;
import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.controller.util.NodeFader;
import bo.roman.radio.ui.model.RadioPlayerEntity;
import javafx.application.Platform;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.Reflection;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

public class CoverArtManager implements Initializable, Updateable {
	private static final int BLUR_RATIO = 30;
	private static final Image LOGO_IMAGE = new Image(DEFAULTLOGO_URL.toString());

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
		if (instance == null) {
			if (coverViewer == null || coverShader == null) {
				throw new IllegalStateException(
						"A new instance of CoverArtManager was tried to be created, but no ImageView and Rectangle provided.");
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

		if (fader != null) {
			// Apply a layer that will shade the blurred Cover Art
			fader.fadeNode(MINOPACITY_SHADER, MAXOPACITY_SHADER, coverShader);
		}
	}

	public void clearIt(NodeFader fader) {
		// Remove Blur Effect
		coverViewer.setEffect(reflection);

		if (fader != null) {
			// Make shader transparent
			fader.fadeNode(MAXOPACITY_SHADER, MINOPACITY_SHADER, coverShader);
		}
	}

	public void shadeUnblurIt(NodeFader fader) {
		// Remove Blur Effect
		coverViewer.setEffect(reflection);

		if (fader != null) {
			// Apply a layer that will shade the blurred Cover Art
			fader.fadeNode(MINOPACITY_SHADER, MAXOPACITY_SHADER, coverShader);
		}
	}
	
	public void update(RadioPlayerEntity e) {
		Optional<RadioPlayerEntity> oe = Optional.of(e); 
		Image image = oe
				.map(RadioPlayerEntity::getAlbum)//
				.flatMap(Album::getCoverArt)//
				.flatMap(CoverArt::getLargeUri)//
				.map(URI::toString)//
				.map(Image::new)//
				.orElseGet(() -> oe.map(RadioPlayerEntity::getRadio)
						.flatMap(Radio::getLogoUri)
						.map(URI::toString)
						.map(Image::new)
						.orElse(LOGO_IMAGE));
		
		Platform.runLater(() -> coverViewer.setImage(image));
	}
}
