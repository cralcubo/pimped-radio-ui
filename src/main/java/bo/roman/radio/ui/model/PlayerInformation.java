package bo.roman.radio.ui.model;

import bo.roman.radio.cover.model.Album;
import bo.roman.radio.cover.model.Radio;

public class PlayerInformation {
	private final Album album;
	private final Radio radio;
	
	public PlayerInformation(Album album, Radio radio) {
		this.album = album;
		this.radio = radio;
	}
	
	public Album getAlbum() {
		return album;
	}
	
	public Radio getRadio() {
		return radio;
	}
}
