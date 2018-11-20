package bo.roman.radio.ui.business.displayer;

import java.awt.Image;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.apple.eawt.Application;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.ui.model.RadioPlayerEntity;
import bo.roman.radio.utilities.LoggerUtils;
import javafx.application.Platform;

public class DockInfoManager implements Initializable, Updateable {
	private final static Logger logger = LoggerFactory.getLogger(DockInfoManager.class);
	private static final ImageIcon DEFAULT_IMAGE = new ImageIcon(RadioPlayerEntity.DEFAULTLOGO_URL);
	private static DockInfoManager instance;

	public static DockInfoManager getInstance() {
		if (instance == null) {
			instance = new DockInfoManager();
		}
		return instance;
	}

	@Override
	public void initialize() {
		setDockImage(DEFAULT_IMAGE.getImage());
	}

	@Override
	public void update(RadioPlayerEntity e) {
		Image image = Optional.of(e)//
				.map(RadioPlayerEntity::getRadio)//
				.flatMap(Radio::getLogoUri)//
				.flatMap(this::toURL)//
				.map(ImageIcon::new)//
				.map(ImageIcon::getImage)//
				.orElse(DEFAULT_IMAGE.getImage());
		Platform.runLater(() -> setDockImage(image));
	}

	private Optional<URL> toURL(URI uri) {
		try {
			LoggerUtils.logDebug(logger, () -> "Converting: '" + uri + "' to URL");
			return Optional.of(uri.toURL());
		} catch (MalformedURLException e) {
			logger.error("There was an error converting URI to URL.", e);
			return Optional.empty();
		}
	}
	
	private void setDockImage(Image img) {
		Application app = Application.getApplication();
		if (app != null) {
			app.setDockIconImage(img);
		}
	}
}
