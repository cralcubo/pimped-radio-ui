package bo.roman.radio.ui.model;

import static bo.roman.radio.utilities.ResourceFinder.findFileUri;

import java.net.URI;
import java.util.Optional;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;

public class RadioPlayerEntity {
	private static final String DEFAULT_LOGO = "resources/logo/pimped-radio-glossy.jpeg";
	private static final Optional<URI> DEFAULTLOGO_URI = Optional.of(findFileUri(DEFAULT_LOGO));
	
	private static final Album emptyAlbum = new Album.Builder().build();
	private static final Radio pimpedRadio = new Radio.Builder()//
			.logoUri(DEFAULTLOGO_URI)//
			.name("Pimped Radio")//
			.build();
	
	private static final Radio errorRadio = new Radio.Builder()//
			.logoUri(DEFAULTLOGO_URI)//
			.name("Cannot play radio station")//
			.build();
	

	private Radio radio;
	private Album album;
	private static RadioPlayerEntity instance;
	private static RadioPlayerEntity error;
	private static RadioPlayerEntity empty;

	private RadioPlayerEntity() {
	}

	private RadioPlayerEntity(Radio r, Album a) {
		this.album = a;
		this.radio = r;
	}

	public static RadioPlayerEntity getInstance() {
		if (instance == null) {
			instance = new RadioPlayerEntity();
		}
		return instance;
	}
	
	public static RadioPlayerEntity getErrorInstance() {
		if (error == null) {
			error = new RadioPlayerEntity(errorRadio, emptyAlbum);
		}
		return error;
	}

	public static RadioPlayerEntity getStopedInstance() {
		if (empty == null) {
			empty = new RadioPlayerEntity(pimpedRadio, emptyAlbum);
		}
		return empty;
	}

	public void setRadio(Radio radio) {
		this.radio = radio;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	public Radio getRadio() {
		return radio;
	}

	public Album getAlbum() {
		return album;
	}

	@Override
	public String toString() {
		return "RadioPlayerEntity [radio=" + radio + ", album=" + album + "]";
	}
}
