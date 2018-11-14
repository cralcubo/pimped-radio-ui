package bo.roman.radio.ui.model;

import java.net.URL;

import bo.roman.radio.utilities.ResourceFinder;

public class PlayerImageInformation {
	private static final String DEFAULT_LOGO = "resources/logo/pimped-radio-glossy.jpeg";
	public static final URL DEFAULTLOGO_URL = ResourceFinder.findFileUrl(DEFAULT_LOGO);
	
	private final URL albumUrl;
	private final URL radioUrl;
	
	public PlayerImageInformation(URL albumUrl, URL radioUrl) {
		this.albumUrl = albumUrl;
		this.radioUrl = radioUrl;
	}
	
	public URL getAlbumUrl() {
		return albumUrl;
	}
	
	public URL getRadioUrl() {
		return radioUrl;
	}

	@Override
	public String toString() {
		return "PlayerImageInformation [albumUrl=" + albumUrl + ", radioUrl=" + radioUrl + "]";
	}
}
