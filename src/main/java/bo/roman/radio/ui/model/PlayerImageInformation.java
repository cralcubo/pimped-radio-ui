package bo.roman.radio.ui.model;

import java.net.URL;

import bo.roman.radio.utilities.ResourceFinder;

public class PlayerImageInformation {
	private static final String DEFAULT_LOGO = "resources/logo/pimped-radio-glossy.jpeg";
	private static final URL DEFAULTLOGO_URL = ResourceFinder.findFileUrl(DEFAULT_LOGO);
	public static final PlayerImageInformation DEFAULT = new PlayerImageInformation(DEFAULTLOGO_URL);
	
	private final URL imageUrl;
	
	public PlayerImageInformation(URL imageUrl) {
		this.imageUrl = imageUrl;
	}
	
	public URL getImageUrl() {
		return imageUrl;
	}

}
