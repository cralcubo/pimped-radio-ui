package bo.roman.radio.ui.business.displayer;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Optional;

import javax.swing.ImageIcon;

import com.apple.eawt.Application;

import bo.roman.radio.cover.model.Radio;
import bo.roman.radio.ui.Initializable;
import bo.roman.radio.utilities.ResourceFinder;

public class DockInfoManager implements Initializable {
	private static final String DEFAULT_LOGO = "resources/logo/pimped-radio-glossy.jpeg";
	private static final URL DEFAULTLOGO_URL = ResourceFinder.findFileUrl(DEFAULT_LOGO);
	
	private static DockInfoManager instance;
	private final ImageIcon defaultIcon;
	
	public DockInfoManager() {
		defaultIcon = new ImageIcon(DEFAULTLOGO_URL);
	}

	public static DockInfoManager getInstance() {
		if (instance == null) {
			instance = new DockInfoManager();
		}
		return instance;
	}
	
	@Override
	public void initialize() {
		Application.getApplication().setDockIconImage(defaultIcon.getImage());
	}
	
	public void update(Optional<Radio> oRadio) {
		ImageIcon icon = oRadio.flatMap(r -> r.getLogoUri()
								.flatMap(uri -> convertSilently(uri)))
									.map(url -> new ImageIcon(url))
									.orElseGet(() -> defaultIcon);
		
		Application.getApplication().setDockIconImage(icon.getImage());
	}
	
	private static Optional<URL> convertSilently(URI uri) {
		try {
			return Optional.of(uri.toURL());
		} catch (MalformedURLException e) {
			return Optional.empty();
		}
	}

}
